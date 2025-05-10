package com.donate.chillinnmobile.activity

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
 * Activity for user registration
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    
    // UI components
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var fullNameInputLayout: TextInputLayout
    private lateinit var phoneInputLayout: TextInputLayout
    
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var phoneEditText: EditText
    
    private lateinit var registerButton: Button
    private lateinit var loginLinkTextView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // Initialize ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        
        // Initialize UI components
        initializeViews()
        
        // Set up click listeners
        setupClickListeners()
        
        // Observe registration status
        observeRegistrationStatus()
    }
    
    private fun initializeViews() {
        emailInputLayout = findViewById(R.id.email_input_layout)
        passwordInputLayout = findViewById(R.id.password_input_layout)
        confirmPasswordInputLayout = findViewById(R.id.confirm_password_input_layout)
        fullNameInputLayout = findViewById(R.id.full_name_input_layout)
        phoneInputLayout = findViewById(R.id.phone_input_layout)
        
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text)
        fullNameEditText = findViewById(R.id.full_name_edit_text)
        phoneEditText = findViewById(R.id.phone_edit_text)
        
        registerButton = findViewById(R.id.register_button)
        loginLinkTextView = findViewById(R.id.login_link_text_view)
        progressBar = findViewById(R.id.progress_bar)
    }
    
    private fun setupClickListeners() {
        registerButton.setOnClickListener {
            if (validateInputs()) {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                val fullName = fullNameEditText.text.toString().trim()
                val phone = phoneEditText.text.toString().trim()
                
                userViewModel.register(email, password, fullName, phone)
            }
        }
        
        loginLinkTextView.setOnClickListener {
            // Go back to login screen
            finish()
        }
    }
    
    private fun observeRegistrationStatus() {
        userViewModel.registerStatus.observe(this) { (success, errorMessage, isLoading) ->
            if (isLoading) {
                showLoading(true)
            } else {
                showLoading(false)
                
                if (success) {
                    // Registration successful
                    showToast("Registration successful! Please login.")
                    finish() // Go back to login screen
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
        
        // Validate full name
        val fullName = fullNameEditText.text.toString().trim()
        if (fullName.isEmpty()) {
            fullNameInputLayout.error = "Full name cannot be empty"
            isValid = false
        } else {
            fullNameInputLayout.error = null
        }
        
        // Validate phone
        val phone = phoneEditText.text.toString().trim()
        if (phone.isEmpty() || phone.length < 10) {
            phoneInputLayout.error = "Please enter a valid phone number"
            isValid = false
        } else {
            phoneInputLayout.error = null
        }
        
        // Validate password
        val password = passwordEditText.text.toString().trim()
        if (password.isEmpty() || password.length < 6) {
            passwordInputLayout.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordInputLayout.error = null
        }
        
        // Validate confirm password
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        if (confirmPassword != password) {
            confirmPasswordInputLayout.error = "Passwords do not match"
            isValid = false
        } else {
            confirmPasswordInputLayout.error = null
        }
        
        return isValid
    }
    
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            registerButton.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            registerButton.isEnabled = true
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 