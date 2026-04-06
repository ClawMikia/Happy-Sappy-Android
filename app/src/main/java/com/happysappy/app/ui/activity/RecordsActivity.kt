// File: ui/activity/RecordsActivity.kt
package com.happysappy.app.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.happysappy.app.R
import com.happysappy.app.databinding.ActivityRecordsBinding
import com.happysappy.app.ui.adapter.ExpenseAdapter
import com.happysappy.app.ui.viewmodel.ExpenseViewModel

/**
 * RecordsActivity - Expense Records Screen
 * 
 * This activity displays all expenses with:
 * - Search functionality
 * - Filter and sort options
 * - Full expense list with RecyclerView
 * - Delete functionality
 */
class RecordsActivity : BaseActivity() {

    private lateinit var binding: ActivityRecordsBinding
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        // Setup RecyclerView
        setupRecyclerView()

        // Setup search
        setupSearch()

        // Setup filter and sort
        setupFilterAndSort()

        // Setup bottom navigation
        setupBottomNavigation()

        // Observe ViewModel data
        observeViewModel()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter { expense ->
            showDeleteConfirmation(expense)
        }
        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(this@RecordsActivity)
            adapter = expenseAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchExpenses(query)
                    binding.btnClearSearch.visibility = View.VISIBLE
                } else {
                    viewModel.clearSearch()
                    binding.btnClearSearch.visibility = View.GONE
                    viewModel.clearFilters()
                }
            }
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
            binding.btnClearSearch.visibility = View.GONE
            viewModel.clearFilters()
        }
    }

    private fun setupFilterAndSort() {
        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }

        binding.btnSort.setOnClickListener {
            showSortDialog()
        }
    }

    private fun showFilterDialog() {
        val categories = arrayOf(
            "All",
            getString(R.string.category_food),
            getString(R.string.category_transport),
            getString(R.string.category_shopping),
            getString(R.string.category_entertainment),
            getString(R.string.category_bills),
            getString(R.string.category_health),
            getString(R.string.category_education),
            getString(R.string.category_other)
        )

        AlertDialog.Builder(this)
            .setTitle(R.string.records_filter)
            .setItems(categories) { _, which ->
                if (which == 0) {
                    viewModel.clearFilters()
                } else {
                    viewModel.filterByCategory(categories[which])
                }
            }
            .setNegativeButton(R.string.records_cancel, null)
            .show()
    }

    private fun showSortDialog() {
        val sortOptions = arrayOf(
            "Date (Newest First)",
            "Date (Oldest First)",
            "Amount (High to Low)",
            "Amount (Low to High)",
            "Category (A-Z)"
        )

        AlertDialog.Builder(this)
            .setTitle(R.string.records_sort)
            .setItems(sortOptions) { _, which ->
                when (which) {
                    0 -> viewModel.sortExpenses(ExpenseViewModel.SortBy.DATE_NEW_TO_OLD)
                    1 -> viewModel.sortExpenses(ExpenseViewModel.SortBy.DATE_OLD_TO_NEW)
                    2 -> viewModel.sortExpenses(ExpenseViewModel.SortBy.AMOUNT_HIGH_TO_LOW)
                    3 -> viewModel.sortExpenses(ExpenseViewModel.SortBy.AMOUNT_LOW_TO_HIGH)
                    4 -> viewModel.sortExpenses(ExpenseViewModel.SortBy.CATEGORY)
                }
            }
            .setNegativeButton(R.string.records_cancel, null)
            .show()
    }

    private fun showDeleteConfirmation(expense: com.happysappy.app.data.entity.ExpenseEntity) {
        AlertDialog.Builder(this)
            .setTitle(R.string.records_delete_confirm)
            .setMessage("${expense.category} - ${viewModel.formatCurrency(expense.amountSpent)}")
            .setPositiveButton(R.string.records_delete) { _, _ ->
                viewModel.deleteExpense(expense)
            }
            .setNegativeButton(R.string.records_cancel, null)
            .show()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.apply {
            selectedItemId = R.id.navigation_records
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_dashboard -> {
                        startActivity(android.content.Intent(this@RecordsActivity, MainActivity::class.java))
                        true
                    }
                    R.id.navigation_records -> true
                    R.id.navigation_add -> {
                        startActivity(android.content.Intent(this@RecordsActivity, AddExpenseActivity::class.java))
                        true
                    }
                    R.id.navigation_settings -> {
                        startActivity(android.content.Intent(this@RecordsActivity, SettingsActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun observeViewModel() {
        // Observe all expenses or search results
        viewModel.allExpenses.observe(this) { expenses ->
            expenseAdapter.submitList(expenses)
            updateEmptyState(expenses.isEmpty())
        }

        viewModel.searchResults.observe(this) { results ->
            if (results.isNotEmpty()) {
                expenseAdapter.submitList(results)
                updateEmptyState(false)
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                // Handle error
                viewModel.clearError()
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvExpenses.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}