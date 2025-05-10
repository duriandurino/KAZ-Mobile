package com.donate.chillinnmobile.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.donate.chillinnmobile.model.Notification
import com.donate.chillinnmobile.model.NotificationType
import com.donate.chillinnmobile.utils.NotificationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Repository for managing notifications
 */
class NotificationRepository(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "notification_prefs"
        private const val KEY_NOTIFICATIONS = "notifications"
        private const val MAX_STORED_NOTIFICATIONS = 50
        private const val TAG = "NotificationRepository"
    }

    // Local cache of notifications
    private val notificationCache = ConcurrentHashMap<String, Notification>()
    
    // Notification manager for showing notifications
    private val notificationManager = NotificationManager(context)
    
    // Shared preferences for persisting notifications
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    
    // Date format for storing/retrieving dates
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    
    init {
        // Initialize notification channels
        notificationManager.createNotificationChannels()
        
        // Load cached notifications
        loadNotificationsFromStorage()
    }
    
    /**
     * Get all notifications
     */
    suspend fun getAllNotifications(): List<Notification> = withContext(Dispatchers.IO) {
        return@withContext notificationCache.values.sortedByDescending { it.timestamp }
    }
    
    /**
     * Get unread notifications
     */
    suspend fun getUnreadNotifications(): List<Notification> = withContext(Dispatchers.IO) {
        return@withContext notificationCache.values
            .filter { !it.isRead }
            .sortedByDescending { it.timestamp }
    }
    
    /**
     * Get notifications by type
     */
    suspend fun getNotificationsByType(type: NotificationType): List<Notification> = withContext(Dispatchers.IO) {
        return@withContext notificationCache.values
            .filter { it.type == type }
            .sortedByDescending { it.timestamp }
    }
    
    /**
     * Mark notification as read
     */
    suspend fun markAsRead(notificationId: String): Boolean = withContext(Dispatchers.IO) {
        val notification = notificationCache[notificationId] ?: return@withContext false
        
        val updatedNotification = notification.copy(isRead = true)
        notificationCache[notificationId] = updatedNotification
        
        saveNotificationsToStorage()
        
        return@withContext true
    }
    
    /**
     * Mark all notifications as read
     */
    suspend fun markAllAsRead(): Boolean = withContext(Dispatchers.IO) {
        val updated = notificationCache.values.map { it.copy(isRead = true) }
        
        // Update cache
        notificationCache.clear()
        updated.forEach { notification ->
            notificationCache[notification.id] = notification
        }
        
        saveNotificationsToStorage()
        
        return@withContext true
    }
    
    /**
     * Delete a specific notification
     */
    suspend fun deleteNotification(notificationId: String): Boolean = withContext(Dispatchers.IO) {
        val removed = notificationCache.remove(notificationId) != null
        
        if (removed) {
            saveNotificationsToStorage()
        }
        
        return@withContext removed
    }
    
    /**
     * Delete all notifications
     */
    suspend fun deleteAllNotifications(): Boolean = withContext(Dispatchers.IO) {
        notificationCache.clear()
        saveNotificationsToStorage()
        
        return@withContext true
    }
    
    /**
     * Create a new notification
     * 
     * @param title Notification title
     * @param message Notification message
     * @param type Notification type
     * @param show Whether to show the notification immediately
     * @param actionData Optional data for handling notification tap
     * @param imageUrl Optional image URL
     */
    suspend fun createNotification(
        title: String,
        message: String,
        type: NotificationType,
        show: Boolean = true,
        actionData: String? = null,
        imageUrl: String? = null
    ): Notification = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val notification = Notification(
            id = id,
            title = title,
            message = message,
            type = type,
            isRead = false,
            actionData = actionData,
            imageUrl = imageUrl
        )
        
        // Add to cache
        notificationCache[id] = notification
        
        // Trim cache if needed
        if (notificationCache.size > MAX_STORED_NOTIFICATIONS) {
            trimNotificationCache()
        }
        
        // Persist to storage
        saveNotificationsToStorage()
        
        // Show notification if requested
        if (show) {
            notificationManager.showNotification(title, message, type, actionData)
        }
        
        return@withContext notification
    }
    
    /**
     * Create a booking confirmation notification
     */
    suspend fun createBookingConfirmationNotification(
        bookingId: String,
        roomName: String,
        checkInDate: String,
        show: Boolean = true
    ): Notification {
        val title = "Booking Confirmed!"
        val message = "Your booking for $roomName is confirmed. Check-in date: $checkInDate. " +
                "We're looking forward to your stay!"
        
        val notification = createNotification(
            title = title,
            message = message,
            type = NotificationType.BOOKING,
            show = false,
            actionData = bookingId
        )
        
        if (show) {
            notificationManager.showBookingConfirmation(bookingId, roomName, checkInDate)
        }
        
        return notification
    }
    
    /**
     * Create a payment confirmation notification
     */
    suspend fun createPaymentConfirmationNotification(
        bookingId: String,
        amount: String,
        show: Boolean = true
    ): Notification {
        val title = "Payment Successful"
        val message = "Your payment of $amount has been successfully processed for booking #$bookingId."
        
        val notification = createNotification(
            title = title,
            message = message,
            type = NotificationType.BOOKING,
            show = false,
            actionData = bookingId
        )
        
        if (show) {
            notificationManager.showPaymentSuccessful(bookingId, amount)
        }
        
        return notification
    }
    
    /**
     * Load notifications from shared preferences
     */
    private fun loadNotificationsFromStorage() {
        val notificationsJson = sharedPreferences.getString(KEY_NOTIFICATIONS, null) ?: return
        
        try {
            val jsonArray = JSONArray(notificationsJson)
            
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                
                val id = jsonObject.getString("id")
                val title = jsonObject.getString("title")
                val message = jsonObject.getString("message")
                val typeString = jsonObject.getString("type")
                val timestampString = jsonObject.getString("timestamp")
                val isRead = jsonObject.getBoolean("isRead")
                val actionData = if (jsonObject.has("actionData")) jsonObject.getString("actionData") else null
                val imageUrl = if (jsonObject.has("imageUrl")) jsonObject.getString("imageUrl") else null
                
                // Parse type
                val type = try {
                    NotificationType.valueOf(typeString)
                } catch (e: Exception) {
                    NotificationType.GENERAL
                }
                
                // Parse date
                val timestamp = try {
                    dateFormat.parse(timestampString) ?: Date()
                } catch (e: Exception) {
                    Date()
                }
                
                val notification = Notification(
                    id = id,
                    title = title,
                    message = message,
                    type = type,
                    timestamp = timestamp,
                    isRead = isRead,
                    actionData = actionData,
                    imageUrl = imageUrl
                )
                
                notificationCache[id] = notification
            }
            
            Log.d(TAG, "Loaded ${notificationCache.size} notifications from storage")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading notifications from storage", e)
        }
    }
    
    /**
     * Save notifications to shared preferences
     */
    private fun saveNotificationsToStorage() {
        try {
            val jsonArray = JSONArray()
            
            for (notification in notificationCache.values) {
                val jsonObject = JSONObject().apply {
                    put("id", notification.id)
                    put("title", notification.title)
                    put("message", notification.message)
                    put("type", notification.type.name)
                    put("timestamp", dateFormat.format(notification.timestamp))
                    put("isRead", notification.isRead)
                    notification.actionData?.let { put("actionData", it) }
                    notification.imageUrl?.let { put("imageUrl", it) }
                }
                
                jsonArray.put(jsonObject)
            }
            
            sharedPreferences.edit()
                .putString(KEY_NOTIFICATIONS, jsonArray.toString())
                .apply()
            
            Log.d(TAG, "Saved ${notificationCache.size} notifications to storage")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving notifications to storage", e)
        }
    }
    
    /**
     * Trim notification cache to stay within size limit
     */
    private fun trimNotificationCache() {
        val sortedNotifications = notificationCache.values.sortedBy { it.timestamp }
        val overflow = notificationCache.size - MAX_STORED_NOTIFICATIONS
        
        if (overflow > 0) {
            for (i in 0 until overflow) {
                if (i < sortedNotifications.size) {
                    notificationCache.remove(sortedNotifications[i].id)
                }
            }
        }
    }
} 