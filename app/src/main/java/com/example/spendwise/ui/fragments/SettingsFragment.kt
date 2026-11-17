package com.example.spendwise.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.spendwise.R
import com.example.spendwise.data.AppDatabase
import com.example.spendwise.ui.dialogs.CurrencyPickerBottomSheet
import com.example.spendwise.ui.dialogs.MonthlyBudgetBottomSheet
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.example.spendwise.utils.NotificationHelper
import com.example.spendwise.utils.NotificationScheduler
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
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

    private var isUpdatingNotificationSwitches = false
    private var pendingSwitchToEnable: MaterialSwitch? = null
    private var switchDailyReminder: MaterialSwitch? = null
    private var switchWeeklySummary: MaterialSwitch? = null
    private var tvReminderTime: TextView? = null

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        prefs.edit { putBoolean("notif_permission_asked", true) }

        if (isGranted) {
            val targetSwitch = pendingSwitchToEnable
            if (targetSwitch == switchDailyReminder) {
                val hour = prefs.getInt(NotificationScheduler.KEY_DAILY_HOUR, NotificationScheduler.DEFAULT_DAILY_HOUR)
                val minute = prefs.getInt(NotificationScheduler.KEY_DAILY_MINUTE, NotificationScheduler.DEFAULT_DAILY_MINUTE)
                NotificationScheduler.enableDaily(requireContext(), hour, minute)
                isUpdatingNotificationSwitches = true
                switchDailyReminder?.isChecked = true
                isUpdatingNotificationSwitches = false
            } else if (targetSwitch == switchWeeklySummary) {
                NotificationScheduler.enableWeekly(requireContext())
                isUpdatingNotificationSwitches = true
                switchWeeklySummary?.isChecked = true
                isUpdatingNotificationSwitches = false
            }
        }
        pendingSwitchToEnable = null
    }

    private val backupLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri -> uri?.let { backupDatabaseTo(it) } }

    private val restoreLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { restoreDatabaseFrom(it) } }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)


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

        // ── 5.5. Notifications ──
        switchDailyReminder = view.findViewById(R.id.switch_daily_reminder)
        switchWeeklySummary = view.findViewById(R.id.switch_weekly_summary)
        tvReminderTime = view.findViewById(R.id.tv_reminder_time)
        val rowReminderTime = view.findViewById<View>(R.id.row_reminder_time)

        switchDailyReminder?.setOnCheckedChangeListener { _, isChecked ->
            if (isUpdatingNotificationSwitches) return@setOnCheckedChangeListener
            if (isChecked) {
                handleEnableNotification(switchDailyReminder) {
                    val hour = prefs.getInt(NotificationScheduler.KEY_DAILY_HOUR, NotificationScheduler.DEFAULT_DAILY_HOUR)
                    val minute = prefs.getInt(NotificationScheduler.KEY_DAILY_MINUTE, NotificationScheduler.DEFAULT_DAILY_MINUTE)
                    NotificationScheduler.enableDaily(requireContext(), hour, minute)
                }
            } else {
                NotificationScheduler.disableDaily(requireContext())
            }
        }

        switchWeeklySummary?.setOnCheckedChangeListener { _, isChecked ->
            if (isUpdatingNotificationSwitches) return@setOnCheckedChangeListener
            if (isChecked) {
                handleEnableNotification(switchWeeklySummary) {
                    NotificationScheduler.enableWeekly(requireContext())
                }
            } else {
                NotificationScheduler.disableWeekly(requireContext())
            }
        }

        rowReminderTime.setOnClickListener {
            val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(requireContext())
            val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
            val currentHour = prefs.getInt(NotificationScheduler.KEY_DAILY_HOUR, NotificationScheduler.DEFAULT_DAILY_HOUR)
            val currentMinute = prefs.getInt(NotificationScheduler.KEY_DAILY_MINUTE, NotificationScheduler.DEFAULT_DAILY_MINUTE)

            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(currentHour)
                .setMinute(currentMinute)
                .setTitleText("Reminder Time")
                .build()

            picker.addOnPositiveButtonClickListener {
                val newHour = picker.hour
                val newMinute = picker.minute
                prefs.edit {
                    putInt(NotificationScheduler.KEY_DAILY_HOUR, newHour)
                    putInt(NotificationScheduler.KEY_DAILY_MINUTE, newMinute)
                }
                tvReminderTime?.text = formatTime(newHour, newMinute)
                if (switchDailyReminder?.isChecked == true && NotificationHelper.hasNotificationPermission(requireContext())) {
                    NotificationScheduler.enableDaily(requireContext(), newHour, newMinute)
                }
            }
            picker.show(parentFragmentManager, "reminder_time_picker")
        }


        // ── 6.5. Export PDF ──
        view.findViewById<MaterialButton>(R.id.btn_export_pdf).setOnClickListener {
            showMonthPickerForPdf()
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
            showAboutDialog()
        }

        // ── 13. Privacy Policy ──
        view.findViewById<MaterialButton>(R.id.btn_privacy).setOnClickListener {
            showPrivacyPolicyDialog()
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


    // ── Budget Dialog (Modern Bottom Sheet) ──
    private fun showBudgetDialog() {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        MonthlyBudgetBottomSheet.newInstance(
            onSave = { value ->
                prefs.edit().putFloat("monthly_budget", value).apply()
                val msg = if (value > 0f) {
                    "Budget set to ${com.example.spendwise.utils.CurrencyHelper.format(requireContext(), value.toDouble())}"
                } else {
                    "Budget limit removed"
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            },
            onRemove = {
                prefs.edit().putFloat("monthly_budget", 0f).apply()
                Toast.makeText(context, "Budget limit removed", Toast.LENGTH_SHORT).show()
            }
        ).show(childFragmentManager, MonthlyBudgetBottomSheet.TAG)
    }

    // ── Currency Dialog (Modern Bottom Sheet) ──
    private fun showCurrencyDialog() {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        val currentCode = prefs.getString("currency_code", "default") ?: "default"
        CurrencyPickerBottomSheet.newInstance(currentCode) { code, displayName ->
            prefs.edit().putString("currency_code", code).apply()
            Toast.makeText(context, "Currency set to $displayName", Toast.LENGTH_SHORT).show()
        }.show(childFragmentManager, CurrencyPickerBottomSheet.TAG)
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


    // ── About Dialog (Rich Bottom Sheet) ──
    private fun showAboutDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_about, null)
        dialog.setContentView(dialogView)

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        val versionName = try {
            requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName ?: "1.0.4"
        } catch (e: Exception) {
            "1.0.4"
        }
        dialogView.findViewById<TextView>(R.id.tv_dialog_version)?.text = "Version $versionName - 100% Offline and Ad-Free"

        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.skipCollapsed = true

        dialogView.findViewById<View>(R.id.btn_dialog_close_top)?.setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_dialog_rate_app)?.setOnClickListener {
            dialog.dismiss()
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireContext().packageName}")))
            } catch (e: Exception) {
                Toast.makeText(context, "Play Store not available", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_dialog_close_about)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // ── Privacy Policy Dialog (Rich Bottom Sheet) ──
    private fun showPrivacyPolicyDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_privacy, null)
        dialog.setContentView(dialogView)

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.skipCollapsed = true

        dialogView.findViewById<View>(R.id.btn_dialog_close_top)?.setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_dialog_close_privacy)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    // ── Month Picker for PDF ──
    private fun showMonthPickerForPdf() {
        val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val cal = Calendar.getInstance()
        val months = Array(12) {
            val title = format.format(cal.time)
            // Save time properties attached to this index
            val _year = cal.get(Calendar.YEAR)
            val _month = cal.get(Calendar.MONTH)
            cal.add(Calendar.MONTH, -1)
            Pair(title, Pair(_year, _month))
        }

        val titles = months.map { it.first }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Month for Report")
            .setSingleChoiceItems(titles, 0) { dialog, which ->
                val (year, month) = months[which].second
                val monthTitle = months[which].first
                generatePdfForMonth(year, month, monthTitle)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun generatePdfForMonth(year: Int, month: Int, title: String) {
        val startCal = Calendar.getInstance().apply {
            set(year, month, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            set(year, month, startCal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val start = startCal.timeInMillis
        val end = endCal.timeInMillis

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(requireContext(), CoroutineScope(Dispatchers.IO))
                val expenses = db.expenseDao().getExpensesByDateRangeSync(start, end)
                
                if (expenses.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No data for $title", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                
                val categoryTotals = db.expenseDao().getCategoryTotals(start, end)
                
                val totalIncome = expenses.filter { it.isIncome }.sumOf { it.amount }
                val totalExpense = expenses.filter { !it.isIncome }.sumOf { it.amount }

                val file = com.example.spendwise.utils.PdfExportHelper.generateMonthlyReport(
                    requireContext(),
                    title,
                    expenses,
                    categoryTotals,
                    totalIncome,
                    totalExpense
                )

                withContext(Dispatchers.Main) {
                    val uri = androidx.core.content.FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.fileprovider",
                        file
                    )
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(intent, "Share PDF Report"))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to generate PDF: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
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

    private fun handleEnableNotification(targetSwitch: MaterialSwitch?, onGrantedAction: () -> Unit) {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        if (NotificationHelper.hasNotificationPermission(requireContext())) {
            onGrantedAction()
            return
        }

        // Revert switch until granted
        isUpdatingNotificationSwitches = true
        targetSwitch?.isChecked = false
        isUpdatingNotificationSwitches = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notifAsked = prefs.getBoolean("notif_permission_asked", false)
            val notificationsBlocked = !NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
            val permanentlyDenied = notificationsBlocked || (notifAsked && !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS))

            if (permanentlyDenied) {
                showNotificationSettingsDialog()
            } else {
                pendingSwitchToEnable = targetSwitch
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            showNotificationSettingsDialog()
        }
    }

    private fun showNotificationSettingsDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Notifications Disabled")
            .setMessage("Notifications are disabled for SpendWise. Please enable them in system settings to receive reminders and summaries.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppNotificationSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppNotificationSettings() {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open settings", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        updateNotificationUi()
    }

    private fun updateNotificationUi() {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        val hasPermission = NotificationHelper.hasNotificationPermission(requireContext())
        val dailyEnabled = prefs.getBoolean(NotificationScheduler.KEY_DAILY_ENABLED, false) && hasPermission
        val weeklyEnabled = prefs.getBoolean(NotificationScheduler.KEY_WEEKLY_ENABLED, false) && hasPermission

        isUpdatingNotificationSwitches = true
        switchDailyReminder?.isChecked = dailyEnabled
        switchWeeklySummary?.isChecked = weeklyEnabled

        val hour = prefs.getInt(NotificationScheduler.KEY_DAILY_HOUR, NotificationScheduler.DEFAULT_DAILY_HOUR)
        val minute = prefs.getInt(NotificationScheduler.KEY_DAILY_MINUTE, NotificationScheduler.DEFAULT_DAILY_MINUTE)
        tvReminderTime?.text = formatTime(hour, minute)
        isUpdatingNotificationSwitches = false
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
    }

    // ── Backup Database ──
    private fun backupDatabaseTo(uri: Uri) {
        try {
            val dbFile = requireContext().getDatabasePath("spendwise_database")
            // Checkpoint WAL to ensure all data is in main db file
            AppDatabase.getDatabase(requireContext(), CoroutineScope(Dispatchers.IO))
                .openHelper.writableDatabase
                .query("PRAGMA wal_checkpoint(FULL)")
                .use { it.moveToFirst() }

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
        val ctx = requireContext().applicationContext
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1. Close Room so it releases the file lock
                AppDatabase.closeAndReset()

                withContext(Dispatchers.IO) {
                    val dbFile = ctx.getDatabasePath("spendwise_database")

                    // 2. Delete stale WAL / SHM / journal files
                    for (suffix in listOf("-wal", "-shm", "-journal")) {
                        File(dbFile.path + suffix).delete()
                    }

                    // 3. Copy the backup over the main DB file
                    ctx.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(dbFile).use { output -> input.copyTo(output) }
                    } ?: throw IllegalStateException("Cannot open backup file")
                }

                Toast.makeText(ctx, "Database restored. Restarting…", Toast.LENGTH_SHORT).show()

                // 4. Kill process so the next launch opens a fresh Room connection
                @Suppress("DEPRECATION")
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    android.os.Process.killProcess(android.os.Process.myPid())
                }, 600)
            } catch (e: Exception) {
                Toast.makeText(ctx, "Restore failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
