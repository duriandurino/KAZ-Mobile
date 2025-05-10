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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.adapters.RoomAdapter
import com.donate.chillinnmobile.model.Room
import com.donate.chillinnmobile.model.RoomFilterModel
import com.donate.chillinnmobile.utils.FilterDialogHelper
import com.donate.chillinnmobile.utils.showToast
import com.donate.chillinnmobile.viewmodel.AdminViewModel
import com.donate.chillinnmobile.viewmodel.RoomViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragment for managing hotel rooms (admin functionality)
 */
class ManageRoomsFragment : Fragment() {

    private lateinit var adminViewModel: AdminViewModel
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var roomAdapter: RoomAdapter
    
    // Filter values
    private var currentFilter: RoomFilterModel = RoomFilterModel.DEFAULT
    
    // UI components
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var emptyStateTextView: TextView
    private lateinit var addRoomFab: FloatingActionButton
    private lateinit var filterButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_rooms, container, false)
        
        // Initialize ViewModels
        adminViewModel = ViewModelProvider(requireActivity()).get(AdminViewModel::class.java)
        roomViewModel = ViewModelProvider(requireActivity()).get(RoomViewModel::class.java)
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up RecyclerView
        setupRecyclerView()
        
        // Set up click listeners
        setupClickListeners()
        
        // Observe room data
        observeRoomData()
        
        // Load rooms
        loadRooms()
        
        return view
    }
    
    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.rooms_recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)
        errorTextView = view.findViewById(R.id.error_text_view)
        emptyStateTextView = view.findViewById(R.id.empty_state_text_view)
        addRoomFab = view.findViewById(R.id.add_room_fab)
        filterButton = view.findViewById(R.id.filter_button)
    }
    
    private fun setupRecyclerView() {
        // We'll reuse the RoomAdapter but with long click actions for admin functions
        roomAdapter = RoomAdapter { room ->
            // Show options dialog for the selected room
            showRoomOptionsDialog(room)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = roomAdapter
        }
    }
    
    private fun setupClickListeners() {
        addRoomFab.setOnClickListener {
            navigateToAddEditRoom()
        }
        
        filterButton.setOnClickListener {
            showFilterDialog()
        }
    }
    
    private fun observeRoomData() {
        roomViewModel.rooms.observe(viewLifecycleOwner) { rooms ->
            if (rooms.isNullOrEmpty()) {
                emptyStateTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyStateTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                roomAdapter.submitList(rooms)
            }
        }
        
        roomViewModel.isLoadingRooms.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        roomViewModel.roomsError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = error
                recyclerView.visibility = View.GONE
            } else {
                errorTextView.visibility = View.GONE
            }
        }
        
        adminViewModel.roomOperationStatus.observe(viewLifecycleOwner) { (success, errorMessage, isLoading) ->
            if (!isLoading) {
                if (success) {
                    // Operation successful
                    showToast("Room operation completed successfully")
                    loadRooms() // Refresh room list
                } else if (errorMessage != null) {
                    // Show error message
                    showToast(errorMessage)
                }
            }
        }
    }
    
    private fun loadRooms() {
        // Load rooms with current filter
        roomViewModel.getRooms(
            priceMin = if (currentFilter.minPrice > 0) currentFilter.minPrice else null,
            priceMax = if (currentFilter.maxPrice < 1000) currentFilter.maxPrice else null,
            roomTypes = if (currentFilter.roomTypes.isNotEmpty()) currentFilter.roomTypes else null,
            amenities = if (currentFilter.amenities.isNotEmpty()) currentFilter.amenities else null,
            minRating = if (currentFilter.minRating > 0) currentFilter.minRating else null
        )
    }
    
    private fun showRoomOptionsDialog(room: Room) {
        val options = arrayOf("Edit Room", "Change Status", "Delete Room")
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Room Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> navigateToAddEditRoom(room.id) // Edit room
                    1 -> showChangeStatusDialog(room) // Change status
                    2 -> showDeleteConfirmationDialog(room) // Delete room
                }
            }
            .show()
    }
    
    private fun navigateToAddEditRoom(roomId: String? = null) {
        val action = if (roomId != null) {
            ManageRoomsFragmentDirections.actionManageRoomsFragmentToAddEditRoomFragment(roomId)
        } else {
            ManageRoomsFragmentDirections.actionManageRoomsFragmentToAddEditRoomFragment(null)
        }
        findNavController().navigate(action)
    }
    
    private fun showChangeStatusDialog(room: Room) {
        val statuses = com.donate.chillinnmobile.model.RoomStatus.values().map { it.name }.toTypedArray()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Change Room Status")
            .setItems(statuses) { _, which ->
                val newStatus = com.donate.chillinnmobile.model.RoomStatus.values()[which]
                adminViewModel.updateRoomStatus(room.id, newStatus)
            }
            .show()
    }
    
    private fun showDeleteConfirmationDialog(room: Room) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Room")
            .setMessage("Are you sure you want to delete Room #${room.roomNumber}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                adminViewModel.deleteRoom(room.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showFilterDialog() {
        FilterDialogHelper.showFilterDialog(
            requireContext(),
            currentFilter
        ) { newFilter ->
            currentFilter = newFilter
            loadRooms()
        }
    }
} 