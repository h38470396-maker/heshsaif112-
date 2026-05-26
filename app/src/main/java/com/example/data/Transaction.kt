package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "INCOME" or "EXPENSE"
    val amount: Double,
    val description: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val productId: Int? = null // optional link to product sold or bought
)
