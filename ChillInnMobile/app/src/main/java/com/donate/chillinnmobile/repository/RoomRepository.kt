package com.donate.chillinnmobile.repository

import android.util.Log
import com.donate.chillinnmobile.model.Review
import com.donate.chillinnmobile.model.Room
import com.donate.chillinnmobile.network.ApiClient
import com.donate.chillinnmobile.utils.createDemoRoomImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Date
import java.util.UUID
import kotlin.random.Random

/**
 * Repository for handling room-related data operations
 */
class RoomRepository {
    private val apiService = ApiClient.apiService
    private val TAG = "RoomRepository"
    
    // Mock data for demo - will be replaced with actual API calls when backend is ready
    private val mockRooms = listOf(
        Room(
            id = "room-1",
            name = "Deluxe King Room",
            roomType = "Deluxe",
            roomNumber = "101",
            description = "Spacious room with a king-size bed, featuring modern decor and amenities. The room comes with a private bathroom with a shower and bathtub, free Wi-Fi, a flat-screen TV, and a mini-bar.",
            capacity = 2,
            pricePerNight = 150.0,
            discountPercentage = 0,
            floor = 1,
            status = "AVAILABLE",
            amenities = listOf("Free Wi-Fi", "Mini-bar", "TV", "Air Conditioning", "Bathtub"),
            images = null, // Will be populated with createDemoRoomImages
            rating = 4.5f
        ),
        Room(
            id = "room-2",
            name = "Executive Suite",
            roomType = "Suite",
            roomNumber = "201",
            description = "Luxurious suite with separate living area and bedroom. Features include a king-size bed, work desk, sofa, dining area, and premium amenities. Enjoy city views, premium toiletries, and complimentary breakfast.",
            capacity = 3,
            pricePerNight = 250.0,
            discountPercentage = 10,
            floor = 2,
            status = "AVAILABLE",
            amenities = listOf("Free Wi-Fi", "Mini-bar", "TV", "Air Conditioning", "Living Area", "Breakfast Included"),
            images = null, // Will be populated with createDemoRoomImages
            rating = 4.8f
        ),
        Room(
            id = "room-3",
            name = "Standard Twin Room",
            roomType = "Standard",
            roomNumber = "102",
            description = "Comfortable room with two twin beds, ideal for friends or colleagues. Features include a work desk, private bathroom with shower, flat-screen TV, and coffee maker.",
            capacity = 2,
            pricePerNight = 120.0,
            discountPercentage = 0,
            floor = 1,
            status = "AVAILABLE",
            amenities = listOf("Free Wi-Fi", "TV", "Coffee Maker", "Air Conditioning"),
            images = null, // Will be populated with createDemoRoomImages
            rating = 4.2f
        ),
        Room(
            id = "room-4",
            name = "Family Suite",
            roomType = "Suite",
            roomNumber = "301",
            description = "Spacious suite designed for families, featuring a master bedroom with king-size bed and a second bedroom with two twin beds. Includes a living area, two bathrooms, and family-friendly amenities.",
            capacity = 4,
            pricePerNight = 300.0,
            discountPercentage = 15,
            floor = 3,
            status = "AVAILABLE",
            amenities = listOf("Free Wi-Fi", "Mini-bar", "TV", "Air Conditioning", "Living Area", "Multiple Bedrooms", "Breakfast Included"),
            images = null, // Will be populated with createDemoRoomImages
            rating = 4.7f
        ),
        Room(
            id = "room-5",
            name = "Honeymoon Suite",
            roomType = "Suite",
            roomNumber = "401",
            description = "Romantic suite ideal for couples, featuring a king-size bed, jacuzzi bathtub, champagne service, and panoramic city views. Includes a private balcony and special romantic amenities.",
            capacity = 2,
            pricePerNight = 350.0,
            discountPercentage = 0,
            floor = 4,
            status = "AVAILABLE",
            amenities = listOf("Free Wi-Fi", "Mini-bar", "TV", "Air Conditioning", "Jacuzzi", "Balcony", "Champagne Service"),
            images = null, // Will be populated with createDemoRoomImages
            rating = 4.9f
        )
    ).map { room ->
        // Create demo images for each room
        room.copy(images = createDemoRoomImages(room.id))
    }
    
    private val mockReviews = mutableMapOf<String, List<Review>>().apply {
        mockRooms.forEach { room ->
            val roomReviews = (1..Random.nextInt(5, 15)).map { 
                Review(
                    id = UUID.randomUUID().toString(),
                    roomId = room.id,
                    userId = "user-${Random.nextInt(1, 100)}",
                    userName = "Guest ${Random.nextInt(1000, 9999)}",
                    rating = Random.nextInt(3, 6).toFloat(),
                    comment = getRandomReviewComment(),
                    date = Date(System.currentTimeMillis() - Random.nextLong(1000000000, 10000000000))
                )
            }
            put(room.id, roomReviews)
        }
    }
    
