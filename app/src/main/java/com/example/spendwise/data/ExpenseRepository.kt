package com.example.spendwise.data

import androidx.lifecycle.LiveData

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val recurringExpenseDao: RecurringExpenseDao,
    private val savingsGoalDao: SavingsGoalDao,
    private val splitDao: SplitDao
) {

    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()
    val totalExpense: LiveData<Double?> = expenseDao.getTotalExpense()
    val allRecurringExpenses: LiveData<List<RecurringExpense>> = recurringExpenseDao.getAllActive()
    val allSavingsGoals: LiveData<List<SavingsGoal>> = savingsGoalDao.getAllGoals()
    val allSplitGroups: LiveData<List<SplitGroup>> = splitDao.getAllGroups()

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

    // Savings goals
    suspend fun insertGoal(goal: SavingsGoal) {
        savingsGoalDao.insert(goal)
    }

    suspend fun updateGoal(goal: SavingsGoal) {
        savingsGoalDao.update(goal)
    }

    suspend fun deleteGoal(goal: SavingsGoal) {
        savingsGoalDao.delete(goal)
    }

    // Split expenses
    suspend fun insertSplitGroup(group: SplitGroup): Long {
        return splitDao.insertGroup(group)
    }

    suspend fun updateSplitGroup(group: SplitGroup) {
        splitDao.updateGroup(group)
    }

    suspend fun deleteSplitGroup(group: SplitGroup) {
        splitDao.deleteGroup(group)
    }

    suspend fun getSplitGroupByExpenseId(expenseId: Int): SplitGroup? {
        return splitDao.getGroupByExpenseId(expenseId)
    }

    suspend fun getSplitGroupById(groupId: Int): SplitGroup? {
        return splitDao.getGroupById(groupId)
    }

    fun getSplitMembersByGroupId(groupId: Int): LiveData<List<SplitMember>> {
        return splitDao.getMembersByGroupId(groupId)
    }

    suspend fun getSplitMembersByGroupIdSync(groupId: Int): List<SplitMember> {
        return splitDao.getMembersByGroupIdSync(groupId)
    }

    suspend fun insertSplitMember(member: SplitMember) {
        splitDao.insertMember(member)
    }

    suspend fun updateSplitMember(member: SplitMember) {
        splitDao.updateMember(member)
    }

    suspend fun deleteSplitMember(member: SplitMember) {
        splitDao.deleteMember(member)
    }

    suspend fun deleteAllMembersForGroup(groupId: Int) {
        splitDao.deleteAllMembersForGroup(groupId)
    }
}
