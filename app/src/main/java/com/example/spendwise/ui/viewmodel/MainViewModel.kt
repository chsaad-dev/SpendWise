package com.example.spendwise.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.spendwise.data.AppDatabase
import com.example.spendwise.data.Category
import com.example.spendwise.data.CategoryTotal
import com.example.spendwise.data.Expense
import com.example.spendwise.data.ExpenseRepository
import com.example.spendwise.data.RecurringExpense
import com.example.spendwise.data.SavingsGoal
import com.example.spendwise.data.SplitGroup
import com.example.spendwise.data.SplitMember
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository
    val allExpenses: LiveData<List<Expense>>
    val allCategories: LiveData<List<Category>>
    val totalExpense: LiveData<Double?>
    val allRecurringExpenses: LiveData<List<RecurringExpense>>
    val allSavingsGoals: LiveData<List<SavingsGoal>>
    val allSplitGroups: LiveData<List<SplitGroup>>

    private val _filterDateRange = MutableLiveData<Pair<Long, Long>>()
    val filterDateRange: LiveData<Pair<Long, Long>> = _filterDateRange

    private val _selectedCategory = MutableLiveData<String?>(null)
    val selectedCategory: LiveData<String?> = _selectedCategory

    private val _searchQuery = MutableLiveData<String?>(null)

    val filteredExpenses = MediatorLiveData<List<Expense>>()

    private val _categoryTotals = MutableLiveData<List<CategoryTotal>>()
    val categoryTotals: LiveData<List<CategoryTotal>> = _categoryTotals

    private val _monthlyTotal = MutableLiveData<Double>(0.0)
    val monthlyTotal: LiveData<Double> = _monthlyTotal

    val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
        currency = Currency.getInstance(Locale.getDefault())
    }

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        val expenseDao = db.expenseDao()
        val categoryDao = db.categoryDao()
        val recurringDao = db.recurringExpenseDao()
        val savingsGoalDao = db.savingsGoalDao()
        val splitDao = db.splitDao()
        repository = ExpenseRepository(expenseDao, categoryDao, recurringDao, savingsGoalDao, splitDao)
        allExpenses = repository.allExpenses
        allCategories = repository.allCategories
        totalExpense = repository.totalExpense
        allRecurringExpenses = repository.allRecurringExpenses
        allSavingsGoals = repository.allSavingsGoals
        allSplitGroups = repository.allSplitGroups

        setMonthFilter(Calendar.getInstance())

        filteredExpenses.addSource(allExpenses) { applyFilters() }
        filteredExpenses.addSource(_filterDateRange) { applyFilters() }
        filteredExpenses.addSource(_selectedCategory) { applyFilters() }
        filteredExpenses.addSource(_searchQuery) { applyFilters() }
        processRecurringExpenses()
    }

    private fun applyFilters() {
        val expenses = allExpenses.value ?: emptyList()
        val dateRange = _filterDateRange.value
        val category = _selectedCategory.value
        val query = _searchQuery.value

        var result = expenses

        if (dateRange != null) {
            result = result.filter { it.date in dateRange.first..dateRange.second }
        }

        if (!category.isNullOrEmpty()) {
            result = result.filter { it.category == category }
        }

        if (!query.isNullOrBlank()) {
            val q = query.lowercase()
            result = result.filter {
                it.note.lowercase().contains(q) || it.category.lowercase().contains(q)
            }
        }

        filteredExpenses.value = result
        _monthlyTotal.value = result.sumOf { it.amount }
        if (dateRange != null) {
            loadCategoryTotals(dateRange.first, dateRange.second)
        }
    }

    private fun loadCategoryTotals(start: Long, end: Long) {
        viewModelScope.launch {
            _categoryTotals.value = repository.getCategoryTotals(start, end)
        }
    }

    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }

    fun setSearchQuery(query: String?) {
        _searchQuery.value = query
    }

    fun insertExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.updateExpense(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.deleteExpense(expense)
    }

    fun insertCategory(category: Category) = viewModelScope.launch {
        repository.insertCategory(category)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        repository.deleteCategory(category)
    }

    fun clearAllData() = viewModelScope.launch {
        repository.deleteAllExpenses()
    }

    fun clearAllCategories() = viewModelScope.launch {
        repository.deleteAllCategories()
    }

    fun getExpenseById(id: Int, callback: (Expense?) -> Unit) {
        viewModelScope.launch {
            callback(repository.getExpenseById(id))
        }
    }

    fun insertRecurring(r: RecurringExpense) = viewModelScope.launch {
        repository.insertRecurring(r)
    }

    fun deleteRecurring(r: RecurringExpense) = viewModelScope.launch {
        repository.deleteRecurring(r)
    }

    private fun processRecurringExpenses() {
        viewModelScope.launch {
            val activeRecurrings = repository.getAllActiveRecurringSync()
            val now = System.currentTimeMillis()

            for (r in activeRecurrings) {
                val lastCal = Calendar.getInstance().apply { timeInMillis = r.lastProcessedDate }
                val nowCal = Calendar.getInstance()

                val shouldGenerate = when (r.recurrenceType) {
                    "DAILY" -> {
                        lastCal.get(Calendar.DAY_OF_YEAR) != nowCal.get(Calendar.DAY_OF_YEAR) ||
                        lastCal.get(Calendar.YEAR) != nowCal.get(Calendar.YEAR)
                    }
                    "WEEKLY" -> {
                        lastCal.get(Calendar.WEEK_OF_YEAR) != nowCal.get(Calendar.WEEK_OF_YEAR) ||
                        lastCal.get(Calendar.YEAR) != nowCal.get(Calendar.YEAR)
                    }
                    "MONTHLY" -> {
                        lastCal.get(Calendar.MONTH) != nowCal.get(Calendar.MONTH) ||
                        lastCal.get(Calendar.YEAR) != nowCal.get(Calendar.YEAR)
                    }
                    else -> false
                }

                if (shouldGenerate) {
                    repository.insertExpense(
                        Expense(
                            amount = r.amount,
                            category = r.category,
                            date = now,
                            note = "🔄 ${r.note}"
                        )
                    )
                    repository.updateRecurring(r.copy(lastProcessedDate = now))
                }
            }
        }
    }

    fun setMonthFilter(calendar: Calendar) {
        val start = calendar.clone() as Calendar
        start.set(Calendar.DAY_OF_MONTH, 1)
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)

        val end = calendar.clone() as Calendar
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH))
        end.set(Calendar.HOUR_OF_DAY, 23)
        end.set(Calendar.MINUTE, 59)
        end.set(Calendar.SECOND, 59)
        end.set(Calendar.MILLISECOND, 999)

        _filterDateRange.value = Pair(start.timeInMillis, end.timeInMillis)
    }

    fun setDateRangeFilter(start: Long, end: Long) {
        _filterDateRange.value = Pair(start, end)
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun getMonthlyExpense(start: Long, end: Long): LiveData<Double?> {
        return repository.getMonthlyExpense(start, end)
    }

    fun getBudgetLimit(): Float {
        val prefs = getApplication<android.app.Application>()
            .getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        return prefs.getFloat("monthly_budget", 0f)
    }

    // ── Savings Goals ──
    fun insertGoal(goal: SavingsGoal) = viewModelScope.launch {
        repository.insertGoal(goal)
    }

    fun updateGoal(goal: SavingsGoal) = viewModelScope.launch {
        repository.updateGoal(goal)
    }

    fun deleteGoal(goal: SavingsGoal) = viewModelScope.launch {
        repository.deleteGoal(goal)
    }

    // ── Split Expenses ──
    fun insertSplitGroup(group: SplitGroup, callback: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertSplitGroup(group)
            callback(id)
        }
    }

    fun deleteSplitGroup(group: SplitGroup) = viewModelScope.launch {
        repository.deleteAllMembersForGroup(group.id)
        repository.deleteSplitGroup(group)
    }

    fun getSplitGroupByExpenseId(expenseId: Int, callback: (SplitGroup?) -> Unit) {
        viewModelScope.launch {
            callback(repository.getSplitGroupByExpenseId(expenseId))
        }
    }

    fun getSplitMembersByGroupId(groupId: Int): LiveData<List<SplitMember>> {
        return repository.getSplitMembersByGroupId(groupId)
    }

    fun insertSplitMember(member: SplitMember) = viewModelScope.launch {
        repository.insertSplitMember(member)
    }

    fun updateSplitMember(member: SplitMember) = viewModelScope.launch {
        repository.updateSplitMember(member)
    }

    fun deleteSplitMember(member: SplitMember) = viewModelScope.launch {
        repository.deleteSplitMember(member)
    }

    // ── Duplicate Expense ──
    fun duplicateExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(
            expense.copy(
                id = 0,
                date = System.currentTimeMillis()
            )
        )
    }
}
