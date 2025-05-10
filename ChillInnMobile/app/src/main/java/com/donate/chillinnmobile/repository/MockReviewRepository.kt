package com.donate.chillinnmobile.repository

import android.util.Log
import com.donate.chillinnmobile.model.OwnerResponse
import com.donate.chillinnmobile.model.Review
import com.donate.chillinnmobile.model.ReviewStatus
import com.donate.chillinnmobile.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * Mock repository for handling review-related data operations during development
 */
class MockReviewRepository {
    private val TAG = "MockReviewRepository"
    
    private val reviews = mutableListOf<Review>()
    private val pendingReviews = mutableListOf<Review>()
    
    // Sample user names for mock reviews
    private val sampleUserNames = listOf(
        "John Smith", "Emma Johnson", "Michael Brown", "Olivia Davis", 
        "William Wilson", "Sophia Martinez", "James Anderson", "Ava Taylor",
        "Benjamin Thomas", "Isabella White", "Lucas Harris", "Mia Martin",
        "Henry Thompson", "Charlotte Garcia", "Alexander Clark", "Amelia Lewis"
    )
    
    // Sample comments for mock reviews
    private val sampleComments = listOf(
        "The room was extremely comfortable and clean. The staff was friendly and helpful.",
        "Great stay! The bed was comfortable and the room was spacious.",
        "Excellent location with fantastic amenities. Will definitely come back.",
        "The room was clean but smaller than expected. Good value for the price.",
        "Amazing view from the window! The bathroom was spotless and modern.",
        "The room service was fast and the food was delicious. Highly recommend.",
        "Quiet room with comfortable beds. Perfect for a business trip.",
        "Very disappointed with the cleanliness of the room. Not what I expected.",
        "Superb experience! The staff went above and beyond to make our stay special.",
        "Good location, but the room was a bit outdated. Could use some renovation.",
        "Perfect stay for our anniversary. The room was beautiful and romantic.",
        "The room was okay, but the noise from the street was quite disturbing at night.",
        "Excellent facilities and very friendly staff. The room was immaculate.",
        "Beautiful decor and very comfortable bed. Would stay here again.",
        "The room was fine, but the air conditioning wasn't working properly.",
        "One of the best hotel experiences I've had. The room exceeded my expectations."
    )
    
    init {
        // Generate some mock reviews
        generateMockReviews()
    }
    
