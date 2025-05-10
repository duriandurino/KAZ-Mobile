package com.donate.chillinnmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.adapters.BookingAdapter
import com.donate.chillinnmobile.model.Booking
import com.donate.chillinnmobile.utils.showToast
import com.donate.chillinnmobile.viewmodel.BookingViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment for displaying user's bookings
 */
class BookingsFragment : Fragment() {

    private lateinit var bookingViewModel: BookingViewModel
    private lateinit var bookingAdapter: BookingAdapter
    
    // UI components
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var emptyStateTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookings, container, false)
        
        // Initialize ViewModel
        bookingViewModel = ViewModelProvider(requireActivity()).get(BookingViewModel::class.java)
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up RecyclerView
        setupRecyclerView()
        
        // Observe data
        observeBookings()
        observeBookingOperations()
        
        // Load bookings
        loadBookings()
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh bookings when returning to this fragment
        loadBookings()
    }
    
    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.bookings_recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)
        errorTextView = view.findViewById(R.id.error_text_view)
        emptyStateTextView = view.findViewById(R.id.empty_state_text_view)
    }
    
    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(
            onItemClicked = { booking ->
                navigateToBookingDetails(booking)
            },
            onCancelClicked = { booking ->
                showCancelBookingDialog(booking)
            },
            onPaymentClicked = { booking ->
                navigateToPayment(booking)
            }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookingAdapter
        }
    }
    
    private fun observeBookings() {
        bookingViewModel.userBookings.observe(viewLifecycleOwner) { bookings ->
            if (bookings.isNullOrEmpty()) {
                emptyStateTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyStateTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                bookingAdapter.submitList(bookings)
            }
        }
        
        bookingViewModel.isLoadingBookings.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        bookingViewModel.bookingsError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = error
                recyclerView.visibility = View.GONE
            } else {
                errorTextView.visibility = View.GONE
            }
        }
    }
    
    private fun observeBookingOperations() {
        bookingViewModel.bookingOperationStatus.observe(viewLifecycleOwner) { (success, errorMessage, isLoading) ->
            if (!isLoading) {
                if (success) {
                    // Operation successful
                    showToast("Operation completed successfully")
                    loadBookings() // Refresh bookings
                } else if (errorMessage != null) {
                    // Show error message
                    showToast(errorMessage)
                }
            }
        }
    }
    
    private fun loadBookings() {
        bookingViewModel.getUserBookings()
    }
    
    private fun navigateToBookingDetails(booking: Booking) {
        val action = BookingsFragmentDirections.actionBookingsFragmentToBookingDetailsFragment(booking.id)
        findNavController().navigate(action)
    }
    
    private fun navigateToPayment(booking: Booking) {
        val action = BookingsFragmentDirections.actionBookingsFragmentToPaymentFragment(booking.id)
        findNavController().navigate(action)
    }
    
    private fun showCancelBookingDialog(booking: Booking) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel this booking?")
            .setPositiveButton("Yes") { _, _ ->
                bookingViewModel.cancelBooking(booking.id)
            }
            .setNegativeButton("No", null)
            .show()
    }
} 