package com.donate.chillinnmobile.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.adapters.BookingHistoryAdapter
import com.donate.chillinnmobile.adapters.FavoriteRoomsAdapter
import com.donate.chillinnmobile.model.User
import com.donate.chillinnmobile.utils.ImageUploadDialogHelper
import com.donate.chillinnmobile.utils.ImageUploadManager
import com.donate.chillinnmobile.utils.showToast
import com.donate.chillinnmobile.viewmodel.UserViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

/**
 * Fragment displaying user profile and settings
 */
class ProfileFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var imageUploadDialogHelper: ImageUploadDialogHelper
    
    // UI components
    private lateinit var profileImageView: ImageView
    private lateinit var editProfileImageButton: Button
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userPhoneTextView: TextView
    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var notificationsSwitch: SwitchMaterial
    private lateinit var favoriteRoomsRecyclerView: RecyclerView
    private lateinit var bookingHistoryRecyclerView: RecyclerView
    
    // New UI components for navigation
    private lateinit var accountSettingsButton: Button
    private lateinit var helpSupportButton: Button
    
    // Adapters
    private lateinit var favoriteRoomsAdapter: FavoriteRoomsAdapter
    private lateinit var bookingHistoryAdapter: BookingHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        // Initialize ViewModel
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up adapters and RecyclerViews
        setupRecyclerViews()
        
        // Set up image upload helper
        imageUploadDialogHelper = ImageUploadDialogHelper(this)
        
        // Set up observers
        observeUserData()
        
        // Set up click listeners
        setupClickListeners()
        
        // Load user data
        loadUserData()
        
        return view
    }
    
    private fun initializeViews(view: View) {
        profileImageView = view.findViewById(R.id.ivProfileImage)
        editProfileImageButton = view.findViewById(R.id.btnEditProfileImage)
        userNameTextView = view.findViewById(R.id.tvUserName)
        userEmailTextView = view.findViewById(R.id.tvUserEmail)
        userPhoneTextView = view.findViewById(R.id.tvUserPhone)
        themeSwitch = view.findViewById(R.id.switchTheme)
        notificationsSwitch = view.findViewById(R.id.switchNotifications)
        favoriteRoomsRecyclerView = view.findViewById(R.id.rvFavoriteRooms)
        bookingHistoryRecyclerView = view.findViewById(R.id.rvBookingHistory)
        
        // Initialize new UI components
        accountSettingsButton = view.findViewById(R.id.btnAccountSettings)
        helpSupportButton = view.findViewById(R.id.btnHelpSupport)
    }
    
    private fun setupRecyclerViews() {
        // Set up favorite rooms RecyclerView
        favoriteRoomsAdapter = FavoriteRoomsAdapter(
            onItemClick = { roomId ->
                navigateToRoomDetails(roomId)
            },
            onRemoveFavorite = { roomId ->
                removeFromFavorites(roomId)
            }
        )
        
        favoriteRoomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = favoriteRoomsAdapter
        }
        
        // Set up booking history RecyclerView
        bookingHistoryAdapter = BookingHistoryAdapter(
            onItemClick = { bookingId ->
                navigateToBookingDetails(bookingId)
            }
        )
        
        bookingHistoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookingHistoryAdapter
        }
    }
    
    private fun observeUserData() {
        userViewModel.userData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                updateUI(user)
            }
        }
        
        userViewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            favoriteRoomsAdapter.submitList(favorites)
        }
        
        userViewModel.bookingHistory.observe(viewLifecycleOwner) { bookings ->
            bookingHistoryAdapter.submitList(bookings)
        }
    }
    
    private fun setupClickListeners() {
        // Set up profile image click for editing
        editProfileImageButton.setOnClickListener {
            showProfileImageUploadDialog()
        }
        
        // Set up switch listeners
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateThemePreference(isChecked)
        }
        
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateNotificationPreference(isChecked)
        }
        
        // Set up navigation button listeners
        accountSettingsButton.setOnClickListener {
            navigateToAccountSettings()
        }
        
        helpSupportButton.setOnClickListener {
            navigateToHelpSupport()
        }
    }
    
    private fun loadUserData() {
        userViewModel.getCurrentUser()
        userViewModel.getUserFavorites()
        userViewModel.getUserBookingHistory()
    }
    
    private fun updateUI(user: User) {
        userNameTextView.text = user.name
        userEmailTextView.text = user.email
        userPhoneTextView.text = user.phone
        
        // Load profile image if exists
        user.profileImageUrl?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_profile)
                .circleCrop()
                .into(profileImageView)
        } ?: run {
            // Set default profile image
            profileImageView.setImageResource(R.drawable.placeholder_profile)
        }
        
        // Set switch states based on user preferences
        themeSwitch.isChecked = user.darkThemeEnabled ?: false
        notificationsSwitch.isChecked = user.notificationsEnabled ?: true
    }
    
    private fun showProfileImageUploadDialog() {
        imageUploadDialogHelper.apply {
            onImageUploaded = { image ->
                updateProfileImage(Uri.parse(image.url))
            }
            
            showImageUploadDialog(
                category = ImageUploadManager.CATEGORY_PROFILE,
                entityId = userViewModel.getUserId() ?: "default_user"
            )
        }
    }
    
    private fun updateProfileImage(imageUri: Uri) {
        // Update UI
        Glide.with(this)
            .load(imageUri)
            .circleCrop()
            .into(profileImageView)
        
        // Update in ViewModel (which would normally save to backend)
        userViewModel.updateProfileImage(imageUri.toString())
        
        // Show success message
        showToast("Profile picture updated successfully")
    }
    
    private fun updateThemePreference(darkThemeEnabled: Boolean) {
        userViewModel.updateThemePreference(darkThemeEnabled)
        
        // In a real app, we would apply the theme change here
        showToast("Theme preference updated")
    }
    
    private fun updateNotificationPreference(notificationsEnabled: Boolean) {
        userViewModel.updateNotificationPreference(notificationsEnabled)
        showToast("Notification preference updated")
    }
    
    private fun removeFromFavorites(roomId: String) {
        lifecycleScope.launch {
            userViewModel.removeFromFavorites(roomId)
            showToast("Room removed from favorites")
        }
    }
    
    private fun navigateToRoomDetails(roomId: String) {
        // In a real app, this would navigate to room details
        showToast("Navigating to room details: $roomId")
    }
    
    private fun navigateToBookingDetails(bookingId: String) {
        // In a real app, this would navigate to booking details
        showToast("Navigating to booking details: $bookingId")
    }
    
    private fun navigateToAccountSettings() {
        findNavController().navigate(R.id.action_profileFragment_to_accountFragment)
    }
    
    private fun navigateToHelpSupport() {
        findNavController().navigate(R.id.action_profileFragment_to_helpSupportFragment)
    }
} 