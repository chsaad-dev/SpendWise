package com.example.spendwise.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.spendwise.R
import com.example.spendwise.data.SavingsGoal
import com.example.spendwise.utils.CurrencyHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class AddSavingsBottomSheet : BottomSheetDialogFragment() {

    private var goal: SavingsGoal? = null
    private var onAddSavingsCallback: ((amount: Double) -> Unit)? = null

    override fun getTheme(): Int = R.style.Theme_SpendWise_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_add_savings, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { sheet ->
            sheet.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_bottom_sheet)
            BottomSheetBehavior.from(sheet).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val g = goal ?: return

        val tvSubtitle = view.findViewById<TextView>(R.id.tv_add_savings_subtitle)
        val tilAmount = view.findViewById<TextInputLayout>(R.id.til_savings_amount)
        val etAmount = view.findViewById<TextInputEditText>(R.id.et_savings_amount)
        val chip500 = view.findViewById<Chip>(R.id.chip_savings_500)
        val chip1000 = view.findViewById<Chip>(R.id.chip_savings_1000)
        val chip5000 = view.findViewById<Chip>(R.id.chip_savings_5000)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btn_confirm_add_savings)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btn_cancel_add_savings)

        val fmt = CurrencyHelper.getCurrencyFormat(requireContext())
        val symbol = fmt.currency?.getSymbol(Locale.getDefault()) ?: fmt.currency?.symbol ?: ""
        val prefix = if (symbol.isNotBlank()) "$symbol " else "Rs "
        tilAmount.prefixText = prefix

        val remaining = (g.targetAmount - g.savedAmount).coerceAtLeast(0.0)
        val fmtCompact = CurrencyHelper.getCurrencyFormat(requireContext()).apply {
            if (remaining % 1.0 == 0.0) maximumFractionDigits = 0
        }
        val remainingFormatted = fmtCompact.format(remaining)
        tvSubtitle.text = "Contributing toward \"${g.name}\" ($remainingFormatted remaining)"

        val sym = prefix.trim()
        chip500.text = "+ $sym 500"
        chip1000.text = "+ $sym 1,000"
        chip5000.text = "+ $sym 5,000"

        fun addAmount(value: Double) {
            val current = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            val newTotal = current + value
            val text = if (newTotal % 1.0 == 0.0) newTotal.toLong().toString() else newTotal.toString()
            etAmount.setText(text)
            etAmount.setSelection(text.length)
        }

        chip500.setOnClickListener { addAmount(500.0) }
        chip1000.setOnClickListener { addAmount(1000.0) }
        chip5000.setOnClickListener { addAmount(5000.0) }

        btnAdd.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(context, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onAddSavingsCallback?.invoke(amount)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "AddSavingsBottomSheet"

        fun newInstance(
            goal: SavingsGoal,
            onAddSavings: (amount: Double) -> Unit
        ): AddSavingsBottomSheet {
            return AddSavingsBottomSheet().apply {
                this.goal = goal
                this.onAddSavingsCallback = onAddSavings
            }
        }
    }
}
