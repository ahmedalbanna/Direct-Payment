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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceCatalogData
import com.example.data.model.ServiceType
import com.example.data.repository.BillInquiryResult
import com.example.ui.viewmodel.PendingTransaction

enum class BillsStep {
    SELECT_SERVICE,
    ENTER_ACCOUNT,
    REVIEW_AND_PAY
}

@Composable
fun BillsScreen(
    inquiryResult: BillInquiryResult?,
    isInquiring: Boolean,
    onBack: () -> Unit,
    onInquireBill: (String, String) -> Unit,
    onClearInquiry: () -> Unit,
    onSubmitBillPay: (PendingTransaction) -> Unit
) {
    val services = ServiceCatalogData.billServices
    var currentStep by remember { mutableStateOf(BillsStep.SELECT_SERVICE) }
    var selectedService by remember { mutableStateOf(services.first()) }
    var accountId by remember { mutableStateOf("") }
    var payAmountText by remember { mutableStateOf("") }

    // Auto-advance to Review step when inquiry result is received
    LaunchedEffect(inquiryResult) {
        if (inquiryResult != null && currentStep == BillsStep.ENTER_ACCOUNT) {
            currentStep = BillsStep.REVIEW_AND_PAY
            payAmountText = inquiryResult.dueAmountYer.toInt().toString()
        }
    }

    val iconForCategory = when (selectedService.id) {
        "landline" -> Icons.Default.Phone
        "adsl" -> Icons.Default.Wifi
        "yemen_4g" -> Icons.Default.CellTower
        "electricity" -> Icons.Default.Bolt
        "water" -> Icons.Default.WaterDrop
        else -> Icons.Default.ReceiptLong
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("bills_screen")
    ) {
        // Top Custom Header with Step Logic
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    when (currentStep) {
                        BillsStep.SELECT_SERVICE -> onBack()
                        BillsStep.ENTER_ACCOUNT -> currentStep = BillsStep.SELECT_SERVICE
                        BillsStep.REVIEW_AND_PAY -> {
                            currentStep = BillsStep.ENTER_ACCOUNT
                            onClearInquiry()
                        }
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "رجوع")
                }
                Column {
                    Text(
                        text = "سداد الفواتير والاشتراكات",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (currentStep) {
                            BillsStep.SELECT_SERVICE -> "الخطوة 1: اختر نوع الخدمة"
                            BillsStep.ENTER_ACCOUNT -> "الخطوة 2: أدخل بيانات الحساب"
                            BillsStep.REVIEW_AND_PAY -> "الخطوة 3: المراجعة والدفع"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when (currentStep) {
                BillsStep.SELECT_SERVICE -> {
                    Text(
                        text = "اختر الخدمة المطلوب سدادها:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    services.forEach { service ->
                        val isSelected = service.id == selectedService.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .clickable {
                                    selectedService = service
                                    currentStep = BillsStep.ENTER_ACCOUNT
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, service.primaryColor) else null
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(CircleShape).background(service.primaryColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when(service.id) {
                                            "landline" -> Icons.Default.Phone
                                            "adsl" -> Icons.Default.Wifi
                                            else -> Icons.Default.ReceiptLong
                                        },
                                        contentDescription = null,
                                        tint = service.primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = service.nameAr,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isSelected) service.primaryColor else Color.Transparent
                                )
                            }
                        }
                    }
                }

                BillsStep.ENTER_ACCOUNT -> {
                    Text(
                        text = "بيانات الحساب لخدمة ${selectedService.nameAr}:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = accountId,
                                onValueChange = {
                                    accountId = it
                                    onClearInquiry()
                                },
                                modifier = Modifier.fillMaxWidth().testTag("bill_account_input_wizard"),
                                label = { Text("رقم الحساب / الهاتف الثابت") },
                                leadingIcon = { Icon(imageVector = iconForCategory, contentDescription = null, tint = selectedService.primaryColor) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Button(
                                onClick = { onInquireBill(selectedService.id, accountId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("inquire_bill_button_wizard"),
                                enabled = accountId.length >= 3 && !isInquiring,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = selectedService.primaryColor)
                            ) {
                                if (isInquiring) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 3.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("جاري الاستعلام...", style = MaterialTheme.typography.labelLarge)
                                } else {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("استعلام عن المستحق", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }

                BillsStep.REVIEW_AND_PAY -> {
                    Text(
                        text = "تفاصيل المشترك والمبلغ المستحق:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "تم جلب البيانات بنجاح",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            DetailInfoRow("اسم المشترك:", inquiryResult?.subscriberName ?: "غير متوفر")
                            DetailInfoRow(
                                "المبلغ المستحق:",
                                "${inquiryResult?.dueAmountYer ?: 0.0} ريال",
                                isPrice = true
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "مبلغ السداد المطلوب:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            OutlinedTextField(
                                value = payAmountText,
                                onValueChange = { payAmountText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bill_pay_amount_input_wizard"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(14.dp),
                                suffix = { Text("ريال", style = MaterialTheme.typography.bodyMedium) }
                            )
                        }
                    }

                    val parsedAmount = payAmountText.toDoubleOrNull() ?: 0.0
                    Button(
                        onClick = {
                            onSubmitBillPay(
                                PendingTransaction(
                                    serviceType = ServiceType.BILL,
                                    serviceName = "سداد فاتورة ${selectedService.nameAr}",
                                    providerCategory = selectedService.nameAr,
                                    targetNumber = accountId,
                                    amount = parsedAmount,
                                    cost = parsedAmount * 0.99,
                                    subscriberName = inquiryResult?.subscriberName
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .testTag("submit_bill_pay_button_wizard"),
                        enabled = parsedAmount > 0,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = selectedService.primaryColor)
                    ) {
                        Text(
                            "تأكيد السداد الآن ($parsedAmount ريال)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String, isPrice: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            style = if (isPrice) {
                MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            } else {
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            },
            color = if (isPrice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
