package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val stock: Int,
    val costPrice: Double,
    val sellPrice: Double,
    val sku: String = ""
)
