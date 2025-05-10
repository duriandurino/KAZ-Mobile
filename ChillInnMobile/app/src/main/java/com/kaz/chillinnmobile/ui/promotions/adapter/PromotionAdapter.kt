package com.kaz.chillinnmobile.ui.promotions.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.kaz.chillinnmobile.R
import com.kaz.chillinnmobile.databinding.ItemPromotionBinding
import com.kaz.chillinnmobile.model.Promotion
import com.kaz.chillinnmobile.utils.AccessibilityUtils

class PromotionAdapter(
    private val onPromotionClick: (Promotion) -> Unit
) : ListAdapter<Promotion, PromotionAdapter.PromotionViewHolder>(PromotionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val binding = ItemPromotionBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return PromotionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PromotionViewHolder(
        private val binding: ItemPromotionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.btnViewPromotion.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPromotionClick(getItem(position))
                }
            }
            
            // Make the entire card clickable
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPromotionClick(getItem(position))
                }
            }
        }
        
        fun bind(promotion: Promotion) {
            with(binding) {
                // Set text fields
                promotionTitle.text = promotion.title
                promotionDescription.text = promotion.description
                promotionDiscount.text = promotion.discount
                promotionBadge.text = promotion.badgeText
                
                // Load image with Glide
                Glide.with(promotionImage)
                    .load(promotion.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.placeholder_room)
                    .error(R.drawable.image_error)
                    .centerCrop()
                    .into(promotionImage)
                
                // Set badge visibility
                promotionBadge.visibility = android.view.View.VISIBLE
                
                // Setup accessibility features
                setupAccessibility(promotion)
            }
        }
        
        private fun setupAccessibility(promotion: Promotion) {
            with(binding) {
                // Create detailed content description for screen readers
                val daysRemaining = promotion.getDaysRemaining()
                val accessibilityDescription = if (promotion.isActive()) {
                    if (daysRemaining > 0) {
                        "${promotion.title}. ${promotion.description}. ${promotion.discount}. ${promotion.badgeText} offer. Expires in $daysRemaining days."
                    } else {
                        "${promotion.title}. ${promotion.description}. ${promotion.discount}. ${promotion.badgeText} offer. Expires today."
                    }
                } else {
                    "${promotion.title}. ${promotion.description}. ${promotion.discount}. ${promotion.badgeText} offer. This promotion has expired."
                }
                
                // Setup the card for accessibility
                AccessibilityUtils.setupAccessibleCard(root, accessibilityDescription)
                
                // Make the view button properly accessible
                btnViewPromotion.contentDescription = "View details for ${promotion.title} promotion"
                
                // Set image content description
                promotionImage.contentDescription = "${promotion.title} promotion image"
                
                // Group related text elements for better screen reader experience
                AccessibilityUtils.groupForAccessibility(
                    container = promotionTitle.parent as ViewGroup,
                    contentDescription = "${promotion.title}. ${promotion.description}",
                    views = arrayOf(promotionTitle, promotionDescription)
                )
                
                // Set traversal order for logical navigation flow
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    AccessibilityUtils.setAccessibilityTraversalOrder(
                        promotionBadge,
                        promotionTitle,
                        promotionDescription,
                        promotionDiscount,
                        btnViewPromotion
                    )
                }
            }
        }
    }
}

class PromotionDiffCallback : DiffUtil.ItemCallback<Promotion>() {
    override fun areItemsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
        return oldItem == newItem
    }
} 