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

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpense(): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getMonthlyExpense(startDate: Long, endDate: Long): LiveData<Double?>

    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE date BETWEEN :startDate AND :endDate GROUP BY category")
    suspend fun getCategoryTotals(startDate: Long, endDate: Long): List<CategoryTotal>

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
