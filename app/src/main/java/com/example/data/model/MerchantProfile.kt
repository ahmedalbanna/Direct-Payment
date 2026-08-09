package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "merchant_profile")
data class MerchantProfile(
    @PrimaryKey val id: Int = 1,
    val merchantName: String = "محلات عقبات للخدمات والاتصالات",
    val phone: String = "783131819",
    val agentCode: String = "131819",
    val balance: Double = 350000.0,
    val securityPin: String = "1234",
    val isLocked: Boolean = false,
    val isOnboardingCompleted: Boolean = false,
    val simulateApiFailures: Boolean = false
)
