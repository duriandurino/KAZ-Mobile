package com.donate.chillinnmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Review
import com.donate.chillinnmobile.utils.loadImage
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter for displaying Review items in a RecyclerView
 */
class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        
        private val userNameTextView: TextView = itemView.findViewById(R.id.user_name_text_view)
        private val reviewDateTextView: TextView = itemView.findViewById(R.id.review_date_text_view)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)
        private val commentTextView: TextView = itemView.findViewById(R.id.comment_text_view)
        private val reviewImageView: ImageView = itemView.findViewById(R.id.review_image_view)
        
        fun bind(review: Review) {
            userNameTextView.text = review.guestName
            reviewDateTextView.text = dateFormat.format(review.createdAt)
            ratingBar.rating = review.rating.toFloat()
            commentTextView.text = review.comment
            
            // Load review image if available
            if (review.images?.isNotEmpty() == true) {
                reviewImageView.visibility = View.VISIBLE
                reviewImageView.loadImage(review.images.first().url)
                
                // Set click listener to show images in fullscreen
                reviewImageView.setOnClickListener {
                    // Implement fullscreen image viewer
                }
            } else {
                reviewImageView.visibility = View.GONE
            }
        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }
    }
} 