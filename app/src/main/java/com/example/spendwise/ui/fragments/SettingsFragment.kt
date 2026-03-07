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
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("About SpendWise")
                .setMessage(
                    "SpendWise v2.0\n\n" +
                    "SpendWise is your comprehensive, offline-first personal finance companion designed to help you effortlessly manage your money.\n\n" +
                    "Key Features:\n" +
                    "• Intelligent Tracking: Log daily spending with custom categories.\n" +
                    "• Visual Analytics: Understand habits through dynamic charts and summaries.\n" +
                    "• Advanced Budgeting: Set monthly limits to keep spending in check.\n" +
                    "• Collaborative: Split bills with friends seamlessly.\n" +
                    "• Data Control: Export, backup, and restore your database at any time.\n\n" +
                    "Developer: SaadDev\n" +
                    "Built with ❤️ and native Android Material Design 3 for a fluid, modern, and beautiful user experience."
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
                    "At SpendWise, we believe your financial data is strictly yours. We have built this application with a \"Privacy First\" and \"Offline First\" philosophy.\n\n" +
                    "1. Local Storage: All transactions, categories, and preferences are stored securely on your device's internal storage.\n" +
                    "2. Zero Data Collection: We do not collect, harvest, or transmit any personal or financial information.\n" +
                    "3. No Third-Party Tracking: There are no remote analytics, tracking SDKs, telemetry, or ad networks integrated into SpendWise.\n" +
                    "4. Fully Offline: The app functions 100% offline and does not require an internet connection.\n" +
                    "5. Total Control: You retain full ownership of your records. You can permanently wipe all data directly from the settings at any time.\n\n" +
                    "By using SpendWise, you are guaranteed absolute privacy over your personal finances."
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
