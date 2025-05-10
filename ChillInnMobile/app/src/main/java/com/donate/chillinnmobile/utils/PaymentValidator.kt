package com.donate.chillinnmobile.utils

import android.content.Context
import com.donate.chillinnmobile.R
import java.util.*
import java.util.regex.Pattern

/**
 * Validator for payment-related information.
 * Provides validation methods for credit card numbers, expiration dates,
 * CVV codes, and cardholder names.
 */
object PaymentValidator {

    /**
     * Result of payment validation containing success status and error message
     */
    data class PaymentValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    // Credit card number patterns
    private val VISA_PATTERN = Pattern.compile("^4[0-9]{12}(?:[0-9]{3})?$")
    private val MASTERCARD_PATTERN = Pattern.compile("^5[1-5][0-9]{14}$")
    private val AMEX_PATTERN = Pattern.compile("^3[47][0-9]{13}$")
    private val DISCOVER_PATTERN = Pattern.compile("^6(?:011|5[0-9]{2})[0-9]{12}$")
    
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

    /**
     * Validate credit card number using Luhn algorithm and card type patterns
     * @param cardNumber Credit card number (can include spaces/dashes)
     * @param context Context for string resources
     * @return PaymentValidationResult with validation status
     */
    fun validateCardNumber(cardNumber: String, context: Context): PaymentValidationResult {
        // Remove spaces and dashes
        val cleanNumber = cardNumber.replace("[\\s-]".toRegex(), "")
        
        if (cleanNumber.isEmpty()) {
            return PaymentValidationResult(
                false,
                context.getString(R.string.error_invalid_card_number)
            )
        }
        
        if (!isValidCardNumber(cleanNumber)) {
            return PaymentValidationResult(
                false,
                context.getString(R.string.error_invalid_card_number)
            )
        }
        
        return PaymentValidationResult(true)
    }

    /**
     * Get card type based on card number
     * @param cardNumber Credit card number (can include spaces/dashes)
     * @return CardType enum value
     */
    fun getCardType(cardNumber: String): CardType {
        // Remove spaces and dashes
        val cleanNumber = cardNumber.replace("[\\s-]".toRegex(), "")
        
        return when {
            VISA_PATTERN.matcher(cleanNumber).matches() -> CardType.VISA
            MASTERCARD_PATTERN.matcher(cleanNumber).matches() -> CardType.MASTERCARD
            AMEX_PATTERN.matcher(cleanNumber).matches() -> CardType.AMEX
            DISCOVER_PATTERN.matcher(cleanNumber).matches() -> CardType.DISCOVER
            else -> CardType.UNKNOWN
        }
    }

    /**
     * Validate credit card expiration date (MM/YY format)
     * @param expiryDate Expiration date
     * @param context Context for string resources
     * @return PaymentValidationResult with validation status
     */
    fun validateExpiryDate(expiryDate: String, context: Context): PaymentValidationResult {
        // Check format
        if (!expiryDate.matches("^(0[1-9]|1[0-2])/([0-9]{2})$".toRegex())) {
            return PaymentValidationResult(
                false,
                context.getString(R.string.error_expired_card)
            )
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
        if (year < currentYear || (year == currentYear && month < currentMonth)) {
            return PaymentValidationResult(
                false,
                context.getString(R.string.error_expired_card)
            )
        }
        
        return PaymentValidationResult(true)
    }

    /**
     * Validate credit card CVV code
     * @param cvv CVV code
     * @param cardType Card type (determines expected CVV length)
     * @param context Context for string resources
     * @return PaymentValidationResult with validation status
     */
    fun validateCVV(cvv: String, cardType: CardType, context: Context): PaymentValidationResult {
        val expectedLength = when (cardType) {
            CardType.AMEX -> 4
            else -> 3
        }
        
        if (cvv.isEmpty() || !cvv.matches("[0-9]{$expectedLength}".toRegex())) {
            return PaymentValidationResult(
                false,
                context.getString(R.string.error_invalid_cvv)
            )
        }
        
        return PaymentValidationResult(true)
    }

    /**
     * Validate cardholder name
     * @param name Cardholder name
     * @param context Context for string resources
     * @return PaymentValidationResult with validation status
     */
    fun validateCardholderName(name: String, context: Context): PaymentValidationResult {
        if (name.trim().isEmpty()) {
            return PaymentValidationResult(
                false,
                context.getString(R.string.error_invalid_name)
            )
        }
        
        return PaymentValidationResult(true)
    }

    /**
     * Format credit card number with spaces for better readability
     * @param cardNumber Credit card number
     * @return Formatted card number
     */
    fun formatCardNumber(cardNumber: String): String {
        val cleanNumber = cardNumber.replace("[\\s-]".toRegex(), "")
        val cardType = getCardType(cleanNumber)
        
        return when (cardType) {
            CardType.AMEX -> {
                // Format: XXXX XXXXXX XXXXX
                when {
                    cleanNumber.length > 10 -> 
                        "${cleanNumber.substring(0, 4)} ${cleanNumber.substring(4, 10)} ${cleanNumber.substring(10)}"
                    cleanNumber.length > 4 -> 
                        "${cleanNumber.substring(0, 4)} ${cleanNumber.substring(4)}"
                    else -> cleanNumber
                }
            }
            else -> {
                // Format: XXXX XXXX XXXX XXXX
                val result = StringBuilder()
                for (i in cleanNumber.indices) {
                    if (i > 0 && i % 4 == 0) {
                        result.append(" ")
                    }
                    result.append(cleanNumber[i])
                }
                result.toString()
            }
        }
    }

    /**
     * Format expiry date as MM/YY
     * @param month Month part of expiry date
     * @param year Year part of expiry date
     * @return Formatted expiry date
     */
    fun formatExpiryDate(month: Int, year: Int): String {
        val monthStr = if (month < 10) "0$month" else month.toString()
        val yearStr = (year % 100).toString().padStart(2, '0')
        return "$monthStr/$yearStr"
    }

    /**
     * Mask all but the last 4 digits of a card number
     * @param cardNumber Credit card number
     * @return Masked card number (e.g., **** **** **** 1234)
     */
    fun maskCardNumber(cardNumber: String): String {
        val cleanNumber = cardNumber.replace("[\\s-]".toRegex(), "")
        
        if (cleanNumber.length < 4) {
            return cleanNumber
        }
        
        val lastFour = cleanNumber.substring(cleanNumber.length - 4)
        val maskedPart = "*".repeat(cleanNumber.length - 4)
        
        val cardType = getCardType(cleanNumber)
        
        return when (cardType) {
            CardType.AMEX -> {
                // Format: **** ****** X1234
                val maskedLength = cleanNumber.length - 4
                val firstPart = "*".repeat(4)
                val secondPart = "*".repeat(maskedLength - 4)
                "$firstPart $secondPart $lastFour"
            }
            else -> {
                // Format: **** **** **** 1234
                val result = StringBuilder()
                for (i in maskedPart.indices) {
                    if (i > 0 && i % 4 == 0) {
                        result.append(" ")
                    }
                    result.append(maskedPart[i])
                }
                
                if (maskedPart.isNotEmpty()) {
                    result.append(" ")
                }
                result.append(lastFour)
                result.toString()
            }
        }
    }

    // Private helper methods
    
    /**
     * Validate credit card number using Luhn algorithm and card patterns
     * @param cardNumber Credit card number (no spaces/dashes)
     * @return true if valid, false otherwise
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
} 