package com.kaz.chillinnmobile.utils

import android.content.Context
import android.os.Build
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.google.android.material.card.MaterialCardView

/**
 * Utility class providing helper methods for implementing accessibility features
 */
object AccessibilityUtils {

    /**
     * Checks if screen reader (TalkBack) is enabled
     */
    fun isScreenReaderEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled
    }
    
    /**
     * Sets up proper accessibility behavior for clickable images
     * Makes sure images announce themselves as buttons when appropriate
     */
    fun setupAccessibleImage(imageView: ImageView, contentDescription: String, isClickable: Boolean = true) {
        imageView.contentDescription = contentDescription
        
        if (isClickable) {
            ViewCompat.setAccessibilityDelegate(imageView, object : ViewCompat.AccessibilityDelegate() {
                override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.className = android.widget.Button::class.java.name
                    info.isClickable = true
                }
            })
        }
    }
    
    /**
     * Sets up proper accessibility for card views with multiple clickable elements
     */
    fun setupAccessibleCard(cardView: MaterialCardView, contentDescription: String) {
        cardView.contentDescription = contentDescription
        
        // Set focus behavior to block descendants which helps prevent
        // accessibility services from focusing on both the card and its children
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cardView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            cardView.accessibilityTraversalBefore = View.NO_ID
        }
        
        // Announce the card as a button to screen readers
        ViewCompat.setAccessibilityDelegate(cardView, object : ViewCompat.AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.className = android.widget.Button::class.java.name
                info.isClickable = true
            }
        })
    }
    
    /**
     * Groups related elements for better screen reader navigation
     * Useful for elements that should be announced together
     */
    fun groupForAccessibility(container: View, contentDescription: String, vararg views: View) {
        // Set container description
        container.contentDescription = contentDescription
        
        // Hide individual views from accessibility services
        views.forEach { view ->
            view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }
        
        // Make container accessible
        container.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        
        // Make it focusable for accessibility
        container.isFocusable = true
    }
    
    /**
     * Makes a view announce the given text to screen readers
     * without changing its visible text
     */
    fun announceForAccessibility(view: View, text: String) {
        view.announceForAccessibility(text)
    }
    
    /**
     * Sets up accessibility ordering for a sequence of elements
     * This ensures that screen readers navigate in the intended order
     */
    fun setAccessibilityTraversalOrder(vararg views: View) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        
        for (i in 0 until views.size - 1) {
            views[i].accessibilityTraversalAfter = views[i + 1].id
            views[i + 1].accessibilityTraversalBefore = views[i].id
        }
    }
} 