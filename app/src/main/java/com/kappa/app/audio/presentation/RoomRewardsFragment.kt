package com.kappa.app.audio.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.kappa.app.R
import com.kappa.app.main.MyMenuViewModel
import com.kappa.app.main.SimpleRowAdapter
import kotlinx.coroutines.launch

class RoomRewardsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room_rewards, container, false)
    }

    private val myMenuViewModel: MyMenuViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val emptyText = view.findViewById<TextView>(R.id.text_rewards_empty)
        val refreshButton = view.findViewById<MaterialButton>(R.id.button_rewards_refresh)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_rewards_announcements)
        val rowsAdapter = SimpleRowAdapter()
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = rowsAdapter

        refreshButton.setOnClickListener {
            myMenuViewModel.loadRoomRewards()
        }
        myMenuViewModel.loadRoomRewards()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myMenuViewModel.viewState.collect { state ->
                    rowsAdapter.submitRows(
                        state.announcements.map { announcement ->
                            announcement.title to announcement.message
                        }
                    )
                    when {
                        state.error != null -> {
                            emptyText.text = state.error
                            emptyText.visibility = View.VISIBLE
                        }
                        state.announcements.isEmpty() -> {
                            emptyText.text = "No announcements yet."
                            emptyText.visibility = View.VISIBLE
                        }
                        else -> {
                            emptyText.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}
