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
    val createdAt: Long = System.currentTimeMillis()
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
    val totalCompletions: Int
)
