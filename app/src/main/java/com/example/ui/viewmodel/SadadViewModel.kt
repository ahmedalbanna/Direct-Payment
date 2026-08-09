package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.MerchantProfile
import com.example.data.model.ServiceType
import com.example.data.model.TransactionRecord
import com.example.data.repository.BillInquiryResult
import com.example.data.repository.SadadRepository
import com.example.data.repository.TransactionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    DASHBOARD,
    RECHARGE,
    BILLS,
    DIGITAL_CARDS,
    HISTORY,
    SETTINGS
}

data class PendingTransaction(
    val serviceType: ServiceType,
    val serviceName: String,
    val providerCategory: String,
    val targetNumber: String,
    val amount: Double,
    val cost: Double = amount * 0.99,
    val subscriberName: String? = null,
    val digitalCode: String? = null,
    val notes: String? = null
)

class SadadViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SadadRepository

    val merchantProfile: StateFlow<MerchantProfile?>
    val transactions: StateFlow<List<TransactionRecord>>

    private val _activeTab = MutableStateFlow(MainTab.DASHBOARD)
    val activeTab: StateFlow<MainTab> = _activeTab.asStateFlow()

    private val _isBalanceVisible = MutableStateFlow(true)
    val isBalanceVisible: StateFlow<Boolean> = _isBalanceVisible.asStateFlow()

    private val _pendingTx = MutableStateFlow<PendingTransaction?>(null)
    val pendingTx: StateFlow<PendingTransaction?> = _pendingTx.asStateFlow()

    private val _receiptTx = MutableStateFlow<TransactionRecord?>(null)
    val receiptTx: StateFlow<TransactionRecord?> = _receiptTx.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Bill Inquiry State
    private val _inquiryResult = MutableStateFlow<BillInquiryResult?>(null)
    val inquiryResult: StateFlow<BillInquiryResult?> = _inquiryResult.asStateFlow()

    private val _isInquiring = MutableStateFlow(false)
    val isInquiring: StateFlow<Boolean> = _isInquiring.asStateFlow()

    // Customer Ledger State
    init {
        val db = AppDatabase.getDatabase(application)
        repository = SadadRepository(db.merchantDao(), db.transactionDao())

        merchantProfile = repository.merchantProfile.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), null
        )
        transactions = repository.allTransactions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        viewModelScope.launch {
            repository.ensureInitialMerchantProfile()
        }
    }

    fun selectTab(tab: MainTab) {
        _activeTab.value = tab
    }

    fun toggleBalanceVisibility() {
        _isBalanceVisible.value = !_isBalanceVisible.value
    }

    fun setPendingTransaction(tx: PendingTransaction?) {
        _errorMessage.value = null
        _pendingTx.value = tx
    }

    fun dismissReceipt() {
        _receiptTx.value = null
    }

    fun viewReceipt(tx: TransactionRecord) {
        _receiptTx.value = tx
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // Direct Bill Inquiry
    fun inquireBill(serviceId: String, accountId: String) {
        if (accountId.isBlank()) {
            _toastMessage.value = "يرجى إدخال رقم الحساب أو الهاتف للاستعلام"
            return
        }
        viewModelScope.launch {
            _isInquiring.value = true
            _inquiryResult.value = null
            try {
                val res = repository.inquireBill(serviceId, accountId)
                _inquiryResult.value = res
            } catch (e: Exception) {
                _toastMessage.value = "فشل في الاستعلام عن الحساب"
            } finally {
                _isInquiring.value = false
            }
        }
    }

    fun clearInquiry() {
        _inquiryResult.value = null
    }

    // Execute Payment with PIN
    fun confirmPendingTransaction(pin: String) {
        val tx = _pendingTx.value ?: return
        viewModelScope.launch {
            _isProcessing.value = true
            _errorMessage.value = null

            val result = repository.executeTransaction(
                serviceType = tx.serviceType,
                serviceName = tx.serviceName,
                providerCategory = tx.providerCategory,
                targetNumber = tx.targetNumber,
                amount = tx.amount,
                cost = tx.cost,
                enteredPin = pin,
                subscriberName = tx.subscriberName,
                digitalCode = tx.digitalCode,
                notes = tx.notes
            )

            _isProcessing.value = false

            when (result) {
                is TransactionResult.Success -> {
                    _pendingTx.value = null
                    _receiptTx.value = result.transaction
                    _toastMessage.value = "تمت العملية بنجاح! تم الخصم وطباعة السند."
                }
                is TransactionResult.RefundedFailure -> {
                    _pendingTx.value = null
                    _receiptTx.value = result.transaction
                    _toastMessage.value = "فشلت العملية - " + result.errorMessage
                }
                is TransactionResult.InvalidPin -> {
                    _errorMessage.value = result.message
                }
                is TransactionResult.InsufficientBalance -> {
                    _errorMessage.value = "رصيدك غير كافٍ. المتوفر: ${result.currentBalance} ريال، المطلوب: ${result.required} ريال"
                }
            }
        }
    }

    // POS Lock Screen Actions
    fun lockPos() {
        viewModelScope.launch {
            repository.setLockedState(true)
        }
    }

    fun unlockPos(pin: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val valid = repository.verifyPin(pin)
            if (valid) {
                repository.setLockedState(false)
                onSuccess()
            } else {
                onError()
            }
        }
    }

    fun toggleSimulateFailure(enable: Boolean) {
        viewModelScope.launch {
            repository.setSimulateFailure(enable)
            _toastMessage.value = if (enable) "تم تفعيل محاكاة فشل الشبكة والاسترجاع الآلي" else "تم إيقاف وضع الفشل"
        }
    }

    fun addMerchantDeposit(amount: Double) {
        viewModelScope.launch {
            repository.addMerchantBalance(amount)
            _toastMessage.value = "تم إيداع $amount ريال بنجاح إلى رصيد التاجر!"
        }
    }

    fun updateSecurityPin(newPin: String) {
        if (newPin.length < 4) {
            _toastMessage.value = "رمز PIN يجب أن يتكون من 4 أرقام على الأقل"
            return
        }
        viewModelScope.launch {
            repository.updateSecurityPin(newPin)
            _toastMessage.value = "تم تغيير رمز PIN الأمني بنجاح!"
        }
    }
}
