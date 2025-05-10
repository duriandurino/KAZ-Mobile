package com.donate.chillinnmobile.model

import java.util.Date

/**
 * Image class representing images for rooms
 */
data class Image(
    val id: String,
    val url: String,
    val caption: String? = null,
    val description: String? = null,
    val roomId: String,
    val isPrimary: Boolean = false,
    val uploadedAt: Date = Date()
) 