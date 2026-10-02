package com.example.data.model

enum class StatsPeriod {
    WEEKLY,
    MONTHLY
}

data class DailyCompletionRate(
    val dateString: String,
    val dayLabel: String,
    val completedCount: Int,
    val totalCount: Int,
    val completionPercentage: Int,
    val isToday: Boolean = false
)

data class PeriodStats(
    val period: StatsPeriod,
    val rates: List<DailyCompletionRate>,
    val averageRate: Int,
    val bestDayName: String,
    val totalHabitsCount: Int,
    val totalCompletionsInPeriod: Int
)
