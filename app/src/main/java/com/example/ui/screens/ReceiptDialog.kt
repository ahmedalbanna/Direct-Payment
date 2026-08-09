package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.GenericShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionStatus
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(32.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = if (showThermalPreview) "معاينة الطباعة" else "سند العملية",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = { showThermalPreview = !showThermalPreview }) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "تبديل العرض",
                            tint = if (showThermalPreview) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!showThermalPreview) {
                    // Modern Styled Receipt
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = (if (isSuccess) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error).copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (isSuccess) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isSuccess) "عملية ناجحة" else "عملية غير مكتملة",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = if (isSuccess) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Details Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ReceiptDetailRow("الخدمة", transaction.serviceName, isHeader = true)
                                ReceiptDetailRow("رقم المستفيد", transaction.targetNumber)
                                if (!transaction.subscriberName.isNull_or_blank()) {
                                    ReceiptDetailRow("اسم المشترك", transaction.subscriberName!!)
                                }
                                ReceiptDetailRow("رقم المرجع", transaction.transactionCode)

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                )

                                ReceiptDetailRow(
                                    "المبلغ الإجمالي",
                                    "${transaction.amount} ريال",
                                    isBold = true,
                                    isLarge = true
                                )
                                ReceiptDetailRow(
                                    "الرصيد بعد الخصم",
                                    "${transaction.balanceAfter} ريال",
                                    isBold = true
                                )
                            }
                        }

                        if (!transaction.digitalCode.isNull_or_blank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "كود التفعيل / البطاقة",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = transaction.digitalCode!!,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 2.sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Thermal Paper Receipt Simulator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val thermalPaperShape = GenericShape { size, _ ->
                            val zigZagHeight = 10f
                            val zigZagCount = 20
                            val step = size.width / (zigZagCount * 2)

                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width, size.height - zigZagHeight)
                            
                            // Bottom ZigZag
                            for (i in zigZagCount * 2 downTo 1) {
                                val x = i * step
                                val y = if (i % 2 == 0) size.height else size.height - zigZagHeight
                                lineTo(x, y)
                            }
                            lineTo(0f, size.height - zigZagHeight)
                            close()
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(thermalPaperShape),
                            color = Color(0xFFFFFFF0), // Ivory receipt paper
                            shadowElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 24.dp, bottom = 40.dp, start = 20.dp, end = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = merchantName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Black
                                )
                                Text(
                                    text = "هاتف: $merchantPhone",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Black
                                )
                                Text(
                                    text = "--------------------------------",
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Black
                                )
                                
                                Text(
                                    text = "رقم العملية: ${transaction.transactionCode}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = formattedDate,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Black
                                )
                                Text(
                                    text = "--------------------------------",
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Black
                                )

                                ThermalDetailRow("الخدمة:", transaction.serviceName)
                                ThermalDetailRow("الرقم:", transaction.targetNumber)
                                if (!transaction.subscriberName.isNull_or_blank()) {
                                    ThermalDetailRow("الاسم:", transaction.subscriberName!!)
                                }
                                ThermalDetailRow("المبلغ:", "${transaction.amount} YER")
                                
                                Text("--------------------------------", fontFamily = FontFamily.Monospace)
                                
                                if (!transaction.digitalCode.isNull_or_blank()) {
                                    Text("كود البطاقة:", fontSize = 12.sp)
                                    Text(transaction.digitalCode!!, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("--------------------------------", fontFamily = FontFamily.Monospace)
                                }

                                Text("الحالة: ${if (isSuccess) "ناجحة SUCCESS" else "فاشلة FAILED"}", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("شكراً لتعاملكم معنا", fontSize = 11.sp)
                                Text("* الجنوب سداد - الأمان والسرعة *", fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Text(
                            "إغلاق",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = {
                            val shareText = """
                                🧾 *سند الجنوب سداد - $merchantName*
                                -----------------------------
                                📌 رقم العملية: ${transaction.transactionCode}
                                🗓️ التاريخ: $formattedDate
                                📱 الخدمة: ${transaction.serviceName}
                                📞 رقم المستفيد: ${transaction.targetNumber}
                                💰 المبلغ: ${transaction.amount} ريال
                                ⚡ الحالة: ${if (isSuccess) "ناجحة ✅" else "فاشلة ❌"}
                                ${if (!transaction.digitalCode.isNull_or_blank()) "🔑 الكود: ${transaction.digitalCode}\n" else ""}
                                -----------------------------
                                شكراً لتعاملكم معنا!
                            """.trimIndent()

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "مشاركة عبر:"))
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "مشاركة السند",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptDetailRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isHeader: Boolean = false,
    isLarge: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            style = if (isLarge) {
                MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            } else if (isHeader) {
                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold)
            } else if (isBold) {
                MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            } else {
                MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
            },
            color = if (isHeader) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ThermalDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
            color = Color.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            color = Color.Black
        )
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()
