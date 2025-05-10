package com.donate.chillinnmobile.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.donate.chillinnmobile.R

/**
 * Adapter for displaying and managing images attached to reviews
 */
class ReviewImageAdapter(
    private val onRemoveClicked: (Int) -> Unit
) : ListAdapter<Uri, ReviewImageAdapter.ImageViewHolder>(ImageDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review_image, parent, false)
        return ImageViewHolder(view, onRemoveClicked)
    }
    
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ImageViewHolder(
        itemView: View,
        private val onRemoveClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val imageView: ImageView = itemView.findViewById(R.id.reviewImage)
        private val removeButton: ImageView = itemView.findViewById(R.id.removeImageButton)
        
        init {
            removeButton.setOnClickListener {
                onRemoveClicked(bindingAdapterPosition)
            }
        }
        
        fun bind(uri: Uri) {
            Glide.with(itemView.context)
                .load(uri)
                .transform(
                    CenterCrop(),
                    RoundedCorners(itemView.context.resources.getDimensionPixelSize(R.dimen.image_corner_radius))
                )
                .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.image_placeholder))
                .error(ContextCompat.getDrawable(itemView.context, R.drawable.image_error))
                .into(imageView)
        }
    }
    
    private class ImageDiffCallback : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }
        
        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }
    }
} 