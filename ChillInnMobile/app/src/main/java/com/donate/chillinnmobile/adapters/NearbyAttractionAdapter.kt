package com.donate.chillinnmobile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.databinding.ItemNearbyAttractionBinding
import com.donate.chillinnmobile.model.NearbyAttraction

/**
 * Adapter for displaying nearby attractions in a RecyclerView.
 */
class NearbyAttractionAdapter(
    private val onItemClick: (NearbyAttraction) -> Unit,
    private val onDirectionsClick: (NearbyAttraction) -> Unit
) : ListAdapter<NearbyAttraction, NearbyAttractionAdapter.AttractionViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val binding = ItemNearbyAttractionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AttractionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AttractionViewHolder(
        private val binding: ItemNearbyAttractionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            binding.btnDirections.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDirectionsClick(getItem(position))
                }
            }
        }

        fun bind(attraction: NearbyAttraction) {
            binding.tvAttractionName.text = attraction.name
            binding.tvCategory.text = attraction.category
            binding.tvDistance.text = "${attraction.distanceFromHotel} km away"
            binding.ratingBar.rating = attraction.rating
            binding.tvRating.text = attraction.rating.toString()
            
            // Load image if available
            if (attraction.imageUrl != null) {
                Glide.with(binding.ivAttractionImage.context)
                    .load(attraction.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(binding.ivAttractionImage)
            } else {
                binding.ivAttractionImage.setImageResource(R.drawable.placeholder_image)
            }
            
            // Set price level indicators
            setPriceLevel(attraction.priceLevel ?: 0)
            
            // Set opening hours if available
            binding.tvOpeningHours.text = attraction.openingHours ?: "Hours not available"
        }
        
        private fun setPriceLevel(priceLevel: Int) {
            binding.tvPriceLevel.text = when (priceLevel) {
                0 -> "Free"
                1 -> "$"
                2 -> "$$"
                3 -> "$$$"
                4 -> "$$$$"
                else -> "Varies"
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NearbyAttraction>() {
            override fun areItemsTheSame(oldItem: NearbyAttraction, newItem: NearbyAttraction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NearbyAttraction, newItem: NearbyAttraction): Boolean {
                return oldItem == newItem
            }
        }
    }
} 