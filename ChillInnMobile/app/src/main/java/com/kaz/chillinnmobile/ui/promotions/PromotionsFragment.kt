package com.kaz.chillinnmobile.ui.promotions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kaz.chillinnmobile.R
import com.kaz.chillinnmobile.databinding.FragmentPromotionsBinding
import com.kaz.chillinnmobile.model.Promotion
import com.kaz.chillinnmobile.ui.promotions.adapter.PromotionAdapter
import com.kaz.chillinnmobile.utils.AccessibilityUtils

class PromotionsFragment : Fragment() {

    private var _binding: FragmentPromotionsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var promotionAdapter: PromotionAdapter

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
        
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupAccessibility()
        loadPromotions()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    
    private fun setupRecyclerView() {
        promotionAdapter = PromotionAdapter { promotion ->
            navigateToPromotionDetails(promotion)
        }
        
        binding.recyclerViewPromotions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = promotionAdapter
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadPromotions()
        }
        
        // Set the SwipeRefreshLayout colors to match app theme
        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.primary,
            R.color.secondary
        )
    }
    
    private fun setupAccessibility() {
        // Set content description for toolbar for better screen reader experience
        binding.toolbar.contentDescription = getString(R.string.special_offers)
        
        // Make refresh layout announce its status for accessibility
        binding.swipeRefreshLayout.setOnRefreshListener {
            AccessibilityUtils.announceForAccessibility(
                binding.root, 
                getString(R.string.loading_promotions)
            )
            loadPromotions()
        }
        
        // Make empty state accessible
        binding.layoutEmpty.btnRetry.contentDescription = getString(R.string.retry_loading_promotions)
        
        // Set content description for the recycler view to help screen readers understand the content
        binding.recyclerViewPromotions.contentDescription = getString(R.string.promotions_list)
        
        // Add accessibility behavior to the empty state
        ViewCompat.setAccessibilityDelegate(binding.layoutEmpty.root, object : ViewCompat.AccessibilityDelegate() {
            override fun onPopulateAccessibilityEvent(host: View, event: android.view.accessibility.AccessibilityEvent) {
                super.onPopulateAccessibilityEvent(host, event)
                if (event.eventType == android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SELECTED) {
                    // Announce the empty state message 
                    event.text.add(binding.layoutEmpty.tvEmptyMessage.text)
                }
            }
        })
    }
    
    private fun loadPromotions() {
        // Show loading state
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutEmpty.root.visibility = View.GONE
        
        // Announce loading for screen readers
        AccessibilityUtils.announceForAccessibility(
            binding.root, 
            getString(R.string.loading_promotions)
        )
        
        // Simulate network delay (would be replaced with actual API call)
        binding.root.postDelayed({
            val promotions = generateSamplePromotions()
            
            if (promotions.isEmpty()) {
                showEmptyState()
            } else {
                showPromotions(promotions)
            }
            
            // Hide loading indicators
            binding.progressBar.visibility = View.GONE
            binding.swipeRefreshLayout.isRefreshing = false
            
            // Announce completion for accessibility
            if (promotions.isEmpty()) {
                AccessibilityUtils.announceForAccessibility(
                    binding.root,
                    getString(R.string.no_promotions)
                )
            } else {
                AccessibilityUtils.announceForAccessibility(
                    binding.root,
                    getString(R.string.loaded_promotions, promotions.size)
                )
            }
        }, 1000)
    }
    
    private fun showPromotions(promotions: List<Promotion>) {
        promotionAdapter.submitList(promotions)
        binding.recyclerViewPromotions.visibility = View.VISIBLE
        binding.layoutEmpty.root.visibility = View.GONE
    }
    
    private fun showEmptyState() {
        binding.recyclerViewPromotions.visibility = View.GONE
        binding.layoutEmpty.root.visibility = View.VISIBLE
        
        binding.layoutEmpty.tvEmptyMessage.text = getString(R.string.no_promotions)
        binding.layoutEmpty.btnRetry.setOnClickListener {
            loadPromotions()
        }
    }
    
    private fun navigateToPromotionDetails(promotion: Promotion) {
        // TODO: Navigate to promotion details when backend is ready
        Snackbar.make(
            binding.root,
            getString(R.string.promotion_details_coming_soon),
            Snackbar.LENGTH_SHORT
        ).show()
        
        // Announce for accessibility
        AccessibilityUtils.announceForAccessibility(
            binding.root, 
            getString(R.string.promotion_details_coming_soon)
        )
    }
    
    private fun generateSamplePromotions(): List<Promotion> {
        // Generate sample promotions for UI testing
        return listOf(
            Promotion(
                id = "1",
                title = "Summer Exclusive Offer",
                description = "Enjoy 30% off on luxury suites for your summer getaway",
                discount = "30% OFF",
                imageUrl = "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80",
                badgeText = "Limited Time",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
                promotionCode = "SUMMER2025"
            ),
            Promotion(
                id = "2",
                title = "Weekend Retreat Package",
                description = "Book 2 nights and get 1 night free with complimentary breakfast",
                discount = "1 Night FREE",
                imageUrl = "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80",
                badgeText = "Weekends Only",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + (60L * 24 * 60 * 60 * 1000),
                promotionCode = "WEEKEND2025"
            ),
            Promotion(
                id = "3",
                title = "Honeymoon Special",
                description = "Romantic package with champagne, spa treatment, and special dinner",
                discount = "15% OFF",
                imageUrl = "https://images.unsplash.com/photo-1602002418816-5c0aeef426aa?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80",
                badgeText = "Couples",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000),
                promotionCode = "HONEYMOON2025"
            ),
            Promotion(
                id = "4",
                title = "Business Traveler Deal",
                description = "Special rates for business travelers with high-speed WiFi and workspace",
                discount = "20% OFF",
                imageUrl = "https://images.unsplash.com/photo-1564501049412-61c2a3083791?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80",
                badgeText = "Business",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + (45L * 24 * 60 * 60 * 1000),
                promotionCode = "BUSINESS2025"
            ),
            Promotion(
                id = "5",
                title = "Extended Stay Discount",
                description = "Significant savings for stays longer than 7 nights",
                discount = "25% OFF",
                imageUrl = "https://images.unsplash.com/photo-1584132967334-10e028bd69f7?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80",
                badgeText = "Long Stay",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + (120L * 24 * 60 * 60 * 1000),
                promotionCode = "EXTENDED2025"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 