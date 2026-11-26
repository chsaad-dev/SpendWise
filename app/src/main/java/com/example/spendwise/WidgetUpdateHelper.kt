package com.example.spendwise

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import com.example.spendwise.data.AppDatabase
import com.example.spendwise.data.Expense
import com.example.spendwise.utils.CurrencyHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object WidgetUpdateHelper {

    const val EXTRA_OPEN_ADD_EXPENSE = "open_add_expense"
    const val EXTRA_PRESET_IS_INCOME = "preset_is_income"
    const val EXTRA_OPEN_BUDGET_SHEET = "open_budget_sheet"

    private const val PREFS_NAME = "SpendWisePrefs"
    private const val KEY_MONTHLY_BUDGET = "monthly_budget"

    private const val SLOT_QUICK_HOME = 1
    private const val SLOT_QUICK_EXPENSE = 2
    private const val SLOT_QUICK_INCOME = 3
    private const val SLOT_PULSE_HOME = 4
    private const val SLOT_PULSE_ADD = 5
    private const val SLOT_PULSE_BUDGET = 6
    private const val SLOT_RECENT_HOME = 7
    private const val SLOT_RECENT_ADD = 8

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    enum class WidgetType {
        QUICK_ADD,
        BUDGET_PULSE,
        RECENT_ACTIVITY
    }

    private data class RecentRowIds(
        val rowId: Int,
        val categoryId: Int,
        val subtitleId: Int,
        val amountId: Int
    )

    private val recentRows = listOf(
        RecentRowIds(R.id.row_1, R.id.tv_row_1_category, R.id.tv_row_1_subtitle, R.id.tv_row_1_amount),
        RecentRowIds(R.id.row_2, R.id.tv_row_2_category, R.id.tv_row_2_subtitle, R.id.tv_row_2_amount),
        RecentRowIds(R.id.row_3, R.id.tv_row_3_category, R.id.tv_row_3_subtitle, R.id.tv_row_3_amount)
    )

    fun isDarkTheme(context: Context): Boolean {
        return when (SpendWiseApp.getSavedThemeMode(context)) {
            SpendWiseApp.THEME_DARK -> true
            SpendWiseApp.THEME_LIGHT -> false
            else -> {
                val nightFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                nightFlags == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    fun updateAllWidgets(context: Context) {
        val appContext = context.applicationContext
        ioScope.launch {
            updateAllWidgetsSync(appContext)
        }
    }

    suspend fun updateAllWidgetsSync(context: Context) {
        val appContext = context.applicationContext
        try {
            val manager = AppWidgetManager.getInstance(appContext)
            val quickIds = manager.getAppWidgetIds(ComponentName(appContext, QuickAddWidgetProvider::class.java))
            val pulseIds = manager.getAppWidgetIds(ComponentName(appContext, BudgetPulseWidgetProvider::class.java))
            val recentIds = manager.getAppWidgetIds(ComponentName(appContext, RecentActivityWidgetProvider::class.java))

            if (quickIds.isEmpty() && pulseIds.isEmpty() && recentIds.isEmpty()) return

            val dao = AppDatabase.getDatabase(appContext, ioScope).expenseDao()
            val isDark = isDarkTheme(appContext)

            if (quickIds.isNotEmpty()) {
                val (todayStart, todayEnd) = getTodayRange()
                val todayExpense = dao.getExpenseSumInRange(todayStart, todayEnd)
                for (id in quickIds) {
                    manager.updateAppWidget(id, buildQuickAddViews(appContext, id, isDark, todayExpense))
                }
            }

            if (pulseIds.isNotEmpty()) {
                val (monthStart, monthEnd) = getCurrentMonthRange()
                val totalIncome = dao.getTotalIncomeSync()
                val totalExpense = dao.getTotalExpenseSync()
                val monthlyIncome = dao.getIncomeSumInRange(monthStart, monthEnd)
                val monthlyExpense = dao.getExpenseSumInRange(monthStart, monthEnd)
                val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val monthlyBudget = prefs.getFloat(KEY_MONTHLY_BUDGET, 0f)
                for (id in pulseIds) {
                    manager.updateAppWidget(
                        id,
                        buildBudgetPulseViews(
                            appContext,
                            id,
                            isDark,
                            totalIncome - totalExpense,
                            monthlyIncome,
                            monthlyExpense,
                            monthlyBudget
                        )
                    )
                }
            }

            if (recentIds.isNotEmpty()) {
                val recentList = dao.getRecentExpensesSync(3)
                for (id in recentIds) {
                    manager.updateAppWidget(id, buildRecentActivityViews(appContext, id, isDark, recentList))
                }
            }
        } catch (_: Exception) {
        }
    }

    fun handleProviderUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        pendingResult: BroadcastReceiver.PendingResult,
        type: WidgetType
    ) {
        val appContext = context.applicationContext
        ioScope.launch {
            try {
                val dao = AppDatabase.getDatabase(appContext, ioScope).expenseDao()
                val isDark = isDarkTheme(appContext)
                when (type) {
                    WidgetType.QUICK_ADD -> {
                        val (todayStart, todayEnd) = getTodayRange()
                        val todayExpense = dao.getExpenseSumInRange(todayStart, todayEnd)
                        for (id in appWidgetIds) {
                            appWidgetManager.updateAppWidget(
                                id,
                                buildQuickAddViews(appContext, id, isDark, todayExpense)
                            )
                        }
                    }
                    WidgetType.BUDGET_PULSE -> {
                        val (monthStart, monthEnd) = getCurrentMonthRange()
                        val totalIncome = dao.getTotalIncomeSync()
                        val totalExpense = dao.getTotalExpenseSync()
                        val monthlyIncome = dao.getIncomeSumInRange(monthStart, monthEnd)
                        val monthlyExpense = dao.getExpenseSumInRange(monthStart, monthEnd)
                        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        val monthlyBudget = prefs.getFloat(KEY_MONTHLY_BUDGET, 0f)
                        for (id in appWidgetIds) {
                            appWidgetManager.updateAppWidget(
                                id,
                                buildBudgetPulseViews(
                                    appContext,
                                    id,
                                    isDark,
                                    totalIncome - totalExpense,
                                    monthlyIncome,
                                    monthlyExpense,
                                    monthlyBudget
                                )
                            )
                        }
                    }
                    WidgetType.RECENT_ACTIVITY -> {
                        val recentList = dao.getRecentExpensesSync(3)
                        for (id in appWidgetIds) {
                            appWidgetManager.updateAppWidget(
                                id,
                                buildRecentActivityViews(appContext, id, isDark, recentList)
                            )
                        }
                    }
                }
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun buildQuickAddViews(
        context: Context,
        appWidgetId: Int,
        isDark: Boolean,
        todayExpense: Double
    ): RemoteViews {
        val layoutId = if (isDark) R.layout.widget_quick_add_dark else R.layout.widget_quick_add_light
        val views = RemoteViews(context.packageName, layoutId)

        views.setTextViewText(R.id.tv_quick_label, "TODAY SPENT")
        val amountText = if (todayExpense > 0.0) {
            CurrencyHelper.format(context, todayExpense)
        } else {
            "SpendWise"
        }
        views.setTextViewText(R.id.tv_quick_amount, amountText)

        views.setOnClickPendingIntent(
            R.id.widget_root,
            createLaunchPendingIntent(context, SLOT_QUICK_HOME, appWidgetId)
        )
        views.setOnClickPendingIntent(
            R.id.btn_add_expense,
            createLaunchPendingIntent(context, SLOT_QUICK_EXPENSE, appWidgetId) {
                putExtra(EXTRA_OPEN_ADD_EXPENSE, true)
                putExtra(EXTRA_PRESET_IS_INCOME, false)
            }
        )
        views.setOnClickPendingIntent(
            R.id.btn_add_income,
            createLaunchPendingIntent(context, SLOT_QUICK_INCOME, appWidgetId) {
                putExtra(EXTRA_OPEN_ADD_EXPENSE, true)
                putExtra(EXTRA_PRESET_IS_INCOME, true)
            }
        )

        return views
    }

    private fun buildBudgetPulseViews(
        context: Context,
        appWidgetId: Int,
        isDark: Boolean,
        netBalance: Double,
        monthlyIncome: Double,
        monthlyExpense: Double,
        monthlyBudget: Float
    ): RemoteViews {
        val layoutId = if (isDark) R.layout.widget_budget_pulse_dark else R.layout.widget_budget_pulse_light
        val views = RemoteViews(context.packageName, layoutId)

        views.setTextViewText(R.id.tv_net_balance, CurrencyHelper.format(context, netBalance))
        views.setTextViewText(R.id.tv_monthly_income, "+${CurrencyHelper.format(context, monthlyIncome)}")
        views.setTextViewText(R.id.tv_monthly_expense, "-${CurrencyHelper.format(context, monthlyExpense)}")

        if (monthlyBudget > 0f) {
            views.setViewVisibility(R.id.layout_budget_active, View.VISIBLE)
            views.setViewVisibility(R.id.tv_budget_empty, View.GONE)

            val budgetText = "Monthly Budget: ${CurrencyHelper.format(context, monthlyExpense)} / ${CurrencyHelper.format(context, monthlyBudget.toDouble())}"
            views.setTextViewText(R.id.tv_budget_status, budgetText)

            val pctFloat = (monthlyExpense / monthlyBudget.toDouble()) * 100.0
            val progress = pctFloat.toInt().coerceIn(0, 100)
            val activeBarId = when {
                pctFloat >= 100.0 -> R.id.progress_budget_danger
                pctFloat >= 80.0 -> R.id.progress_budget_warn
                else -> R.id.progress_budget_ok
            }

            views.setViewVisibility(
                R.id.progress_budget_ok,
                if (activeBarId == R.id.progress_budget_ok) View.VISIBLE else View.GONE
            )
            views.setViewVisibility(
                R.id.progress_budget_warn,
                if (activeBarId == R.id.progress_budget_warn) View.VISIBLE else View.GONE
            )
            views.setViewVisibility(
                R.id.progress_budget_danger,
                if (activeBarId == R.id.progress_budget_danger) View.VISIBLE else View.GONE
            )
            views.setProgressBar(activeBarId, 100, progress, false)
        } else {
            views.setViewVisibility(R.id.layout_budget_active, View.GONE)
            views.setViewVisibility(R.id.tv_budget_empty, View.VISIBLE)
        }

        views.setOnClickPendingIntent(
            R.id.widget_root,
            createLaunchPendingIntent(context, SLOT_PULSE_HOME, appWidgetId)
        )
        views.setOnClickPendingIntent(
            R.id.btn_add,
            createLaunchPendingIntent(context, SLOT_PULSE_ADD, appWidgetId) {
                putExtra(EXTRA_OPEN_ADD_EXPENSE, true)
                putExtra(EXTRA_PRESET_IS_INCOME, false)
            }
        )
        views.setOnClickPendingIntent(
            R.id.layout_budget_section,
            createLaunchPendingIntent(context, SLOT_PULSE_BUDGET, appWidgetId) {
                putExtra(EXTRA_OPEN_BUDGET_SHEET, true)
            }
        )

        return views
    }

    private fun buildRecentActivityViews(
        context: Context,
        appWidgetId: Int,
        isDark: Boolean,
        recentList: List<Expense>
    ): RemoteViews {
        val layoutId = if (isDark) R.layout.widget_recent_activity_dark else R.layout.widget_recent_activity_light
        val views = RemoteViews(context.packageName, layoutId)

        val incomeColor = Color.parseColor(if (isDark) "#30D158" else "#16A34A")
        val expenseColor = Color.parseColor(if (isDark) "#FF453A" else "#DC2626")

        if (recentList.isEmpty()) {
            views.setViewVisibility(R.id.empty_state_text, View.VISIBLE)
            for (row in recentRows) {
                views.setViewVisibility(row.rowId, View.GONE)
            }
        } else {
            views.setViewVisibility(R.id.empty_state_text, View.GONE)
            recentRows.forEachIndexed { index, row ->
                if (index < recentList.size) {
                    val item = recentList[index]
                    views.setViewVisibility(row.rowId, View.VISIBLE)

                    val categoryTitle = item.category.ifBlank {
                        if (item.isIncome) "Income" else "Expense"
                    }
                    val dateLabel = formatRelativeDate(item.date)
                    val subtitle = if (item.note.isNotBlank()) {
                        "${item.note} · $dateLabel"
                    } else {
                        dateLabel
                    }
                    val sign = if (item.isIncome) "+" else "-"
                    val formattedAmount = "$sign${CurrencyHelper.format(context, item.amount)}"

                    views.setTextViewText(row.categoryId, categoryTitle)
                    views.setTextViewText(row.subtitleId, subtitle)
                    views.setTextViewText(row.amountId, formattedAmount)
                    views.setTextColor(row.amountId, if (item.isIncome) incomeColor else expenseColor)
                } else {
                    views.setViewVisibility(row.rowId, View.GONE)
                }
            }
        }

        views.setOnClickPendingIntent(
            R.id.widget_root,
            createLaunchPendingIntent(context, SLOT_RECENT_HOME, appWidgetId)
        )
        views.setOnClickPendingIntent(
            R.id.btn_add,
            createLaunchPendingIntent(context, SLOT_RECENT_ADD, appWidgetId) {
                putExtra(EXTRA_OPEN_ADD_EXPENSE, true)
                putExtra(EXTRA_PRESET_IS_INCOME, false)
            }
        )

        return views
    }

    private fun createLaunchPendingIntent(
        context: Context,
        slot: Int,
        appWidgetId: Int,
        configureIntent: Intent.() -> Unit = {}
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            configureIntent()
        }
        val requestCode = (slot * 100_000) + appWidgetId
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getTodayRange(): Pair<Long, Long> {
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return Pair(start.timeInMillis, end.timeInMillis)
    }

    private fun getCurrentMonthRange(): Pair<Long, Long> {
        val start = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return Pair(start.timeInMillis, end.timeInMillis)
    }

    private fun formatRelativeDate(timestamp: Long): String {
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        return when {
            isSameDay(target, today) -> "Today"
            isSameDay(target, yesterday) -> "Yesterday"
            else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
        }
    }

    private fun isSameDay(a: Calendar, b: Calendar): Boolean {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }
}
