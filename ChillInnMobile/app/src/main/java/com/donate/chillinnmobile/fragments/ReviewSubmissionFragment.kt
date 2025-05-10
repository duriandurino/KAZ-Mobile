package com.donate.chillinnmobile.fragments

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.adapters.ReviewImageAdapter
import com.donate.chillinnmobile.databinding.FragmentReviewSubmissionBinding
import com.donate.chillinnmobile.utils.ImageUploadManager
import com.donate.chillinnmobile.viewmodel.ReviewViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList

class ReviewSubmissionFragment : Fragment() {
    
    private var _binding: FragmentReviewSubmissionBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ReviewViewModel by viewModels()
    private val args: ReviewSubmissionFragmentArgs by navArgs()
    
    private val imageAdapter = ReviewImageAdapter(
        onRemoveClicked = { position -> removeImage(position) }
    )
    
    private val imageUris = ArrayList<Uri>()
    private val imageUploadManager = ImageUploadManager()
    
    // Launcher for picking images from gallery
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            addImage(it)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewSubmissionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupObservers()
    }
    
    private fun setupViews() {
        // Room info
        binding.roomName.text = args.roomName
        
        // Rating bar
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            updateSubmitButtonState()
            
            // Show appropriate emoji based on rating
            updateRatingEmoji(rating)
        }
        
        // Comment counter
        binding.reviewComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val count = s?.length ?: 0
                binding.commentCounter.text = "$count/500"
                updateSubmitButtonState()
            }
        })
        
        // Image recycler
        binding.reviewImagesRecycler.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
        binding.reviewImagesRecycler.adapter = imageAdapter
        
        // Add image button
        binding.addImageButton.setOnClickListener {
            if (imageUris.size < 5) {
                pickImageLauncher.launch("image/*")
            } else {
                Snackbar.make(
                    binding.root,
                    "Maximum 5 images allowed",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        
        // Submit button
        binding.submitButton.setOnClickListener {
            showConfirmationDialog()
        }
        
        // Cancel button
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupObservers() {
        viewModel.reviewSubmissionStatus.observe(viewLifecycleOwner) { (success, errorMessage, isLoading) ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            
            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Your review has been submitted successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            } else if (errorMessage != null) {
                Snackbar.make(
                    binding.root,
                    errorMessage,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun addImage(uri: Uri) {
        imageUris.add(uri)
        imageAdapter.submitList(ArrayList(imageUris))
        binding.reviewImagesRecycler.visibility = View.VISIBLE
        updateSubmitButtonState()
    }
    
    private fun removeImage(position: Int) {
        if (position in imageUris.indices) {
            imageUris.removeAt(position)
            imageAdapter.submitList(ArrayList(imageUris))
            
            if (imageUris.isEmpty()) {
                binding.reviewImagesRecycler.visibility = View.GONE
            }
            
            updateSubmitButtonState()
        }
    }
    
    private fun updateRatingEmoji(rating: Float) {
        val emojiResource = when {
            rating >= 4.5f -> R.drawable.ic_emoji_excellent
            rating >= 3.5f -> R.drawable.ic_emoji_good
            rating >= 2.5f -> R.drawable.ic_emoji_okay
            rating >= 1.5f -> R.drawable.ic_emoji_bad
            rating > 0f -> R.drawable.ic_emoji_terrible
            else -> 0
        }
        
        if (emojiResource != 0) {
            binding.ratingEmoji.setImageResource(emojiResource)
            binding.ratingEmoji.visibility = View.VISIBLE
        } else {
            binding.ratingEmoji.visibility = View.INVISIBLE
        }
    }
    
    private fun updateSubmitButtonState() {
        val hasRating = binding.ratingBar.rating > 0
        binding.submitButton.isEnabled = hasRating
    }
    
    private fun showConfirmationDialog() {
        val rating = binding.ratingBar.rating
        val comment = binding.reviewComment.text.toString().trim()
        
        // Build dialog message
        val message = StringBuilder("Rating: ${rating.toInt()} stars\n")
        if (comment.isNotEmpty()) {
            message.append("\nComment: $comment")
        }
        if (imageUris.isNotEmpty()) {
            message.append("\n\nYou've attached ${imageUris.size} image(s)")
        }
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Submit Review")
            .setMessage(message.toString())
            .setPositiveButton("Submit") { _, _ ->
                submitReview()
            }
            .setNegativeButton("Edit") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun submitReview() {
        val rating = binding.ratingBar.rating
        val comment = binding.reviewComment.text.toString().trim().ifEmpty { null }
        
        binding.progressBar.visibility = View.VISIBLE
        
        // First upload images if any
        if (imageUris.isNotEmpty()) {
            uploadImagesAndSubmitReview(rating, comment)
        } else {
            // No images to upload, submit directly
            viewModel.submitReview(
                roomId = args.roomId,
                rating = rating,
                comment = comment
            )
        }
    }
    
    private fun uploadImagesAndSubmitReview(rating: Float, comment: String?) {
        // For this mock implementation, we'll simulate uploading images
        // In a real app, you would upload each image and get back URLs
        
        // Simulate network delay
        binding.progressBar.visibility = View.VISIBLE
        
        // Mock image URLs (in a real app, these would come from your image upload service)
        val mockImageUrls = imageUris.mapIndexed { index, _ ->
            "https://example.com/images/room_review_${System.currentTimeMillis()}_$index.jpg"
        }
        
        // Submit review with mock image URLs
        viewModel.submitReview(
            roomId = args.roomId,
            rating = rating,
            comment = comment,
            images = mockImageUrls
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 