package com.donate.chillinnmobile.repository

import com.donate.chillinnmobile.model.HotelLocation
import com.donate.chillinnmobile.model.NearbyAttraction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Repository for handling location-related data operations.
 * Currently using mock data until backend integration is complete.
 */
class LocationRepository {

    /**
     * Get location information for a specific hotel.
     * 
     * @param hotelId ID of the hotel.
     * @return HotelLocation object containing location details.
     */
    suspend fun getHotelLocation(hotelId: String): HotelLocation {
        return withContext(Dispatchers.IO) {
            // Mock data - will be replaced with API call when backend is ready
            mockHotelLocation(hotelId)
        }
    }

    /**
     * Get nearby attractions for a specific hotel.
     * 
     * @param hotelId ID of the hotel.
     * @param radius Search radius in kilometers.
     * @param categories Optional list of categories to filter results.
     * @return List of NearbyAttraction objects.
     */
    suspend fun getNearbyAttractions(
        hotelId: String, 
        radius: Double = 5.0,
        categories: List<String>? = null
    ): List<NearbyAttraction> {
        return withContext(Dispatchers.IO) {
            // Mock data - will be replaced with API call when backend is ready
            val attractions = mockNearbyAttractions(hotelId)
            
            // Filter by radius and categories if specified
            attractions.filter { attraction ->
                val withinRadius = attraction.distanceFromHotel <= radius
                val matchesCategory = categories?.contains(attraction.category) ?: true
                
                withinRadius && matchesCategory
            }.sortedBy { it.distanceFromHotel }
        }
    }
    
    /**
     * Calculate distance between two geographical points using Haversine formula.
     * 
     * @param lat1 Latitude of point 1.
     * @param lon1 Longitude of point 1.
     * @param lat2 Latitude of point 2.
     * @param lon2 Longitude of point 2.
     * @return Distance in kilometers.
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Earth radius in kilometers
        
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return r * c
    }
    
    /**
     * Calculate travel time estimation between two points.
     * 
     * @param distanceKm Distance in kilometers.
     * @param transportMode Transport mode (walking, driving, etc.).
     * @return Estimated travel time in minutes.
     */
    fun estimateTravelTime(distanceKm: Double, transportMode: String): Int {
        return when (transportMode.lowercase(Locale.ROOT)) {
            "walking" -> (distanceKm * 12).toInt() // Approx. 5 km/h
            "biking" -> (distanceKm * 4).toInt()   // Approx. 15 km/h
            "transit" -> (distanceKm * 3).toInt()  // Approx. 20 km/h with stops
            "driving" -> (distanceKm * 1.5).toInt() // Approx. 40 km/h in city
            else -> (distanceKm * 3).toInt()       // Default estimation
        }
    }

    // Mock data generators

    private fun mockHotelLocation(hotelId: String): HotelLocation {
        return HotelLocation(
            id = UUID.randomUUID().toString(),
            hotelId = hotelId,
            latitude = 40.7128,  // New York City coordinates
            longitude = -74.0060,
            address = "123 Luxury Avenue",
            city = "New York",
            state = "NY",
            country = "USA",
            zipCode = "10001",
            mapImageUrl = "https://example.com/maps/hotel_location.jpg",
            parkingAvailable = true,
            parkingDescription = "Valet parking available for $25/day. Self-parking available for $15/day.",
            publicTransitNotes = "2 blocks from 34th St. Penn Station subway. Bus stops directly in front of hotel.",
            distanceFromCityCenter = 1.2
        )
    }

