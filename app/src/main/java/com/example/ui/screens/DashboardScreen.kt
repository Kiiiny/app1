package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
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
import com.example.ui.DashboardStats
import com.example.ui.HabitFilter
import com.example.ui.HabitIcons
import com.example.ui.HabitViewModel
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.AppTheme
import com.example.ui.theme.CardDensity

@Composable
fun DashboardScreen(
    viewModel: HabitViewModel,
    onNavigateToAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val habitItems by viewModel.filteredHabitItems.collectAsStateWithLifecycle()
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val todaySteps by viewModel.todaySteps.collectAsStateWithLifecycle()
    val isVacationMode by viewModel.isVacationModeEnabled.collectAsStateWithLifecycle()
    val currentLanguage = LocalAppLanguage.current

    var habitToDelete by remember { mutableStateOf<HabitItemUiState?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Vacation Mode Banner
            if (isVacationMode) {
                item {
                    VacationModeBanner(
                        onDisable = { viewModel.toggleVacationMode(false) }
                    )
                }
            }

            // Header: Greeting and Localized Date
            item {
                DashboardHeader(
                    formattedDate = viewModel.getTodayFormattedDate(currentLanguage)
                )
            }

            // Hero Progress Card
            item {
                ProgressSummaryCard(
                    stats = stats
                )
            }

            // Health & Step Counter Tracker Card
            item {
                HealthStepsTrackerCard(
                    steps = todaySteps,
                    onRefresh = { viewModel.refreshSteps() },
                    onAddTestSteps = { viewModel.addTestSteps(it) }
                )
            }

            // Filter Chips (All, Pending, Completed)
            item {
                FilterChipsSection(
                    selectedFilter = selectedFilter,
                    stats = stats,
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
                        text = Strings.current.todayHabits,
                        style = MaterialTheme.typography.titleLarge,
                        color = AppTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${habitItems.size} ${Strings.current.habitsCountSuffix}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted
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
                        cardDensity = AppTheme.cardDensity,
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
                .padding(16.dp)
                .testTag("add_habit_fab"),
            containerColor = AppTheme.colors.primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = Strings.current.addHabitFab
                )
            },
            text = {
                Text(
                    text = Strings.current.addHabitFab,
                    fontWeight = FontWeight.Bold
                )
            }
        )

        // Delete Confirmation Dialog
        habitToDelete?.let { habitItem ->
            AlertDialog(
                onDismissRequest = { habitToDelete = null },
                title = {
                    Text(
                        text = Strings.current.deleteHabitTitle,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                },
                text = {
                    Text(
                        text = Strings.current.deleteHabitMessage(habitItem.habit.name),
                        color = AppTheme.colors.textSecondary
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
                        Text(Strings.current.confirmDelete, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { habitToDelete = null }
                    ) {
                        Text(Strings.current.cancel, color = AppTheme.colors.textSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun VacationModeBanner(
    onDisable: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B).copy(alpha = 0.15f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BeachAccess,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = Strings.current.vacationActiveBanner,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFEF08A),
                    lineHeight = 18.sp
                )
            }
            TextButton(
                onClick = onDisable,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("إيقاف", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DashboardHeader(
    formattedDate: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = Strings.current.greeting,
            style = MaterialTheme.typography.headlineMedium,
            color = AppTheme.colors.textPrimary,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.primary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun ProgressSummaryCard(
    stats: DashboardStats,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = stats.progressFraction,
        animationSpec = tween(durationMillis = 800),
        label = "progress_animation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("progress_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.card
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = Strings.current.todayProgress,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )

                Text(
                    text = if (stats.totalCount > 0) {
                        Strings.current.progressDetailFormat(stats.completedCount, stats.totalCount)
                    } else {
                        Strings.current.noHabitsYet
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary
                )

                if (stats.longestStreak > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFFF97316),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = Strings.current.longestStreakFormat(stats.longestStreak),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFF97316),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(76.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = AppTheme.colors.highlight,
                    strokeWidth = 7.dp,
                    strokeCap = StrokeCap.Round
                )

                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = AppTheme.colors.primary,
                    strokeWidth = 7.dp,
                    strokeCap = StrokeCap.Round
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${stats.progressPercentage}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppTheme.colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChipsSection(
    selectedFilter: HabitFilter,
    stats: DashboardStats,
    onFilterSelected: (HabitFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingCount = stats.totalCount - stats.completedCount

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == HabitFilter.ALL,
            onClick = { onFilterSelected(HabitFilter.ALL) },
            label = { Text(Strings.current.filterAllFormat(stats.totalCount)) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = AppTheme.colors.primary,
                selectedLabelColor = Color.White,
                containerColor = AppTheme.colors.card,
                labelColor = AppTheme.colors.textSecondary
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedFilter == HabitFilter.ALL,
                borderColor = AppTheme.colors.border,
                selectedBorderColor = AppTheme.colors.primary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("filter_all")
        )

        FilterChip(
            selected = selectedFilter == HabitFilter.PENDING,
            onClick = { onFilterSelected(HabitFilter.PENDING) },
            label = { Text(Strings.current.filterPendingFormat(pendingCount.coerceAtLeast(0))) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = AppTheme.colors.primary,
                selectedLabelColor = Color.White,
                containerColor = AppTheme.colors.card,
                labelColor = AppTheme.colors.textSecondary
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedFilter == HabitFilter.PENDING,
                borderColor = AppTheme.colors.border,
                selectedBorderColor = AppTheme.colors.primary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("filter_pending")
        )

        FilterChip(
            selected = selectedFilter == HabitFilter.COMPLETED,
            onClick = { onFilterSelected(HabitFilter.COMPLETED) },
            label = { Text(Strings.current.filterCompletedFormat(stats.completedCount)) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = AppTheme.colors.primary,
                selectedLabelColor = Color.White,
                containerColor = AppTheme.colors.card,
                labelColor = AppTheme.colors.textSecondary
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedFilter == HabitFilter.COMPLETED,
                borderColor = AppTheme.colors.border,
                selectedBorderColor = AppTheme.colors.primary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("filter_completed")
        )
    }
}

@Composable
fun HabitCardItem(
    itemState: HabitItemUiState,
    cardDensity: CardDensity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val habit = itemState.habit
    val isCompleted = itemState.isCompletedToday
    val habitColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (_: Exception) {
        AppTheme.colors.primary
    }

    val iconVector = HabitIcons.getIconById(habit.iconName)
    val isCompact = cardDensity == CardDensity.COMPACT
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_item_${habit.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) AppTheme.colors.cardElevated else AppTheme.colors.card
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isCompleted) 1.5.dp else 1.dp,
            color = if (isCompleted) habitColor.copy(alpha = 0.6f) else AppTheme.colors.border
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 10.dp else 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Checkbox Circle
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) habitColor else AppTheme.colors.highlight
                    )
                    .clickable { onToggle() }
                    .testTag("toggle_habit_${habit.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Habit Icon Pill
            Box(
                modifier = Modifier
                    .size(if (isCompact) 34.dp else 40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(habitColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = habitColor,
                    modifier = Modifier.size(if (isCompact) 18.dp else 22.dp)
                )
            }

            // Habit Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) AppTheme.colors.textMuted else AppTheme.colors.textPrimary,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Notes
                if (!isCompact && habit.notes.isNotBlank()) {
                    Text(
                        text = habit.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 11.sp
                    )
                }

                // Badges Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    // Time of Day
                    val timeOfDayText = when (habit.timeOfDay) {
                        "MORNING" -> Strings.current.timeMorning
                        "AFTERNOON" -> Strings.current.timeAfternoon
                        "EVENING" -> Strings.current.timeEvening
                        else -> null
                    }
                    if (timeOfDayText != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AppTheme.colors.highlight)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AppTheme.colors.textMuted,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = timeOfDayText,
                                style = MaterialTheme.typography.labelSmall,
                                color = AppTheme.colors.textSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Streak & Freeze Badge
                    if (itemState.isStreakFrozen) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0284C7).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${Strings.current.streakFrozenBadge} (${itemState.currentStreak})",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    } else if (itemState.currentStreak > 0) {
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

                    // Momentum Badge
                    if (itemState.momentumScore > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF59E0B).copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${itemState.momentumScore}% ${Strings.current.momentumLabel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFF59E0B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Habit Stacking Anchor Badge
                    if (itemState.anchorHabitName != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF6366F1).copy(alpha = 0.18f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = Color(0xFF818CF8),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "بعد: ${itemState.anchorHabitName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF818CF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Habit Stacking Trigger for others
                    if (itemState.stackedHabitCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFA855F7).copy(alpha = 0.18f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "محفز لـ ${itemState.stackedHabitCount} عادات 🔗",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC084FC),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Health / Step Counter Sync Badge
                    if (itemState.habit.isHealthSynced) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.18f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsWalk,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${itemState.habit.targetSteps} خطوة",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF34D399),
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
                        contentDescription = "Options",
                        tint = AppTheme.colors.textMuted
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(AppTheme.colors.surface)
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
                                    text = Strings.current.confirmDelete,
                                    color = MaterialTheme.colorScheme.error
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
fun HealthStepsTrackerCard(
    steps: Int,
    targetGoal: Int = 10000,
    onRefresh: () -> Unit,
    onAddTestSteps: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (steps.toFloat() / targetGoal.toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsWalk,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = Strings.current.stepsCardTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                        Text(
                            text = Strings.current.stepsCardSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = Strings.current.refreshSensor,
                            tint = AppTheme.colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Button(
                        onClick = { onAddTestSteps(1000) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981).copy(alpha = 0.2f),
                            contentColor = Color(0xFF10B981)
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = Strings.current.addTestSteps,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF10B981),
                trackColor = AppTheme.colors.highlight,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "👣 $steps من أصل $targetGoal خطوة ($percentage%)",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTheme.colors.textSecondary,
                    fontWeight = FontWeight.Medium
                )

                if (steps >= targetGoal) {
                    Text(
                        text = "تم تحقيق الهدف! 🏆",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
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
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.highlight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (filter) {
                        HabitFilter.ALL -> Icons.Default.Add
                        HabitFilter.COMPLETED -> Icons.Default.CheckCircle
                        HabitFilter.PENDING -> Icons.Default.Schedule
                    },
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = AppTheme.colors.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (filter) {
                    HabitFilter.ALL -> Strings.current.emptyAllTitle
                    HabitFilter.COMPLETED -> Strings.current.emptyCompletedTitle
                    HabitFilter.PENDING -> Strings.current.emptyPendingTitle
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (filter) {
                    HabitFilter.ALL -> Strings.current.emptyAllDesc
                    HabitFilter.COMPLETED -> Strings.current.emptyCompletedDesc
                    HabitFilter.PENDING -> Strings.current.emptyPendingDesc
                },
                style = MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
                textAlign = TextAlign.Center
            )

            if (filter == HabitFilter.ALL) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAddClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.colors.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(Strings.current.addFirstHabitButton, color = Color.White)
                }
            }
        }
    }
}
