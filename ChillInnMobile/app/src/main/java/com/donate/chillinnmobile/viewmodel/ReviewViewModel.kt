package com.donate.chillinnmobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donate.chillinnmobile.model.Review
import com.donate.chillinnmobile.model.ReviewStatus
import com.donate.chillinnmobile.repository.MockReviewRepository
import com.donate.chillinnmobile.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * ViewModel for review-related operations
 */
class ReviewViewModel : ViewModel() {
    
    private val reviewRepository = MockReviewRepository() // Using mock repository
    
    // LiveData for room reviews
    private val _roomReviews = MutableLiveData<List<Review>?>()
    val roomReviews: LiveData<List<Review>?> = _roomReviews
    
    // LiveData for filtered room reviews
    private val _filteredReviews = MutableLiveData<List<Review>?>()
    val filteredReviews: LiveData<List<Review>?> = _filteredReviews
    
    // LiveData for room rating statistics
    private val _roomRatingStats = MutableLiveData<Pair<Float, Int>>() // Average rating, Number of reviews
    val roomRatingStats: LiveData<Pair<Float, Int>> = _roomRatingStats
    
    // LiveData for user reviews
    private val _userReviews = MutableLiveData<List<Review>?>()
    val userReviews: LiveData<List<Review>?> = _userReviews
    
    // LiveData for pending reviews (admin)
    private val _pendingReviews = MutableLiveData<List<Review>?>()
    val pendingReviews: LiveData<List<Review>?> = _pendingReviews
    
    // LiveData for loading state
    private val _isLoadingReviews = MutableLiveData<Boolean>(false)
    val isLoadingReviews: LiveData<Boolean> = _isLoadingReviews
    
    // LiveData for error state
    private val _reviewError = MutableLiveData<String?>(null)
    val reviewError: LiveData<String?> = _reviewError
    
    // LiveData for review submission status
    private val _reviewSubmissionStatus = MutableLiveData<Triple<Boolean, String?, Boolean>>() // Success, Error message, IsLoading
    val reviewSubmissionStatus: LiveData<Triple<Boolean, String?, Boolean>> = _reviewSubmissionStatus
    
    // Current active filter
    private var currentFilter: ReviewFilter = ReviewFilter.ALL
    
    /**
     * Enum for review filtering
     */
    enum class ReviewFilter {
        ALL,
        POSITIVE, // 4-5 stars
        NEGATIVE, // 1-3 stars
        WITH_IMAGES,
        WITH_COMMENTS
    }
    
    /**
     * Get all reviews for a specific room
     */
    fun getRoomReviews(roomId: String) {
        _isLoadingReviews.value = true
        _reviewError.value = null
        
        viewModelScope.launch {
            try {
                val reviews = reviewRepository.getRoomReviews(roomId)
                _roomReviews.postValue(reviews)
                
                // Apply current filter
                applyFilter(currentFilter)
                
                // Calculate and update rating statistics
                val ratingStats = reviewRepository.calculateRoomRating(roomId)
                _roomRatingStats.postValue(ratingStats)
                
                _isLoadingReviews.postValue(false)
            } catch (e: Exception) {
                _reviewError.postValue("Failed to load reviews: ${e.message}")
                _isLoadingReviews.postValue(false)
            }
        }
    }
    
    /**
     * Filter reviews by various criteria
     */
    fun applyFilter(filter: ReviewFilter) {
        currentFilter = filter
        val reviews = _roomReviews.value ?: return
        
        val filtered = when (filter) {
            ReviewFilter.ALL -> reviews
            ReviewFilter.POSITIVE -> reviews.filter { it.rating >= 4.0f }
            ReviewFilter.NEGATIVE -> reviews.filter { it.rating < 4.0f }
            ReviewFilter.WITH_IMAGES -> reviews.filter { !it.images.isNullOrEmpty() }
            ReviewFilter.WITH_COMMENTS -> reviews.filter { !it.comment.isNullOrEmpty() }
        }
        
        _filteredReviews.value = filtered
    }
    
    /**
     * Get all reviews by the current user
     */
    fun getUserReviews() {
        if (!SessionManager.isLoggedIn()) {
            _reviewError.value = "Please login to view your reviews."
            return
        }
        
        _isLoadingReviews.value = true
        
        viewModelScope.launch {
            try {
                val userId = SessionManager.getUserId() ?: return@launch
                val reviews = reviewRepository.getUserReviews(userId)
                _userReviews.postValue(reviews)
                _isLoadingReviews.postValue(false)
            } catch (e: Exception) {
                _reviewError.postValue("Failed to load user reviews: ${e.message}")
                _isLoadingReviews.postValue(false)
            }
        }
    }
    
