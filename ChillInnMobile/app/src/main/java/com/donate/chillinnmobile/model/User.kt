package com.donate.chillinnmobile.model

import java.util.Date

/**
 * User model representing app users
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val profileImageUrl: String? = null,
    val darkThemeEnabled: Boolean? = false,
    val notificationsEnabled: Boolean? = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Enum representing different types of users in the system
 */
enum class UserType {
    GUEST,
    ADMIN
} 