package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "annual_targets")
data class AnnualTarget(
    @PrimaryKey val id: Int = 1, // Single active annual target config
    val year: Int = 2026,
    val targetAmount: Double = 50000.0,
    val description: String = "الهدف المالي السنوي"
)
