package com.donate.chillinnmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Image

/**
 * Adapter for image carousel in room details
 */
class ImageCarouselAdapter(
    private var images: List<Image> = emptyList(),
    private val onImageClick: ((Image) -> Unit)? = null
) : RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_carousel, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    fun updateImages(newImages: List<Image>) {
        this.images = newImages
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.carouselImageView)

        init {
            imageView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onImageClick?.invoke(images[position])
                }
            }
        }

        fun bind(image: Image) {
            // Use Glide to load the image
            Glide.with(itemView.context)
                .load(image.url)
                .placeholder(R.drawable.placeholder_room)
                .error(R.drawable.placeholder_room)
                .centerCrop()
                .into(imageView)
        }
    }
} 