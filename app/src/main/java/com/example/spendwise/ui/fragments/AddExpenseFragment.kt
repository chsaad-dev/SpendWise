package com.example.spendwise.ui.fragments

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.spendwise.R
import com.example.spendwise.data.Category
import com.example.spendwise.data.Expense
import com.example.spendwise.data.RecurringExpense
import com.example.spendwise.ui.dialogs.DeleteTransactionBottomSheet
import com.example.spendwise.ui.dialogs.ReceiptViewerDialogFragment
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.example.spendwise.utils.CurrencyHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddExpenseFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val calendar: Calendar = Calendar.getInstance()
    private var editExpenseId: Int = -1
    private var isEditMode: Boolean = false
    private var isIncome: Boolean = false
    private var receiptPath: String? = null

    private var selectedCategory: String = ""
    private var categoryList: List<Category> = emptyList()

    // Receipt photo picker
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleReceiptUri(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val cardTypeExpense = view.findViewById<MaterialCardView>(R.id.card_type_expense)
        val tvTypeExpense = view.findViewById<TextView>(R.id.tv_type_expense)
        val cardTypeIncome = view.findViewById<MaterialCardView>(R.id.card_type_income)
        val tvTypeIncome = view.findViewById<TextView>(R.id.tv_type_income)

        val tvCurrencyPrefix = view.findViewById<TextView>(R.id.tv_currency_prefix)
        val etAmount = view.findViewById<EditText>(R.id.et_amount)
        val chipAdd500 = view.findViewById<TextView>(R.id.chip_add_500)
        val chipAdd1000 = view.findViewById<TextView>(R.id.chip_add_1000)
        val chipAdd5000 = view.findViewById<TextView>(R.id.chip_add_5000)

        val layoutCategorySection = view.findViewById<View>(R.id.layout_category_section)
        val hsvCategories = view.findViewById<HorizontalScrollView>(R.id.hsv_categories)
        val layoutCategoryPills = view.findViewById<LinearLayout>(R.id.layout_category_pills)

        val tilDate = view.findViewById<TextInputLayout>(R.id.til_date)
        val etDate = view.findViewById<TextInputEditText>(R.id.et_date)
        val tilNote = view.findViewById<TextInputLayout>(R.id.til_note)
        val etNote = view.findViewById<TextInputEditText>(R.id.et_note)
        val tilTags = view.findViewById<TextInputLayout>(R.id.til_tags)
        val etTags = view.findViewById<TextInputEditText>(R.id.et_tags)

        val layoutReceipt = view.findViewById<View>(R.id.layout_receipt)
        val rowAttachReceipt = view.findViewById<View>(R.id.row_attach_receipt)
        val layoutReceiptAttached = view.findViewById<LinearLayout>(R.id.layout_receipt_attached)
        val cardReceiptPreview = view.findViewById<View>(R.id.card_receipt_preview)
        val ivReceiptPreview = view.findViewById<ImageView>(R.id.iv_receipt_preview)
        val btnRemoveReceipt = view.findViewById<MaterialButton>(R.id.btn_remove_receipt)

        val layoutRecurring = view.findViewById<View>(R.id.layout_recurring)
        val switchRecurring = view.findViewById<MaterialSwitch>(R.id.switch_recurring)
        val tilRecurrence = view.findViewById<TextInputLayout>(R.id.til_recurrence)
        val actvRecurrence = view.findViewById<AutoCompleteTextView>(R.id.actv_recurrence)

        val btnSave = view.findViewById<MaterialButton>(R.id.btn_save)
        val btnSplit = view.findViewById<MaterialButton>(R.id.btn_split)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btn_delete)

        val density = resources.displayMetrics.density

        // Back button
        btnBack.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            findNavController().popBackStack()
        }

        // Live Currency Prefix
        val symbol = try {
            CurrencyHelper.getCurrencyFormat(requireContext()).currency?.symbol?.trim() ?: "Rs"
        } catch (e: Exception) {
            "Rs"
        }
        tvCurrencyPrefix.text = symbol

        // Quick Amount Chips
        fun addQuickAmount(delta: Double) {
            val current = etAmount.text.toString().trim().toDoubleOrNull() ?: 0.0
            val updated = current + delta
            if (updated % 1.0 == 0.0) {
                etAmount.setText(updated.toInt().toString())
            } else {
                etAmount.setText(String.format(Locale.US, "%.2f", updated))
            }
            etAmount.setSelection(etAmount.text?.length ?: 0)
            etAmount.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        chipAdd500.setOnClickListener { addQuickAmount(500.0) }
        chipAdd1000.setOnClickListener { addQuickAmount(1000.0) }
        chipAdd5000.setOnClickListener { addQuickAmount(5000.0) }

        // Category Pills rendering logic
        fun renderCategoryPills(categories: List<Category>) {
            categoryList = categories
            layoutCategoryPills.removeAllViews()

            if (selectedCategory.isBlank() && categories.isNotEmpty()) {
                selectedCategory = categories.first().name
            }

            var selectedView: View? = null

            for (cat in categories) {
                val pillView = layoutInflater.inflate(R.layout.item_category_pill, layoutCategoryPills, false)
                val cardPill = pillView.findViewById<MaterialCardView>(R.id.card_category_pill)
                val viewDot = pillView.findViewById<View>(R.id.view_category_dot)
                val tvName = pillView.findViewById<TextView>(R.id.tv_category_name)

                tvName.text = cat.name

                val dotColor = try {
                    Color.parseColor(cat.colorHex)
                } catch (e: Exception) {
                    ContextCompat.getColor(requireContext(), R.color.brand_primary)
                }
                viewDot.background?.mutate()?.setTint(dotColor)

                val isSelected = cat.name.equals(selectedCategory, ignoreCase = true)
                if (isSelected) {
                    selectedView = cardPill
                    cardPill.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.brand_primary_container))
                    cardPill.strokeColor = ContextCompat.getColor(requireContext(), R.color.brand_primary)
                    cardPill.strokeWidth = (1.5f * density).toInt()
                    tvName.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))
                    tvName.typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
                } else {
                    cardPill.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_surface_subtle))
                    cardPill.strokeColor = ContextCompat.getColor(requireContext(), R.color.stroke_subtle)
                    cardPill.strokeWidth = (1f * density).toInt()
                    tvName.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                    tvName.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                }

                cardPill.setOnClickListener {
                    it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    selectedCategory = cat.name
                    renderCategoryPills(categoryList)
                    hsvCategories.post {
                        val scrollX = cardPill.left - (hsvCategories.width - cardPill.width) / 2
                        hsvCategories.smoothScrollTo(scrollX.coerceAtLeast(0), 0)
                    }
                }

                layoutCategoryPills.addView(pillView)
            }

            selectedView?.let { v ->
                hsvCategories.post {
                    val scrollX = v.left - (hsvCategories.width - v.width) / 2
                    hsvCategories.smoothScrollTo(scrollX.coerceAtLeast(0), 0)
                }
            }
        }

        // Mode switching logic
        fun updateMode(income: Boolean) {
            isIncome = income
            if (!isIncome) {
                cardTypeExpense.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_surface))
                cardTypeExpense.cardElevation = 2f * density
                tvTypeExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.danger_primary))
                tvTypeExpense.typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)

                cardTypeIncome.setCardBackgroundColor(Color.TRANSPARENT)
                cardTypeIncome.cardElevation = 0f
                tvTypeIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                tvTypeIncome.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)

                layoutCategorySection.visibility = View.VISIBLE
                tilTags.visibility = View.VISIBLE
                layoutReceipt.visibility = View.VISIBLE
                layoutRecurring.visibility = if (isEditMode) View.GONE else View.VISIBLE

                btnSave.text = if (isEditMode) "Update Expense" else "Save Expense"
                btnSave.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.brand_primary)
                if (isEditMode) {
                    tvTitle.text = "Edit Expense"
                    btnSplit.visibility = View.VISIBLE
                }
            } else {
                cardTypeIncome.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_surface))
                cardTypeIncome.cardElevation = 2f * density
                tvTypeIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_primary))
                tvTypeIncome.typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)

                cardTypeExpense.setCardBackgroundColor(Color.TRANSPARENT)
                cardTypeExpense.cardElevation = 0f
                tvTypeExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                tvTypeExpense.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)

                layoutCategorySection.visibility = View.GONE
                tilTags.visibility = View.GONE
                layoutReceipt.visibility = View.GONE
                layoutRecurring.visibility = View.GONE

                btnSave.text = if (isEditMode) "Update Income" else "Save Income"
                btnSave.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.success_primary)
                if (isEditMode) {
                    tvTitle.text = "Edit Income"
                    btnSplit.visibility = View.GONE
                }
            }
        }

        cardTypeExpense.setOnClickListener {
            if (isIncome) {
                it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                updateMode(false)
            }
        }

        cardTypeIncome.setOnClickListener {
            if (!isIncome) {
                it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                updateMode(true)
            }
        }

        // Receipt attachment
        rowAttachReceipt.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            pickImageLauncher.launch("image/*")
        }

        val openReceiptViewer = {
            receiptPath?.let { path ->
                ReceiptViewerDialogFragment.newInstance(path)
                    .show(childFragmentManager, ReceiptViewerDialogFragment.TAG)
            }
        }
        cardReceiptPreview?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            openReceiptViewer()
        }
        ivReceiptPreview.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            openReceiptViewer()
        }

        btnRemoveReceipt.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            receiptPath = null
            layoutReceiptAttached.visibility = View.GONE
            rowAttachReceipt.visibility = View.VISIBLE
        }

        // Recurrence type dropdown
        val recurrenceOptions = listOf("Daily", "Weekly", "Monthly")
        actvRecurrence.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, recurrenceOptions)
        )
        actvRecurrence.setText("Monthly", false)

        switchRecurring.setOnCheckedChangeListener { _, isChecked ->
            tilRecurrence.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Check Edit Mode
        editExpenseId = arguments?.getInt("expenseId", -1) ?: -1
        isEditMode = editExpenseId != -1

        if (isEditMode) {
            btnDelete.visibility = View.VISIBLE
            layoutRecurring.visibility = View.GONE

            viewModel.getExpenseById(editExpenseId) { expense ->
                expense?.let { exp ->
                    if (exp.amount % 1.0 == 0.0) {
                        etAmount.setText(exp.amount.toInt().toString())
                    } else {
                        etAmount.setText(String.format(Locale.US, "%.2f", exp.amount))
                    }
                    selectedCategory = exp.category
                    if (categoryList.isNotEmpty()) {
                        renderCategoryPills(categoryList)
                    }
                    etNote.setText(exp.note)
                    etTags.setText(exp.tags ?: "")
                    calendar.timeInMillis = exp.date
                    updateDateLabel(etDate)

                    if (!exp.receiptPath.isNullOrBlank()) {
                        receiptPath = exp.receiptPath
                        showReceiptPreview(ivReceiptPreview, layoutReceiptAttached, rowAttachReceipt, receiptPath!!)
                    }

                    updateMode(exp.isIncome)

                    btnSplit.setOnClickListener { _ ->
                        val bundle = Bundle().apply {
                            putInt("expenseId", exp.id)
                            putFloat("expenseAmount", exp.amount.toFloat())
                            putString("expenseCategory", exp.category)
                        }
                        findNavController().navigate(R.id.action_addExpense_to_split, bundle)
                    }
                }
            }
        } else {
            updateMode(false)
        }

        // Date Picker
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

        // Observe Categories
        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            renderCategoryPills(categories)
        }

        // Save Button
        btnSave.setOnClickListener {
            val amountStr = etAmount.text.toString().trim()
            val category = if (isIncome) "Income" else selectedCategory.trim()
            val note = etNote.text.toString().trim()
            val tags = if (isIncome) null else etTags.text.toString().trim().takeIf { it.isNotBlank() }
            val finalReceiptPath = if (isIncome) null else receiptPath

            if (amountStr.isBlank() || (!isIncome && category.isBlank())) {
                Toast.makeText(context, if (isIncome) "Please enter an amount" else "Please select a category and enter amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0.0) {
                Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

            if (isEditMode) {
                val updated = Expense(
                    id = editExpenseId,
                    amount = amount,
                    category = category,
                    date = calendar.timeInMillis,
                    note = note,
                    receiptPath = finalReceiptPath,
                    tags = tags,
                    isIncome = isIncome
                )
                viewModel.updateExpense(updated)
                Toast.makeText(context, if (isIncome) "Income updated ✓" else "Expense updated ✓", Toast.LENGTH_SHORT).show()
            } else {
                val expense = Expense(
                    amount = amount,
                    category = category,
                    date = calendar.timeInMillis,
                    note = note,
                    receiptPath = finalReceiptPath,
                    tags = tags,
                    isIncome = isIncome
                )
                viewModel.insertExpense(expense)

                if (!isIncome && switchRecurring.isChecked) {
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
                    Toast.makeText(context, if (isIncome) "Income saved ✓" else "Expense saved ✓", Toast.LENGTH_SHORT).show()
                }
            }
            findNavController().popBackStack()
        }

        // Delete Button
        btnDelete.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            DeleteTransactionBottomSheet.newInstance(isIncome) {
                viewModel.getExpenseById(editExpenseId) { expense ->
                    expense?.let { viewModel.deleteExpense(it) }
                }
                val itemType = if (isIncome) "Income" else "Expense"
                Toast.makeText(context, "$itemType deleted", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.show(parentFragmentManager, DeleteTransactionBottomSheet.TAG)
        }
    }

    private fun handleReceiptUri(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return
            val receiptsDir = File(requireContext().filesDir, "receipts")
            if (!receiptsDir.exists()) receiptsDir.mkdirs()

            val fileName = "receipt_${UUID.randomUUID()}.jpg"
            val destFile = File(receiptsDir, fileName)
            inputStream.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            receiptPath = destFile.absolutePath

            val ivPreview = view?.findViewById<ImageView>(R.id.iv_receipt_preview)
            val layoutAttached = view?.findViewById<LinearLayout>(R.id.layout_receipt_attached)
            val rowAttach = view?.findViewById<View>(R.id.row_attach_receipt)
            if (ivPreview != null && layoutAttached != null && rowAttach != null) {
                showReceiptPreview(ivPreview, layoutAttached, rowAttach, destFile.absolutePath)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to attach receipt", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showReceiptPreview(
        iv: ImageView,
        layoutAttached: LinearLayout,
        rowAttach: View,
        path: String
    ) {
        val file = File(path)
        if (file.exists()) {
            val bitmap = decodeSampledBitmap(path, 800, 800)
            if (bitmap != null) {
                iv.setImageBitmap(bitmap)
                layoutAttached.visibility = View.VISIBLE
                rowAttach.visibility = View.GONE
            }
        }
    }

    private fun decodeSampledBitmap(path: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(path, options)

            var inSampleSize = 1
            val height = options.outHeight
            val width = options.outWidth

            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2
                while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            options.inJustDecodeBounds = false
            options.inSampleSize = inSampleSize
            options.inPreferredConfig = Bitmap.Config.RGB_565
            BitmapFactory.decodeFile(path, options)
        } catch (e: Exception) {
            null
        }
    }

    private fun updateDateLabel(editText: TextInputEditText) {
        val myFormat = "MMM dd, yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        editText.setText(sdf.format(calendar.time))
    }
}
