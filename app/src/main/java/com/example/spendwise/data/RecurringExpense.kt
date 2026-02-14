package com.example.spendwise.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_expenses")
data class RecurringExpense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val category: String,
    val note: String,
    val recurrenceType: String,
    val lastProcessedDate: Long,
    val isActive: Boolean = true
)
