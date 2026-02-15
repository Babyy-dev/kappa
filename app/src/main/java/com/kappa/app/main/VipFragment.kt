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
import com.kappa.app.R
import kotlinx.coroutines.launch

class VipFragment : Fragment() {

    private val myMenuViewModel: MyMenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusText = view.findViewById<TextView>(R.id.text_vip_status)
        myMenuViewModel.loadVip()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myMenuViewModel.viewState.collect { state ->
                    if (state.error != null) {
                        statusText.text = state.error
                    } else {
                        val level = resolveVipLevel(state.diamondBalance)
                        statusText.text = "VIP Level: $level\nDiamonds: ${state.diamondBalance}\nCoins: ${state.coinBalance}"
                    }
                }
            }
        }
    }

    private fun resolveVipLevel(diamonds: Long): Int {
        val thresholds = listOf(0L, 1000L, 5000L, 20000L, 50000L, 100000L, 250000L, 500000L, 1000000L)
        return thresholds.indexOfLast { diamonds >= it } + 1
    }
}
