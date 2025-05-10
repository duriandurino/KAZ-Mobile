package com.donate.chillinnmobile.model

import java.util.Date

/**
 * AdminAction class representing actions performed by admins
 */
data class AdminAction(
    val id: String,
    val adminId: String,
    val actionType: ActionType,
    val description: String,
    val targetId: String? = null, // ID of entity affected by the action
    val timestamp: Date = Date()
)

/**
 * Enum representing different types of admin actions
 */
enum class ActionType {
    CREATE_ROOM,
    UPDATE_ROOM,
    DELETE_ROOM,
    APPROVE_BOOKING,
    REJECT_BOOKING,
    MODIFY_USER,
    OTHER
} 