package com.donate.chillinnmobile.repository

import android.util.Log
import com.donate.chillinnmobile.model.Booking
import com.donate.chillinnmobile.model.Image
import com.donate.chillinnmobile.model.Room
import com.donate.chillinnmobile.model.User
import com.donate.chillinnmobile.model.UserType
import com.donate.chillinnmobile.network.ApiClient
import com.donate.chillinnmobile.utils.SessionManager
import com.donate.chillinnmobile.utils.createDemoRoomImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.util.UUID

/**
 * Repository for user-related data operations
 */
class UserRepository {
    private val apiService = ApiClient.apiService
    private val TAG = "UserRepository"
    
    // In a real app, these would be stored in a database or fetched from a server
    private var currentUser = User(
        id = "user123",
        name = "John Doe",
        email = "john.doe@example.com",
        phone = "+1 234 567 890",
        address = "123 Main St, New York, NY",
        profileImageUrl = null,
        darkThemeEnabled = false,
        notificationsEnabled = true
    )
    
    private val favoriteRooms = mutableListOf<Room>()
    private val bookingHistory = mutableListOf<Booking>()
    
    init {
        // Generate mock data for demo
        generateMockData()
    }
    
    /**
     * Login user with email and password
     * @return Success status with error message if failed
     */
    suspend fun login(email: String, password: String): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        try {
            val loginRequest = mapOf(
                "email" to email,
                "password" to password
            )
            
            val response = apiService.login(loginRequest)
            
            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                
                // Extract data from response
                val token = responseBody["token"] as? String ?: ""
                val userId = responseBody["userId"] as? String ?: ""
                val userTypeStr = responseBody["userType"] as? String ?: ""
                val userType = try {
                    UserType.valueOf(userTypeStr.uppercase())
                } catch (e: Exception) {
                    UserType.GUEST
                }
                
                // Save session data
                SessionManager.saveAuthData(token, userId, email, userType)
                
                return@withContext Pair(true, null)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Login failed"
                Log.e(TAG, "Login error: $errorMessage")
                return@withContext Pair(false, errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during login", e)
            return@withContext Pair(false, "Network error. Please check your connection.")
        } catch (e: Exception) {
            Log.e(TAG, "Error during login", e)
            return@withContext Pair(false, "An unexpected error occurred.")
        }
    }
    
