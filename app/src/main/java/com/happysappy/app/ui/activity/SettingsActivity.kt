// File: ui/activity/SettingsActivity.kt
package com.happysappy.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.happysappy.app.R
import com.happysappy.app.databinding.ActivitySettingsBinding
import com.happysappy.app.util.PreferenceManager

/**
 * SettingsActivity - App Settings Screen
 * 
 * This activity provides settings for:
 * - Theme selection (Light, Dark, System)
 * - Notification preferences
 * - Data management (Export, Clear)
 * - About information
 */
class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferenceManager: PreferenceManager
    
    // Currency data
    private val currencies = listOf(
        "USD - US Dollar ($)",
        "EUR - Euro (€)",
        "GBP - British Pound (£)",
        "JPY - Japanese Yen (¥)",
        "CNY - Chinese Yuan (¥)",
        "AUD - Australian Dollar (A$)",
        "CAD - Canadian Dollar (C$)",
        "CHF - Swiss Franc (Fr)",
        "INR - Indian Rupee (₹)",
        "KRW - South Korean Won (₩)",
        "SGD - Singapore Dollar (S$)",
        "HKD - Hong Kong Dollar (HK$)",
        "NZD - New Zealand Dollar (NZ$)",
        "SEK - Swedish Krona (kr)",
        "NOK - Norwegian Krone (kr)",
        "DKK - Danish Krone (kr)",
        "MXN - Mexican Peso ($)",
        "BRL - Brazilian Real (R$)",
        "ZAR - South African Rand (R)",
        "RUB - Russian Ruble (₽)",
        "TRY - Turkish Lira (₺)",
        "AED - UAE Dirham (د.إ)",
        "SAR - Saudi Riyal (﷼)",
        "THB - Thai Baht (฿)",
        "MYR - Malaysian Ringgit (RM)",
        "IDR - Indonesian Rupiah (Rp)",
        "PHP - Philippine Peso (₱)",
        "VND - Vietnamese Dong (₫)",
        "PLN - Polish Zloty (zł)",
        "CZK - Czech Koruna (Kč)"
    )
    
    // Currency code mapping
    private val currencyCodes = mapOf(
        "USD - US Dollar ($)" to "USD",
        "EUR - Euro (€)" to "EUR",
        "GBP - British Pound (£)" to "GBP",
        "JPY - Japanese Yen (¥)" to "JPY",
        "CNY - Chinese Yuan (¥)" to "CNY",
        "AUD - Australian Dollar (A$)" to "AUD",
        "CAD - Canadian Dollar (C$)" to "CAD",
        "CHF - Swiss Franc (Fr)" to "CHF",
        "INR - Indian Rupee (₹)" to "INR",
        "KRW - South Korean Won (₩)" to "KRW",
        "SGD - Singapore Dollar (S$)" to "SGD",
        "HKD - Hong Kong Dollar (HK$)" to "HKD",
        "NZD - New Zealand Dollar (NZ$)" to "NZD",
        "SEK - Swedish Krona (kr)" to "SEK",
        "NOK - Norwegian Krone (kr)" to "NOK",
        "DKK - Danish Krone (kr)" to "DKK",
        "MXN - Mexican Peso ($)" to "MXN",
        "BRL - Brazilian Real (R$)" to "BRL",
        "ZAR - South African Rand (R)" to "ZAR",
        "RUB - Russian Ruble (₽)" to "RUB",
        "TRY - Turkish Lira (₺)" to "TRY",
        "AED - UAE Dirham (د.إ)" to "AED",
        "SAR - Saudi Riyal (﷼)" to "SAR",
        "THB - Thai Baht (฿)" to "THB",
        "MYR - Malaysian Ringgit (RM)" to "MYR",
        "IDR - Indonesian Rupiah (Rp)" to "IDR",
        "PHP - Philippine Peso (₱)" to "PHP",
        "VND - Vietnamese Dong (₫)" to "VND",
        "PLN - Polish Zloty (zł)" to "PLN",
        "CZK - Czech Koruna (Kč)" to "CZK"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Preference Manager
        preferenceManager = PreferenceManager(this)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        setToolbarTitleTextColorWhite()

        // Load current settings
        loadSettings()

        // Setup click listeners
        setupNotificationToggles()
        setupDataManagement()
        setupAboutSection()

        // Setup bottom navigation
        setupBottomNavigation()
    }

    private fun loadSettings() {
        // Load currency setting
        setupCurrencySpinner()
        

        // Load notification settings
        binding.switchNotifications.isChecked = preferenceManager.areNotificationsEnabled()
        binding.switchDailyReminder.isChecked = preferenceManager.isDailyReminderEnabled()
        binding.switchWeeklySummary.isChecked = preferenceManager.isWeeklySummaryEnabled()
        binding.switchMonthlyReport.isChecked = preferenceManager.isMonthlyReportEnabled()

        // Update notification toggles state based on main switch
        updateNotificationTogglesState()
    }
    
    private fun setupCurrencySpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            currencies
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = adapter
        
        // Set current currency
        val currentCurrency = preferenceManager.getCurrency()
        val currencyDisplay = currencyCodes.entries.find { it.value == currentCurrency }?.key ?: currencies[0]
        val index = currencies.indexOf(currencyDisplay)
        if (index >= 0) {
            binding.spinnerCurrency.setSelection(index)
        }
        
        // Handle currency selection
        binding.spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCurrency = currencies[position]
                val currencyCode = currencyCodes[selectedCurrency] ?: "USD"
                preferenceManager.saveCurrency(currencyCode)
                Toast.makeText(this@SettingsActivity, "Currency changed to $currencyCode", Toast.LENGTH_SHORT).show()
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun applyTheme(theme: String) {
        val mode = when (theme) {
            PreferenceManager.THEME_LIGHT -> ""
            PreferenceManager.THEME_DARK -> ""
            PreferenceManager.THEME_SYSTEM -> ""
            else -> ""
        }
        // Theme functionality removed
        
        // Navigate back to MainActivity to apply theme across the app
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupNotificationToggles() {
        // Main notification toggle
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.saveNotificationsEnabled(isChecked)
            updateNotificationTogglesState()
        }

        // Individual notification toggles
        binding.switchDailyReminder.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.saveDailyReminderEnabled(isChecked)
        }

        binding.switchWeeklySummary.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.saveWeeklySummaryEnabled(isChecked)
        }

        binding.switchMonthlyReport.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.saveMonthlyReportEnabled(isChecked)
        }
    }

    private fun updateNotificationTogglesState() {
        val isEnabled = binding.switchNotifications.isChecked
        binding.switchDailyReminder.isEnabled = isEnabled
        binding.switchWeeklySummary.isEnabled = isEnabled
        binding.switchMonthlyReport.isEnabled = isEnabled
    }

    private fun setupDataManagement() {
        binding.btnExport.setOnClickListener {
            exportData()
        }

        binding.btnClearData.setOnClickListener {
            showClearDataConfirmation()
        }
    }

    private fun exportData() {
        // TODO: Implement data export functionality
        Toast.makeText(this, "Export feature coming soon!", Toast.LENGTH_SHORT).show()
    }

    private fun showClearDataConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.settings_clear)
            .setMessage(R.string.settings_clear_confirm)
            .setPositiveButton(R.string.yes) { _, _ ->
                clearAllData()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun clearAllData() {
        // TODO: Implement data clearing
        // This would involve clearing the Room database
        Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show()
    }

    private fun setupAboutSection() {
        binding.tvPrivacy.setOnClickListener {
            // TODO: Open privacy policy
            Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        setupRainbowNavigation(binding.bottomNavigation)
        binding.bottomNavigation.apply {
            selectedItemId = R.id.navigation_settings
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_dashboard -> {
                        startActivity(Intent(this@SettingsActivity, MainActivity::class.java))
                        true
                    }
                    R.id.navigation_records -> {
                        startActivity(Intent(this@SettingsActivity, RecordsActivity::class.java))
                        true
                    }
                    R.id.navigation_add -> {
                        startActivity(Intent(this@SettingsActivity, AddExpenseActivity::class.java))
                        true
                    }
                    R.id.navigation_settings -> true
                    else -> false
                }
            }
        }
    }
}