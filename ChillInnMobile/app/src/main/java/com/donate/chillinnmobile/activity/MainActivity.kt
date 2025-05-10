package com.donate.chillinnmobile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.utils.SessionManager
import com.donate.chillinnmobile.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Main activity that hosts the app's navigation and fragments
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        
        // Set up navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Set up bottom navigation
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavView.setupWithNavController(navController)
        
        // Set up top level destinations
        val topLevelDestinations = if (userViewModel.isAdmin()) {
            // Admin view includes admin dashboard
            setOf(
                R.id.homeFragment,
                R.id.bookingsFragment,
                R.id.adminDashboardFragment,
                R.id.profileFragment
            )
        } else {
            // Regular user view
            setOf(
                R.id.homeFragment,
                R.id.bookingsFragment,
                R.id.profileFragment
            )
        }
        
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)
        setupActionBarWithNavController(navController, appBarConfiguration)
        
        // Handle visibility of admin menu items in bottom navigation
        val adminMenuItem = bottomNavView.menu.findItem(R.id.adminDashboardFragment)
        adminMenuItem?.isVisible = userViewModel.isAdmin()
        
        // Observe authentication state changes
        userViewModel.userProfile.observe(this) { user ->
            if (user == null && SessionManager.isLoggedIn() == false) {
                // User has logged out, navigate to login screen
                navigateToLogin()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                userViewModel.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
} 