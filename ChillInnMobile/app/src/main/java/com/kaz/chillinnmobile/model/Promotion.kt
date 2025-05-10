package com.kaz.chillinnmobile.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model class representing a hotel promotion or special offer
 */
@Parcelize
data class Promotion(
    val id: String,
    val title: String,
    val description: String,
    val discount: String,
    val imageUrl: String,
    val badgeText: String,
    val startDate: Long,
    val endDate: Long,
    val promotionCode: String
) : Parcelable {
    /**
     * Checks if the promotion is currently active based on system time
     */
    fun isActive(): Boolean {
        val currentTime = System.currentTimeMillis()
        return currentTime in startDate..endDate
    }
    
    /**
     * Returns formatted duration of the promotion in days
     */
    fun getDurationInDays(): Int {
        return ((endDate - startDate) / (1000 * 60 * 60 * 24)).toInt()
    }
    
    /**
     * Returns days remaining until promotion ends
     */
    fun getDaysRemaining(): Int {
        val currentTime = System.currentTimeMillis()
        if (currentTime > endDate) return 0
        return ((endDate - currentTime) / (1000 * 60 * 60 * 24)).toInt()
    }
} 