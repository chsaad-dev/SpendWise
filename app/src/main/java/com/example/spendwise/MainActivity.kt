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
import androidx.activity.OnBackPressedCallback
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.spendwise.ui.fragments.CategoriesFragment
import com.example.spendwise.ui.fragments.HistoryFragment
import com.example.spendwise.ui.fragments.HomeFragment
import com.example.spendwise.ui.fragments.SavingsGoalFragment
import com.example.spendwise.ui.fragments.SettingsFragment
import com.example.spendwise.utils.NotificationScheduler
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG_HOME = "home"
        private const val TAG_HISTORY = "history"
        private const val TAG_CATEGORIES = "categories"
        private const val TAG_GOALS = "goals"
        private const val TAG_SETTINGS = "settings"
        private const val KEY_ACTIVE_TAB = "key_active_tab"
    }

    private lateinit var navController: NavController
    private var pendingAddExpense = false
    private var activeTabTag = TAG_HOME

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

        if (savedInstanceState != null) {
            activeTabTag = savedInstanceState.getString(KEY_ACTIVE_TAB, TAG_HOME) ?: TAG_HOME
        }

        val rootLayout = findViewById<View>(R.id.main_content)
        val tabContainer = findViewById<View>(R.id.tab_fragment_container)
        val cardBottomNav = findViewById<View>(R.id.card_bottom_nav)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostView = findViewById<View>(R.id.nav_host_fragment)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        Navigation.setViewNavController(rootLayout, navController)
        Navigation.setViewNavController(tabContainer, navController)

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

        val allTags = listOf(TAG_HOME, TAG_HISTORY, TAG_CATEGORIES, TAG_GOALS, TAG_SETTINGS)

        if (savedInstanceState == null) {
            val homeFrag = HomeFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.tab_fragment_container, homeFrag, TAG_HOME)
                .commit()
            activeTabTag = TAG_HOME
        } else {
            val ft = supportFragmentManager.beginTransaction()
            for (tag in allTags) {
                val frag = supportFragmentManager.findFragmentByTag(tag)
                if (frag != null && frag.isAdded) {
                    if (tag == activeTabTag) {
                        ft.show(frag)
                    } else {
                        ft.hide(frag)
                    }
                }
            }
            ft.commit()
        }

        fun getMenuIdForTag(tag: String): Int = when (tag) {
            TAG_HOME -> R.id.homeFragment
            TAG_HISTORY -> R.id.historyFragment
            TAG_CATEGORIES -> R.id.categoriesFragment
            TAG_GOALS -> R.id.savingsGoalFragment
            TAG_SETTINGS -> R.id.settingsFragment
            else -> R.id.homeFragment
        }

        fun getTagForMenuId(menuId: Int): String? = when (menuId) {
            R.id.homeFragment -> TAG_HOME
            R.id.historyFragment -> TAG_HISTORY
            R.id.categoriesFragment -> TAG_CATEGORIES
            R.id.savingsGoalFragment -> TAG_GOALS
            R.id.settingsFragment -> TAG_SETTINGS
            else -> null
        }

        bottomNav.selectedItemId = getMenuIdForTag(activeTabTag)

        fun switchTab(targetTag: String) {
            if (targetTag == activeTabTag) return

            val currentFrag = supportFragmentManager.findFragmentByTag(activeTabTag)
            var targetFrag = supportFragmentManager.findFragmentByTag(targetTag)

            val ft = supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.tab_fade_in, R.anim.tab_fade_out)

            if (currentFrag != null && currentFrag.isAdded) {
                ft.hide(currentFrag)
            }

            if (targetFrag == null) {
                targetFrag = when (targetTag) {
                    TAG_HOME -> HomeFragment()
                    TAG_HISTORY -> HistoryFragment()
                    TAG_CATEGORIES -> CategoriesFragment()
                    TAG_GOALS -> SavingsGoalFragment()
                    TAG_SETTINGS -> SettingsFragment()
                    else -> return
                }
                ft.add(R.id.tab_fragment_container, targetFrag, targetTag)
            } else {
                ft.show(targetFrag)
            }

            ft.commit()
            activeTabTag = targetTag
        }

        bottomNav.setOnItemSelectedListener { item ->
            val targetTag = getTagForMenuId(item.itemId) ?: return@setOnItemSelectedListener false
            if (targetTag == activeTabTag) {
                return@setOnItemSelectedListener true
            }
            switchTab(targetTag)
            true
        }

        bottomNav.setOnItemReselectedListener {
            // Guard against duplicate rapid taps: already active tab does nothing
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentDestId = navController.currentDestination?.id
                if (currentDestId == R.id.addExpenseFragment || currentDestId == R.id.splitExpenseFragment) {
                    navController.popBackStack()
                } else if (activeTabTag != TAG_HOME) {
                    bottomNav.selectedItemId = R.id.homeFragment
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

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
                    navHostView.visibility = View.VISIBLE
                    tabContainer.visibility = View.GONE
                }
                R.id.addExpenseFragment,
                R.id.splitExpenseFragment -> {
                    hideBottomNav(immediate = false)
                    navHostView.visibility = View.VISIBLE
                }
                else -> {
                    showBottomNav()
                    checkNotificationPermission()
                    navHostView.visibility = View.GONE
                    tabContainer.visibility = View.VISIBLE
                }
            }

            if (destination.id == R.id.homeFragment && pendingAddExpense) {
                pendingAddExpense = false
                bottomNav.selectedItemId = R.id.homeFragment
                navController.navigate(R.id.action_home_to_addExpense)
            }
        }

        checkWidgetIntent()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_ACTIVE_TAB, activeTabTag)
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
            findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.homeFragment
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
