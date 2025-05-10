package com.donate.chillinnmobile.model

import java.util.Date

/**
 * Payment class representing booking payments
 */
data class Payment(
    val id: String,
    val bookingId: String,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val status: PaymentStatus,
    val transactionId: String? = null,
    val timestamp: Date = Date()
)

/**
 * Enum representing different payment methods
 */
enum class PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL,
    BANK_TRANSFER,
    CASH
}

/**
 * Enum representing different payment statuses
 */
enum class PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    REFUNDED
} 