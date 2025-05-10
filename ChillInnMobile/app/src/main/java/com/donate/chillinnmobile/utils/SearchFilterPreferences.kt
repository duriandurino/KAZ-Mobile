package com.donate.chillinnmobile.utils

import android.content.Context
import android.content.SharedPreferences
import com.donate.chillinnmobile.model.RoomFilterModel
import org.json.JSONArray
import org.json.JSONObject

/**
 * Utility for saving and retrieving search filter preferences
 */
class SearchFilterPreferences(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "search_filter_prefs"
        private const val KEY_FILTER_ENABLED = "filter_persistence_enabled"
        private const val KEY_LAST_FILTER = "last_filter"
        private const val DEFAULT_ENABLED = true
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    
    /**
     * Check if filter persistence is enabled
     */
    fun isFilterPersistenceEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_FILTER_ENABLED, DEFAULT_ENABLED)
    }
    
    /**
     * Enable or disable filter persistence
     */
    fun setFilterPersistenceEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_FILTER_ENABLED, enabled).apply()
    }
    
    /**
     * Save filter to preferences
     */
    fun saveFilter(filter: RoomFilterModel) {
        if (!isFilterPersistenceEnabled()) return
        
        val filterJson = JSONObject().apply {
            put("minPrice", filter.minPrice)
            put("maxPrice", filter.maxPrice)
            
            // Save room types
            val roomTypesArray = JSONArray()
            filter.selectedRoomTypes?.forEach { roomTypesArray.put(it) }
            put("roomTypes", roomTypesArray)
            
            // Save amenities
            val amenitiesArray = JSONArray()
            filter.selectedAmenities?.forEach { amenitiesArray.put(it) }
            put("amenities", amenitiesArray)
            
            put("minRating", filter.minRating)
            put("capacity", filter.capacity)
            put("sortBy", filter.sortBy)
            put("sortOrder", filter.sortOrder)
        }
        
        sharedPreferences.edit().putString(KEY_LAST_FILTER, filterJson.toString()).apply()
    }
    
    /**
     * Load last saved filter from preferences
     * @return The last saved filter or null if none exists
     */
    fun loadLastFilter(): RoomFilterModel? {
        if (!isFilterPersistenceEnabled()) return null
        
        val filterJson = sharedPreferences.getString(KEY_LAST_FILTER, null) ?: return null
        
        return try {
            val json = JSONObject(filterJson)
            
            // Load room types
            val roomTypesArray = json.optJSONArray("roomTypes")
            val roomTypes = if (roomTypesArray != null) {
                List(roomTypesArray.length()) { roomTypesArray.getString(it) }
            } else null
            
            // Load amenities
            val amenitiesArray = json.optJSONArray("amenities")
            val amenities = if (amenitiesArray != null) {
                List(amenitiesArray.length()) { amenitiesArray.getString(it) }
            } else null
            
            RoomFilterModel(
                minPrice = json.optDouble("minPrice", 0.0),
                maxPrice = json.optDouble("maxPrice", 1000.0),
                selectedRoomTypes = roomTypes,
                selectedAmenities = amenities,
                minRating = json.optFloat("minRating", 0f),
                capacity = json.optInt("capacity", 0),
                sortBy = json.optString("sortBy", "price"),
                sortOrder = json.optString("sortOrder", "asc")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Clear saved filter
     */
    fun clearSavedFilter() {
        sharedPreferences.edit().remove(KEY_LAST_FILTER).apply()
    }
} 