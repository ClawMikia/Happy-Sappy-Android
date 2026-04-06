// File: util/GamificationManager.kt
package com.happysappy.app.util

import android.content.Context
import com.happysappy.app.data.entity.ExpenseEntity

/**
 * GamificationManager - Manages gamification logic and achievement tracking.
 * 
 * This class handles:
 * - XP calculation and level progression
 * - Streak tracking (no spend streak, logging streak)
 * - Achievement unlocking
 * - Discipline score calculation
 */
class GamificationManager(private val context: Context) {
    
    private val preferenceManager = PreferenceManager(context)
    private val notificationHelper = NotificationHelper(context)
    
    companion object {
        // XP rewards
        const val XP_LOG_EXPENSE = 10
        const val XP_DAILY_STREAK_BONUS = 5
        const val XP_WEEKLY_BONUS = 50
        const val XP_MONTHLY_BONUS = 200
        
        // All required categories for Category Explorer achievement
        val ALL_CATEGORIES = setOf(
            "Food", "Transport", "Shopping", "Entertainment",
            "Bills", "Health", "Education", "Other"
        )
        
        // All required frequencies for Frequency King achievement
        val ALL_FREQUENCIES = setOf(
            ExpenseEntity.FREQUENCY_DAILY,
            ExpenseEntity.FREQUENCY_WEEKLY,
            ExpenseEntity.FREQUENCY_MONTHLY,
            ExpenseEntity.FREQUENCY_QUARTERLY,
            ExpenseEntity.FREQUENCY_ANNUALLY
        )
    }
    
    /**
     * Award XP for logging an expense.
     */
    fun awardExpenseXP(): Int {
        val currentXP = preferenceManager.getTotalXP()
        val newXP = currentXP + XP_LOG_EXPENSE
        preferenceManager.saveTotalXP(newXP)
        return XP_LOG_EXPENSE
    }
    
    /**
     * Award streak bonus XP.
     */
    fun awardStreakBonus(streakDays: Int): Int {
        val bonus = streakDays * XP_DAILY_STREAK_BONUS
        val currentXP = preferenceManager.getTotalXP()
        preferenceManager.saveTotalXP(currentXP + bonus)
        return bonus
    }
    
    /**
     * Check and unlock achievements based on expense data.
     * Returns list of newly unlocked achievements.
     */
    fun checkAchievements(expenses: List<ExpenseEntity>): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()
        val unlockedIds = getUnlockedAchievementIds().toMutableSet()
        
        // Check First Expense
        if (!unlockedIds.contains(Achievement.ID_FIRST_EXPENSE) && expenses.isNotEmpty()) {
            unlockAchievement(Achievement.ID_FIRST_EXPENSE)
            newlyUnlocked.add(Achievement.DEFAULT_ACHIEVEMENTS.find { it.id == Achievement.ID_FIRST_EXPENSE }!!)
        }
        
        // Check Week Streak (7 day logging streak)
        val loggingStreak = calculateLoggingStreak(expenses)
        if (!unlockedIds.contains(Achievement.ID_WEEK_STREAK) && loggingStreak >= 7) {
            unlockAchievement(Achievement.ID_WEEK_STREAK)
            newlyUnlocked.add(Achievement.DEFAULT_ACHIEVEMENTS.find { it.id == Achievement.ID_WEEK_STREAK }!!)
        }
        
        // Check Category Explorer
        val categoriesUsed = expenses.map { it.category }.toSet()
        if (!unlockedIds.contains(Achievement.ID_CATEGORY_EXPLORER) && categoriesUsed.containsAll(ALL_CATEGORIES)) {
            unlockAchievement(Achievement.ID_CATEGORY_EXPLORER)
            newlyUnlocked.add(Achievement.DEFAULT_ACHIEVEMENTS.find { it.id == Achievement.ID_CATEGORY_EXPLORER }!!)
        }
        
        // Check Frequency King
        val frequenciesUsed = expenses.map { it.frequency }.toSet()
        if (!unlockedIds.contains(Achievement.ID_FREQUENCY_KING) && frequenciesUsed.containsAll(ALL_FREQUENCIES)) {
            unlockAchievement(Achievement.ID_FREQUENCY_KING)
            newlyUnlocked.add(Achievement.DEFAULT_ACHIEVEMENTS.find { it.id == Achievement.ID_FREQUENCY_KING }!!)
        }
        
