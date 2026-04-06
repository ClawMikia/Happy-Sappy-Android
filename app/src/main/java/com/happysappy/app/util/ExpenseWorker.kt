// File: util/ExpenseWorker.kt
package com.happysappy.app.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.happysappy.app.data.database.AppDatabase
import com.happysappy.app.data.entity.ExpenseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ExpenseWorker - WorkManager worker for scheduled notifications.
 * 
 * This worker handles background tasks for:
 * - Daily expense reminders
 * - Weekly expense summaries
 * - Monthly expense reports
 */
class ExpenseWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        // Worker types
        const val WORK_TYPE_DAILY = "daily_reminder"
        const val WORK_TYPE_WEEKLY = "weekly_summary"
        const val WORK_TYPE_MONTHLY = "monthly_report"
        
        // Unique work names
        const val WORK_DAILY_NAME = "expense_daily_reminder"
        const val WORK_WEEKLY_NAME = "expense_weekly_summary"
        const val WORK_MONTHLY_NAME = "expense_monthly_report"
    }
    
    private val notificationHelper = NotificationHelper(applicationContext)
    
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            // Ensure notification channel exists
            notificationHelper.createNotificationChannel()
            
            // Get work type from input data
            val workType = inputData.getString("work_type") ?: return@withContext Result.failure()
            
            when (workType) {
                WORK_TYPE_DAILY -> handleDailyReminder()
                WORK_TYPE_WEEKLY -> handleWeeklySummary()
                WORK_TYPE_MONTHLY -> handleMonthlyReport()
                else -> Result.failure()
            }
        }
    }
    
    private suspend fun handleDailyReminder(): Result {
        return try {
            notificationHelper.showDailyReminder()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private suspend fun handleWeeklySummary(): Result {
        return try {
            // Calculate weekly totals
            val database = AppDatabase.getDatabase(applicationContext)
            val dao = database.expenseDao()
            
            // Get start and end of current week
            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            val startOfWeek = calendar.timeInMillis
            
            calendar.add(java.util.Calendar.DAY_OF_WEEK, 6)
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
            calendar.set(java.util.Calendar.MINUTE, 59)
            calendar.set(java.util.Calendar.SECOND, 59)
            val endOfWeek = calendar.timeInMillis
            
            val total = dao.getTotalExpensesByDateRange(startOfWeek, endOfWeek) ?: 0.0
            val count = dao.getExpenseCountByDateRange(startOfWeek, endOfWeek)
            
            notificationHelper.showWeeklySummary(total, count)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private suspend fun handleMonthlyReport(): Result {
        return try {
            // Calculate monthly totals
            val database = AppDatabase.getDatabase(applicationContext)
            val dao = database.expenseDao()
            
            // Get start and end of current month
            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            val startOfMonth = calendar.timeInMillis
            
            calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
            calendar.set(java.util.Calendar.MINUTE, 59)
            calendar.set(java.util.Calendar.SECOND, 59)
            val endOfMonth = calendar.timeInMillis
            
            val total = dao.getTotalExpensesByDateRange(startOfMonth, endOfMonth) ?: 0.0
            val count = dao.getExpenseCountByDateRange(startOfMonth, endOfMonth)
            
            notificationHelper.showMonthlyReport(total, count)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}