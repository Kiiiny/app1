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

            habits.map { habit ->
                val habitCompletions = completionsByHabit[habit.id] ?: emptyList()
                val isCompleted = todayCompletedIds.contains(habit.id)
                val streak = calculateStreak(habitCompletions, todayDateString, isCompleted)

                HabitItemUiState(
                    habit = habit,
                    isCompletedToday = isCompleted,
                    currentStreak = streak,
                    totalCompletions = habitCompletions.size
                )
            }
        }
    }

    private fun calculateStreak(
        completions: List<HabitCompletion>,
        todayDateString: String,
        isCompletedToday: Boolean
    ): Int {
        if (completions.isEmpty()) return 0

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val completedDates = completions.map { it.dateString }.toSet()

        var streak = 0
        val calendar = Calendar.getInstance()

        try {
            val todayDate = dateFormat.parse(todayDateString) ?: Date()
            calendar.time = todayDate

            // If not completed today, start checking from yesterday
            if (!isCompletedToday) {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }

            while (true) {
                val checkDateStr = dateFormat.format(calendar.time)
                if (completedDates.contains(checkDateStr)) {
                    streak++
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }
        } catch (_: Exception) {
            return if (isCompletedToday) 1 else 0
        }

        return streak
    }

    suspend fun toggleHabitCompletion(habitId: Long, dateString: String) {
        val existing = completionDao.getCompletion(habitId, dateString)
        if (existing != null) {
            completionDao.deleteCompletion(habitId, dateString)
        } else {
            completionDao.insertCompletion(
                HabitCompletion(
                    habitId = habitId,
                    dateString = dateString
                )
            )
        }
    }

    suspend fun insertHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabitById(habitId)
        completionDao.deleteCompletionsForHabit(habitId)
    }

    suspend fun resetTodayCompletions(todayDateString: String) {
        completionDao.deleteCompletionsForDate(todayDateString)
    }

    suspend fun clearAllData() {
        completionDao.deleteAllCompletions()
        habitDao.deleteAllHabits()
    }

    suspend fun seedSampleHabitsIfEmpty() {
        val count = habitDao.getHabitCount()
        if (count == 0) {
            insertSampleHabits()
        }
    }

    suspend fun insertSampleHabits() {
        val samples = listOf(
            Habit(
                name = "شرب 2 لتر من الماء",
                frequency = "DAILY",
                iconName = "water",
                colorHex = "#38BDF8",
                timeOfDay = "ANYTIME",
                notes = "الحفاظ على ترطيب الجسم والنشاط طوال اليوم"
            ),
            Habit(
                name = "قراءة 20 دقيقة",
                frequency = "DAILY",
                iconName = "book",
                colorHex = "#C084FC",
                timeOfDay = "EVENING",
                notes = "قراءة كتب مفيدة وتطوير المعرفة الذاتية"
            ),
            Habit(
                name = "تمارين رياضية صباحية",
                frequency = "DAILY",
                iconName = "fitness",
                colorHex = "#FB7185",
                timeOfDay = "MORNING",
                notes = "نشاط خفيف وتمارين تمدد لبداية يوم مليء بالطاقة"
            ),
            Habit(
                name = "جلسة تأمل وتنفس عميق",
                frequency = "WEEKDAYS",
                iconName = "meditation",
                colorHex = "#34D399",
                timeOfDay = "MORNING",
                notes = "10 دقائق من الصفاء الذهني وتخفيف التوتر"
            ),
            Habit(
                name = "أذكار وتأمل يومي",
                frequency = "DAILY",
                iconName = "star",
                colorHex = "#FBBF24",
                timeOfDay = "MORNING",
                notes = "راحة للقلب وطمأنينة للنفس"
            )
        )
        samples.forEach { habitDao.insertHabit(it) }
    }

    fun getAllCompletions(): Flow<List<HabitCompletion>> = completionDao.getAllCompletions()
}
