package com.example.hotelapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationManagerCompat
import com.example.hotelapp.R
import com.example.hotelapp.activity.MainActivity
import com.example.hotelapp.model.Promotion
import java.util.concurrent.atomic.AtomicInteger

/**
 * Helper class for managing notifications across the app.
 * Handles notification channels, creation, and display.
 */
class NotificationHelper(private val context: Context) {

    companion object {
        // Notification channel IDs
        const val CHANNEL_PROMOTIONS = "promotions_channel"
        const val CHANNEL_BOOKINGS = "bookings_channel"
        const val CHANNEL_GENERAL = "general_channel"
        
        // Notification IDs - use atomic integer for thread safety
        private val notificationId = AtomicInteger(0)
        
        // Request code for pending intent
        private const val REQUEST_CODE_OPEN_PROMOTION = 100
        
        // Deep link actions
        const val ACTION_OPEN_PROMOTIONS = "com.example.hotelapp.action.OPEN_PROMOTIONS"
        const val ACTION_OPEN_PROMOTION_DETAILS = "com.example.hotelapp.action.OPEN_PROMOTION_DETAILS"
        const val EXTRA_PROMOTION_ID = "extra_promotion_id"
    }

    /**
     * Create notification channels for Android O and above
     */
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Promotions channel - Medium importance
            val promotionsChannel = NotificationChannel(
                CHANNEL_PROMOTIONS,
                "Special Offers & Promotions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about special offers and limited-time promotions"
                enableLights(true)
                lightColor = Color.parseColor("#D4AF37") // Secondary color
                setShowBadge(true)
            }
            
            // Bookings channel - High importance
            val bookingsChannel = NotificationChannel(
                CHANNEL_BOOKINGS,
                "Booking Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important updates about your bookings"
                enableLights(true)
                lightColor = Color.parseColor("#2C1810") // Primary color
                setShowBadge(true)
            }
            
            // General channel - Low importance
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "General information and updates"
                setShowBadge(false)
            }

            // Register all channels
            notificationManager.createNotificationChannels(
                listOf(promotionsChannel, bookingsChannel, generalChannel)
            )
        }
    }

    /**
     * Show a notification for a new promotion
     * @param promotion The promotion to create a notification for
     * @param isExclusive Whether this is an exclusive offer (affects styling)
     */
    fun showPromotionNotification(promotion: Promotion, isExclusive: Boolean = false) {
        // Create an intent to open the promotions section
        val intent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_OPEN_PROMOTION_DETAILS
            putExtra(EXTRA_PROMOTION_ID, promotion.id)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_OPEN_PROMOTION,
            intent,
            pendingIntentFlags
        )
        
        // Create a rich notification with image
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_promotion_notification)
        
        // Build the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_PROMOTIONS)
            .setSmallIcon(R.drawable.ic_notification_promotion)
            .setLargeIcon(largeIcon)
            .setContentTitle(promotion.title)
            .setContentText(promotion.shortDescription)
            .setStyle(NotificationCompat.BigTextStyle().bigText(promotion.description))
            .setPriority(if (isExclusive) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setColor(ContextCompat.getColor(context, R.color.secondary))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setCategory(NotificationCompat.CATEGORY_PROMO)
        
        // Add exclusive badge for exclusive offers
        if (isExclusive) {
            builder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        }
            
        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId.incrementAndGet(), builder.build())
        }
    }
    
    /**
     * Show a notification for a booking update
     * @param bookingId The booking ID
     * @param title Notification title
     * @param message Notification message
     */
    fun showBookingNotification(bookingId: String, title: String, message: String) {
        // Create an intent to open the booking details
        val intent = Intent(context, MainActivity::class.java).apply {
            action = "com.example.hotelapp.action.OPEN_BOOKING"
            putExtra("booking_id", bookingId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            bookingId.hashCode(),
            intent,
            pendingIntentFlags
        )
        
        // Build the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_BOOKINGS)
            .setSmallIcon(R.drawable.ic_notification_booking)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(ContextCompat.getColor(context, R.color.primary))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            
        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(bookingId.hashCode(), builder.build())
        }
    }
    
    /**
     * Clear all notifications for the app
     */
    fun clearAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
    
    /**
     * Schedule a promotion notification to be shown at a specific time
     * This would typically use WorkManager for real implementation
     */
    fun schedulePromotionNotification(promotion: Promotion, delayMillis: Long) {
        // In a real implementation, this would use WorkManager
        // For now, we'll just simulate it
        android.os.Handler().postDelayed({
            showPromotionNotification(promotion)
        }, delayMillis)
    }
} 