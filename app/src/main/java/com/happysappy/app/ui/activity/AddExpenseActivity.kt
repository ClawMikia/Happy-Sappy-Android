// File: ui/activity/AddExpenseActivity.kt
package com.happysappy.app.ui.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.happysappy.app.R
import com.happysappy.app.data.entity.ExpenseEntity
import com.happysappy.app.databinding.ActivityAddExpenseBinding
import com.happysappy.app.ui.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * AddExpenseActivity - Add New Expense Screen
 * 
 * This activity allows users to add a new expense with:
 * - Amount input
 * - Category selection (via chips)
 * - Frequency selection (Daily, Weekly, Monthly, Quarterly, Annually)
 * - Date selection
 * - Optional note
 */
class AddExpenseActivity : BaseActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var viewModel: ExpenseViewModel
    
    private var selectedCategory: String = ""
    private var selectedFrequency: String = ExpenseEntity.FREQUENCY_DAILY
    private var selectedDate: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        setToolbarTitleTextColorWhite()

        // Setup bottom navigation
        setupBottomNavigation()

        // Setup UI components
        setupFrequencySpinner()
        setupDateSelection()
        setupCategoryChips()
        setupAddButton()
        
        // Set default date
        updateDateDisplay()
    }

    private fun setupFrequencySpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ExpenseEntity.VALID_FREQUENCIES
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFrequency.adapter = adapter
        
        binding.spinnerFrequency.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                selectedFrequency = ExpenseEntity.VALID_FREQUENCIES[position]
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                selectedFrequency = ExpenseEntity.FREQUENCY_DAILY
            }
        }
    }

    private fun setupDateSelection() {
        binding.tvDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
        
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                selectedDate = newCalendar.timeInMillis
                updateDateDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        binding.tvDate.text = sdf.format(Date(selectedDate))
    }

    private fun setupCategoryChips() {
        val categoryMap = mapOf(
            R.id.chipFood to getString(R.string.category_food),
            R.id.chipTransport to getString(R.string.category_transport),
            R.id.chipShopping to getString(R.string.category_shopping),
            R.id.chipEntertainment to getString(R.string.category_entertainment),
            R.id.chipBills to getString(R.string.category_bills),
            R.id.chipHealth to getString(R.string.category_health),
            R.id.chipEducation to getString(R.string.category_education),
            R.id.chipOther to getString(R.string.category_other)
        )

        binding.chipGroupCategories.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds[0]
                selectedCategory = categoryMap[chipId] ?: ""
            }
        }

        // Select first category by default
        binding.chipFood.isChecked = true
        selectedCategory = getString(R.string.category_food)
    }

    private fun setupAddButton() {
        binding.btnAddExpense.setOnClickListener {
            addExpense()
        }
    }

    private fun addExpense() {
        // Validate inputs
        val amountText = binding.etAmount.text.toString().trim()
        if (amountText.isEmpty()) {
            binding.etAmount.error = getString(R.string.error_amount_required)
            binding.etAmount.requestFocus()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.etAmount.error = getString(R.string.error_amount_invalid)
            binding.etAmount.requestFocus()
            return
        }

        if (selectedCategory.isEmpty()) {
            Toast.makeText(this, R.string.error_category_required, Toast.LENGTH_SHORT).show()
            return
        }

        val note = binding.etNote.text.toString().trim()

        // Add expense via ViewModel
        viewModel.addExpense(
            amountSpent = amount,
            category = selectedCategory,
            frequency = selectedFrequency,
            note = if (note.isNotEmpty()) note else null,
            date = selectedDate
        )

        // Show success message
        Toast.makeText(this, R.string.add_expense_success, Toast.LENGTH_SHORT).show()

        // Clear form and return to dashboard
        clearForm()
        finish()
    }

    private fun setupBottomNavigation() {
        setupRainbowNavigation(binding.bottomNavigation)
        binding.bottomNavigation.apply {
            selectedItemId = R.id.navigation_add
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_dashboard -> {
                        startActivity(Intent(this@AddExpenseActivity, MainActivity::class.java))
                        true
                    }
                    R.id.navigation_records -> {
                        startActivity(Intent(this@AddExpenseActivity, RecordsActivity::class.java))
                        true
                    }
                    R.id.navigation_add -> true
                    R.id.navigation_settings -> {
                        startActivity(Intent(this@AddExpenseActivity, SettingsActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun clearForm() {
        binding.etAmount.text?.clear()
        binding.etNote.text?.clear()
        binding.chipFood.isChecked = true
        binding.spinnerFrequency.setSelection(0)
        selectedDate = System.currentTimeMillis()
        updateDateDisplay()
        selectedCategory = getString(R.string.category_food)
    }
}