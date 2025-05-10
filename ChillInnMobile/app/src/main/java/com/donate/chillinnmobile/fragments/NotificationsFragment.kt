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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.adapters.NotificationAdapter
import com.donate.chillinnmobile.model.Notification
import com.donate.chillinnmobile.model.NotificationType
import com.donate.chillinnmobile.utils.showToast
import com.donate.chillinnmobile.viewmodel.NotificationViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

/**
 * Fragment for displaying notifications
 */
class NotificationsFragment : Fragment() {

    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var notificationAdapter: NotificationAdapter
    
    // UI components
    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var emptyView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var markAllReadButton: Button
    private lateinit var testNotificationFab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        
        // Initialize ViewModel
        notificationViewModel = ViewModelProvider(requireActivity()).get(NotificationViewModel::class.java)
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up RecyclerView
        setupRecyclerView()
        
        // Set up listeners
        setupListeners()
        
        // Observe data
        observeViewModel()
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data each time the fragment becomes visible
        notificationViewModel.loadNotifications()
    }
    
    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.notifications_recycler_view)
        tabLayout = view.findViewById(R.id.tab_layout)
        emptyView = view.findViewById(R.id.empty_view)
        progressBar = view.findViewById(R.id.progress_bar)
        markAllReadButton = view.findViewById(R.id.mark_all_read_button)
        testNotificationFab = view.findViewById(R.id.test_notification_fab)
    }
    
    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter(
            requireContext(),
            onItemClick = { notification ->
                handleNotificationClick(notification)
            },
            onDeleteClick = { notification ->
                showDeleteConfirmation(notification)
            }
        )
        
        recyclerView.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }
    
    private fun setupListeners() {
        // Tab selection listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> notificationViewModel.loadNotifications() // All
                    1 -> notificationViewModel.loadNotificationsByType(NotificationType.BOOKING)
                    2 -> notificationViewModel.loadNotificationsByType(NotificationType.PROMOTION)
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        
        // Mark all as read button
        markAllReadButton.setOnClickListener {
            notificationViewModel.markAllAsRead()
        }
        
        // Test notification button (for development)
        testNotificationFab.setOnClickListener {
            showTestNotificationDialog()
        }
    }
    
    private fun observeViewModel() {
        // Observe notifications
        notificationViewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            notificationAdapter.submitList(notifications)
            
            // Show/hide empty view
            if (notifications.isEmpty()) {
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
        
        // Observe loading state
        notificationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observe error state
        notificationViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { showToast(it) }
        }
    }
    
    private fun handleNotificationClick(notification: Notification) {
        // Mark notification as read
        notificationViewModel.markAsRead(notification.id)
        
        // Handle navigation based on notification type and action data
        when (notification.type) {
            NotificationType.BOOKING -> {
                // Navigate to booking details if we have a booking ID
                notification.actionData?.let { bookingId ->
                    // Navigate to booking details
                    // findNavController().navigate(NotificationsFragmentDirections.actionNotificationsFragmentToBookingDetailsFragment(bookingId))
                    showToast("Navigate to booking: $bookingId")
                }
            }
            NotificationType.PROMOTION -> {
                // Handle promotion code
                notification.actionData?.let { promoCode ->
                    // Show dialog with promo code
                    showPromoCodeDialog(promoCode)
                }
            }
            NotificationType.GENERAL -> {
                // General notifications might not have specific actions
                // Just mark as read
            }
        }
    }
    
    private fun showDeleteConfirmation(notification: Notification) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Notification")
            .setMessage("Are you sure you want to delete this notification?")
            .setPositiveButton("Delete") { _, _ ->
                notificationViewModel.deleteNotification(notification.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showPromoCodeDialog(promoCode: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Promotion Code")
            .setMessage("Use code $promoCode for special discount!")
            .setPositiveButton("Copy Code") { _, _ ->
                // Copy to clipboard
                val clipboard = requireContext().getSystemService(android.content.ClipboardManager::class.java)
                val clip = android.content.ClipData.newPlainText("Promo Code", promoCode)
                clipboard.setPrimaryClip(clip)
                showToast("Code copied to clipboard")
            }
            .setNegativeButton("Close", null)
            .show()
    }
    
    private fun showTestNotificationDialog() {
        val items = arrayOf("Booking", "Promotion", "General")
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create Test Notification")
            .setItems(items) { _, which ->
                val type = when (which) {
                    0 -> NotificationType.BOOKING
                    1 -> NotificationType.PROMOTION
                    else -> NotificationType.GENERAL
                }
                notificationViewModel.createTestNotification(type)
            }
            .show()
    }
} 