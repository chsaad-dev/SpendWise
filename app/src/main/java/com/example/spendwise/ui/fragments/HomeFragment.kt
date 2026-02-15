package com.example.spendwise.ui.fragments

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: ExpenseAdapter
    private val currentMonth = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTotal = view.findViewById<TextView>(R.id.tv_total_expense)
        val tvCount = view.findViewById<TextView>(R.id.tv_transaction_count)
        val tvMonth = view.findViewById<TextView>(R.id.tv_month_label)
        val btnPrev = view.findViewById<ImageButton>(R.id.btn_prev_month)
        val btnNext = view.findViewById<ImageButton>(R.id.btn_next_month)
        val rvExpenses = view.findViewById<RecyclerView>(R.id.rv_expenses)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add)
        val chart = view.findViewById<HorizontalBarChart>(R.id.chart_categories)
        val chipGroup = view.findViewById<ChipGroup>(R.id.chip_group_categories)
        val layoutEmpty = view.findViewById<LinearLayout>(R.id.layout_empty)
        val layoutChartEmpty = view.findViewById<LinearLayout>(R.id.layout_chart_empty)
        val etSearch = view.findViewById<TextInputEditText>(R.id.et_search)
        val layoutBudget = view.findViewById<LinearLayout>(R.id.layout_budget)
        val progressBudget = view.findViewById<LinearProgressIndicator>(R.id.progress_budget)
        val tvBudget = view.findViewById<TextView>(R.id.tv_budget_label)
        val cardInsight = view.findViewById<MaterialCardView>(R.id.card_insight)
        val tvTopCategory = view.findViewById<TextView>(R.id.tv_top_category)

        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

        fun updateMonthLabel() {
            tvMonth.text = monthFormat.format(currentMonth.time)
            viewModel.setMonthFilter(currentMonth)
        }

        updateMonthLabel()

        btnPrev.setOnClickListener {
            currentMonth.add(Calendar.MONTH, -1)
            updateMonthLabel()
        }

        btnNext.setOnClickListener {
            currentMonth.add(Calendar.MONTH, 1)
            updateMonthLabel()
        }

        // Search
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString()?.takeIf { it.isNotBlank() })
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Adapter with click to edit + long-press to duplicate
        adapter = ExpenseAdapter(
            onItemClick = { expense ->
                val bundle = Bundle().apply {
                    putInt("expenseId", expense.id)
                }
                findNavController().navigate(R.id.action_home_to_addExpense, bundle)
            },
            onItemLongClick = { expense ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Duplicate Expense")
                    .setMessage("Create a copy of this ${expense.category} expense (${viewModel.formatCurrency(expense.amount)})?")
                    .setPositiveButton("Duplicate") { _, _ ->
                        viewModel.duplicateExpense(expense)
                        Toast.makeText(context, "Expense duplicated", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        rvExpenses.layoutManager = LinearLayoutManager(context)
        rvExpenses.adapter = adapter

        // Swipe to delete with Undo Snackbar
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private val deleteBackground = ColorDrawable(Color.parseColor("#FFDAD6"))

            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val expense = adapter.currentList[position]

                viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

                // Delete immediately, offer undo
                viewModel.deleteExpense(expense)
                Snackbar.make(view, "Expense deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        viewModel.insertExpense(expense)
                    }
                    .setAnchorView(fabAdd)
                    .show()
            }

            override fun onChildDraw(c: Canvas, rv: RecyclerView, vh: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = vh.itemView
                if (dX < 0) {
                    deleteBackground.setBounds(
                        itemView.right + dX.toInt(), itemView.top,
                        itemView.right, itemView.bottom
                    )
                    deleteBackground.draw(c)
                }
                super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(rvExpenses)

        // Observe filtered expenses
        viewModel.filteredExpenses.observe(viewLifecycleOwner) { expenses ->
            adapter.submitList(expenses)
            layoutEmpty.visibility = if (expenses.isNullOrEmpty()) View.VISIBLE else View.GONE
            rvExpenses.visibility = if (expenses.isNullOrEmpty()) View.GONE else View.VISIBLE
            tvCount.text = "${expenses?.size ?: 0} transactions"
        }

        // Observe monthly total + budget bar
        viewModel.monthlyTotal.observe(viewLifecycleOwner) { total ->
            tvTotal.text = viewModel.formatCurrency(total ?: 0.0)

            // Budget progress
            val budget = viewModel.getBudgetLimit()
            if (budget > 0f) {
                layoutBudget.visibility = View.VISIBLE
                val spent = (total ?: 0.0).toFloat()
                val pct = ((spent / budget) * 100).toInt().coerceIn(0, 100)
                progressBudget.setProgressCompat(pct, true)
                tvBudget.text = "${viewModel.formatCurrency(spent.toDouble())} / ${viewModel.formatCurrency(budget.toDouble())} (${pct}%)"

                // Color: green < 60%, yellow 60-85%, red > 85%
                val color = when {
                    pct < 60 -> Color.WHITE
                    pct < 85 -> Color.parseColor("#FFEB3B")
                    else -> Color.parseColor("#FF5252")
                }
                progressBudget.setIndicatorColor(color)
            } else {
                layoutBudget.visibility = View.GONE
            }
        }

        // Category filter chips
        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            chipGroup.removeAllViews()

            val allChip = Chip(requireContext()).apply {
                text = "All"
                isCheckable = true
                isChecked = true
                setOnClickListener {
                    viewModel.setCategoryFilter(null)
                }
            }
            chipGroup.addView(allChip)

            categories.forEach { cat ->
                val chip = Chip(requireContext()).apply {
                    text = cat.name
                    isCheckable = true
                    setOnClickListener {
                        viewModel.setCategoryFilter(cat.name)
                    }
                }
                chipGroup.addView(chip)
            }
        }

        // Top category insight + Chart
        setupChart(chart)
        val pieChart = view.findViewById<PieChart>(R.id.chart_pie)
        val cardPieChart = view.findViewById<MaterialCardView>(R.id.card_pie_chart)
        setupPieChart(pieChart)

        viewModel.categoryTotals.observe(viewLifecycleOwner) { totals ->
            if (totals.isNullOrEmpty()) {
                chart.visibility = View.GONE
                layoutChartEmpty.visibility = View.VISIBLE
                cardInsight.visibility = View.GONE
                cardPieChart.visibility = View.GONE
            } else {
                chart.visibility = View.VISIBLE
                layoutChartEmpty.visibility = View.GONE
                cardPieChart.visibility = View.VISIBLE
                updateChart(chart, totals.map { it.category }, totals.map { it.total.toFloat() })
                updatePieChart(pieChart, totals.map { it.category }, totals.map { it.total.toFloat() })

                // Top category insight
                val top = totals.maxByOrNull { it.total }
                val totalSpend = totals.sumOf { it.total }
                if (top != null && totalSpend > 0) {
                    val pct = ((top.total / totalSpend) * 100).toInt()
                    tvTopCategory.text = "💡 Biggest spend this month: ${top.category} (${pct}%)"
                    cardInsight.visibility = View.VISIBLE
                } else {
                    cardInsight.visibility = View.GONE
                }
            }
        }

        fabAdd.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            findNavController().navigate(R.id.action_home_to_addExpense)
        }
    }

    private fun setupChart(chart: HorizontalBarChart) {
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setTouchEnabled(false)
        chart.setDrawGridBackground(false)
        chart.setDrawBarShadow(false)
        chart.setFitBars(true)

        chart.axisLeft.isEnabled = false
        chart.axisRight.isEnabled = false

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(false)
            textColor = Color.parseColor("#3F484A")
            granularity = 1f
        }
    }

    private fun updateChart(chart: HorizontalBarChart, labels: List<String>, values: List<Float>) {
        val chartColors = listOf(
            Color.parseColor("#006B75"),
            Color.parseColor("#A855F7"),
            Color.parseColor("#F59E0B"),
            Color.parseColor("#EF4444"),
            Color.parseColor("#3B82F6"),
            Color.parseColor("#10B981"),
            Color.parseColor("#EC4899"),
            Color.parseColor("#6366F1")
        )

        val entries = values.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }

        val dataSet = BarDataSet(entries, "").apply {
            colors = chartColors.take(values.size)
            setDrawValues(true)
            valueTextSize = 11f
            valueTextColor = Color.parseColor("#3F484A")
        }

        chart.data = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        chart.xAxis.labelCount = labels.size
        chart.invalidate()
    }

    private fun setupPieChart(chart: PieChart) {
        chart.description.isEnabled = false
        chart.setUsePercentValues(true)
        chart.setDrawEntryLabels(true)
        chart.setEntryLabelColor(Color.parseColor("#3F484A"))
        chart.setEntryLabelTextSize(11f)
        chart.isDrawHoleEnabled = true
        chart.holeRadius = 50f
        chart.transparentCircleRadius = 55f
        chart.setHoleColor(Color.TRANSPARENT)
        chart.setCenterTextSize(14f)
        chart.setCenterTextColor(Color.parseColor("#3F484A"))
        chart.legend.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isRotationEnabled = true
        chart.animateY(800)
    }

    private fun updatePieChart(chart: PieChart, labels: List<String>, values: List<Float>) {
        val pieColors = listOf(
            Color.parseColor("#006B75"),
            Color.parseColor("#A855F7"),
            Color.parseColor("#F59E0B"),
            Color.parseColor("#EF4444"),
            Color.parseColor("#3B82F6"),
            Color.parseColor("#10B981"),
            Color.parseColor("#EC4899"),
            Color.parseColor("#6366F1")
        )

        val entries = labels.zip(values).map { (label, value) ->
            PieEntry(value, label)
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = pieColors.take(values.size)
            sliceSpace = 2f
            selectionShift = 5f
            setDrawValues(true)
            valueTextSize = 10f
            valueTextColor = Color.WHITE
            valueFormatter = PercentFormatter(chart)
        }

        chart.data = PieData(dataSet)
        val total = values.sum()
        chart.centerText = viewModel.formatCurrency(total.toDouble())
        chart.invalidate()
    }
}
