package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HabitIcons
import com.example.ui.HabitViewModel
import com.example.ui.localization.Strings
import com.example.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddHabitScreen(
    viewModel: HabitViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    val strings = Strings.current

    var habitName by remember { mutableStateOf("") }
    var habitNameError by remember { mutableStateOf<String?>(null) }

    var selectedFrequency by remember { mutableStateOf("DAILY") }
    val selectedCustomDays = remember { mutableStateListOf(0, 1, 2, 3, 4, 5, 6) }

    var selectedIconId by remember { mutableStateOf("water") }
    var selectedColorHex by remember { mutableStateOf("#38BDF8") }
    var selectedTimeOfDay by remember { mutableStateOf("ANYTIME") }
    var notes by remember { mutableStateOf("") }

    // Habit Stacking & Health Integration State
    val allExistingHabits by viewModel.allHabits.collectAsStateWithLifecycle()
    var isStackingEnabled by remember { mutableStateOf(false) }
    var selectedAnchorHabitId by remember { mutableStateOf<Long?>(null) }
    var stackCue by remember { mutableStateOf("") }

    var isHealthSyncEnabled by remember { mutableStateOf(false) }
    var targetSteps by remember { mutableStateOf(10000) }

    val colorsList = listOf(
        "#38BDF8", // Cyan Water
        "#C084FC", // Lilac Book
        "#FB7185", // Rose Fitness
        "#34D399", // Emerald Meditation
        "#FBBF24", // Amber Star
        "#818CF8", // Indigo Sleep
        "#EC4899", // Pink Heart
        "#A855F7"  // Vivid Purple
    )

    val frequencyOptions = listOf(
        "DAILY" to Strings.current.freqDaily,
        "WEEKDAYS" to Strings.current.freqWeekdays,
        "WEEKENDS" to Strings.current.freqWeekends,
        "WEEKLY_3" to Strings.current.freqWeekly3,
        "CUSTOM" to Strings.current.freqCustom
    )

    val timeOfDayOptions = listOf(
        "ANYTIME" to Strings.current.timeAnytime,
        "MORNING" to Strings.current.timeMorning,
        "AFTERNOON" to Strings.current.timeAfternoon,
        "EVENING" to Strings.current.timeEvening
    )

    val weekDays = Strings.current.daysOfWeekLabels.mapIndexed { index, label -> index to label }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = Strings.current.addHabitTitle,
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
                // Section 1: Habit Name
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
                                .padding(16.dp)
                        ) {
                            Text(
                                text = Strings.current.habitNameLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = habitName,
                                onValueChange = {
                                    habitName = it
                                    if (habitNameError != null) habitNameError = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("habit_name_input"),
                                placeholder = {
                                    Text(
                                        text = Strings.current.habitNamePlaceholder,
                                        color = AppTheme.colors.textMuted
                                    )
                                },
                                isError = habitNameError != null,
                                supportingText = {
                                    habitNameError?.let {
                                        Text(text = it, color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppTheme.colors.primary,
                                    unfocusedBorderColor = AppTheme.colors.highlight,
                                    focusedContainerColor = AppTheme.colors.cardElevated,
                                    unfocusedContainerColor = AppTheme.colors.cardElevated,
                                    focusedTextColor = AppTheme.colors.textPrimary,
                                    unfocusedTextColor = AppTheme.colors.textPrimary
                                )
                            )
                        }
                    }
                }

                // Section 2: Frequency
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
                                .padding(16.dp)
                        ) {
                            Text(
                                text = Strings.current.frequencyLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                frequencyOptions.forEach { (key, label) ->
                                    val isSelected = selectedFrequency == key
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedFrequency = key },
                                        label = { Text(label) },
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

                            if (selectedFrequency == "CUSTOM") {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = Strings.current.selectDaysTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    weekDays.forEach { (index, label) ->
                                        val isDaySelected = selectedCustomDays.contains(index)
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isDaySelected) AppTheme.colors.primary else AppTheme.colors.cardElevated
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isDaySelected) AppTheme.colors.primary else AppTheme.colors.highlight,
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    if (isDaySelected) {
                                                        if (selectedCustomDays.size > 1) {
                                                            selectedCustomDays.remove(index)
                                                        }
                                                    } else {
                                                        selectedCustomDays.add(index)
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label.take(2),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (isDaySelected) Color.White else AppTheme.colors.textSecondary,
                                                fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 3: Time of Day
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
                                .padding(16.dp)
                        ) {
                            Text(
                                text = Strings.current.timeOfDayLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                timeOfDayOptions.forEach { (key, label) ->
                                    val isSelected = selectedTimeOfDay == key
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedTimeOfDay = key },
                                        label = { Text(label) },
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
                    }
                }

                // Section 4: Icon Selection
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
                                .padding(16.dp)
                        ) {
                            Text(
                                text = Strings.current.habitIconLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(HabitIcons.allIcons) { iconItem ->
                                    val isSelected = selectedIconId == iconItem.id
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.clickable { selectedIconId = iconItem.id }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    if (isSelected) AppTheme.colors.primary else AppTheme.colors.cardElevated
                                                )
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.highlight,
                                                    shape = RoundedCornerShape(14.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = iconItem.icon,
                                                contentDescription = iconItem.nameArabic,
                                                tint = if (isSelected) Color.White else AppTheme.colors.textSecondary,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = iconItem.nameArabic,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 5: Color Accent
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
                                .padding(16.dp)
                        ) {
                            Text(
                                text = Strings.current.habitColorLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(colorsList) { hexColor ->
                                    val isSelected = selectedColorHex.equals(hexColor, ignoreCase = true)
                                    val parsedColor = Color(android.graphics.Color.parseColor(hexColor))

                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(parsedColor)
                                            .border(
                                                width = if (isSelected) 3.dp else 0.dp,
                                                color = if (isSelected) Color.White else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable { selectedColorHex = hexColor },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 6: Notes
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
                                .padding(16.dp)
                        ) {
                            Text(
                                text = Strings.current.notesLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = Strings.current.notesPlaceholder,
                                        color = AppTheme.colors.textMuted,
                                        fontSize = 13.sp
                                    )
                                },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppTheme.colors.primary,
                                    unfocusedBorderColor = AppTheme.colors.highlight,
                                    focusedContainerColor = AppTheme.colors.cardElevated,
                                    unfocusedContainerColor = AppTheme.colors.cardElevated,
                                    focusedTextColor = AppTheme.colors.textPrimary,
                                    unfocusedTextColor = AppTheme.colors.textPrimary
                                )
                            )
                        }
                    }
                }

                // Section 7: Habit Stacking (Atomic Habits)
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
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = null,
                                        tint = Color(0xFF818CF8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = Strings.current.habitStackingTitle,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                        Text(
                                            text = Strings.current.habitStackingDesc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AppTheme.colors.textMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = isStackingEnabled,
                                    onCheckedChange = { isStackingEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF6366F1),
                                        uncheckedThumbColor = AppTheme.colors.textMuted,
                                        uncheckedTrackColor = AppTheme.colors.cardElevated
                                    )
                                )
                            }

                            if (isStackingEnabled) {
                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = Strings.current.anchorHabitLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AppTheme.colors.textSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                if (allExistingHabits.isEmpty()) {
                                    Text(
                                        text = "لا توجد عادات مسجلة مسبقاً للربط بها. ستكون هذه عادتك الأولى!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppTheme.colors.textMuted
                                    )
                                } else {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(allExistingHabits) { habit ->
                                            val isSelected = selectedAnchorHabitId == habit.id
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = {
                                                    selectedAnchorHabitId = if (isSelected) null else habit.id
                                                    if (!isSelected && stackCue.isEmpty()) {
                                                        stackCue = "بعد ${habit.name} مباشرة ✨"
                                                    }
                                                },
                                                label = { Text("بعد: ${habit.name}") },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Color(0xFF6366F1),
                                                    selectedLabelColor = Color.White,
                                                    containerColor = AppTheme.colors.cardElevated,
                                                    labelColor = AppTheme.colors.textSecondary
                                                ),
                                                border = FilterChipDefaults.filterChipBorder(
                                                    enabled = true,
                                                    selected = isSelected,
                                                    borderColor = AppTheme.colors.highlight,
                                                    selectedBorderColor = Color(0xFF6366F1)
                                                ),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = Strings.current.stackCueLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = AppTheme.colors.textSecondary,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    OutlinedTextField(
                                        value = stackCue,
                                        onValueChange = { stackCue = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = {
                                            Text(
                                                text = Strings.current.stackCuePlaceholder,
                                                color = AppTheme.colors.textMuted,
                                                fontSize = 12.sp
                                            )
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF6366F1),
                                            unfocusedBorderColor = AppTheme.colors.highlight,
                                            focusedContainerColor = AppTheme.colors.cardElevated,
                                            unfocusedContainerColor = AppTheme.colors.cardElevated,
                                            focusedTextColor = AppTheme.colors.textPrimary,
                                            unfocusedTextColor = AppTheme.colors.textPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 8: Health & Step Counter Sync
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
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsWalk,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = Strings.current.healthSyncTitle,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                        Text(
                                            text = Strings.current.healthSyncDesc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AppTheme.colors.textMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = isHealthSyncEnabled,
                                    onCheckedChange = { isHealthSyncEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF10B981),
                                        uncheckedThumbColor = AppTheme.colors.textMuted,
                                        uncheckedTrackColor = AppTheme.colors.cardElevated
                                    )
                                )
                            }

                            if (isHealthSyncEnabled) {
                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = Strings.current.targetStepsLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AppTheme.colors.textSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                val stepOptions = listOf(5000, 8000, 10000, 12000, 15000)
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(stepOptions) { stepsVal ->
                                        val isSelected = targetSteps == stepsVal
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { targetSteps = stepsVal },
                                            label = { Text("👣 $stepsVal خطوة") },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF10B981),
                                                selectedLabelColor = Color.White,
                                                containerColor = AppTheme.colors.cardElevated,
                                                labelColor = AppTheme.colors.textSecondary
                                            ),
                                            border = FilterChipDefaults.filterChipBorder(
                                                enabled = true,
                                                selected = isSelected,
                                                borderColor = AppTheme.colors.highlight,
                                                selectedBorderColor = Color(0xFF10B981)
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 9: Save & Cancel Buttons
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (habitName.trim().isEmpty()) {
                                    habitNameError = strings.habitNameError
                                } else {
                                    val daysStr = if (selectedFrequency == "CUSTOM") {
                                        selectedCustomDays.sorted().joinToString(",")
                                    } else {
                                        "0,1,2,3,4,5,6"
                                    }
                                    viewModel.addNewHabit(
                                        name = habitName,
                                        frequency = selectedFrequency,
                                        selectedDays = daysStr,
                                        iconName = selectedIconId,
                                        colorHex = selectedColorHex,
                                        timeOfDay = selectedTimeOfDay,
                                        notes = notes,
                                        anchorHabitId = if (isStackingEnabled) selectedAnchorHabitId else null,
                                        stackCue = if (isStackingEnabled) stackCue else "",
                                        isHealthSynced = isHealthSyncEnabled,
                                        targetSteps = if (isHealthSyncEnabled) targetSteps else 10000
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("save_habit_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppTheme.colors.primary
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Strings.current.saveHabitButton,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AppTheme.colors.textSecondary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.highlight)
                        ) {
                            Text(
                                text = Strings.current.cancel,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
