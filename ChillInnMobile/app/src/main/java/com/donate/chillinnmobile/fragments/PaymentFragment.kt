package com.donate.chillinnmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Booking
import com.donate.chillinnmobile.model.Payment
import com.donate.chillinnmobile.model.PaymentMethod
import com.donate.chillinnmobile.utils.formatPrice
import com.donate.chillinnmobile.utils.showToast
import com.donate.chillinnmobile.viewmodel.BookingViewModel
import com.donate.chillinnmobile.viewmodel.NotificationViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID

/**
 * Fragment for processing payments
 */
class PaymentFragment : Fragment() {

    private val args: PaymentFragmentArgs by navArgs()
    
    private lateinit var bookingViewModel: BookingViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    
    // UI components
    private lateinit var bookingIdTextView: TextView
    private lateinit var roomInfoTextView: TextView
    private lateinit var bookingDatesTextView: TextView
    private lateinit var totalAmountTextView: TextView
    private lateinit var paymentMethodRadioGroup: RadioGroup
    private lateinit var creditCardRadioButton: RadioButton
    private lateinit var debitCardRadioButton: RadioButton
    private lateinit var paypalRadioButton: RadioButton
    
    private lateinit var cardNumberLayout: TextInputLayout
    private lateinit var cardNumberEditText: TextInputEditText
    private lateinit var cardholderNameLayout: TextInputLayout
    private lateinit var cardholderNameEditText: TextInputEditText
    private lateinit var expiryDateLayout: TextInputLayout
    private lateinit var expiryDateEditText: TextInputEditText
    private lateinit var cvvLayout: TextInputLayout
    private lateinit var cvvEditText: TextInputEditText
    
    private lateinit var processPaymentButton: Button
    private lateinit var cancelButton: Button
    private lateinit var progressBar: ProgressBar
    