    /**
     * Submit a new review for a room
     */
    fun submitReview(
        roomId: String,
        rating: Float,
        comment: String? = null,
        images: List<String>? = null
    ) {
        if (!SessionManager.isLoggedIn()) {
            _reviewSubmissionStatus.value = Triple(false, "Please login to submit a review.", false)
            return
        }
        
        if (rating < 1.0f) {
            _reviewSubmissionStatus.value = Triple(false, "Please provide a rating.", false)
            return
        }
        
        _reviewSubmissionStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            try {
                val review = reviewRepository.submitReview(
                    roomId = roomId,
                    rating = rating,
                    comment = comment,
                    images = images
                )
                
                _reviewSubmissionStatus.postValue(Triple(true, null, false))
                
                // Refresh room reviews
                getRoomReviews(roomId)
            } catch (e: Exception) {
                _reviewSubmissionStatus.postValue(
                    Triple(false, "Error: ${e.message}", false)
                )
            }
        }
    }
    
    /**
     * Update an existing review
     */
    fun updateReview(
        reviewId: String,
        roomId: String,
        rating: Float,
        comment: String? = null,
        images: List<String>? = null
    ) {
        if (rating < 1.0f) {
            _reviewSubmissionStatus.value = Triple(false, "Please provide a rating.", false)
            return
        }
        
        _reviewSubmissionStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            try {
                val updatedReview = reviewRepository.updateReview(
                    reviewId = reviewId,
                    rating = rating,
                    comment = comment,
                    images = images
                )
                
                if (updatedReview != null) {
                    _reviewSubmissionStatus.postValue(Triple(true, null, false))
                    
                    // Refresh room reviews
                    getRoomReviews(roomId)
                } else {
                    _reviewSubmissionStatus.postValue(
                        Triple(false, "Failed to update review.", false)
                    )
                }
            } catch (e: Exception) {
                _reviewSubmissionStatus.postValue(
                    Triple(false, "Error: ${e.message}", false)
                )
            }
        }
    }
    
    /**
     * Delete a review
     */
    fun deleteReview(reviewId: String, roomId: String) {
        _reviewSubmissionStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            try {
                val success = reviewRepository.deleteReview(reviewId)
                
                if (success) {
                    _reviewSubmissionStatus.postValue(Triple(true, null, false))
                    
                    // Refresh room reviews
                    getRoomReviews(roomId)
                } else {
                    _reviewSubmissionStatus.postValue(
                        Triple(false, "Failed to delete review.", false)
                    )
                }
            } catch (e: Exception) {
                _reviewSubmissionStatus.postValue(
                    Triple(false, "Error: ${e.message}", false)
                )
            }
        }
    }
    
    /**
     * Mark a review as helpful or not helpful
     */
    fun markReviewHelpfulness(reviewId: String, isHelpful: Boolean) {
        viewModelScope.launch {
            try {
                reviewRepository.markReviewHelpfulness(reviewId, isHelpful)
                
                // Refresh reviews if needed
                val currentReviews = _roomReviews.value
                if (currentReviews != null && currentReviews.any { it.id == reviewId }) {
                    val roomId = currentReviews.first { it.id == reviewId }.roomId
                    getRoomReviews(roomId)
                }
            } catch (e: Exception) {
                _reviewError.postValue("Error marking review: ${e.message}")
            }
        }
    }
    
    /**
     * Get pending reviews for admin
     */
    fun getPendingReviews() {
        if (!SessionManager.isAdmin()) {
            _reviewError.value = "Only admins can access pending reviews."
            return
        }
        
        _isLoadingReviews.value = true
        
        viewModelScope.launch {
            try {
                val reviews = reviewRepository.getPendingReviews()
                _pendingReviews.postValue(reviews)
                _isLoadingReviews.postValue(false)
            } catch (e: Exception) {
                _reviewError.postValue("Failed to load pending reviews: ${e.message}")
                _isLoadingReviews.postValue(false)
            }
        }
    }
    
    /**
     * Approve or reject a review (admin only)
     */
    fun moderateReview(reviewId: String, isApproved: Boolean, adminComment: String? = null) {
        if (!SessionManager.isAdmin()) {
            _reviewError.value = "Only admins can moderate reviews."
            return
        }
        
        _isLoadingReviews.value = true
        
        viewModelScope.launch {
            try {
                val success = reviewRepository.moderateReview(reviewId, isApproved, adminComment)
                
                if (success) {
                    // Refresh pending reviews
                    getPendingReviews()
                } else {
                    _reviewError.postValue("Failed to moderate review.")
                }
                
                _isLoadingReviews.postValue(false)
            } catch (e: Exception) {
                _reviewError.postValue("Error moderating review: ${e.message}")
                _isLoadingReviews.postValue(false)
            }
        }
    }
    
    /**
     * Add owner response to a review (admin only)
     */
    fun addOwnerResponse(reviewId: String, comment: String) {
        if (!SessionManager.isAdmin()) {
            _reviewError.value = "Only admins can respond to reviews."
            return
        }
        
        if (comment.isBlank()) {
            _reviewError.value = "Response cannot be empty."
            return
        }
        
        _isLoadingReviews.value = true
        
        viewModelScope.launch {
            try {
                val updatedReview = reviewRepository.addOwnerResponse(reviewId, comment)
                
                if (updatedReview != null) {
                    // Refresh room reviews if needed
                    getRoomReviews(updatedReview.roomId)
                } else {
                    _reviewError.postValue("Failed to add response.")
                }
                
                _isLoadingReviews.postValue(false)
            } catch (e: Exception) {
                _reviewError.postValue("Error adding response: ${e.message}")
                _isLoadingReviews.postValue(false)
            }
        }
    }
    
    /**
     * Sort reviews by different criteria
     */
    fun sortReviews(sortCriteria: SortCriteria) {
        val reviews = _filteredReviews.value ?: _roomReviews.value ?: return
        
        val sorted = when (sortCriteria) {
            SortCriteria.NEWEST -> reviews.sortedByDescending { it.createdAt }
            SortCriteria.OLDEST -> reviews.sortedBy { it.createdAt }
            SortCriteria.HIGHEST_RATED -> reviews.sortedByDescending { it.rating }
            SortCriteria.LOWEST_RATED -> reviews.sortedBy { it.rating }
            SortCriteria.MOST_HELPFUL -> reviews.sortedByDescending { it.helpfulCount }
        }
        
        _filteredReviews.value = sorted
    }
    
    /**
     * Enum for review sorting
     */
    enum class SortCriteria {
        NEWEST,
        OLDEST,
        HIGHEST_RATED,
        LOWEST_RATED,
        MOST_HELPFUL
    }
} 