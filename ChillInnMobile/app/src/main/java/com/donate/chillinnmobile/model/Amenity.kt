package com.donate.chillinnmobile.model

/**
 * Amenity class representing hotel room amenities
 */
data class Amenity(
    val id: String,
    val name: String,
    val description: String,
    val icon: String? = null,
    val category: String? = null
) 