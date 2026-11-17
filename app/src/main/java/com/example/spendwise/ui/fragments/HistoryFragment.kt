package com.example.spendwise.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.example.spendwise.data.AppDatabase
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.example.spendwise.utils.CurrencyHelper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var selectedYear = Calendar.getInstance().get(Calendar.YEAR)
    private var reloadYearAction: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvYear = view.findViewById<TextView>(R.id.tv_year_label)
        val tvYearlyTotal = view.findViewById<TextView>(R.id.tv_yearly_total)
        val tvYearlyIncome = view.findViewById<TextView>(R.id.tv_yearly_income)
        val tvYearlySpent = view.findViewById<TextView>(R.id.tv_yearly_spent)
        val btnPrev = view.findViewById<ImageButton>(R.id.btn_prev_year)
        val btnNext = view.findViewById<ImageButton>(R.id.btn_next_year)
        val rvMonths = view.findViewById<RecyclerView>(R.id.rv_months)
        val layoutEmpty = view.findViewById<LinearLayout>(R.id.layout_empty)
        val cardTrendChart = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.card_trend_chart)
        val trendChart = view.findViewById<LineChart>(R.id.chart_trend)

        rvMonths.layoutManager = LinearLayoutManager(context)

        fun loadYear() {
            if (!isAdded) return
            tvYear.text = selectedYear.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val ctx = context ?: return@launch
                val db = AppDatabase.getDatabase(ctx, CoroutineScope(Dispatchers.IO))
                val monthData = mutableListOf<MonthSummary>()
                val trendEntries = mutableListOf<Entry>()
                val trendLabels = mutableListOf<String>()
                var yearIncome = 0.0
                var yearSpent = 0.0
                var yearTotal = 0.0

                for (month in 0..11) {
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, selectedYear)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val start = cal.timeInMillis

                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                    cal.set(Calendar.HOUR_OF_DAY, 23)
                    cal.set(Calendar.MINUTE, 59)
                    cal.set(Calendar.SECOND, 59)
                    cal.set(Calendar.MILLISECOND, 999)
                    val end = cal.timeInMillis

                    val expenses = db.expenseDao().getExpensesByDateRangeSync(start, end)
                    
                    val incomeSum = expenses.filter { it.isIncome }.sumOf { it.amount }
                    val expenseSum = expenses.filter { !it.isIncome }.sumOf { it.amount }
                    val balance = incomeSum - expenseSum

                    yearIncome += incomeSum
                    yearSpent += expenseSum
                    yearTotal += balance

                    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.time)
                    val shortName = SimpleDateFormat("MMM", Locale.getDefault()).format(cal.time)

                    trendLabels.add(shortName)
                    val plotValue = Math.max(0.0, balance).toFloat()
                    trendEntries.add(Entry(month.toFloat(), plotValue))

                    if (expenses.isNotEmpty()) {
                        val categoryBreakdown = expenses
                            .filter { !it.isIncome }
                            .groupBy { it.category }
                            .map { (cat, items) -> cat to items.sumOf { it.amount } }
                            .sortedByDescending { it.second }

                        monthData.add(MonthSummary(monthName, month, balance, expenses.size, categoryBreakdown))
                    }
                }

                // Reverse so recent months show first
                monthData.reverse()

                withContext(Dispatchers.Main) {
                    if (!isAdded) return@withContext
                    val fmt = CurrencyHelper.getCurrencyFormat(ctx)
                    tvYearlyTotal.text = fmt.format(yearTotal)
                    tvYearlyIncome.text = fmt.format(yearIncome)
                    tvYearlySpent.text = fmt.format(yearSpent)

                    // Trend chart
                    if (trendChart != null) {
                        setupTrendChart(trendChart, trendEntries, trendLabels)
                    }

                    if (monthData.isEmpty()) {
                        rvMonths.visibility = View.GONE
                        cardTrendChart.visibility = View.GONE
                        layoutEmpty.visibility = View.VISIBLE
                    } else {
                        rvMonths.visibility = View.VISIBLE
                        cardTrendChart.visibility = View.VISIBLE
                        layoutEmpty.visibility = View.GONE
                        rvMonths.adapter = MonthAdapter(monthData, fmt) { monthIndex ->
                            val filterCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, selectedYear)
                                set(Calendar.MONTH, monthIndex)
                                set(Calendar.DAY_OF_MONTH, 1)
                            }
                            viewModel.setMonthFilter(filterCal)

                            val resultBundle = Bundle().apply {
                                putInt("selected_month", monthIndex)
                                putInt("selected_year", selectedYear)
                            }
                            parentFragmentManager.setFragmentResult("request_select_month", resultBundle)

                            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.homeFragment
                        }
                    }
                }
            }
        }

        reloadYearAction = { loadYear() }
        loadYear()

        btnPrev.setOnClickListener {
            selectedYear--
            loadYear()
        }

        btnNext.setOnClickListener {
            selectedYear++
            loadYear()
        }
    }

    override fun onResume() {
        super.onResume()
        reloadYearAction?.invoke()
    }

    private fun setupTrendChart(chart: LineChart, entries: List<Entry>, labels: List<String>) {
        val ctx = chart.context
        val primaryColor = ContextCompat.getColor(ctx, R.color.brand_primary)
        val secondaryTextColor = ContextCompat.getColor(ctx, R.color.text_secondary)
        val strokeSubtleColor = ContextCompat.getColor(ctx, R.color.stroke_subtle)
        val surfaceColor = ContextCompat.getColor(ctx, R.color.bg_surface)

        val dataSet = LineDataSet(entries, "Monthly Spending").apply {
            color = primaryColor
            lineWidth = 2.5f
            setDrawCircles(true)
            circleRadius = 4f
            setCircleColor(primaryColor)
            setDrawCircleHole(true)
            circleHoleRadius = 2.5f
            circleHoleColor = surfaceColor
            setDrawFilled(true)
            fillColor = primaryColor
            fillAlpha = 35
            setDrawValues(false)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        }

        chart.data = LineData(dataSet)

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setTouchEnabled(false)
        chart.setDrawGridBackground(false)

        chart.axisLeft.apply {
            resetAxisMaximum()
            axisMinimum = 0f
            spaceTop = 18f
            setDrawGridLines(true)
            gridColor = strokeSubtleColor
            setDrawAxisLine(false)
            textColor = secondaryTextColor
            textSize = 10f
        }
        chart.axisRight.isEnabled = false

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(labels)
            granularity = 1f
            setDrawGridLines(false)
            setDrawAxisLine(false)
            textColor = secondaryTextColor
            textSize = 10f
            labelCount = labels.size
        }

        chart.animateX(600)
        chart.invalidate()
    }

    data class MonthSummary(
        val monthName: String,
        val monthIndex: Int,
        val total: Double,
        val count: Int,
        val categories: List<Pair<String, Double>>
    )

    class MonthAdapter(
        private val months: List<MonthSummary>,
        private val fmt: NumberFormat,
        private val onMonthClick: (monthIndex: Int) -> Unit
    ) : RecyclerView.Adapter<MonthAdapter.VH>() {

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_month_name)
            val tvTotal: TextView = view.findViewById(R.id.tv_month_total)
            val tvCount: TextView = view.findViewById(R.id.tv_month_count)
            val layoutCategories: LinearLayout = view.findViewById(R.id.layout_categories)
            val tvViewDetails: TextView = view.findViewById(R.id.tv_view_details)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_month_summary, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val month = months[position]
            val context = holder.itemView.context
            holder.tvName.text = month.monthName
            
            val absTotal = Math.abs(month.total)
            if (month.total > 0) {
                holder.tvTotal.text = "+${fmt.format(absTotal)}"
                holder.tvTotal.backgroundTintList = ContextCompat.getColorStateList(context, R.color.success_container)
                holder.tvTotal.setTextColor(ContextCompat.getColor(context, R.color.success_primary))
            } else if (month.total == 0.0) {
                holder.tvTotal.text = fmt.format(0.0)
                holder.tvTotal.backgroundTintList = ContextCompat.getColorStateList(context, R.color.bg_surface_subtle)
                holder.tvTotal.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            } else {
                holder.tvTotal.text = "-${fmt.format(absTotal)}"
                holder.tvTotal.backgroundTintList = ContextCompat.getColorStateList(context, R.color.danger_container)
                holder.tvTotal.setTextColor(ContextCompat.getColor(context, R.color.danger_primary))
            }

            holder.tvCount.text = if (month.count == 1) "1 transaction" else "${month.count} transactions"

            holder.layoutCategories.removeAllViews()

            val density = context.resources.displayMetrics.density
            val verticalPadding = (3 * density).toInt()

            month.categories.forEach { (category, amount) ->
                val row = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = verticalPadding
                        bottomMargin = verticalPadding
                    }
                }

                val tvCat = TextView(context).apply {
                    text = category
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    textSize = 14f
                    setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                }

                val tvAmt = TextView(context).apply {
                    text = "-${fmt.format(amount)}"
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    textSize = 14f
                    typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                    setTextColor(ContextCompat.getColor(context, R.color.danger_primary))
                }

                row.addView(tvCat)
                row.addView(tvAmt)
                holder.layoutCategories.addView(row)
            }

            holder.itemView.setOnClickListener {
                onMonthClick(month.monthIndex)
            }
            holder.tvViewDetails.setOnClickListener {
                onMonthClick(month.monthIndex)
            }
        }

        override fun getItemCount() = months.size
    }
}