    /**
     * Generate mock reviews for testing
     */
    private fun generateMockReviews() {
        val roomIds = listOf("room_1", "room_2", "room_3", "room_4", "room_5")
        val guestNames = listOf("John Smith", "Emma Johnson", "Robert Williams", "Lisa Brown", "Michael Davis")
        val comments = listOf(
            "Great room with an amazing view! The staff was friendly and accommodating.",
            "The room was clean and comfortable. Highly recommend for business travelers.",
            "Decent stay but the Wi-Fi connection was unstable during my visit.",
            "Excellent experience overall. The room service was prompt and the food was delicious.",
            "The location is perfect, very close to main attractions. Room was a bit small though.",
            "Very comfortable bed and pillows. Slept like a baby!",
            "The bathroom was spotless and the shower pressure was fantastic.",
            "Nice room but could hear noise from the hallway at night.",
            "Loved the modern decor and the smart TV features.",
            "The room temperature control didn't work properly but staff fixed it quickly."
        )
        val profileImages = listOf(
            "https://randomuser.me/api/portraits/men/1.jpg",
            "https://randomuser.me/api/portraits/women/2.jpg",
            "https://randomuser.me/api/portraits/men/3.jpg",
            "https://randomuser.me/api/portraits/women/4.jpg",
            "https://randomuser.me/api/portraits/men/5.jpg"
        )
        val reviewTags = listOf(
            listOf("clean", "comfortable", "friendly staff"),
            listOf("great location", "value for money"),
            listOf("quiet", "spacious"),
            listOf("modern", "tech-friendly"),
            listOf("great view", "luxury")
        )
        
        // Create approved reviews
        for (i in 1..20) {
            val roomIndex = Random.nextInt(roomIds.size)
            val guestIndex = Random.nextInt(guestNames.size)
            val commentIndex = Random.nextInt(comments.size)
            val rating = Random.nextDouble(3.0, 5.0).toFloat()
            val hasOwnerResponse = Random.nextBoolean()
            
            val ownerResponse = if (hasOwnerResponse) {
                OwnerResponse(
                    adminId = "admin_1",
                    adminName = "Hotel Manager",
                    comment = "Thank you for your feedback! We're glad you enjoyed your stay with us.",
                    createdAt = Date(System.currentTimeMillis() - Random.nextLong(86400000 * 2)), // Within last 2 days
                    updatedAt = Date(System.currentTimeMillis() - Random.nextLong(86400000)) // Within last day
                )
            } else null
            
            val createdDate = Date(System.currentTimeMillis() - Random.nextLong(86400000 * 30)) // Within last 30 days
            
            reviews.add(
                Review(
                    id = "review_$i",
                    guestId = "guest_${guestIndex + 1}",
                    guestName = guestNames[guestIndex],
                    guestProfileImage = profileImages[guestIndex],
                    roomId = roomIds[roomIndex],
                    bookingId = "booking_${i}",
                    rating = rating,
                    comment = comments[commentIndex],
                    images = if (Random.nextBoolean()) listOf(
                        "https://placekitten.com/800/600?image=${Random.nextInt(16)}",
                        "https://placekitten.com/800/600?image=${Random.nextInt(16)}"
                    ) else null,
                    createdAt = createdDate,
                    updatedAt = createdDate,
                    isVerified = true,
                    ownerResponse = ownerResponse,
                    helpfulCount = Random.nextInt(50),
                    notHelpfulCount = Random.nextInt(10),
                    status = ReviewStatus.APPROVED,
                    tags = reviewTags[roomIndex % reviewTags.size]
                )
            )
        }
        
        // Create pending reviews for admin approval
        for (i in 1..5) {
            val roomIndex = Random.nextInt(roomIds.size)
            val guestIndex = Random.nextInt(guestNames.size)
            val commentIndex = Random.nextInt(comments.size)
            val rating = Random.nextDouble(1.0, 5.0).toFloat()
            
            pendingReviews.add(
                Review(
                    id = "pending_review_$i",
                    guestId = "guest_${guestIndex + 1}",
                    guestName = guestNames[guestIndex],
                    guestProfileImage = profileImages[guestIndex],
                    roomId = roomIds[roomIndex],
                    bookingId = "booking_${20 + i}",
                    rating = rating,
                    comment = comments[commentIndex],
                    images = if (Random.nextBoolean()) listOf(
                        "https://placekitten.com/800/600?image=${Random.nextInt(16)}"
                    ) else null,
                    createdAt = Date(System.currentTimeMillis() - Random.nextLong(86400000)), // Within last day
                    updatedAt = Date(System.currentTimeMillis() - Random.nextLong(86400000)), // Within last day
                    isVerified = false,
                    status = ReviewStatus.PENDING
                )
            )
        }
    }
    
    /**
     * Get all reviews for a specific room
     */
    suspend fun getRoomReviews(roomId: String): List<Review> {
        // Simulate network delay
        Thread.sleep(500)
        
        return reviews.filter { it.roomId == roomId && it.status == ReviewStatus.APPROVED }
    }
    
    /**
     * Get all reviews by a specific user
     */
    suspend fun getUserReviews(userId: String): List<Review> {
        // Simulate network delay
        Thread.sleep(500)
        
        return reviews.filter { it.guestId == userId }
    }
    
    /**
     * Submit a new review for a room
     */
    suspend fun submitReview(
        roomId: String,
        rating: Float,
        comment: String? = null,
        images: List<String>? = null
    ): Review {
        // Simulate network delay
        Thread.sleep(1000)
        
        val userId = SessionManager.getUserId() ?: "guest_1" // Default for testing
        val userName = SessionManager.getUserName() ?: "Test User" // Default for testing
        
        val newReview = Review(
            id = UUID.randomUUID().toString(),
            guestId = userId,
            guestName = userName,
            roomId = roomId,
            bookingId = "booking_${reviews.size + pendingReviews.size + 1}",
            rating = rating,
            comment = comment,
            images = images,
            createdAt = Date(),
            updatedAt = Date(),
            isVerified = false,
            status = ReviewStatus.PENDING
        )
        
        pendingReviews.add(newReview)
        return newReview
    }
    
    /**
     * Update an existing review
     */
    suspend fun updateReview(
        reviewId: String,
        rating: Float,
        comment: String? = null,
        images: List<String>? = null
    ): Review? {
        // Simulate network delay
        Thread.sleep(800)
        
        val existingReviewIndex = reviews.indexOfFirst { it.id == reviewId }
        
        if (existingReviewIndex != -1) {
            val existingReview = reviews[existingReviewIndex]
            val updatedReview = existingReview.copy(
                rating = rating,
                comment = comment,
                images = images,
                updatedAt = Date(),
                status = ReviewStatus.PENDING // Reset to pending for re-approval
            )
            
            reviews[existingReviewIndex] = updatedReview
            return updatedReview
        }
        
        return null
    }
    
