package com.kappa.app.main

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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.kappa.app.R
import kotlinx.coroutines.launch

class StarPathFragment : Fragment() {

    private val myMenuViewModel: MyMenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_star_path, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusText = view.findViewById<TextView>(R.id.text_star_path_status)
        val messageText = view.findViewById<TextView>(R.id.text_star_path_message)
        val diamondsInput = view.findViewById<TextInputEditText>(R.id.input_star_path_diamonds)
        val requestButton = view.findViewById<MaterialButton>(R.id.button_star_path_request)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_star_path_rewards)
        val rowsAdapter = SimpleRowAdapter()
        recycler.adapter = rowsAdapter

        requestButton.setOnClickListener {
            val diamonds = diamondsInput.text?.toString()?.trim()?.toLongOrNull()
            if (diamonds == null || diamonds <= 0) {
                messageText.text = "Enter valid diamonds"
                messageText.visibility = View.VISIBLE
                return@setOnClickListener
            }
            myMenuViewModel.requestReward(diamonds)
            diamondsInput.text = null
        }

        myMenuViewModel.loadStarPath()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myMenuViewModel.viewState.collect { state ->
                    val target = 600000L
                    val progress = state.diamondBalance % target
                    val pending = state.rewards.count { it.status.equals("PENDING", ignoreCase = true) }
                    val approved = state.rewards.count { it.status.equals("APPROVED", ignoreCase = true) }
                    statusText.text =
                        "Diamonds: ${state.diamondBalance}\nProgress: $progress/$target\nPending: $pending • Approved: $approved"

                    rowsAdapter.submitRows(
                        state.rewards.map { reward ->
                            reward.status to "${reward.diamonds} diamonds • ${reward.createdAt}"
                        }
                    )

                    when {
                        state.error != null -> {
                            messageText.text = state.error
                            messageText.visibility = View.VISIBLE
                        }
                        state.message != null -> {
                            messageText.text = state.message
                            messageText.visibility = View.VISIBLE
                        }
                        state.rewards.isEmpty() -> {
                            messageText.text = "No reward requests yet"
                            messageText.visibility = View.VISIBLE
                        }
                        else -> {
                            messageText.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}
