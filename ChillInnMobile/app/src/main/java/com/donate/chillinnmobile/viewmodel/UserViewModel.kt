package com.donate.chillinnmobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.donate.chillinnmobile.model.Booking
import com.donate.chillinnmobile.model.PaymentMethod
import com.donate.chillinnmobile.model.PaymentMethodType
import com.donate.chillinnmobile.model.Room
import com.donate.chillinnmobile.model.User
import com.donate.chillinnmobile.repository.UserRepository
import com.donate.chillinnmobile.utils.SessionManager
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

/**
 * ViewModel for user-related operations like login, registration, and profile management
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userRepository = UserRepository()
    
    // Initialize SessionManager
    init {
        SessionManager.init(application.applicationContext)
    }
    
    // LiveData for login status
    private val _loginStatus = MutableLiveData<Triple<Boolean, String?, Boolean>>() // Success, Error message, IsLoading
    val loginStatus: LiveData<Triple<Boolean, String?, Boolean>> = _loginStatus
    
    // LiveData for registration status
    private val _registerStatus = MutableLiveData<Triple<Boolean, String?, Boolean>>() // Success, Error message, IsLoading
    val registerStatus: LiveData<Triple<Boolean, String?, Boolean>> = _registerStatus
    
    // LiveData for user profile
    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> = _userProfile
    
    // LiveData for profile update status
    private val _profileUpdateStatus = MutableLiveData<Triple<Boolean, String?, Boolean>>() // Success, Error message, IsLoading
    val profileUpdateStatus: LiveData<Triple<Boolean, String?, Boolean>> = _profileUpdateStatus
    
    // Current user data
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData
    
    // User's favorite rooms
    private val _favorites = MutableLiveData<List<Room>>()
    val favorites: LiveData<List<Room>> = _favorites
    
    // User's booking history
    private val _bookingHistory = MutableLiveData<List<Booking>>()
    val bookingHistory: LiveData<List<Booking>> = _bookingHistory
    
    // User's payment methods
    private val _paymentMethods = MutableLiveData<List<PaymentMethod>>()
    val paymentMethods: LiveData<List<PaymentMethod>> = _paymentMethods
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Login user with email and password
     */
    fun login(email: String, password: String) {
        _loginStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            val (success, errorMessage) = userRepository.login(email, password)
            
            if (success) {
                refreshUserProfile()
            }
            
            _loginStatus.postValue(Triple(success, errorMessage, false)) // Stop loading
        }
    }
    
    /**
     * Register a new user
     */
    fun register(email: String, password: String, fullName: String, phoneNumber: String) {
        _registerStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            val (success, errorMessage) = userRepository.register(email, password, fullName, phoneNumber)
            _registerStatus.postValue(Triple(success, errorMessage, false)) // Stop loading
        }
    }
    
    /**
     * Get user profile
     */
    fun refreshUserProfile() {
        viewModelScope.launch {
            val profile = userRepository.getUserProfile()
            _userProfile.postValue(profile)
        }
    }
    
    /**
     * Update user profile
     */
    fun updateUserProfile(fullName: String? = null, phoneNumber: String? = null, profileImage: String? = null) {
        _profileUpdateStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            val updatedProfile = userRepository.updateUserProfile(fullName, phoneNumber, profileImage)
            
            if (updatedProfile != null) {
                _userProfile.postValue(updatedProfile)
                _profileUpdateStatus.postValue(Triple(true, null, false))
            } else {
                _profileUpdateStatus.postValue(Triple(false, "Failed to update profile", false))
            }
        }
    }
    
    /**
     * Logout user
     */
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _userProfile.postValue(null)
        }
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return SessionManager.isLoggedIn()
    }
    
    /**
     * Check if user is admin
     */
    fun isAdmin(): Boolean {
        return SessionManager.isAdmin()
    }
    
    /**
     * Get the current user's data
     */
    fun getCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userRepository.getCurrentUser()
                _userData.value = user
            } catch (e: Exception) {
                _error.value = "Failed to load user data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Get the user's ID
     */
    fun getUserId(): String? {
        return _userData.value?.id
    }
    
    /**
     * Get the user's favorite rooms
     */
    fun getUserFavorites() {
        viewModelScope.launch {
            try {
                val favorites = userRepository.getUserFavorites()
                _favorites.value = favorites
            } catch (e: Exception) {
                _error.value = "Failed to load favorites: ${e.message}"
            }
        }
    }
    
    /**
     * Get the user's booking history
     */
    fun getUserBookingHistory() {
        viewModelScope.launch {
            try {
                val bookings = userRepository.getUserBookingHistory()
                _bookingHistory.value = bookings
            } catch (e: Exception) {
                _error.value = "Failed to load booking history: ${e.message}"
            }
        }
    }
    
    /**
     * Get user's payment methods
     */
    fun getUserPaymentMethods() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // This would normally call the repository
                // For now, return mock data
                val paymentMethods = getMockPaymentMethods()
                _paymentMethods.value = paymentMethods
            } catch (e: Exception) {
                _error.value = "Failed to load payment methods: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Add a new payment method
     */
    fun addPaymentMethod(
        type: PaymentMethodType,
        cardNumber: String,
        cardholderName: String,
        expiryMonth: Int,
        expiryYear: Int,
        setAsDefault: Boolean
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // In a real app, this would call the repository
                // For now, just add it to our local list
                val newPaymentMethod = PaymentMethod(
                    id = UUID.randomUUID().toString(),
                    userId = getUserId() ?: "default_user",
                    type = type,
                    cardNumber = cardNumber.takeLast(4),  // Only store last 4 digits
                    cardholderName = cardholderName,
                    expiryMonth = expiryMonth,
                    expiryYear = expiryYear,
                    isDefault = setAsDefault
                )
                
                // If this is the default, update any existing default
                val currentList = _paymentMethods.value ?: emptyList()
                val updatedList = if (setAsDefault) {
                    currentList.map { it.copy(isDefault = false) }.toMutableList()
                } else {
                    currentList.toMutableList()
                }
                
                updatedList.add(newPaymentMethod)
                _paymentMethods.value = updatedList
                
            } catch (e: Exception) {
                _error.value = "Failed to add payment method: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Remove a payment method
     */
    fun removePaymentMethod(paymentMethodId: String) {
        viewModelScope.launch {
            try {
                // In a real app, this would call the repository
                // For now, just remove from our local list
                val currentList = _paymentMethods.value ?: return@launch
                _paymentMethods.value = currentList.filter { it.id != paymentMethodId }
            } catch (e: Exception) {
                _error.value = "Failed to remove payment method: ${e.message}"
            }
        }
    }
    
    /**
     * Set a payment method as default
     */
    fun setDefaultPaymentMethod(paymentMethodId: String) {
        viewModelScope.launch {
            try {
                // In a real app, this would call the repository
                // For now, just update our local list
                val currentList = _paymentMethods.value ?: return@launch
                _paymentMethods.value = currentList.map {
                    it.copy(isDefault = it.id == paymentMethodId)
                }
            } catch (e: Exception) {
                _error.value = "Failed to set default payment method: ${e.message}"
            }
        }
    }
    
    /**
     * Get mock payment methods for testing
     */
    private fun getMockPaymentMethods(): List<PaymentMethod> {
        return listOf(
            PaymentMethod(
                id = "pm_1",
                userId = "user_1",
                type = PaymentMethodType.VISA,
                cardNumber = "4242",
                cardholderName = "John Doe",
                expiryMonth = 12,
                expiryYear = 2024,
                isDefault = true
            ),
            PaymentMethod(
                id = "pm_2",
                userId = "user_1",
                type = PaymentMethodType.MASTERCARD,
                cardNumber = "5678",
                cardholderName = "John Doe",
                expiryMonth = 3,
                expiryYear = 2025,
                isDefault = false
            )
        )
    }
    
    /**
     * Remove a room from favorites
     */
    suspend fun removeFromFavorites(roomId: String) {
        try {
            userRepository.removeFromFavorites(roomId)
            
            // Update the local list
            _favorites.value = _favorites.value?.filter { it.id != roomId }
        } catch (e: Exception) {
            _error.value = "Failed to remove from favorites: ${e.message}"
        }
    }
    
    /**
     * Update user's profile image
     */
    fun updateProfileImage(imageUrl: String) {
        viewModelScope.launch {
            try {
                // Update in repository
                userRepository.updateProfileImage(imageUrl)
                
                // Update local data
                _userData.value = _userData.value?.copy(profileImageUrl = imageUrl)
            } catch (e: Exception) {
                _error.value = "Failed to update profile image: ${e.message}"
            }
        }
    }
    
    /**
     * Update user's theme preference
     */
    fun updateThemePreference(darkThemeEnabled: Boolean) {
        viewModelScope.launch {
            try {
                // Update in repository
                userRepository.updateThemePreference(darkThemeEnabled)
                
                // Update local data
                _userData.value = _userData.value?.copy(darkThemeEnabled = darkThemeEnabled)
            } catch (e: Exception) {
                _error.value = "Failed to update theme preference: ${e.message}"
            }
        }
    }
    
    /**
     * Update user's notification preference
     */
    fun updateNotificationPreference(notificationsEnabled: Boolean) {
        viewModelScope.launch {
            try {
                // Update in repository
                userRepository.updateNotificationPreference(notificationsEnabled)
                
                // Update local data
                _userData.value = _userData.value?.copy(notificationsEnabled = notificationsEnabled)
            } catch (e: Exception) {
                _error.value = "Failed to update notification preference: ${e.message}"
            }
        }
    }
    
    /**
     * Clear any error message
     */
    fun clearError() {
        _error.value = null
    }
} 