    // Booking and payment data
    private var booking: Booking? = null
    private var selectedPaymentMethod: PaymentMethod = PaymentMethod.CREDIT_CARD
    // Store the last payment ID created
    private var lastPaymentId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)
        
        // Initialize ViewModels
        bookingViewModel = ViewModelProvider(requireActivity()).get(BookingViewModel::class.java)
        notificationViewModel = ViewModelProvider(requireActivity()).get(NotificationViewModel::class.java)
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up click listeners
        setupClickListeners()
        
        // Observe data
        observeBookingDetails()
        observePaymentStatus()
        
        // Load booking details
        loadBookingDetails()
        
        return view
    }
    
    private fun initializeViews(view: View) {
        bookingIdTextView = view.findViewById(R.id.booking_id_text_view)
        roomInfoTextView = view.findViewById(R.id.room_info_text_view)
        bookingDatesTextView = view.findViewById(R.id.booking_dates_text_view)
        totalAmountTextView = view.findViewById(R.id.total_amount_text_view)
        
        paymentMethodRadioGroup = view.findViewById(R.id.payment_method_radio_group)
        creditCardRadioButton = view.findViewById(R.id.credit_card_radio_button)
        debitCardRadioButton = view.findViewById(R.id.debit_card_radio_button)
        paypalRadioButton = view.findViewById(R.id.paypal_radio_button)
        
        cardNumberLayout = view.findViewById(R.id.card_number_layout)
        cardNumberEditText = view.findViewById(R.id.card_number_edit_text)
        cardholderNameLayout = view.findViewById(R.id.cardholder_name_layout)
        cardholderNameEditText = view.findViewById(R.id.cardholder_name_edit_text)
        expiryDateLayout = view.findViewById(R.id.expiry_date_layout)
        expiryDateEditText = view.findViewById(R.id.expiry_date_edit_text)
        cvvLayout = view.findViewById(R.id.cvv_layout)
        cvvEditText = view.findViewById(R.id.cvv_edit_text)
        
        processPaymentButton = view.findViewById(R.id.process_payment_button)
        cancelButton = view.findViewById(R.id.cancel_button)
        progressBar = view.findViewById(R.id.progress_bar)
    }
    
    private fun setupClickListeners() {
        paymentMethodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            handlePaymentMethodSelection(checkedId)
        }
        
        processPaymentButton.setOnClickListener {
            if (validatePaymentDetails()) {
                processPayment()
            }
        }
        
        cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun observeBookingDetails() {
        bookingViewModel.bookingDetails.observe(viewLifecycleOwner) { booking ->
            if (booking != null) {
                this.booking = booking
                populateBookingDetails(booking)
            }
        }
    }
    
    private fun observePaymentStatus() {
        bookingViewModel.paymentStatus.observe(viewLifecycleOwner) { (success, errorMessage, isLoading) ->
            if (isLoading) {
                showLoading(true)
            } else {
                showLoading(false)
                
                if (success) {
                    // Payment successful
                    showToast("Payment processed successfully!")
                    
                    // Store last 4 digits of card
                    val lastFourDigits = if (selectedPaymentMethod == PaymentMethod.CREDIT_CARD || 
                                            selectedPaymentMethod == PaymentMethod.DEBIT_CARD) {
                        val cardNumber = cardNumberEditText.text.toString().trim()
                        if (cardNumber.length >= 4) {
                            cardNumber.takeLast(4)
                        } else {
                            ""
                        }
                    } else {
                        ""
                    }
                    
                    // Create payment confirmation notification
                    val bookingId = booking?.id ?: args.bookingId
                    val totalAmount = booking?.totalPrice?.formatPrice() ?: "$0.00"
                    notificationViewModel.createPaymentConfirmationNotification(bookingId, totalAmount)
                    
                    // Navigate to confirmation screen if we have a payment ID
                    lastPaymentId?.let { paymentId ->
                        findNavController().navigate(
                            PaymentFragmentDirections.actionPaymentFragmentToPaymentConfirmationFragment(
                                paymentId = paymentId,
                                lastFourDigits = lastFourDigits
                            )
                        )
                    } ?: run {
                        // Fallback to bookings list if something went wrong
                        findNavController().navigate(R.id.action_paymentFragment_to_bookingsFragment)
                    }
                } else if (errorMessage != null) {
                    // Show error message
                    showToast(errorMessage)
                }
            }
        }
    }
    
    private fun loadBookingDetails() {
        val bookingId = args.bookingId
        bookingViewModel.getBookingDetails(bookingId)
    }
    
    private fun populateBookingDetails(booking: Booking) {
        bookingIdTextView.text = "Booking ID: ${booking.id}"
        
        // Format dates for display
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        val checkInFormatted = dateFormat.format(booking.checkInDate)
        val checkOutFormatted = dateFormat.format(booking.checkOutDate)
        
        roomInfoTextView.text = "Room information will be displayed here" // This would be populated with actual room details in a real app
        bookingDatesTextView.text = "Check-in: $checkInFormatted\nCheck-out: $checkOutFormatted"
        totalAmountTextView.text = "Total Amount: ${booking.totalPrice.formatPrice()}"
    }
    
    private fun handlePaymentMethodSelection(checkedId: Int) {
        when (checkedId) {
            R.id.credit_card_radio_button -> {
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD
                showCardFields(true)
            }
            R.id.debit_card_radio_button -> {
                selectedPaymentMethod = PaymentMethod.DEBIT_CARD
                showCardFields(true)
            }
            R.id.paypal_radio_button -> {
                selectedPaymentMethod = PaymentMethod.PAYPAL
                showCardFields(false)
            }
        }
    }
    
    private fun showCardFields(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        cardNumberLayout.visibility = visibility
        cardholderNameLayout.visibility = visibility
        expiryDateLayout.visibility = visibility
        cvvLayout.visibility = visibility
    }
    
    private fun validatePaymentDetails(): Boolean {
        if (booking == null) {
            showToast("Booking details not available.")
            return false
        }
        
        when (selectedPaymentMethod) {
            PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD -> {
                // Validate card details
                if (cardNumberEditText.text.isNullOrBlank()) {
                    cardNumberLayout.error = "Please enter card number"
                    return false
                } else {
                    cardNumberLayout.error = null
                }
                
                if (cardholderNameEditText.text.isNullOrBlank()) {
                    cardholderNameLayout.error = "Please enter cardholder name"
                    return false
                } else {
                    cardholderNameLayout.error = null
                }
                
                if (expiryDateEditText.text.isNullOrBlank()) {
                    expiryDateLayout.error = "Please enter expiry date"
                    return false
                } else {
                    expiryDateLayout.error = null
                }
                
                if (cvvEditText.text.isNullOrBlank()) {
                    cvvLayout.error = "Please enter CVV"
                    return false
                } else {
                    cvvLayout.error = null
                }
            }
            PaymentMethod.PAYPAL -> {
                // For PayPal, we would typically redirect to PayPal's site or app
                // For simplicity, we'll just accept it as is
            }
            else -> {
                // Other payment methods would have their own validation
            }
        }
        
        return true
    }
    
    private fun processPayment() {
        val booking = this.booking ?: return
        
        // Gather payment details based on selected method
        val paymentDetails = mutableMapOf<String, Any>()
        
        when (selectedPaymentMethod) {
            PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD -> {
                paymentDetails["cardNumber"] = cardNumberEditText.text.toString()
                paymentDetails["cardholderName"] = cardholderNameEditText.text.toString()
                paymentDetails["expiryDate"] = expiryDateEditText.text.toString()
                paymentDetails["cvv"] = cvvEditText.text.toString()
            }
            PaymentMethod.PAYPAL -> {
                // We would collect different details for PayPal
                paymentDetails["paypalEmail"] = "user@example.com" // This would be collected from the user
            }
            else -> {
                // Other payment methods would have their own details
            }
        }
        
        // Generate a mock payment ID for the confirmation screen
        // In a real app, this would come from the backend
        lastPaymentId = "PM-" + UUID.randomUUID().toString().substring(0, 8)
        
        // Add transaction ID to payment details for mock processing
        paymentDetails["transactionId"] = "TXN" + UUID.randomUUID().toString().substring(0, 8)
        
        // Process the payment
        bookingViewModel.processPayment(
            bookingId = booking.id,
            amount = booking.totalPrice,
            paymentMethod = selectedPaymentMethod.name,
            paymentDetails = paymentDetails
        )
    }
    
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            processPaymentButton.isEnabled = false
            cancelButton.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            processPaymentButton.isEnabled = true
            cancelButton.isEnabled = true
        }
    }
} 