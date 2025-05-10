package com.donate.chillinnmobile.model

/**
 * Service class representing additional hotel services
 */
data class Service(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: ServiceCategory,
    val icon: String? = null,
    val available: Boolean = true
)

/**
 * Enum representing different service categories
 */
enum class ServiceCategory {
    FOOD,
    TRANSPORTATION,
    SPA,
    LAUNDRY,
    ENTERTAINMENT,
    BUSINESS,
    OTHER
} 