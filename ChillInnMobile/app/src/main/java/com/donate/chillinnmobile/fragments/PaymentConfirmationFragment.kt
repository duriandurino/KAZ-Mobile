package com.donate.chillinnmobile.fragments

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Booking
import com.donate.chillinnmobile.model.Payment
import com.donate.chillinnmobile.utils.formatPrice
import com.donate.chillinnmobile.viewmodel.BookingViewModel
import com.donate.chillinnmobile.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Fragment for displaying payment confirmation details
 */
class PaymentConfirmationFragment : Fragment() {

    private val args: PaymentConfirmationFragmentArgs by navArgs()
    
    private lateinit var bookingViewModel: BookingViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    
    // UI components
    private lateinit var successIcon: ImageView
    private lateinit var transactionIdValue: TextView
    private lateinit var bookingIdValue: TextView
    private lateinit var dateValue: TextView
    private lateinit var paymentMethodValue: TextView
    private lateinit var amountValue: TextView
    private lateinit var roomInfoTextView: TextView
    private lateinit var bookingDatesTextView: TextView
    private lateinit var viewBookingsButton: Button
    private lateinit var backToHomeButton: Button
    
    // Data
    private var payment: Payment? = null
    private var booking: Booking? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment_confirmation, container, false)
        
        // Initialize ViewModels
        bookingViewModel = ViewModelProvider(requireActivity()).get(BookingViewModel::class.java)
        notificationViewModel = ViewModelProvider(requireActivity()).get(NotificationViewModel::class.java)
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up click listeners
        setupClickListeners()
        
        // Observe data
        observePaymentDetails()
        observeBookingDetails()
        
        // Load data
        if (args.paymentId.isNotEmpty()) {
            bookingViewModel.getPaymentDetails(args.paymentId)
        }
        
        // Start animation
        startSuccessAnimation()
        
        return view
    }
    
    private fun initializeViews(view: View) {
        successIcon = view.findViewById(R.id.success_icon)
        transactionIdValue = view.findViewById(R.id.transaction_id_value)
        bookingIdValue = view.findViewById(R.id.booking_id_value)
        dateValue = view.findViewById(R.id.date_value)
        paymentMethodValue = view.findViewById(R.id.payment_method_value)
        amountValue = view.findViewById(R.id.amount_value)
        roomInfoTextView = view.findViewById(R.id.room_info_text_view)
        bookingDatesTextView = view.findViewById(R.id.booking_dates_text_view)
        viewBookingsButton = view.findViewById(R.id.view_bookings_button)
        backToHomeButton = view.findViewById(R.id.back_to_home_button)
    }
    
    private fun setupClickListeners() {
        viewBookingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_paymentConfirmationFragment_to_bookingsFragment)
        }
        
        backToHomeButton.setOnClickListener {
            findNavController().navigate(R.id.action_paymentConfirmationFragment_to_homeFragment)
        }
    }
    
    private fun observePaymentDetails() {
        bookingViewModel.paymentDetails.observe(viewLifecycleOwner) { payment ->
            if (payment != null) {
                this.payment = payment
                populatePaymentDetails(payment)
                
                // Load booking details for this payment
                bookingViewModel.getBookingDetails(payment.bookingId)
            }
        }
    }
    
    private fun observeBookingDetails() {
        bookingViewModel.bookingDetails.observe(viewLifecycleOwner) { booking ->
            if (booking != null) {
                this.booking = booking
                populateBookingDetails(booking)
                
                // Create booking confirmation notification
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val checkInFormatted = dateFormat.format(booking.checkInDate)
                notificationViewModel.createBookingConfirmationNotification(
                    bookingId = booking.id,
                    roomName = "Deluxe Suite", // In a real app, this would come from the room data
                    checkInDate = checkInFormatted
                )
            }
        }
    }
    
    private fun populatePaymentDetails(payment: Payment) {
        // Format transaction ID
        transactionIdValue.text = payment.transactionId ?: "N/A"
        
        // Format booking ID
        bookingIdValue.text = payment.bookingId
        
        // Format date
        val dateFormat = SimpleDateFormat("MMM dd, yyyy - HH:mm:ss", Locale.getDefault())
        dateValue.text = dateFormat.format(payment.timestamp)
        
        // Format payment method
        val maskedCard = when {
            payment.paymentMethod.name.contains("CARD") -> {
                "**** **** **** " + (args.lastFourDigits.takeIf { it.isNotEmpty() } ?: "1234")
            }
            payment.paymentMethod.name == "PAYPAL" -> "PayPal"
            else -> payment.paymentMethod.name
        }
        paymentMethodValue.text = payment.paymentMethod.name.replace("_", " ") + 
                if (maskedCard.startsWith("*")) " ($maskedCard)" else ""
        
        // Format amount
        amountValue.text = payment.amount.formatPrice()
    }
    
    private fun populateBookingDetails(booking: Booking) {
        // Format room info
        roomInfoTextView.text = "Deluxe Suite - Room #101" // This would be populated with actual room details
        
        // Format dates
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val checkInFormatted = dateFormat.format(booking.checkInDate)
        val checkOutFormatted = dateFormat.format(booking.checkOutDate)
        bookingDatesTextView.text = "Check-in: $checkInFormatted\nCheck-out: $checkOutFormatted"
    }
    
    private fun startSuccessAnimation() {
        // Scale animation for success icon
        successIcon.scaleX = 0f
        successIcon.scaleY = 0f
        successIcon.alpha = 0f
        
        successIcon.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(500)
            .start()
    }
} 