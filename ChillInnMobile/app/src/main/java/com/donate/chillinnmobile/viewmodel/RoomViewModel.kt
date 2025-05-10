package com.donate.chillinnmobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donate.chillinnmobile.model.Review
import com.donate.chillinnmobile.model.Room
import com.donate.chillinnmobile.repository.RoomRepository
import com.donate.chillinnmobile.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * ViewModel for room-related operations
 */
class RoomViewModel : ViewModel() {
    
    private val roomRepository = RoomRepository()
    
    // LiveData for all rooms
    private val _rooms = MutableLiveData<List<Room>?>()
    val rooms: LiveData<List<Room>?> = _rooms
    
    // LiveData for room loading state
    private val _isLoadingRooms = MutableLiveData<Boolean>(false)
    val isLoadingRooms: LiveData<Boolean> = _isLoadingRooms
    
    // LiveData for room error state
    private val _roomsError = MutableLiveData<String?>(null)
    val roomsError: LiveData<String?> = _roomsError
    
    // LiveData for single room details
    private val _roomDetails = MutableLiveData<Room?>()
    val roomDetails: LiveData<Room?> = _roomDetails
    
    // LiveData for room details loading state
    private val _isLoadingRoomDetails = MutableLiveData<Boolean>(false)
    val isLoadingRoomDetails: LiveData<Boolean> = _isLoadingRoomDetails
    
    // LiveData for room reviews
    private val _roomReviews = MutableLiveData<List<Review>?>()
    val roomReviews: LiveData<List<Review>?> = _roomReviews
    
    // LiveData for CRUD operations status (for admin)
    private val _roomOperationStatus = MutableLiveData<Triple<Boolean, String?, Boolean>>() // Success, Error message, IsLoading
    val roomOperationStatus: LiveData<Triple<Boolean, String?, Boolean>> = _roomOperationStatus
    
    /**
     * Get all available rooms with optional filters
     */
    fun getRooms(
        checkInDate: String? = null,
        checkOutDate: String? = null,
        capacity: Int? = null,
        priceMin: Double? = null,
        priceMax: Double? = null,
        roomTypes: List<String>? = null,
        amenities: List<String>? = null,
        minRating: Float? = null
    ) {
        _isLoadingRooms.value = true
        _roomsError.value = null
        
        viewModelScope.launch {
            val result = roomRepository.getRooms(
                checkInDate, 
                checkOutDate, 
                capacity, 
                priceMin, 
                priceMax,
                roomTypes,
                amenities,
                minRating
            )
            
            if (result != null) {
                _rooms.postValue(result)
                _roomsError.postValue(null)
            } else {
                _roomsError.postValue("Failed to load rooms. Please try again.")
            }
            
            _isLoadingRooms.postValue(false)
        }
    }
    
    /**
     * Search for rooms by name, description, or features
     */
    fun searchRooms(query: String) {
        _isLoadingRooms.value = true
        _roomsError.value = null
        
        viewModelScope.launch {
            val result = roomRepository.searchRooms(query)
            
            if (result != null) {
                _rooms.postValue(result)
                _roomsError.postValue(null)
            } else {
                _roomsError.postValue("Failed to search rooms. Please try again.")
            }
            
            _isLoadingRooms.postValue(false)
        }
    }
    
    /**
     * Get room by ID - returns LiveData for easy observation
     */
    fun getRoomById(roomId: String): LiveData<Room?> {
        getRoomDetails(roomId)
        return roomDetails
    }
    
    /**
     * Get details for a specific room
     */
    fun getRoomDetails(roomId: String) {
        _isLoadingRoomDetails.value = true
        
        viewModelScope.launch {
            val result = roomRepository.getRoomDetails(roomId)
            _roomDetails.postValue(result)
            _isLoadingRoomDetails.postValue(false)
            
            // Also load reviews for this room
            loadRoomReviews(roomId)
        }
    }
    
    /**
     * Load reviews for a specific room
     */
    fun loadRoomReviews(roomId: String) {
        viewModelScope.launch {
            val reviews = roomRepository.getRoomReviews(roomId)
            _roomReviews.postValue(reviews)
        }
    }
    
    /**
     * Admin function: Create a new room
     */
    fun createRoom(roomData: Map<String, Any>) {
        if (!SessionManager.isAdmin()) {
            _roomOperationStatus.value = Triple(false, "Unauthorized. Admin access required.", false)
            return
        }
        
        _roomOperationStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            val result = roomRepository.createRoom(roomData)
            
            if (result != null) {
                _roomOperationStatus.postValue(Triple(true, null, false))
                // Refresh rooms list
                getRooms()
            } else {
                _roomOperationStatus.postValue(Triple(false, "Failed to create room", false))
            }
        }
    }
    
    /**
     * Admin function: Update an existing room
     */
    fun updateRoom(roomId: String, updateData: Map<String, Any>) {
        if (!SessionManager.isAdmin()) {
            _roomOperationStatus.value = Triple(false, "Unauthorized. Admin access required.", false)
            return
        }
        
        _roomOperationStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            val result = roomRepository.updateRoom(roomId, updateData)
            
            if (result != null) {
                _roomOperationStatus.postValue(Triple(true, null, false))
                _roomDetails.postValue(result)
                // Refresh rooms list
                getRooms()
            } else {
                _roomOperationStatus.postValue(Triple(false, "Failed to update room", false))
            }
        }
    }
    
    /**
     * Admin function: Delete a room
     */
    fun deleteRoom(roomId: String) {
        if (!SessionManager.isAdmin()) {
            _roomOperationStatus.value = Triple(false, "Unauthorized. Admin access required.", false)
            return
        }
        
        _roomOperationStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            val success = roomRepository.deleteRoom(roomId)
            
            if (success) {
                _roomOperationStatus.postValue(Triple(true, null, false))
                // Refresh rooms list
                getRooms()
            } else {
                _roomOperationStatus.postValue(Triple(false, "Failed to delete room", false))
            }
        }
    }
} 