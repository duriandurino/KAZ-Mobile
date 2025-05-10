package com.donate.chillinnmobile.repository

import com.donate.chillinnmobile.model.AttractionCategory
import com.donate.chillinnmobile.model.HotelLocation
import com.donate.chillinnmobile.model.NearbyAttraction
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Mock repository for location-related data operations
 */
class MockLocationRepository {
    
    // Mock hotel location data
    private val hotel = HotelLocation(
        id = "hotel_main",
        name = "Chill Inn Resort & Spa",
        address = "123 Relaxation Boulevard, Paradise City, 98765",
        latitude = 34.0522, // Los Angeles coordinates (for example)
        longitude = -118.2437,
        description = "Experience luxury and tranquility at Chill Inn Resort & Spa, where every stay is designed to provide ultimate relaxation and comfort. Our hotel features modern amenities, stunning views, and exceptional service to make your stay unforgettable.",
        phoneNumber = "+1 (555) 123-4567",
        email = "info@chillinnresort.com",
        website = "https://www.chillinnresort.com",
        imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1080&q=80",
        rating = 4.8f,
        nearbyAttractions = generateMockAttractions()
    )
    
    /**
     * Get hotel location details
     */
    suspend fun getHotelLocation(): HotelLocation {
        // Simulate network delay
        delay(800)
        return hotel
    }
    
    /**
     * Get nearby attractions
     */
    suspend fun getNearbyAttractions(): List<NearbyAttraction> {
        // Simulate network delay
        delay(1000)
        return hotel.nearbyAttractions
    }
    
    /**
     * Get attractions by category
     */
    suspend fun getAttractionsByCategory(category: AttractionCategory): List<NearbyAttraction> {
        // Simulate network delay
        delay(800)
        return hotel.nearbyAttractions.filter { it.category == category }
    }
    
