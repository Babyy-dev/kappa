package com.kappa.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.button.MaterialButton
import com.kappa.app.R
import com.kappa.app.auth.presentation.AuthViewModel
import com.kappa.app.core.config.AppConfig

class SettingsFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val serverText = view.findViewById<TextView>(R.id.text_settings_server)
        val logoutButton = view.findViewById<MaterialButton>(R.id.button_settings_logout)
        serverText.text = "API: ${AppConfig.baseUrl}"

        logoutButton.setOnClickListener {
            authViewModel.logout()
            findNavController().navigate(
                R.id.navigation_login,
                null,
                navOptions {
                    popUpTo(R.id.navigation_inbox) { inclusive = true }
                }
            )
        }
    }
}
