// File: ui/activity/MainActivity.kt
package com.happysappy.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.happysappy.app.R
import com.happysappy.app.data.entity.ExpenseEntity
import com.happysappy.app.databinding.ActivityMainBinding
import com.happysappy.app.ui.adapter.ExpenseAdapter
import com.happysappy.app.ui.viewmodel.ExpenseViewModel

/**
 * MainActivity - Dashboard Screen
 * 
 * This is the main entry point of the app, displaying:
 * - Total spent, expense count, and average
 * - Gamification stats (streaks, level, XP)
 * - Recent expenses list
 * - Bottom navigation to other screens
 */
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Happy Sappy"
        setToolbarTitleTextColorWhite()

        // Setup RecyclerView
        setupRecyclerView()

        // Observe ViewModel data
        observeViewModel()

        // Setup bottom navigation
        setupBottomNavigation()

        // Setup click listeners
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter { expense ->
            // Handle delete
            viewModel.deleteExpense(expense)
        }
        binding.rvRecentExpenses.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = expenseAdapter
        }
    }

    private fun observeViewModel() {
        // Observe statistics
        viewModel.totalExpenses.observe(this) { total ->
            binding.tvTotalSpent.text = viewModel.formatCurrency(total ?: 0.0)
        }

        viewModel.expenseCount.observe(this) { count ->
            binding.tvExpenseCount.text = count.toString()
        }

        viewModel.averageExpense.observe(this) { average ->
            binding.tvAverage.text = viewModel.formatCurrency(average ?: 0.0)
        }

        // Observe recent expenses
        viewModel.recentExpenses.observe(this) { expenses ->
            expenseAdapter.submitList(expenses)
            
            // Show/hide empty state
            if (expenses.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.rvRecentExpenses.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.rvRecentExpenses.visibility = View.VISIBLE
            }
        }

        // Observe gamification data (simplified for now)
        viewModel.allExpenses.observe(this) { allExpenses ->
            updateGamificationStats(allExpenses)
        }

        // Observe errors
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                // Show error (could use Snackbar)
                viewModel.clearError()
            }
        }
    }

    private fun updateGamificationStats(expenses: List<ExpenseEntity>) {
        // Calculate streak (simplified)
        val streak = calculateStreak(expenses)
        binding.tvStreak.text = getString(R.string.gamification_streak_days, streak)
        binding.progressStreak.progress = (streak * 10).coerceAtMost(100)

        // Calculate level and XP (simplified)
        val xp = expenses.size * 10 // 10 XP per expense
        val level = when {
            xp >= 1001 -> 5
            xp >= 601 -> 4
            xp >= 301 -> 3
            xp >= 101 -> 2
            else -> 1
        }
        binding.tvLevel.text = getString(R.string.gamification_level, level)
        binding.tvXP.text = getString(R.string.gamification_xp, xp)
    }

    private fun calculateStreak(expenses: List<ExpenseEntity>): Int {
        if (expenses.isEmpty()) return 0
        
        val today = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L
        var streak = 0
        var currentDate = today

        // Simple streak calculation
        val sortedExpenses = expenses.sortedByDescending { it.date }
        for (expense in sortedExpenses) {
            val diff = currentDate - expense.date
            if (diff <= oneDay) {
                streak++
                currentDate = expense.date
            } else {
                break
            }
        }
        return streak
    }

    private fun setupBottomNavigation() {
        setupRainbowNavigation(binding.bottomNavigation)
        binding.bottomNavigation.apply {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_dashboard -> {
                        // Already on dashboard
                        true
                    }
                    R.id.navigation_records -> {
                        startActivity(Intent(this@MainActivity, RecordsActivity::class.java))
                        true
                    }
                    R.id.navigation_add -> {
                        startActivity(Intent(this@MainActivity, AddExpenseActivity::class.java))
                        true
                    }
                    R.id.navigation_settings -> {
                        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupClickListeners() {
        // View all expenses
        binding.tvViewAll.setOnClickListener {
            startActivity(Intent(this, RecordsActivity::class.java))
        }
    }
}