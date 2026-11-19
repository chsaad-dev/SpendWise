package com.example.spendwise.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.spendwise.R
import com.example.spendwise.utils.CurrencyHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.Locale

class MonthlyBudgetBottomSheet : BottomSheetDialogFragment() {

    private var onSaveListener: ((Float) -> Unit)? = null
    private var onRemoveListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_monthly_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        val currentBudget = prefs.getFloat("monthly_budget", 0f)

        val tilBudget = view.findViewById<TextInputLayout>(R.id.til_monthly_budget)
        val etBudget = view.findViewById<TextInputEditText>(R.id.et_monthly_budget)
        val chip1 = view.findViewById<MaterialButton>(R.id.chip_preset_1)
        val chip2 = view.findViewById<MaterialButton>(R.id.chip_preset_2)
        val chip3 = view.findViewById<MaterialButton>(R.id.chip_preset_3)
        val btnSave = view.findViewById<MaterialButton>(R.id.btn_save_budget)
        val btnRemove = view.findViewById<MaterialButton>(R.id.btn_remove_budget)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btn_cancel_budget)

        val symbol = CurrencyHelper.getCurrencyFormat(requireContext()).currency?.symbol?.trim() ?: "Rs"
        tilBudget.prefixText = "$symbol "

        val numFmt = NumberFormat.getIntegerInstance(Locale.getDefault())
        chip1.text = "$symbol ${numFmt.format(10000)}"
        chip2.text = "$symbol ${numFmt.format(25000)}"
        chip3.text = "$symbol ${numFmt.format(50000)}"

        if (currentBudget > 0f) {
            val displayValue = if (currentBudget % 1.0f == 0f) {
                currentBudget.toInt().toString()
            } else {
                currentBudget.toString()
            }
            etBudget.setText(displayValue)
            etBudget.setSelection(displayValue.length)
            btnRemove.visibility = View.VISIBLE
        } else {
            btnRemove.visibility = View.GONE
        }

        chip1.setOnClickListener {
            etBudget.setText("10000")
            etBudget.setSelection(etBudget.text?.length ?: 0)
        }
        chip2.setOnClickListener {
            etBudget.setText("25000")
            etBudget.setSelection(etBudget.text?.length ?: 0)
        }
        chip3.setOnClickListener {
            etBudget.setText("50000")
            etBudget.setSelection(etBudget.text?.length ?: 0)
        }

        btnSave.setOnClickListener {
            val text = etBudget.text?.toString()?.trim() ?: ""
            val value = text.toFloatOrNull()
            if (value == null || value <= 0f) {
                tilBudget.error = "Enter a valid budget limit"
                return@setOnClickListener
            }
            tilBudget.error = null
            onSaveListener?.invoke(value)
            dismiss()
        }

        btnRemove.setOnClickListener {
            onRemoveListener?.invoke()
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "MonthlyBudgetBottomSheet"

        fun newInstance(
            onSave: (Float) -> Unit,
            onRemove: () -> Unit
        ): MonthlyBudgetBottomSheet {
            return MonthlyBudgetBottomSheet().apply {
                this.onSaveListener = onSave
                this.onRemoveListener = onRemove
            }
        }
    }
}
