package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DigitalCardItem
import com.example.data.model.ServiceCatalogData
import com.example.data.model.ServiceType
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.PendingTransaction

@Composable
fun DigitalCardsScreen(
    onBack: () -> Unit,
    onSubmitCardPurchase: (PendingTransaction) -> Unit
) {
    val cards = ServiceCatalogData.digitalCards
    var selectedCard by remember { mutableStateOf(cards.first()) }
    var playerId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("digital_cards_screen"),
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
                text = "شراء الكروت الرقمية والألعاب",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = "اختر البطاقة الرقمية أو لعبة الشحن:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        // Cards List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            cards.forEach { card ->
                val isSelected = card.id == selectedCard.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCard = card }
                        .testTag("digital_card_item_${card.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) card.primaryColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, card.primaryColor) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = card.primaryColor
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = card.nameAr,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "الفئة: ${card.valueLabel}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Text(
                            text = "${card.priceYer.toInt()} ريال",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = card.primaryColor
                        )
                    }
                }
            }
        }

        // Player ID / Target Card Number Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "معرف اللاعب (Player ID) أو رقم العميل:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = playerId,
                    onValueChange = { playerId = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_player_id_input"),
                    singleLine = true,
                    placeholder = { Text("أدخل Player ID مثل 5198240182") },
                    leadingIcon = { Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, tint = selectedCard.primaryColor) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp)
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
                .height(54.dp)
                .testTag("submit_card_purchase_button"),
            colors = ButtonDefaults.buttonColors(containerColor = selectedCard.primaryColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "شراء الكرت وتوليد الكود بـ PIN (${selectedCard.priceYer.toInt()} ريال)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
