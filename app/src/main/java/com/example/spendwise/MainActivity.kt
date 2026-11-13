package com.example.spendwise

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.spendwise.utils.NotificationScheduler
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var pendingAddExpense = false

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationScheduler.enableDaily(this, 20, 0)
            NotificationScheduler.enableWeekly(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        val rootLayout = findViewById<View>(R.id.main_content)
        val navHostFragmentView = findViewById<View>(R.id.nav_host_fragment)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNav.setupWithNavController(navController)

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            rootLayout.updatePadding(top = systemBars.top)

            val density = resources.displayMetrics.density
            val baseNavHeightPx = (80 * density).toInt()

            val isBottomNavVisible = bottomNav.visibility == View.VISIBLE
            val bottomMargin = if (isBottomNavVisible) {
                baseNavHeightPx + systemBars.bottom
            } else {
                systemBars.bottom
            }

            navHostFragmentView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.bottomMargin = bottomMargin
            }

            bottomNav.updatePadding(bottom = systemBars.bottom)

            insets
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.welcomeFragment -> {
                    bottomNav.visibility = View.GONE
                }
                else -> {
                    bottomNav.visibility = View.VISIBLE
                    checkNotificationPermission()
                }
            }

            ViewCompat.requestApplyInsets(rootLayout)

            if (destination.id == R.id.homeFragment && pendingAddExpense) {
                pendingAddExpense = false
                navController.navigate(R.id.action_home_to_addExpense)
            }
        }

        checkWidgetIntent()
    }

    private fun checkNotificationPermission() {
        val prefs = getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        val alreadyAsked = prefs.getBoolean("notif_permission_asked", false)
        if (alreadyAsked) return

        prefs.edit { putBoolean("notif_permission_asked", true) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationScheduler.enableDaily(this, 20, 0)
                NotificationScheduler.enableWeekly(this)
            } else {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            NotificationScheduler.enableDaily(this, 20, 0)
            NotificationScheduler.enableWeekly(this)
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        checkWidgetIntent()

        if (pendingAddExpense && navController.currentDestination?.id == R.id.homeFragment) {
            pendingAddExpense = false
            navController.navigate(R.id.action_home_to_addExpense)
        }
    }

    private fun checkWidgetIntent() {
        if (intent?.getBooleanExtra("open_add_expense", false) == true) {
            intent.removeExtra("open_add_expense")
            pendingAddExpense = true
        }
    }
}
