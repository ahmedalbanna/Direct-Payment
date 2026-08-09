package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.ErrorRed
import com.example.ui.viewmodel.PendingTransaction
import kotlinx.coroutines.delay

enum class RechargeStep {
    SELECT_SERVICE_TYPE,
    ENTER_PHONE,
    SELECT_NETWORK,
    SELECT_PACKAGE,
    ENTER_AMOUNT,
    INQUIRY,
    CONFIRMATION
}

enum class RechargeServiceType {
    DIRECT, PACKAGE
}

@Composable
fun RechargeScreen(
    onBack: () -> Unit,
    onSubmitRecharge: (PendingTransaction) -> Unit
) {
    val networks = ServiceCatalogData.networks
    var currentStep by remember { mutableStateOf(RechargeStep.SELECT_SERVICE_TYPE) }
    var selectedNetwork by remember { mutableStateOf(networks.first()) }
    var isNetworkDetected by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("1000") }
    var serviceType by remember { mutableStateOf(RechargeServiceType.DIRECT) }
    var debtAmount by remember { mutableStateOf(0.0) }

    val filteredPackages = remember(selectedNetwork) {
        ServiceCatalogData.packages.filter { it.networkId == selectedNetwork.id }
    }
    var selectedPackage by remember(selectedNetwork) { mutableStateOf<TelecomPackage?>(null) }

    // Auto detect network by prefix
    fun autoSelectNetworkByPhone(phone: String) {
        if (phone.length >= 2) {
            val prefix = phone.substring(0, 2)
            val matched = networks.find { net -> net.prefixList.contains(prefix) }
            if (matched != null) {
                selectedNetwork = matched
                isNetworkDetected = true
            } else {
                isNetworkDetected = false
            }
        } else {
            isNetworkDetected = false
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
                        RechargeStep.SELECT_SERVICE_TYPE -> onBack()
                        RechargeStep.ENTER_PHONE -> currentStep = RechargeStep.SELECT_SERVICE_TYPE
                        RechargeStep.SELECT_NETWORK -> currentStep = RechargeStep.ENTER_PHONE
                        RechargeStep.ENTER_AMOUNT -> currentStep = RechargeStep.ENTER_PHONE
                        RechargeStep.SELECT_PACKAGE -> currentStep = RechargeStep.ENTER_PHONE
                        RechargeStep.INQUIRY -> currentStep = if (serviceType == RechargeServiceType.DIRECT) RechargeStep.ENTER_AMOUNT else RechargeStep.SELECT_PACKAGE
                        RechargeStep.CONFIRMATION -> currentStep = RechargeStep.INQUIRY
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
                            RechargeStep.SELECT_SERVICE_TYPE -> "خطوة 1: اختر نوع الخدمة"
                            RechargeStep.ENTER_PHONE -> "خطوة 2: أدخل رقم الهاتف"
                            RechargeStep.SELECT_NETWORK -> "تغيير الشبكة يدوياً"
                            RechargeStep.ENTER_AMOUNT -> "خطوة 3: حدد المبلغ"
                            RechargeStep.SELECT_PACKAGE -> "خطوة 3: اختر الباقة"
                            RechargeStep.INQUIRY -> "جاري الاستعلام عن الرقم..."
                            RechargeStep.CONFIRMATION -> "خطوة 4: مراجعة وتأكيد"
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (currentStep) {
                RechargeStep.SELECT_NETWORK -> {
                    Text(
                        text = "اختر الشبكة المراد شحنها:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
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
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = network.nameAr,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "البادئات: ${network.prefixList.joinToString(", ")}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isNetworkDetected) "رقم هاتف ${selectedNetwork.nameAr}:" else "أدخل رقم الهاتف:",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        
                        if (isNetworkDetected) {
                            Text(
                                text = "تغيير الشبكة؟",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { currentStep = RechargeStep.SELECT_NETWORK }
                            )
                        }
                    }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = if (isNetworkDetected) androidx.compose.foundation.BorderStroke(1.dp, selectedNetwork.primaryColor.copy(alpha = 0.5f)) else null
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
                                    Icon(
                                        imageVector = Icons.Default.PhonelinkRing, 
                                        contentDescription = null, 
                                        tint = if (isNetworkDetected) selectedNetwork.primaryColor else MaterialTheme.colorScheme.outline
                                    )
                                },
                                trailingIcon = if (isNetworkDetected) {
                                    {
                                        Surface(
                                            modifier = Modifier.padding(end = 8.dp),
                                            shape = CircleShape,
                                            color = selectedNetwork.primaryColor.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                text = selectedNetwork.nameAr,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = selectedNetwork.primaryColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (isNetworkDetected) selectedNetwork.primaryColor else MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = if (isNetworkDetected) selectedNetwork.primaryColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
                                )
                            )

                            // Quick Phone Chips
                            Text(
                                text = "أرقام سريعة:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant
                                        )
                                    ) {
                                        Text(
                                            text = quickNum,
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { 
                            currentStep = if (serviceType == RechargeServiceType.DIRECT) RechargeStep.ENTER_AMOUNT else RechargeStep.SELECT_PACKAGE 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = phoneNumber.length >= 8 && isNetworkDetected,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isNetworkDetected) selectedNetwork.primaryColor else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("الاستمرار لتحديد التفاصيل", fontWeight = FontWeight.Bold)
                    }
                }

                RechargeStep.SELECT_SERVICE_TYPE -> {
                    Text(
                        text = "ماذا تريد أن تفعل اليوم؟",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ServiceTypeCard(
                            modifier = Modifier.weight(1f),
                            title = "رصيد فوري",
                            icon = Icons.Default.AddCard,
                            color = selectedNetwork.primaryColor,
                            onClick = {
                                serviceType = RechargeServiceType.DIRECT
                                currentStep = RechargeStep.ENTER_PHONE
                            }
                        )
                        ServiceTypeCard(
                            modifier = Modifier.weight(1f),
                            title = "اشتراك باقة",
                            icon = Icons.Default.Dataset,
                            color = SuccessGreen,
                            onClick = {
                                serviceType = RechargeServiceType.PACKAGE
                                currentStep = RechargeStep.ENTER_PHONE
                            }
                        )
                    }
                }

                RechargeStep.ENTER_AMOUNT -> {
                    Text(
                        text = "أدخل مبلغ التعبئة (رصيد فوري):",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        @OptIn(ExperimentalLayoutApi::class)
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { amountText = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("المبلغ المالي (ريال)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(14.dp),
                                trailingIcon = { Text("YER", modifier = Modifier.padding(horizontal = 12.dp), color = Color.Gray) }
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                listOf("500", "1000", "2000", "5000").forEach { quickAmount ->
                                    @OptIn(ExperimentalMaterial3Api::class)
                                    FilterChip(
                                        selected = amountText == quickAmount,
                                        onClick = { amountText = quickAmount },
                                        label = { Text(quickAmount) }
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (amountText.toDoubleOrNull() != null) {
                                currentStep = RechargeStep.INQUIRY
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        enabled = amountText.isNotEmpty(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = selectedNetwork.primaryColor)
                    ) {
                        Text("الاستمرار للاستعلام", fontWeight = FontWeight.Bold)
                    }
                }

                RechargeStep.INQUIRY -> {
                    LaunchedEffect(Unit) {
                        delay(1500)
                        debtAmount = if (phoneNumber.endsWith("0") || phoneNumber.endsWith("5")) 500.0 else 0.0
                        currentStep = RechargeStep.CONFIRMATION
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = selectedNetwork.primaryColor, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "جاري الاستعلام عن الرقم $phoneNumber",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "يتم الآن التحقق من وجود سلف سابقة لدى ${selectedNetwork.nameAr}...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 8.dp)
                        )
                    }
                }

                RechargeStep.SELECT_PACKAGE -> {
                    Text(
                        text = "اختر الباقة لـ $phoneNumber:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
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
                            Text(
                                text = "شحن رصيد مباشر (مبلغ مخصص)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { amountText = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                placeholder = { Text("أدخل المبلغ يدوياً") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                suffix = { Text("ريال", style = MaterialTheme.typography.bodyMedium) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Text(
                        text = "أو اختر باقة جاهزة:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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
                                    Text(
                                        text = pkg.titleAr,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = pkg.descriptionAr,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${pkg.priceYer.toInt()} ريال",
                                    color = selectedNetwork.primaryColor,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { currentStep = RechargeStep.INQUIRY },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = amountText.toDoubleOrNull() ?: 0.0 > 0,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = selectedNetwork.primaryColor)
                    ) {
                        Text("الاستمرار للاستعلام", fontWeight = FontWeight.Bold)
                    }
                }

                RechargeStep.CONFIRMATION -> {
                    val baseAmount = amountText.toDoubleOrNull() ?: 0.0
                    val totalAmount = baseAmount + debtAmount

                    Text(
                        text = "مراجعة تفاصيل العملية:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            DetailRow("الشبكة:", selectedNetwork.nameAr, selectedNetwork.primaryColor)
                            DetailRow("رقم الهاتف:", phoneNumber)
                            DetailRow("نوع الخدمة:", if (serviceType == RechargeServiceType.DIRECT) "رصيد فوري" else "اشتراك باقة")
                            if (serviceType == RechargeServiceType.PACKAGE) {
                                DetailRow("الباقة:", selectedPackage?.titleAr ?: "")
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.3f))
                            
                            DetailRow("مبلغ العملية:", "$baseAmount ريال")
                            if (debtAmount > 0) {
                                DetailRow("السلفة السابقة:", "$debtAmount ريال", valueColor = ErrorRed)
                            }
                            
                            DetailRow(
                                label = "الإجمالي المطلوب خصمه:", 
                                value = "$totalAmount ريال",
                                valueColor = selectedNetwork.primaryColor,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val pkgName = if (serviceType == RechargeServiceType.DIRECT) "رصيد فوري" else selectedPackage?.titleAr ?: "باقة"
                            onSubmitRecharge(
                                PendingTransaction(
                                    serviceType = if (serviceType == RechargeServiceType.DIRECT) ServiceType.RECHARGE else ServiceType.BILL,
                                    serviceName = "${selectedNetwork.nameAr} - $pkgName",
                                    providerCategory = selectedNetwork.nameAr,
                                    targetNumber = phoneNumber,
                                    amount = totalAmount,
                                    cost = totalAmount * 0.99
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
                        Text(
                            "تأكيد العملية الآن ($totalAmount ريال)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceTypeCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            color = valueColor,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = fontWeight)
        )
    }
}

