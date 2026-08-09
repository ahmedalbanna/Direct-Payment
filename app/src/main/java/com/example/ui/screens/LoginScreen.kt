package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryBlue

@Composable
fun LoginScreen(
    merchantName: String,
    onLoginSuccess: () -> Unit,
    onNavigateToReset: () -> Unit = {}
) {
    var pin by remember { mutableStateOf("") }
    val maxPinLength = 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = PrimaryBlue.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = PrimaryBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "مرحباً بك، $merchantName",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "أدخل رمز PIN للمتابعة",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // PIN Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(maxPinLength) { index ->
                val isFilled = index < pin.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isFilled) PrimaryBlue else Color.LightGray.copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Number Pad
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            null -> Spacer(modifier = Modifier.size(72.dp))
                            "back" -> {
                                IconButton(
                                    onClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) },
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Icon(Icons.Default.Backspace, contentDescription = "مسح")
                                }
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            if (pin.length < maxPinLength) {
                                                pin += char
                                                if (pin.length == maxPinLength) {
                                                    // In a real app, verify PIN here
                                                    // For now, any 4 digits work for the demo
                                                    onLoginSuccess()
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onNavigateToReset) {
            Text(
                text = "نسيت رمز PIN؟",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
