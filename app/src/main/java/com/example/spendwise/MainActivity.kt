package com.example.spendwise

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
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
        val themeMode = SpendWiseApp.getSavedThemeMode(this)
        SpendWiseApp.applyThemeMode(themeMode)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val rootLayout = findViewById<View>(R.id.main_content)
        val cardBottomNav = findViewById<View>(R.id.card_bottom_nav)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNav.setupWithNavController(navController)

        // Glass blur on Android 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurLayer = findViewById<View>(R.id.nav_blur_layer)
            try {
                blurLayer?.setRenderEffect(
                    android.graphics.RenderEffect.createBlurEffect(
                        28f, 28f, android.graphics.Shader.TileMode.CLAMP
                    )
                )
            } catch (_: Exception) {}
        }

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Status bar inset for root layout
            rootLayout.updatePadding(top = systemBars.top)

            // Dynamic bottom margin for floating pill navigation dock so it floats above gesture bar / navigation bar
            val density = resources.displayMetrics.density
            val baseBottomMargin = (14 * density).toInt()
            cardBottomNav.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.bottomMargin = baseBottomMargin + systemBars.bottom
            }

            insets
        }

        var isNavVisible = true

        fun showBottomNav() {
            isNavVisible = true
            cardBottomNav.animate().cancel()
            cardBottomNav.visibility = View.VISIBLE
            cardBottomNav.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(220)
                .setListener(null)
                .start()
        }

        fun hideBottomNav(immediate: Boolean = false) {
            isNavVisible = false
            cardBottomNav.animate().cancel()
            if (immediate) {
                cardBottomNav.visibility = View.GONE
                cardBottomNav.alpha = 0f
                cardBottomNav.translationY = 300f
                return
            }
            cardBottomNav.animate()
                .translationY(300f)
                .alpha(0f)
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (!isNavVisible) {
                            cardBottomNav.visibility = View.GONE
                        }
                    }
                })
                .start()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.welcomeFragment -> {
                    hideBottomNav(immediate = true)
                }
                R.id.addExpenseFragment,
                R.id.splitExpenseFragment -> {
                    hideBottomNav(immediate = false)
                }
                else -> {
                    showBottomNav()
                    checkNotificationPermission()
                }
            }

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
