package com.example.spendwise.ui.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.example.spendwise.data.Expense
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseAdapter(
    private val onItemClick: (Expense) -> Unit
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(DiffCallback()) {

    private val categoryColors = mapOf(
        "Food" to "#FF6B35",
        "Transport" to "#1B98E0",
        "Shopping" to "#E84393",
        "Entertainment" to "#A855F7",
        "Health" to "#10B981",
        "Others" to "#64748B"
    )

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        private val tvNote: TextView = itemView.findViewById(R.id.tv_note)
        private val categoryDot: View = itemView.findViewById(R.id.view_category_dot)

        fun bind(expense: Expense) {
            tvCategory.text = expense.category
            tvAmount.text = currencyFormat.format(expense.amount)
            tvNote.text = if (expense.note.isBlank()) "—" else expense.note

            val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            tvDate.text = dateFormat.format(Date(expense.date))

            val color = categoryColors[expense.category] ?: "#64748B"
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setColor(Color.parseColor(color))
            categoryDot.background = drawable

            itemView.setOnClickListener { onItemClick(expense) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Expense, newItem: Expense) = oldItem == newItem
    }
}
