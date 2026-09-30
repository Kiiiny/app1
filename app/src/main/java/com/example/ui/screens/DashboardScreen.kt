package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HabitItemUiState
import com.example.ui.HabitFilter
import com.example.ui.HabitIcons
import com.example.ui.HabitViewModel
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

@Composable
fun DashboardScreen(
    viewModel: HabitViewModel,
    onNavigateToAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val habitItems by viewModel.filteredHabitItems.collectAsStateWithLifecycle()
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    var habitToDelete by remember { mutableStateOf<HabitItemUiState?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightPurple)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Greeting and Arabic Date
            item {
                DashboardHeader(
                    arabicDate = viewModel.todayArabicDateDisplay
                )
            }

            // Hero Progress Card
            item {
                ProgressSummaryCard(
                    stats = stats
                )
            }

            // Filter Chips (الكل، المتبقية، المكتملة)
            item {
                FilterChipsSection(
                    currentFilter = selectedFilter,
                    totalCount = stats.totalCount,
                    completedCount = stats.completedCount,
                    onFilterSelected = { viewModel.setFilter(it) }
                )
            }

            // Habits Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "عادات اليوم",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimaryLight,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${habitItems.size} عادات",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedLight
                    )
                }
            }

            // Habits List or Empty State
            if (habitItems.isEmpty()) {
                item {
                    EmptyHabitsState(
                        filter = selectedFilter,
                        onAddClicked = onNavigateToAdd
                    )
                }
            } else {
                items(
                    items = habitItems,
                    key = { it.habit.id }
                ) { itemState ->
                    HabitCardItem(
                        itemState = itemState,
                        onToggle = { viewModel.toggleHabit(itemState.habit.id) },
                        onDelete = { habitToDelete = itemState }
                    )
                }
            }
        }

        // Floating Action Button to Add New Habit
        ExtendedFloatingActionButton(
            onClick = onNavigateToAdd,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
                .testTag("add_habit_fab"),
            containerColor = VividPurple,
            contentColor = TextPrimaryLight,
            elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(6.dp),
            shape = RoundedCornerShape(16.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة عادة"
                )
            },
            text = {
                Text(
                    text = "إضافة عادة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        )

        // Delete Confirmation Dialog
        habitToDelete?.let { habitItem ->
            AlertDialog(
                onDismissRequest = { habitToDelete = null },
                containerColor = DeepVioletSurface,
                titleContentColor = TextPrimaryLight,
                textContentColor = TextSecondaryLight,
                title = {
                    Text(
                        text = "حذف العادة",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    )
                },
                text = {
                    Text(
                        text = "هل أنت متأكد من رغبتك في حذف عادة \"${habitItem.habit.name}\"؟ سيتم مسح سجل إنجازاتها نهائياً.",
                        textAlign = TextAlign.Start
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteHabit(habitItem.habit.id)
                            habitToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("نعم، حذف", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { habitToDelete = null }
                    ) {
                        Text("إلغاء", color = TextSecondaryLight)
                    }
                }
            )
        }
    }
}

@Composable
fun DashboardHeader(
    arabicDate: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "مرحباً بك!",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryLight,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = arabicDate,
            style = MaterialTheme.typography.bodyMedium,
            color = BrightLilac,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun ProgressSummaryCard(
    stats: com.example.ui.DashboardStats,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = stats.progressFraction,
        label = "progress_animation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(RoyalPurpleHighlight, VividPurple.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = RoyalPurpleCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "إنجاز اليوم",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (stats.totalCount > 0) {
                            "أنجزت ${stats.completedCount} من أصل ${stats.totalCount} عادات"
                        } else {
                            "لم تضف عادات لليوم بعد"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryLight,
                        textAlign = TextAlign.Start
                    )
                }

                // Circular Percentage Badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = RoyalPurpleHighlight,
                        strokeWidth = 5.dp,
                        trackColor = Color.Transparent,
                        strokeCap = StrokeCap.Round
                    )
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = VividPurple,
                        strokeWidth = 5.dp,
                        trackColor = Color.Transparent,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "${stats.progressPercentage}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = LightLilac
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Linear Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = VividPurple,
                trackColor = RoyalPurpleHighlight,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Streak & Motivation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "السلسلة",
                        tint = Color(0xFFF97316),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "أعلى التزام: ${stats.longestStreak} أيام متتالية",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimaryLight,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (stats.completedCount == stats.totalCount && stats.totalCount > 0) {
                    Text(
                        text = "رائع! اكتملت جميع العادات ✨",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF34D399),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChipsSection(
    currentFilter: HabitFilter,
    totalCount: Int,
    completedCount: Int,
    onFilterSelected: (HabitFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingCount = totalCount - completedCount

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = currentFilter == HabitFilter.ALL,
                onClick = { onFilterSelected(HabitFilter.ALL) },
                label = { Text("الكل ($totalCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VividPurple,
                    selectedLabelColor = TextPrimaryLight,
                    containerColor = RoyalPurpleElevated,
                    labelColor = TextSecondaryLight
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = currentFilter == HabitFilter.ALL,
                    borderColor = RoyalPurpleHighlight,
                    selectedBorderColor = VividPurple
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
        item {
            FilterChip(
                selected = currentFilter == HabitFilter.PENDING,
                onClick = { onFilterSelected(HabitFilter.PENDING) },
                label = { Text("المتبقية ($pendingCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VividPurple,
                    selectedLabelColor = TextPrimaryLight,
                    containerColor = RoyalPurpleElevated,
                    labelColor = TextSecondaryLight
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = currentFilter == HabitFilter.PENDING,
                    borderColor = RoyalPurpleHighlight,
                    selectedBorderColor = VividPurple
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
        item {
            FilterChip(
                selected = currentFilter == HabitFilter.COMPLETED,
                onClick = { onFilterSelected(HabitFilter.COMPLETED) },
                label = { Text("المكتملة ($completedCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VividPurple,
                    selectedLabelColor = TextPrimaryLight,
                    containerColor = RoyalPurpleElevated,
                    labelColor = TextSecondaryLight
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = currentFilter == HabitFilter.COMPLETED,
                    borderColor = RoyalPurpleHighlight,
                    selectedBorderColor = VividPurple
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun HabitCardItem(
    itemState: HabitItemUiState,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val habit = itemState.habit
    val habitIcon = HabitIcons.getIcon(habit.iconName)
    val accentColor = HabitIcons.getColor(habit.colorHex)
    var showMenu by remember { mutableStateOf(false) }

    val frequencyText = when (habit.frequency) {
        "DAILY" -> "يومياً"
        "WEEKDAYS" -> "أيام العمل"
        "WEEKENDS" -> "عطلة الأسبوع"
        "WEEKLY_3" -> "3 مرات أسبوعياً"
        else -> "مخصص"
    }

    val timeOfDayText = when (habit.timeOfDay) {
        "MORNING" -> "صباحاً"
        "AFTERNOON" -> "ظهراً"
        "EVENING" -> "مساءً"
        else -> "في أي وقت"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_card_${habit.id}")
            .clickable { onToggle() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (itemState.isCompletedToday) {
                DeepVioletSurface
            } else {
                RoyalPurpleCard
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (itemState.isCompletedToday) 1.5.dp else 1.dp,
            color = if (itemState.isCompletedToday) accentColor.copy(alpha = 0.6f) else RoyalPurpleHighlight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interactive Checkbox Button (Custom Styled Circle)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (itemState.isCompletedToday) {
                            accentColor
                        } else {
                            RoyalPurpleElevated
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = if (itemState.isCompletedToday) accentColor else RoyalPurpleHighlight,
                        shape = CircleShape
                    )
                    .clickable { onToggle() }
                    .testTag("checkbox_${habit.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (itemState.isCompletedToday) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "مكتمل",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Habit Icon Badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(
                        width = 1.dp,
                        color = accentColor.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = habitIcon,
                    contentDescription = habit.name,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Habit Details Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (itemState.isCompletedToday) TextMutedLight else TextPrimaryLight,
                    textDecoration = if (itemState.isCompletedToday) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )

                if (habit.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = habit.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Badges Row (Frequency, Time, Streak)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Frequency Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(RoyalPurpleElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = frequencyText,
                            style = MaterialTheme.typography.labelSmall,
                            color = BrightLilac,
                            fontSize = 10.sp
                        )
                    }

                    // Time of Day Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(RoyalPurpleElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TextMutedLight,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = timeOfDayText,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryLight,
                            fontSize = 10.sp
                        )
                    }

                    // Streak Badge
                    if (itemState.currentStreak > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF97316).copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${itemState.currentStreak}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFF97316),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Options Menu (Delete)
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "خيارات العادة",
                        tint = TextMutedLight
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(DeepVioletSurface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "حذف العادة",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHabitsState(
    filter: HabitFilter,
    onAddClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = RoyalPurpleCard.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalPurpleHighlight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(RoyalPurpleElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = BrightLilac,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (filter) {
                    HabitFilter.ALL -> "لا توجد عادات مضافة بعد"
                    HabitFilter.COMPLETED -> "لم تكتمل أي عادة حتى الآن"
                    HabitFilter.PENDING -> "أحسنت! لا توجد عادات متبقية لليوم"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (filter) {
                    HabitFilter.ALL -> "ابدأ رحلة بناء عاداتك اليومية واستمر في التطوير المستمر"
                    HabitFilter.COMPLETED -> "اضغط على مربع العادة في القائمة عند إنجازها"
                    HabitFilter.PENDING -> "لقد أنجزت كل ما خططت له لهذا اليوم بامتياز!"
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedLight,
                textAlign = TextAlign.Center
            )

            if (filter == HabitFilter.ALL) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAddClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VividPurple
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("أضف عادتك الأولى")
                }
            }
        }
    }
}
