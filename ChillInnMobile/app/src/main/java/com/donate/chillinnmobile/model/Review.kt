package com.donate.chillinnmobile.model

import java.util.Date

/**
 * Review class representing guest reviews for rooms
 */
data class Review(
    val id: String,
    val guestId: String,
    val guestName: String, // Added guest name for display purposes
    val guestProfileImage: String? = null, // Added profile image URL
    val roomId: String,
    val bookingId: String,
    val rating: Float, // Rating from 1.0 to 5.0
    val comment: String? = null,
    val images: List<String>? = null, // URLs of images attached to review
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isVerified: Boolean = false,
    val ownerResponse: OwnerResponse? = null, // Added owner/admin response
    val helpfulCount: Int = 0, // Number of users who found this review helpful
    val notHelpfulCount: Int = 0, // Number of users who found this review unhelpful
    val status: ReviewStatus = ReviewStatus.PENDING, // Review moderation status
    val tags: List<String>? = null // Tags categorizing the review (e.g., "clean", "comfortable")
)

/**
 * Owner/admin response to a review
 */
data class OwnerResponse(
    val adminId: String,
    val adminName: String,
    val comment: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Enum representing review moderation status
 */
enum class ReviewStatus {
    PENDING,
    APPROVED,
    REJECTED
} 