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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MerchantProfile

@Composable
fun SettingsScreen(
    merchantProfile: MerchantProfile?,
    onBack: () -> Unit,
    onChangePin: (String) -> Unit,
    onAddDeposit: (Double) -> Unit,
    onToggleSimulateFailure: (Boolean) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var newPinText by remember { mutableStateOf("") }
    var depositText by remember { mutableStateOf("50000") }
    var showChangePin by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                    Text(
                        text = "الإعدادات والتحكم",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .testTag("settings_screen"),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Account Group
            SettingsGroup(title = "الحساب والملف الشخصي") {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "الملف الشخصي للمتجر",
                    subtitle = "الاسم، الرصيد، إحصائيات العمليات",
                    onClick = onNavigateToProfile
                )
            }

            // Security Group
            SettingsGroup(title = "الأمان والحماية") {
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "تغيير رمز PIN",
                    subtitle = "تحديث رمز التأكيد السريع للعمليات",
                    onClick = { showChangePin = !showChangePin }
                )

                if (showChangePin) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "الرمز الحالي: ${merchantProfile?.securityPin ?: "****"}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            OutlinedTextField(
                                value = newPinText,
                                onValueChange = { if (it.length <= 4) newPinText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("new_pin_input"),
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                label = { Text("رمز PIN الجديد (4 أرقام)") },
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    onChangePin(newPinText)
                                    newPinText = ""
                                    showChangePin = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("save_new_pin_button"),
                                enabled = newPinText.length == 4,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("تحديث الرمز")
                            }
                        }
                    }
                }
            }

            // Merchant Tools Group
            SettingsGroup(title = "أدوات التاجر (تجريبي)") {
                SettingsItem(
                    icon = Icons.Default.AddCard,
                    title = "تغذية رصيد المحل",
                    subtitle = "إضافة مبلغ تجريبي لمحفظة المتجر",
                    trailing = {
                        Text(
                            text = "إيداع",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    onClick = { onAddDeposit(50000.0) }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "محاكاة فشل الشبكة",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "تفعيل فشل الـ API لاختبار الاسترجاع التلقائي",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = merchantProfile?.simulateApiFailures == true,
                        onCheckedChange = { onToggleSimulateFailure(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.error,
                            checkedTrackColor = MaterialTheme.colorScheme.errorContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 0.5.dp
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (trailing != null) {
            trailing()
        } else {
            Icon(
                imageVector = Icons.Default.ArrowBack, // Since it's RTL Arabic, ArrowBack points left
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

