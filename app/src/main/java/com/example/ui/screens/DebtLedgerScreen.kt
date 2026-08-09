package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Customer
import com.example.data.model.LedgerEntry
import com.example.data.model.LedgerType
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DebtLedgerScreen(
    customers: List<Customer>,
    selectedCustomer: Customer?,
    ledgerEntries: List<LedgerEntry>,
    onBack: () -> Unit,
    onSelectCustomer: (Customer?) -> Unit,
    onAddCustomer: (String, String, Double, String) -> Unit,
    onRecordLedgerTransaction: (Double, LedgerType, String) -> Unit
) {
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var showRecordLedgerDialog by remember { mutableStateOf(false) }
    var ledgerTypeToRecord by remember { mutableStateOf(LedgerType.DEBIT) }

    val dateFormat = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("debt_ledger_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Top
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (selectedCustomer != null) onSelectCustomer(null) else onBack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "رجوع")
                }
                Text(
                    text = if (selectedCustomer != null) "كشف حساب: ${selectedCustomer.name}" else "إدارة الديون وحسابات العملاء",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (selectedCustomer == null) {
                IconButton(
                    onClick = { showAddCustomerDialog = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                        .testTag("add_customer_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة عميل", tint = Color.White)
                }
            }
        }

        if (selectedCustomer == null) {
            // Summary Banner
            val totalSystemDebt = customers.sumOf { it.totalDebt }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "إجمالي ديون العملاء المسجلة:", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = "$totalSystemDebt ريال يمني",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = ErrorRed
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ErrorRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Book, contentDescription = null, tint = ErrorRed)
                    }
                }
            }

            Text(
                text = "قائمة العملاء والنقاط المسجلة:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            // Customers List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(customers) { customer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCustomer(customer) }
                            .testTag("customer_item_${customer.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = PrimaryBlue)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = customer.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = customer.phone,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (customer.totalDebt >= 0) "عليه دين:" else "له رصيد:",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${Math.abs(customer.totalDebt)} ريال",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (customer.totalDebt > 0) ErrorRed else SuccessGreen
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Customer Ledger View
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "رقم الهاتف: ${selectedCustomer.phone}", fontSize = 13.sp, color = Color.Gray)
                        Text(
                            text = "صافي الدين الحالي: ${selectedCustomer.totalDebt} ريال",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (selectedCustomer.totalDebt > 0) ErrorRed else SuccessGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                ledgerTypeToRecord = LedgerType.DEBIT
                                showRecordLedgerDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("تسجيل دين عليه (+)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                ledgerTypeToRecord = LedgerType.CREDIT
                                showRecordLedgerDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("تسجيل سداد منه (-)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Text(
                text = "حركات كشف الحساب اللحظي:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ledgerEntries) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (entry.type == LedgerType.DEBIT) "دين جديد (+)" else "سداد منه (-)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (entry.type == LedgerType.DEBIT) ErrorRed else SuccessGreen
                                )
                                Text(
                                    text = "${entry.amount} ريال",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (entry.type == LedgerType.DEBIT) ErrorRed else SuccessGreen
                                )
                            }
                            Text(text = entry.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = dateFormat.format(Date(entry.timestamp)), fontSize = 10.sp, color = Color.Gray)
                                Text(text = "الدين التراكمي بعدها: ${entry.balanceAfter} ريال", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Customer Modal
    if (showAddCustomerDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var initialDebtText by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddCustomerDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "إضافة عميل / نقطة جديدة", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("اسم العميل أو المحل") }
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        placeholder = { Text("رقم الهاتف") }
                    )

                    OutlinedTextField(
                        value = initialDebtText,
                        onValueChange = { initialDebtText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("رقم الدين الافتتاحي (إن وجد)") }
                    )

                    Button(
                        onClick = {
                            val initDebt = initialDebtText.toDoubleOrNull() ?: 0.0
                            onAddCustomer(name, phone, initDebt, notes)
                            showAddCustomerDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("حفظ العميل", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Record Ledger Entry Modal
    if (showRecordLedgerDialog) {
        var amountText by remember { mutableStateOf("") }
        var descriptionText by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showRecordLedgerDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (ledgerTypeToRecord == LedgerType.DEBIT) "تسجيل دين جديد على العميل" else "تسجيل سداد نقدي من العميل",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (ledgerTypeToRecord == LedgerType.DEBIT) ErrorRed else SuccessGreen
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("المبلغ بالريال اليمني") }
                    )

                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("البيان / التفاصيل (مثال: سداد نقدي نقد) ") }
                    )

                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    Button(
                        onClick = {
                            onRecordLedgerTransaction(amount, ledgerTypeToRecord, descriptionText.ifBlank { "حركة دفتر ديون" })
                            showRecordLedgerDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = amount > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (ledgerTypeToRecord == LedgerType.DEBIT) ErrorRed else SuccessGreen
                        )
                    ) {
                        Text("حفظ الحركة", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
