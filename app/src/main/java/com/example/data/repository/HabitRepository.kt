package com.example.data.repository

import com.example.data.local.HabitCompletionDao
import com.example.data.local.HabitDao
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import com.example.data.model.HabitItemUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao
) {

    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    fun getTodayHabitItems(todayDateString: String): Flow<List<HabitItemUiState>> {
        return combine(
            habitDao.getAllHabits(),
            completionDao.getCompletionsForDate(todayDateString),
            completionDao.getAllCompletions()
        ) { habits, todayCompletions, allCompletions ->
            val todayCompletedIds = todayCompletions.map { it.habitId }.toSet()
            val completionsByHabit = allCompletions.groupBy { it.habitId }
            val habitsMap = habits.associateBy { it.id }
            val stackedCounts = habits.groupBy { it.anchorHabitId }

            habits.map { habit ->
                val habitCompletions = completionsByHabit[habit.id] ?: emptyList()
                val isCompleted = todayCompletedIds.contains(habit.id)
                val streakResult = calculateForgivingStreak(habitCompletions, todayDateString, isCompleted)

                val anchorName = habit.anchorHabitId?.let { habitsMap[it]?.name }
                val stackedCount = stackedCounts[habit.id]?.size ?: 0

                HabitItemUiState(
                    habit = habit,
                    isCompletedToday = isCompleted,
                    currentStreak = streakResult.streak,
                    totalCompletions = habitCompletions.size,
                    isStreakFrozen = streakResult.isFrozen,
                    momentumScore = streakResult.momentumScore,
                    freezeAvailable = true,
                    anchorHabitName = anchorName,
                    stackedHabitCount = stackedCount
                )
            }
        }
    }

    data class ForgivingStreakResult(
        val streak: Int,
        val isFrozen: Boolean,
        val momentumScore: Int
    )

    private fun calculateForgivingStreak(
        completions: List<HabitCompletion>,
        todayDateString: String,
        isCompletedToday: Boolean
    ): ForgivingStreakResult {
        if (completions.isEmpty()) {
            return ForgivingStreakResult(0, false, 0)
        }

        val completedDates = completions.map { it.dateString }.toSet()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val calendar = Calendar.getInstance()
        try {
            calendar.time = dateFormat.parse(todayDateString) ?: Date()
        } catch (_: Exception) {
            calendar.time = Date()
        }

        var streak = 0
        var isFrozen = false
        var usedFreeze = false

        if (isCompletedToday) {
            streak = 1
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = dateFormat.format(calendar.time)
            if (completedDates.contains(yesterdayStr)) {
                streak = 1
                isFrozen = true
                usedFreeze = true
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                val momentum = calculateMomentum(completedDates, todayDateString, isCompletedToday)
                return ForgivingStreakResult(0, false, momentum)
            }
        }

        while (true) {
            val dateStr = dateFormat.format(calendar.time)
            if (completedDates.contains(dateStr)) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                if (!usedFreeze && streak > 0) {
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                    val beforeMissDateStr = dateFormat.format(calendar.time)
                    if (completedDates.contains(beforeMissDateStr)) {
                        usedFreeze = true
                        isFrozen = true
                        streak++
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                        continue
                    }
                }
                break
            }
        }

        val momentum = calculateMomentum(completedDates, todayDateString, isCompletedToday)
        return ForgivingStreakResult(streak, isFrozen, momentum)
    }

    private fun calculateMomentum(
        completedDates: Set<String>,
        todayDateString: String,
        isCompletedToday: Boolean
    ): Int {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        try {
            calendar.time = dateFormat.parse(todayDateString) ?: Date()
        } catch (_: Exception) {
            calendar.time = Date()
        }

        var completionsInLast7Days = if (isCompletedToday) 1 else 0
        for (i in 1..6) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val dStr = dateFormat.format(calendar.time)
            if (completedDates.contains(dStr)) {
                completionsInLast7Days++
            }
        }

        val basePercentage = (completionsInLast7Days * 100) / 7
        val momentum = if (isCompletedToday) {
            (basePercentage + 15).coerceAtMost(100)
        } else {
            basePercentage.coerceAtLeast(15)
        }
        return momentum.coerceIn(15, 100)
    }

    suspend fun toggleHabitCompletion(habitId: Long, dateString: String): Boolean {
        val existing = completionDao.getCompletion(habitId, dateString)
        return if (existing != null) {
            completionDao.deleteCompletion(habitId, dateString)
            false
        } else {
            completionDao.insertCompletion(
                HabitCompletion(
                    habitId = habitId,
                    dateString = dateString
                )
            )
            true
        }
    }

    suspend fun insertHabit(habit: Habit): Long = habitDao.insertHabit(habit)

    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)

    suspend fun deleteHabit(habitId: Long) {
        completionDao.deleteAllCompletionsForHabit(habitId)
        habitDao.deleteHabitById(habitId)
    }

    suspend fun resetTodayProgress(todayDateString: String) {
        completionDao.deleteCompletionsForDate(todayDateString)
    }

    suspend fun clearAllData() {
        completionDao.deleteAllCompletions()
        habitDao.deleteAllHabits()
    }

    suspend fun seedSampleHabitsIfEmpty() {
        val count = habitDao.getHabitsCount()
        if (count == 0) {
            insertSampleHabits()
        }
    }

    suspend fun insertSampleHabits() {
        val habit1Id = habitDao.insertHabit(
            Habit(
                name = "قهوة الصباح والاسترخاء",
                frequency = "DAILY",
                iconName = "coffee",
                colorHex = "#F59E0B",
                timeOfDay = "MORNING",
                notes = "بداية اليوم بهدوء واستعداد ذهني"
            )
        )

        val habit2Id = habitDao.insertHabit(
            Habit(
                name = "قراءة 20 دقيقة",
                frequency = "DAILY",
                iconName = "book",
                colorHex = "#C084FC",
                timeOfDay = "MORNING",
                notes = "قراءة كتب مفيدة وتطوير الذات",
                anchorHabitId = habit1Id,
                stackCue = "بعد شرب قهوة الصباح مباشرة ☕"
            )
        )

        val habit3Id = habitDao.insertHabit(
            Habit(
                name = "المشي اليومي (10,000 خطوة)",
                frequency = "DAILY",
                iconName = "walk",
                colorHex = "#10B981",
                timeOfDay = "ANYTIME",
                notes = "يتكامل تلقائياً مع حساس خطوات الهاتف والصحة",
                isHealthSynced = true,
                targetSteps = 10000
            )
        )

        val habit4Id = habitDao.insertHabit(
            Habit(
                name = "شرب 2 لتر من الماء",
                frequency = "DAILY",
                iconName = "water",
                colorHex = "#38BDF8",
                timeOfDay = "ANYTIME",
                notes = "الحفاظ على ترطيب الجسم والنشاط"
            )
        )

        val habit5Id = habitDao.insertHabit(
            Habit(
                name = "أذكار وتأمل صباحي",
                frequency = "DAILY",
                iconName = "star",
                colorHex = "#EC4899",
                timeOfDay = "MORNING",
                notes = "راحة للقلب وطمأنينة للنفس"
            )
        )

        val insertedIds = listOf(habit1Id, habit2Id, habit3Id, habit4Id, habit5Id)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (dayOffset in 0..14) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -dayOffset)
            val dateStr = dateFormat.format(c.time)
            insertedIds.forEachIndexed { idx, id ->
                if ((dayOffset + idx) % 4 != 0) {
                    completionDao.insertCompletion(HabitCompletion(habitId = id, dateString = dateStr))
                }
            }
        }
    }

    fun getAllCompletions(): Flow<List<HabitCompletion>> = completionDao.getAllCompletions()
}
