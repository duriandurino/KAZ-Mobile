package com.donate.chillinnmobile.model

/**
 * RoomType class representing different types of hotel rooms
 */
data class RoomType(
    val id: String,
    val name: String,
    val description: String,
    val basePrice: Double,
    val maxOccupancy: Int,
    val bedConfiguration: String
) 