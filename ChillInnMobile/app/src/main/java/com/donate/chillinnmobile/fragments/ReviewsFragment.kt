package com.donate.chillinnmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.adapters.ReviewAdapter
import com.donate.chillinnmobile.model.Room
import com.donate.chillinnmobile.utils.loadImage
import com.donate.chillinnmobile.utils.showToast
import com.donate.chillinnmobile.viewmodel.ReviewViewModel
import com.donate.chillinnmobile.viewmodel.RoomViewModel
import com.google.android.material.card.MaterialCardView

/**
 * Fragment for displaying and submitting reviews
 */
class ReviewsFragment : Fragment() {

    private val args: ReviewsFragmentArgs by navArgs()
    
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var reviewAdapter: ReviewAdapter
    
    // UI components
    private lateinit var roomNameTextView: TextView
    private lateinit var roomImageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var emptyStateTextView: TextView
    private lateinit var addReviewCard: MaterialCardView
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var addImageButton: Button
    private lateinit var selectedImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reviews, container, false)
        
        // Initialize ViewModels
        roomViewModel = ViewModelProvider(requireActivity()).get(RoomViewModel::class.java)
        reviewViewModel = ViewModelProvider(requireActivity()).get(ReviewViewModel::class.java)
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up RecyclerView
        setupRecyclerView()
        
        // Set up click listeners
        setupClickListeners()
        
        // Observe data
        observeRoomData()
        observeReviewData()
        
        // Load room details and reviews
        loadRoomDetails()
        loadReviews()
        
        return view
    }
    
    private fun initializeViews(view: View) {
        roomNameTextView = view.findViewById(R.id.room_name_text_view)
        roomImageView = view.findViewById(R.id.room_image_view)
        recyclerView = view.findViewById(R.id.reviews_recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)
        errorTextView = view.findViewById(R.id.error_text_view)
        emptyStateTextView = view.findViewById(R.id.empty_state_text_view)
        addReviewCard = view.findViewById(R.id.add_review_card)
        ratingBar = view.findViewById(R.id.rating_bar)
        reviewEditText = view.findViewById(R.id.review_edit_text)
        submitButton = view.findViewById(R.id.submit_button)
        addImageButton = view.findViewById(R.id.add_image_button)
        selectedImageView = view.findViewById(R.id.selected_image_view)
    }
    
    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter()
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewAdapter
        }
    }
    
    private fun setupClickListeners() {
        submitButton.setOnClickListener {
            if (validateReview()) {
                submitReview()
            }
        }
        
        addImageButton.setOnClickListener {
            // In a real app, this would show a dialog to pick images
            showToast("Image selection would be implemented here")
            selectedImageView.visibility = View.VISIBLE
            selectedImageView.setImageResource(R.drawable.placeholder_image)
        }
        
        selectedImageView.setOnClickListener {
            // In a real app, this would show a full-screen image preview
            selectedImageView.visibility = View.GONE
        }
    }
    
    private fun observeRoomData() {
        roomViewModel.roomDetails.observe(viewLifecycleOwner) { room ->
            if (room != null) {
                populateRoomDetails(room)
            }
        }
    }
    
    private fun observeReviewData() {
        reviewViewModel.roomReviews.observe(viewLifecycleOwner) { reviews ->
            if (reviews.isNullOrEmpty()) {
                emptyStateTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyStateTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                reviewAdapter.submitList(reviews)
            }
        }
        
        reviewViewModel.isLoadingReviews.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        reviewViewModel.reviewError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = error
            } else {
                errorTextView.visibility = View.GONE
            }
        }
        
        reviewViewModel.reviewSubmissionStatus.observe(viewLifecycleOwner) { (success, errorMessage, isLoading) ->
            if (isLoading) {
                submitButton.isEnabled = false
                progressBar.visibility = View.VISIBLE
            } else {
                submitButton.isEnabled = true
                progressBar.visibility = View.GONE
                
                if (success) {
                    // Review submitted successfully
                    showToast("Review submitted successfully!")
                    clearReviewForm()
                    loadReviews() // Reload reviews to include the new one
                } else if (errorMessage != null) {
                    // Show error message
                    showToast(errorMessage)
                }
            }
        }
    }
    
    private fun loadRoomDetails() {
        val roomId = args.roomId
        roomViewModel.getRoomDetails(roomId)
    }
    
    private fun loadReviews() {
        val roomId = args.roomId
        reviewViewModel.getRoomReviews(roomId)
    }
    
    private fun populateRoomDetails(room: Room) {
        roomNameTextView.text = "${room.roomType.name} - Room #${room.roomNumber}"
        
        // Load room image
        if (room.images?.isNotEmpty() == true) {
            val primaryImage = room.images.find { it.isPrimary } ?: room.images.first()
            roomImageView.loadImage(primaryImage.url)
        } else {
            roomImageView.setImageResource(R.drawable.placeholder_image)
        }
    }
    
    private fun validateReview(): Boolean {
        // Validate rating
        if (ratingBar.rating == 0f) {
            showToast("Please select a rating")
            return false
        }
        
        // Validate comment
        val comment = reviewEditText.text.toString().trim()
        if (comment.isEmpty()) {
            showToast("Please enter a review comment")
            return false
        }
        
        return true
    }
    
    private fun submitReview() {
        val roomId = args.roomId
        val rating = ratingBar.rating.toInt()
        val comment = reviewEditText.text.toString().trim()
        
        // For simplicity, we're not handling actual image upload in this example
        reviewViewModel.submitReview(roomId, rating, comment)
    }
    
    private fun clearReviewForm() {
        ratingBar.rating = 0f
        reviewEditText.setText("")
        selectedImageView.visibility = View.GONE
    }
} 