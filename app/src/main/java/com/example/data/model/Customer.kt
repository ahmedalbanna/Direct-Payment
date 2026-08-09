package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val totalDebt: Double = 0.0, // Positive = Customer owes money (عليـه), Negative = Customer has credit (لـه)
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
