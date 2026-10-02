package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            val prefs = context.getSharedPreferences("app_theme_preferences", Context.MODE_PRIVATE)
            val remindersEnabled = prefs.getBoolean("reminders_enabled", true)
            if (remindersEnabled) {
                val savedTime = prefs.getString("reminder_time", "08:00 AM") ?: "08:00 AM"
                val (hour, minute) = HabitReminderScheduler.parseTimeString(savedTime)
                HabitReminderScheduler.scheduleDailyReminder(context, hour, minute)
            }
        }
    }
}
