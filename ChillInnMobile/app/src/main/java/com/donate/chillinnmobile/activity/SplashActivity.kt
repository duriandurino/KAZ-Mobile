package com.donate.chillinnmobile.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.utils.SessionManager

/**
 * Splash screen activity shown on app launch
 */
class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY = 2000L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        // Initialize SessionManager
        SessionManager.init(applicationContext)
        
        // Delayed transition to the appropriate activity
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, SPLASH_DELAY)
    }
    
    /**
     * Navigate to the appropriate screen based on login status
     */
    private fun navigateToNextScreen() {
        val isLoggedIn = SessionManager.isLoggedIn()
        
        val intent = if (isLoggedIn) {
            // User is logged in, go to MainActivity
            Intent(this, MainActivity::class.java)
        } else {
            // User is not logged in, go to LoginActivity
            Intent(this, LoginActivity::class.java)
        }
        
        // Start the appropriate activity and finish the splash screen
        startActivity(intent)
        finish()
    }
} 