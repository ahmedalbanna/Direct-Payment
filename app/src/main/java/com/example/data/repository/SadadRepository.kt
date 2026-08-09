package com.example.data.repository

import com.example.data.db.MerchantDao
import com.example.data.db.TransactionDao
import com.example.data.model.MerchantProfile
import com.example.data.model.ServiceType
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.util.UUID

sealed class TransactionResult {
    data class Success(val transaction: TransactionRecord) : TransactionResult()
    data class RefundedFailure(val transaction: TransactionRecord, val errorMessage: String) : TransactionResult()
    data class InvalidPin(val message: String = "رمز PIN غير صحيح") : TransactionResult()
    data class InsufficientBalance(val currentBalance: Double, val required: Double) : TransactionResult()
}

data class BillInquiryResult(
    val accountId: String,
    val subscriberName: String,
    val dueAmountYer: Double,
    val serviceCategory: String,
    val status: String = "SUCCESS"
)

class SadadRepository(
    private val merchantDao: MerchantDao,
    private val transactionDao: TransactionDao
) {

    val merchantProfile: Flow<MerchantProfile?> = merchantDao.getMerchantProfile()
    val allTransactions: Flow<List<TransactionRecord>> = transactionDao.getAllTransactions()

    suspend fun ensureInitialMerchantProfile() {
        var profile = merchantDao.getMerchantSync()
        if (profile == null) {
            profile = MerchantProfile(
                merchantName = "محلات الباشا للخدمات والاتصالات",
                phone = "777123456",
                agentCode = "POS-9941",
                balance = 350000.0,
                securityPin = "1234",
                isLocked = false
            )
            merchantDao.insertOrUpdateProfile(profile)

            // Seed initial transactions for demonstration
            val initialBalance = 350000.0
            val t1 = TransactionRecord(
                transactionCode = "SAD-982141",
                serviceType = ServiceType.RECHARGE,
                serviceName = "تعبئة رصيد يمن موبايل",
                providerCategory = "Yemen Mobile",
                targetNumber = "771234567",
                amount = 2000.0,
                cost = 1980.0,
                status = TransactionStatus.SUCCESS,
                balanceBefore = 351980.0,
                balanceAfter = 350000.0,
                timestamp = System.currentTimeMillis() - 3600000
            )
            transactionDao.insertTransaction(t1)
        }
    }

    suspend fun updateSecurityPin(newPin: String) {
        val current = merchantDao.getMerchantSync() ?: return
        merchantDao.insertOrUpdateProfile(current.copy(securityPin = newPin))
    }

    suspend fun setLockedState(locked: Boolean) {
        merchantDao.setLockedState(locked)
    }

    suspend fun setSimulateFailure(simulate: Boolean) {
        merchantDao.setSimulateFailure(simulate)
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        val current = merchantDao.getMerchantSync() ?: return
        merchantDao.insertOrUpdateProfile(current.copy(isOnboardingCompleted = completed))
    }

    suspend fun addMerchantBalance(amount: Double) {
        val profile = merchantDao.getMerchantSync() ?: return
        val updated = profile.balance + amount
        merchantDao.updateBalance(updated)
    }

    suspend fun verifyPin(enteredPin: String): Boolean {
        val profile = merchantDao.getMerchantSync() ?: return false
        return profile.securityPin == enteredPin
    }

    // Direct Bill Inquiry Simulation
    suspend fun inquireBill(serviceId: String, accountId: String): BillInquiryResult {
        delay(800) // Simulate fast network API response
        val names = listOf("م. محمد حسين الشامي", "عبدالله علي باوزير", "خالد سالم العولقي", "عمر عبدالكريم القاضي", "شركة النهضة للتجارة")
        val sampleName = names[(accountId.hashCode() and 0x7FFFFFFF) % names.size]
        val dueAmount = when (serviceId) {
            "landline" -> 1500.0 + ((accountId.hashCode() and 0x7F) * 10)
            "adsl" -> 3000.0 + ((accountId.hashCode() and 0x3F) * 100)
            "yemen_4g" -> 4800.0
            "electricity" -> 8500.0 + ((accountId.hashCode() and 0xFF) * 20)
            "water" -> 2200.0
            else -> 2000.0
        }
        return BillInquiryResult(
            accountId = accountId,
            subscriberName = sampleName,
            dueAmountYer = dueAmount,
            serviceCategory = serviceId
        )
    }

    // Core Financial Execution with Security PIN verification & Auto-Refund
    suspend fun executeTransaction(
        serviceType: ServiceType,
        serviceName: String,
        providerCategory: String,
        targetNumber: String,
        amount: Double,
        cost: Double = amount * 0.99,
        enteredPin: String,
        subscriberName: String? = null,
        digitalCode: String? = null,
        notes: String? = null
    ): TransactionResult {
        val profile = merchantDao.getMerchantSync() ?: return TransactionResult.InvalidPin("الملف غير متوفر")

        // 1. PIN Verification
        if (profile.securityPin != enteredPin) {
            return TransactionResult.InvalidPin("رمز PIN الخاص بك غير صحيح. يرجى المحاولة مجدداً.")
        }

        // 2. Balance Check
        val currentBalance = profile.balance
        if (currentBalance < cost) {
            return TransactionResult.InsufficientBalance(currentBalance, cost)
        }

        val balanceBefore = currentBalance
        val balanceDeducted = currentBalance - cost
        val code = "SAD-" + (100000..999999).random()

        // Temporarily deduct balance
        merchantDao.updateBalance(balanceDeducted)

        // Simulate network API request processing
        delay(1200)

        // 3. Check if simulated API failure is turned on
        if (profile.simulateApiFailures) {
            // Immediate Auto-Refund Logic
            val refundedBalance = balanceDeducted + cost
            merchantDao.updateBalance(refundedBalance)

            val failedTx = TransactionRecord(
                transactionCode = code,
                serviceType = serviceType,
                serviceName = serviceName,
                providerCategory = providerCategory,
                targetNumber = targetNumber,
                amount = amount,
                cost = cost,
                status = TransactionStatus.FAILED,
                balanceBefore = balanceBefore,
                balanceAfter = refundedBalance,
                subscriberName = subscriberName,
                digitalCode = digitalCode,
                notes = "فشل في المزود - تم استرجاع المبلغ آلياً لحساب التاجر"
            )
            transactionDao.insertTransaction(failedTx)

            return TransactionResult.RefundedFailure(
                transaction = failedTx,
                errorMessage = "خطأ في استجابة خادم المزود. تم إلغاء العملية واسترجاع المبلغ $cost ريال تلقائياً لحسابك!"
            )
        }

        // 4. Success Execution
        val successTx = TransactionRecord(
            transactionCode = code,
            serviceType = serviceType,
            serviceName = serviceName,
            providerCategory = providerCategory,
            targetNumber = targetNumber,
            amount = amount,
            cost = cost,
            status = TransactionStatus.SUCCESS,
            balanceBefore = balanceBefore,
            balanceAfter = balanceDeducted,
            subscriberName = subscriberName,
            digitalCode = digitalCode,
            notes = notes
        )

        val txId = transactionDao.insertTransaction(successTx)
        val savedTx = successTx.copy(id = txId)

        return TransactionResult.Success(savedTx)
    }
}
