package com.donate.chillinnmobile.model

import java.util.Date

/**
 * Guest class representing hotel guests
 */
data class Guest(
    val user: User,
    val address: String,
    val preferences: List<String>? = null,
    val loyaltyPoints: Int = 0,
    val bookingHistory: List<String>? = null // List of booking IDs
) 