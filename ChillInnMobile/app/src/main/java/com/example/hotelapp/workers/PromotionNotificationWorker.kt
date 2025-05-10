package com.example.hotelapp.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.hotelapp.model.Promotion
import com.example.hotelapp.utils.NotificationHelper

/**
 * Worker class for processing scheduled promotion notifications.
 * This worker is triggered by WorkManager at the scheduled time to
 * display a notification for a promotion.
 */
class PromotionNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            // Extract promotion data from input
            val promotionId = inputData.getString("promotion_id") ?: return Result.failure()
            val title = inputData.getString("promotion_title") ?: return Result.failure()
            val shortDescription = inputData.getString("promotion_short_description") ?: ""
            val description = inputData.getString("promotion_description") ?: ""
            val isExclusive = inputData.getBoolean("is_exclusive", false)
            
            // Create a temporary Promotion object from input data
            val promotion = Promotion(
                id = promotionId,
                title = title,
                shortDescription = shortDescription,
                description = description,
                isExclusive = isExclusive
            )
            
            // Show the notification
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showPromotionNotification(promotion, isExclusive)
            
            return Result.success()
        } catch (e: Exception) {
            // Log the error (in a real app, use proper logging)
            return Result.failure()
        }
    }
} 