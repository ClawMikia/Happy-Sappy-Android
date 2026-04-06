// File: data/dao/ExpenseDao.kt
package com.happysappy.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.happysappy.app.data.entity.ExpenseEntity

/**
 * Data Access Object (DAO) for expense-related database operations.
 * 
 * This interface defines all the methods for interacting with the 
 * expenses table in the Room database, including CRUD operations
 * and various query methods for filtering and sorting expenses.
 */
@Dao
interface ExpenseDao {
    
    // ==================== INSERT OPERATIONS ====================
    
    /**
     * Insert a single expense into the database.
     * @param expense The expense entity to insert
     * @return The row ID of the inserted expense
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long
    
    /**
     * Insert multiple expenses into the database.
     * @param expenses List of expense entities to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExpenses(expenses: List<ExpenseEntity>)
    
    // ==================== UPDATE OPERATIONS ====================
    
    /**
     * Update an existing expense in the database.
     * @param expense The expense entity with updated values
     */
    @Update
    suspend fun updateExpense(expense: ExpenseEntity)
    
    /**
     * Update multiple expenses in the database.
     * @param expenses List of expense entities to update
     */
    @Update
    suspend fun updateAllExpenses(expenses: List<ExpenseEntity>)
    
    // ==================== DELETE OPERATIONS ====================
    
