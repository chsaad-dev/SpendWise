package com.example.spendwise.ui.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class CurrencyPickerBottomSheet : BottomSheetDialogFragment() {

    data class CurrencyOption(
        val code: String,
        val symbol: String,
        val name: String,
        val description: String
    )

    private var selectedCode: String = "default"
    private var onCurrencySelectedListener: ((code: String, displayName: String) -> Unit)? = null

    private val currencies = listOf(
        CurrencyOption("default", "⚙", "System Default", "Device default locale & currency"),
        CurrencyOption("PKR", "Rs", "PKR — Pakistani Rupee", "Pakistani Rupee (Rs)"),
        CurrencyOption("USD", "$", "USD — US Dollar", "United States Dollar ($)"),
        CurrencyOption("EUR", "€", "EUR — Euro", "European Euro (€)"),
        CurrencyOption("GBP", "£", "GBP — British Pound", "British Pound Sterling (£)"),
        CurrencyOption("INR", "₹", "INR — Indian Rupee", "Indian Rupee (₹)"),
        CurrencyOption("AED", "د.إ", "AED — UAE Dirham", "UAE Dirham (د.إ)"),
        CurrencyOption("SAR", "﷼", "SAR — Saudi Riyal", "Saudi Riyal (﷼)"),
        CurrencyOption("CAD", "C$", "CAD — Canadian Dollar", "Canadian Dollar (C$)"),
        CurrencyOption("AUD", "A$", "AUD — Australian Dollar", "Australian Dollar (A$)"),
        CurrencyOption("JPY", "¥", "JPY — Japanese Yen", "Japanese Yen (¥)"),
        CurrencyOption("BDT", "৳", "BDT — Bangladeshi Taka", "Bangladeshi Taka (৳)")
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                sheet.setBackgroundResource(android.R.color.transparent)
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_currency_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvCurrencies = view.findViewById<RecyclerView>(R.id.rv_currencies)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btn_cancel_currency)

        rvCurrencies.layoutManager = LinearLayoutManager(requireContext())
        rvCurrencies.adapter = CurrencyAdapter(currencies, selectedCode) { option ->
            onCurrencySelectedListener?.invoke(option.code, option.name)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private class CurrencyAdapter(
        private val items: List<CurrencyOption>,
        private val currentSelected: String,
        private val onClick: (CurrencyOption) -> Unit
    ) : RecyclerView.Adapter<CurrencyAdapter.VH>() {

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val card: MaterialCardView = view.findViewById(R.id.card_currency)
            val badge: MaterialCardView = view.findViewById(R.id.badge_symbol)
            val tvSymbol: TextView = view.findViewById(R.id.tv_currency_symbol)
            val tvName: TextView = view.findViewById(R.id.tv_currency_name)
            val tvSubtitle: TextView = view.findViewById(R.id.tv_currency_subtitle)
            val ivCheck: ImageView = view.findViewById(R.id.iv_currency_check)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_currency_option, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            val context = holder.itemView.context
            val isSelected = item.code.equals(currentSelected, ignoreCase = true)

            holder.tvSymbol.text = item.symbol
            holder.tvName.text = item.name
            holder.tvSubtitle.text = item.description

            val density = context.resources.displayMetrics.density

            if (isSelected) {
                holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.brand_primary_container))
                holder.card.strokeColor = ContextCompat.getColor(context, R.color.brand_primary)
                holder.card.strokeWidth = (1.5f * density).toInt()

                holder.badge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.brand_primary))
                holder.tvSymbol.setTextColor(Color.WHITE)

                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.brand_primary))
                holder.ivCheck.visibility = View.VISIBLE
            } else {
                holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.bg_surface_subtle))
                holder.card.strokeColor = ContextCompat.getColor(context, R.color.stroke_subtle)
                holder.card.strokeWidth = (1f * density).toInt()

                holder.badge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.bg_surface))
                holder.tvSymbol.setTextColor(ContextCompat.getColor(context, R.color.text_primary))

                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                holder.ivCheck.visibility = View.GONE
            }

            holder.card.setOnClickListener {
                onClick(item)
            }
        }

        override fun getItemCount(): Int = items.size
    }

    companion object {
        const val TAG = "CurrencyPickerBottomSheet"

        fun newInstance(
            currentCode: String,
            onSelected: (code: String, displayName: String) -> Unit
        ): CurrencyPickerBottomSheet {
            return CurrencyPickerBottomSheet().apply {
                this.selectedCode = currentCode
                this.onCurrencySelectedListener = onSelected
            }
        }
    }
}