    private fun getRandomReviewComment(): String {
        val comments = listOf(
            "Great room, very comfortable and clean.",
            "Excellent service, would definitely stay again.",
            "The room was nice but a bit small.",
            "Loved the amenities, especially the jacuzzi.",
            "Perfect location and amazing view.",
            "The staff was very helpful and friendly.",
            "Good value for money, would recommend.",
            "The room exceeded our expectations.",
            "Very satisfied with our stay.",
            "Clean, comfortable, and quiet."
        )
        return comments[Random.nextInt(comments.size)]
    }
    
    /**
     * Get all available rooms with optional filters
     */
    suspend fun getRooms(
        checkInDate: String? = null,
        checkOutDate: String? = null,
        capacity: Int? = null,
        priceMin: Double? = null,
        priceMax: Double? = null,
        roomTypes: List<String>? = null,
        amenities: List<String>? = null,
        minRating: Float? = null
    ): List<Room>? = withContext(Dispatchers.IO) {
        try {
            // Simulate network delay
            delay(1000)
            
            // If backend is not ready, use mock data
            var filteredRooms = mockRooms
            
            // Apply filters
            capacity?.let { cap ->
                filteredRooms = filteredRooms.filter { it.capacity >= cap }
            }
            
            priceMin?.let { min ->
                filteredRooms = filteredRooms.filter { it.pricePerNight >= min }
            }
            
            priceMax?.let { max ->
                filteredRooms = filteredRooms.filter { it.pricePerNight <= max }
            }
            
            roomTypes?.let { types ->
                if (types.isNotEmpty()) {
                    filteredRooms = filteredRooms.filter { room -> 
                        types.contains(room.roomType) 
                    }
                }
            }
            
            amenities?.let { amenitiesList ->
                if (amenitiesList.isNotEmpty()) {
                    filteredRooms = filteredRooms.filter { room ->
                        room.amenities?.any { amenity -> 
                            amenitiesList.any { it.equals(amenity, ignoreCase = true) }
                        } ?: false
                    }
                }
            }
            
            minRating?.let { rating ->
                if (rating > 0) {
                    filteredRooms = filteredRooms.filter { it.rating >= rating }
                }
            }
            
            return@withContext filteredRooms
            
            // When backend is ready, use this code instead:
            /*
            val filters = mutableMapOf<String, String>()
            
            checkInDate?.let { filters["checkInDate"] = it }
            checkOutDate?.let { filters["checkOutDate"] = it }
            capacity?.let { filters["capacity"] = it.toString() }
            priceMin?.let { filters["priceMin"] = it.toString() }
            priceMax?.let { filters["priceMax"] = it.toString() }
            
            roomTypes?.let { 
                if (it.isNotEmpty()) filters["roomTypes"] = it.joinToString(",") 
            }
            
            amenities?.let { 
                if (it.isNotEmpty()) filters["amenities"] = it.joinToString(",") 
            }
            
            minRating?.let { filters["minRating"] = it.toString() }
            
            val response = apiService.getRooms(filters)
            
            if (response.isSuccessful && response.body() != null) {
                return@withContext response.body()
            } else {
                Log.e(TAG, "Failed to get rooms: ${response.errorBody()?.string()}")
                return@withContext null
            }
            */
        } catch (e: IOException) {
            Log.e(TAG, "Network error getting rooms", e)
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting rooms", e)
            return@withContext null
        }
    }
    
