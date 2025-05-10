package com.donate.chillinnmobile.model

import java.util.Date

/**
 * Recommendation class representing personalized room recommendations for guests
 */
data class Recommendation(
    val id: String,
    val guestId: String,
    val roomId: String,
    val reason: String,
    val discount: Double? = 0.0,
    val validUntil: Date? = null,
    val createdAt: Date = Date()
) 