package com.donate.chillinnmobile.model

import java.util.Date

/**
 * Model class for room bookings
 */
data class Booking(
    val id: String,
    val userId: String,
    val roomId: String,
    val roomType: String? = null,
    val checkInDate: Date,
    val checkOutDate: Date,
    val guestCount: Int,
    val totalPrice: Double,
    val status: String, // PENDING, CONFIRMED, CANCELLED, COMPLETED
    val paymentStatus: String? = null, // PENDING, PAID, REFUNDED
    val specialRequests: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Enum representing different booking statuses
 */
enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED,
    NO_SHOW
} 