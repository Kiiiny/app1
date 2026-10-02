package com.example.ui.statistics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyCompletionRate
import com.example.data.model.StatsPeriod
import com.example.ui.theme.AppTheme

@Composable
fun CompletionBarChart(
    rates: List<DailyCompletionRate>,
    period: StatsPeriod,
    selectedDay: DailyCompletionRate?,
    onDaySelected: (DailyCompletionRate) -> Unit,
    modifier: Modifier = Modifier
) {
    val isMonthly = period == StatsPeriod.MONTHLY
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = if (isMonthly) {
                Modifier
                    .fillMaxHeight()
                    .horizontalScroll(scrollState)
            } else {
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            },
            horizontalArrangement = if (isMonthly) Arrangement.spacedBy(8.dp) else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            rates.forEach { rate ->
                val isSelected = (selectedDay?.dateString == rate.dateString)

                ChartBarItem(
                    rate = rate,
                    isSelected = isSelected,
                    barWidth = if (isMonthly) 22.dp else 32.dp,
                    onClick = { onDaySelected(rate) }
                )
            }
        }
    }
}

@Composable
private fun ChartBarItem(
    rate: DailyCompletionRate,
    isSelected: Boolean,
    barWidth: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val heightFraction by animateFloatAsState(
        targetValue = (rate.completionPercentage / 100f).coerceIn(0.06f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "bar_height"
    )

    val barBrush = if (rate.isToday) {
        Brush.verticalGradient(
            colors = listOf(
                AppTheme.colors.primary,
                AppTheme.colors.primaryVariant
            )
        )
    } else if (rate.completionPercentage >= 80) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF10B981),
                Color(0xFF34D399)
            )
        )
    } else if (rate.completionPercentage > 0) {
        Brush.verticalGradient(
            colors = listOf(
                AppTheme.colors.primary.copy(alpha = 0.8f),
                AppTheme.colors.primary.copy(alpha = 0.5f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                AppTheme.colors.highlight,
                AppTheme.colors.highlight.copy(alpha = 0.5f)
            )
        )
    }

    Column(
        modifier = Modifier
            .width(barWidth)
            .fillMaxHeight()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Percentage label on top
        if (isSelected || rate.completionPercentage > 0) {
            Text(
                text = "${rate.completionPercentage}%",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textMuted
            )
            Spacer(modifier = Modifier.height(3.dp))
        }

        // The Bar
        Box(
            modifier = Modifier
                .width(if (isSelected) barWidth else barWidth - 4.dp)
                .fillMaxHeight(heightFraction * 0.75f)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                .background(barBrush)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Day label at bottom
        Text(
            text = rate.dayLabel,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = if (rate.isToday || isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (rate.isToday) AppTheme.colors.primary else if (isSelected) AppTheme.colors.textPrimary else AppTheme.colors.textSecondary,
            maxLines = 1
        )
    }
}