    /**
     * Delete a review
     */
    suspend fun deleteReview(reviewId: String): Boolean {
        // Simulate network delay
        Thread.sleep(500)
        
        val existingReviewIndex = reviews.indexOfFirst { it.id == reviewId }
        
        if (existingReviewIndex != -1) {
            reviews.removeAt(existingReviewIndex)
            return true
        }
        
        val pendingReviewIndex = pendingReviews.indexOfFirst { it.id == reviewId }
        
        if (pendingReviewIndex != -1) {
            pendingReviews.removeAt(pendingReviewIndex)
            return true
        }
        
        return false
    }
    
    /**
     * Get all pending reviews for admin approval
     */
    suspend fun getPendingReviews(): List<Review> {
        // Simulate network delay
        Thread.sleep(500)
        
        return pendingReviews
    }
    
    /**
     * Approve or reject a review
     */
    suspend fun moderateReview(
        reviewId: String,
        isApproved: Boolean,
        adminComment: String? = null
    ): Boolean {
        // Simulate network delay
        Thread.sleep(800)
        
        val pendingReviewIndex = pendingReviews.indexOfFirst { it.id == reviewId }
        
        if (pendingReviewIndex != -1) {
            val pendingReview = pendingReviews[pendingReviewIndex]
            
            val ownerResponse = if (adminComment != null) {
                OwnerResponse(
                    adminId = SessionManager.getUserId() ?: "admin_1",
                    adminName = SessionManager.getUserName() ?: "Hotel Manager",
                    comment = adminComment,
                    createdAt = Date(),
                    updatedAt = Date()
                )
            } else null
            
            val updatedReview = pendingReview.copy(
                status = if (isApproved) ReviewStatus.APPROVED else ReviewStatus.REJECTED,
                ownerResponse = ownerResponse,
                updatedAt = Date()
            )
            
            pendingReviews.removeAt(pendingReviewIndex)
            
            if (isApproved) {
                reviews.add(updatedReview)
            }
            
            return true
        }
        
        return false
    }
    
    /**
     * Mark review as helpful or not helpful
     */
    suspend fun markReviewHelpfulness(
        reviewId: String,
        isHelpful: Boolean
    ): Review? {
        // Simulate network delay
        Thread.sleep(300)
        
        val reviewIndex = reviews.indexOfFirst { it.id == reviewId }
        
        if (reviewIndex != -1) {
            val review = reviews[reviewIndex]
            val updatedReview = if (isHelpful) {
                review.copy(helpfulCount = review.helpfulCount + 1)
            } else {
                review.copy(notHelpfulCount = review.notHelpfulCount + 1)
            }
            
            reviews[reviewIndex] = updatedReview
            return updatedReview
        }
        
        return null
    }
    
    /**
     * Calculate room rating statistics
     */
    suspend fun calculateRoomRating(roomId: String): Pair<Float, Int> {
        // Get only approved reviews
        val roomReviews = reviews.filter { it.roomId == roomId && it.status == ReviewStatus.APPROVED }
        
        if (roomReviews.isEmpty()) {
            return Pair(0.0f, 0)
        }
        
        val totalRating = roomReviews.sumOf { it.rating.toDouble() }
        val averageRating = (totalRating / roomReviews.size).toFloat()
        
        return Pair(averageRating, roomReviews.size)
    }
    
    /**
     * Add admin response to a review
     */
    suspend fun addOwnerResponse(
        reviewId: String,
        comment: String
    ): Review? {
        // Simulate network delay
        Thread.sleep(500)
        
        val reviewIndex = reviews.indexOfFirst { it.id == reviewId }
        
        if (reviewIndex != -1) {
            val review = reviews[reviewIndex]
            
            val ownerResponse = OwnerResponse(
                adminId = SessionManager.getUserId() ?: "admin_1",
                adminName = SessionManager.getUserName() ?: "Hotel Manager",
                comment = comment,
                createdAt = Date(),
                updatedAt = Date()
            )
            
            val updatedReview = review.copy(
                ownerResponse = ownerResponse,
                updatedAt = Date()
            )
            
            reviews[reviewIndex] = updatedReview
            return updatedReview
        }
        
        return null
    }
} 