package com.donate.chillinnmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Amenity
import com.donate.chillinnmobile.utils.loadImage

/**
 * Adapter for displaying Amenity items in a RecyclerView
 */
class AmenityAdapter : ListAdapter<Amenity, AmenityAdapter.AmenityViewHolder>(AmenityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmenityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_amenity, parent, false)
        return AmenityViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmenityViewHolder, position: Int) {
        val amenity = getItem(position)
        holder.bind(amenity)
    }

    class AmenityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        private val amenityIconImageView: ImageView = itemView.findViewById(R.id.amenity_icon_image_view)
        private val amenityNameTextView: TextView = itemView.findViewById(R.id.amenity_name_text_view)
        
        fun bind(amenity: Amenity) {
            // Set amenity name
            amenityNameTextView.text = amenity.name
            
            // Load amenity icon if available, otherwise use default icon
            if (!amenity.icon.isNullOrEmpty()) {
                amenityIconImageView.loadImage(amenity.icon)
            } else {
                // Use a default icon based on the amenity name
                val resourceId = getAmenityIconResource(amenity.name)
                amenityIconImageView.setImageResource(resourceId)
            }
        }
        
        /**
         * Get a default icon resource ID based on amenity name
         */
        private fun getAmenityIconResource(amenityName: String): Int {
            return when {
                amenityName.contains("wifi", ignoreCase = true) -> R.drawable.ic_wifi
                amenityName.contains("tv", ignoreCase = true) -> R.drawable.ic_tv
                amenityName.contains("air", ignoreCase = true) -> R.drawable.ic_air_conditioning
                amenityName.contains("breakfast", ignoreCase = true) -> R.drawable.ic_breakfast
                amenityName.contains("parking", ignoreCase = true) -> R.drawable.ic_parking
                amenityName.contains("pool", ignoreCase = true) -> R.drawable.ic_pool
                amenityName.contains("gym", ignoreCase = true) -> R.drawable.ic_gym
                amenityName.contains("spa", ignoreCase = true) -> R.drawable.ic_spa
                amenityName.contains("minibar", ignoreCase = true) -> R.drawable.ic_minibar
                amenityName.contains("safe", ignoreCase = true) -> R.drawable.ic_safe
                else -> R.drawable.ic_amenity_default
            }
        }
    }

    class AmenityDiffCallback : DiffUtil.ItemCallback<Amenity>() {
        override fun areItemsTheSame(oldItem: Amenity, newItem: Amenity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Amenity, newItem: Amenity): Boolean {
            return oldItem == newItem
        }
    }
} 