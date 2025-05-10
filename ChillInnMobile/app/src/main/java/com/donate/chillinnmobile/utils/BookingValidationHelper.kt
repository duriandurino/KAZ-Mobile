package com.donate.chillinnmobile.utils

import android.content.Context
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Room
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Helper class for booking-related validation.
 * Provides validation methods specifically for the booking process including
 * date validation, availability checking, and guest count validation.
 */
object BookingValidationHelper {

    /**
     * Result of booking validation containing success status and error message
     */
    data class BookingValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    /**
     * Validate check-in and check-out dates
     * @param checkInDate The check-in date
     * @param checkOutDate The check-out date
     * @param context Context for string resources
     * @return BookingValidationResult with validation status
     */
    fun validateDates(
        checkInDate: Date,
        checkOutDate: Date,
        context: Context
    ): BookingValidationResult {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // Check if check-in date is in the past
        if (checkInDate.before(today)) {
            return BookingValidationResult(
                false,
                context.getString(R.string.error_past_check_in_date)
            )
        }

        // Check if check-out date is before check-in date
        if (checkOutDate.before(checkInDate)) {
            return BookingValidationResult(
                false,
                context.getString(R.string.error_invalid_checkout_date)
            )
        }

        // Check if check-in and check-out are the same day
        if (checkInDate.time == checkOutDate.time) {
            return BookingValidationResult(
                false,
                context.getString(R.string.error_same_day_booking)
            )
        }

        // Check if stay is within reasonable limits (e.g., less than 30 days)
        val stayDurationMs = checkOutDate.time - checkInDate.time
        val stayDurationDays = TimeUnit.MILLISECONDS.toDays(stayDurationMs)

        if (stayDurationDays > 30) {
            return BookingValidationResult(
                false,
                context.getString(R.string.error_max_stay_duration)
            )
        }

        return BookingValidationResult(true)
    }

    /**
     * Validate guest count against room capacity
     * @param guestCount Number of guests
     * @param room Room object with capacity information
     * @param context Context for string resources
     * @return BookingValidationResult with validation status
     */
    fun validateGuestCount(
        guestCount: Int,
        room: Room,
        context: Context
    ): BookingValidationResult {
        if (guestCount <= 0) {
            return BookingValidationResult(
                false,
                context.getString(R.string.error_invalid_guest_count)
            )
        }

        if (guestCount > room.capacity) {
            return BookingValidationResult(
                false,
                context.getString(
                    R.string.error_exceeds_capacity,
                    room.capacity
                )
            )
        }

        return BookingValidationResult(true)
    }

    /**
     * Calculate total stay cost based on dates and room price
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @param room Room object with pricing information
     * @return Total cost for the stay
     */
    fun calculateTotalCost(
        checkInDate: Date,
        checkOutDate: Date,
        room: Room
    ): Double {
        val stayDurationMs = checkOutDate.time - checkInDate.time
        val stayDurationDays = TimeUnit.MILLISECONDS.toDays(stayDurationMs)
        
        // Apply discount if applicable
        val pricePerNight = if (room.discountPercentage > 0) {
            room.pricePerNight * (1 - (room.discountPercentage / 100.0))
        } else {
            room.pricePerNight
        }
        
        return pricePerNight * stayDurationDays
    }

    /**
     * Format date as a readable string
     * @param date Date to format
     * @param format Optional date format pattern (defaults to "MMM dd, yyyy")
     * @return Formatted date string
     */
    fun formatDate(date: Date, format: String = "MMM dd, yyyy"): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * Parse date string to Date object
     * @param dateString Date string
     * @param format Format pattern for parsing (defaults to "MMM dd, yyyy")
     * @return Date object or null if parsing fails
     */
    fun parseDate(dateString: String, format: String = "MMM dd, yyyy"): Date? {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
} 