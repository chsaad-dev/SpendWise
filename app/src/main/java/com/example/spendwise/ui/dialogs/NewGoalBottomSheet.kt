package com.example.spendwise.ui.dialogs

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.spendwise.R
import com.example.spendwise.utils.CurrencyHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewGoalBottomSheet : BottomSheetDialogFragment() {

    private var onCreateGoalCallback: ((name: String, target: Double, deadline: Long) -> Unit)? = null

    override fun getTheme(): Int = R.style.Theme_SpendWise_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_new_goal, container, false)
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

        val etName = view.findViewById<TextInputEditText>(R.id.et_goal_name)
        val tilTarget = view.findViewById<TextInputLayout>(R.id.til_goal_target)
        val etTarget = view.findViewById<TextInputEditText>(R.id.et_goal_target)
        val etDeadline = view.findViewById<TextInputEditText>(R.id.et_goal_deadline)
        val btnCreate = view.findViewById<MaterialButton>(R.id.btn_create_goal)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btn_cancel_goal)

        val fmt = CurrencyHelper.getCurrencyFormat(requireContext())
        val symbol = fmt.currency?.getSymbol(Locale.getDefault()) ?: fmt.currency?.symbol ?: ""
        val prefix = if (symbol.isNotBlank()) "$symbol " else "Rs "
        tilTarget.prefixText = prefix

        val calendar = Calendar.getInstance().apply {
            add(Calendar.MONTH, 3)
        }
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        etDeadline.setText(dateFormat.format(calendar.time))

        etDeadline.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    etDeadline.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnCreate.setOnClickListener {
            val name = etName.text.toString().trim()
            val target = etTarget.text.toString().toDoubleOrNull()

            if (name.isBlank() || target == null || target <= 0) {
                Toast.makeText(context, "Please enter a valid name and target", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onCreateGoalCallback?.invoke(name, target, calendar.timeInMillis)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "NewGoalBottomSheet"

        fun newInstance(
            onCreateGoal: (name: String, target: Double, deadline: Long) -> Unit
        ): NewGoalBottomSheet {
            return NewGoalBottomSheet().apply {
                this.onCreateGoalCallback = onCreateGoal
            }
        }
    }
}
