package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.SendAndArchive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceType
import com.example.ui.theme.SecondaryTeal
import com.example.ui.viewmodel.PendingTransaction

@Composable
fun TransferScreen(
    onBack: () -> Unit,
    onSubmitTransfer: (PendingTransaction) -> Unit
) {
    var targetAccount by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("transfer_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "رجوع")
            }
            Text(
                text = "تحويل مالي بين الحسابات",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "بيانات حساب المستلم (رمز الوكيل / الهاتف):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = targetAccount,
                    onValueChange = { targetAccount = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transfer_target_input"),
                    singleLine = true,
                    placeholder = { Text("أدخل رمز POS أو هاتف الحساب المقابل") },
                    leadingIcon = { Icon(imageVector = Icons.Default.SendAndArchive, contentDescription = null, tint = SecondaryTeal) },
                    shape = RoundedCornerShape(14.dp)
                )

                Text(
                    text = "المبلغ المراد تحويله (ريال يمني):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transfer_amount_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("0.0") },
                    shape = RoundedCornerShape(14.dp),
                    suffix = { Text("ريال يمني") }
                )

                Text(
                    text = "سبب التحويل / ملاحظات:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("مثال: تغذية رصيد نقطة بيع") },
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        val parsedAmount = amountText.toDoubleOrNull() ?: 0.0

        Button(
            onClick = {
                onSubmitTransfer(
                    PendingTransaction(
                        serviceType = ServiceType.TRANSFER,
                        serviceName = "تحويل مالي إلى $targetAccount",
                        providerCategory = "حسابات وكلاء",
                        targetNumber = targetAccount,
                        amount = parsedAmount,
                        cost = parsedAmount, // no fee for direct agent transfer
                        notes = notesText
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("submit_transfer_button"),
            enabled = targetAccount.isNotBlank() && parsedAmount > 0,
            colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "تحويل المبلغ بـ PIN ($parsedAmount ريال)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
