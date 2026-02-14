package com.example.spendwise.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.example.spendwise.data.AppDatabase
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

    private var selectedYear = Calendar.getInstance().get(Calendar.YEAR)

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
        val btnPrev = view.findViewById<ImageButton>(R.id.btn_prev_year)
        val btnNext = view.findViewById<ImageButton>(R.id.btn_next_year)
        val rvMonths = view.findViewById<RecyclerView>(R.id.rv_months)
        val layoutEmpty = view.findViewById<LinearLayout>(R.id.layout_empty)
        val trendChart = view.findViewById<LineChart>(R.id.chart_trend)

        rvMonths.layoutManager = LinearLayoutManager(context)

        fun loadYear() {
            tvYear.text = selectedYear.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(requireContext(), CoroutineScope(Dispatchers.IO))
                val monthData = mutableListOf<MonthSummary>()
                val trendEntries = mutableListOf<Entry>()
                val trendLabels = mutableListOf<String>()
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
                    val total = expenses.sumOf { it.amount }

                    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.time)
                    val shortName = SimpleDateFormat("MMM", Locale.getDefault()).format(cal.time)

                    trendLabels.add(shortName)
                    trendEntries.add(Entry(month.toFloat(), total.toFloat()))

                    if (expenses.isNotEmpty()) {
                        yearTotal += total
                        val categoryBreakdown = expenses
                            .groupBy { it.category }
                            .map { (cat, items) -> cat to items.sumOf { it.amount } }
                            .sortedByDescending { it.second }

                        monthData.add(MonthSummary(monthName, total, expenses.size, categoryBreakdown))
                    }
                }

                // Reverse so recent months show first
                monthData.reverse()

                withContext(Dispatchers.Main) {
                    val fmt = NumberFormat.getCurrencyInstance(Locale.getDefault())
                    tvYearlyTotal.text = fmt.format(yearTotal)

                    // Trend chart
                    if (trendChart != null) {
                        setupTrendChart(trendChart, trendEntries, trendLabels)
                    }

                    if (monthData.isEmpty()) {
                        rvMonths.visibility = View.GONE
                        layoutEmpty.visibility = View.VISIBLE
                    } else {
                        rvMonths.visibility = View.VISIBLE
                        layoutEmpty.visibility = View.GONE
                        rvMonths.adapter = MonthAdapter(monthData, fmt)
                    }
                }
            }
        }

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

    private fun setupTrendChart(chart: LineChart, entries: List<Entry>, labels: List<String>) {
        val dataSet = LineDataSet(entries, "Monthly Spending").apply {
            color = Color.parseColor("#006B75")
            lineWidth = 2.5f
            setDrawCircles(true)
            setCircleColor(Color.parseColor("#006B75"))
            circleRadius = 4f
            setDrawCircleHole(true)
            circleHoleRadius = 2.5f
            setDrawFilled(true)
            fillColor = Color.parseColor("#006B75")
            fillAlpha = 40
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        chart.data = LineData(dataSet)

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setTouchEnabled(false)
        chart.setDrawGridBackground(false)

        chart.axisLeft.apply {
            setDrawGridLines(true)
            gridColor = Color.parseColor("#E0E0E0")
            setDrawAxisLine(false)
            textColor = Color.parseColor("#888888")
            textSize = 10f
        }
        chart.axisRight.isEnabled = false

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(labels)
            granularity = 1f
            setDrawGridLines(false)
            setDrawAxisLine(false)
            textColor = Color.parseColor("#888888")
            textSize = 10f
            labelCount = labels.size
        }

        chart.animateX(600)
        chart.invalidate()
    }

    data class MonthSummary(
        val monthName: String,
        val total: Double,
        val count: Int,
        val categories: List<Pair<String, Double>>
    )

    class MonthAdapter(
        private val months: List<MonthSummary>,
        private val fmt: NumberFormat
    ) : RecyclerView.Adapter<MonthAdapter.VH>() {

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_month_name)
            val tvTotal: TextView = view.findViewById(R.id.tv_month_total)
            val tvCount: TextView = view.findViewById(R.id.tv_month_count)
            val layoutCategories: LinearLayout = view.findViewById(R.id.layout_categories)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_month_summary, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val month = months[position]
            holder.tvName.text = month.monthName
            holder.tvTotal.text = fmt.format(month.total)
            holder.tvCount.text = "${month.count} transactions"

            holder.layoutCategories.removeAllViews()

            month.categories.forEach { (category, amount) ->
                val row = LinearLayout(holder.itemView.context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 6, 0, 6)
                }

                val tvCat = TextView(holder.itemView.context).apply {
                    text = category
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall)
                    setTextColor(context.getColor(android.R.color.darker_gray))
                }

                val tvAmt = TextView(holder.itemView.context).apply {
                    text = fmt.format(amount)
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall)
                    setTextColor(context.getColor(android.R.color.darker_gray))
                }

                row.addView(tvCat)
                row.addView(tvAmt)
                holder.layoutCategories.addView(row)
            }
        }

        override fun getItemCount() = months.size
    }
}
