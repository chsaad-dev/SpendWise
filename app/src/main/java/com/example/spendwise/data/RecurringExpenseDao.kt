package com.example.spendwise.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecurringExpenseDao {
    @Query("SELECT * FROM recurring_expenses WHERE isActive = 1 ORDER BY id DESC")
    fun getAllActive(): LiveData<List<RecurringExpense>>

    @Query("SELECT * FROM recurring_expenses WHERE isActive = 1")
    suspend fun getAllActiveSync(): List<RecurringExpense>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recurringExpense: RecurringExpense)

    @Update
    suspend fun update(recurringExpense: RecurringExpense)

    @Delete
    suspend fun delete(recurringExpense: RecurringExpense)

    @Query("DELETE FROM recurring_expenses")
    suspend fun deleteAll()
}
