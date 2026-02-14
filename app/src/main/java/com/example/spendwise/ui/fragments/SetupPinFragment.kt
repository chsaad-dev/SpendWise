package com.example.spendwise.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.spendwise.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SetupPinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_pin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNewPin = view.findViewById<TextInputEditText>(R.id.et_new_pin)
        val etConfirm = view.findViewById<TextInputEditText>(R.id.et_confirm_pin)
        val btnStart = view.findViewById<MaterialButton>(R.id.btn_get_started)

        btnStart.setOnClickListener {
            val pin = etNewPin.text.toString()
            val confirm = etConfirm.text.toString()

            when {
                pin.length < 4 -> {
                    Toast.makeText(context, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show()
                }
                pin != confirm -> {
                    Toast.makeText(context, "PINs don't match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putString("user_pin", pin)
                        .putBoolean("pin_setup_done", true)
                        .apply()

                    Toast.makeText(context, "PIN created! Welcome to SpendWise", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_setup_to_home)
                }
            }
        }
    }
}
