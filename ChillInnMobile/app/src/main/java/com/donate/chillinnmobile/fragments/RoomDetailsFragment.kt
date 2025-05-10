package com.donate.chillinnmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.adapters.AmenityAdapter
import com.donate.chillinnmobile.adapters.ImageCarouselAdapter
import com.donate.chillinnmobile.adapters.ReviewAdapter
import com.donate.chillinnmobile.model.Image
import com.donate.chillinnmobile.model.Room
import com.donate.chillinnmobile.utils.formatPrice
import com.donate.chillinnmobile.utils.loadImage
import com.donate.chillinnmobile.utils.showToast
import com.donate.chillinnmobile.viewmodel.RoomViewModel
import com.donate.chillinnmobile.viewmodel.ReviewViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Fragment for displaying detailed information about a room
 */
class RoomDetailsFragment : Fragment() {

    private val args: RoomDetailsFragmentArgs by navArgs()
    
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var amenityAdapter: AmenityAdapter
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var imageCarouselAdapter: ImageCarouselAdapter
    
    // UI components
    private lateinit var viewPagerImages: ViewPager2
    private lateinit var tabLayoutIndicator: TabLayout
    private lateinit var roomTypeTextView: TextView
    private lateinit var roomPriceTextView: TextView
    private lateinit var ratingValueTextView: TextView
    private lateinit var roomDescriptionTextView: TextView
    private lateinit var availabilityStatusTextView: TextView
    private lateinit var amenitiesRecyclerView: RecyclerView
    private lateinit var reviewsRecyclerView: RecyclerView
    private lateinit var backButton: ImageView
    private lateinit var favoriteButton: ImageView
    private lateinit var bookNowButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_room_details, container, false)
        
        // Initialize ViewModels
        roomViewModel = ViewModelProvider(requireActivity()).get(RoomViewModel::class.java)
        reviewViewModel = ViewModelProvider(requireActivity()).get(ReviewViewModel::class.java)
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up RecyclerViews and ViewPager
        setupRecyclerViews()
        setupImageCarousel()
        
        // Set up click listeners
        setupClickListeners()
        
        // Set up observers
        observeRoomData()
        observeReviewData()
        
        // Load room details
        loadRoomDetails()
        
        return view
    }
    
    private fun initializeViews(view: View) {
        viewPagerImages = view.findViewById(R.id.viewPagerImages)
        tabLayoutIndicator = view.findViewById(R.id.tabLayoutIndicator)
        roomTypeTextView = view.findViewById(R.id.tvRoomType)
        roomPriceTextView = view.findViewById(R.id.tvRoomPrice)
        ratingValueTextView = view.findViewById(R.id.tvRatingValue)
        roomDescriptionTextView = view.findViewById(R.id.tvRoomDescription)
        availabilityStatusTextView = view.findViewById(R.id.tvAvailabilityStatus)
        amenitiesRecyclerView = view.findViewById(R.id.rvAmenities)
        reviewsRecyclerView = view.findViewById(R.id.rvReviews)
        backButton = view.findViewById(R.id.ivBackButton)
        favoriteButton = view.findViewById(R.id.ivFavoriteButton)
        bookNowButton = view.findViewById(R.id.btnBookNow)
        progressBar = view.findViewById(R.id.progressBar)
        errorTextView = view.findViewById(R.id.tvError)
    }
    
    private fun setupRecyclerViews() {
        // Set up amenities RecyclerView
        amenityAdapter = AmenityAdapter()
        amenitiesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = amenityAdapter
        }
        
        // Set up reviews RecyclerView
        reviewAdapter = ReviewAdapter()
        reviewsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewAdapter
        }
    }
    
    private fun setupImageCarousel() {
        imageCarouselAdapter = ImageCarouselAdapter(emptyList()) { image ->
            // Handle image click - e.g., show full-screen image
            showToast("Viewing image: ${image.url}")
        }
        
        viewPagerImages.apply {
            adapter = imageCarouselAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        
        // Connect TabLayout with ViewPager2 for dots indicator
        TabLayoutMediator(tabLayoutIndicator, viewPagerImages) { _, _ ->
            // No configuration needed for dots indicator
        }.attach()
    }
    
    private fun setupClickListeners() {
        // Set up back button
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Set up favorite button
        favoriteButton.setOnClickListener {
            toggleFavorite()
        }
        
        // Set up Book Now button
        bookNowButton.setOnClickListener {
            navigateToBooking()
        }
    }
    
    private fun toggleFavorite() {
        // In a real app, this would toggle the favorite status in the database
        // For now, we'll just show a toast
        showToast("Added to favorites")
        favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
    }
    
    private fun observeRoomData() {
        roomViewModel.roomDetails.observe(viewLifecycleOwner) { room ->
            if (room != null) {
                populateRoomDetails(room)
            }
        }
        
        roomViewModel.isLoadingRoomDetails.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        roomViewModel.roomsError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = error
            } else {
                errorTextView.visibility = View.GONE
            }
        }
    }
    
    private fun observeReviewData() {
        roomViewModel.roomReviews.observe(viewLifecycleOwner) { reviews ->
            if (reviews.isNullOrEmpty()) {
                reviewsRecyclerView.visibility = View.GONE
            } else {
                reviewsRecyclerView.visibility = View.VISIBLE
                reviewAdapter.submitList(reviews)
                
                // Update rating text with review count
                val rating = calculateAverageRating(reviews)
                ratingValueTextView.text = "$rating (${reviews.size} reviews)"
            }
        }
    }
    
    private fun calculateAverageRating(reviews: List<com.donate.chillinnmobile.model.Review>): Float {
        if (reviews.isEmpty()) return 0f
        val sum = reviews.sumOf { it.rating.toDouble() }
        return (sum / reviews.size).toFloat()
    }
    
    private fun loadRoomDetails() {
        val roomId = args.roomId
        roomViewModel.getRoomDetails(roomId)
    }
    
    private fun populateRoomDetails(room: Room) {
        // Set room images in carousel
        room.images?.let { images ->
            if (images.isNotEmpty()) {
                imageCarouselAdapter.updateImages(images)
                
                // Make indicator visible only if there are multiple images
                tabLayoutIndicator.visibility = if (images.size > 1) View.VISIBLE else View.GONE
            } else {
                // Use default placeholder if no images
                imageCarouselAdapter.updateImages(listOf(createPlaceholderImage()))
                tabLayoutIndicator.visibility = View.GONE
            }
        } ?: run {
            // Use default placeholder if no images
            imageCarouselAdapter.updateImages(listOf(createPlaceholderImage()))
            tabLayoutIndicator.visibility = View.GONE
        }
        
        // Set room details
        roomTypeTextView.text = room.roomType
        roomPriceTextView.text = "$${room.pricePerNight}/night"
        roomDescriptionTextView.text = room.description
        availabilityStatusTextView.text = "Status: ${room.status}"
        
        // Set amenities
        room.amenities?.let { amenities ->
            if (amenities.isNotEmpty()) {
                amenityAdapter.submitList(amenities)
                amenitiesRecyclerView.visibility = View.VISIBLE
            } else {
                amenitiesRecyclerView.visibility = View.GONE
            }
        } ?: run {
            amenitiesRecyclerView.visibility = View.GONE
        }
        
        // Set button enabled based on room status
        val isAvailable = room.status == "AVAILABLE"
        bookNowButton.isEnabled = isAvailable
        bookNowButton.alpha = if (isAvailable) 1.0f else 0.5f
    }
    
    private fun createPlaceholderImage(): Image {
        return Image(
            id = "placeholder",
            url = "placeholder",
            roomId = args.roomId,
            isPrimary = true,
            description = "Placeholder image"
        )
    }
    
    private fun navigateToBooking() {
        val roomId = args.roomId
        val action = RoomDetailsFragmentDirections.actionRoomDetailsFragmentToBookingFragment(roomId)
        findNavController().navigate(action)
    }
} 