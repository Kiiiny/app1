package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitCompletion
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HabitContributionHeatmap(
    completions: List<HabitCompletion>,
    weeksCount: Int = 18,
    modifier: Modifier = Modifier
) {
    val isArabic = LocalAppLanguage.current.isRtl
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val displayDateFormat = remember(isArabic) {
        SimpleDateFormat("d MMMM yyyy", if (isArabic) Locale("ar") else Locale.US)
    }

    val completionsPerDate = remember(completions) {
        completions.groupBy { it.dateString }.mapValues { it.value.size }
    }

    var selectedDateInfo by remember { mutableStateOf<String?>(null) }

    val gridWeeks = remember(completionsPerDate, weeksCount) {
        val weeks = mutableListOf<List<DayHeatData>>()
        val calendar = Calendar.getInstance()

        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday
        calendar.add(Calendar.DAY_OF_YEAR, -((weeksCount - 1) * 7 + currentDayOfWeek))

        for (w in 0 until weeksCount) {
            val weekDays = mutableListOf<DayHeatData>()
            for (d in 0..6) {
                val date = calendar.time
                val dateStr = dateFormat.format(date)
                val count = completionsPerDate[dateStr] ?: 0
                weekDays.add(
                    DayHeatData(
                        dateString = dateStr,
                        displayDate = displayDateFormat.format(date),
                        count = count,
                        dayOfWeek = d
                    )
                )
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            weeks.add(weekDays)
        }
        weeks
    }

    val scrollState = rememberScrollState()

    Card(
        modifier = modifier.fillMaxWidth(),
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
                Column {
                    Text(
                        text = if (isArabic) "🟩 خريطة الاستمرارية السنوية (Heatmap)" else "🟩 Consistency Heatmap (GitHub Style)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                    Text(
                        text = if (isArabic) "سجل بصري يومي لكثافة إنجاز عاداتك (لا تكسر السلسلة)" else "Daily visual density record (Don't break the chain)",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                gridWeeks.forEach { week ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        week.forEach { dayData ->
                            val cellColor = getHeatColor(dayData.count)
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(cellColor)
                                    .border(
                                        width = 0.5.dp,
                                        color = if (dayData.count > 0) Color.Transparent else AppTheme.colors.highlight,
                                        shape = RoundedCornerShape(3.dp)
                                    )
                                    .clickable {
                                        selectedDateInfo = if (isArabic) {
                                            "${dayData.displayDate}: ${dayData.count} عادات مكتملة 🎯"
                                        } else {
                                            "${dayData.displayDate}: ${dayData.count} habits completed 🎯"
                                        }
                                    }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedDateInfo != null) {
                    Text(
                        text = selectedDateInfo ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = if (isArabic) "اضغط على أي مربع للتفاصيل" else "Tap any square for details",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted,
                        fontSize = 10.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = if (isArabic) "أقل" else "Less",
                        fontSize = 9.sp,
                        color = AppTheme.colors.textMuted
                    )
                    Box(modifier = Modifier.size(9.dp).clip(RoundedCornerShape(2.dp)).background(AppTheme.colors.cardElevated))
                    Box(modifier = Modifier.size(9.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF064E3B)))
                    Box(modifier = Modifier.size(9.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF059669)))
                    Box(modifier = Modifier.size(9.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF10B981)))
                    Box(modifier = Modifier.size(9.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF34D399)))
                    Text(
                        text = if (isArabic) "أكثر" else "More",
                        fontSize = 9.sp,
                        color = AppTheme.colors.textMuted
                    )
                }
            }
        }
    }
}

private fun getHeatColor(count: Int): Color {
    return when {
        count == 0 -> Color(0xFF1E293B).copy(alpha = 0.6f)
        count == 1 -> Color(0xFF064E3B)
        count in 2..3 -> Color(0xFF059669)
        count in 4..5 -> Color(0xFF10B981)
        else -> Color(0xFF34D399)
    }
}

data class DayHeatData(
    val dateString: String,
    val displayDate: String,
    val count: Int,
    val dayOfWeek: Int
)
