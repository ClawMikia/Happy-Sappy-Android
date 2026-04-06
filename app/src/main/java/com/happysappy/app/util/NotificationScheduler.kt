// File: util/NotificationScheduler.kt
package com.happysappy.app.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

/**
 * NotificationScheduler - Schedules notification workers using WorkManager.
 * 
 * This class provides methods for scheduling and canceling:
 * - Daily reminder notifications
 * - Weekly summary notifications
 * - Monthly report notifications
 */
class NotificationScheduler(private val context: Context) {
    
    private val workManager = WorkManager.getInstance(context)
    private val preferenceManager = PreferenceManager(context)
    
    /**
     * Schedule all notifications based on user preferences.
     */
    fun scheduleAllNotifications() {
        if (!preferenceManager.areNotificationsEnabled()) {
            cancelAllNotifications()
            return
        }
        
        if (preferenceManager.isDailyReminderEnabled()) {
            scheduleDailyReminder()
        } else {
            cancelDailyReminder()
        }
        
        if (preferenceManager.isWeeklySummaryEnabled()) {
            scheduleWeeklySummary()
        } else {
            cancelWeeklySummary()
        }
        
        if (preferenceManager.isMonthlyReportEnabled()) {
            scheduleMonthlyReport()
        } else {
            cancelMonthlyReport()
        }
    }
    
    /**
     * Schedule daily reminder notification.
     * Runs every day at 9:00 AM by default.
     */
    fun scheduleDailyReminder() {
        val (hour, minute) = preferenceManager.getDailyReminderTime()
        
        // Calculate initial delay to run at specified time
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
        }
        
        var initialDelay = calendar.timeInMillis - System.currentTimeMillis()
        if (initialDelay < 0) {
            initialDelay += 24 * 60 * 60 * 1000L // Add one day if time has passed
        }
        
        val workRequest = OneTimeWorkRequestBuilder<ExpenseWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(ExpenseWorker.WORK_DAILY_NAME)
            .setInputData(workDataOf("work_type" to ExpenseWorker.WORK_TYPE_DAILY))
            .build()
        
        workManager.enqueueUniqueWork(
            ExpenseWorker.WORK_DAILY_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        
        // Also schedule a repeating work as backup
        val periodicWork = PeriodicWorkRequestBuilder<ExpenseWorker>(1, TimeUnit.DAYS)
            .addTag(ExpenseWorker.WORK_DAILY_NAME)
            .setInputData(workDataOf("work_type" to ExpenseWorker.WORK_TYPE_DAILY))
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            ExpenseWorker.WORK_DAILY_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWork
        )
    }
    
    /**
     * Cancel daily reminder notification.
     */
    fun cancelDailyReminder() {
        workManager.cancelUniqueWork(ExpenseWorker.WORK_DAILY_NAME)
    }
    
    /**
     * Schedule weekly summary notification.
     * Runs every Monday at 9:00 AM.
     */
    fun scheduleWeeklySummary() {
        val workRequest = PeriodicWorkRequestBuilder<ExpenseWorker>(7, TimeUnit.DAYS)
            .addTag(ExpenseWorker.WORK_WEEKLY_NAME)
            .setInputData(workDataOf("work_type" to ExpenseWorker.WORK_TYPE_WEEKLY))
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            ExpenseWorker.WORK_WEEKLY_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    /**
     * Cancel weekly summary notification.
     */
    fun cancelWeeklySummary() {
        workManager.cancelUniqueWork(ExpenseWorker.WORK_WEEKLY_NAME)
    }
    
    /**
     * Schedule monthly report notification.
     * Runs on the first day of each month at 9:00 AM.
     */
    fun scheduleMonthlyReport() {
        // Calculate initial delay to first day of next month
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(java.util.Calendar.DAY_OF_MONTH, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 9)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            add(java.util.Calendar.MONTH, 1) // Next month
        }
        
        val initialDelay = calendar.timeInMillis - System.currentTimeMillis()
        
        val workRequest = OneTimeWorkRequestBuilder<ExpenseWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(ExpenseWorker.WORK_MONTHLY_NAME)
            .setInputData(workDataOf("work_type" to ExpenseWorker.WORK_TYPE_MONTHLY))
            .build()
        
        workManager.enqueueUniqueWork(
            ExpenseWorker.WORK_MONTHLY_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    /**
     * Cancel monthly report notification.
     */
    fun cancelMonthlyReport() {
        workManager.cancelUniqueWork(ExpenseWorker.WORK_MONTHLY_NAME)
    }
    
    /**
     * Cancel all scheduled notifications.
     */
    fun cancelAllNotifications() {
        cancelDailyReminder()
        cancelWeeklySummary()
        cancelMonthlyReport()
    }
    
    /**
     * Reschedule all notifications (useful when settings change).
     */
    fun rescheduleAll() {
        cancelAllNotifications()
        scheduleAllNotifications()
    }
}