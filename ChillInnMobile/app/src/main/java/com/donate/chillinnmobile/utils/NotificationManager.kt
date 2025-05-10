package com.donate.chillinnmobile.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.activity.MainActivity
import com.donate.chillinnmobile.model.NotificationType
import java.util.Random

/**
 * Manager class for handling app notifications
 */
class NotificationManager(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID_BOOKING = "booking_notifications"
        private const val CHANNEL_ID_PROMOTIONS = "promotion_notifications"
        private const val CHANNEL_ID_GENERAL = "general_notifications"
        
        // Request codes for pending intents
        private const val REQUEST_CODE_BOOKING = 100
        private const val REQUEST_CODE_PROMOTION = 200
        private const val REQUEST_CODE_GENERAL = 300
    }
    
    /**
     * Initialize notification channels for Android O and above
     */
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val bookingChannel = NotificationChannel(
                CHANNEL_ID_BOOKING,
                "Booking Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for booking updates, confirmations, and reminders"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            
            val promotionsChannel = NotificationChannel(
                CHANNEL_ID_PROMOTIONS,
                "Promotion Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for special offers and promotions"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            
            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General notifications about app updates and information"
                enableLights(true)
                lightColor = Color.YELLOW
            }
            
            // Register channels with the system
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(listOf(bookingChannel, promotionsChannel, generalChannel))
        }
    }
    
    /**
     * Show a notification to the user
     * 
     * @param title Notification title
     * @param message Notification message
     * @param type Type of notification
     * @param actionData Extra data for deep linking (e.g., booking ID)
     */
    fun showNotification(
        title: String,
        message: String,
        type: NotificationType,
        actionData: String? = null
    ) {
        val channelId = when (type) {
            NotificationType.BOOKING -> CHANNEL_ID_BOOKING
            NotificationType.PROMOTION -> CHANNEL_ID_PROMOTIONS
            NotificationType.GENERAL -> CHANNEL_ID_GENERAL
        }
        
        val requestCode = when (type) {
            NotificationType.BOOKING -> REQUEST_CODE_BOOKING
            NotificationType.PROMOTION -> REQUEST_CODE_PROMOTION
            NotificationType.GENERAL -> REQUEST_CODE_GENERAL
        }
        
        // Create intent for when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", type.name)
            actionData?.let { putExtra("notification_data", it) }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        // Add style for long messages
        if (message.length > 50) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        }
        
        // Set priority based on notification type
        when (type) {
            NotificationType.BOOKING -> builder.priority = NotificationCompat.PRIORITY_HIGH
            NotificationType.PROMOTION -> builder.priority = NotificationCompat.PRIORITY_DEFAULT
            NotificationType.GENERAL -> builder.priority = NotificationCompat.PRIORITY_LOW
        }
        
        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            // Generate a unique ID for this notification
            val notificationId = Random().nextInt(1000) + type.ordinal * 1000
            
            // Check notification permission (Android 13+)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || 
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notify(notificationId, builder.build())
            }
        }
    }
    
    /**
     * Show a booking confirmation notification
     */
    fun showBookingConfirmation(bookingId: String, roomName: String, checkInDate: String) {
        val title = "Booking Confirmed!"
        val message = "Your booking for $roomName is confirmed. Check-in date: $checkInDate. " +
                "We're looking forward to your stay!"
        
        showNotification(title, message, NotificationType.BOOKING, bookingId)
    }
    
    /**
     * Show a booking reminder notification
     */
    fun showBookingReminder(bookingId: String, roomName: String, checkInDate: String, daysLeft: Int) {
        val title = "Upcoming Stay Reminder"
        val message = "Your stay at $roomName is coming up in $daysLeft days! " +
                "Check-in date: $checkInDate. We look forward to welcoming you!"
        
        showNotification(title, message, NotificationType.BOOKING, bookingId)
    }
    
    /**
     * Show a special offer notification
     */
    fun showSpecialOffer(title: String, message: String, promoCode: String? = null) {
        showNotification(title, message, NotificationType.PROMOTION, promoCode)
    }
    
    /**
     * Show a payment successful notification
     */
    fun showPaymentSuccessful(bookingId: String, amount: String) {
        val title = "Payment Successful"
        val message = "Your payment of $amount has been successfully processed for booking #$bookingId."
        
        showNotification(title, message, NotificationType.BOOKING, bookingId)
    }
} 