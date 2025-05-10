package com.example.hotelapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.hotelapp.model.Promotion
import com.example.hotelapp.workers.PromotionNotificationWorker
import java.util.concurrent.TimeUnit

/**
 * Manager class for handling promotion notification triggers.
 * This class is responsible for scheduling and sending notifications
 * for new promotions and special offers.
 */
class PromotionNotificationManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "promotion_notification_prefs"
        private const val KEY_LAST_NOTIFICATION_TIME = "last_notification_time"
        private const val KEY_NOTIFICATION_SENT_PREFIX = "notification_sent_"
        
        // Notification frequency constraints
        private const val MIN_HOURS_BETWEEN_NOTIFICATIONS = 24
        private const val MAX_NOTIFICATIONS_PER_WEEK = 3
    }

    private val notificationHelper = NotificationHelper(context)
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Initialize notification channels
     */
    fun initialize() {
        notificationHelper.createNotificationChannels()
    }
    
    /**
     * Trigger notification for a new promotion that was just released
     * @param promotion The new promotion to notify about
     * @param isExclusive Whether this is an exclusive offer
     */
    fun triggerNewPromotionNotification(promotion: Promotion, isExclusive: Boolean = false) {
        // Check if we should send this notification (throttling)
        if (!shouldSendPromotionNotification(promotion.id)) {
            return
        }
        
        // Send the notification
        notificationHelper.showPromotionNotification(promotion, isExclusive)
        
        // Mark this promotion as notified
        markPromotionAsNotified(promotion.id)
    }
    
    /**
     * Schedule a promotion notification for later
     * @param promotion The promotion to notify about
     * @param delayHours Hours to delay before sending notification
     */
    fun schedulePromotionNotification(promotion: Promotion, delayHours: Int) {
        // Create input data for worker
        val inputData = Data.Builder()
            .putString("promotion_id", promotion.id)
            .putString("promotion_title", promotion.title)
            .putString("promotion_short_description", promotion.shortDescription)
            .putString("promotion_description", promotion.description)
            .putBoolean("is_exclusive", promotion.isExclusive)
            .build()
        
        // Create work request
        val promotionNotificationWork = OneTimeWorkRequestBuilder<PromotionNotificationWorker>()
            .setInitialDelay(delayHours.toLong(), TimeUnit.HOURS)
            .setInputData(inputData)
            .addTag("promotion_notification")
            .addTag("promotion_${promotion.id}")
            .build()
        
        // Enqueue work request
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "promotion_notification_${promotion.id}",
                ExistingWorkPolicy.REPLACE,
                promotionNotificationWork
            )
    }
    
    /**
     * Trigger notifications for multiple new promotions at once
     * @param promotions List of new promotions
     */
    fun triggerMultiplePromotionNotifications(promotions: List<Promotion>) {
        // Filter out promotions that have already been notified or that exceed our throttling limits
        val promotionsToNotify = promotions
            .filter { shouldSendPromotionNotification(it.id) }
            .take(MAX_NOTIFICATIONS_PER_WEEK)
        
        // Send notifications with progressive delays to avoid overwhelming the user
        promotionsToNotify.forEachIndexed { index, promotion ->
            if (index == 0) {
                // Send the first one immediately
                notificationHelper.showPromotionNotification(promotion, promotion.isExclusive)
            } else {
                // Schedule the rest with progressive delays
                val delayMinutes = index * 30L // 30 min increments
                scheduleWithMinuteDelay(promotion, delayMinutes)
            }
            
            // Mark as notified
            markPromotionAsNotified(promotion.id)
        }
    }
    
    /**
     * Schedule a promotional notification with minute-level delay
     * For more precise short-term scheduling
     */
    private fun scheduleWithMinuteDelay(promotion: Promotion, delayMinutes: Long) {
        val inputData = Data.Builder()
            .putString("promotion_id", promotion.id)
            .putString("promotion_title", promotion.title)
            .putString("promotion_short_description", promotion.shortDescription)
            .putString("promotion_description", promotion.description)
            .putBoolean("is_exclusive", promotion.isExclusive)
            .build()
        
        val promotionNotificationWork = OneTimeWorkRequestBuilder<PromotionNotificationWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(inputData)
            .addTag("promotion_notification")
            .addTag("promotion_${promotion.id}")
            .build()
        
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "promotion_notification_${promotion.id}",
                ExistingWorkPolicy.REPLACE,
                promotionNotificationWork
            )
    }
    
    /**
     * Cancel a scheduled promotion notification
     * @param promotionId The ID of the promotion to cancel notification for
     */
    fun cancelScheduledPromotionNotification(promotionId: String) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("promotion_notification_$promotionId")
    }
    
    /**
     * Cancel all scheduled promotion notifications
     */
    fun cancelAllScheduledPromotionNotifications() {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("promotion_notification")
    }
    
    /**
     * Check if we should send a notification for this promotion
     * This implements notification throttling to avoid overwhelming the user
     */
    private fun shouldSendPromotionNotification(promotionId: String): Boolean {
        // Check if this specific promotion has already been notified
        if (preferences.getBoolean("$KEY_NOTIFICATION_SENT_PREFIX$promotionId", false)) {
            return false
        }
        
        // Check when we last sent any promotion notification
        val lastNotificationTime = preferences.getLong(KEY_LAST_NOTIFICATION_TIME, 0)
        val currentTime = System.currentTimeMillis()
        val hoursSinceLastNotification = TimeUnit.MILLISECONDS.toHours(currentTime - lastNotificationTime)
        
        // Enforce minimum time between notifications
        return hoursSinceLastNotification >= MIN_HOURS_BETWEEN_NOTIFICATIONS
    }
    
    /**
     * Mark a promotion as having been notified
     */
    private fun markPromotionAsNotified(promotionId: String) {
        preferences.edit()
            .putBoolean("$KEY_NOTIFICATION_SENT_PREFIX$promotionId", true)
            .putLong(KEY_LAST_NOTIFICATION_TIME, System.currentTimeMillis())
            .apply()
    }
    
    /**
     * Reset notification tracking for a promotion
     * Useful for testing or when a promotion is updated significantly
     */
    fun resetPromotionNotificationTracking(promotionId: String) {
        preferences.edit()
            .remove("$KEY_NOTIFICATION_SENT_PREFIX$promotionId")
            .apply()
    }
    
    /**
     * Reset all notification tracking data
     */
    fun resetAllPromotionNotificationTracking() {
        preferences.edit().clear().apply()
    }
} 