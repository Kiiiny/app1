package com.example.ui.statistics

import com.example.data.model.DailyCompletionRate
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import com.example.data.model.PeriodStats
import com.example.data.model.StatsPeriod
import com.example.ui.localization.AppLanguage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object HabitStatsCalculator {

    fun calculatePeriodStats(
        period: StatsPeriod,
        habits: List<Habit>,
        allCompletions: List<HabitCompletion>,
        todayDateString: String,
        language: AppLanguage
    ): PeriodStats {
        val daysCount = if (period == StatsPeriod.WEEKLY) 7 else 30
        val isArabic = language.isRtl

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val shortDayFormat = SimpleDateFormat("E", if (isArabic) Locale("ar") else Locale.US)
        val shortDateFormat = SimpleDateFormat("d MMM", if (isArabic) Locale("ar") else Locale.US)

        val completionsByDate = allCompletions.groupBy { it.dateString }
        val calendar = Calendar.getInstance()
        try {
            calendar.time = dateFormat.parse(todayDateString) ?: Date()
        } catch (_: Exception) {
            calendar.time = Date()
        }

        val rates = mutableListOf<DailyCompletionRate>()
        var totalCompletionsInPeriod = 0

        for (offset in 0 until daysCount) {
            val c = calendar.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, -offset)
            val dateStr = dateFormat.format(c.time)
            val isToday = (offset == 0)

            val dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday, 6=Saturday
            val activeHabitsForDay = habits.filter { habit ->
                when (habit.frequency) {
                    "DAILY" -> true
                    "WEEKDAYS" -> dayOfWeek in 1..5
                    "WEEKENDS" -> dayOfWeek == 0 || dayOfWeek == 6
                    "WEEKLY_3" -> true
                    "CUSTOM" -> {
                        val days = habit.selectedDays.split(",").mapNotNull { it.trim().toIntOrNull() }
                        days.contains(dayOfWeek)
                    }
                    else -> true
                }
            }
            val targetCount = if (activeHabitsForDay.isNotEmpty()) activeHabitsForDay.size else habits.size.coerceAtLeast(1)

            val dayCompletions = completionsByDate[dateStr]?.size ?: 0
            totalCompletionsInPeriod += dayCompletions

            val pct = if (targetCount > 0) {
                ((dayCompletions.toFloat() / targetCount.toFloat()) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }

            val label = if (period == StatsPeriod.WEEKLY) {
                if (isToday) (if (isArabic) "اليوم" else "Today") else shortDayFormat.format(c.time)
            } else {
                if (isToday) (if (isArabic) "اليوم" else "Today") else shortDateFormat.format(c.time)
            }

            rates.add(
                DailyCompletionRate(
                    dateString = dateStr,
                    dayLabel = label,
                    completedCount = dayCompletions,
                    totalCount = targetCount,
                    completionPercentage = pct,
                    isToday = isToday
                )
            )
        }

        val chronologicalRates = rates.reversed()
        val avg = if (chronologicalRates.isNotEmpty()) {
            chronologicalRates.map { it.completionPercentage }.average().toInt()
        } else {
            0
        }

        val bestDay = chronologicalRates.maxByOrNull { it.completedCount }?.dayLabel
            ?: if (isArabic) "لا يوجد" else "None"

        return PeriodStats(
            period = period,
            rates = chronologicalRates,
            averageRate = avg,
            bestDayName = bestDay,
            totalHabitsCount = habits.size,
            totalCompletionsInPeriod = totalCompletionsInPeriod
        )
    }
}
