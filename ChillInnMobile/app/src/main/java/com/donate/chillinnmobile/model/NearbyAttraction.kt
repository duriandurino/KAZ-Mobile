package com.donate.chillinnmobile.model

import java.io.Serializable

/**
 * Data class representing a point of interest/attraction near the hotel.
 * 
 * @property id Unique identifier for the attraction
 * @property name Name of the attraction
 * @property description Brief description of the attraction
 * @property latitude Geographical latitude coordinate
 * @property longitude Geographical longitude coordinate
 * @property address Complete address of the attraction
 * @property distanceFromHotel Distance from the hotel in kilometers
 * @property category Category of the attraction (e.g., Restaurant, Museum)
 * @property imageUrl URL to an image of the attraction
 * @property websiteUrl URL to the attraction's website if available
 * @property openingHours Opening hours information as text
 * @property priceLevel Price level indicator (1-4, where 1 is least expensive)
 * @property rating Average rating of the attraction (0-5)
 */
data class NearbyAttraction(
    val id: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val distanceFromHotel: Double,
    val category: String,
    val imageUrl: String? = null,
    val websiteUrl: String? = null,
    val openingHours: String? = null,
    val priceLevel: Int? = null,
    val rating: Float = 0f
) : Serializable 