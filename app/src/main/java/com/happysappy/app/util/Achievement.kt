// File: util/Achievement.kt
package com.happysappy.app.util

/**
 * Data class representing an achievement in the gamification system.
 * 
 * @param id Unique identifier for the achievement
 * @param name Display name of the achievement
 * @param description Description of what the achievement requires
 * @param icon Emoji or icon identifier for visual display
 * @param xpReward XP points awarded when unlocked
 * @param isUnlocked Whether the achievement has been unlocked
 * @param unlockedDate Timestamp when the achievement was unlocked (null if not unlocked)
 */
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedDate: Long? = null
) {
    companion object {
        // Achievement IDs
        const val ID_FIRST_EXPENSE = "first_expense"
        const val ID_WEEK_STREAK = "week_streak"
        const val ID_BUDGET_MASTER = "budget_master"
        const val ID_FREQUENCY_KING = "frequency_king"
        const val ID_CATEGORY_EXPLORER = "category_explorer"
        const val ID_MONTHLY_MASTER = "monthly_master"
        
        // Default achievements
        val DEFAULT_ACHIEVEMENTS = listOf(
            Achievement(
                id = ID_FIRST_EXPENSE,
                name = "First Step",
                description = "Log your first expense",
                icon = "🎯",
                xpReward = 50
            ),
            Achievement(
                id = ID_WEEK_STREAK,
                name = "Week Warrior",
                description = "7-day logging streak",
                icon = "🔥",
                xpReward = 100
            ),
            Achievement(
                id = ID_BUDGET_MASTER,
                name = "Budget Master",
                description = "Stay under budget for a week",
                icon = "💰",
                xpReward = 150
            ),
            Achievement(
                id = ID_FREQUENCY_KING,
                name = "Frequency King",
                description = "Use all frequency types",
                icon = "👑",
                xpReward = 200
            ),
            Achievement(
                id = ID_CATEGORY_EXPLORER,
                name = "Category Explorer",
                description = "Use all categories",
                icon = "🗺️",
                xpReward = 150
            ),
            Achievement(
                id = ID_MONTHLY_MASTER,
                name = "Monthly Master",
                description = "Complete month of tracking",
                icon = "📅",
                xpReward = 300
            )
        )
    }
    
    /**
     * Get the progress text for display.
     */
    fun getProgressText(progress: Int, total: Int): String {
        return if (isUnlocked) {
            "✓ Completed"
        } else {
            "$progress / $total"
        }
    }
}

/**
 * Data class for user progress in the gamification system.
 */
data class UserProgress(
    val noSpendStreak: Int = 0,
    val loggingStreak: Int = 0,
    val disciplineScore: Int = 100,
    val totalXP: Int = 0,
    val level: Int = 1,
    val unlockedAchievements: Set<String> = emptySet(),
    val lastExpenseDate: Long? = null,
    val categoriesUsed: Set<String> = emptySet(),
    val frequenciesUsed: Set<String> = emptySet()
) {
    /**
     * Calculate level based on XP.
     * Level thresholds: 0, 100, 300, 600, 1000
     */
    fun calculateLevel(): Int {
        return when {
            totalXP >= 1001 -> 5 // Legend
            totalXP >= 601 -> 4  // Master
            totalXP >= 301 -> 3  // Saver
            totalXP >= 101 -> 2  // Tracker
            else -> 1            // Beginner
        }
    }
    
    /**
     * Get level name based on level number.
     */
    fun getLevelName(): String {
        return when (level) {
            5 -> "Legend"
            4 -> "Master"
            3 -> "Saver"
            2 -> "Tracker"
            else -> "Beginner"
        }
    }
    
    /**
     * Get XP progress to next level.
     */
    fun getXPProgress(): Pair<Int, Int> {
        val thresholds = listOf(0, 100, 300, 600, 1000, Int.MAX_VALUE)
        val currentLevel = calculateLevel()
        val currentThreshold = thresholds[currentLevel - 1]
        val nextThreshold = thresholds.getOrElse(currentLevel) { Int.MAX_VALUE }
        
        val progress = totalXP - currentThreshold
        val required = nextThreshold - currentThreshold
        
        return Pair(progress, required)
    }
}