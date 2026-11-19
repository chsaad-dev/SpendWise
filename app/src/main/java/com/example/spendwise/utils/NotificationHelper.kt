package com.example.spendwise.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.spendwise.MainActivity
import com.example.spendwise.R
import com.example.spendwise.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

object NotificationHelper {

    const val CHANNEL_REMINDER_ID = "spendwise_reminder"
    const val CHANNEL_WEEKLY_ID = "spendwise_weekly"

    const val NOTIF_ID_REMINDER = 1001
    const val NOTIF_ID_WEEKLY = 1002

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDER_ID,
                "Daily Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you to log expenses daily"
            }

            val weeklyChannel = NotificationChannel(
                CHANNEL_WEEKLY_ID,
                "Weekly Summary",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Weekly spending summary notification"
            }

            manager.createNotificationChannel(reminderChannel)
            manager.createNotificationChannel(weeklyChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun showDailyReminder(context: Context) {
        if (!hasNotificationPermission(context)) return

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDER_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("SpendWise Reminder \uD83D\uDCB0")
            .setContentText("Don't forget to log today's expenses!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_REMINDER, notification)
        } catch (e: SecurityException) {
            // Silently skip if permission was revoked concurrently
        }
    }

    suspend fun showWeeklySummary(context: Context) = withContext(Dispatchers.IO) {
        if (!hasNotificationPermission(context)) return@withContext

        try {
            val db = AppDatabase.getDatabase(context, CoroutineScope(Dispatchers.IO))
            val dao = db.expenseDao()

            val now = Calendar.getInstance()
            val startOfWeek = (now.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, -7)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val expenses = dao.getExpensesByDateRangeSync(startOfWeek.timeInMillis, now.timeInMillis)
            val total = expenses.sumOf { it.amount }
            val count = expenses.size

            val currencyFormat = CurrencyHelper.getCurrencyFormat(context)
            val topCategory = expenses.groupBy { it.category }
                .maxByOrNull { it.value.sumOf { e -> e.amount } }?.key ?: "None"

            val message = if (count > 0) {
                "You spent ${currencyFormat.format(total)} across $count transactions. Top category: $topCategory"
            } else {
                "No expenses logged this week. Great saving! \uD83C\uDF89"
            }

            val tapIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_WEEKLY_ID)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("Weekly Spending Summary \uD83D\uDCCA")
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(NOTIF_ID_WEEKLY, notification)
        } catch (e: SecurityException) {
        } catch (e: Exception) {
        }
    }
}

