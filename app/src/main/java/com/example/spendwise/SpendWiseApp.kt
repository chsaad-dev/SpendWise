package com.example.spendwise

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.spendwise.utils.NotificationHelper
import com.google.android.material.color.DynamicColors

class SpendWiseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        NotificationHelper.createNotificationChannels(this)

        val themeMode = getSavedThemeMode(this)
        applyThemeMode(themeMode)
    }

    companion object {
        const val PREFS_NAME = "SpendWisePrefs"
        const val KEY_THEME_MODE = "theme_mode"
        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"

        fun getSavedThemeMode(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_THEME_MODE, null) ?: run {
                // If legacy boolean "dark_mode" was saved by user, respect it; otherwise default to "system"
                if (prefs.contains("dark_mode")) {
                    if (prefs.getBoolean("dark_mode", false)) THEME_DARK else THEME_LIGHT
                } else {
                    THEME_SYSTEM
                }
            }
        }

        fun applyThemeMode(themeMode: String) {
            val mode = when (themeMode) {
                THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }
}
