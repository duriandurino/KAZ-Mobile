package com.donate.chillinnmobile.utils

/**
 * Constants used throughout the application
 */
object Constants {
    // API related
    const val BASE_URL = "https://api.chillinn.example.com/"
    
    // Navigation
    const val ARG_ROOM_ID = "roomId"
    const val ARG_BOOKING_ID = "bookingId"
    
    // Date formats
    const val DATE_FORMAT_DISPLAY = "MMM dd, yyyy"
    const val DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    
    // Booking related
    const val MIN_BOOKING_DAYS = 1
    const val MAX_BOOKING_DAYS = 30
    const val MAX_GUESTS_PER_ROOM = 4
    
    // Payment related
    const val PAYMENT_TIMEOUT_SECONDS = 300 // 5 minutes
    
    // Image upload
    const val MAX_IMAGE_SIZE_MB = 5
    const val MAX_REVIEW_IMAGES = 3
    
    // Error messages
    const val ERROR_NETWORK = "Network error. Please check your connection."
    const val ERROR_SERVER = "Server error. Please try again later."
    const val ERROR_UNKNOWN = "An unknown error occurred."
    const val ERROR_AUTHENTICATION = "Authentication failed. Please log in again."
    
    // Permissions
    const val PERMISSION_REQUEST_CAMERA = 100
    const val PERMISSION_REQUEST_STORAGE = 101
} 