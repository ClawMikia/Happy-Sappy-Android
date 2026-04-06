// File: ui/activity/BaseActivity.kt
package com.happysappy.app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
}
