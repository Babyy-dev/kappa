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
import com.kappa.app.R
import kotlinx.coroutines.launch

class BackpackFragment : Fragment() {

    private val myMenuViewModel: MyMenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_backpack, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusText = view.findViewById<TextView>(R.id.text_backpack_status)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_backpack_transactions)
        val rowsAdapter = SimpleRowAdapter()
        recycler.adapter = rowsAdapter

        myMenuViewModel.loadBackpack()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myMenuViewModel.viewState.collect { state ->
                    val rows = state.transactions.map { tx ->
                        tx.type to "${tx.amount} • ${tx.createdAt}"
                    }
                    rowsAdapter.submitRows(rows)
                    when {
                        state.error != null -> {
                            statusText.text = state.error
                            statusText.visibility = View.VISIBLE
                        }
                        rows.isEmpty() -> {
                            statusText.text = "No transactions yet"
                            statusText.visibility = View.VISIBLE
                        }
                        else -> {
                            statusText.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}
