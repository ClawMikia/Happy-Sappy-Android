// File: ui/activity/BaseActivity.kt
package com.happysappy.app.ui.activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuItemCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.happysappy.app.R
import com.happysappy.app.util.PreferenceManager

/**
 * BaseActivity - Base class for all activities to handle theme application.
 * 
 * This class ensures the user's theme preference is applied before the activity
 * content is set, allowing theme changes to take effect immediately.
 */
abstract class BaseActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize PreferenceManager first
        preferenceManager = PreferenceManager(this)
        
        // Apply theme BEFORE super.onCreate() for it to take effect
        applySavedTheme()
        
        super.onCreate(savedInstanceState)
    }

    /**
     * Apply the saved theme setting.
     * Call this method to refresh the theme when returning to an activity.
     */
    override fun onStart() {
        super.onStart()
        applySavedTheme()
    }

    private fun applySavedTheme() {
        if (!::preferenceManager.isInitialized) return
        
        val theme = preferenceManager.getTheme()
        val mode = when (theme) {
            PreferenceManager.THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            PreferenceManager.THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            PreferenceManager.THEME_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * Get the PreferenceManager instance.
     * Available for subclasses that need to access preferences.
     */
    protected fun getPreferenceManager(): PreferenceManager {
        return preferenceManager
    }

    /**
     * Applies individual colors to bottom navigation items.
     */
    protected fun setupRainbowNavigation(navView: BottomNavigationView) {
        navView.itemIconTintList = null
        navView.itemTextColor = null
        
        val menu = navView.menu
        colorMenuItem(menu.findItem(R.id.navigation_dashboard), getColor(R.color.rainbow_orange))
        colorMenuItem(menu.findItem(R.id.navigation_records), getColor(R.color.rainbow_blue))
        colorMenuItem(menu.findItem(R.id.navigation_add), getColor(R.color.rainbow_green_light))
        colorMenuItem(menu.findItem(R.id.navigation_settings), getColor(R.color.rainbow_purple_light))
    }

    private fun colorMenuItem(item: MenuItem?, color: Int) {
        item?.let {
            MenuItemCompat.setIconTintList(it, ColorStateList.valueOf(color))
            val s = SpannableString(it.title)
            s.setSpan(ForegroundColorSpan(color), 0, s.length, 0)
            it.title = s
        }
    }
    
    /**
     * Sets the toolbar title text color to white.
     */
    protected fun setToolbarTitleTextColorWhite() {
        try {
            val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            toolbar?.setTitleTextColor(getColor(R.color.white))
        } catch (e: Exception) {
            // Log error if needed
        }
    }
}
