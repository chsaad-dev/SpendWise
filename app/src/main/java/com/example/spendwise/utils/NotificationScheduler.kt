package com.example.spendwise.utils

import android.content.Context
import androidx.core.content.edit
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.spendwise.workers.ReminderWorker
import com.example.spendwise.workers.WeeklySummaryWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val PREFS_NAME = "SpendWisePrefs"
    const val KEY_DAILY_ENABLED = "notif_daily_enabled"
    const val KEY_DAILY_HOUR = "notif_daily_hour"
    const val KEY_DAILY_MINUTE = "notif_daily_minute"
    const val KEY_WEEKLY_ENABLED = "notif_weekly_enabled"

    const val WORK_NAME_DAILY = "daily_reminder"
    const val WORK_NAME_WEEKLY = "weekly_summary"

    const val DEFAULT_DAILY_HOUR = 20
    const val DEFAULT_DAILY_MINUTE = 0
    const val DEFAULT_WEEKLY_HOUR = 18
    const val DEFAULT_WEEKLY_MINUTE = 0

    fun enableDaily(context: Context, hour: Int = DEFAULT_DAILY_HOUR, minute: Int = DEFAULT_DAILY_MINUTE) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putBoolean(KEY_DAILY_ENABLED, true)
            putInt(KEY_DAILY_HOUR, hour)
            putInt(KEY_DAILY_MINUTE, minute)
        }

        enqueueDailyWork(context, hour, minute)
    }

    fun disableDaily(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_DAILY_ENABLED, false) }
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_DAILY)
    }

    fun scheduleNextDaily(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean(KEY_DAILY_ENABLED, false)
        if (isEnabled) {
            val hour = prefs.getInt(KEY_DAILY_HOUR, DEFAULT_DAILY_HOUR)
            val minute = prefs.getInt(KEY_DAILY_MINUTE, DEFAULT_DAILY_MINUTE)
            enqueueDailyWork(context, hour, minute)
        }
    }

    private fun enqueueDailyWork(context: Context, hour: Int, minute: Int) {
        val delay = calculateDailyDelay(hour, minute)
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME_DAILY,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun enableWeekly(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_WEEKLY_ENABLED, true) }

        enqueueWeeklyWork(context)
    }

    fun disableWeekly(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_WEEKLY_ENABLED, false) }
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_WEEKLY)
    }

    fun scheduleNextWeekly(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean(KEY_WEEKLY_ENABLED, false)
        if (isEnabled) {
            enqueueWeeklyWork(context)
        }
    }

    private fun enqueueWeeklyWork(context: Context) {
        val delay = calculateWeeklyDelay()
        val workRequest = OneTimeWorkRequestBuilder<WeeklySummaryWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME_WEEKLY,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun rescheduleAll(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dailyEnabled = prefs.getBoolean(KEY_DAILY_ENABLED, false)
        if (dailyEnabled) {
            val hour = prefs.getInt(KEY_DAILY_HOUR, DEFAULT_DAILY_HOUR)
            val minute = prefs.getInt(KEY_DAILY_MINUTE, DEFAULT_DAILY_MINUTE)
            enqueueDailyWork(context, hour, minute)
        } else {
            disableDaily(context)
        }

        val weeklyEnabled = prefs.getBoolean(KEY_WEEKLY_ENABLED, false)
        if (weeklyEnabled) {
            enqueueWeeklyWork(context)
        } else {
            disableWeekly(context)
        }
    }

    fun calculateDailyDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    fun calculateWeeklyDelay(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, DEFAULT_WEEKLY_HOUR)
            set(Calendar.MINUTE, DEFAULT_WEEKLY_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        while (target.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY || !target.after(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }
}
