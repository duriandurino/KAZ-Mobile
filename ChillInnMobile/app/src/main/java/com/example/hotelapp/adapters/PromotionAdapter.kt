package com.example.hotelapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hotelapp.R
import com.example.hotelapp.databinding.ItemPromotionBinding
import com.example.hotelapp.model.Promotion
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter for displaying promotion items in a RecyclerView.
 * Uses ListAdapter for efficient item updates with DiffUtil.
 */
class PromotionAdapter(private val listener: PromotionClickListener) : 
    ListAdapter<Promotion, PromotionAdapter.PromotionViewHolder>(PromotionDiffCallback()) {

    interface PromotionClickListener {
        fun onPromotionClicked(promotion: Promotion)
        fun onViewPromotionClicked(promotion: Promotion)
        fun onPromotionLongClicked(promotion: Promotion): Boolean
    }

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

    inner class PromotionViewHolder(private val binding: ItemPromotionBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.touchFeedback.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPromotionClicked(getItem(position))
                }
            }
            
            binding.btnViewPromotion.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onViewPromotionClicked(getItem(position))
                }
            }
            
            binding.touchFeedback.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    return@setOnLongClickListener listener.onPromotionLongClicked(getItem(position))
                }
                return@setOnLongClickListener false
            }
        }

        fun bind(promotion: Promotion) {
            with(binding) {
                // Set promotion title and description
                promotionTitle.text = promotion.title
                promotionDescription.text = promotion.shortDescription
                
                // Set badge text if available
                if (promotion.badgeText != null) {
                    promotionBadge.text = promotion.badgeText
                    promotionBadge.visibility = android.view.View.VISIBLE
                } else {
                    promotionBadge.visibility = android.view.View.GONE
                }
                
                // Set formatted discount if available
                val discount = promotion.getFormattedDiscount()
                if (discount != null) {
                    promotionDiscount.text = discount
                    promotionDiscount.visibility = android.view.View.VISIBLE
                } else {
                    promotionDiscount.visibility = android.view.View.GONE
                }
                
                // Load image with Glide
                Glide.with(itemView.context)
                    .load(promotion.imageUrl)
                    .placeholder(R.drawable.placeholder_promotion)
                    .error(R.drawable.error_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(promotionImage)
                
                // Set expiration indicator if applicable
                val daysRemaining = promotion.getDaysRemaining()
                if (daysRemaining != null && daysRemaining <= 3) {
                    // Customize for nearly expired promotions
                    if (daysRemaining <= 1) {
                        btnViewPromotion.setIconTintResource(R.color.error)
                        btnViewPromotion.setStrokeColorResource(R.color.error)
                        btnViewPromotion.setTextColor(itemView.context.getColor(R.color.error))
                    }
                }
                
                // Add content description for accessibility
                val contentDescription = StringBuilder()
                    .append(promotion.title)
                    .append(". ")
                    .append(promotion.shortDescription)
                
                if (promotion.isExclusive) {
                    contentDescription.append(". Exclusive offer.")
                }
                
                if (daysRemaining != null && daysRemaining <= 3) {
                    contentDescription.append(". Ends soon")
                    if (daysRemaining <= 1) {
                        contentDescription.append(", ends today!")
                    } else {
                        contentDescription.append(", ends in $daysRemaining days!")
                    }
                }
                
                itemView.contentDescription = contentDescription.toString()
            }
        }
    }

    /**
     * DiffUtil callback for efficient updates
     */
    class PromotionDiffCallback : DiffUtil.ItemCallback<Promotion>() {
        override fun areItemsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
            return oldItem == newItem
        }
    }
} 