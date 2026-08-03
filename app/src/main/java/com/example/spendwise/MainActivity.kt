package com.example.spendwise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var pendingAddExpense = false

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
