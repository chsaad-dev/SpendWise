package com.example.spendwise.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.spendwise.R
import com.example.spendwise.data.Expense
import com.example.spendwise.data.RecurringExpense
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddExpenseFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val calendar = Calendar.getInstance()
    private var editExpenseId: Int = -1
    private var isEditMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val etAmount = view.findViewById<TextInputEditText>(R.id.et_amount)
        val actvCategory = view.findViewById<AutoCompleteTextView>(R.id.actv_category)
        val etDate = view.findViewById<TextInputEditText>(R.id.et_date)
        val etNote = view.findViewById<TextInputEditText>(R.id.et_note)
        val btnSave = view.findViewById<MaterialButton>(R.id.btn_save)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btn_delete)
        val switchRecurring = view.findViewById<MaterialSwitch>(R.id.switch_recurring)
        val tilRecurrence = view.findViewById<TextInputLayout>(R.id.til_recurrence)
        val actvRecurrence = view.findViewById<AutoCompleteTextView>(R.id.actv_recurrence)
        val layoutRecurring = view.findViewById<LinearLayout>(R.id.layout_recurring)

        // Recurrence type dropdown
        val recurrenceOptions = listOf("Daily", "Weekly", "Monthly")
        actvRecurrence.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, recurrenceOptions)
        )
        actvRecurrence.setText("Monthly", false)

        switchRecurring.setOnCheckedChangeListener { _, isChecked ->
            tilRecurrence.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        editExpenseId = arguments?.getInt("expenseId", -1) ?: -1
        isEditMode = editExpenseId != -1

        if (isEditMode) {
            tvTitle.text = "Edit Expense"
            btnSave.text = "Update Expense"
            btnDelete.visibility = View.VISIBLE
            layoutRecurring.visibility = View.GONE

            viewModel.getExpenseById(editExpenseId) { expense ->
                expense?.let {
                    etAmount.setText(String.format("%.2f", it.amount))
                    actvCategory.setText(it.category, false)
                    etNote.setText(it.note)
                    calendar.timeInMillis = it.date
                    updateDateLabel(etDate)
                }
            }
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            updateDateLabel(etDate)
        }

        etDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        updateDateLabel(etDate)

        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            val categoryAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories.map { it.name }
            )
            actvCategory.setAdapter(categoryAdapter)
        }

        btnSave.setOnClickListener {
            val amountStr = etAmount.text.toString()
            val category = actvCategory.text.toString()
            val note = etNote.text.toString()

            if (amountStr.isBlank() || category.isBlank()) {
                Toast.makeText(context, "Please enter amount and category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

            if (isEditMode) {
                val updated = Expense(
                    id = editExpenseId,
                    amount = amount,
                    category = category,
                    date = calendar.timeInMillis,
                    note = note
                )
                viewModel.updateExpense(updated)
                Toast.makeText(context, "Expense updated", Toast.LENGTH_SHORT).show()
            } else {
                val expense = Expense(
                    amount = amount,
                    category = category,
                    date = calendar.timeInMillis,
                    note = note
                )
                viewModel.insertExpense(expense)

                if (switchRecurring.isChecked) {
                    val recurrenceType = when (actvRecurrence.text.toString()) {
                        "Daily" -> "DAILY"
                        "Weekly" -> "WEEKLY"
                        else -> "MONTHLY"
                    }
                    viewModel.insertRecurring(
                        RecurringExpense(
                            amount = amount,
                            category = category,
                            note = note,
                            recurrenceType = recurrenceType,
                            lastProcessedDate = System.currentTimeMillis()
                        )
                    )
                    Toast.makeText(context, "Recurring expense saved ✓", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Expense saved", Toast.LENGTH_SHORT).show()
                }
            }
            findNavController().popBackStack()
        }

        btnDelete.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.getExpenseById(editExpenseId) { expense ->
                        expense?.let { viewModel.deleteExpense(it) }
                    }
                    Toast.makeText(context, "Expense deleted", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun updateDateLabel(editText: TextInputEditText) {
        val myFormat = "MMM dd, yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        editText.setText(sdf.format(calendar.time))
    }
}
