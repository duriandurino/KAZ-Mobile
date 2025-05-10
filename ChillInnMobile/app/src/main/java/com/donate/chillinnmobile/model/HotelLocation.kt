package com.donate.chillinnmobile.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * Data class representing a hotel's location details.
 * 
 * @property id Unique identifier for the hotel location
 * @property hotelId Reference to the associated hotel
 * @property latitude Geographical latitude coordinate
 * @property longitude Geographical longitude coordinate 
 * @property address Complete address of the hotel
 * @property city City where the hotel is located
 * @property state State or region where the hotel is located
 * @property country Country where the hotel is located
 * @property zipCode Postal/zip code of the hotel location
 * @property mapImageUrl URL to a static map image showing hotel location
 * @property parkingAvailable Whether parking is available at the hotel
 * @property parkingDescription Description of parking options if available
 * @property publicTransitNotes Information about nearby public transit options
 * @property distanceFromCityCenter Distance from city center in kilometers
 */
data class HotelLocation(
    val id: String,
    val hotelId: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val zipCode: String,
    val mapImageUrl: String? = null,
    val parkingAvailable: Boolean = false,
    val parkingDescription: String? = null,
    val publicTransitNotes: String? = null,
    val distanceFromCityCenter: Double? = null
) : Serializable

/**
 * Data class representing a point of interest near the hotel
 */
@Parcelize
data class NearbyAttraction(
    val id: String,
    val name: String,
    val description: String? = null,
    val category: AttractionCategory,
    val latitude: Double,
    val longitude: Double,
    val distanceFromHotel: Float, // in kilometers
    val imageUrl: String? = null,
    val rating: Float = 0f,
    val priceLevel: Int = 0 // 0-4, where 0 is free and 4 is very expensive
) : Parcelable

/**
 * Enum representing different types of attractions
 */
enum class AttractionCategory {
    RESTAURANT,
    CAFE,
    SHOPPING,
    MUSEUM,
    PARK,
    BEACH,
    HISTORICAL,
    ENTERTAINMENT,
    NIGHTLIFE,
    OTHER
} 