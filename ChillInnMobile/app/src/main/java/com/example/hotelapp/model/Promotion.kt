package com.example.hotelapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Data model representing a hotel promotion or special offer.
 * This class contains all information needed for displaying promotions
 * in the app and sending notifications.
 */
@Parcelize
data class Promotion(
    val id: String,
    val title: String,
    val shortDescription: String,
    val description: String,
    val discountPercentage: Int? = null,
    val discountAmount: Double? = null,
    val startDate: Date? = null,
    val endDate: Date? = null,
    val imageUrl: String? = null,
    val promoCode: String? = null,
    val isExclusive: Boolean = false,
    val badgeText: String? = null,
    val targetRoomTypes: List<String>? = null,
    val minNights: Int? = null,
    val termsAndConditions: String? = null,
    val isActive: Boolean = true
) : Parcelable {

    /**
     * Check if the promotion is currently valid based on dates
     */
    fun isValid(): Boolean {
        if (!isActive) return false
        
        val now = Date()
        if (startDate != null && now.before(startDate)) return false
        if (endDate != null && now.after(endDate)) return false
        
        return true
    }
    
    /**
     * Get days remaining until promotion expires
     * @return Number of days remaining, or null if no end date
     */
    fun getDaysRemaining(): Int? {
        if (endDate == null) return null
        
        val now = Date()
        if (now.after(endDate)) return 0
        
        val diffInMillis = endDate.time - now.time
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }
    
    /**
     * Get formatted discount text
     * @return Formatted discount string (e.g., "30% OFF" or "$50 OFF")
     */
    fun getFormattedDiscount(): String? {
        return when {
            discountPercentage != null -> "$discountPercentage% OFF"
            discountAmount != null -> "$$discountAmount OFF"
            else -> null
        }
    }
    
    /**
     * Get the duration of the promotion in days
     * @return Number of days the promotion is valid for, or null if no start/end date
     */
    fun getDurationInDays(): Int? {
        if (startDate == null || endDate == null) return null
        
        val diffInMillis = endDate.time - startDate.time
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }
    
    /**
     * Get a status message about the promotion's timeframe
     * @return User-friendly string about promotion timeframe
     */
    fun getTimeframeMessage(): String {
        val daysRemaining = getDaysRemaining()
        
        return when {
            daysRemaining == null -> "Limited time offer"
            daysRemaining <= 1 -> "Ends today!"
            daysRemaining <= 3 -> "Ends in $daysRemaining days!"
            daysRemaining <= 7 -> "Ends this week!"
            daysRemaining <= 30 -> "Ends in $daysRemaining days"
            else -> "Limited time offer"
        }
    }
} 