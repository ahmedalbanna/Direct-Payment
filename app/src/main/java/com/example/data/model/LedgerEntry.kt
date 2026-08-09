package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LedgerType {
    DEBIT,  // دين جديد عليه (Customer owes)
    CREDIT  // سداد منه (Customer paid back)
}

@Entity(tableName = "ledger_entries")
data class LedgerEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val amount: Double,
    val type: LedgerType,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val balanceAfter: Double
)
