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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.kappa.app.R
import kotlinx.coroutines.launch

class RechargeFragment : Fragment() {

    private val myMenuViewModel: MyMenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recharge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val balanceText = view.findViewById<TextView>(R.id.text_recharge_balance)
        val messageText = view.findViewById<TextView>(R.id.text_recharge_message)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_recharge_packages)
        val rowsAdapter = SimpleRowAdapter()
        recycler.adapter = rowsAdapter

        val button = view.findViewById<MaterialButton>(R.id.button_recharge_submit)
        button.setOnClickListener {
            findNavController().navigate(R.id.navigation_wallet)
        }
        myMenuViewModel.loadRecharge()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myMenuViewModel.viewState.collect { state ->
                    balanceText.text = "Balance: ${state.coinBalance} coins"
                    rowsAdapter.submitRows(
                        state.packages.map { pkg ->
                            pkg.name to "${pkg.coinAmount} coins • $${pkg.priceUsd}"
                        }
                    )
                    if (state.error != null) {
                        messageText.text = state.error
                        messageText.visibility = View.VISIBLE
                    } else if (state.packages.isEmpty()) {
                        messageText.text = "No packages available"
                        messageText.visibility = View.VISIBLE
                    } else {
                        messageText.visibility = View.GONE
                    }
                }
            }
        }
    }
}
