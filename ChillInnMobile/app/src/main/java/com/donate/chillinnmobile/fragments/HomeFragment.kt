package com.donate.chillinnmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
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
import com.donate.chillinnmobile.viewmodel.RoomViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.RangeSlider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.kaz.chillinnmobile.model.Promotion
import com.kaz.chillinnmobile.ui.promotions.adapter.PromotionAdapter

/**
 * Home fragment displaying available rooms and search filters
 */
class HomeFragment : Fragment() {

    private lateinit var roomViewModel: RoomViewModel
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var promotionAdapter: PromotionAdapter
    
    // UI components
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var emptyStateTextView: TextView
    private lateinit var searchView: SearchView
    private lateinit var filterButton: Button
    private lateinit var checkInTextView: TextView
    private lateinit var checkOutTextView: TextView
    private lateinit var guestCountTextView: TextView
    private lateinit var priceRangeSlider: RangeSlider
    
    // Filter values
    private var checkInDate: Date? = null
    private var checkOutDate: Date? = null
    private var guestCount: Int = 1
    private var currentFilter: RoomFilterModel = RoomFilterModel.DEFAULT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        // Initialize ViewModel
        roomViewModel = ViewModelProvider(requireActivity()).get(RoomViewModel::class.java)
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up RecyclerView
        setupRecyclerView()
        
        // Set up search and filters
        setupSearchAndFilters()
        
        // Set up observers
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
        searchView = view.findViewById(R.id.search_view)
        filterButton = view.findViewById(R.id.filter_button)
        checkInTextView = view.findViewById(R.id.check_in_text_view)
        checkOutTextView = view.findViewById(R.id.check_out_text_view)
        guestCountTextView = view.findViewById(R.id.guest_count_text_view)
        priceRangeSlider = view.findViewById(R.id.price_range_slider)
    }
    
    private fun setupRecyclerView() {
        roomAdapter = RoomAdapter { room ->
            // Navigate to room details on item click
            val action = HomeFragmentDirections.actionHomeFragmentToRoomDetailsFragment(room.id)
            findNavController().navigate(action)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = roomAdapter
        }
    }
    
    private fun setupSearchAndFilters() {
        // Set up search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchRooms(query)
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    // Reset search when query is empty
                    loadRooms()
                }
                return true
            }
        })
        
        // Set up date pickers
        checkInTextView.setOnClickListener {
            showDatePicker(true)
        }
        
        checkOutTextView.setOnClickListener {
            showDatePicker(false)
        }
        
        // Set up guest count
        guestCountTextView.setOnClickListener {
            showGuestCountDialog()
        }
        
        // Set up price range slider
        priceRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            currentFilter = currentFilter.copy(
                minPrice = values[0].toDouble(),
                maxPrice = values[1].toDouble()
            )
        }
        
        // Set up filter button
        filterButton.setOnClickListener {
            showFilterDialog()
        }
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
    
    private fun showGuestCountDialog() {
        val guestCounts = (1..10).map { it.toString() }.toTypedArray()
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Number of Guests")
            .setItems(guestCounts) { _, which ->
                guestCount = which + 1 // +1 because array index starts at 0
                guestCountTextView.text = "$guestCount Guests"
                loadRooms()
            }
            .show()
    }
    
    private fun showDatePicker(isCheckIn: Boolean) {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        
        if (isCheckIn) {
            datePickerBuilder.setTitleText("Select Check-in Date")
        } else {
            datePickerBuilder.setTitleText("Select Check-out Date")
        }
        
        val datePicker = datePickerBuilder.build()
        
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Date(selection)
            if (isCheckIn) {
                checkInDate = selectedDate
                checkInTextView.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(selectedDate)
                
                // If check-out date is not set or is before check-in date, reset it
                if (checkOutDate == null || checkOutDate!! < checkInDate) {
                    val calendar = Calendar.getInstance()
                    calendar.time = selectedDate
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    checkOutDate = calendar.time
                    checkOutTextView.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(checkOutDate!!)
                }
            } else {
                checkOutDate = selectedDate
                checkOutTextView.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(selectedDate)
            }
            
            // Reload rooms with new dates
            loadRooms()
        }
        
        datePicker.show(requireActivity().supportFragmentManager, datePicker.toString())
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
    }
    
    private fun searchRooms(query: String?) {
        if (!query.isNullOrBlank()) {
            roomViewModel.searchRooms(query)
        } else {
            loadRooms()
        }
    }
    
    private fun loadRooms() {
        // Format dates for API
        val checkInString = checkInDate?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) }
        val checkOutString = checkOutDate?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) }
        
        // Load rooms with filters
        roomViewModel.getRooms(
            checkInDate = checkInString,
            checkOutDate = checkOutString,
            capacity = if (guestCount > 0) guestCount else null,
            priceMin = if (currentFilter.minPrice > 0) currentFilter.minPrice else null,
            priceMax = if (currentFilter.maxPrice < 1000) currentFilter.maxPrice else null,
            roomTypes = if (currentFilter.roomTypes.isNotEmpty()) currentFilter.roomTypes else null,
            amenities = if (currentFilter.amenities.isNotEmpty()) currentFilter.amenities else null,
            minRating = if (currentFilter.minRating > 0) currentFilter.minRating else null
        )
    }
} 