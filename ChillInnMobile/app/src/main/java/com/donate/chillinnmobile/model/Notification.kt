package com.donate.chillinnmobile.model

import java.util.Date

/**
 * Data class for app notifications
 */
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Date = Date(),
    val isRead: Boolean = false,
    val actionData: String? = null,
    val imageUrl: String? = null
) 