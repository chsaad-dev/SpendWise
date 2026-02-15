package com.example.spendwise.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "split_groups")
data class SplitGroup(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expenseId: Int,
    val totalAmount: Double,
    val description: String,
    val createdDate: Long = System.currentTimeMillis()
)
