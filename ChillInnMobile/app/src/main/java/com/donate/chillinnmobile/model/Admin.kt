package com.donate.chillinnmobile.model

import java.util.Date

/**
 * Admin class representing hotel administrators
 */
data class Admin(
    val user: User,
    val department: String,
    val role: String,
    val permissions: List<String>,
    val activityLog: List<AdminAction>? = null
) 