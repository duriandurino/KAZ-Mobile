package com.donate.chillinnmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Booking
import com.donate.chillinnmobile.model.Room
import com.donate.chillinnmobile.utils.BookingValidationHelper
import com.donate.chillinnmobile.utils.DatePickerHelper
import com.donate.chillinnmobile.viewmodel.BookingViewModel
import com.donate.chillinnmobile.viewmodel.RoomViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

/**
 * Fragment for booking a room
 */
class BookingFragment : Fragment() {

    private val args: BookingFragmentArgs by navArgs()
    
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var bookingViewModel: BookingViewModel
    
    // UI components
    private lateinit var tilCheckIn: TextInputLayout
    private lateinit var tilCheckOut: TextInputLayout
    private lateinit var tilGuestCount: TextInputLayout
    private lateinit var etCheckIn: TextInputEditText
    private lateinit var etCheckOut: TextInputEditText
    private lateinit var actGuestCount: AutoCompleteTextView
    private lateinit var etSpecialRequests: TextInputEditText
    
    // Date variables
    private var checkInDate: Date? = null
    private var checkOutDate: Date? = null
    
    // Room data
    private var currentRoom: Room? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize ViewModels
        bookingViewModel = ViewModelProvider(this).get(BookingViewModel::class.java)
        roomViewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        
        // Initialize UI components
        initializeViews(view)
        
        // Load room data
        loadRoomData()
        
        // Set up date pickers
        setupDatePickers()
        
        // Set up guest count dropdown
        setupGuestCountDropdown()
        
        // Set up button listeners
        view.findViewById<View>(R.id.btnProceedToPayment).setOnClickListener {
            if (validateBookingForm()) {
                createBooking()
            }
        }
        
