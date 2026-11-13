package com.example.spendwise.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spendwise.utils.NotificationHelper
import com.example.spendwise.utils.NotificationScheduler

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        NotificationHelper.showDailyReminder(applicationContext)
        NotificationScheduler.scheduleNextDaily(applicationContext)
        return Result.success()
    }
}

