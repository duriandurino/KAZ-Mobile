package com.example.hotelapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotelapp.R
import com.example.hotelapp.adapters.PromotionAdapter
import com.example.hotelapp.databinding.FragmentPromotionsBinding
import com.example.hotelapp.model.Promotion
import com.example.hotelapp.utils.PromotionNotificationManager
import com.example.hotelapp.viewmodel.PromotionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.Date

/**
 * Fragment for displaying a list of promotions and special offers.
 * Includes functionality to trigger notification for selected promotions.
 */
class PromotionsFragment : Fragment(), PromotionAdapter.PromotionClickListener {

    private var _binding: FragmentPromotionsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: PromotionViewModel
    private lateinit var adapter: PromotionAdapter
    private lateinit var notificationManager: PromotionNotificationManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPromotionsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupToolbar()
        setupSwipeRefresh()
        setupNotificationManager()
        
        observePromotions()
        loadPromotions()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity())[PromotionViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = PromotionAdapter(this)
        binding.recyclerViewPromotions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PromotionsFragment.adapter
        }
    }
    
    private fun setupToolbar() {
        binding.toolbar.apply {
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_promotions, menu)
                }
                
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_notification_settings -> {
                            showNotificationSettingsDialog()
                            true
                        }
                        R.id.action_refresh -> {
                            refreshPromotions()
                            true
                        }
                        else -> false
                    }
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)
            
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshPromotions()
        }
        
        // Set the colors for the refresh animation
        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.secondary,
            R.color.primary,
            R.color.tertiary
        )
    }
    
    private fun setupNotificationManager() {
        notificationManager = PromotionNotificationManager(requireContext())
        notificationManager.initialize()
    }
    
    private fun observePromotions() {
        viewModel.promotions.observe(viewLifecycleOwner) { promotions ->
            if (promotions.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                adapter.submitList(promotions)
                
                // Check for new promotions that need notification
                checkForNewPromotions(promotions)
            }
            
            binding.swipeRefreshLayout.isRefreshing = false
            binding.progressBar.visibility = View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                binding.swipeRefreshLayout.isRefreshing = false
                binding.progressBar.visibility = View.GONE
            }
        }
        
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading && adapter.itemCount == 0) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun loadPromotions() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.loadPromotions()
    }
    
    private fun refreshPromotions() {
        binding.swipeRefreshLayout.isRefreshing = true
        viewModel.refreshPromotions()
    }
    
    private fun showEmptyState() {
        binding.layoutEmpty.root.visibility = View.VISIBLE
        binding.recyclerViewPromotions.visibility = View.GONE
    }
    
    private fun hideEmptyState() {
        binding.layoutEmpty.root.visibility = View.GONE
        binding.recyclerViewPromotions.visibility = View.VISIBLE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * Check for new promotions that might need notification
     */
    private fun checkForNewPromotions(promotions: List<Promotion>) {
        // Get promotions that are new (added in the last day)
        val newPromotions = promotions.filter { 
            it.isValid() && 
            (it.startDate == null || // No start date specified
            Date().time - it.startDate.time < 24 * 60 * 60 * 1000) // Less than 24 hours old
        }
        
        if (newPromotions.isNotEmpty()) {
            // Trigger notifications for these new promotions
            notificationManager.triggerMultiplePromotionNotifications(newPromotions)
        }
    }
    
    /**
     * Manually trigger notification for a specific promotion
     */
    private fun triggerPromotionNotification(promotion: Promotion) {
        if (promotion.isValid()) {
            notificationManager.triggerNewPromotionNotification(
                promotion,
                promotion.isExclusive
            )
            
            Snackbar.make(
                binding.root,
                "Notification sent for: ${promotion.title}",
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            Snackbar.make(
                binding.root,
                "Cannot send notification for inactive promotion",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
    
    /**
     * Show dialog for notification settings
     */
    private fun showNotificationSettingsDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Promotion Notifications")
            .setMessage("Would you like to receive notifications about special offers and promotions?")
            .setPositiveButton("Enable") { _, _ ->
                // In a real app, this would store the preference
                Snackbar.make(
                    binding.root,
                    "Promotion notifications enabled",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Disable") { _, _ ->
                // In a real app, this would store the preference
                Snackbar.make(
                    binding.root,
                    "Promotion notifications disabled",
                    Snackbar.LENGTH_SHORT
                ).show()
                
                // Cancel any scheduled notifications
                notificationManager.cancelAllScheduledPromotionNotifications()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }
    
    /**
     * Callback when a promotion is clicked
     */
    override fun onPromotionClicked(promotion: Promotion) {
        // Navigate to promotion details
        // findNavController().navigate(...)
    }
    
    /**
     * Callback when the View button on a promotion is clicked
     */
    override fun onViewPromotionClicked(promotion: Promotion) {
        // Navigate to promotion details
        // findNavController().navigate(...)
    }
    
    /**
     * Callback for long press on a promotion (used for testing notification)
     */
    override fun onPromotionLongClicked(promotion: Promotion): Boolean {
        // Show test notification dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Test Notification")
            .setMessage("Would you like to send a test notification for this promotion?")
            .setPositiveButton("Send") { _, _ ->
                triggerPromotionNotification(promotion)
            }
            .setNegativeButton("Cancel", null)
            .show()
        
        return true
    }
} 