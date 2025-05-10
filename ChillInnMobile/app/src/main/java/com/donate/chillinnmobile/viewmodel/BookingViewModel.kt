package com.donate.chillinnmobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donate.chillinnmobile.model.Booking
import com.donate.chillinnmobile.model.Payment
import com.donate.chillinnmobile.repository.BookingRepository
import com.donate.chillinnmobile.utils.SessionManager
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel for booking-related operations
 */
class BookingViewModel : ViewModel() {
    
    private val bookingRepository = BookingRepository()
    
    // LiveData for user's bookings
    private val _userBookings = MutableLiveData<List<Booking>?>()
    val userBookings: LiveData<List<Booking>?> = _userBookings
    
    // LiveData for loading state
    private val _isLoadingBookings = MutableLiveData<Boolean>(false)
    val isLoadingBookings: LiveData<Boolean> = _isLoadingBookings
    
    // LiveData for error state
    private val _bookingsError = MutableLiveData<String?>(null)
    val bookingsError: LiveData<String?> = _bookingsError
    
    // LiveData for single booking details
    private val _bookingDetails = MutableLiveData<Booking?>()
    val bookingDetails: LiveData<Booking?> = _bookingDetails
    
    // LiveData for booking operations status
    private val _bookingOperationStatus = MutableLiveData<Triple<Boolean, String?, Boolean>>() // Success, Error message, IsLoading
    val bookingOperationStatus: LiveData<Triple<Boolean, String?, Boolean>> = _bookingOperationStatus
    
    // LiveData for payment status
    private val _paymentStatus = MutableLiveData<Triple<Boolean, String?, Boolean>>() // Success, Error message, IsLoading
    val paymentStatus: LiveData<Triple<Boolean, String?, Boolean>> = _paymentStatus
    
    // LiveData for payment details
    private val _paymentDetails = MutableLiveData<Payment?>()
    val paymentDetails: LiveData<Payment?> = _paymentDetails
    
    /**
     * Get all bookings for the current user
     */
    fun getUserBookings() {
        if (!SessionManager.isLoggedIn()) return
        
        _isLoadingBookings.value = true
        _bookingsError.value = null
        
        viewModelScope.launch {
            val result = bookingRepository.getUserBookings()
            
            if (result != null) {
                _userBookings.postValue(result)
            } else {
                _bookingsError.postValue("Failed to load bookings. Please try again.")
            }
            
            _isLoadingBookings.postValue(false)
        }
    }
    
    /**
     * Get details for a specific booking
     */
    fun getBookingDetails(bookingId: String) {
        if (!SessionManager.isLoggedIn()) return
        
        viewModelScope.launch {
            val result = bookingRepository.getBookingDetails(bookingId)
            _bookingDetails.postValue(result)
        }
    }
    
    /**
     * Create a new booking
     */
    fun createBooking(
        roomId: String,
        checkInDate: Date,
        checkOutDate: Date,
        numberOfGuests: Int,
        specialRequests: String? = null
    ) {
        if (!SessionManager.isLoggedIn()) {
            _bookingOperationStatus.value = Triple(false, "Please login to book a room.", false)
            return
        }
        
        _bookingOperationStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            val booking = bookingRepository.createBooking(
                roomId, checkInDate, checkOutDate, numberOfGuests, specialRequests
            )
            
            if (booking != null) {
                _bookingOperationStatus.postValue(Triple(true, null, false))
                _bookingDetails.postValue(booking)
                // Refresh user bookings
                getUserBookings()
            } else {
                _bookingOperationStatus.postValue(Triple(false, "Failed to create booking", false))
            }
        }
    }
    
    /**
     * Update an existing booking
     */
    fun updateBooking(
        bookingId: String,
        checkInDate: Date? = null,
        checkOutDate: Date? = null,
        numberOfGuests: Int? = null,
        specialRequests: String? = null
    ) {
        if (!SessionManager.isLoggedIn()) return
        
        _bookingOperationStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            val updatedBooking = bookingRepository.updateBooking(
                bookingId, checkInDate, checkOutDate, numberOfGuests, specialRequests
            )
            
            if (updatedBooking != null) {
                _bookingOperationStatus.postValue(Triple(true, null, false))
                _bookingDetails.postValue(updatedBooking)
                // Refresh user bookings
                getUserBookings()
            } else {
                _bookingOperationStatus.postValue(Triple(false, "Failed to update booking", false))
            }
        }
    }
    
    /**
     * Cancel a booking
     */
    fun cancelBooking(bookingId: String) {
        if (!SessionManager.isLoggedIn()) return
        
        _bookingOperationStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            val success = bookingRepository.cancelBooking(bookingId)
            
            if (success) {
                _bookingOperationStatus.postValue(Triple(true, null, false))
                // Refresh user bookings
                getUserBookings()
            } else {
                _bookingOperationStatus.postValue(Triple(false, "Failed to cancel booking", false))
            }
        }
    }
    
    /**
     * Process payment for a booking
     */
    fun processPayment(
        bookingId: String,
        amount: Double,
        paymentMethod: String,
        paymentDetails: Map<String, Any>
    ) {
        if (!SessionManager.isLoggedIn()) return
        
        _paymentStatus.value = Triple(false, null, true) // Start loading
        
        viewModelScope.launch {
            val payment = bookingRepository.processPayment(
                bookingId, amount, paymentMethod, paymentDetails
            )
            
            if (payment != null) {
                _paymentStatus.postValue(Triple(true, null, false))
                _paymentDetails.postValue(payment)
                // Refresh booking details to show updated payment status
                getBookingDetails(bookingId)
            } else {
                _paymentStatus.postValue(Triple(false, "Payment processing failed", false))
            }
        }
    }
    
    /**
     * Get payment details
     */
    fun getPaymentDetails(paymentId: String) {
        if (!SessionManager.isLoggedIn()) return
        
        viewModelScope.launch {
            val payment = bookingRepository.getPaymentDetails(paymentId)
            _paymentDetails.postValue(payment)
        }
    }
} 