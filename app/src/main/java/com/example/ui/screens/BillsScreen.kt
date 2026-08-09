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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.ServiceCatalogData
import com.example.data.model.ServiceType
import com.example.data.repository.BillInquiryResult
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.PendingTransaction

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
    var selectedService by remember { mutableStateOf(services.first()) }
    var accountId by remember { mutableStateOf("") }
    var payAmountText by remember { mutableStateOf("") }

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
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("bills_screen"),
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
                text = "سداد الفواتير والاشتراكات",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Services Chips
        Text(text = "اختر الخدمة المطلوب سدادها:", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            services.forEach { service ->
                val isSelected = service.id == selectedService.id
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clickable {
                            selectedService = service
                            onClearInquiry()
                            payAmountText = ""
                        }
                        .testTag("bill_service_chip_${service.id}"),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) service.primaryColor else MaterialTheme.colorScheme.surface,
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)) else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = service.nameAr,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Account / Line Number Card with Inquiry Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "رقم حساب / هاتف الخدمة (${selectedService.nameAr}):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = accountId,
                    onValueChange = {
                        accountId = it
                        onClearInquiry()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bill_account_input"),
                    singleLine = true,
                    placeholder = { Text("أدخل رقم الحساب أو الرقم الثابت") },
                    leadingIcon = { Icon(imageVector = iconForCategory, contentDescription = null, tint = selectedService.primaryColor) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp)
                )

                // Instant Inquiry Button (استعلام عن الفاتورة)
                OutlinedButton(
                    onClick = { onInquireBill(selectedService.id, accountId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("inquire_bill_button"),
                    enabled = accountId.isNotBlank() && !isInquiring,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isInquiring) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("جاري الاستعلام اللحظي من الخادم...")
                    } else {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("استعلام عن المستحق واسم المشترك")
                    }
                }
            }
        }

        // Inquiry Result Panel
        AnimatedVisibility(visible = inquiryResult != null) {
            if (inquiryResult != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inquiry_result_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SuccessGreen)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "نتيجة الاستعلام اللحظي ناجحة", fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "اسم المشترك المسجل:", color = Color.Gray, fontSize = 13.sp)
                            Text(text = inquiryResult.subscriberName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "المبلغ المستحق حالياً:", color = Color.Gray, fontSize = 13.sp)
                            Text(text = "${inquiryResult.dueAmountYer} ريال", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Button(
                            onClick = { payAmountText = inquiryResult.dueAmountYer.toInt().toString() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Text("اعتماد المبلغ المستحق ($payAmountText ريال)", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Payment Amount Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "مبلغ السداد (ريال يمني):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = payAmountText,
                    onValueChange = { payAmountText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bill_pay_amount_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    suffix = { Text("ريال") }
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
                .height(54.dp)
                .testTag("submit_bill_pay_button"),
            enabled = accountId.isNotBlank() && parsedAmount > 0,
            colors = ButtonDefaults.buttonColors(containerColor = selectedService.primaryColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "سداد الفاتورة بـ PIN ($parsedAmount ريال)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
