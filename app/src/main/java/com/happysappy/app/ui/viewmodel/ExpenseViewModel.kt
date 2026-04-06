// File: ui/viewmodel/ExpenseViewModel.kt
package com.happysappy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happysappy.app.data.dao.ExpenseDao
import com.happysappy.app.data.entity.ExpenseEntity
import com.happysappy.app.data.repository.ExpenseRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel for managing expense-related UI data and business logic.
 * 
 * This ViewModel follows the MVVM pattern and provides a clean separation
 * between the UI and the data layer. It manages the lifecycle-aware data
 * and handles all database operations through the repository.
 * 
 * Key Features:
 * - LiveData for automatic UI updates
 * - Coroutine-based async operations
 * - Filtering and sorting capabilities
 * - Statistics and aggregation
 * - Search functionality
 */
class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    
    // Repository instance for data operations
    private val repository: ExpenseRepository
    
    // ==================== LiveData for UI Observation ====================
    
    // All expenses list
    private val _allExpenses: MutableLiveData<List<ExpenseEntity>> = MutableLiveData()
    val allExpenses: LiveData<List<ExpenseEntity>> = _allExpenses
    
    // Loading state
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Error messages
    private val _errorMessage: MutableLiveData<String?> = MutableLiveData(null)
    val errorMessage: LiveData<String?> = _errorMessage
    
    // Current filter state
    private val _currentFilter: MutableLiveData<FilterOptions> = MutableLiveData(FilterOptions())
    val currentFilter: LiveData<FilterOptions> = _currentFilter
    
    // Statistics data
    private val _totalExpenses: MutableLiveData<Double> = MutableLiveData(0.0)
    val totalExpenses: LiveData<Double> = _totalExpenses
    
    private val _expenseCount: MutableLiveData<Int> = MutableLiveData(0)
    val expenseCount: LiveData<Int> = _expenseCount
    
    private val _averageExpense: MutableLiveData<Double> = MutableLiveData(0.0)
    val averageExpense: LiveData<Double> = _averageExpense
    
    // Category totals for charts
    private val _categoryTotals: MutableLiveData<List<ExpenseDao.CategoryTotal>> = MutableLiveData()
    val categoryTotals: LiveData<List<ExpenseDao.CategoryTotal>> = _categoryTotals
    
    // Recent expenses
    private val _recentExpenses: MutableLiveData<List<ExpenseEntity>> = MutableLiveData()
    val recentExpenses: LiveData<List<ExpenseEntity>> = _recentExpenses
    
    // Search results
    private val _searchResults: MutableLiveData<List<ExpenseEntity>> = MutableLiveData()
    val searchResults: LiveData<List<ExpenseEntity>> = _searchResults
    
    // Available categories
    private val _categories: MutableLiveData<List<String>> = MutableLiveData()
    val categories: LiveData<List<String>> = _categories
    
    init {
        // Initialize repository with application context
        repository = ExpenseRepository(application)
        
        // Load initial data
        loadAllExpenses()
        loadStatistics()
        loadCategories()
        loadCategoryTotals()
        loadRecentExpenses()
    }
    
    // ==================== CRUD OPERATIONS ====================
    
    /**
     * Add a new expense to the database.
     * 
     * @param amountSpent The amount spent
     * @param category The expense category
     * @param frequency The frequency (Daily, Weekly, Monthly, Quarterly, Annually)
     * @param note Optional note or description
     * @param date Optional date (defaults to current time)
     */
    fun addExpense(
        amountSpent: Double,
        category: String,
        frequency: String,
        note: String? = null,
        date: Long = System.currentTimeMillis()
    ) {
        // Validate input
        if (amountSpent <= 0) {
            _errorMessage.value = "Amount must be greater than zero"
            return
        }
        
        if (!ExpenseEntity.isValidFrequency(frequency)) {
            _errorMessage.value = "Invalid frequency. Must be Daily, Weekly, Monthly, Quarterly, or Annually"
            return
        }
        
        if (category.isBlank()) {
            _errorMessage.value = "Category cannot be empty"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val expense = ExpenseEntity(
                    amountSpent = amountSpent,
                    category = category,
                    date = date,
                    frequency = frequency,
                    note = note
                )
                repository.insertExpense(expense)
                _errorMessage.value = null
                
                // Refresh data after insertion
                loadAllExpenses()
                loadStatistics()
                loadRecentExpenses()
                loadCategoryTotals()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add expense: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update an existing expense.
     * 
     * @param expense The expense entity with updated values
     */
    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateExpense(expense)
                _errorMessage.value = null
                
                // Refresh data after update
                loadAllExpenses()
                loadStatistics()
                loadRecentExpenses()
                loadCategoryTotals()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update expense: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Delete an expense.
     * 
     * @param expense The expense entity to delete
     */
    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteExpense(expense)
                _errorMessage.value = null
                
                // Refresh data after deletion
                loadAllExpenses()
                loadStatistics()
                loadRecentExpenses()
                loadCategoryTotals()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete expense: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Delete an expense by ID.
     * 
     * @param expenseId The ID of the expense to delete
     */
    fun deleteExpenseById(expenseId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteExpenseById(expenseId)
                _errorMessage.value = null
                
                // Refresh data after deletion
                loadAllExpenses()
                loadStatistics()
                loadRecentExpenses()
                loadCategoryTotals()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete expense: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // ==================== LOAD DATA OPERATIONS ====================
    
    /**
     * Load all expenses from the database.
     * Applies current filter if any.
     */
    private fun loadAllExpenses() {
        val filter = _currentFilter.value ?: FilterOptions()
        
        // Observe expenses based on current filter
        val expensesLiveData = when {
            filter.category != null && filter.startDate != null && filter.endDate != null -> {
                repository.getExpensesByCategoryAndDateRange(
                    filter.category,
                    filter.startDate!!,
                    filter.endDate!!
                )
            }
            filter.category != null -> repository.getExpensesByCategory(filter.category)
            filter.startDate != null && filter.endDate != null -> {
                repository.getExpensesByDateRange(filter.startDate!!, filter.endDate!!)
            }
            filter.frequency != null -> repository.getExpensesByFrequency(filter.frequency!!)
            else -> repository.getAllExpenses()
        }
        
        expensesLiveData.observeForever { expenses ->
            // Apply amount filter if specified
            val filteredExpenses = if (filter.minAmount != null || filter.maxAmount != null) {
                expenses.filter { expense ->
                    val minCheck = filter.minAmount == null || expense.amountSpent >= filter.minAmount!!
                    val maxCheck = filter.maxAmount == null || expense.amountSpent <= filter.maxAmount!!
                    minCheck && maxCheck
                }
            } else {
                expenses
            }
            
            // Apply sorting
            val sortedExpenses = when (filter.sortBy) {
                SortBy.AMOUNT_HIGH_TO_LOW -> filteredExpenses.sortedByDescending { it.amountSpent }
                SortBy.AMOUNT_LOW_TO_HIGH -> filteredExpenses.sortedBy { it.amountSpent }
                SortBy.CATEGORY -> filteredExpenses.sortedBy { it.category }
                SortBy.DATE_OLD_TO_NEW -> filteredExpenses.sortedBy { it.date }
                SortBy.DATE_NEW_TO_OLD, null -> filteredExpenses.sortedByDescending { it.date }
            }
            
            _allExpenses.value = sortedExpenses
        }
    }
    
    /**
     * Load statistics data (total, count, average).
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                _totalExpenses.value = repository.getTotalExpenses() ?: 0.0
                _expenseCount.value = repository.getExpenseCount()
                _averageExpense.value = repository.getAverageExpense() ?: 0.0
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load statistics: ${e.message}"
            }
        }
    }
    
    /**
     * Load category totals for charts.
     */
    private fun loadCategoryTotals() {
        repository.getCategoryTotals().observeForever { totals ->
            _categoryTotals.value = totals
        }
    }
    
    /**
     * Load recent expenses (limited to 10).
     */
    private fun loadRecentExpenses() {
        repository.getRecentExpenses(10).observeForever { expenses ->
            _recentExpenses.value = expenses
        }
    }
    
    /**
     * Load all unique categories.
     */
    private fun loadCategories() {
        repository.getAllCategories().observeForever { categories ->
            _categories.value = categories
        }
    }
    
    // ==================== FILTER OPERATIONS ====================
    
    /**
     * Apply filter options to the expense list.
     * 
     * @param filterOptions The filter options to apply
     */
    fun applyFilter(filterOptions: FilterOptions) {
        _currentFilter.value = filterOptions
        loadAllExpenses()
    }
    
    /**
     * Filter expenses by category.
     * 
     * @param category The category to filter by
     */
    fun filterByCategory(category: String) {
        val currentOptions = _currentFilter.value ?: FilterOptions()
        applyFilter(currentOptions.copy(category = category))
    }
    
    /**
     * Filter expenses by date range.
     * 
     * @param startDate Start timestamp (inclusive)
     * @param endDate End timestamp (inclusive)
     */
    fun filterByDateRange(startDate: Long, endDate: Long) {
        val currentOptions = _currentFilter.value ?: FilterOptions()
        applyFilter(currentOptions.copy(startDate = startDate, endDate = endDate))
    }
    
    /**
     * Filter expenses by amount range.
     * 
     * @param minAmount Minimum amount (inclusive)
     * @param maxAmount Maximum amount (inclusive)
     */
    fun filterByAmountRange(minAmount: Double?, maxAmount: Double?) {
        val currentOptions = _currentFilter.value ?: FilterOptions()
        applyFilter(currentOptions.copy(minAmount = minAmount, maxAmount = maxAmount))
    }
    
    /**
     * Filter expenses by frequency.
     * 
     * @param frequency The frequency to filter by
     */
    fun filterByFrequency(frequency: String) {
        val currentOptions = _currentFilter.value ?: FilterOptions()
        applyFilter(currentOptions.copy(frequency = frequency))
    }
    
    /**
     * Clear all filters and show all expenses.
     */
    fun clearFilters() {
        _currentFilter.value = FilterOptions()
        loadAllExpenses()
    }
    
    /**
     * Sort expenses by specified criteria.
     * 
     * @param sortBy The sort criteria
     */
    fun sortExpenses(sortBy: SortBy) {
        val currentOptions = _currentFilter.value ?: FilterOptions()
        applyFilter(currentOptions.copy(sortBy = sortBy))
    }
    
    // ==================== SEARCH OPERATIONS ====================
    
    /**
     * Search expenses by query string.
     * 
     * @param query The search query
     */
    fun searchExpenses(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        repository.searchExpenses(query).observeForever { results ->
            _searchResults.value = results
        }
    }
    
    /**
     * Clear search results.
     */
    fun clearSearch() {
        _searchResults.value = emptyList()
    }
    
    // ==================== STATISTICS OPERATIONS ====================
    
    /**
     * Get total expenses for a specific date range.
     * 
     * @param startDate Start timestamp
     * @param endDate End timestamp
     * @return Total amount spent in the date range
     */
    suspend fun getTotalExpensesByDateRange(startDate: Long, endDate: Long): Double? {
        return repository.getTotalExpensesByDateRange(startDate, endDate)
    }
    
    /**
     * Get total expenses by category.
     * 
     * @param category The category
     * @return Total amount spent in the category
     */
    suspend fun getTotalByCategory(category: String): Double? {
        return repository.getTotalByCategory(category)
    }
    
    /**
     * Get expense count for a date range.
     * 
     * @param startDate Start timestamp
     * @param endDate End timestamp
     * @return Number of expenses in the date range
     */
    suspend fun getExpenseCountByDateRange(startDate: Long, endDate: Long): Int {
        return repository.getExpenseCountByDateRange(startDate, endDate)
    }
    
    /**
     * Get highest expense amount.
     * 
     * @return The highest expense amount
     */
    suspend fun getHighestExpense(): Double? {
        return repository.getHighestExpense()
    }
    
    /**
     * Get lowest expense amount.
     * 
     * @return The lowest expense amount
     */
    suspend fun getLowestExpense(): Double? {
        return repository.getLowestExpense()
    }
    
    // ==================== UTILITY FUNCTIONS ====================
    
    /**
     * Format timestamp to readable date string.
     * 
     * @param timestamp The timestamp to format
     * @param pattern The date format pattern (default: "MMM dd, yyyy")
     * @return Formatted date string
     */
    fun formatDate(timestamp: Long, pattern: String = "MMM dd, yyyy"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to time string.
     * 
     * @param timestamp The timestamp to format
     * @param pattern The time format pattern (default: "hh:mm a")
     * @return Formatted time string
     */
    fun formatTime(timestamp: Long, pattern: String = "hh:mm a"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format amount as currency string.
     * 
     * @param amount The amount to format
     * @return Formatted currency string
     */
    fun formatCurrency(amount: Double): String {
        return String.format(Locale.getDefault(), "$%.2f", amount)
    }
    
    /**
     * Clear error message.
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Get start of day timestamp.
     * 
     * @param date The date (defaults to today)
     * @return Timestamp for start of day (00:00:00)
     */
    fun getStartOfDay(date: Date = Date()): Long {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    /**
     * Get end of day timestamp.
     * 
     * @param date The date (defaults to today)
     * @return Timestamp for end of day (23:59:59)
     */
    fun getEndOfDay(date: Date = Date()): Long {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }
    
    // ==================== DATA CLASSES ====================
    
    /**
     * Data class for filter options.
     */
    data class FilterOptions(
        val category: String? = null,
        val startDate: Long? = null,
        val endDate: Long? = null,
        val minAmount: Double? = null,
        val maxAmount: Double? = null,
        val frequency: String? = null,
        val sortBy: SortBy? = SortBy.DATE_NEW_TO_OLD
    )
    
    /**
     * Enum for sort options.
     */
    enum class SortBy {
        DATE_NEW_TO_OLD,
        DATE_OLD_TO_NEW,
        AMOUNT_HIGH_TO_LOW,
        AMOUNT_LOW_TO_HIGH,
        CATEGORY
    }
}