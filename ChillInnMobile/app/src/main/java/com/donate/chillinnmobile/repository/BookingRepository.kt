package com.donate.chillinnmobile.repository

import android.util.Log
import com.donate.chillinnmobile.model.Booking
import com.donate.chillinnmobile.model.Payment
import com.donate.chillinnmobile.model.PaymentMethod
import com.donate.chillinnmobile.model.PaymentStatus
import com.donate.chillinnmobile.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Repository for handling booking-related data operations
 */
class BookingRepository {
    private val apiService = ApiClient.apiService
    private val TAG = "BookingRepository"
    
    // Mock local data
    private val mockBookings = ConcurrentHashMap<String, Booking>()
    private val mockPayments = ConcurrentHashMap<String, Payment>()
    
    /**
     * Create a new booking
     */
    suspend fun createBooking(
        roomId: String,
        checkInDate: Date,
        checkOutDate: Date,
        numberOfGuests: Int,
        specialRequests: String? = null
    ): Booking? = withContext(Dispatchers.IO) {
        try {
            // Try to use API if available
            val bookingRequest = mapOf(
                "roomId" to roomId,
                "checkInDate" to checkInDate.toString(),
                "checkOutDate" to checkOutDate.toString(),
                "numberOfGuests" to numberOfGuests,
                "specialRequests" to (specialRequests ?: "")
            )
            
            try {
                val response = apiService.createBooking(bookingRequest)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext response.body()
                }
            } catch (e: Exception) {
                // Backend not available, use mock implementation
                Log.d(TAG, "Backend not available, using mock implementation")
            }
            
            // Create mock booking when backend is not available
            val bookingId = "BK-" + UUID.randomUUID().toString().substring(0, 8)
            
            // Calculate mock price based on days
            val daysDiff = ((checkOutDate.time - checkInDate.time) / (1000 * 60 * 60 * 24)).toInt()
            val dailyRate = (80..300).random().toDouble()
            val totalPrice = dailyRate * daysDiff
            
            val mockBooking = Booking(
                id = bookingId,
                userId = "user-mock",
                roomId = roomId,
                checkInDate = checkInDate,
                checkOutDate = checkOutDate,
                numberOfGuests = numberOfGuests,
                specialRequests = specialRequests,
                status = "PENDING",
                totalPrice = totalPrice,
                createdAt = Date()
            )
            
            // Store in mock database
            mockBookings[bookingId] = mockBooking
            
            return@withContext mockBooking
        } catch (e: Exception) {
            Log.e(TAG, "Error creating booking", e)
            return@withContext null
        }
    }
    
    /**
     * Get all bookings for the current user
     */
    suspend fun getUserBookings(): List<Booking>? = withContext(Dispatchers.IO) {
        try {
            // Try to use API if available
            try {
                val response = apiService.getUserBookings()
                if (response.isSuccessful && response.body() != null) {
                    return@withContext response.body()
                }
            } catch (e: Exception) {
                // Backend not available, use mock implementation
                Log.d(TAG, "Backend not available, using mock implementation")
            }
            
            // Return mock bookings when backend is not available
            return@withContext mockBookings.values.toList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user bookings", e)
            return@withContext null
        }
    }
    
    /**
     * Get details for a specific booking
     */
    suspend fun getBookingDetails(bookingId: String): Booking? = withContext(Dispatchers.IO) {
        try {
            // Try to use API if available
            try {
                val response = apiService.getBookingDetails(bookingId)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext response.body()
                }
            } catch (e: Exception) {
                // Backend not available, use mock implementation
                Log.d(TAG, "Backend not available, using mock implementation")
            }
            
            // Return mock booking when backend is not available
            if (mockBookings.containsKey(bookingId)) {
                return@withContext mockBookings[bookingId]
            }
            
            // If not in mock database, create a random one for demo purposes
            val mockBooking = createMockBooking(bookingId)
            mockBookings[bookingId] = mockBooking
            
            return@withContext mockBooking
        } catch (e: Exception) {
            Log.e(TAG, "Error getting booking details", e)
            return@withContext null
        }
    }
    
    /**
     * Create a mock booking for testing
     */
    private fun createMockBooking(bookingId: String): Booking {
        val currentDate = Date()
        val checkInDate = Date(currentDate.time + (1000 * 60 * 60 * 24 * 3)) // 3 days from now
        val checkOutDate = Date(currentDate.time + (1000 * 60 * 60 * 24 * 5)) // 5 days from now
        
        return Booking(
            id = bookingId,
            userId = "user-mock",
            roomId = "room-" + UUID.randomUUID().toString().substring(0, 8),
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            numberOfGuests = (1..4).random(),
            specialRequests = "No special requests",
            status = "PENDING",
            totalPrice = (150..500).random().toDouble(),
            createdAt = Date()
        )
    }
    
    /**
     * Update an existing booking
     */
    suspend fun updateBooking(
        bookingId: String,
        checkInDate: Date? = null,
        checkOutDate: Date? = null,
        numberOfGuests: Int? = null,
        specialRequests: String? = null
    ): Booking? = withContext(Dispatchers.IO) {
        try {
            // Try to use API if available
            val updateRequest = mutableMapOf<String, Any>()
            
            checkInDate?.let { updateRequest["checkInDate"] = it.toString() }
            checkOutDate?.let { updateRequest["checkOutDate"] = it.toString() }
            numberOfGuests?.let { updateRequest["numberOfGuests"] = it }
            specialRequests?.let { updateRequest["specialRequests"] = it }
            
            try {
                val response = apiService.updateBooking(bookingId, updateRequest)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext response.body()
                }
            } catch (e: Exception) {
                // Backend not available, use mock implementation
                Log.d(TAG, "Backend not available, using mock implementation")
            }
            
            // Update mock booking when backend is not available
            val existingBooking = mockBookings[bookingId] ?: return@withContext null
            
            val updatedBooking = existingBooking.copy(
                checkInDate = checkInDate ?: existingBooking.checkInDate,
                checkOutDate = checkOutDate ?: existingBooking.checkOutDate,
                numberOfGuests = numberOfGuests ?: existingBooking.numberOfGuests,
                specialRequests = specialRequests ?: existingBooking.specialRequests
            )
            
            mockBookings[bookingId] = updatedBooking
            
            return@withContext updatedBooking
        } catch (e: Exception) {
            Log.e(TAG, "Error updating booking", e)
            return@withContext null
        }
    }
    
    /**
     * Cancel a booking
     */
    suspend fun cancelBooking(bookingId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Try to use API if available
            try {
                val response = apiService.cancelBooking(bookingId)
                if (response.isSuccessful) {
                    return@withContext true
                }
            } catch (e: Exception) {
                // Backend not available, use mock implementation
                Log.d(TAG, "Backend not available, using mock implementation")
            }
            
            // Cancel mock booking when backend is not available
            val existingBooking = mockBookings[bookingId] ?: return@withContext false
            
            val cancelledBooking = existingBooking.copy(status = "CANCELLED")
            mockBookings[bookingId] = cancelledBooking
            
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Error canceling booking", e)
            return@withContext false
        }
    }
    
    /**
     * Process payment for a booking
     */
    suspend fun processPayment(
        bookingId: String,
        amount: Double,
        paymentMethod: String,
        paymentDetails: Map<String, Any>
    ): Payment? = withContext(Dispatchers.IO) {
        try {
            // Try to use API if available
            val paymentRequest = mapOf(
                "bookingId" to bookingId,
                "amount" to amount,
                "paymentMethod" to paymentMethod,
                "paymentDetails" to paymentDetails
            )
            
            try {
                val response = apiService.processPayment(paymentRequest)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext response.body()
                }
            } catch (e: Exception) {
                // Backend not available, use mock implementation
                Log.d(TAG, "Backend not available, using mock implementation")
            }
            
            // Add artificial delay to simulate payment processing
            delay(1500)
            
            // Extract payment ID and transaction ID from details (provided by PaymentFragment)
            val paymentId = paymentDetails["paymentId"] as? String ?: "PM-" + UUID.randomUUID().toString().substring(0, 8)
            val transactionId = paymentDetails["transactionId"] as? String ?: "TXN" + UUID.randomUUID().toString().substring(0, 8)
            
            // Create mock payment when backend is not available
            val mockPayment = Payment(
                id = paymentId,
                bookingId = bookingId,
                amount = amount,
                paymentMethod = PaymentMethod.valueOf(paymentMethod),
                status = PaymentStatus.COMPLETED,
                transactionId = transactionId,
                timestamp = Date()
            )
            
            // Update booking status
            val existingBooking = mockBookings[bookingId]
            if (existingBooking != null) {
                mockBookings[bookingId] = existingBooking.copy(status = "CONFIRMED")
            }
            
            // Store payment in mock database
            mockPayments[paymentId] = mockPayment
            
            return@withContext mockPayment
        } catch (e: Exception) {
            Log.e(TAG, "Error processing payment", e)
            return@withContext null
        }
    }
    
    /**
     * Get payment details
     */
    suspend fun getPaymentDetails(paymentId: String): Payment? = withContext(Dispatchers.IO) {
        try {
            // Try to use API if available
            try {
                val response = apiService.getPaymentDetails(paymentId)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext response.body()
                }
            } catch (e: Exception) {
                // Backend not available, use mock implementation
                Log.d(TAG, "Backend not available, using mock implementation")
            }
            
            // Return mock payment if available
            if (mockPayments.containsKey(paymentId)) {
                return@withContext mockPayments[paymentId]
            }
            
            // If not in mock database, create a random one for demo purposes
            val mockPayment = createMockPayment(paymentId)
            mockPayments[paymentId] = mockPayment
            
            return@withContext mockPayment
        } catch (e: Exception) {
            Log.e(TAG, "Error getting payment details", e)
            return@withContext null
        }
    }
    
    /**
     * Create a mock payment for testing
     */
    private fun createMockPayment(paymentId: String): Payment {
        val bookingId = "BK-" + UUID.randomUUID().toString().substring(0, 8)
        val transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8)
        
        return Payment(
            id = paymentId,
            bookingId = bookingId,
            amount = (100..500).random().toDouble(),
            paymentMethod = PaymentMethod.values().random(),
            status = PaymentStatus.COMPLETED,
            transactionId = transactionId,
            timestamp = Date()
        )
    }
} 