    /**
     * Generate mock nearby attractions
     */
    private fun generateMockAttractions(): List<NearbyAttraction> {
        return listOf(
            NearbyAttraction(
                id = "attr_1",
                name = "Seaside Gourmet Restaurant",
                description = "Fine dining with ocean views, specializing in seafood and local cuisine.",
                category = AttractionCategory.RESTAURANT,
                latitude = 34.0530,
                longitude = -118.2457,
                distanceFromHotel = 0.5f,
                imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1080&q=80",
                rating = 4.7f,
                priceLevel = 3
            ),
            NearbyAttraction(
                id = "attr_2",
                name = "Cultural History Museum",
                description = "Explore the rich heritage and history of the region through interactive exhibits.",
                category = AttractionCategory.MUSEUM,
                latitude = 34.0510,
                longitude = -118.2427,
                distanceFromHotel = 0.8f,
                imageUrl = "https://images.unsplash.com/photo-1566127992631-137a642a90f4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1080&q=80",
                rating = 4.5f,
                priceLevel = 1
            ),
            NearbyAttraction(
                id = "attr_3",
                name = "Emerald City Park",
                description = "A beautiful urban park with walking trails, gardens, and recreational areas.",
                category = AttractionCategory.PARK,
                latitude = 34.0540,
                longitude = -118.2447,
                distanceFromHotel = 1.2f,
                imageUrl = "https://images.unsplash.com/photo-1568515387631-8b650bbcdb90?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1080&q=80",
                rating = 4.6f,
                priceLevel = 0
            ),
            NearbyAttraction(
                id = "attr_4",
                name = "Sunset Beach",
                description = "Pristine sandy beach with crystal clear waters, perfect for swimming and sunbathing.",
                category = AttractionCategory.BEACH,
                latitude = 34.0505,
                longitude = -118.2507,
                distanceFromHotel = 1.5f,
                imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1080&q=80",
                rating = 4.9f,
                priceLevel = 0
            ),
            NearbyAttraction(
                id = "attr_5",
                name = "Luxury Shopping Mall",
                description = "High-end shopping destination featuring designer brands and luxury goods.",
                category = AttractionCategory.SHOPPING,
                latitude = 34.0535,
                longitude = -118.2427,
                distanceFromHotel = 0.9f,
                imageUrl = "https://images.unsplash.com/photo-1555529771-7888783a18d3?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1080&q=80",
                rating = 4.4f,
                priceLevel = 3
            ),
            NearbyAttraction(
                id = "attr_6",
                name = "Café Artisan",
                description = "Cozy café serving specialty coffee, pastries, and light meals in a relaxed atmosphere.",
                category = AttractionCategory.CAFE,
                latitude = 34.0518,
                longitude = -118.2442,
                distanceFromHotel = 0.3f,
                imageUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1080&q=80",
                rating = 4.6f,
                priceLevel = 2
            ),
            NearbyAttraction(
                id = "attr_7",
                name = "Historic Lighthouse",
                description = "19th century lighthouse offering panoramic ocean views and guided historical tours.",
                category = AttractionCategory.HISTORICAL,
                latitude = 34.0495,
                longitude = -118.2517,
                distanceFromHotel = 1.7f,
                imageUrl = "https://images.unsplash.com/photo-1682804554461-6198c0fe159b?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1080&q=80",
                rating = 4.5f,
                priceLevel = 1
            ),
            NearbyAttraction(
                id = "attr_8",
                name = "Paradise Cinema",
                description = "Luxury movie theater with reclining seats and gourmet food service.",
                category = AttractionCategory.ENTERTAINMENT,
                latitude = 34.0528,
                longitude = -118.2420,
                distanceFromHotel = 0.7f,
                imageUrl = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1080&q=80",
                rating = 4.3f,
                priceLevel = 2
            ),
            NearbyAttraction(
                id = "attr_9",
                name = "Skyline Bar & Lounge",
                description = "Rooftop bar offering craft cocktails, small plates, and stunning city views.",
                category = AttractionCategory.NIGHTLIFE,
                latitude = 34.0525,
                longitude = -118.2452,
                distanceFromHotel = 0.4f,
                imageUrl = "https://images.unsplash.com/photo-1514933651103-005eec06c04b?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1080&q=80",
                rating = 4.7f,
                priceLevel = 3
            ),
            NearbyAttraction(
                id = "attr_10",
                name = "Spa Retreat",
                description = "Luxurious day spa offering massages, facials, and holistic wellness treatments.",
                category = AttractionCategory.OTHER,
                latitude = 34.0519,
                longitude = -118.2430,
                distanceFromHotel = 0.2f,
                imageUrl = "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1080&q=80",
                rating = 4.8f,
                priceLevel = 4
            )
        )
    }
    
    /**
     * Get directions from hotel to attraction (mock data)
     */
    suspend fun getDirections(attractionId: String): List<Pair<Double, Double>> {
        // Simulate network delay
        delay(1200)
        
        val attraction = hotel.nearbyAttractions.find { it.id == attractionId }
            ?: return emptyList()
        
        // Create a simple mock path between the hotel and the attraction
        return createMockPath(
            startLat = hotel.latitude,
            startLng = hotel.longitude,
            endLat = attraction.latitude,
            endLng = attraction.longitude
        )
    }
    
    /**
     * Create a mock path between two points
     * In a real app, this would come from a directions API
     */
    private fun createMockPath(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): List<Pair<Double, Double>> {
        val path = mutableListOf<Pair<Double, Double>>()
        
        // Start point
        path.add(Pair(startLat, startLng))
        
        // Generate some intermediate points
        val latStep = (endLat - startLat) / 8
        val lngStep = (endLng - startLng) / 8
        
        for (i in 1..7) {
            // Add some randomness to make the path look more realistic
            val randomFactor = (Math.random() * 0.0002) - 0.0001
            path.add(
                Pair(
                    startLat + (latStep * i) + randomFactor,
                    startLng + (lngStep * i) + randomFactor
                )
            )
        }
        
        // End point
        path.add(Pair(endLat, endLng))
        
        return path
    }
} 