        // Check Monthly Master (30+ expenses)
        if (!unlockedIds.contains(Achievement.ID_MONTHLY_MASTER) && expenses.size >= 30) {
            unlockAchievement(Achievement.ID_MONTHLY_MASTER)
            newlyUnlocked.add(Achievement.DEFAULT_ACHIEVEMENTS.find { it.id == Achievement.ID_MONTHLY_MASTER }!!)
        }
        
        // Award XP for newly unlocked achievements
        newlyUnlocked.forEach { achievement ->
            val currentXP = preferenceManager.getTotalXP()
            preferenceManager.saveTotalXP(currentXP + achievement.xpReward)
            
            // Show notification
            notificationHelper.showAchievementUnlocked(achievement.name)
        }
        
        return newlyUnlocked
    }
    
    /**
     * Calculate logging streak (consecutive days with at least one expense).
     */
    fun calculateLoggingStreak(expenses: List<ExpenseEntity>): Int {
        if (expenses.isEmpty()) return 0
        
        val sortedExpenses = expenses.sortedByDescending { it.date }
        var streak = 0
        var lastDate: Long? = null
        
        for (expense in sortedExpenses) {
            val expenseDate = expense.date
            if (lastDate == null) {
                streak = 1
                lastDate = expenseDate
                continue
            }
            
            val diff = lastDate!! - expenseDate
            val daysDiff = diff / (24 * 60 * 60 * 1000L)
            
            if (daysDiff <= 1) {
                streak++
                lastDate = expenseDate
            } else {
                break
            }
        }
        
        return streak
    }
    
    /**
     * Calculate no spend streak (days without any expense).
     */
    fun calculateNoSpendStreak(expenses: List<ExpenseEntity>): Int {
        if (expenses.isEmpty()) return 0
        
        val mostRecent = expenses.maxByOrNull { it.date } ?: return 0
        val now = System.currentTimeMillis()
        val diff = now - mostRecent.date
        return (diff / (24 * 60 * 60 * 1000L)).toInt()
    }
    
    /**
     * Calculate discipline score (0-100).
     * Based on spending patterns and consistency.
     */
    fun calculateDisciplineScore(expenses: List<ExpenseEntity>): Int {
        if (expenses.isEmpty()) return 100
        
        val loggingStreak = calculateLoggingStreak(expenses)
        val totalExpenses = expenses.size
        
        // Base score starts at 100
        var score = 100
        
        // Bonus for logging consistency
        score += (loggingStreak * 2).coerceAtMost(30)
        
        // Penalty for too many expenses (encourage mindful spending)
        if (totalExpenses > 50) {
            score -= ((totalExpenses - 50) / 5).coerceAtMost(30)
        }
        
        return score.coerceIn(0, 100)
    }
    
    /**
     * Get current user progress.
     */
    fun getUserProgress(expenses: List<ExpenseEntity>): UserProgress {
        val loggingStreak = calculateLoggingStreak(expenses)
        val noSpendStreak = calculateNoSpendStreak(expenses)
        val disciplineScore = calculateDisciplineScore(expenses)
        val totalXP = preferenceManager.getTotalXP()
        val level = UserProgress(totalXP = totalXP).calculateLevel()
        val unlockedAchievements = getUnlockedAchievementIds()
        val categoriesUsed = expenses.map { it.category }.toSet()
        val frequenciesUsed = expenses.map { it.frequency }.toSet()
        
        return UserProgress(
            noSpendStreak = noSpendStreak,
            loggingStreak = loggingStreak,
            disciplineScore = disciplineScore,
            totalXP = totalXP,
            level = level,
            unlockedAchievements = unlockedAchievements,
            lastExpenseDate = expenses.maxByOrNull { it.date }?.date,
            categoriesUsed = categoriesUsed,
            frequenciesUsed = frequenciesUsed
        )
    }
    
    /**
     * Get all achievements with unlock status.
     */
    fun getAllAchievements(expenses: List<ExpenseEntity>): List<Achievement> {
        val unlockedIds = getUnlockedAchievementIds()
        
        return Achievement.DEFAULT_ACHIEVEMENTS.map { achievement ->
            achievement.copy(
                isUnlocked = unlockedIds.contains(achievement.id)
            )
        }
    }
    
    private fun getUnlockedAchievementIds(): Set<String> {
        // For now, return empty set - in production, this would be stored in preferences or database
        return emptySet()
    }
    
    private fun unlockAchievement(achievementId: String) {
        // In production, this would save to preferences or database
        // For now, just show notification
    }
    
    /**
     * Reset all gamification data.
     */
    fun resetProgress() {
        preferenceManager.saveTotalXP(0)
        preferenceManager.saveNoSpendStreak(0)
        preferenceManager.saveDisciplineScore(100)
    }
}