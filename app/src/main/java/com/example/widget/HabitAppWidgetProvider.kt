package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.HabitDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HabitAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET ||
            intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE
        ) {
            updateAllWidgets(context)
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.ACTION_REFRESH_WIDGET"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, HabitAppWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

            CoroutineScope(Dispatchers.IO).launch {
                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            }
        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = HabitDatabase.getDatabase(context)
                    val allHabits = db.habitDao().getAllHabitsList()

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val todayDate = dateFormat.format(Date())

                    val completions = db.habitCompletionDao().getCompletionsListForDate(todayDate)
                    val completedIds = completions.map { it.habitId }.toSet()

                    val calendar = Calendar.getInstance()
                    val dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday ... 6=Saturday

                    val todayHabits = allHabits.filter { habit ->
                        when (habit.frequency) {
                            "DAILY" -> true
                            "WEEKDAYS" -> dayIndex in 1..5
                            "WEEKENDS" -> dayIndex == 0 || dayIndex == 6
                            "CUSTOM" -> {
                                val days = habit.selectedDays.split(",").mapNotNull { it.trim().toIntOrNull() }
                                days.contains(dayIndex)
                            }
                            else -> true
                        }
                    }

                    val totalCount = todayHabits.size
                    val completedCount = todayHabits.count { completedIds.contains(it.id) }
                    val percent = if (totalCount > 0) (completedCount * 100) / totalCount else 0

                    val views = RemoteViews(context.packageName, R.layout.widget_habit_tracker)

                    views.setTextViewText(R.id.widget_percent, "$percent%")
                    views.setProgressBar(R.id.widget_progress_bar, 100, percent, false)
                    views.setTextViewText(
                        R.id.widget_stats_text,
                        "$completedCount من $totalCount عادات مكتملة اليوم"
                    )

                    // Top 3 Habits preview
                    val topHabits = todayHabits.take(3)

                    // Row 1
                    if (topHabits.isNotEmpty()) {
                        views.setViewVisibility(R.id.widget_habit_row_1, View.VISIBLE)
                        val h1 = topHabits[0]
                        val isDone1 = completedIds.contains(h1.id)
                        views.setTextViewText(R.id.widget_habit_name_1, "• ${h1.name}")
                        views.setTextViewText(R.id.widget_habit_status_1, if (isDone1) "✅" else "⭕")
                    } else {
                        views.setViewVisibility(R.id.widget_habit_row_1, View.GONE)
                    }

                    // Row 2
                    if (topHabits.size > 1) {
                        views.setViewVisibility(R.id.widget_habit_row_2, View.VISIBLE)
                        val h2 = topHabits[1]
                        val isDone2 = completedIds.contains(h2.id)
                        views.setTextViewText(R.id.widget_habit_name_2, "• ${h2.name}")
                        views.setTextViewText(R.id.widget_habit_status_2, if (isDone2) "✅" else "⭕")
                    } else {
                        views.setViewVisibility(R.id.widget_habit_row_2, View.GONE)
                    }

                    // Row 3
                    if (topHabits.size > 2) {
                        views.setViewVisibility(R.id.widget_habit_row_3, View.VISIBLE)
                        val h3 = topHabits[2]
                        val isDone3 = completedIds.contains(h3.id)
                        views.setTextViewText(R.id.widget_habit_name_3, "• ${h3.name}")
                        views.setTextViewText(R.id.widget_habit_status_3, if (isDone3) "✅" else "⭕")
                    } else {
                        views.setViewVisibility(R.id.widget_habit_row_3, View.GONE)
                    }

                    // Open app click intent
                    val appIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    val pendingAppIntent = PendingIntent.getActivity(
                        context,
                        0,
                        appIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_root, pendingAppIntent)

                    // Refresh button intent
                    val refreshIntent = Intent(context, HabitAppWidgetProvider::class.java).apply {
                        action = ACTION_REFRESH_WIDGET
                    }
                    val pendingRefreshIntent = PendingIntent.getBroadcast(
                        context,
                        appWidgetId,
                        refreshIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_refresh_btn, pendingRefreshIntent)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (_: Exception) {
                    // Fail gracefully
                }
            }
        }
    }
}
