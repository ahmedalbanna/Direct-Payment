package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionStatus {
    SUCCESS, PENDING, FAILED
}

enum class ServiceType {
    RECHARGE, BILL, DIGITAL_CARD, TRANSFER, DEBT_SETTLEMENT
}

@Entity(tableName = "transaction_records")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionCode: String,
    val serviceType: ServiceType,
    val serviceName: String, // e.g. "يمن موبايل - رصيد", "يوو U - باقة", "إنترنت ADSL", "كرت باجي PUBG 60 UC"
    val providerCategory: String, // e.g. "Yemen Mobile", "YOU", "Sabafon", "Y", "Landline", "PUBG"
    val targetNumber: String, // e.g. Phone or Account ID or Customer Name
    val amount: Double,
    val cost: Double = amount * 0.99, // merchant discount
    val status: TransactionStatus,
    val balanceBefore: Double,
    val balanceAfter: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val subscriberName: String? = null, // e.g. for bill inquiry result
    val digitalCode: String? = null, // for digital cards PIN/Code
    val notes: String? = null
)
