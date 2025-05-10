package com.donate.chillinnmobile.model

import java.util.Date

/**
 * Room class representing hotel rooms
 */
data class Room(
    val id: String,
    val name: String,
    val roomNumber: String,
    val roomType: String,
    val floor: Int,
    val pricePerNight: Double,
    val discountPercentage: Int = 0,
    val description: String,
    val capacity: Int,
    val status: String,
    val amenities: List<String>? = null,
    val images: List<Image>? = null,
    val rating: Float = 0f,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Enum representing different room statuses
 */
enum class RoomStatus {
    AVAILABLE,
    BOOKED,
    MAINTENANCE,
    CLEANING
} 