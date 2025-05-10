package com.donate.chillinnmobile.model

/**
 * Model class for room filters with sorting options
 */
data class RoomFilterModel(
    val minPrice: Double = 0.0,
    val maxPrice: Double = 1000.0,
    val selectedRoomTypes: List<String>? = null,
    val selectedAmenities: List<String>? = null,
    val minRating: Float = 0f,
    val capacity: Int = 0,
    val sortBy: String = "price", // "price", "rating", "popularity"
    val sortOrder: String = "asc" // "asc", "desc"
) {
    companion object {
        // Sort options
        const val SORT_BY_PRICE = "price"
        const val SORT_BY_RATING = "rating"
        const val SORT_BY_POPULARITY = "popularity"
        
        // Sort order
        const val SORT_ORDER_ASC = "asc"
        const val SORT_ORDER_DESC = "desc"
        
        // Default filter values
        fun getDefaultFilter(): RoomFilterModel {
            return RoomFilterModel(
                minPrice = 0.0,
                maxPrice = 1000.0,
                selectedRoomTypes = null,
                selectedAmenities = null,
                minRating = 0f,
                capacity = 0,
                sortBy = SORT_BY_PRICE,
                sortOrder = SORT_ORDER_ASC
            )
        }
    }
    
    /**
     * Check if any filter is applied
     */
    fun hasActiveFilters(): Boolean {
        return minPrice > 0.0 ||
                maxPrice < 1000.0 ||
                !selectedRoomTypes.isNullOrEmpty() ||
                !selectedAmenities.isNullOrEmpty() ||
                minRating > 0f ||
                capacity > 0
    }
    
    /**
     * Create a copy with different sort parameters
     */
    fun withSort(sortBy: String, sortOrder: String = this.sortOrder): RoomFilterModel {
        return copy(sortBy = sortBy, sortOrder = sortOrder)
    }
    
    /**
     * Toggle sort order for the current sort parameter
     */
    fun toggleSortOrder(): RoomFilterModel {
        val newOrder = if (sortOrder == SORT_ORDER_ASC) SORT_ORDER_DESC else SORT_ORDER_ASC
        return copy(sortOrder = newOrder)
    }
} 