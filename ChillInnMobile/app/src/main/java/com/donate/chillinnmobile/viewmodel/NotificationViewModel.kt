package com.donate.chillinnmobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.donate.chillinnmobile.model.Notification
import com.donate.chillinnmobile.model.NotificationType
import com.donate.chillinnmobile.repository.NotificationRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for managing notifications
 */
class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    
    // Repository
    private val notificationRepository = NotificationRepository(application.applicationContext)
    
    // LiveData for all notifications
    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications
    
    // LiveData for unread notifications count
    private val _unreadCount = MutableLiveData<Int>()
    val unreadCount: LiveData<Int> = _unreadCount
    
    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData for error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Initialize ViewModel and load data
     */
    init {
        loadNotifications()
    }
    
    /**
     * Load all notifications
     */
    fun loadNotifications() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val allNotifications = notificationRepository.getAllNotifications()
                _notifications.postValue(allNotifications)
                
                val unreadNotifications = notificationRepository.getUnreadNotifications()
                _unreadCount.postValue(unreadNotifications.size)
            } catch (e: Exception) {
                _error.postValue("Failed to load notifications: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    
    /**
     * Load notifications of a specific type
     */
    fun loadNotificationsByType(type: NotificationType) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val filtered = notificationRepository.getNotificationsByType(type)
                _notifications.postValue(filtered)
            } catch (e: Exception) {
                _error.postValue("Failed to load notifications: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    
    /**
     * Load only unread notifications
     */
    fun loadUnreadNotifications() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val unread = notificationRepository.getUnreadNotifications()
                _notifications.postValue(unread)
                _unreadCount.postValue(unread.size)
            } catch (e: Exception) {
                _error.postValue("Failed to load notifications: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    
    /**
     * Mark a notification as read
     */
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                val success = notificationRepository.markAsRead(notificationId)
                if (success) {
                    // Update UI
                    loadNotifications()
                }
            } catch (e: Exception) {
                _error.postValue("Failed to mark notification as read: ${e.message}")
            }
        }
    }
    
    /**
     * Mark all notifications as read
     */
    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                val success = notificationRepository.markAllAsRead()
                if (success) {
                    // Update UI
                    loadNotifications()
                }
            } catch (e: Exception) {
                _error.postValue("Failed to mark all notifications as read: ${e.message}")
            }
        }
    }
    
    /**
     * Delete a specific notification
     */
    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                val success = notificationRepository.deleteNotification(notificationId)
                if (success) {
                    // Update UI
                    loadNotifications()
                }
            } catch (e: Exception) {
                _error.postValue("Failed to delete notification: ${e.message}")
            }
        }
    }
    
    /**
     * Delete all notifications
     */
    fun deleteAllNotifications() {
        viewModelScope.launch {
            try {
                val success = notificationRepository.deleteAllNotifications()
                if (success) {
                    // Update UI
                    loadNotifications()
                }
            } catch (e: Exception) {
                _error.postValue("Failed to delete all notifications: ${e.message}")
            }
        }
    }
    
    /**
     * Create a test notification (for development purposes)
     */
    fun createTestNotification(type: NotificationType) {
        viewModelScope.launch {
            try {
                when (type) {
                    NotificationType.BOOKING -> {
                        notificationRepository.createBookingConfirmationNotification(
                            bookingId = "BK-TEST-${System.currentTimeMillis()}",
                            roomName = "Deluxe Suite",
                            checkInDate = "May 20, 2025"
                        )
                    }
                    NotificationType.PROMOTION -> {
                        notificationRepository.createNotification(
                            title = "Special Weekend Offer!",
                            message = "Book now and get 20% off on all rooms this weekend. Use promo code WEEKEND20.",
                            type = NotificationType.PROMOTION,
                            actionData = "WEEKEND20"
                        )
                    }
                    NotificationType.GENERAL -> {
                        notificationRepository.createNotification(
                            title = "App Update Available",
                            message = "A new version of ChillInnMobile is available. Update now to get the latest features and improvements.",
                            type = NotificationType.GENERAL
                        )
                    }
                }
                
                // Update UI
                loadNotifications()
            } catch (e: Exception) {
                _error.postValue("Failed to create test notification: ${e.message}")
            }
        }
    }
    
    /**
     * Create a booking confirmation notification
     */
    fun createBookingConfirmationNotification(bookingId: String, roomName: String, checkInDate: String) {
        viewModelScope.launch {
            try {
                notificationRepository.createBookingConfirmationNotification(
                    bookingId = bookingId,
                    roomName = roomName,
                    checkInDate = checkInDate
                )
                
                // Update UI
                loadNotifications()
            } catch (e: Exception) {
                _error.postValue("Failed to create booking confirmation notification: ${e.message}")
            }
        }
    }
    
    /**
     * Create a payment confirmation notification
     */
    fun createPaymentConfirmationNotification(bookingId: String, amount: String) {
        viewModelScope.launch {
            try {
                notificationRepository.createPaymentConfirmationNotification(
                    bookingId = bookingId,
                    amount = amount
                )
                
                // Update UI
                loadNotifications()
            } catch (e: Exception) {
                _error.postValue("Failed to create payment confirmation notification: ${e.message}")
            }
        }
    }
} 