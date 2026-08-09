package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionHistoryScreen(
    transactions: List<TransactionRecord>,
    onBack: () -> Unit,
    onViewReceipt: (TransactionRecord) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<TransactionStatus?>(null) }

    val dateFormat = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar"))

    val filteredList = remember(searchQuery, selectedStatusFilter, transactions) {
        transactions.filter { tx ->
            val matchesQuery = searchQuery.isBlank() ||
                    tx.targetNumber.contains(searchQuery) ||
                    tx.transactionCode.contains(searchQuery, ignoreCase = true) ||
                    tx.serviceName.contains(searchQuery)

            val matchesStatus = selectedStatusFilter == null || tx.status == selectedStatusFilter
            matchesQuery && matchesStatus
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("transaction_history_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Top
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "رجوع")
            }
            Text(
                text = "كشف حساب العمليات اللحظية",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input"),
            singleLine = true,
            placeholder = { Text("بحث برقم المستفيد أو كود العملية...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(14.dp)
        )

        // Status Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChipItem(
                title = "الكل (${transactions.size})",
                isSelected = selectedStatusFilter == null,
                onClick = { selectedStatusFilter = null }
            )
            FilterChipItem(
                title = "الناجحة",
                isSelected = selectedStatusFilter == TransactionStatus.SUCCESS,
                onClick = { selectedStatusFilter = TransactionStatus.SUCCESS }
            )
            FilterChipItem(
                title = "الفاشلة والمسترجعة",
                isSelected = selectedStatusFilter == TransactionStatus.FAILED,
                onClick = { selectedStatusFilter = TransactionStatus.FAILED }
            )
        }

        Text(
            text = "نتائج كشف الحساب (${filteredList.size}):",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Transactions List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList) { tx ->
                val isSuccess = tx.status == TransactionStatus.SUCCESS
                val statusColor = when (tx.status) {
                    TransactionStatus.SUCCESS -> MaterialTheme.colorScheme.secondary
                    TransactionStatus.FAILED -> MaterialTheme.colorScheme.error
                    TransactionStatus.PENDING -> MaterialTheme.colorScheme.tertiary
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewReceipt(tx) }
                        .testTag("history_item_${tx.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(statusColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (tx.status) {
                                            TransactionStatus.SUCCESS -> Icons.Default.CheckCircle
                                            TransactionStatus.FAILED -> Icons.Default.Error
                                            TransactionStatus.PENDING -> Icons.Default.HourglassTop
                                        },
                                        contentDescription = null,
                                        tint = statusColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = tx.serviceName,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "الرقم: ${tx.targetNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${tx.amount} ريال",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (tx.status) {
                                        TransactionStatus.SUCCESS -> "ناجحة"
                                        TransactionStatus.FAILED -> "فاشلة/مسترجعة"
                                        TransactionStatus.PENDING -> "معلقة"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = statusColor
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الرصيد: قبل (${tx.balanceBefore.toInt()}) → بعد (${tx.balanceAfter.toInt()})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = "طباعة",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = tx.transactionCode,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
