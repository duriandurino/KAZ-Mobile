package com.donate.chillinnmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.adapters.NearbyAttractionAdapter
import com.donate.chillinnmobile.databinding.FragmentNearbyAttractionsBinding
import com.donate.chillinnmobile.model.NearbyAttraction
import com.donate.chillinnmobile.viewmodel.LocationViewModel
import com.google.android.material.chip.Chip

class NearbyAttractionsFragment : Fragment() {

    private var _binding: FragmentNearbyAttractionsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: LocationViewModel by viewModels()
    private lateinit var attractionAdapter: NearbyAttractionAdapter
    
    // Default hotel ID - in a real app, this would come from navigation args
    private val hotelId = "hotel1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNearbyAttractionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        // Setup recycler view
        setupRecyclerView()
        
        // Setup filter controls
        setupFilterControls()
        
        // Setup observers
        setupObservers()
        
        // Load attractions
        viewModel.fetchNearbyAttractions(hotelId)
    }
    
    private fun setupRecyclerView() {
        attractionAdapter = NearbyAttractionAdapter(
            onItemClick = { attraction ->
                // Navigate to attraction detail or show on map
                findNavController().navigate(
                    R.id.action_nearbyAttractionsFragment_to_hotelMapFragment,
                    Bundle().apply {
                        putString("attractionId", attraction.id)
                    }
                )
            },
            onDirectionsClick = { attraction ->
                // Open directions in Google Maps or another map app
                // For example: MapUtils.openDirections(requireContext(), attraction.latitude, attraction.longitude)
            }
        )
        
        binding.recyclerViewAttractions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = attractionAdapter
        }
    }
    
    private fun setupFilterControls() {
        // Setup radius slider
        binding.radiusSlider.addOnChangeListener { _, value, _ ->
            binding.tvRadius.text = "${value.toInt()} km"
            viewModel.setSearchRadius(value.toDouble())
        }
        
        // Setup sort options
        binding.chipDistance.setOnClickListener {
            sortAttractionsByDistance()
        }
        
        binding.chipRating.setOnClickListener {
            sortAttractionsByRating()
        }
        
        // Setup apply filter button
        binding.btnApplyFilter.setOnClickListener {
            applyFilters()
        }
    }
    
    private fun setupObservers() {
        // Observe attractions
        viewModel.nearbyAttractions.observe(viewLifecycleOwner) { attractions ->
            updateAttractionsList(attractions)
            updateCategoryChips(attractions)
            
            // Show empty state if needed
            binding.emptyState.visibility = if (attractions.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewAttractions.visibility = if (attractions.isEmpty()) View.GONE else View.VISIBLE
        }
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            
            if (isLoading) {
                binding.emptyState.visibility = View.GONE
            }
        }
        
        // Observe error state
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun updateAttractionsList(attractions: List<NearbyAttraction>) {
        attractionAdapter.submitList(attractions)
        
        // Update count
        binding.tvAttractionCount.text = "Showing ${attractions.size} attractions"
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
    
    private fun applyFilters() {
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
    }
    
    private fun sortAttractionsByDistance() {
        binding.chipDistance.isChecked = true
        binding.chipRating.isChecked = false
        
        // Get the current attractions list and sort by distance
        viewModel.nearbyAttractions.value?.let { attractions ->
            val sortedList = attractions.sortedBy { it.distanceFromHotel }
            updateAttractionsList(sortedList)
        }
    }
    
    private fun sortAttractionsByRating() {
        binding.chipDistance.isChecked = false
        binding.chipRating.isChecked = true
        
        // Get the current attractions list and sort by rating
        viewModel.nearbyAttractions.value?.let { attractions ->
            val sortedList = attractions.sortedByDescending { it.rating }
            updateAttractionsList(sortedList)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 