    /**
     * Delete a single expense from the database.
     * @param expense The expense entity to delete
     */
    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)
    
    /**
     * Delete an expense by its ID.
     * @param expenseId The ID of the expense to delete
     * @return Number of rows deleted (0 or 1)
     */
    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteExpenseById(expenseId: Long): Int
    
    /**
     * Delete all expenses from the database.
     * Use with caution!
     */
    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
    
    // ==================== GET OPERATIONS ====================
    
    /**
     * Get all expenses from the database.
     * Returns LiveData for automatic UI updates when data changes.
     * @return LiveData list of all expenses sorted by date (newest first)
     */
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<ExpenseEntity>>
    
    /**
     * Get all expenses as a regular list (no LiveData).
     * Useful for one-time operations or background processing.
     * @return List of all expenses sorted by date (newest first)
     */
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    suspend fun getAllExpensesList(): List<ExpenseEntity>
    
    /**
     * Get a single expense by its ID.
     * @param expenseId The ID of the expense to retrieve
     * @return The expense entity or null if not found
     */
    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Long): ExpenseEntity?
    
    /**
     * Get expense by ID as LiveData.
     * @param expenseId The ID of the expense to retrieve
     * @return LiveData of the expense entity
     */
    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    fun getExpenseByIdLiveData(expenseId: Long): LiveData<ExpenseEntity?>
    
    // ==================== FILTER BY CATEGORY ====================
    
    /**
     * Get expenses filtered by category.
     * @param category The category to filter by
     * @return LiveData list of expenses in the specified category
     */
    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): LiveData<List<ExpenseEntity>>
    
    /**
     * Get expenses filtered by multiple categories.
     * @param categories List of categories to filter by
     * @return LiveData list of expenses in any of the specified categories
     */
    @Query("SELECT * FROM expenses WHERE category IN (:categories) ORDER BY date DESC")
    fun getExpensesByCategories(categories: List<String>): LiveData<List<ExpenseEntity>>
    
    // ==================== FILTER BY DATE RANGE ====================
    
    /**
     * Get expenses within a specific date range.
     * @param startDate Start timestamp (inclusive)
     * @param endDate End timestamp (inclusive)
     * @return LiveData list of expenses within the date range
     */
    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): LiveData<List<ExpenseEntity>>
    
    /**
     * Get expenses from today.
     * @param startOfDay Timestamp for start of today (00:00:00)
     * @param endOfDay Timestamp for end of today (23:59:59)
     * @return LiveData list of today's expenses
     */
    @Query("SELECT * FROM expenses WHERE date >= :startOfDay AND date <= :endOfDay ORDER BY date DESC")
    fun getTodayExpenses(startOfDay: Long, endOfDay: Long): LiveData<List<ExpenseEntity>>
    
    /**
     * Get expenses from this week.
     * @param startOfWeek Timestamp for start of the week
     * @param endOfWeek Timestamp for end of the week
     * @return LiveData list of this week's expenses
     */
    @Query("SELECT * FROM expenses WHERE date >= :startOfWeek AND date <= :endOfWeek ORDER BY date DESC")
    fun getWeekExpenses(startOfWeek: Long, endOfWeek: Long): LiveData<List<ExpenseEntity>>
    
    /**
     * Get expenses from this month.
     * @param startOfMonth Timestamp for start of the month
     * @param endOfMonth Timestamp for end of the month
     * @return LiveData list of this month's expenses
     */
    @Query("SELECT * FROM expenses WHERE date >= :startOfMonth AND date <= :endOfMonth ORDER BY date DESC")
    fun getMonthExpenses(startOfMonth: Long, endOfMonth: Long): LiveData<List<ExpenseEntity>>
    
    // ==================== FILTER BY AMOUNT RANGE ====================
    
    /**
     * Get expenses within a specific amount range.
     * @param minAmount Minimum amount (inclusive)
     * @param maxAmount Maximum amount (inclusive)
     * @return LiveData list of expenses within the amount range
     */
    @Query("SELECT * FROM expenses WHERE amountSpent >= :minAmount AND amountSpent <= :maxAmount ORDER BY date DESC")
    fun getExpensesByAmountRange(minAmount: Double, maxAmount: Double): LiveData<List<ExpenseEntity>>
    
    /**
     * Get expenses above a certain amount.
     * @param minAmount Minimum amount threshold
     * @return LiveData list of expenses above the threshold
     */
    @Query("SELECT * FROM expenses WHERE amountSpent >= :minAmount ORDER BY date DESC")
    fun getExpensesAboveAmount(minAmount: Double): LiveData<List<ExpenseEntity>>
    
    /**
     * Get expenses below a certain amount.
     * @param maxAmount Maximum amount threshold
     * @return LiveData list of expenses below the threshold
     */
    @Query("SELECT * FROM expenses WHERE amountSpent <= :maxAmount ORDER BY date DESC")
    fun getExpensesBelowAmount(maxAmount: Double): LiveData<List<ExpenseEntity>>
    
    // ==================== FILTER BY FREQUENCY ====================
    
    /**
     * Get expenses filtered by frequency.
     * @param frequency The frequency to filter by (Daily, Weekly, Monthly, Quarterly, Annually)
     * @return LiveData list of expenses with the specified frequency
     */
    @Query("SELECT * FROM expenses WHERE frequency = :frequency ORDER BY date DESC")
    fun getExpensesByFrequency(frequency: String): LiveData<List<ExpenseEntity>>
    
    /**
     * Get expenses filtered by multiple frequencies.
     * @param frequencies List of frequencies to filter by
     * @return LiveData list of expenses with any of the specified frequencies
     */
    @Query("SELECT * FROM expenses WHERE frequency IN (:frequencies) ORDER BY date DESC")
    fun getExpensesByFrequencies(frequencies: List<String>): LiveData<List<ExpenseEntity>>
    
    // ==================== COMBINED FILTERS ====================
    
    /**
     * Get expenses filtered by category and date range.
     * @param category The category to filter by
     * @param startDate Start timestamp (inclusive)
     * @param endDate End timestamp (inclusive)
     * @return LiveData list of expenses matching both criteria
     */
    @Query("SELECT * FROM expenses WHERE category = :category AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesByCategoryAndDateRange(
        category: String,
        startDate: Long,
        endDate: Long
    ): LiveData<List<ExpenseEntity>>
    
    /**
     * Get expenses filtered by category, date range, and amount range.
     * @param category The category to filter by
     * @param startDate Start timestamp (inclusive)
     * @param endDate End timestamp (inclusive)
     * @param minAmount Minimum amount (inclusive)
     * @param maxAmount Maximum amount (inclusive)
     * @return LiveData list of expenses matching all criteria
     */
    @Query("""
        SELECT * FROM expenses 
        WHERE category = :category 
        AND date >= :startDate AND date <= :endDate 
        AND amountSpent >= :minAmount AND amountSpent <= :maxAmount 
        ORDER BY date DESC
    """)
    fun getExpensesByCategoryDateAndAmount(
        category: String,
        startDate: Long,
        endDate: Long,
        minAmount: Double,
        maxAmount: Double
    ): LiveData<List<ExpenseEntity>>
    
    // ==================== AGGREGATE QUERIES ====================
    
    /**
     * Get the total amount spent across all expenses.
     * @return Total sum of all expense amounts
     */
    @Query("SELECT SUM(amountSpent) FROM expenses")
    suspend fun getTotalExpenses(): Double?
    
    /**
     * Get the total amount spent within a date range.
     * @param startDate Start timestamp (inclusive)
     * @param endDate End timestamp (inclusive)
     * @return Total sum of expenses within the date range
     */
    @Query("SELECT SUM(amountSpent) FROM expenses WHERE date >= :startDate AND date <= :endDate")
    suspend fun getTotalExpensesByDateRange(startDate: Long, endDate: Long): Double?
    
    /**
     * Get the total amount spent by category.
     * @param category The category to sum
     * @return Total sum of expenses in the category
     */
    @Query("SELECT SUM(amountSpent) FROM expenses WHERE category = :category")
    suspend fun getTotalByCategory(category: String): Double?
    
    /**
     * Get the count of all expenses.
     * @return Total number of expense records
     */
    @Query("SELECT COUNT(*) FROM expenses")
    suspend fun getExpenseCount(): Int
    
    /**
     * Get the count of expenses within a date range.
     * @param startDate Start timestamp (inclusive)
     * @param endDate End timestamp (inclusive)
     * @return Number of expenses within the date range
     */
    @Query("SELECT COUNT(*) FROM expenses WHERE date >= :startDate AND date <= :endDate")
    suspend fun getExpenseCountByDateRange(startDate: Long, endDate: Long): Int
    
    /**
     * Get the average expense amount.
     * @return Average amount across all expenses
     */
    @Query("SELECT AVG(amountSpent) FROM expenses")
    suspend fun getAverageExpense(): Double?
    
    /**
     * Get the highest expense amount.
     * @return Maximum expense amount
     */
    @Query("SELECT MAX(amountSpent) FROM expenses")
    suspend fun getHighestExpense(): Double?
    
    /**
     * Get the lowest expense amount.
     * @return Minimum expense amount
     */
    @Query("SELECT MIN(amountSpent) FROM expenses")
    suspend fun getLowestExpense(): Double?
    
    // ==================== CATEGORY STATISTICS ====================
    
    /**
     * Get all unique categories from expenses.
     * @return List of distinct category names
     */
    @Query("SELECT DISTINCT category FROM expenses ORDER BY category ASC")
    fun getAllCategories(): LiveData<List<String>>
    
    /**
     * Get total amount spent per category.
     * @return List of category totals as Pair<category, total>
     */
    @Query("SELECT category, SUM(amountSpent) as total FROM expenses GROUP BY category ORDER BY total DESC")
    fun getCategoryTotals(): LiveData<List<CategoryTotal>>
    
    /**
     * Data class for category total statistics.
     */
    data class CategoryTotal(
        val category: String,
        val total: Double
    )
    
    // ==================== RECENT EXPENSES ====================
    
    /**
     * Get the most recent expenses (limited count).
     * @param limit Maximum number of expenses to return
     * @return LiveData list of the most recent expenses
     */
    @Query("SELECT * FROM expenses ORDER BY date DESC LIMIT :limit")
    fun getRecentExpenses(limit: Int = 10): LiveData<List<ExpenseEntity>>
    
    /**
     * Get the most recent expense.
     * @return LiveData of the most recent expense
     */
    @Query("SELECT * FROM expenses ORDER BY date DESC LIMIT 1")
    fun getMostRecentExpense(): LiveData<ExpenseEntity?>
    
    // ==================== SEARCH ====================
    
    /**
     * Search expenses by note content.
     * @param query The search query string
     * @return LiveData list of expenses matching the search query
     */
    @Query("""
        SELECT * FROM expenses 
        WHERE note LIKE '%' || :query || '%' 
        OR category LIKE '%' || :query || '%'
        ORDER BY date DESC
    """)
    fun searchExpenses(query: String): LiveData<List<ExpenseEntity>>
}