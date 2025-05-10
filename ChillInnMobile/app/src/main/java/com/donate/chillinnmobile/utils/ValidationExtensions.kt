package com.donate.chillinnmobile.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension functions for form validation using Material Design components.
 * These extensions simplify the integration of ValidationUtils with TextInputLayout.
 */

/**
 * Validate email field
 * @return True if validation passes
 */
fun TextInputLayout.validateEmail(): Boolean {
    val email = this.editText?.text.toString().trim()
    val result = ValidationUtils.validateEmail(email)
    
    if (!result.isValid) {
        this.error = result.errorMessage
    } else {
        this.error = null
    }
    
    return result.isValid
}

/**
 * Validate password field
 * @return True if validation passes
 */
fun TextInputLayout.validatePassword(): Boolean {
    val password = this.editText?.text.toString()
    val result = ValidationUtils.validatePassword(password)
    
    if (!result.isValid) {
        this.error = result.errorMessage
    } else {
        this.error = null
    }
    
    return result.isValid
}

/**
 * Validate password confirmation
 * @param passwordLayout TextInputLayout containing the original password
 * @return True if validation passes
 */
fun TextInputLayout.validateConfirmPassword(passwordLayout: TextInputLayout): Boolean {
    val password = passwordLayout.editText?.text.toString()
    val confirmPassword = this.editText?.text.toString()
    val result = ValidationUtils.validateConfirmPassword(password, confirmPassword)
    
    if (!result.isValid) {
        this.error = result.errorMessage
    } else {
        this.error = null
    }
    
    return result.isValid
}

/**
 * Validate non-empty field
 * @param fieldName Name of the field for error message
 * @return True if validation passes
 */
fun TextInputLayout.validateNotEmpty(fieldName: String): Boolean {
    val text = this.editText?.text.toString().trim()
    val result = ValidationUtils.validateNotEmpty(text, fieldName)
    
    if (!result.isValid) {
        this.error = result.errorMessage
    } else {
        this.error = null
    }
    
    return result.isValid
}

/**
 * Validate field length
 * @param fieldName Name of the field for error message
 * @param minLength Minimum required length
 * @param maxLength Maximum allowed length
 * @return True if validation passes
 */
fun TextInputLayout.validateLength(
    fieldName: String,
    minLength: Int,
    maxLength: Int
): Boolean {
    val text = this.editText?.text.toString().trim()
    val result = ValidationUtils.validateLength(text, fieldName, minLength, maxLength)
    
    if (!result.isValid) {
        this.error = result.errorMessage
    } else {
        this.error = null
    }
    
    return result.isValid
}

/**
 * Validate phone number
 * @return True if validation passes
 */
fun TextInputLayout.validatePhone(): Boolean {
    val phone = this.editText?.text.toString().trim()
    val result = ValidationUtils.validatePhone(phone)
    
    if (!result.isValid) {
        this.error = result.errorMessage
    } else {
        this.error = null
    }
    
    return result.isValid
}

/**
 * Validate credit card number
 * @return True if validation passes
 */
fun TextInputLayout.validateCreditCard(): Boolean {
    val cardNumber = this.editText?.text.toString().trim()
    val result = ValidationUtils.validateCreditCard(cardNumber)
    
    if (!result.isValid) {
        this.error = result.errorMessage
    } else {
        this.error = null
    }
    
    return result.isValid
}

/**
 * Validate credit card expiry date
 * @return True if validation passes
 */
fun TextInputLayout.validateExpiryDate(): Boolean {
    val expiryDate = this.editText?.text.toString().trim()
    val result = ValidationUtils.validateExpiryDate(expiryDate)
    
    if (!result.isValid) {
        this.error = result.errorMessage
    } else {
        this.error = null
    }
    
    return result.isValid
}

/**
 * Validate credit card CVV
 * @param cardType Type of card
 * @return True if validation passes
 */
fun TextInputLayout.validateCVV(
    cardType: ValidationUtils.CardType = ValidationUtils.CardType.UNKNOWN
): Boolean {
    val cvv = this.editText?.text.toString().trim()
    val result = ValidationUtils.validateCVV(cvv, cardType)
    
    if (!result.isValid) {
        this.error = result.errorMessage
    } else {
        this.error = null
    }
    
    return result.isValid
}

