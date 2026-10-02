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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DailyCompletionRate
import com.example.data.model.StatsPeriod
import com.example.ui.HabitViewModel
import com.example.ui.components.HabitContributionHeatmap
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.Strings
import com.example.ui.statistics.CompletionBarChart
import com.example.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: HabitViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    val periodStats by viewModel.periodStats.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val allCompletions by viewModel.allCompletions.collectAsStateWithLifecycle()
    val isArabic = LocalAppLanguage.current.isRtl

    var selectedDay by remember { mutableStateOf<DailyCompletionRate?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = Strings.current.statsTitle,
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
                // Period Selector Tabs (Weekly / Monthly)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppTheme.colors.cardElevated)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val isWeekly = selectedPeriod == StatsPeriod.WEEKLY
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isWeekly) AppTheme.colors.primary else Color.Transparent)
                                .clickable {
                                    viewModel.setStatsPeriod(StatsPeriod.WEEKLY)
                                    selectedDay = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = null,
                                    tint = if (isWeekly) Color.White else AppTheme.colors.textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isArabic) "أسبوعي (7 أيام)" else "Weekly (7 Days)",
                                    fontWeight = if (isWeekly) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isWeekly) Color.White else AppTheme.colors.textSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        val isMonthly = selectedPeriod == StatsPeriod.MONTHLY
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isMonthly) AppTheme.colors.primary else Color.Transparent)
                                .clickable {
                                    viewModel.setStatsPeriod(StatsPeriod.MONTHLY)
                                    selectedDay = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = if (isMonthly) Color.White else AppTheme.colors.textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isArabic) "شهري (30 يوماً)" else "Monthly (30 Days)",
                                    fontWeight = if (isMonthly) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isMonthly) Color.White else AppTheme.colors.textSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Three Key Metric Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(34.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        progress = { 1f },
                                        modifier = Modifier.fillMaxSize(),
                                        color = AppTheme.colors.highlight,
                                        strokeWidth = 3.dp,
                                        strokeCap = StrokeCap.Round
                                    )
                                    CircularProgressIndicator(
                                        progress = { periodStats.averageRate / 100f },
                                        modifier = Modifier.fillMaxSize(),
                                        color = AppTheme.colors.primary,
                                        strokeWidth = 3.dp,
                                        strokeCap = StrokeCap.Round
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${periodStats.averageRate}%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = AppTheme.colors.textPrimary
                                )
                                Text(
                                    text = if (isArabic) "معدل الإنجاز" else "Avg. Rate",
                                    fontSize = 10.sp,
                                    color = AppTheme.colors.textMuted
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFBBF24).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = periodStats.bestDayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.textPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (isArabic) "أفضل يوم" else "Best Day",
                                    fontSize = 10.sp,
                                    color = AppTheme.colors.textMuted
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${periodStats.totalCompletionsInPeriod}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = AppTheme.colors.textPrimary
                                )
                                Text(
                                    text = if (isArabic) "مرات الإنجاز" else "Completed",
                                    fontSize = 10.sp,
                                    color = AppTheme.colors.textMuted
                                )
                            }
                        }
                    }
                }

                // Interactive Bar Chart Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
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
                                Column {
                                    Text(
                                        text = if (isArabic) "رسم بياني للإنجاز اليومي" else "Daily Completion Trend",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = if (isArabic) "اضغط على أي عمود لمعاينة التفاصيل" else "Tap any bar to inspect daily breakdown",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppTheme.colors.textMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ShowChart,
                                    contentDescription = null,
                                    tint = AppTheme.colors.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            CompletionBarChart(
                                rates = periodStats.rates,
                                period = selectedPeriod,
                                selectedDay = selectedDay,
                                onDaySelected = { selectedDay = it }
                            )
                        }
                    }
                }

                // Selected Day Details Card
                selectedDay?.let { day ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.primary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "${day.dayLabel} (${day.dateString})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = if (isArabic) {
                                            "أنجزت ${day.completedCount} من أصل ${day.totalCount} عادات مبرمجة"
                                        } else {
                                            "Completed ${day.completedCount} of ${day.totalCount} planned habits"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppTheme.colors.textSecondary
                                    )
                                }

                                Text(
                                    text = "${day.completionPercentage}%",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AppTheme.colors.primary
                                )
                            }
                        }
                    }
                }

                // GitHub-style Visual Consistency Heatmap
                item {
                    HabitContributionHeatmap(
                        completions = allCompletions
                    )
                }
            }
        }
    }
}
