// File: data/repository/ExpenseRepository.kt
package com.happysappy.app.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.happysappy.app.data.dao.ExpenseDao
import com.happysappy.app.data.database.AppDatabase
import com.happysappy.app.data.entity.ExpenseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository class that provides a clean API for data operations.
 * 
 * This class abstracts the data layer from the rest of the application,
 * providing a single source of truth for expense data. It mediates between
 * the ViewModel and the data sources (database).
 * 
 * Key Responsibilities:
 * - CRUD operations for expenses
 * - Filtering and sorting expenses
 * - Aggregate queries for statistics
 * - Managing data consistency
 */
class ExpenseRepository(context: Context) {
    
    // DAO instance for database operations
    private val expenseDao: ExpenseDao
    
    init {
        // Initialize database and DAO
        val database = AppDatabase.getDatabase(context)
        expenseDao = database.expenseDao()
    }
    
    // ==================== INSERT OPERATIONS ====================
    
    /**
     * Insert a new expense into the database.
     * Executes on IO dispatcher to avoid blocking the main thread.
     * 
     * @param expense The expense entity to insert
     * @return The row ID of the inserted expense
     */
    suspend fun insertExpense(expense: ExpenseEntity): Long {
        return withContext(Dispatchers.IO) {
            expenseDao.insertExpense(expense)
        }
    }
    
    /**
     * Insert multiple expenses into the database.
     * Useful for bulk imports or initial data population.
     * 
     * @param expenses List of expense entities to insert
     */
    suspend fun insertAllExpenses(expenses: List<ExpenseEntity>) {
        withContext(Dispatchers.IO) {
            expenseDao.insertAllExpenses(expenses)
        }
    }
    
    // ==================== UPDATE OPERATIONS ====================
    
    /**
     * Update an existing expense in the database.
     * 
     * @param expense The expense entity with updated values
     */
    suspend fun updateExpense(expense: ExpenseEntity) {
        withContext(Dispatchers.IO) {
            expenseDao.updateExpense(expense)
        }
    }
    
    /**
     * Update multiple expenses in the database.
     * 
     * @param expenses List of expense entities to update
     */
    suspend fun updateAllExpenses(expenses: List<ExpenseEntity>) {
        withContext(Dispatchers.IO) {
            expenseDao.updateAllExpenses(expenses)
        }
    }
    
    // ==================== DELETE OPERATIONS ====================
    
    /**
     * Delete a single expense from the database.
     * 
     * @param expense The expense entity to delete
     */
    suspend fun deleteExpense(expense: ExpenseEntity) {
        withContext(Dispatchers.IO) {
            expenseDao.deleteExpense(expense)
        }
    }
    
    /**
     * Delete an expense by its ID.
     * 
     * @param expenseId The ID of the expense to delete
     * @return Number of rows deleted (0 or 1)
     */
    suspend fun deleteExpenseById(expenseId: Long): Int {
        return withContext(Dispatchers.IO) {
            expenseDao.deleteExpenseById(expenseId)
        }
    }
    
    /**
     * Delete all expenses from the database.
     * Use with caution - this operation cannot be undone!
     */
    suspend fun deleteAllExpenses() {
        withContext(Dispatchers.IO) {
            expenseDao.deleteAllExpenses()
        }
    }
    
    // ==================== GET OPERATIONS (LiveData) ====================
    
