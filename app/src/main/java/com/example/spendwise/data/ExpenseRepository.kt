package com.example.spendwise.data

import androidx.lifecycle.LiveData

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val recurringExpenseDao: RecurringExpenseDao
) {

    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()
    val totalExpense: LiveData<Double?> = expenseDao.getTotalExpense()
    val allRecurringExpenses: LiveData<List<RecurringExpense>> = recurringExpenseDao.getAllActive()

    fun getMonthlyExpense(start: Long, end: Long): LiveData<Double?> {
        return expenseDao.getMonthlyExpense(start, end)
    }

    fun getExpensesByDateRange(start: Long, end: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesByDateRange(start, end)
    }

    fun getExpensesByCategory(category: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesByCategory(category)
    }

    fun getExpensesByCategoryAndDateRange(category: String, start: Long, end: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesByCategoryAndDateRange(category, start, end)
    }

    suspend fun getCategoryTotals(start: Long, end: Long): List<CategoryTotal> {
        return expenseDao.getCategoryTotals(start, end)
    }

    fun searchExpenses(query: String): LiveData<List<Expense>> {
        return expenseDao.searchExpenses(query)
    }

    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun deleteAllExpenses() {
        expenseDao.deleteAllExpenses()
    }

    suspend fun getExpenseById(id: Int): Expense? {
        return expenseDao.getExpenseById(id)
    }

    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    suspend fun deleteAllCategories() {
        categoryDao.deleteAllCategories()
    }

    // Recurring expenses
    suspend fun insertRecurring(r: RecurringExpense) {
        recurringExpenseDao.insert(r)
    }

    suspend fun updateRecurring(r: RecurringExpense) {
        recurringExpenseDao.update(r)
    }

    suspend fun deleteRecurring(r: RecurringExpense) {
        recurringExpenseDao.delete(r)
    }

    suspend fun getAllActiveRecurringSync(): List<RecurringExpense> {
        return recurringExpenseDao.getAllActiveSync()
    }
}
