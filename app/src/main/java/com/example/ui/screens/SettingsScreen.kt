package com.example.ui.screens

import android.app.TimePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.reminder.HabitReminderScheduler
import com.example.ui.HabitViewModel
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.AppTheme
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.CardDensity
import com.example.ui.theme.FontScalePreference
import com.example.ui.theme.ThemeMode
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: HabitViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val remindersEnabled by viewModel.remindersEnabled.collectAsStateWithLifecycle()
    val reminderTime by viewModel.reminderTime.collectAsStateWithLifecycle()
    val isVacationMode by viewModel.isVacationModeEnabled.collectAsStateWithLifecycle()
    val isArabic = LocalAppLanguage.current.isRtl
    val context = LocalContext.current

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showResetTodayDialog by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = Strings.current.settingsTitle,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Strings.current.backButtonDesc,
                            tint = AppTheme.colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Vacation & Recovery Mode (وضع الإجازة والمرض)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BeachAccess,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = Strings.current.vacationModeLabel,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                        Text(
                                            text = Strings.current.vacationModeDesc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AppTheme.colors.textMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = isVacationMode,
                                    onCheckedChange = { viewModel.toggleVacationMode(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFFF59E0B),
                                        uncheckedThumbColor = AppTheme.colors.textMuted,
                                        uncheckedTrackColor = AppTheme.colors.cardElevated
                                    )
                                )
                            }
                        }
                    }
                }

                // Section 1: Appearance Customization
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = AppTheme.colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = Strings.current.appearanceSection,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textPrimary
                                )
                            }

                            // Theme Palette
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = Strings.current.themePaletteLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppTheme.colors.textSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AppThemePalette.values().forEach { palette ->
                                        val isSelected = themeSettings.palette == palette
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { viewModel.setThemePalette(palette) },
                                            leadingIcon = {
                                                Box(
                                                    modifier = Modifier
                                                        .size(14.dp)
                                                        .clip(CircleShape)
                                                        .background(palette.previewColor)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    if (isArabic) palette.displayNameArabic else palette.displayNameEnglish,
                                                    fontSize = 12.sp
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AppTheme.colors.primary,
                                                selectedLabelColor = Color.White,
                                                containerColor = AppTheme.colors.cardElevated,
                                                labelColor = AppTheme.colors.textSecondary
                                            ),
                                            border = FilterChipDefaults.filterChipBorder(
                                                enabled = true,
                                                selected = isSelected,
                                                borderColor = AppTheme.colors.highlight,
                                                selectedBorderColor = AppTheme.colors.primary
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }
                            }

                            // Lighting Mode (Dark / Light)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = Strings.current.lightingModeLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppTheme.colors.textSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val isDark = themeSettings.mode == ThemeMode.DARK
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isDark) AppTheme.colors.primary else AppTheme.colors.cardElevated)
                                            .clickable { viewModel.setThemeMode(ThemeMode.DARK) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DarkMode,
                                                contentDescription = null,
                                                tint = if (isDark) Color.White else AppTheme.colors.textMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = Strings.current.darkModeLabel,
                                                color = if (isDark) Color.White else AppTheme.colors.textSecondary,
                                                fontWeight = if (isDark) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    val isLight = themeSettings.mode == ThemeMode.LIGHT
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isLight) AppTheme.colors.primary else AppTheme.colors.cardElevated)
                                            .clickable { viewModel.setThemeMode(ThemeMode.LIGHT) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LightMode,
                                                contentDescription = null,
                                                tint = if (isLight) Color.White else AppTheme.colors.textMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = Strings.current.lightModeLabel,
                                                color = if (isLight) Color.White else AppTheme.colors.textSecondary,
                                                fontWeight = if (isLight) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Card Density
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = Strings.current.cardDensityLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppTheme.colors.textSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val isComfortable = themeSettings.cardDensity == CardDensity.COMFORTABLE
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isComfortable) AppTheme.colors.primary else AppTheme.colors.cardElevated)
                                            .clickable { viewModel.setCardDensity(CardDensity.COMFORTABLE) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = Strings.current.densityComfortable,
                                            color = if (isComfortable) Color.White else AppTheme.colors.textSecondary,
                                            fontWeight = if (isComfortable) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                    }

                                    val isCompact = themeSettings.cardDensity == CardDensity.COMPACT
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isCompact) AppTheme.colors.primary else AppTheme.colors.cardElevated)
                                            .clickable { viewModel.setCardDensity(CardDensity.COMPACT) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = Strings.current.densityCompact,
                                            color = if (isCompact) Color.White else AppTheme.colors.textSecondary,
                                            fontWeight = if (isCompact) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            // Font Scale Preference
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = Strings.current.fontScaleLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppTheme.colors.textSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    FontScalePreference.values().forEach { scale ->
                                        val isSelected = themeSettings.fontScale == scale
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) AppTheme.colors.primary else AppTheme.colors.cardElevated)
                                                .clickable { viewModel.setFontScale(scale) }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isArabic) scale.displayNameArabic else scale.displayNameEnglish,
                                                color = if (isSelected) Color.White else AppTheme.colors.textSecondary,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Reset Theme Button
                            OutlinedButton(
                                onClick = { viewModel.resetThemeSettings() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = AppTheme.colors.textSecondary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.highlight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.current.resetThemeButton,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Section 2: Language & RTL
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = AppTheme.colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = Strings.current.languageSection,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textPrimary
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppTheme.colors.cardElevated)
                                    .clickable { showLanguageDialog = true }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = Strings.current.appLanguageLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = appLanguage.displayName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppTheme.colors.primary
                                    )
                                }

                                Text(
                                    text = Strings.current.layoutDirectionValue,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppTheme.colors.textMuted
                                )
                            }
                        }
                    }
                }

                // Section 3: Forgiving Streak & Momentum Explanation
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = Strings.current.forgivingStreakSection,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textPrimary
                                )
                            }

                            Text(
                                text = Strings.current.momentumExplanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.textSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // Section 4: Reminders
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = AppTheme.colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = Strings.current.remindersSection,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textPrimary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = Strings.current.dailyReminderLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = Strings.current.dailyReminderDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppTheme.colors.textMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Switch(
                                    checked = remindersEnabled,
                                    onCheckedChange = { viewModel.toggleReminders(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AppTheme.colors.primary,
                                        uncheckedThumbColor = AppTheme.colors.textMuted,
                                        uncheckedTrackColor = AppTheme.colors.cardElevated
                                    )
                                )
                            }

                            if (remindersEnabled) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AppTheme.colors.cardElevated)
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Alarm,
                                            contentDescription = null,
                                            tint = AppTheme.colors.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = Strings.current.reminderTimeLabel,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = reminderTime,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.primary,
                                            fontSize = 14.sp
                                        )

                                        Button(
                                            onClick = {
                                                val (h, m) = HabitReminderScheduler.parseTimeString(reminderTime)
                                                TimePickerDialog(
                                                    context,
                                                    { _, selectedHour, selectedMinute ->
                                                        val amPm = if (selectedHour < 12) "AM" else "PM"
                                                        val displayHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                                                        val formattedTime = String.format(Locale.US, "%02d:%02d %s", displayHour, selectedMinute, amPm)
                                                        viewModel.setReminderTime(formattedTime)
                                                    },
                                                    h,
                                                    m,
                                                    false
                                                ).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.primary),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text(text = Strings.current.editButton, fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }

                                Button(
                                    onClick = { viewModel.sendTestNotificationNow() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AppTheme.colors.cardElevated,
                                        contentColor = AppTheme.colors.primary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = Strings.current.testReminderBtn,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 5: Data Management
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = Strings.current.dataManagementSection,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )

                            // Reset Today Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = Strings.current.resetTodayLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = Strings.current.resetTodayDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppTheme.colors.textMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showResetTodayDialog = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = AppTheme.colors.primary
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.highlight)
                                ) {
                                    Text(text = Strings.current.resetTodayConfirmBtn, fontSize = 11.sp)
                                }
                            }

                            // Load Sample Habits
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = Strings.current.loadSampleHabitsLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = Strings.current.loadSampleHabitsDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppTheme.colors.textMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Button(
                                    onClick = { viewModel.loadSampleHabits() },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AppTheme.colors.primary
                                    )
                                ) {
                                    Text(text = Strings.current.loadBtn, fontSize = 11.sp, color = Color.White)
                                }
                            }

                            // Clear All Data
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = Strings.current.clearAllDataLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = Strings.current.clearAllDataDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppTheme.colors.textMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showClearAllDialog = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                                ) {
                                    Text(text = Strings.current.clearAllDataConfirmBtn, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // Section 6: About
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = Strings.current.appNameDisplay,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                        Text(
                            text = Strings.current.appVersionInfo,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Strings.current.appQuote,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.primary,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Language Switch Dialog
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = {
                    Text(
                        text = Strings.current.switchLanguageDialogTitle,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppLanguage.values().forEach { lang ->
                            val isSelected = appLanguage == lang
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) AppTheme.colors.primary.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable {
                                        viewModel.switchLanguage(lang)
                                        showLanguageDialog = false
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lang.displayName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textPrimary
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = AppTheme.colors.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text(Strings.current.cancel, color = AppTheme.colors.textSecondary)
                    }
                }
            )
        }

        // Reset Today Confirmation Dialog
        if (showResetTodayDialog) {
            AlertDialog(
                onDismissRequest = { showResetTodayDialog = false },
                title = {
                    Text(
                        text = Strings.current.resetTodayConfirmTitle,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                },
                text = {
                    Text(
                        text = Strings.current.resetTodayConfirmMessage,
                        color = AppTheme.colors.textSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetTodayProgress()
                            showResetTodayDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.primary)
                    ) {
                        Text(Strings.current.resetTodayConfirmBtn, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetTodayDialog = false }) {
                        Text(Strings.current.cancel, color = AppTheme.colors.textSecondary)
                    }
                }
            )
        }

        // Clear All Data Confirmation Dialog
        if (showClearAllDialog) {
            AlertDialog(
                onDismissRequest = { showClearAllDialog = false },
                title = {
                    Text(
                        text = Strings.current.clearAllDataConfirmTitle,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Text(
                        text = Strings.current.clearAllDataConfirmMessage,
                        color = AppTheme.colors.textSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAllData()
                            showClearAllDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(Strings.current.clearAllDataConfirmBtn, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearAllDialog = false }) {
                        Text(Strings.current.cancel, color = AppTheme.colors.textSecondary)
                    }
                }
            )
        }
    }
}
