package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionStatus
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptDialog(
    transaction: TransactionRecord,
    merchantName: String,
    merchantPhone: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showThermalPreview by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar"))
    val formattedDate = dateFormat.format(Date(transaction.timestamp))

    val isSuccess = transaction.status == TransactionStatus.SUCCESS

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("receipt_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Top
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                    Text(
                        text = if (showThermalPreview) "معاينة الطابعة الحرارية POS" else "سند عملية رسمية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!showThermalPreview) {
                    // Standard Invoice View
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (isSuccess) SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (isSuccess) SuccessGreen else ErrorRed,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isSuccess) "تمت العملية بنجاح" else "فشلت العملية - تم استرجاع المبلغ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = if (isSuccess) SuccessGreen else ErrorRed
                    )

                    Text(
                        text = merchantName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Receipt Content Details
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ReceiptRow("رقم العملية المرجعي:", transaction.transactionCode)
                            ReceiptRow("التاريخ والوقت:", formattedDate)
                            ReceiptRow("الخدمة:", transaction.serviceName)
                            ReceiptRow("المزود / الشبكة:", transaction.providerCategory)
                            ReceiptRow("رقم المستفيد / الحساب:", transaction.targetNumber)

                            if (!transaction.subscriberName.isNull_or_blank()) {
                                ReceiptRow("اسم المشترك:", transaction.subscriberName ?: "")
                            }

                            if (!transaction.digitalCode.isNull_or_blank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimaryBlue.copy(alpha = 0.1f))
                                        .padding(12.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Text(text = "رمز البطاقة / الكود الرقمي:", fontSize = 12.sp, color = PrimaryBlue)
                                        Text(text = transaction.digitalCode ?: "", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryBlue)
                                    }
                                }
                            }

                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

                            ReceiptRow("المبلغ المدفوع:", "${transaction.amount} ريال يمني", isBold = true)
                            ReceiptRow("الرصيد قبل العملية:", "${transaction.balanceBefore} ريال")
                            ReceiptRow("الرصيد المتبقي بعد الخصم:", "${transaction.balanceAfter} ريال", isBold = true)

                            if (!transaction.notes.isNull_or_blank()) {
                                Text(
                                    text = "ملاحظات: ${transaction.notes}",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Thermal Receipt Simulator View (58mm Thermal Print Paper View)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFE0)), // Receipt paper tint
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("==========================", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                            Text(merchantName, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center)
                            Text("هاتف: $merchantPhone", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            Text("سند عملية رقم: ${transaction.transactionCode}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(formattedDate, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text("--------------------------", fontFamily = FontFamily.Monospace, fontSize = 12.sp)

                            ThermalRow("الخدمة:", transaction.serviceName)
                            ThermalRow("الرقم:", transaction.targetNumber)
                            if (!transaction.subscriberName.isNull_or_blank()) {
                                ThermalRow("الاسم:", transaction.subscriberName ?: "")
                            }
                            ThermalRow("المبلغ:", "${transaction.amount} YER")
                            ThermalRow("الحالة:", if (isSuccess) "ناجحة SUCCESS" else "فاشلة FAILED")

                            if (!transaction.digitalCode.isNull_or_blank()) {
                                Text("--------------------------", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                Text("الكود: ${transaction.digitalCode}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Text("--------------------------", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                            Text("شكراً لتعاملكم معنا", fontSize = 12.sp, textAlign = TextAlign.Center)
                            Text("==========================", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showThermalPreview = !showThermalPreview },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (showThermalPreview) "سند عادي" else "معاينة حرارية", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            val shareText = """
                                🧾 *سند عملية - $merchantName*
                                -----------------------------
                                📌 رقم العملية: ${transaction.transactionCode}
                                🗓️ التاريخ: $formattedDate
                                📱 الخدمة: ${transaction.serviceName}
                                📞 رقم المستفيد: ${transaction.targetNumber}
                                💰 المبلغ: ${transaction.amount} ريال يمني
                                ⚡ الحالة: ${if (isSuccess) "ناجحة ✅" else "فاشلة/مسترجعة ❌"}
                                ${if (!transaction.digitalCode.isNull_or_blank()) "🔑 رمز الكود: ${transaction.digitalCode}\n" else ""}
                                -----------------------------
                                شكراً لتعاملكم معنا!
                            """.trimIndent()

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "مشاركة السند عبر:"))
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("share_whatsapp_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاركة واتساب", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Text(
            text = value,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ThermalRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
        Text(text = value, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()
