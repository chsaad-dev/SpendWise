package com.example.spendwise.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    suspend fun getAllExpensesSync(): List<Expense>

    @Query("SELECT * FROM expenses WHERE note LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchExpenses(query: String): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getExpensesByDateRangeSync(startDate: Long, endDate: Long): List<Expense>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByCategoryAndDateRange(category: String, startDate: Long, endDate: Long): LiveData<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE isIncome = 0")
    fun getTotalExpense(): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE isIncome = 1")
    fun getTotalIncome(): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE isIncome = 0 AND date BETWEEN :startDate AND :endDate")
    fun getMonthlyExpense(startDate: Long, endDate: Long): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE isIncome = 1 AND date BETWEEN :startDate AND :endDate")
    fun getMonthlyIncome(startDate: Long, endDate: Long): LiveData<Double?>

    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE isIncome = 0 AND date BETWEEN :startDate AND :endDate GROUP BY category")
    suspend fun getCategoryTotals(startDate: Long, endDate: Long): List<CategoryTotal>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE isIncome = 0 AND date >= :startDate AND date <= :endDate")
    suspend fun getExpenseSumInRange(startDate: Long, endDate: Long): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE isIncome = 1 AND date >= :startDate AND date <= :endDate")
    suspend fun getIncomeSumInRange(startDate: Long, endDate: Long): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE isIncome = 0")
    suspend fun getTotalExpenseSync(): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE isIncome = 1")
    suspend fun getTotalIncomeSync(): Double

    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC LIMIT :limit")
    suspend fun getRecentExpensesSync(limit: Int): List<Expense>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Int): Expense?
}

data class CategoryTotal(
    val category: String,
    val total: Double
)
