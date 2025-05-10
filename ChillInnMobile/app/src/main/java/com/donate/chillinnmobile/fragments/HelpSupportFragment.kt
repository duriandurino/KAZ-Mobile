package com.donate.chillinnmobile.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.utils.showToast
import com.google.android.material.appbar.MaterialToolbar

/**
 * Fragment for Help & Support
 */
class HelpSupportFragment : Fragment() {

    // UI components
    private lateinit var toolbar: MaterialToolbar
    private lateinit var callSupportButton: Button
    private lateinit var chatSupportButton: Button
    private lateinit var faqItem1: LinearLayout
    private lateinit var faqItem2: LinearLayout
    private lateinit var faqItem3: LinearLayout
    private lateinit var viewAllFaqsButton: Button
    private lateinit var sendFeedbackButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_help_support, container, false)
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up click listeners
        setupClickListeners()
        
        return view
    }
    
    private fun initializeViews(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        callSupportButton = view.findViewById(R.id.btnCallSupport)
        chatSupportButton = view.findViewById(R.id.btnChatSupport)
        faqItem1 = view.findViewById(R.id.faqItem1)
        faqItem2 = view.findViewById(R.id.faqItem2)
        faqItem3 = view.findViewById(R.id.faqItem3)
        viewAllFaqsButton = view.findViewById(R.id.btnViewAllFaqs)
        sendFeedbackButton = view.findViewById(R.id.btnSendFeedback)
        
        // Set up toolbar
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupClickListeners() {
        // Call support
        callSupportButton.setOnClickListener {
            dialSupportNumber()
        }
        
        // Live chat
        chatSupportButton.setOnClickListener {
            openLiveChat()
        }
        
        // FAQ items
        setupFaqItems()
        
        // View all FAQs
        viewAllFaqsButton.setOnClickListener {
            showToast("Navigate to full FAQ page")
            // In a real app, this would navigate to a full FAQ screen
        }
        
        // Send feedback
        sendFeedbackButton.setOnClickListener {
            openFeedbackForm()
        }
        
        // Setup troubleshooting items
        setupTroubleshootingItems(view)
    }
    
    private fun setupFaqItems() {
        // Set up the expandable FAQ items
        setupExpandableFaqItem(faqItem1)
        setupExpandableFaqItem(faqItem2)
        setupExpandableFaqItem(faqItem3)
    }
    
    private fun setupExpandableFaqItem(faqItem: LinearLayout) {
        // Get the question TextView (first child)
        val questionView = faqItem.getChildAt(0) as TextView
        
        // Get the answer TextView (second child)
        val answerView = faqItem.getChildAt(1) as TextView
        
        // Set up click listener to expand/collapse
        faqItem.setOnClickListener {
            // Toggle answer visibility
            if (answerView.visibility == View.VISIBLE) {
                answerView.visibility = View.GONE
                questionView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, R.drawable.ic_expand_more, 0
                )
            } else {
                answerView.visibility = View.VISIBLE
                questionView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, R.drawable.ic_expand_less, 0
                )
            }
        }
    }
    
    private fun setupTroubleshootingItems(rootView: View?) {
        if (rootView == null) return
        
        // Report a bug
        val reportBugItem = rootView.findViewById<LinearLayout>(R.id.reportBugItem)
        reportBugItem?.setOnClickListener {
            showToast("Report a bug")
            // In a real app, this would open a bug report form
        }
        
        // Check for updates
        val checkUpdatesItem = rootView.findViewById<LinearLayout>(R.id.checkUpdatesItem)
        checkUpdatesItem?.setOnClickListener {
            showToast("Checking for updates...")
            // In a real app, this would check for app updates
        }
        
        // Clear cache
        val clearCacheItem = rootView.findViewById<LinearLayout>(R.id.clearCacheItem)
        clearCacheItem?.setOnClickListener {
            showToast("Cache cleared")
            // In a real app, this would clear the app cache
        }
    }
    
    private fun dialSupportNumber() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:+18001234567")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            showToast("No phone app available")
        }
    }
    
    private fun openLiveChat() {
        showToast("Opening live chat")
        // In a real app, this would open a live chat screen or web view
    }
    
    private fun openFeedbackForm() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@chillinn.com")
            putExtra(Intent.EXTRA_SUBJECT, "ChillInn Mobile App Feedback")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            showToast("No email app available")
        }
    }
} 