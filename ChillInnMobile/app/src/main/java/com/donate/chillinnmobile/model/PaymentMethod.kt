package com.donate.chillinnmobile.model

/**
 * Model representing a saved payment method
 */
data class PaymentMethod(
    val id: String,
    val userId: String,
    val type: PaymentMethodType,
    val cardNumber: String? = null,         // Last 4 digits shown to user
    val cardholderName: String? = null,
    val expiryMonth: Int? = null,
    val expiryYear: Int? = null,
    val isDefault: Boolean = false
) {
    /**
     * Returns masked card number for display
     */
    fun getDisplayCardNumber(): String {
        return cardNumber?.let { number ->
            "•••• •••• •••• $number"
        } ?: "••••"
    }

    /**
     * Returns formatted expiration date
     */
    fun getExpiryDate(): String {
        return if (expiryMonth != null && expiryYear != null) {
            String.format("%02d/%02d", expiryMonth, expiryYear % 100)
        } else {
            ""
        }
    }
}

/**
 * Enum for different payment method types
 */
enum class PaymentMethodType {
    VISA,
    MASTERCARD,
    AMEX,
    DISCOVER,
    PAYPAL,
    OTHER
} 