    /**
     * Search for rooms by name, description, or features
     */
    suspend fun searchRooms(query: String): List<Room>? = withContext(Dispatchers.IO) {
        try {
            // Simulate network delay
            delay(800)
            
            // For demo, filter mock rooms by name, type, or description
            val lowercaseQuery = query.lowercase()
            return@withContext mockRooms.filter { room ->
                room.name.lowercase().contains(lowercaseQuery) ||
                room.roomType.lowercase().contains(lowercaseQuery) ||
                room.description.lowercase().contains(lowercaseQuery) ||
                room.amenities?.any { it.lowercase().contains(lowercaseQuery) } ?: false
            }
            
            // When backend is ready, use this code instead:
            /*
            val response = apiService.searchRooms(query)
            
            if (response.isSuccessful && response.body() != null) {
                return@withContext response.body()
            } else {
                Log.e(TAG, "Failed to search rooms: ${response.errorBody()?.string()}")
                return@withContext null
            }
            */
        } catch (e: IOException) {
            Log.e(TAG, "Network error searching rooms", e)
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "Error searching rooms", e)
            return@withContext null
        }
    }
    
    /**
     * Get details for a specific room
     */
    suspend fun getRoomDetails(roomId: String): Room? = withContext(Dispatchers.IO) {
        try {
            // Simulate network delay
            delay(800)
            
            // If backend is not ready, use mock data
            return@withContext mockRooms.find { it.id == roomId }
            
            // When backend is ready, use this code instead:
            /*
            val response = apiService.getRoomDetails(roomId)
            
            if (response.isSuccessful && response.body() != null) {
                return@withContext response.body()
            } else {
                Log.e(TAG, "Failed to get room details: ${response.errorBody()?.string()}")
                return@withContext null
            }
            */
        } catch (e: IOException) {
            Log.e(TAG, "Network error getting room details", e)
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting room details", e)
            return@withContext null
        }
    }
    
    /**
     * Admin function: Create a new room
     */
    suspend fun createRoom(roomData: Map<String, Any>): Room? = withContext(Dispatchers.IO) {
        try {
            // Simulate network delay
            delay(1500)
            
            // For demo, just return a mock room
            val newRoomId = "room-${mockRooms.size + 1}"
            val newRoom = Room(
                id = newRoomId,
                name = roomData["name"] as? String ?: "New Room",
                roomType = roomData["roomType"] as? String ?: "Standard",
                roomNumber = roomData["roomNumber"] as? String ?: "101",
                description = roomData["description"] as? String ?: "New room description",
                capacity = (roomData["capacity"] as? Int) ?: 2,
                pricePerNight = (roomData["pricePerNight"] as? Double) ?: 100.0,
                discountPercentage = (roomData["discountPercentage"] as? Int) ?: 0,
                floor = (roomData["floor"] as? Int) ?: 1,
                status = "AVAILABLE",
                amenities = (roomData["amenities"] as? List<String>) ?: listOf("Free Wi-Fi", "TV"),
                images = createDemoRoomImages(newRoomId),
                rating = 0f  // New room has no ratings yet
            )
            
            return@withContext newRoom
            
            // When backend is ready, use this code:
            /*
            val response = apiService.createRoom(roomData)
            
            if (response.isSuccessful && response.body() != null) {
                return@withContext response.body()
            } else {
                Log.e(TAG, "Failed to create room: ${response.errorBody()?.string()}")
                return@withContext null
            }
            */
        } catch (e: Exception) {
            Log.e(TAG, "Error creating room", e)
            return@withContext null
        }
    }
    
    /**
     * Admin function: Update an existing room
     */
    suspend fun updateRoom(roomId: String, updateData: Map<String, Any>): Room? = withContext(Dispatchers.IO) {
        try {
            // Simulate network delay
            delay(1000)
            
            // For demo, find the room and return a modified version
            val room = mockRooms.find { it.id == roomId } ?: return@withContext null
            
            return@withContext room.copy(
                name = updateData["name"] as? String ?: room.name,
                roomType = updateData["roomType"] as? String ?: room.roomType,
                roomNumber = updateData["roomNumber"] as? String ?: room.roomNumber,
                description = updateData["description"] as? String ?: room.description,
                capacity = (updateData["capacity"] as? Int) ?: room.capacity,
                pricePerNight = (updateData["pricePerNight"] as? Double) ?: room.pricePerNight,
                discountPercentage = (updateData["discountPercentage"] as? Int) ?: room.discountPercentage,
                floor = (updateData["floor"] as? Int) ?: room.floor,
                status = updateData["status"] as? String ?: room.status,
                amenities = (updateData["amenities"] as? List<String>) ?: room.amenities
            )
            
            // When backend is ready, use this code:
            /*
            val response = apiService.updateRoom(roomId, updateData)
            
            if (response.isSuccessful && response.body() != null) {
                return@withContext response.body()
            } else {
                Log.e(TAG, "Failed to update room: ${response.errorBody()?.string()}")
                return@withContext null
            }
            */
        } catch (e: Exception) {
            Log.e(TAG, "Error updating room", e)
            return@withContext null
        }
    }
    
    /**
     * Admin function: Delete a room
     */
    suspend fun deleteRoom(roomId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Simulate network delay
            delay(800)
            
            // For demo, just return success
            return@withContext true
            
            // When backend is ready, use this code:
            /*
            val response = apiService.deleteRoom(roomId)
            
            if (response.isSuccessful) {
                return@withContext true
            } else {
                Log.e(TAG, "Failed to delete room: ${response.errorBody()?.string()}")
                return@withContext false
            }
            */
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting room", e)
            return@withContext false
        }
    }
    
    /**
     * Get reviews for a specific room
     */
    suspend fun getRoomReviews(roomId: String): List<Review>? = withContext(Dispatchers.IO) {
        try {
            // Simulate network delay
            delay(600)
            
            // If backend is not ready, use mock data
            return@withContext mockReviews[roomId] ?: emptyList()
            
            // When backend is ready, use this code:
            /*
            val response = apiService.getRoomReviews(roomId)
            
            if (response.isSuccessful && response.body() != null) {
                return@withContext response.body()
            } else {
                Log.e(TAG, "Failed to get room reviews: ${response.errorBody()?.string()}")
                return@withContext null
            }
            */
        } catch (e: Exception) {
            Log.e(TAG, "Error getting room reviews", e)
            return@withContext null
        }
    }
} 