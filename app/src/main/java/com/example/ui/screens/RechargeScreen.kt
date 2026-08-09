package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceCatalogData
import com.example.data.model.ServiceType
import com.example.data.model.TelecomNetwork
import com.example.data.model.TelecomPackage
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.PendingTransaction

enum class RechargeStep {
    SELECT_NETWORK,
    ENTER_PHONE,
    SELECT_PACKAGE,
    CONFIRMATION
}

@Composable
fun RechargeScreen(
    onBack: () -> Unit,
    onSubmitRecharge: (PendingTransaction) -> Unit
) {
    val networks = ServiceCatalogData.networks
    var currentStep by remember { mutableStateOf(RechargeStep.SELECT_NETWORK) }
    var selectedNetwork by remember { mutableStateOf(networks.first()) }
    var phoneNumber by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("1000") }

    val filteredPackages = remember(selectedNetwork) {
        ServiceCatalogData.packages.filter { it.networkId == selectedNetwork.id }
    }
    var selectedPackage by remember(selectedNetwork) { mutableStateOf<TelecomPackage?>(null) }

    // Auto detect network by prefix
    fun autoSelectNetworkByPhone(phone: String) {
        if (phone.length >= 2) {
            val prefix = phone.substring(0, 2)
            val matched = networks.find { net -> net.prefixList.contains(prefix) }
            if (matched != null && matched.id != selectedNetwork.id) {
                selectedNetwork = matched
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("recharge_screen")
    ) {
        // Custom App Bar / Header
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
                        RechargeStep.SELECT_NETWORK -> onBack()
                        RechargeStep.ENTER_PHONE -> currentStep = RechargeStep.SELECT_NETWORK
                        RechargeStep.SELECT_PACKAGE -> currentStep = RechargeStep.ENTER_PHONE
                        RechargeStep.CONFIRMATION -> currentStep = RechargeStep.SELECT_PACKAGE
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "رجوع")
                }
                Column {
                    Text(
                        text = "تعبئة رصيد وباقات",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (currentStep) {
                            RechargeStep.SELECT_NETWORK -> "خطوة 1: اختر شركة الاتصال"
                            RechargeStep.ENTER_PHONE -> "خطوة 2: أدخل رقم الهاتف"
                            RechargeStep.SELECT_PACKAGE -> "خطوة 3: اختر الباقة أو المبلغ"
                            RechargeStep.CONFIRMATION -> "خطوة 4: مراجعة العملية"
                        },
                        fontSize = 12.sp,
                        color = PrimaryBlue
                    )
                }
            }
        }

        // Progress Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            RechargeStep.entries.forEach { step ->
                val isCompleted = step.ordinal < currentStep.ordinal
                val isCurrent = step == currentStep
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(4.dp)
                        .width(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCurrent) PrimaryBlue 
                            else if (isCompleted) PrimaryBlue.copy(alpha = 0.4f) 
                            else Color.Gray.copy(alpha = 0.2f)
                        )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (currentStep) {
                RechargeStep.SELECT_NETWORK -> {
                    Text(
                        text = "اختر الشبكة المراد شحنها:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    networks.forEach { network ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clickable {
                                    selectedNetwork = network
                                    currentStep = RechargeStep.ENTER_PHONE
                                }
                                .testTag("network_step_${network.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = if (selectedNetwork.id == network.id) androidx.compose.foundation.BorderStroke(2.dp, network.primaryColor) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(network.primaryColor.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = network.nameAr.take(1),
                                            color = network.primaryColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(text = network.nameAr, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(text = "البادئات: ${network.prefixList.joinToString(", ")}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                                if (selectedNetwork.id == network.id) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = network.primaryColor)
                                }
                            }
                        }
                    }
                }

                RechargeStep.ENTER_PHONE -> {
                    Text(
                        text = "أدخل رقم هاتف ${selectedNetwork.nameAr}:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = {
                                    if (it.length <= 9) {
                                        phoneNumber = it
                                        autoSelectNetworkByPhone(it)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("recharge_phone_input_wizard"),
                                singleLine = true,
                                placeholder = { Text("مثال: 771234567") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.PhonelinkRing, contentDescription = null, tint = selectedNetwork.primaryColor)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(14.dp)
                            )

                            // Quick Phone Chips
                            Text(text = "أرقام سريعة:", fontSize = 12.sp, color = Color.Gray)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("771234567", "733456789", "711223344").forEach { quickNum ->
                                    Surface(
                                        modifier = Modifier.clickable {
                                            phoneNumber = quickNum
                                            autoSelectNetworkByPhone(quickNum)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.background,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
                                    ) {
                                        Text(
                                            text = quickNum,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { currentStep = RechargeStep.SELECT_PACKAGE },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = phoneNumber.length >= 8,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = selectedNetwork.primaryColor)
                    ) {
                        Text("الاستمرار لاختيار الباقة", fontWeight = FontWeight.Bold)
                    }
                }

                RechargeStep.SELECT_PACKAGE -> {
                    Text(
                        text = "اختر الباقة لـ $phoneNumber:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    // Custom Amount First
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPackage = null
                                amountText = ""
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPackage == null) selectedNetwork.primaryColor.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (selectedPackage == null) androidx.compose.foundation.BorderStroke(2.dp, selectedNetwork.primaryColor) else null
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "شحن رصيد مباشر (مبلغ مخصص)", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { amountText = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                placeholder = { Text("أدخل المبلغ يدوياً") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                suffix = { Text("ريال") },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Text(text = "أو اختر باقة جاهزة:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)

                    filteredPackages.forEach { pkg ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPackage = pkg
                                    amountText = pkg.priceYer.toInt().toString()
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPackage?.id == pkg.id) selectedNetwork.primaryColor.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                            ),
                            border = if (selectedPackage?.id == pkg.id) androidx.compose.foundation.BorderStroke(2.dp, selectedNetwork.primaryColor) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = pkg.titleAr, fontWeight = FontWeight.Bold)
                                    Text(text = pkg.descriptionAr, fontSize = 11.sp, color = Color.Gray)
                                }
                                Text(
                                    text = "${pkg.priceYer.toInt()} ريال",
                                    color = selectedNetwork.primaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { currentStep = RechargeStep.CONFIRMATION },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = amountText.toDoubleOrNull() ?: 0.0 > 0,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = selectedNetwork.primaryColor)
                    ) {
                        Text("الاستمرار للمراجعة", fontWeight = FontWeight.Bold)
                    }
                }

                RechargeStep.CONFIRMATION -> {
                    Text(
                        text = "مراجعة تفاصيل العملية:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            DetailRow("الشبكة:", selectedNetwork.nameAr, selectedNetwork.primaryColor)
                            DetailRow("رقم الهاتف:", phoneNumber)
                            DetailRow("نوع العملية:", selectedPackage?.titleAr ?: "رصيد مباشر")
                            DetailRow("المبلغ الإجمالي:", "$amountText ريال", fontWeight = FontWeight.ExtraBold)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(selectedNetwork.primaryColor.copy(alpha = 0.1f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "يرجى التأكد من صحة الرقم قبل التأكيد. سيتم خصم المبلغ من رصيدك فوراً.",
                                    fontSize = 11.sp,
                                    color = selectedNetwork.primaryColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
                    Button(
                        onClick = {
                            val pkgName = selectedPackage?.titleAr ?: "تعبئة رصيد مباشر"
                            onSubmitRecharge(
                                PendingTransaction(
                                    serviceType = ServiceType.RECHARGE,
                                    serviceName = "${selectedNetwork.nameAr} - $pkgName",
                                    providerCategory = selectedNetwork.nameAr,
                                    targetNumber = phoneNumber,
                                    amount = parsedAmount,
                                    cost = parsedAmount * 0.99
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .testTag("submit_recharge_button_wizard"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = selectedNetwork.primaryColor)
                    ) {
                        Text("تأكيد العملية الآن ($parsedAmount ريال)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface, fontWeight: FontWeight = FontWeight.Bold) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, color = valueColor, fontWeight = fontWeight, fontSize = 15.sp)
    }
}

