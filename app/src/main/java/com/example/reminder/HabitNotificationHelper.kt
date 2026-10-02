package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object HabitNotificationHelper {

    const val CHANNEL_ID = "habit_daily_reminders_channel"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "تذكيرات العادات اليومية"
            val descriptionText = "قناة إرسال التنبيهات الذكية لتذكيرك بالعادات المتبقية خلال اليوم"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showHabitReminderNotification(
        context: Context,
        title: String,
        message: String,
        pendingHabitsList: List<String> = emptyList()
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(title)
        inboxStyle.setSummaryText("تطبيق عاداتي")
        if (pendingHabitsList.isNotEmpty()) {
            pendingHabitsList.take(5).forEach { habitName ->
                inboxStyle.addLine("• $habitName")
            }
            if (pendingHabitsList.size > 5) {
                inboxStyle.addLine("والمزيد...")
            }
        } else {
            inboxStyle.addLine(message)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(inboxStyle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } catch (_: SecurityException) {
            // Notification permission might not be granted
        }
    }
}
