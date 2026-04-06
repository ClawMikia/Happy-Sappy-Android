// File: util/NotificationHelper.kt
package com.happysappy.app.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.happysappy.app.R
import com.happysappy.app.ui.activity.MainActivity

/**
 * NotificationHelper - Handles notification creation and management.
 * 
 * This class provides methods for creating and displaying various types
 * of notifications including daily reminders, weekly summaries, and
 * achievement notifications.
 */
class NotificationHelper(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "expense_reminders_channel"
        const val CHANNEL_NAME = "Expense Reminders"
        const val CHANNEL_DESCRIPTION = "Reminders for expense tracking"
        
        // Notification IDs
        const val NOTIFICATION_DAILY_REMINDER = 1
        const val NOTIFICATION_WEEKLY_SUMMARY = 2
        const val NOTIFICATION_MONTHLY_REPORT = 3
        const val NOTIFICATION_ACHIEVEMENT = 4
        const val NOTIFICATION_STREAK = 5
        
        private const val REQUEST_CODE_MAIN = 0
    }
    
    /**
     * Create the notification channel (required for Android 8.0+).
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Check if notification permission is granted.
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required for older versions
        }
    }
    
    /**
     * Show daily reminder notification.
     */
    fun showDailyReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_MAIN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_daily_title))
            .setContentText(context.getString(R.string.notification_daily_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notify(NOTIFICATION_DAILY_REMINDER, notification)
    }
    
    /**
     * Show weekly summary notification.
     */
    fun showWeeklySummary(totalSpent: Double, expenseCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_MAIN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val contentText = "You logged $expenseCount expenses totaling ${String.format("%.2f", totalSpent)} this week."
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_weekly_title))
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .build()
        
        notify(NOTIFICATION_WEEKLY_SUMMARY, notification)
    }
    
    /**
     * Show monthly report notification.
     */
    fun showMonthlyReport(totalSpent: Double, expenseCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_MAIN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val contentText = "Your monthly report: $expenseCount expenses totaling ${String.format("%.2f", totalSpent)}."
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_monthly_title))
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .build()
        
        notify(NOTIFICATION_MONTHLY_REPORT, notification)
    }
    
    /**
     * Show achievement unlocked notification.
     */
    fun showAchievementUnlocked(achievementName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_MAIN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle(context.getString(R.string.notification_achievement_title))
            .setContentText("$achievementName unlocked!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notify(NOTIFICATION_ACHIEVEMENT, notification)
    }
    
    /**
     * Show streak notification.
     */
    fun showStreakReminder(streakDays: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_MAIN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val contentText = context.getString(R.string.notification_streak_text, streakDays)
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_streak_title))
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notify(NOTIFICATION_STREAK, notification)
    }
    
    /**
     * Cancel a specific notification.
     */
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
    
    /**
     * Cancel all notifications.
     */
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
    
    private fun notify(id: Int, notification: android.app.Notification) {
        if (hasNotificationPermission()) {
            NotificationManagerCompat.from(context).notify(id, notification)
        }
    }
}