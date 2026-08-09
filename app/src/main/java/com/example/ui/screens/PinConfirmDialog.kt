package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.PendingTransaction

@Composable
fun PinConfirmDialog(
    pendingTx: PendingTransaction,
    isProcessing: Boolean,
    errorMessage: String?,
    onConfirmPin: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val maxPinLength = 4

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(32.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss, enabled = !isProcessing) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "تأكيد العملية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PinConfirmDetailRow("الخدمة:", pendingTx.serviceName, isPrimary = true)
                        PinConfirmDetailRow("رقم الحساب:", pendingTx.targetNumber)
                        if (pendingTx.subscriberName != null) {
                            PinConfirmDetailRow("الاسم:", pendingTx.subscriberName)
                        }
                        PinConfirmDetailRow("المبلغ:", "${pendingTx.amount} ريال", isBold = true)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "أدخل رمز PIN لتأكيد الخصم",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PIN Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(maxPinLength) { index ->
                        val isFilled = index < pin.length
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) PrimaryBlue else Color.LightGray.copy(alpha = 0.5f))
                        )
                    }
                }

                AnimatedVisibility(visible = errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = ErrorRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (isProcessing) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("جاري معالجة الطلب...", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Numeric Pad for Dialog
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val buttons = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf(null, "0", "back")
                        )

                        buttons.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                row.forEach { char ->
                                    when (char) {
                                        null -> Spacer(modifier = Modifier.size(60.dp))
                                        "back" -> {
                                            IconButton(
                                                onClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) },
                                                modifier = Modifier.size(60.dp)
                                            ) {
                                                Icon(Icons.Default.Backspace, contentDescription = "مسح", tint = Color.DarkGray)
                                            }
                                        }
                                        else -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        if (pin.length < maxPinLength) {
                                                            pin += char
                                                            if (pin.length == maxPinLength) {
                                                                onConfirmPin(pin)
                                                            }
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = char,
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PinConfirmDetailRow(label: String, value: String, isPrimary: Boolean = false, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Text(
            text = value,
            fontWeight = if (isBold || isPrimary) FontWeight.Bold else FontWeight.Medium,
            fontSize = if (isPrimary) 14.sp else 13.sp,
            color = if (isPrimary) PrimaryBlue else Color.Black
        )
    }
}

