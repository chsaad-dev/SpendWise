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

        val prefs = getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
