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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DigitalCardItem
import com.example.data.model.ServiceCatalogData
import com.example.data.model.ServiceType
import com.example.ui.viewmodel.PendingTransaction

enum class DigitalCardsStep {
    SELECT_CARD,
    ENTER_DETAILS,
    CONFIRMATION
}

@Composable
fun DigitalCardsScreen(
    onBack: () -> Unit,
    onSubmitCardPurchase: (PendingTransaction) -> Unit
) {
    val cards = ServiceCatalogData.digitalCards
    var currentStep by remember { mutableStateOf(DigitalCardsStep.SELECT_CARD) }
    var selectedCard by remember { mutableStateOf(cards.first()) }
    var playerId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("digital_cards_screen")
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
                        DigitalCardsStep.SELECT_CARD -> onBack()
                        DigitalCardsStep.ENTER_DETAILS -> currentStep = DigitalCardsStep.SELECT_CARD
                        DigitalCardsStep.CONFIRMATION -> currentStep = DigitalCardsStep.ENTER_DETAILS
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "رجوع")
                }
                Column {
                    Text(
                        text = "الكروت الرقمية والألعاب",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (currentStep) {
                            DigitalCardsStep.SELECT_CARD -> "خطوة 1: اختر نوع البطاقة"
                            DigitalCardsStep.ENTER_DETAILS -> "خطوة 2: أدخل معرف اللاعب"
                            DigitalCardsStep.CONFIRMATION -> "خطوة 3: مراجعة الشراء"
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
                DigitalCardsStep.SELECT_CARD -> {
                    Text(
                        text = "اختر البطاقة أو اللعبة المطلوبة:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    cards.forEach { card ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clickable {
                                    selectedCard = card
                                    currentStep = DigitalCardsStep.ENTER_DETAILS
                                }
                                .testTag("card_step_${card.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = if (selectedCard.id == card.id) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = card.nameAr,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "الفئة: ${card.valueLabel}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = "${card.priceYer.toInt()} ريال",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                DigitalCardsStep.ENTER_DETAILS -> {
                    Text(
                        text = "أدخل بيانات المستفيد لـ ${selectedCard.nameAr}:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "يرجى إدخال معرف اللاعب (ID) أو رقم العميل بدقة لضمان وصول الشحن.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            OutlinedTextField(
                                value = playerId,
                                onValueChange = { playerId = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("card_player_id_wizard"),
                                label = { Text("ID اللاعب / رقم العميل") },
                                placeholder = { Text("مثال: 5198240182") },
                                leadingIcon = { Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(14.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Button(
                                onClick = { currentStep = DigitalCardsStep.CONFIRMATION },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("الاستمرار للمراجعة", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                DigitalCardsStep.CONFIRMATION -> {
                    Text(
                        text = "مراجعة تفاصيل الطلب:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DetailRowItem("المنتج:", selectedCard.nameAr, MaterialTheme.colorScheme.primary)
                            DetailRowItem("الفئة:", selectedCard.valueLabel)
                            DetailRowItem(
                                "معرف المستفيد:",
                                if (playerId.isBlank()) "شحن كود مباشر" else playerId
                            )
                            DetailRowItem(
                                "سعر الشراء:",
                                "${selectedCard.priceYer.toInt()} ريال",
                                fontWeight = FontWeight.ExtraBold
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )

                            Text(
                                text = "عند النقر على تأكيد، سيتم خصم المبلغ وتوليد كود البطاقة فوراً.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    val generatedVoucherCode = "PIN-" + (1000..9999).random() + "-" + (1000..9999).random()

                    Button(
                        onClick = {
                            onSubmitCardPurchase(
                                PendingTransaction(
                                    serviceType = ServiceType.DIGITAL_CARD,
                                    serviceName = "كرت ${selectedCard.nameAr} - ${selectedCard.valueLabel}",
                                    providerCategory = selectedCard.nameAr,
                                    targetNumber = playerId.ifBlank { "شحن كود مباشر" },
                                    amount = selectedCard.priceYer,
                                    cost = selectedCard.priceYer * 0.99,
                                    digitalCode = generatedVoucherCode,
                                    notes = "كود البطاقة المولد: $generatedVoucherCode"
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .testTag("submit_card_purchase_wizard"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            "تأكيد الشراء الآن (${selectedCard.priceYer.toInt()} ريال)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRowItem(
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