    private fun mockNearbyAttractions(hotelId: String): List<NearbyAttraction> {
        return listOf(
            NearbyAttraction(
                id = UUID.randomUUID().toString(),
                name = "Central Park",
                description = "Iconic 843-acre urban park with walking paths, a zoo, carousel, and boat rentals.",
                latitude = 40.7812,
                longitude = -73.9665,
                address = "Central Park, New York, NY",
                distanceFromHotel = 3.2,
                category = "Park",
                imageUrl = "https://example.com/images/central_park.jpg",
                websiteUrl = "https://www.centralparknyc.org/",
                openingHours = "6:00 AM - 1:00 AM daily",
                priceLevel = 1,
                rating = 4.8f
            ),
            NearbyAttraction(
                id = UUID.randomUUID().toString(),
                name = "Empire State Building",
                description = "Iconic 102-story skyscraper with observation decks offering panoramic city views.",
                latitude = 40.7484,
                longitude = -73.9857,
                address = "350 5th Ave, New York, NY 10118",
                distanceFromHotel = 0.6,
                category = "Landmark",
                imageUrl = "https://example.com/images/empire_state.jpg",
                websiteUrl = "https://www.esbnyc.com/",
                openingHours = "8:00 AM - 2:00 AM daily",
                priceLevel = 3,
                rating = 4.7f
            ),
            NearbyAttraction(
                id = UUID.randomUUID().toString(),
                name = "The Metropolitan Museum of Art",
                description = "Vast collection of art spanning 5,000 years of world culture.",
                latitude = 40.7794,
                longitude = -73.9632,
                address = "1000 5th Ave, New York, NY 10028",
                distanceFromHotel = 4.1,
                category = "Museum",
                imageUrl = "https://example.com/images/met_museum.jpg",
                websiteUrl = "https://www.metmuseum.org/",
                openingHours = "10:00 AM - 5:30 PM Sun-Thu, 10:00 AM - 9:00 PM Fri-Sat",
                priceLevel = 2,
                rating = 4.9f
            ),
            NearbyAttraction(
                id = UUID.randomUUID().toString(),
                name = "Times Square",
                description = "Iconic, busy intersection known for bright lights, Broadway theaters & commercial activity.",
                latitude = 40.7580,
                longitude = -73.9855,
                address = "Manhattan, NY 10036",
                distanceFromHotel = 1.1,
                category = "Landmark",
                imageUrl = "https://example.com/images/times_square.jpg",
                websiteUrl = "https://www.timessquarenyc.org/",
                openingHours = "Always open",
                priceLevel = 1,
                rating = 4.5f
            ),
            NearbyAttraction(
                id = UUID.randomUUID().toString(),
                name = "Broadway Theatre District",
                description = "Heart of American theater industry featuring shows in historic venues.",
                latitude = 40.7590,
                longitude = -73.9845,
                address = "Broadway, New York, NY",
                distanceFromHotel = 1.3,
                category = "Entertainment",
                imageUrl = "https://example.com/images/broadway.jpg",
                websiteUrl = "https://www.broadway.org/",
                openingHours = "Varies by show",
                priceLevel = 4,
                rating = 4.8f
            ),
            NearbyAttraction(
                id = UUID.randomUUID().toString(),
                name = "High Line",
                description = "Elevated linear park created on a former railroad track with gardens & art installations.",
                latitude = 40.7480,
                longitude = -74.0048,
                address = "New York, NY 10011",
                distanceFromHotel = 1.8,
                category = "Park",
                imageUrl = "https://example.com/images/high_line.jpg",
                websiteUrl = "https://www.thehighline.org/",
                openingHours = "7:00 AM - 10:00 PM daily",
                priceLevel = 1,
                rating = 4.6f
            ),
            NearbyAttraction(
                id = UUID.randomUUID().toString(),
                name = "Chelsea Market",
                description = "Food hall & shopping mall in a historic factory building with diverse vendors.",
                latitude = 40.7420,
                longitude = -74.0048,
                address = "75 9th Ave, New York, NY 10011",
                distanceFromHotel = 2.4,
                category = "Shopping",
                imageUrl = "https://example.com/images/chelsea_market.jpg",
                websiteUrl = "https://www.chelseamarket.com/",
                openingHours = "7:00 AM - 9:00 PM Mon-Sat, 8:00 AM - 8:00 PM Sun",
                priceLevel = 2,
                rating = 4.6f
            ),
            NearbyAttraction(
                id = UUID.randomUUID().toString(),
                name = "Grand Central Terminal",
                description = "Historic train station with ornate architecture, dining & shops.",
                latitude = 40.7527,
                longitude = -73.9772,
                address = "89 E 42nd St, New York, NY 10017",
                distanceFromHotel = 0.9,
                category = "Landmark",
                imageUrl = "https://example.com/images/grand_central.jpg",
                websiteUrl = "https://www.grandcentralterminal.com/",
                openingHours = "5:30 AM - 2:00 AM daily",
                priceLevel = 1,
                rating = 4.7f
            ),
            NearbyAttraction(
                id = UUID.randomUUID().toString(),
                name = "Museum of Modern Art (MoMA)",
                description = "World-class modern & contemporary art museum with famous works.",
                latitude = 40.7614,
                longitude = -73.9776,
                address = "11 W 53rd St, New York, NY 10019",
                distanceFromHotel = 1.7,
                category = "Museum",
                imageUrl = "https://example.com/images/moma.jpg",
                websiteUrl = "https://www.moma.org/",
                openingHours = "10:30 AM - 5:30 PM daily, closed Tuesdays",
                priceLevel = 3,
                rating = 4.8f
            ),
            NearbyAttraction(
                id = UUID.randomUUID().toString(),
                name = "One World Trade Center",
                description = "Tallest building in the Western Hemisphere with observation deck offering panoramic views.",
                latitude = 40.7127,
                longitude = -74.0134,
                address = "285 Fulton St, New York, NY 10007",
                distanceFromHotel = 3.5,
                category = "Landmark",
                imageUrl = "https://example.com/images/one_world_trade.jpg",
                websiteUrl = "https://www.wtc.com/",
                openingHours = "9:00 AM - 9:00 PM daily",
                priceLevel = 3,
                rating = 4.7f
            )
        )
    }
} 