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
import com.example.ui.HabitIcons
import com.example.ui.HabitViewModel
import com.example.ui.theme.BorderPurple
import com.example.ui.theme.BrightLilac
import com.example.ui.theme.DeepVioletSurface
import com.example.ui.theme.MidnightPurple
import com.example.ui.theme.RoyalPurpleCard
import com.example.ui.theme.RoyalPurpleElevated
import com.example.ui.theme.RoyalPurpleHighlight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VividPurple

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddHabitScreen(
    viewModel: HabitViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Back navigation support
    BackHandler {
        onNavigateBack()
    }

    var habitName by remember { mutableStateOf("") }
    var habitNameError by remember { mutableStateOf<String?>(null) }

    var selectedFrequency by remember { mutableStateOf("DAILY") }
    val selectedCustomDays = remember { mutableStateListOf(0, 1, 2, 3, 4, 5, 6) }

    var selectedIconId by remember { mutableStateOf("water") }
    var selectedColorHex by remember { mutableStateOf("#38BDF8") }
    var selectedTimeOfDay by remember { mutableStateOf("ANYTIME") }
    var notes by remember { mutableStateOf("") }

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
        "DAILY" to "يومي",
        "WEEKDAYS" to "أيام العمل",
        "WEEKENDS" to "عطلة الأسبوع",
        "WEEKLY_3" to "3 مرات أسبوعياً",
        "CUSTOM" to "أيام محددة"
    )

    val timeOfDayOptions = listOf(
        "ANYTIME" to "أي وقت",
        "MORNING" to "صباحاً",
        "AFTERNOON" to "ظهراً",
        "EVENING" to "مساءً"
    )

    val weekDays = listOf(
        0 to "الأحد",
        1 to "الإثنين",
        2 to "الثلاثاء",
        3 to "الأربعاء",
        4 to "الخميس",
        5 to "الجمعة",
        6 to "السبت"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightPurple)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar with back navigation
            TopAppBar(
                title = {
                    Text(
                        text = "إضافة عادة جديدة",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
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
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Section 1: Habit Name
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = RoyalPurpleCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPurple)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "اسم العادة *",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                textAlign = TextAlign.Start
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = habitName,
                                onValueChange = {
                                    habitName = it
                                    if (it.isNotBlank()) habitNameError = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("habit_name_input"),
                                placeholder = {
                                    Text(
                                        text = "مثال: قراءة 20 دقيقة، شرب الماء، مشي...",
                                        color = TextMutedLight,
                                        fontSize = 14.sp
                                    )
                                },
                                isError = habitNameError != null,
                                supportingText = {
                                    if (habitNameError != null) {
                                        Text(
                                            text = habitNameError ?: "",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VividPurple,
                                    unfocusedBorderColor = RoyalPurpleHighlight,
                                    focusedContainerColor = RoyalPurpleElevated,
                                    unfocusedContainerColor = RoyalPurpleElevated,
                                    focusedTextColor = TextPrimaryLight,
                                    unfocusedTextColor = TextPrimaryLight
                                )
                            )
                        }
                    }
                }

                // Section 2: Frequency Selector
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = RoyalPurpleCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPurple)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "تكرار العادة (التكرار) *",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                textAlign = TextAlign.Start
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                frequencyOptions.forEach { (key, label) ->
                                    FilterChip(
                                        selected = selectedFrequency == key,
                                        onClick = { selectedFrequency = key },
                                        label = { Text(label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = VividPurple,
                                            selectedLabelColor = TextPrimaryLight,
                                            containerColor = RoyalPurpleElevated,
                                            labelColor = TextSecondaryLight
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = selectedFrequency == key,
                                            borderColor = RoyalPurpleHighlight,
                                            selectedBorderColor = VividPurple
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }

                            // If CUSTOM, show days of week picker
                            if (selectedFrequency == "CUSTOM") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "حدد أيام التكرار:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryLight
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    weekDays.forEach { (dayIndex, dayLabel) ->
                                        val isSelected = selectedCustomDays.contains(dayIndex)
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) VividPurple else RoyalPurpleElevated
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isSelected) BrightLilac else RoyalPurpleHighlight,
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    if (isSelected) {
                                                        if (selectedCustomDays.size > 1) {
                                                            selectedCustomDays.remove(dayIndex)
                                                        }
                                                    } else {
                                                        selectedCustomDays.add(dayIndex)
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = dayLabel.take(1),
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) TextPrimaryLight else TextMutedLight,
                                                fontSize = 13.sp
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
                        colors = CardDefaults.cardColors(containerColor = RoyalPurpleCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPurple)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "وقت الممارسة المفضل",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                textAlign = TextAlign.Start
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                timeOfDayOptions.forEach { (key, label) ->
                                    FilterChip(
                                        modifier = Modifier.weight(1f),
                                        selected = selectedTimeOfDay == key,
                                        onClick = { selectedTimeOfDay = key },
                                        label = {
                                            Text(
                                                text = label,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth(),
                                                fontSize = 12.sp
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = VividPurple,
                                            selectedLabelColor = TextPrimaryLight,
                                            containerColor = RoyalPurpleElevated,
                                            labelColor = TextSecondaryLight
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = selectedTimeOfDay == key,
                                            borderColor = RoyalPurpleHighlight,
                                            selectedBorderColor = VividPurple
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 4: Icon Selector
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = RoyalPurpleCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPurple)
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
                                Text(
                                    text = "أيقونة العادة *",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryLight
                                )

                                val activeIcon = HabitIcons.allIcons.firstOrNull { it.id == selectedIconId }
                                if (activeIcon != null) {
                                    Text(
                                        text = activeIcon.nameArabic,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BrightLilac,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HabitIcons.allIcons.forEach { iconItem ->
                                    val isSelected = selectedIconId == iconItem.id
                                    val itemColor = HabitIcons.getColor(selectedColorHex)

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .width(72.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) RoyalPurpleHighlight else RoyalPurpleElevated
                                            )
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) itemColor else RoyalPurpleHighlight,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                selectedIconId = iconItem.id
                                                // Optional: pick default color of icon if not custom set
                                                selectedColorHex = iconItem.defaultColorHex
                                            }
                                            .padding(vertical = 10.dp, horizontal = 4.dp)
                                            .testTag("icon_${iconItem.id}")
                                    ) {
                                        Icon(
                                            imageVector = iconItem.icon,
                                            contentDescription = iconItem.nameArabic,
                                            tint = if (isSelected) itemColor else TextMutedLight,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = iconItem.nameArabic.split(" ").first(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) TextPrimaryLight else TextMutedLight,
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 5: Color Selector
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = RoyalPurpleCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPurple)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "لون تمييز العادة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                textAlign = TextAlign.Start
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(colorsList) { hex ->
                                    val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                                    val swatchColor = HabitIcons.getColor(hex)

                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(swatchColor)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) Color.White else BorderPurple,
                                                shape = CircleShape
                                            )
                                            .clickable { selectedColorHex = hex },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
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

                // Section 6: Notes (Optional)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = RoyalPurpleCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderPurple)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "ملاحظة أو دافع ملهم (اختياري)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                textAlign = TextAlign.Start
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = "اكتب كلمة تشجيعية أو تفاصيل تذكرك بأهمية هذه العادة...",
                                        color = TextMutedLight,
                                        fontSize = 13.sp
                                    )
                                },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VividPurple,
                                    unfocusedBorderColor = RoyalPurpleHighlight,
                                    focusedContainerColor = RoyalPurpleElevated,
                                    unfocusedContainerColor = RoyalPurpleElevated,
                                    focusedTextColor = TextPrimaryLight,
                                    unfocusedTextColor = TextPrimaryLight
                                )
                            )
                        }
                    }
                }

                // Section 7: Save & Cancel Buttons
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
                                    habitNameError = "يرجى إدخال اسم العادة أولاً"
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
                                        notes = notes
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("save_habit_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VividPurple
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "حفظ العادة",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextSecondaryLight
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalPurpleHighlight)
                        ) {
                            Text(
                                text = "إلغاء",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