    /**
     * Get all expenses as LiveData.
     * The list is automatically sorted by date (newest first).
     * 
     * @return LiveData list of all expenses
     */
    fun getAllExpenses(): LiveData<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses()
    }
    
    /**
     * Get a single expense by ID as LiveData.
     * 
     * @param expenseId The ID of the expense to retrieve
     * @return LiveData of the expense entity
     */
    fun getExpenseByIdLiveData(expenseId: Long): LiveData<ExpenseEntity?> {
        return expenseDao.getExpenseByIdLiveData(expenseId)
    }
    
    /**
     * Get all expenses as a regular list (not LiveData).
     * Use for one-time operations or background processing.
     * 
     * @return List of all expenses
     */
    suspend fun getAllExpensesList(): List<ExpenseEntity> {
        return withContext(Dispatchers.IO) {
            expenseDao.getAllExpensesList()
        }
    }
    
    /**
     * Get a single expense by ID.
     * 
     * @param expenseId The ID of the expense to retrieve
     * @return The expense entity or null if not found
     */
    suspend fun getExpenseById(expenseId: Long): ExpenseEntity? {
        return withContext(Dispatchers.IO) {
            expenseDao.getExpenseById(expenseId)
        }
    }
    
    // ==================== FILTER BY CATEGORY ====================
    
    /**
     * Get expenses filtered by category.
     * 
     * @param category The category to filter by
     * @return LiveData list of expenses in the specified category
     */
    fun getExpensesByCategory(category: String): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesByCategory(category)
    }
    
    /**
     * Get expenses filtered by multiple categories.
     * 
     * @param categories List of categories to filter by
     * @return LiveData list of expenses in any of the specified categories
     */
    fun getExpensesByCategories(categories: List<String>): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesByCategories(categories)
    }
    
    // ==================== FILTER BY DATE RANGE ====================
    
    /**
     * Get expenses within a specific date range.
     * 
     * @param startDate Start timestamp (inclusive)
     * @param endDate End timestamp (inclusive)
     * @return LiveData list of expenses within the date range
     */
    fun getExpensesByDateRange(startDate: Long, endDate: Long): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesByDateRange(startDate, endDate)
    }
    
    /**
     * Get expenses from today.
     * 
     * @param startOfDay Timestamp for start of today (00:00:00)
     * @param endOfDay Timestamp for end of today (23:59:59)
     * @return LiveData list of today's expenses
     */
    fun getTodayExpenses(startOfDay: Long, endOfDay: Long): LiveData<List<ExpenseEntity>> {
        return expenseDao.getTodayExpenses(startOfDay, endOfDay)
    }
    
    /**
     * Get expenses from this week.
     * 
     * @param startOfWeek Timestamp for start of the week
     * @param endOfWeek Timestamp for end of the week
     * @return LiveData list of this week's expenses
     */
    fun getWeekExpenses(startOfWeek: Long, endOfWeek: Long): LiveData<List<ExpenseEntity>> {
        return expenseDao.getWeekExpenses(startOfWeek, endOfWeek)
    }
    
    /**
     * Get expenses from this month.
     * 
     * @param startOfMonth Timestamp for start of the month
     * @param endOfMonth Timestamp for end of the month
     * @return LiveData list of this month's expenses
     */
    fun getMonthExpenses(startOfMonth: Long, endOfMonth: Long): LiveData<List<ExpenseEntity>> {
        return expenseDao.getMonthExpenses(startOfMonth, endOfMonth)
    }
    
    // ==================== FILTER BY AMOUNT RANGE ====================
    
    /**
     * Get expenses within a specific amount range.
     * 
     * @param minAmount Minimum amount (inclusive)
     * @param maxAmount Maximum amount (inclusive)
     * @return LiveData list of expenses within the amount range
     */
    fun getExpensesByAmountRange(minAmount: Double, maxAmount: Double): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesByAmountRange(minAmount, maxAmount)
    }
    
    /**
     * Get expenses above a certain amount.
     * 
     * @param minAmount Minimum amount threshold
     * @return LiveData list of expenses above the threshold
     */
    fun getExpensesAboveAmount(minAmount: Double): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesAboveAmount(minAmount)
    }
    
    /**
     * Get expenses below a certain amount.
     * 
     * @param maxAmount Maximum amount threshold
     * @return LiveData list of expenses below the threshold
     */
    fun getExpensesBelowAmount(maxAmount: Double): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesBelowAmount(maxAmount)
    }
    
    // ==================== FILTER BY FREQUENCY ====================
    
    /**
     * Get expenses filtered by frequency.
     * 
     * @param frequency The frequency to filter by (Daily, Weekly, Monthly, Quarterly, Annually)
     * @return LiveData list of expenses with the specified frequency
     */
    fun getExpensesByFrequency(frequency: String): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesByFrequency(frequency)
    }
    
    /**
     * Get expenses filtered by multiple frequencies.
     * 
     * @param frequencies List of frequencies to filter by
     * @return LiveData list of expenses with any of the specified frequencies
     */
    fun getExpensesByFrequencies(frequencies: List<String>): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesByFrequencies(frequencies)
    }
    
    // ==================== COMBINED FILTERS ====================
    
    /**
     * Get expenses filtered by category and date range.
     * 
     * @param category The category to filter by
     * @param startDate Start timestamp (inclusive)
     * @param endDate End timestamp (inclusive)
     * @return LiveData list of expenses matching both criteria
     */
    fun getExpensesByCategoryAndDateRange(
        category: String,
        startDate: Long,
        endDate: Long
    ): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesByCategoryAndDateRange(category, startDate, endDate)
    }
    
    /**
     * Get expenses filtered by category, date range, and amount range.
     * 
     * @param category The category to filter by
     * @param startDate Start timestamp (inclusive)
     * @param endDate End timestamp (inclusive)
     * @param minAmount Minimum amount (inclusive)
     * @param maxAmount Maximum amount (inclusive)
     * @return LiveData list of expenses matching all criteria
     */
    fun getExpensesByCategoryDateAndAmount(
        category: String,
        startDate: Long,
        endDate: Long,
        minAmount: Double,
        maxAmount: Double
    ): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesByCategoryDateAndAmount(
            category, startDate, endDate, minAmount, maxAmount
        )
    }
    
    // ==================== AGGREGATE QUERIES ====================
    
    /**
     * Get the total amount spent across all expenses.
     * 
     * @return Total sum of all expense amounts (null if no expenses)
     */
    suspend fun getTotalExpenses(): Double? {
        return withContext(Dispatchers.IO) {
            expenseDao.getTotalExpenses()
        }
    }
    
    /**
     * Get the total amount spent within a date range.
     * 
     * @param startDate Start timestamp (inclusive)
     * @param endDate End timestamp (inclusive)
     * @return Total sum of expenses within the date range
     */
    suspend fun getTotalExpensesByDateRange(startDate: Long, endDate: Long): Double? {
        return withContext(Dispatchers.IO) {
            expenseDao.getTotalExpensesByDateRange(startDate, endDate)
        }
    }
    
    /**
     * Get the total amount spent by category.
     * 
     * @param category The category to sum
     * @return Total sum of expenses in the category
     */
    suspend fun getTotalByCategory(category: String): Double? {
        return withContext(Dispatchers.IO) {
            expenseDao.getTotalByCategory(category)
        }
    }
    
    /**
     * Get the count of all expenses.
     * 
     * @return Total number of expense records
     */
    suspend fun getExpenseCount(): Int {
        return withContext(Dispatchers.IO) {
            expenseDao.getExpenseCount()
        }
    }
    
    /**
     * Get the count of expenses within a date range.
     * 
     * @param startDate Start timestamp (inclusive)
     * @param endDate End timestamp (inclusive)
     * @return Number of expenses within the date range
     */
    suspend fun getExpenseCountByDateRange(startDate: Long, endDate: Long): Int {
        return withContext(Dispatchers.IO) {
            expenseDao.getExpenseCountByDateRange(startDate, endDate)
        }
    }
    
    /**
     * Get the average expense amount.
     * 
     * @return Average amount across all expenses
     */
    suspend fun getAverageExpense(): Double? {
        return withContext(Dispatchers.IO) {
            expenseDao.getAverageExpense()
        }
    }
    
    /**
     * Get the highest expense amount.
     * 
     * @return Maximum expense amount
     */
    suspend fun getHighestExpense(): Double? {
        return withContext(Dispatchers.IO) {
            expenseDao.getHighestExpense()
        }
    }
    
    /**
     * Get the lowest expense amount.
     * 
     * @return Minimum expense amount
     */
    suspend fun getLowestExpense(): Double? {
        return withContext(Dispatchers.IO) {
            expenseDao.getLowestExpense()
        }
    }
    
    // ==================== CATEGORY STATISTICS ====================
    
    /**
     * Get all unique categories from expenses.
     * 
     * @return LiveData list of distinct category names
     */
    fun getAllCategories(): LiveData<List<String>> {
        return expenseDao.getAllCategories()
    }
    
    /**
     * Get total amount spent per category.
     * 
     * @return LiveData list of category totals
     */
    fun getCategoryTotals(): LiveData<List<ExpenseDao.CategoryTotal>> {
        return expenseDao.getCategoryTotals()
    }
    
    // ==================== RECENT EXPENSES ====================
    
    /**
     * Get the most recent expenses (limited count).
     * 
     * @param limit Maximum number of expenses to return
     * @return LiveData list of the most recent expenses
     */
    fun getRecentExpenses(limit: Int = 10): LiveData<List<ExpenseEntity>> {
        return expenseDao.getRecentExpenses(limit)
    }
    
    /**
     * Get the most recent expense.
     * 
     * @return LiveData of the most recent expense
     */
    fun getMostRecentExpense(): LiveData<ExpenseEntity?> {
        return expenseDao.getMostRecentExpense()
    }
    
    // ==================== SEARCH ====================
    
    /**
     * Search expenses by note content or category.
     * 
     * @param query The search query string
     * @return LiveData list of expenses matching the search query
     */
    fun searchExpenses(query: String): LiveData<List<ExpenseEntity>> {
        return expenseDao.searchExpenses(query)
    }
}