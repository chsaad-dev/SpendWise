package com.example.spendwise.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.spendwise.R
import com.example.spendwise.data.Category
import com.example.spendwise.utils.CurrencyHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class CategoryBudgetBottomSheet : BottomSheetDialogFragment() {

    private var category: Category? = null
    private var onSaveCallback: ((Double) -> Unit)? = null
    private var onRemoveCallback: (() -> Unit)? = null

    override fun getTheme(): Int = R.style.Theme_SpendWise_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_category_budget, container, false)
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

        val cat = category ?: return

        val badgeCategory = view.findViewById<FrameLayout>(R.id.badge_budget_category)
        val ivCategoryIcon = view.findViewById<ImageView>(R.id.iv_budget_category_icon)
        val tvTitle = view.findViewById<TextView>(R.id.tv_budget_title)
        val tilAmount = view.findViewById<TextInputLayout>(R.id.til_budget_amount)
        val etAmount = view.findViewById<TextInputEditText>(R.id.et_budget_amount)
        val chip1 = view.findViewById<Chip>(R.id.chip_preset_1)
        val chip2 = view.findViewById<Chip>(R.id.chip_preset_2)
        val chip3 = view.findViewById<Chip>(R.id.chip_preset_3)
        val btnSave = view.findViewById<MaterialButton>(R.id.btn_save_budget)
        val btnRemove = view.findViewById<MaterialButton>(R.id.btn_remove_limit)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btn_cancel_budget)

        tvTitle.text = "Budget for ${cat.name}"

        val categoryColors = mapOf(
            "Food" to "#FF6B35",
            "Transport" to "#1B98E0",
            "Shopping" to "#E84393",
            "Entertainment" to "#A855F7",
            "Health" to "#10B981",
            "Others" to "#64748B"
        )
        val colorHex = if (cat.colorHex != "#000000" && cat.colorHex.isNotBlank()) cat.colorHex
        else categoryColors[cat.name] ?: "#64748B"
        val parsedColor = try {
            Color.parseColor(colorHex)
        } catch (e: Exception) {
            Color.parseColor("#64748B")
        }

        val badgeBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = resources.displayMetrics.density * 12f
            val alpha15 = (255 * 0.15).toInt()
            val colorWithAlpha = Color.argb(
                alpha15,
                Color.red(parsedColor),
                Color.green(parsedColor),
                Color.blue(parsedColor)
            )
            setColor(colorWithAlpha)
        }
        badgeCategory.background = badgeBg

        val iconRes = when (cat.name.lowercase().trim()) {
            "food" -> R.drawable.ic_cat_food
            "transport" -> R.drawable.ic_cat_transport
            "shopping" -> R.drawable.ic_cat_shopping
            "entertainment" -> R.drawable.ic_cat_entertainment
            "health" -> R.drawable.ic_cat_health
            else -> R.drawable.ic_cat_others
        }
        ivCategoryIcon.setImageResource(iconRes)
        ivCategoryIcon.setColorFilter(parsedColor)

        val fmt = CurrencyHelper.getCurrencyFormat(requireContext())
        val symbol = fmt.currency?.getSymbol(Locale.getDefault()) ?: fmt.currency?.symbol ?: ""
        val prefix = if (symbol.isNotBlank()) "$symbol " else "Rs "
        tilAmount.prefixText = prefix

        val prefixTrimmed = prefix.trim()
        chip1.text = "$prefixTrimmed 2,000"
        chip2.text = "$prefixTrimmed 5,000"
        chip3.text = "$prefixTrimmed 10,000"

        chip1.setOnClickListener {
            etAmount.setText("2000")
            etAmount.setSelection(etAmount.text?.length ?: 0)
        }
        chip2.setOnClickListener {
            etAmount.setText("5000")
            etAmount.setSelection(etAmount.text?.length ?: 0)
        }
        chip3.setOnClickListener {
            etAmount.setText("10000")
            etAmount.setSelection(etAmount.text?.length ?: 0)
        }

        if (cat.monthlyBudget > 0) {
            val budgetFormatted = if (cat.monthlyBudget % 1.0 == 0.0) {
                cat.monthlyBudget.toLong().toString()
            } else {
                cat.monthlyBudget.toString()
            }
            etAmount.setText(budgetFormatted)
            etAmount.setSelection(budgetFormatted.length)
            btnRemove.visibility = View.VISIBLE
        } else {
            btnRemove.visibility = View.GONE
            (btnCancel.layoutParams as? LinearLayout.LayoutParams)?.let { lp ->
                lp.marginStart = 0
                btnCancel.layoutParams = lp
            }
        }

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            onSaveCallback?.invoke(amount)
            dismiss()
        }

        btnRemove.setOnClickListener {
            onRemoveCallback?.invoke()
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "CategoryBudgetBottomSheet"

        fun newInstance(
            category: Category,
            onSave: (Double) -> Unit,
            onRemove: () -> Unit
        ): CategoryBudgetBottomSheet {
            return CategoryBudgetBottomSheet().apply {
                this.category = category
                this.onSaveCallback = onSave
                this.onRemoveCallback = onRemove
            }
        }
    }
}
