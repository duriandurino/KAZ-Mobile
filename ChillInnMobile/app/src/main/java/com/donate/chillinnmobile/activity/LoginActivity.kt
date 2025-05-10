package com.donate.chillinnmobile.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.utils.showToast
import com.donate.chillinnmobile.viewmodel.UserViewModel
import com.google.android.material.textfield.TextInputLayout

/**
 * Activity for user login
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    
    // UI components
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // Initialize ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        
        // Initialize UI components
        initializeViews()
        
        // Set up click listeners
        setupClickListeners()
        
        // Observe login status
        observeLoginStatus()
    }
    
    private fun initializeViews() {
        emailInputLayout = findViewById(R.id.email_input_layout)
        passwordInputLayout = findViewById(R.id.password_input_layout)
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        registerTextView = findViewById(R.id.register_text_view)
        progressBar = findViewById(R.id.progress_bar)
    }
    
    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            if (validateInputs()) {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                userViewModel.login(email, password)
            }
        }
        
        registerTextView.setOnClickListener {
            // Navigate to registration screen
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun observeLoginStatus() {
        userViewModel.loginStatus.observe(this) { (success, errorMessage, isLoading) ->
            if (isLoading) {
                showLoading(true)
            } else {
                showLoading(false)
                
                if (success) {
                    // Login successful, navigate to MainActivity
                    navigateToMain()
                } else if (errorMessage != null) {
                    // Show error message
                    showToast(errorMessage)
                }
            }
        }
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Validate email
        val email = emailEditText.text.toString().trim()
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Please enter a valid email address"
            isValid = false
        } else {
            emailInputLayout.error = null
        }
        
        // Validate password
        val password = passwordEditText.text.toString().trim()
        if (password.isEmpty()) {
            passwordInputLayout.error = "Password cannot be empty"
            isValid = false
        } else {
            passwordInputLayout.error = null
        }
        
        return isValid
    }
    
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            loginButton.isEnabled = true
        }
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
} 