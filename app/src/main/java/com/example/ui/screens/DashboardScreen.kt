package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SendAndArchive
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MerchantProfile
import com.example.data.model.TransactionRecord
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryTeal
import com.example.ui.viewmodel.MainTab

data class ActionMenuItem(
    val id: MainTab,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val primaryColor: Color
)

@Composable
fun DashboardScreen(
    merchantProfile: MerchantProfile?,
    isBalanceVisible: Boolean,
    recentTransactions: List<TransactionRecord>,
    onToggleBalance: () -> Unit,
    onSelectTab: (MainTab) -> Unit,
    onLockPos: () -> Unit,
    onQuickDeposit: () -> Unit
) {
    val menuItems = listOf(
        ActionMenuItem(
            id = MainTab.RECHARGE,
            title = "تعبئة رصيد فوري",
            subtitle = "يمن موبايل، U، سبأفون، واي",
            icon = Icons.Default.PhonelinkRing,
            primaryColor = Color(0xFFDC2626)
        ),
        ActionMenuItem(
            id = MainTab.BILLS,
            title = "سداد الفواتير",
            subtitle = "ثابت، ADSL، فورجي، كهرباء، ماء",
            icon = Icons.Default.ReceiptLong,
            primaryColor = Color(0xFF0284C7)
        ),
        ActionMenuItem(
            id = MainTab.DIGITAL_CARDS,
            title = "الكروت والألعاب",
            subtitle = "باجي، فري فاير، جوجل، فورجي",
            icon = Icons.Default.CreditCard,
            primaryColor = Color(0xFFF59E0B)
        ),
        ActionMenuItem(
            id = MainTab.HISTORY,
            title = "كشف حساب لحظي",
            subtitle = "سجل العمليات السابقة والتقارير",
            icon = Icons.Default.History,
            primaryColor = Color(0xFF2563EB)
        ),
        ActionMenuItem(
            id = MainTab.SETTINGS,
            title = "إعدادات الأمان",
            subtitle = "تغيير الـ PIN وإدارة الحساب",
            icon = Icons.Default.Settings,
            primaryColor = Color(0xFF4B5563)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Merchant Header Top
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = merchantProfile?.merchantName ?: "محلات الباشا للخدمات",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "رمز الوكيل: ${merchantProfile?.agentCode ?: "POS-9941"} • ${merchantProfile?.phone ?: ""}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = onLockPos,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("lock_pos_top_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "قفل POS",
                    tint = PrimaryBlue
                )
            }
        }

        // Prominent Wallet Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("wallet_balance_card"),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(DarkNavy, Color(0xFF1E3A8A), Color(0xFF0F172A))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "رصيد التاجر المتوفر حالياً",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        IconButton(onClick = onToggleBalance) {
                            Icon(
                                imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "إخفاء/إظهار الرصيد",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (isBalanceVisible) {
                            Text(
                                text = "${merchantProfile?.balance ?: 0.0}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "• • • • • • • •",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                        }

                        Text(
                            text = "ريال يمني",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentGold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onQuickDeposit,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCard,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تغذية / إيداع رصيد", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section Title: Quick Actions
        Text(
            text = "الخدمات الرئيسية (POS)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Services Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clickable { onSelectTab(item.id) }
                        .testTag("menu_card_${item.id.name.lowercase()}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(item.primaryColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = item.primaryColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = item.subtitle,
                                fontSize = 11.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
