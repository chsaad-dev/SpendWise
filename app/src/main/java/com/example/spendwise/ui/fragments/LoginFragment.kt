package com.example.spendwise.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.spendwise.R
import com.example.spendwise.data.AppDatabase
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etPin = view.findViewById<TextInputEditText>(R.id.et_pin)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val btnForgot = view.findViewById<Button>(R.id.btn_forgot_pin)

        // Try biometric authentication first
        tryBiometricAuth()

        btnLogin.setOnClickListener {
            val pin = etPin.text.toString()
            val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
            val savedPin = prefs.getString("user_pin", "1234") ?: "1234"

            if (pin == savedPin) {
                findNavController().navigate(R.id.action_login_to_home)
            } else {
                Toast.makeText(requireContext(), "Invalid PIN", Toast.LENGTH_SHORT).show()
            }
        }

        btnForgot.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Forgot PIN?")
                .setMessage("This will erase all your expenses and reset your PIN to 1234.\n\nThis action cannot be undone.")
                .setPositiveButton("Reset Everything") { _, _ ->
                    val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putString("user_pin", "1234")
                        .putBoolean("pin_setup_done", true)
                        .apply()

                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.getDatabase(requireContext(), CoroutineScope(Dispatchers.IO))
                            .expenseDao().deleteAllExpenses()
                    }

                    Toast.makeText(context, "PIN reset to 1234. All data cleared.", Toast.LENGTH_LONG).show()
                    etPin.text?.clear()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun tryBiometricAuth() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt()
            }
            else -> {
                // Biometrics not available, fall back to PIN
            }
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                findNavController().navigate(R.id.action_login_to_home)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // User cancelled or error — fall back to PIN silently
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }

        val biometricPrompt = BiometricPrompt(this, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("SpendWise")
            .setSubtitle("Use fingerprint or face to unlock")
            .setNegativeButtonText("Use PIN instead")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