        // Observe booking creation result
        observeBookingCreation()
    }
    
    private fun initializeViews(view: View) {
        tilCheckIn = view.findViewById(R.id.tilCheckIn)
        tilCheckOut = view.findViewById(R.id.tilCheckOut)
        tilGuestCount = view.findViewById(R.id.tilGuestCount)
        etCheckIn = view.findViewById(R.id.etCheckIn)
        etCheckOut = view.findViewById(R.id.etCheckOut)
        actGuestCount = view.findViewById(R.id.actGuestCount)
        etSpecialRequests = view.findViewById(R.id.etSpecialRequests)
    }
    
    private fun loadRoomData() {
        // Load room details using the roomId from arguments
        roomViewModel.getRoomById(args.roomId).observe(viewLifecycleOwner) { room ->
            if (room != null) {
                currentRoom = room
                
                // Update UI with room details
                view?.findViewById<View>(R.id.tvRoomTitle)?.let {
                    it as android.widget.TextView
                    it.text = room.name
                }
                
                view?.findViewById<View>(R.id.tvRoomPrice)?.let {
                    it as android.widget.TextView
                    it.text = "$${room.pricePerNight}/night"
                }
                
                // Load room image
                view?.findViewById<View>(R.id.ivRoomImage)?.let {
                    it as android.widget.ImageView
                    // Use an image loading library like Glide or Picasso here
                    // For now, we'll just use a placeholder
                    it.setImageResource(R.drawable.placeholder_room)
                }
                
                // Update pricing summary when room is loaded
                updatePricingSummary()
                
                // Set up guest count dropdown with room capacity
                setupGuestCountDropdown(room.capacity)
            }
        }
    }
    
    private fun setupDatePickers() {
        // Get today's date and tomorrow as default values
        val today = DatePickerHelper.getToday()
        val tomorrow = DatePickerHelper.getTodayPlus(1)
        
        // Setup check-in date picker
        etCheckIn.setOnClickListener {
            DatePickerHelper.showDatePicker(
                requireContext(),
                etCheckIn,
                null, // Use current date as default
                today, // Minimum date is today
                null, // No maximum date for check-in
                object : DatePickerHelper.OnDateSelectedListener {
                    override fun onDateSelected(date: Date) {
                        checkInDate = date
                        
                        // If check-out date is before check-in date, reset it
                        if (checkOutDate != null && checkOutDate!!.before(checkInDate)) {
                            checkOutDate = DatePickerHelper.getTodayPlus(
                                DatePickerHelper.calculateNights(today, date) + 1
                            )
                            etCheckOut.setText(DatePickerHelper.formatDate(checkOutDate!!))
                        }
                        
                        // Validate dates and update UI
                        validateDates()
                        updatePricingSummary()
                    }
                }
            )
        }
        
        // Setup check-out date picker
        etCheckOut.setOnClickListener {
            // Determine minimum check-out date (day after check-in or tomorrow)
            val minCheckOutDate = if (checkInDate != null) {
                val nextDay = Calendar.getInstance()
                nextDay.time = checkInDate!!
                nextDay.add(Calendar.DAY_OF_MONTH, 1)
                nextDay.time
            } else {
                tomorrow
            }
            
            DatePickerHelper.showDatePicker(
                requireContext(),
                etCheckOut,
                null, // Use current check-out date as default
                minCheckOutDate, // Minimum is day after check-in
                null, // No maximum date for check-out
                object : DatePickerHelper.OnDateSelectedListener {
                    override fun onDateSelected(date: Date) {
                        checkOutDate = date
                        
                        // Validate dates and update UI
                        validateDates()
                        updatePricingSummary()
                    }
                }
            )
        }
    }
    
    private fun setupGuestCountDropdown(maxCapacity: Int = 10) {
        // Create array of guest count options (1 to maxCapacity)
        val maxGuests = maxCapacity.coerceAtMost(20) // Limit to 20 for UI reasons
        val guestCounts = (1..maxGuests).map { it.toString() }.toTypedArray()
        
        // Set up adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, guestCounts)
        actGuestCount.setAdapter(adapter)
        
        // Set default value
        actGuestCount.setText("2", false)
        
        // Listen for changes
        actGuestCount.setOnItemClickListener { _, _, _, _ ->
            validateGuestCount()
            updatePricingSummary()
        }
    }
    
    private fun updatePricingSummary() {
        if (checkInDate != null && checkOutDate != null && currentRoom != null) {
            try {
                // Calculate number of nights using DatePickerHelper
                val nights = DatePickerHelper.calculateNights(checkInDate!!, checkOutDate!!)
                
                // Calculate total cost using BookingValidationHelper
                val totalRoomRate = BookingValidationHelper.calculateTotalCost(
                    checkInDate!!, 
                    checkOutDate!!, 
                    currentRoom!!
                )
                
                // Calculate taxes (assumed to be 10% of room rate)
                val taxesFees = (totalRoomRate * 0.1)
                
                // Calculate total
                val totalPrice = totalRoomRate + taxesFees
                
                // Format values with proper decimal places
                val formattedRoomRate = String.format("%.2f", currentRoom!!.pricePerNight)
                val formattedTotalRoomRate = String.format("%.2f", totalRoomRate)
                val formattedTaxesFees = String.format("%.2f", taxesFees)
                val formattedTotalPrice = String.format("%.2f", totalPrice)
                
                // Update UI
                view?.findViewById<View>(R.id.tvRoomRate)?.let {
                    it as android.widget.TextView
                    it.text = "$${formattedRoomRate} × ${nights} nights = $${formattedTotalRoomRate}"
                }
                
                view?.findViewById<View>(R.id.tvTaxesFees)?.let {
                    it as android.widget.TextView
                    it.text = "$${formattedTaxesFees}"
                }
                
                view?.findViewById<View>(R.id.tvTotalPrice)?.let {
                    it as android.widget.TextView
                    it.text = "$${formattedTotalPrice}"
                }
                
                // Make pricing summary visible (might be hidden initially)
                view?.findViewById<View>(R.id.cardPriceSummary)?.visibility = View.VISIBLE
                
            } catch (e: Exception) {
                // In case of calculation errors, hide the pricing summary
                view?.findViewById<View>(R.id.cardPriceSummary)?.visibility = View.GONE
            }
        } else {
            // If dates or room are not set, hide the pricing summary
            view?.findViewById<View>(R.id.cardPriceSummary)?.visibility = View.GONE
        }
    }
    
    private fun validateDates(): Boolean {
        // Clear previous errors
        tilCheckIn.error = null
        tilCheckOut.error = null
        
        if (checkInDate == null) {
            tilCheckIn.error = getString(R.string.error_past_check_in_date)
            return false
        }
        
        if (checkOutDate == null) {
            tilCheckOut.error = getString(R.string.error_invalid_checkout_date)
            return false
        }
        
        // Use BookingValidationHelper for comprehensive date validation
        val validationResult = BookingValidationHelper.validateDates(
            checkInDate!!, 
            checkOutDate!!, 
            requireContext()
        )
        
        if (!validationResult.isValid) {
            // Determine which field has the error
            when {
                validationResult.errorMessage?.contains("check-in") == true -> {
                    tilCheckIn.error = validationResult.errorMessage
                }
                else -> {
                    tilCheckOut.error = validationResult.errorMessage
                }
            }
            return false
        }
        
        return true
    }
    
    private fun validateGuestCount(): Boolean {
        // Clear previous error
        tilGuestCount.error = null
        
        if (actGuestCount.text.isNullOrEmpty()) {
            tilGuestCount.error = getString(R.string.error_invalid_guest_count)
            return false
        }
        
        val guestCount = actGuestCount.text.toString().toIntOrNull() ?: 0
        if (currentRoom != null) {
            // Use BookingValidationHelper for guest count validation
            val validationResult = BookingValidationHelper.validateGuestCount(
                guestCount, 
                currentRoom!!, 
                requireContext()
            )
            
            if (!validationResult.isValid) {
                tilGuestCount.error = validationResult.errorMessage
                return false
            }
        } else if (guestCount <= 0) {
            tilGuestCount.error = getString(R.string.error_invalid_guest_count)
            return false
        }
        
        return true
    }
    
    private fun validateBookingForm(): Boolean {
        // Validate dates and guest count
        val datesValid = validateDates()
        val guestCountValid = validateGuestCount()
        
        return datesValid && guestCountValid
    }
    
    private fun createBooking() {
        if (currentRoom == null || checkInDate == null || checkOutDate == null) {
            return
        }
        
        val guestCount = actGuestCount.text.toString().toIntOrNull() ?: 2
        val specialRequests = etSpecialRequests.text.toString()
        
        // Create booking through ViewModel
        bookingViewModel.createBooking(
            roomId = currentRoom!!.id,
            checkInDate = checkInDate!!,
            checkOutDate = checkOutDate!!,
            numberOfGuests = guestCount,
            specialRequests = specialRequests.ifEmpty { null }
        )
    }
    
    private fun observeBookingCreation() {
        bookingViewModel.bookingOperationStatus.observe(viewLifecycleOwner) { (success, errorMessage, isLoading) ->
            if (isLoading) {
                // Show loading indicator
                view?.findViewById<View>(R.id.progressBar)?.visibility = View.VISIBLE
                view?.findViewById<View>(R.id.btnProceedToPayment)?.isEnabled = false
            } else {
                // Hide loading indicator
                view?.findViewById<View>(R.id.progressBar)?.visibility = View.GONE
                view?.findViewById<View>(R.id.btnProceedToPayment)?.isEnabled = true
                
                if (success) {
                    // Get the created booking
                    val booking = bookingViewModel.bookingDetails.value
                    
                    if (booking != null) {
                        // Navigate to payment screen with booking ID
                        val action = BookingFragmentDirections.actionBookingFragmentToPaymentFragment(booking.id)
                        findNavController().navigate(action)
                    }
                } else if (errorMessage != null) {
                    // Show error message
                    showError(errorMessage)
                }
            }
        }
    }
    
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
} 