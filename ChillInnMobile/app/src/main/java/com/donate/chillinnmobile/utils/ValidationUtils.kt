package com.donate.chillinnmobile.utils

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Utility class for client-side validation of user inputs.
 * Provides validation methods for common input types like email, password, dates, etc.
 */
object ValidationUtils {

    // Password regex pattern requires at least 8 characters, one uppercase, one lowercase, and one digit
    private val PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
    )
    
    // Credit card regex patterns for common card types
    private val VISA_PATTERN = Pattern.compile("^4[0-9]{12}(?:[0-9]{3})?$")
    private val MASTERCARD_PATTERN = Pattern.compile("^5[1-5][0-9]{14}$")
    private val AMEX_PATTERN = Pattern.compile("^3[47][0-9]{13}$")
    private val DISCOVER_PATTERN = Pattern.compile("^6(?:011|5[0-9]{2})[0-9]{12}$")
    
    // Phone number regex pattern (simple international format)
    private val PHONE_PATTERN = Pattern.compile("^\\+[0-9]{1,3}-[0-9]{3,4}-[0-9]{3,4}-[0-9]{4}$")

    /**
     * Validate email format
     * @param email Email to validate
     * @return Result object with validation status and error message if invalid
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> {
                ValidationResult(false, "Email cannot be empty")
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                ValidationResult(false, "Please enter a valid email address")
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    /**
     * Validate password with requirements:
     * - At least 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one number
     * - At least one special character
     *
     * @param password Password to validate
     * @return Result object with validation status and error message if invalid
     */
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> {
                ValidationResult(false, "Password cannot be empty")
            }
            password.length < 8 -> {
                ValidationResult(false, "Password must be at least 8 characters long")
            }
            !PASSWORD_PATTERN.matcher(password).matches() -> {
                ValidationResult(
                    false,
                    "Password must include uppercase, lowercase, number, and special character"
                )
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    /**
     * Validate password confirmation
     * @param password Original password
     * @param confirmPassword Confirmation password
     * @return Result object with validation status and error message if invalid
     */
    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isEmpty() -> {
                ValidationResult(false, "Please confirm your password")
            }
            password != confirmPassword -> {
                ValidationResult(false, "Passwords do not match")
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    /**
     * Validate that a field is not empty
     * @param text Text to validate
     * @param fieldName Name of the field for error message
     * @return Result object with validation status and error message if invalid
     */
    fun validateNotEmpty(text: String, fieldName: String): ValidationResult {
        return when {
            text.isEmpty() -> {
                ValidationResult(false, "$fieldName cannot be empty")
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    /**
     * Validate text length between min and max
     * @param text Text to validate
     * @param fieldName Name of the field for error message
     * @param minLength Minimum required length
     * @param maxLength Maximum allowed length
     * @return Result object with validation status and error message if invalid
     */
    fun validateLength(
        text: String,
        fieldName: String,
        minLength: Int,
        maxLength: Int
    ): ValidationResult {
        return when {
            text.isEmpty() -> {
                ValidationResult(false, "$fieldName cannot be empty")
            }
            text.length < minLength -> {
                ValidationResult(false, "$fieldName must be at least $minLength characters")
            }
            text.length > maxLength -> {
                ValidationResult(false, "$fieldName cannot exceed $maxLength characters")
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    /**
     * Validate phone number format
     * @param phone Phone number to validate
     * @return Result object with validation status and error message if invalid
     */
    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isEmpty() -> {
                ValidationResult(false, "Phone number cannot be empty")
            }
            !PHONE_PATTERN.matcher(phone).matches() -> {
                ValidationResult(false, "Please enter a valid phone number (e.g., +1-555-555-5555)")
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    /**
     * Validate credit card number
     * @param cardNumber Credit card number to validate
     * @return Result object with validation status and error message if invalid
     */
    fun validateCreditCard(cardNumber: String): ValidationResult {
        // Remove spaces and dashes
        val cleanNumber = cardNumber.replace("[\\s-]".toRegex(), "")
        
        return when {
            cleanNumber.isEmpty() -> {
                ValidationResult(false, "Card number cannot be empty")
            }
            !isValidCardNumber(cleanNumber) -> {
                ValidationResult(false, "Invalid card number")
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    /**
     * Validate credit card expiration date (MM/YY format)
     * @param expiryDate Expiration date to validate
     * @return Result object with validation status and error message if invalid
     */
    fun validateExpiryDate(expiryDate: String): ValidationResult {
        // Check format
        if (!expiryDate.matches("^(0[1-9]|1[0-2])/([0-9]{2})$".toRegex())) {
            return ValidationResult(false, "Expiry date should be in MM/YY format")
        }

        // Parse month and year
        val parts = expiryDate.split("/")
        val month = parts[0].toInt()
        val year = parts[1].toInt() + 2000 // Convert YY to 20YY
        
        // Create date for comparison
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // Calendar months are 0-based
        val currentYear = calendar.get(Calendar.YEAR)
        
        // Check if card is expired
        return when {
            year < currentYear || (year == currentYear && month < currentMonth) -> {
                ValidationResult(false, "Card is expired")
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    /**
     * Validate credit card CVV
     * @param cvv CVV to validate
     * @param cardType Type of card (determines expected CVV length)
     * @return Result object with validation status and error message if invalid
     */
    fun validateCVV(cvv: String, cardType: CardType = CardType.UNKNOWN): ValidationResult {
        val expectedLength = when (cardType) {
            CardType.AMEX -> 4
            else -> 3
        }
        
        return when {
            cvv.isEmpty() -> {
                ValidationResult(false, "CVV cannot be empty")
            }
            !cvv.matches("[0-9]{$expectedLength}".toRegex()) -> {
                ValidationResult(
                    false, 
                    "CVV should be $expectedLength digits"
                )
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    /**
     * Validate booking dates
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @return Result object with validation status and error message if invalid
     */
    fun validateBookingDates(checkInDate: Date, checkOutDate: Date): ValidationResult {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        
        return when {
            checkInDate.before(today) -> {
                ValidationResult(false, "Check-in date cannot be in the past")
            }
            checkOutDate.before(checkInDate) -> {
                ValidationResult(false, "Check-out date cannot be before check-in date")
            }
            checkInDate == checkOutDate -> {
                ValidationResult(false, "Stay must be at least one night")
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    /**
     * Check if number of guests is valid for room capacity
     * @param guestCount Number of guests
     * @param roomCapacity Maximum room capacity
     * @return Result object with validation status and error message if invalid
     */
    fun validateGuestCount(guestCount: Int, roomCapacity: Int): ValidationResult {
        return when {
            guestCount <= 0 -> {
                ValidationResult(false, "Number of guests must be at least 1")
            }
            guestCount > roomCapacity -> {
                ValidationResult(
                    false,
                    "Number of guests cannot exceed room capacity of $roomCapacity"
                )
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    /**
     * Validate a review rating
     * @param rating Rating value
     * @return Result object with validation status and error message if invalid
     */
    fun validateRating(rating: Float): ValidationResult {
        return when {
            rating <= 0 -> {
                ValidationResult(false, "Please select a rating")
            }
            rating > 5 -> {
                ValidationResult(false, "Rating cannot exceed 5 stars")
            }
            else -> {
                ValidationResult(true)
            }
        }
    }

    // Private helper methods

    /**
     * Validate credit card number using Luhn algorithm
     * @param cardNumber Credit card number to validate
     * @return True if card number passes Luhn check
     */
    private fun isValidCardNumber(cardNumber: String): Boolean {
        // Check for valid card patterns
        val isValidPattern = VISA_PATTERN.matcher(cardNumber).matches() ||
                MASTERCARD_PATTERN.matcher(cardNumber).matches() ||
                AMEX_PATTERN.matcher(cardNumber).matches() ||
                DISCOVER_PATTERN.matcher(cardNumber).matches()
        
        if (!isValidPattern) {
            return false
        }
        
        // Luhn algorithm for credit card validation
        var sum = 0
        var alternate = false
        
        for (i in cardNumber.length - 1 downTo 0) {
            var digit = Character.getNumericValue(cardNumber[i])
            
            if (alternate) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }
            
            sum += digit
            alternate = !alternate
        }
        
        return sum % 10 == 0
    }

    /**
     * Data class containing validation result and optional error message
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    /**
     * Enum for credit card types
     */
    enum class CardType {
        VISA,
        MASTERCARD,
        AMEX,
        DISCOVER,
        UNKNOWN
    }
} 