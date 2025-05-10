package com.donate.chillinnmobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donate.chillinnmobile.model.HotelLocation
import com.donate.chillinnmobile.model.NearbyAttraction
import com.donate.chillinnmobile.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel to manage hotel location and nearby attractions data.
 */
class LocationViewModel : ViewModel() {
    
    private val locationRepository = LocationRepository()
    
    // Hotel location data
    private val _hotelLocation = MutableLiveData<HotelLocation>()
    val hotelLocation: LiveData<HotelLocation> = _hotelLocation
    
    // Nearby attractions data
    private val _nearbyAttractions = MutableLiveData<List<NearbyAttraction>>()
    val nearbyAttractions: LiveData<List<NearbyAttraction>> = _nearbyAttractions
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Selected attraction
    private val _selectedAttraction = MutableLiveData<NearbyAttraction?>()
    val selectedAttraction: LiveData<NearbyAttraction?> = _selectedAttraction
    
    // Filter parameters
    private val _searchRadius = MutableStateFlow(5.0)
    val searchRadius: StateFlow<Double> = _searchRadius
    
    private val _selectedCategories = MutableStateFlow<List<String>?>(null)
    val selectedCategories: StateFlow<List<String>?> = _selectedCategories
    
    /**
     * Fetch hotel location data.
     * 
     * @param hotelId ID of the hotel.
     */
    fun fetchHotelLocation(hotelId: String) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val location = locationRepository.getHotelLocation(hotelId)
                _hotelLocation.value = location
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load hotel location: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetch nearby attractions.
     * 
     * @param hotelId ID of the hotel.
     * @param radius Search radius in kilometers.
     * @param categories Optional list of categories to filter results.
     */
    fun fetchNearbyAttractions(
        hotelId: String,
        radius: Double = searchRadius.value,
        categories: List<String>? = selectedCategories.value
    ) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val attractions = locationRepository.getNearbyAttractions(
                    hotelId = hotelId,
                    radius = radius,
                    categories = categories
                )
                _nearbyAttractions.value = attractions
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load nearby attractions: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Set the search radius for nearby attractions.
     * 
     * @param radius Search radius in kilometers.
     */
    fun setSearchRadius(radius: Double) {
        _searchRadius.value = radius
    }
    
    /**
     * Set the categories filter for nearby attractions.
     * 
     * @param categories List of categories to filter by.
     */
    fun setSelectedCategories(categories: List<String>?) {
        _selectedCategories.value = categories
    }
    
    /**
     * Select an attraction to view details.
     * 
     * @param attraction Selected attraction.
     */
    fun selectAttraction(attraction: NearbyAttraction?) {
        _selectedAttraction.value = attraction
    }
    
    /**
     * Get all available attraction categories.
     * 
     * @return List of unique categories.
     */
    fun getAvailableCategories(): List<String> {
        return nearbyAttractions.value?.map { it.category }?.distinct() ?: emptyList()
    }
    
    /**
     * Calculate estimated travel time to a nearby attraction.
     * 
     * @param attraction The attraction to calculate travel time to.
     * @param transportMode Transport mode (walking, driving, etc.).
     * @return Estimated travel time in minutes.
     */
    fun getEstimatedTravelTime(attraction: NearbyAttraction, transportMode: String): Int {
        return locationRepository.estimateTravelTime(attraction.distanceFromHotel, transportMode)
    }
    
    /**
     * Clear any error message.
     */
    fun clearError() {
        _error.value = null
    }
} 