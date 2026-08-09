package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.SadadViewModel

@Composable
fun MainScreen(viewModel: SadadViewModel) {
    val merchantProfile by viewModel.merchantProfile.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val isBalanceVisible by viewModel.isBalanceVisible.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()

    val pendingTx by viewModel.pendingTx.collectAsStateWithLifecycle()
    val receiptTx by viewModel.receiptTx.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val inquiryResult by viewModel.inquiryResult.collectAsStateWithLifecycle()
    val isInquiring by viewModel.isInquiring.collectAsStateWithLifecycle()
    val showSplash by viewModel.showSplash.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearToast()
        }
    }

    if (showSplash) {
        SplashScreen(onFinish = { viewModel.completeSplash() })
        return
    }

    if (merchantProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    if (!merchantProfile!!.isOnboardingCompleted) {
        OnboardingScreen(onFinish = { viewModel.completeOnboarding() })
        return
    }

    if (!isLoggedIn) {
        LoginScreen(
            merchantName = merchantProfile?.merchantName ?: "سداد كاش",
            onLoginSuccess = { viewModel.login() }
        )
        return
    }

    if (merchantProfile?.isLocked == true) {
        LockScreen(
            merchantName = merchantProfile?.merchantName ?: "سداد المباشر",
            onUnlock = { pin, onSuccess, onError ->
                viewModel.unlockPos(pin, onSuccess, onError)
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = activeTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    MainTab.DASHBOARD -> DashboardScreen(
                        merchantProfile = merchantProfile,
                        isBalanceVisible = isBalanceVisible,
                        recentTransactions = transactions.take(5),
                        onToggleBalance = { viewModel.toggleBalanceVisibility() },
                        onSelectTab = { viewModel.selectTab(it) },
                        onLockPos = { viewModel.lockPos() },
                        onQuickDeposit = { viewModel.addMerchantDeposit(50000.0) }
                    )

                    MainTab.RECHARGE -> RechargeScreen(
                        onBack = { viewModel.selectTab(MainTab.DASHBOARD) },
                        onSubmitRecharge = { viewModel.setPendingTransaction(it) }
                    )

                    MainTab.BILLS -> BillsScreen(
                        inquiryResult = inquiryResult,
                        isInquiring = isInquiring,
                        onBack = { viewModel.selectTab(MainTab.DASHBOARD) },
                        onInquireBill = { serviceId, accountId -> viewModel.inquireBill(serviceId, accountId) },
                        onClearInquiry = { viewModel.clearInquiry() },
                        onSubmitBillPay = { viewModel.setPendingTransaction(it) }
                    )

                    MainTab.DIGITAL_CARDS -> DigitalCardsScreen(
                        onBack = { viewModel.selectTab(MainTab.DASHBOARD) },
                        onSubmitCardPurchase = { viewModel.setPendingTransaction(it) }
                    )

                    MainTab.HISTORY -> TransactionHistoryScreen(
                        transactions = transactions,
                        onBack = { viewModel.selectTab(MainTab.DASHBOARD) },
                        onViewReceipt = { viewModel.viewReceipt(it) }
                    )

                    MainTab.SETTINGS -> SettingsScreen(
                        merchantProfile = merchantProfile,
                        onBack = { viewModel.selectTab(MainTab.DASHBOARD) },
                        onChangePin = { viewModel.updateSecurityPin(it) },
                        onAddDeposit = { viewModel.addMerchantDeposit(it) },
                        onToggleSimulateFailure = { viewModel.toggleSimulateFailure(it) },
                        onNavigateToProfile = { viewModel.selectTab(MainTab.PROFILE) }
                    )

                    MainTab.PROFILE -> ProfileScreen(
                        profile = merchantProfile!!,
                        onBack = { viewModel.selectTab(MainTab.SETTINGS) },
                        onLogout = { viewModel.logout() }
                    )
                }
            }

            // PIN Confirmation Dialog
            if (pendingTx != null) {
                PinConfirmDialog(
                    pendingTx = pendingTx!!,
                    isProcessing = isProcessing,
                    errorMessage = errorMessage,
                    onConfirmPin = { pin -> viewModel.confirmPendingTransaction(pin) },
                    onDismiss = { viewModel.setPendingTransaction(null) }
                )
            }

            // Receipt Invoice Dialog
            if (receiptTx != null) {
                ReceiptDialog(
                    transaction = receiptTx!!,
                    merchantName = merchantProfile?.merchantName ?: "محلات الباشا للاتصالات",
                    merchantPhone = merchantProfile?.phone ?: "777123456",
                    onDismiss = { viewModel.dismissReceipt() }
                )
            }
        }
    }
}
