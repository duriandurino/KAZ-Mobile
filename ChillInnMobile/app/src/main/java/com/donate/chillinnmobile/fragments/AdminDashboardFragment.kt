package com.donate.chillinnmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.utils.showToast
import com.donate.chillinnmobile.viewmodel.AdminViewModel

/**
 * Fragment for admin dashboard functionality
 */
class AdminDashboardFragment : Fragment() {

    private lateinit var adminViewModel: AdminViewModel
    
    // UI components
    private lateinit var totalRoomsTextView: TextView
    private lateinit var availableRoomsTextView: TextView
    private lateinit var occupiedRoomsTextView: TextView
    private lateinit var totalBookingsTextView: TextView
    private lateinit var todayCheckInsTextView: TextView
    private lateinit var todayCheckOutsTextView: TextView
    private lateinit var revenueTextView: TextView
    
    private lateinit var manageRoomsButton: Button
    private lateinit var manageBookingsButton: Button
    private lateinit var manageUsersButton: Button
    private lateinit var reportButton: Button
    
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false)
        
        // Initialize ViewModel
        adminViewModel = ViewModelProvider(requireActivity()).get(AdminViewModel::class.java)
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up click listeners
        setupClickListeners()
        
        // Observe dashboard data
        observeDashboardData()
        
        // Load dashboard data
        loadDashboardData()
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this fragment
        loadDashboardData()
    }
    
    private fun initializeViews(view: View) {
        totalRoomsTextView = view.findViewById(R.id.total_rooms_text_view)
        availableRoomsTextView = view.findViewById(R.id.available_rooms_text_view)
        occupiedRoomsTextView = view.findViewById(R.id.occupied_rooms_text_view)
        totalBookingsTextView = view.findViewById(R.id.total_bookings_text_view)
        todayCheckInsTextView = view.findViewById(R.id.today_check_ins_text_view)
        todayCheckOutsTextView = view.findViewById(R.id.today_check_outs_text_view)
        revenueTextView = view.findViewById(R.id.revenue_text_view)
        
        manageRoomsButton = view.findViewById(R.id.manage_rooms_button)
        manageBookingsButton = view.findViewById(R.id.manage_bookings_button)
        manageUsersButton = view.findViewById(R.id.manage_users_button)
        reportButton = view.findViewById(R.id.report_button)
        
        progressBar = view.findViewById(R.id.progress_bar)
    }
    
    private fun setupClickListeners() {
        manageRoomsButton.setOnClickListener {
            navigateToManageRooms()
        }
        
        manageBookingsButton.setOnClickListener {
            navigateToManageBookings()
        }
        
        manageUsersButton.setOnClickListener {
            navigateToManageUsers()
        }
        
        reportButton.setOnClickListener {
            generateReport()
        }
    }
    
    private fun observeDashboardData() {
        adminViewModel.dashboardData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                populateDashboardData(data)
            }
        }
        
        adminViewModel.isLoadingDashboard.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        adminViewModel.dashboardError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                showToast(error)
            }
        }
    }
    
    private fun loadDashboardData() {
        adminViewModel.getDashboardData()
    }
    
    private fun populateDashboardData(data: Map<String, Any>) {
        // Populate dashboard data from API response
        totalRoomsTextView.text = "Total Rooms: ${data["totalRooms"]}"
        availableRoomsTextView.text = "Available: ${data["availableRooms"]}"
        occupiedRoomsTextView.text = "Occupied: ${data["occupiedRooms"]}"
        totalBookingsTextView.text = "Total Bookings: ${data["totalBookings"]}"
        todayCheckInsTextView.text = "Today's Check-ins: ${data["todayCheckIns"]}"
        todayCheckOutsTextView.text = "Today's Check-outs: ${data["todayCheckOuts"]}"
        
        // Format revenue as currency
        val revenue = data["revenue"] as Double
        revenueTextView.text = "Revenue: ${revenue.toString()}"
    }
    
    private fun navigateToManageRooms() {
        val action = AdminDashboardFragmentDirections.actionAdminDashboardFragmentToManageRoomsFragment()
        findNavController().navigate(action)
    }
    
    private fun navigateToManageBookings() {
        // In a real app, navigate to booking management screen
        showToast("Navigate to Booking Management")
    }
    
    private fun navigateToManageUsers() {
        // In a real app, navigate to user management screen
        showToast("Navigate to User Management")
    }
    
    private fun generateReport() {
        // In a real app, generate and download report
        showToast("Generating report...")
        adminViewModel.generateReport()
    }
} 