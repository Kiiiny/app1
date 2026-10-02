package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.HabitDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("app_theme_preferences", Context.MODE_PRIVATE)
        val isVacation = prefs.getBoolean("vacation_mode_enabled", false)
        if (isVacation) {
            // Respect vacation mode: suppress routine reminder
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = HabitDatabase.getDatabase(context)
                val allHabits = db.habitDao().getAllHabitsList()

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val todayDateString = dateFormat.format(Date())

                val completions = db.habitCompletionDao().getCompletionsListForDate(todayDateString)
                val completedHabitIds = completions.map { it.habitId }.toSet()

                val calendar = Calendar.getInstance()
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday

                val activeHabitsForToday = allHabits.filter { habit ->
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

                val pendingHabits = activeHabitsForToday.filter { !completedHabitIds.contains(it.id) }

                if (pendingHabits.isNotEmpty()) {
                    val habitNames = pendingHabits.map { it.name }
                    val message = "لديك ${pendingHabits.size} عادات متبقية لليوم لم تكتمل بعد. استمر في الحفاظ على سلسلتك!"

                    HabitNotificationHelper.showHabitReminderNotification(
                        context = context,
                        title = "✨ حان وقت إنجاز عاداتك اليومية!",
                        message = message,
                        pendingHabitsList = habitNames
                    )
                }

                val savedTime = prefs.getString("reminder_time", "08:00 AM") ?: "08:00 AM"
                val (hour, minute) = HabitReminderScheduler.parseTimeString(savedTime)
                HabitReminderScheduler.scheduleDailyReminder(context, hour, minute)
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }
}
