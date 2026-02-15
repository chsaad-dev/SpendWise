package com.example.spendwise

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.spendwise.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class WeeklySummaryReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "spendwise_weekly"
        const val NOTIFICATION_ID = 1002
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val pendingResult = goAsync()

        scope.launch {
            try {
                val db = AppDatabase.getDatabase(context, this)
                val dao = db.expenseDao()

                // Get this week's range
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

                val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
                val topCategory = expenses.groupBy { it.category }
                    .maxByOrNull { it.value.sumOf { e -> e.amount } }?.key ?: "None"

                val message = if (count > 0) {
                    "You spent ${currencyFormat.format(total)} across $count transactions. Top category: $topCategory"
                } else {
                    "No expenses logged this week. Great saving! \uD83C\uDF89"
                }

                createNotificationChannel(context)

                val tapIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent = PendingIntent.getActivity(
                    context, 0, tapIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setContentTitle("Weekly Spending Summary \uD83D\uDCCA")
                    .setContentText(message)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weekly Summary",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Weekly spending summary notification"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
