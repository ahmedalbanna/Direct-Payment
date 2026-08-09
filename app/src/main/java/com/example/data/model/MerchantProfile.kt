package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "merchant_profile")
data class MerchantProfile(
    @PrimaryKey val id: Int = 1,
    val merchantName: String = "محلات الباشا للاتصالات والخدمات",
    val phone: String = "777123456",
    val agentCode: String = "POS-9941",
    val balance: Double = 350000.0,
    val securityPin: String = "1234",
    val isLocked: Boolean = false,
    val simulateApiFailures: Boolean = false
)
