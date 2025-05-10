package com.donate.chillinnmobile.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.databinding.FragmentHotelMapBinding
import com.donate.chillinnmobile.model.HotelLocation
import com.donate.chillinnmobile.model.NearbyAttraction
import com.donate.chillinnmobile.viewmodel.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip

class HotelMapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHotelMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LocationViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var hotelMarker: Marker? = null
    private val attractionMarkers = mutableMapOf<String, Marker>()
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    
    // Default hotel ID - in a real app, this would come from navigation args
    private val hotelId = "hotel1"
    
    // Permission request launcher for location
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            enableMyLocation()
        } else {
            Toast.makeText(
                requireContext(),
                "Location permission is required to show your location on the map",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHotelMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        // Setup bottom sheet
        setupBottomSheet()
        
        // Setup filter chips
        setupFilterChips()
        
        // Setup radius slider
        setupRadiusSlider()
        
        // Setup observers
        setupObservers()
        
        // Load data
        viewModel.fetchHotelLocation(hotelId)
        viewModel.fetchNearbyAttractions(hotelId)
        
        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Configure map settings
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
            isRotateGesturesEnabled = true
            isTiltGesturesEnabled = true
        }
        
        // Check location permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        
        // Setup map click listener
        map.setOnMarkerClickListener { marker ->
            val attractionId = marker.tag as? String
            if (attractionId != null && attractionId != "hotel") {
                // Find attraction and show details
                viewModel.nearbyAttractions.value?.find { it.id == attractionId }?.let { attraction ->
                    showAttractionDetails(attraction)
                }
            }
            true
        }
        
        // If data is already loaded, update map
        viewModel.hotelLocation.value?.let { displayHotelOnMap(it) }
        viewModel.nearbyAttractions.value?.let { displayAttractionsOnMap(it) }
    }
    
    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        viewModel.selectAttraction(null)
                    }
                }
                
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // Optional: add custom behavior for sliding animation
                }
            })
        }
    }
    
    private fun setupFilterChips() {
        binding.btnFilter.setOnClickListener {
            binding.filterLayout.visibility = if (binding.filterLayout.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }
    
    private fun setupRadiusSlider() {
        binding.radiusSlider.addOnChangeListener { _, value, _ ->
            binding.tvRadius.text = "${value.toInt()} km"
            viewModel.setSearchRadius(value.toDouble())
        }
        
        binding.btnApplyFilter.setOnClickListener {
            // Get selected categories
            val selectedCategories = mutableListOf<String>()
            for (i in 0 until binding.chipGroupCategories.childCount) {
                val chip = binding.chipGroupCategories.getChildAt(i) as? Chip
                if (chip?.isChecked == true) {
                    selectedCategories.add(chip.text.toString())
                }
            }
            
            // Apply filters
            viewModel.setSelectedCategories(selectedCategories.ifEmpty { null })
            viewModel.fetchNearbyAttractions(hotelId)
            
            // Hide filter layout
            binding.filterLayout.visibility = View.GONE
        }
    }
    
    private fun setupObservers() {
        // Observe hotel location
        viewModel.hotelLocation.observe(viewLifecycleOwner) { location ->
            displayHotelOnMap(location)
            binding.hotelName.text = location.address
            binding.hotelAddress.text = "${location.city}, ${location.state}, ${location.country}"
        }
        
        // Observe nearby attractions
        viewModel.nearbyAttractions.observe(viewLifecycleOwner) { attractions ->
            displayAttractionsOnMap(attractions)
            updateCategoryChips(attractions)
        }
        
        // Observe selected attraction
        viewModel.selectedAttraction.observe(viewLifecycleOwner) { attraction ->
            if (attraction != null) {
                showAttractionDetails(attraction)
            } else {
                hideAttractionDetails()
            }
        }
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observe error state
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun displayHotelOnMap(hotelLocation: HotelLocation) {
        googleMap?.let { map ->
            // Clear existing hotel marker
            hotelMarker?.remove()
            
            // Create new marker
            val position = LatLng(hotelLocation.latitude, hotelLocation.longitude)
            hotelMarker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("Hotel")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            
            // Add tag to identify marker
            hotelMarker?.tag = "hotel"
            
            // Move camera to hotel location
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14f))
        }
    }
    
    private fun displayAttractionsOnMap(attractions: List<NearbyAttraction>) {
        googleMap?.let { map ->
            // Clear existing markers
            attractionMarkers.values.forEach { it.remove() }
            attractionMarkers.clear()
            
            // Add new markers
            attractions.forEach { attraction ->
                val position = LatLng(attraction.latitude, attraction.longitude)
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(attraction.name)
                        .snippet(attraction.category)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
                
                // Add tag to identify marker
                marker?.tag = attraction.id
                
                if (marker != null) {
                    attractionMarkers[attraction.id] = marker
                }
            }
        }
    }
    
    private fun updateCategoryChips(attractions: List<NearbyAttraction>) {
        // Get unique categories
        val categories = attractions.map { it.category }.distinct()
        
        // Clear existing chips
        binding.chipGroupCategories.removeAllViews()
        
        // Add new chips
        categories.forEach { category ->
            val chip = layoutInflater.inflate(
                R.layout.item_filter_chip,
                binding.chipGroupCategories,
                false
            ) as Chip
            
            chip.text = category
            binding.chipGroupCategories.addView(chip)
        }
    }
    
    private fun showAttractionDetails(attraction: NearbyAttraction) {
        // Update UI with attraction details
        binding.tvAttractionName.text = attraction.name
        binding.tvAttractionCategory.text = attraction.category
        binding.tvAttractionDescription.text = attraction.description
        binding.tvDistance.text = "${attraction.distanceFromHotel} km away"
        
        // Calculate and show estimated travel times
        val walkingTime = viewModel.getEstimatedTravelTime(attraction, "walking")
        val drivingTime = viewModel.getEstimatedTravelTime(attraction, "driving")
        binding.tvWalkingTime.text = "$walkingTime min"
        binding.tvDrivingTime.text = "$drivingTime min"
        
        // Opening hours
        binding.tvOpeningHours.text = attraction.openingHours ?: "Hours not available"
        
        // Show website button if available
        if (attraction.websiteUrl != null) {
            binding.btnWebsite.visibility = View.VISIBLE
            binding.btnWebsite.setOnClickListener {
                // Open website in browser (implementation depends on your util functions)
                // For example: IntentUtils.openUrl(requireContext(), attraction.websiteUrl)
            }
        } else {
            binding.btnWebsite.visibility = View.GONE
        }
        
        // Show bottom sheet
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        
        // Center map on this attraction
        googleMap?.let { map ->
            val position = LatLng(attraction.latitude, attraction.longitude)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
        }
    }
    
    private fun hideAttractionDetails() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }
    
    private fun enableMyLocation() {
        try {
            googleMap?.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            // Handle permission exception
        }
    }
    
    private fun getMarkerIconByCategory(category: String): BitmapDescriptor {
        return when (category.lowercase()) {
            "restaurant" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            "museum" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            "park" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            "shopping" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
            "landmark" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
            "entertainment" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
            else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 