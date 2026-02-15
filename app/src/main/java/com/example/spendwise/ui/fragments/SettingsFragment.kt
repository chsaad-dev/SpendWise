package com.example.spendwise.ui.fragments

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spendwise.R
import com.example.spendwise.ReminderReceiver
import com.example.spendwise.data.AppDatabase
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SettingsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private val backupLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri -> uri?.let { backupDatabaseTo(it) } }

    private val restoreLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { restoreDatabaseFrom(it) } }

    private val notifPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            enableReminder()
        } else {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
            view?.findViewById<MaterialSwitch>(R.id.switch_reminder)?.isChecked = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)

        // ── 1. Reset PIN ──
        view.findViewById<MaterialButton>(R.id.btn_reset_pin).setOnClickListener {
            showResetPinDialog()
        }

        // ── 2. Monthly Budget ──
        view.findViewById<MaterialButton>(R.id.btn_budget).setOnClickListener {
            showBudgetDialog()
        }

        // ── 3. Default Currency ──
        view.findViewById<MaterialButton>(R.id.btn_currency).setOnClickListener {
            showCurrencyDialog()
        }

        // ── 4. Dark Mode ──
        val switchDark = view.findViewById<MaterialSwitch>(R.id.switch_dark_mode)
        switchDark.isChecked = prefs.getBoolean("dark_mode", false)
        switchDark.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // ── 5. App Language ──
        view.findViewById<MaterialButton>(R.id.btn_language).setOnClickListener {
            showLanguageDialog()
        }

        // ── 6. Daily Reminder ──
        val switchReminder = view.findViewById<MaterialSwitch>(R.id.switch_reminder)
        switchReminder.isChecked = prefs.getBoolean("reminder_enabled", false)
        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    enableReminder()
                }
            } else {
                disableReminder()
            }
        }

        // ── 6b. Weekly Summary ──
        val switchWeekly = view.findViewById<MaterialSwitch>(R.id.switch_weekly_summary)
        switchWeekly.isChecked = prefs.getBoolean("weekly_summary_enabled", false)
        switchWeekly.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("weekly_summary_enabled", isChecked).apply()
            if (isChecked) {
                enableWeeklySummary()
            } else {
                disableWeeklySummary()
            }
        }

        // ── 7. Export CSV ──
        view.findViewById<MaterialButton>(R.id.btn_export_csv).setOnClickListener {
            exportCsv()
        }

        // ── 8. Backup ──
        view.findViewById<MaterialButton>(R.id.btn_backup).setOnClickListener {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
            backupLauncher.launch("SpendWise_Backup_$timestamp.db")
        }

        // ── 9. Restore ──
        view.findViewById<MaterialButton>(R.id.btn_restore).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Restore Database")
                .setMessage("This will replace all current data with the backup. Continue?")
                .setPositiveButton("Restore") { _, _ ->
                    restoreLauncher.launch(arrayOf("application/octet-stream", "*/*"))
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // ── 10. Clear Categories ──
        view.findViewById<MaterialButton>(R.id.btn_clear_categories).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear All Categories")
                .setMessage("This will permanently delete all your categories. Expenses will keep their category names.")
                .setPositiveButton("Delete All") { _, _ ->
                    viewModel.clearAllCategories()
                    Toast.makeText(context, "All categories cleared", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // ── 11. Clear All Data ──
        view.findViewById<MaterialButton>(R.id.btn_clear_data).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear All Expenses")
                .setMessage("This will permanently delete all your expense records. This action cannot be undone.")
                .setPositiveButton("Delete Everything") { _, _ ->
                    viewModel.clearAllData()
                    Toast.makeText(context, "All expenses cleared", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // ── 12. About ──
        view.findViewById<MaterialButton>(R.id.btn_about).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("About SpendWise")
                .setMessage(
                    "SpendWise v2.0\n\n" +
                    "A simple, secure, and offline expense tracker.\n\n" +
                    "• Track daily expenses by category\n" +
                    "• Visualize spending with charts\n" +
                    "• PIN-protected for privacy\n" +
                    "• No internet required\n\n" +
                    "Built with ❤️ using Material 3"
                )
                .setPositiveButton("OK", null)
                .show()
        }

        // ── 13. Privacy Policy ──
        view.findViewById<MaterialButton>(R.id.btn_privacy).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Privacy Policy")
                .setMessage(
                    "SpendWise Privacy Policy\n\n" +
                    "Your privacy matters to us.\n\n" +
                    "• All data is stored locally on your device only.\n" +
                    "• No data is collected, transmitted, or shared with third parties.\n" +
                    "• No analytics or tracking of any kind.\n" +
                    "• No internet connection required or used.\n" +
                    "• Your PIN is stored locally and never leaves your device.\n" +
                    "• Deleting the app removes all data permanently.\n\n" +
                    "You are in full control of your data at all times."
                )
                .setPositiveButton("OK", null)
                .show()
        }

        // ── 14. Rate App ──
        view.findViewById<MaterialButton>(R.id.btn_rate).setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireContext().packageName}")))
            } catch (e: Exception) {
                Toast.makeText(context, "Play Store not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Reset PIN Dialog ──
    private fun showResetPinDialog() {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        val currentPin = prefs.getString("user_pin", "1234") ?: "1234"

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reset_pin, null)
        val etCurrent = dialogView.findViewById<EditText>(R.id.et_current_pin)
        val etNewPin = dialogView.findViewById<EditText>(R.id.et_new_pin)
        val etConfirm = dialogView.findViewById<EditText>(R.id.et_confirm_pin)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Reset PIN")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                when {
                    etCurrent.text.toString() != currentPin ->
                        Toast.makeText(context, "Current PIN is incorrect", Toast.LENGTH_SHORT).show()
                    etNewPin.text.toString().length < 4 ->
                        Toast.makeText(context, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show()
                    etNewPin.text.toString() != etConfirm.text.toString() ->
                        Toast.makeText(context, "New PINs don't match", Toast.LENGTH_SHORT).show()
                    else -> {
                        prefs.edit().putString("user_pin", etNewPin.text.toString()).apply()
                        Toast.makeText(context, "PIN updated successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Budget Dialog ──
    private fun showBudgetDialog() {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        val current = prefs.getFloat("monthly_budget", 0f)

        val input = EditText(requireContext()).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Enter monthly budget"
            if (current > 0f) setText(current.toString())
            setPadding(64, 32, 64, 32)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Monthly Budget Limit")
            .setMessage("Set a spending limit to get warnings when you're close.")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val value = input.text.toString().toFloatOrNull() ?: 0f
                prefs.edit().putFloat("monthly_budget", value).apply()
                val msg = if (value > 0f) "Budget set to ${value.toInt()}" else "Budget limit removed"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Remove Limit") { _, _ ->
                prefs.edit().putFloat("monthly_budget", 0f).apply()
                Toast.makeText(context, "Budget limit removed", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Currency Dialog ──
    private fun showCurrencyDialog() {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        val currencies = arrayOf(
            "System Default", "USD ($)", "EUR (€)", "GBP (£)", "INR (₹)",
            "JPY (¥)", "CAD (C$)", "AUD (A$)", "PKR (Rs)", "BDT (৳)"
        )
        val codes = arrayOf("default", "USD", "EUR", "GBP", "INR", "JPY", "CAD", "AUD", "PKR", "BDT")
        val saved = prefs.getString("currency_code", "default") ?: "default"
        val selected = codes.indexOf(saved).coerceAtLeast(0)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Default Currency")
            .setSingleChoiceItems(currencies, selected) { dialog, which ->
                prefs.edit().putString("currency_code", codes[which]).apply()
                Toast.makeText(context, "Currency set to ${currencies[which]}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Language Dialog ──
    private fun showLanguageDialog() {
        val languages = arrayOf("English (Default)")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("App Language")
            .setMessage("Only English is supported currently. More languages will be added in future updates.")
            .setSingleChoiceItems(languages, 0) { dialog, _ -> dialog.dismiss() }
            .setNegativeButton("OK", null)
            .show()
    }

    // ── Daily Reminder ──
    private fun enableReminder() {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("reminder_enabled", true).apply()

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule daily at 8 PM
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(context, "Daily reminder set for 8:00 PM", Toast.LENGTH_SHORT).show()
    }

    private fun disableReminder() {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("reminder_enabled", false).apply()

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Daily reminder disabled", Toast.LENGTH_SHORT).show()
    }

    // ── Weekly Summary ──
    private fun enableWeeklySummary() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), com.example.spendwise.WeeklySummaryReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 1001, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule every Sunday at 6 PM
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.WEEK_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
        Toast.makeText(context, "Weekly summary set for Sundays at 6 PM", Toast.LENGTH_SHORT).show()
    }

    private fun disableWeeklySummary() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), com.example.spendwise.WeeklySummaryReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 1001, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Weekly summary disabled", Toast.LENGTH_SHORT).show()
    }

    // ── Export CSV ──
    private fun exportCsv() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext(), CoroutineScope(Dispatchers.IO))
            val expenses = db.expenseDao().getAllExpensesSync()

            if (expenses.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No expenses to export", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sb = StringBuilder()
            sb.appendLine("Date,Category,Amount,Note")
            for (e in expenses) {
                val date = dateFormat.format(Date(e.date))
                val note = e.note.replace(",", ";").replace("\n", " ")
                sb.appendLine("$date,${e.category},${e.amount},$note")
            }

            val file = File(requireContext().cacheDir, "SpendWise_Export.csv")
            file.writeText(sb.toString())

            withContext(Dispatchers.Main) {
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    file
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(shareIntent, "Export Expenses"))
            }
        }
    }

    // ── Backup Database ──
    private fun backupDatabaseTo(uri: Uri) {
        try {
            val dbFile = requireContext().getDatabasePath("spendwise_database")
            // Checkpoint WAL to ensure all data is in main db file
            AppDatabase.getDatabase(requireContext(), CoroutineScope(Dispatchers.IO))
                .openHelper.writableDatabase.execSQL("PRAGMA wal_checkpoint(FULL)")

            requireContext().contentResolver.openOutputStream(uri)?.use { output ->
                FileInputStream(dbFile).use { input -> input.copyTo(output) }
            }
            Toast.makeText(context, "Backup saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Restore Database ──
    private fun restoreDatabaseFrom(uri: Uri) {
        try {
            val dbFile = requireContext().getDatabasePath("spendwise_database")
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(dbFile).use { output -> input.copyTo(output) }
            }
            Toast.makeText(context, "Database restored. Please restart the app.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Restore failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
