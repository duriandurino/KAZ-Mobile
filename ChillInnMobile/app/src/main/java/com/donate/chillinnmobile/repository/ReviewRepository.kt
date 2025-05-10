package com.donate.chillinnmobile.repository

import android.util.Log
import com.donate.chillinnmobile.model.Review
import com.donate.chillinnmobile.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repository for handling review-related data operations
 */
class ReviewRepository {
    private val apiService = ApiClient.apiService
    private val TAG = "ReviewRepository"
    
    /**
     * Submit a new review for a room
     */
    suspend fun submitReview(
        roomId: String,
        bookingId: String,
        rating: Float,
        comment: String? = null,
        images: List<String>? = null
    ): Review? = withContext(Dispatchers.IO) {
        try {
            val reviewRequest = mutableMapOf<String, Any>(
                "roomId" to roomId,
                "bookingId" to bookingId,
                "rating" to rating
            )
            
            comment?.let { reviewRequest["comment"] = it }
            images?.let { reviewRequest["images"] = it }
            
            val response = apiService.submitReview(reviewRequest)
            
            if (response.isSuccessful && response.body() != null) {
                return@withContext response.body()
            } else {
                Log.e(TAG, "Failed to submit review: ${response.errorBody()?.string()}")
                return@withContext null
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error submitting review", e)
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "Error submitting review", e)
            return@withContext null
        }
    }
    
    /**
     * Get all reviews for a specific room
     */
    suspend fun getRoomReviews(roomId: String): List<Review>? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getRoomReviews(roomId)
            
            if (response.isSuccessful && response.body() != null) {
                return@withContext response.body()
            } else {
                Log.e(TAG, "Failed to get room reviews: ${response.errorBody()?.string()}")
                return@withContext null
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error getting room reviews", e)
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting room reviews", e)
            return@withContext null
        }
    }
} 