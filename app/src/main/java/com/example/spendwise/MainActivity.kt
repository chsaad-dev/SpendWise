package com.example.spendwise

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var pendingAddExpense = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.loginFragment, R.id.setupPinFragment -> {
                    bottomNav.visibility = View.GONE
                }
                else -> {
                    bottomNav.visibility = View.VISIBLE
                }
            }

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
