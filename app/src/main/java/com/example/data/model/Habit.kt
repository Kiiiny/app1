package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val frequency: String = "DAILY", // DAILY, WEEKLY_3, WEEKDAYS, WEEKENDS, CUSTOM
    val selectedDays: String = "0,1,2,3,4,5,6", // 0=Sunday ... 6=Saturday
    val iconName: String = "water",
    val colorHex: String = "#A855F7",
    val timeOfDay: String = "ANYTIME", // MORNING, AFTERNOON, EVENING, ANYTIME
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    // Habit Stacking fields (Atomic Habits)
    val anchorHabitId: Long? = null,
    val stackCue: String = "", // e.g. "بعد شرب قهوة الصباح مباشرة"
    // Health Integration fields
    val isHealthSynced: Boolean = false,
    val targetSteps: Int = 10000
)

@Entity(tableName = "habit_completions")
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val dateString: String, // format: "yyyy-MM-dd"
    val completedAt: Long = System.currentTimeMillis()
)

data class HabitItemUiState(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val currentStreak: Int,
    val totalCompletions: Int,
    val isStreakFrozen: Boolean = false,
    val momentumScore: Int = 100,
    val freezeAvailable: Boolean = true,
    val anchorHabitName: String? = null,
    val stackedHabitCount: Int = 0
)