/**
 * Validate RatingBar value
 * @param errorView View to show if there's an error (usually a TextView)
 * @return True if validation passes
 */
fun RatingBar.validateRating(errorView: View): Boolean {
    val rating = this.rating
    val result = ValidationUtils.validateRating(rating)
    
    errorView.visibility = if (!result.isValid) View.VISIBLE else View.GONE
    
    return result.isValid
}

/**
 * Set up real-time validation for email field
 */
fun TextInputLayout.setupEmailValidation() {
    this.editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        
        override fun afterTextChanged(s: Editable?) {
            validateEmail()
        }
    })
}

/**
 * Set up real-time validation for password field
 */
fun TextInputLayout.setupPasswordValidation() {
    this.editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        
        override fun afterTextChanged(s: Editable?) {
            validatePassword()
        }
    })
}

/**
 * Set up real-time validation for confirm password field
 * @param passwordLayout TextInputLayout containing the original password
 */
fun TextInputLayout.setupConfirmPasswordValidation(passwordLayout: TextInputLayout) {
    this.editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        
        override fun afterTextChanged(s: Editable?) {
            validateConfirmPassword(passwordLayout)
        }
    })
}

/**
 * Format credit card number with spaces (4 digits per group)
 */
fun EditText.formatAsCardNumber() {
    this.addTextChangedListener(object : TextWatcher {
        private var current = ""
        
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        
        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                val userInput = s.toString().replace("[^\\d]".toRegex(), "")
                if (userInput.length <= 16) {
                    val formatted = StringBuilder()
                    for (i in userInput.indices) {
                        if (i % 4 == 0 && i > 0) {
                            formatted.append(" ")
                        }
                        formatted.append(userInput[i])
                    }
                    
                    current = formatted.toString()
                    s?.replace(0, s.length, current)
                }
            }
        }
    })
}

/**
 * Format expiry date as MM/YY
 */
fun EditText.formatAsExpiryDate() {
    this.addTextChangedListener(object : TextWatcher {
        private var current = ""
        
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        
        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                val userInput = s.toString().replace("[^\\d]".toRegex(), "")
                if (userInput.length <= 4) {
                    val formatted = StringBuilder()
                    for (i in userInput.indices) {
                        if (i == 2 && i < userInput.length) {
                            formatted.append("/")
                        }
                        formatted.append(userInput[i])
                    }
                    
                    current = formatted.toString()
                    s?.replace(0, s.length, current)
                }
            }
        }
    })
}

/**
 * Format phone number as +X-XXX-XXX-XXXX
 */
fun EditText.formatAsPhoneNumber() {
    this.addTextChangedListener(object : TextWatcher {
        private var current = ""
        
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        
        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                val userInput = s.toString().replace("[^\\d+]".toRegex(), "")
                val formatted = StringBuilder()
                
                var index = 0
                if (userInput.isNotEmpty() && userInput[0] == '+') {
                    formatted.append("+")
                    index = 1
                } else if (userInput.isNotEmpty()) {
                    formatted.append("+")
                }
                
                var dashCount = 0
                while (index < userInput.length && dashCount < 3) {
                    // Country code
                    if (dashCount == 0) {
                        val endCountryCode = minOf(index + (if (userInput[0] == '+') 1 else 2), userInput.length)
                        formatted.append(userInput.substring(index, endCountryCode))
                        index = endCountryCode
                        if (index < userInput.length) {
                            formatted.append("-")
                            dashCount++
                        }
                    } 
                    // Area code
                    else if (dashCount == 1) {
                        val endAreaCode = minOf(index + 3, userInput.length)
                        formatted.append(userInput.substring(index, endAreaCode))
                        index = endAreaCode
                        if (index < userInput.length) {
                            formatted.append("-")
                            dashCount++
                        }
                    } 
                    // Prefix
                    else if (dashCount == 2) {
                        val endPrefix = minOf(index + 3, userInput.length)
                        formatted.append(userInput.substring(index, endPrefix))
                        index = endPrefix
                        if (index < userInput.length) {
                            formatted.append("-")
                            dashCount++
                        }
                    }
                }
                
                // Line number
                if (index < userInput.length) {
                    formatted.append(userInput.substring(index))
                }
                
                current = formatted.toString()
                s?.replace(0, s.length, current)
            }
        }
    })
} 