    /**
     * Register a new user
     * @return Success status with error message if failed
     */
    suspend fun register(
        email: String, 
        password: String, 
        fullName: String, 
        phoneNumber: String
    ): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        try {
            val registerRequest = mapOf(
                "email" to email,
                "password" to password,
                "fullName" to fullName,
                "phoneNumber" to phoneNumber,
                "userType" to UserType.GUEST.name
            )
            
            val response = apiService.register(registerRequest)
            
            if (response.isSuccessful && response.body() != null) {
                return@withContext Pair(true, null)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Registration failed"
                Log.e(TAG, "Registration error: $errorMessage")
                return@withContext Pair(false, errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during registration", e)
            return@withContext Pair(false, "Network error. Please check your connection.")
        } catch (e: Exception) {
            Log.e(TAG, "Error during registration", e)
            return@withContext Pair(false, "An unexpected error occurred.")
        }
    }
    
    /**
     * Get user profile
     * @return User profile or null if failed
     */
    suspend fun getUserProfile(): User? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserProfile()
            
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                SessionManager.saveUserProfile(user)
                return@withContext user
            } else {
                Log.e(TAG, "Failed to get user profile: ${response.errorBody()?.string()}")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user profile", e)
            return@withContext null
        }
    }
    
    /**
     * Update user profile
     * @return Updated user profile or null if failed
     */
    suspend fun updateUserProfile(
        fullName: String? = null,
        phoneNumber: String? = null,
        profileImage: String? = null
    ): User? = withContext(Dispatchers.IO) {
        try {
            val updateRequest = mutableMapOf<String, Any>()
            
            fullName?.let { updateRequest["fullName"] = it }
            phoneNumber?.let { updateRequest["phoneNumber"] = it }
            profileImage?.let { updateRequest["profileImage"] = it }
            
            val response = apiService.updateUserProfile(updateRequest)
            
            if (response.isSuccessful && response.body() != null) {
                val updatedUser = response.body()!!
                SessionManager.saveUserProfile(updatedUser)
                return@withContext updatedUser
            } else {
                Log.e(TAG, "Failed to update user profile: ${response.errorBody()?.string()}")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile", e)
            return@withContext null
        }
    }
    
    /**
     * Logout user
     * @return Success status
     */
    suspend fun logout(): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiService.logout()
            SessionManager.clearSession()
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout", e)
            SessionManager.clearSession() // Clear session anyway
            return@withContext true
        }
    }
    
    /**
     * Get the current user
     */
    suspend fun getCurrentUser(): User = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(500)
        return@withContext currentUser
    }
    
    /**
     * Get user's favorite rooms
     */
    suspend fun getUserFavorites(): List<Room> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(500)
        return@withContext favoriteRooms
    }
    
    /**
     * Get user's booking history
     */
    suspend fun getUserBookingHistory(): List<Booking> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(500)
        return@withContext bookingHistory
    }
    
    /**
     * Add a room to favorites
     */
    suspend fun addToFavorites(room: Room) = withContext(Dispatchers.IO) {
        delay(300)
        if (!favoriteRooms.any { it.id == room.id }) {
            favoriteRooms.add(room)
        }
    }
    
    /**
     * Remove a room from favorites
     */
    suspend fun removeFromFavorites(roomId: String) = withContext(Dispatchers.IO) {
        delay(300)
        favoriteRooms.removeIf { it.id == roomId }
    }
    
    /**
     * Update user's profile image
     */
    suspend fun updateProfileImage(imageUrl: String) = withContext(Dispatchers.IO) {
        delay(300)
        currentUser = currentUser.copy(profileImageUrl = imageUrl)
    }
    
    /**
     * Update user's theme preference
     */
    suspend fun updateThemePreference(darkThemeEnabled: Boolean) = withContext(Dispatchers.IO) {
        delay(300)
        currentUser = currentUser.copy(darkThemeEnabled = darkThemeEnabled)
    }
    
    /**
     * Update user's notification preference
     */
    suspend fun updateNotificationPreference(notificationsEnabled: Boolean) = withContext(Dispatchers.IO) {
        delay(300)
        currentUser = currentUser.copy(notificationsEnabled = notificationsEnabled)
    }
    
    /**
     * Generate mock data for demonstration
     */
    private fun generateMockData() {
        // Generate favorite rooms
        val roomTypes = listOf("Deluxe Room", "Suite", "Executive Room", "Standard Room", "Family Room")
        val amenities = listOf("Wi-Fi", "Air Conditioning", "Mini Bar", "Room Service", "TV", "Coffee Maker")
        
        for (i in 1..5) {
            val roomId = "room-${UUID.randomUUID()}"
            val roomType = roomTypes.random()
            val price = (100..300).random().toDouble()
            
            val room = Room(
                id = roomId,
                name = "Room ${100 + i}",
                roomNumber = (100 + i).toString(),
                roomType = roomType,
                floor = (1..5).random(),
                pricePerNight = price,
                description = "A comfortable $roomType with beautiful views.",
                capacity = (1..4).random(),
                status = "AVAILABLE",
                amenities = amenities.shuffled().take((2..5).random()),
                images = createDemoRoomImages(roomId, (1..3).random()),
                rating = (3.0..5.0).random().toFloat()
            )
            
            // Add some rooms to favorites
            if (i <= 3) {
                favoriteRooms.add(room)
            }
        }
        
        // Generate booking history
        val statuses = listOf("CONFIRMED", "COMPLETED", "CANCELLED", "PENDING")
        val calendar = Calendar.getInstance()
        
        for (i in 1..5) {
            val roomId = "room-${UUID.randomUUID()}"
            val roomType = roomTypes.random()
            
            // Set check-in date (past date for history)
            calendar.time = Date()
            calendar.add(Calendar.MONTH, -i)
            val checkInDate = calendar.time
            
            // Set check-out date
            calendar.add(Calendar.DAY_OF_MONTH, (2..7).random())
            val checkOutDate = calendar.time
            
            val booking = Booking(
                id = "booking-${UUID.randomUUID()}",
                userId = currentUser.id,
                roomId = roomId,
                roomType = roomType,
                checkInDate = checkInDate,
                checkOutDate = checkOutDate,
                guestCount = (1..3).random(),
                totalPrice = (100..500).random().toDouble(),
                status = statuses.random(),
                specialRequests = if (i % 2 == 0) "Need extra pillows" else null
            )
            
            bookingHistory.add(booking)
        }
        
        // Sort bookings by date (newest first)
        bookingHistory.sortByDescending { it.checkInDate }
    }
} 