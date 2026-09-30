package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FormatTextdirectionRToL
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HabitViewModel
import com.example.ui.theme.BorderPurple
import com.example.ui.theme.BrightLilac
import com.example.ui.theme.DeepVioletSurface
import com.example.ui.theme.LightLilac
import com.example.ui.theme.MidnightPurple
import com.example.ui.theme.RoyalPurpleCard
import com.example.ui.theme.RoyalPurpleElevated
import com.example.ui.theme.RoyalPurpleHighlight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VividPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: HabitViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val remindersEnabled by viewModel.remindersEnabled.collectAsStateWithLifecycle()
    val reminderTime by viewModel.reminderTime.collectAsStateWithLifecycle()

    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showTimePickerMenu by remember { mutableStateOf(false) }

    val reminderTimeOptions = listOf(
        "07:00 صباحاً",
        "08:00 صباحاً",
        "09:00 صباحاً",
        "13:00 ظهراً",
        "18:00 مساءً",
        "20:00 مساءً",
        "21:30 ليلاً"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightPurple)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "الإعدادات",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع للرئيسية",
                            tint = TextPrimaryLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepVioletSurface
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Overview Stats
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = RoyalPurpleCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPurple)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "إحصائيات الإنجاز",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                StatCounterItem(
                                    label = "إجمالي العادات",
                                    value = "${stats.totalCount}",
                                    color = BrightLilac
                                )
                                StatCounterItem(
                                    label = "المكتملة اليوم",
                                    value = "${stats.completedCount}",
                                    color = Color(0xFF34D399)
                                )
                                StatCounterItem(
                                    label = "أعلى التزام",
                                    value = "${stats.longestStreak} يوم",
                                    color = Color(0xFFF97316)
                                )
                            }
                        }
                    }
                }

                // Section 2: Theme & Interface (Dark Purple & Arabic RTL)
                item {
                    SettingsSection(title = "المظهر والواجهة") {
                        SettingsRow(
                            icon = Icons.Default.Palette,
                            title = "سمة التطبيق",
                            subtitle = "الأرجواني الداكن الفاخر (مفعّل دائماً)",
                            actionContent = {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(VividPurple)
                                )
                            }
                        )

                        SettingsDivider()

                        SettingsRow(
                            icon = Icons.Default.Language,
                            title = "لغة التطبيق",
                            subtitle = "العربية (Arabic)",
                            actionContent = {
                                Text(
                                    text = "العربية",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = BrightLilac,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )

                        SettingsDivider()

                        SettingsRow(
                            icon = Icons.Default.FormatTextdirectionRToL,
                            title = "محاذاة النص",
                            subtitle = "من اليمين إلى اليسار (RTL كامل وشامل)",
                            actionContent = {
                                Icon(
                                    imageVector = Icons.Default.FormatTextdirectionRToL,
                                    contentDescription = null,
                                    tint = BrightLilac,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )

                        SettingsDivider()

                        SettingsRow(
                            icon = Icons.Default.DarkMode,
                            title = "الوضع الليلي",
                            subtitle = "مفعّل تلقائياً لحماية العين وتوفير البطارية",
                            actionContent = {
                                Text(
                                    text = "داكن",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedLight
                                )
                            }
                        )
                    }
                }

                // Section 3: Reminders & Notifications
                item {
                    SettingsSection(title = "التذكيرات والإشعارات") {
                        SettingsRow(
                            icon = Icons.Default.Alarm,
                            title = "تذكير يومي بالعادات",
                            subtitle = "تنبيه لطيف لتسجيل عاداتك اليومية",
                            actionContent = {
                                Switch(
                                    checked = remindersEnabled,
                                    onCheckedChange = { viewModel.toggleReminders() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = TextPrimaryLight,
                                        checkedTrackColor = VividPurple,
                                        uncheckedThumbColor = TextMutedLight,
                                        uncheckedTrackColor = RoyalPurpleElevated
                                    )
                                )
                            }
                        )

                        if (remindersEnabled) {
                            SettingsDivider()

                            Box {
                                SettingsRow(
                                    icon = Icons.Default.Alarm,
                                    title = "وقت التذكير المفضل",
                                    subtitle = reminderTime,
                                    onClick = { showTimePickerMenu = true },
                                    actionContent = {
                                        TextButton(onClick = { showTimePickerMenu = true }) {
                                            Text(
                                                text = "تعديل",
                                                color = BrightLilac,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                )

                                DropdownMenu(
                                    expanded = showTimePickerMenu,
                                    onDismissRequest = { showTimePickerMenu = false },
                                    modifier = Modifier.background(DeepVioletSurface)
                                ) {
                                    reminderTimeOptions.forEach { timeOption ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = timeOption,
                                                    color = TextPrimaryLight,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            },
                                            onClick = {
                                                viewModel.setReminderTime(timeOption)
                                                showTimePickerMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 4: Data Management
                item {
                    SettingsSection(title = "إدارة البيانات") {
                        SettingsRow(
                            icon = Icons.Default.RestartAlt,
                            title = "إعادة تعيين إنجازات اليوم",
                            subtitle = "إلغاء تحديد كل العادات ليومنا هذا",
                            onClick = { showResetConfirmDialog = true },
                            actionContent = {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = BrightLilac
                                )
                            }
                        )

                        SettingsDivider()

                        SettingsRow(
                            icon = Icons.Default.AutoAwesome,
                            title = "استعادة العادات النموذجية",
                            subtitle = "إضافة مجموعة عادات مقترحة (ماء، قراءة، رياضة...)",
                            onClick = { viewModel.loadSampleHabits() },
                            actionContent = {
                                TextButton(onClick = { viewModel.loadSampleHabits() }) {
                                    Text(
                                        text = "تحميل",
                                        color = BrightLilac,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        )

                        SettingsDivider()

                        SettingsRow(
                            icon = Icons.Default.DeleteForever,
                            title = "مسح جميع البيانات",
                            subtitle = "حذف جميع العادات وسجل الإنجازات نهائياً",
                            onClick = { showClearConfirmDialog = true },
                            actionContent = {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }

                // Section 5: About App & Quote
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = RoyalPurpleCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPurple)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = BrightLilac,
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "تطبيق عاداتي",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )

                            Text(
                                text = "الإصدار 1.0.0 • صُمم لمساعدتك على الاستمرار والنمو",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedLight
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(RoyalPurpleElevated)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "«أَحَبُّ الأَعْمَالِ إِلَى اللهِ أَدْوَمُهَا وَإِنْ قَلَّ»",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LightLilac,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Reset Today Confirm Dialog
        if (showResetConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showResetConfirmDialog = false },
                containerColor = DeepVioletSurface,
                titleContentColor = TextPrimaryLight,
                textContentColor = TextSecondaryLight,
                title = {
                    Text("إعادة تعيين اليوم", fontWeight = FontWeight.Bold, textAlign = TextAlign.Start)
                },
                text = {
                    Text("هل ترغب في إلغاء تحديد علامات الإنجاز لجميع عادات اليوم؟", textAlign = TextAlign.Start)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetToday()
                            showResetConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VividPurple)
                    ) {
                        Text("نعم، إعادة ضبط")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirmDialog = false }) {
                        Text("إلغاء", color = TextSecondaryLight)
                    }
                }
            )
        }

        // Clear All Data Confirm Dialog
        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                containerColor = DeepVioletSurface,
                titleContentColor = TextPrimaryLight,
                textContentColor = TextSecondaryLight,
                title = {
                    Text("مسح كافة البيانات", fontWeight = FontWeight.Bold, textAlign = TextAlign.Start)
                },
                text = {
                    Text(
                        "تحذير: سيتم حذف جميع العادات وسجلات الإنجاز السابقة بشكل نهائي. هل تود المتابعة؟",
                        textAlign = TextAlign.Start
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAllData()
                            showClearConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("حذف الكل")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmDialog = false }) {
                        Text("إلغاء", color = TextSecondaryLight)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BrightLilac,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            textAlign = TextAlign.Start
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RoyalPurpleCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderPurple)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    actionContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val rowModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    } else {
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(RoyalPurpleElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrightLilac,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryLight,
                textAlign = TextAlign.Start
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedLight,
                    textAlign = TextAlign.Start
                )
            }
        }

        if (actionContent != null) {
            Spacer(modifier = Modifier.width(8.dp))
            actionContent()
        }
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = RoyalPurpleHighlight
    )
}

@Composable
fun StatCounterItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedLight,
            textAlign = TextAlign.Center
        )
    }
}
