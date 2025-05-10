package com.donate.chillinnmobile.network

import com.donate.chillinnmobile.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface defining API endpoints
 */
interface ApiService {

    // Authentication endpoints
    @POST("auth/login")
    suspend fun login(@Body loginRequest: Map<String, String>): Response<Map<String, Any>>
    
    @POST("auth/register")
    suspend fun register(@Body registerRequest: Map<String, Any>): Response<Map<String, Any>>
    
    @POST("auth/logout")
    suspend fun logout(): Response<Map<String, Any>>
    
    // User endpoints
    @GET("users/profile")
    suspend fun getUserProfile(): Response<User>
    
    @PUT("users/profile")
    suspend fun updateUserProfile(@Body userProfile: Map<String, Any>): Response<User>
    
    // Room endpoints
    @GET("rooms")
    suspend fun getRooms(@QueryMap filters: Map<String, String>? = null): Response<List<Room>>
    
    @GET("rooms/search")
    suspend fun searchRooms(@Query("query") query: String): Response<List<Room>>
    
    @GET("rooms/{roomId}")
    suspend fun getRoomDetails(@Path("roomId") roomId: String): Response<Room>
    
    // Booking endpoints
    @POST("bookings")
    suspend fun createBooking(@Body bookingRequest: Map<String, Any>): Response<Booking>
    
    @GET("bookings")
    suspend fun getUserBookings(): Response<List<Booking>>
    
    @GET("bookings/{bookingId}")
    suspend fun getBookingDetails(@Path("bookingId") bookingId: String): Response<Booking>
    
    @PUT("bookings/{bookingId}")
    suspend fun updateBooking(
        @Path("bookingId") bookingId: String, 
        @Body updateRequest: Map<String, Any>
    ): Response<Booking>
    
    @DELETE("bookings/{bookingId}")
    suspend fun cancelBooking(@Path("bookingId") bookingId: String): Response<Map<String, Any>>
    
    // Payment endpoints
    @POST("payments")
    suspend fun processPayment(@Body paymentRequest: Map<String, Any>): Response<Payment>
    
    @GET("payments/{paymentId}")
    suspend fun getPaymentDetails(@Path("paymentId") paymentId: String): Response<Payment>
    
    // Review endpoints
    @POST("reviews")
    suspend fun submitReview(@Body reviewRequest: Map<String, Any>): Response<Review>
    
    @GET("rooms/{roomId}/reviews")
    suspend fun getRoomReviews(@Path("roomId") roomId: String): Response<List<Review>>
    
    // Admin-specific endpoints
    @GET("admin/dashboard")
    suspend fun getAdminDashboardData(): Response<Map<String, Any>>
    
    @POST("admin/rooms")
    suspend fun createRoom(@Body roomRequest: Map<String, Any>): Response<Room>
    
    @PUT("admin/rooms/{roomId}")
    suspend fun updateRoom(
        @Path("roomId") roomId: String, 
        @Body updateRequest: Map<String, Any>
    ): Response<Room>
    
    @DELETE("admin/rooms/{roomId}")
    suspend fun deleteRoom(@Path("roomId") roomId: String): Response<Map<String, Any>>
} 