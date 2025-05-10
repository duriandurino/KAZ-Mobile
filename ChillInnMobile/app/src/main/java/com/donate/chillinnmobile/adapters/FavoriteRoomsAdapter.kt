package com.donate.chillinnmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Room

/**
 * Adapter for displaying favorite rooms in the profile
 */
class FavoriteRoomsAdapter(
    private val onItemClick: (String) -> Unit,
    private val onRemoveFavorite: (String) -> Unit
) : ListAdapter<Room, FavoriteRoomsAdapter.FavoriteRoomViewHolder>(FavoriteRoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_room, parent, false)
        return FavoriteRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteRoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roomImageView: ImageView = itemView.findViewById(R.id.ivRoomImage)
        private val roomTypeTextView: TextView = itemView.findViewById(R.id.tvRoomType)
        private val roomPriceTextView: TextView = itemView.findViewById(R.id.tvRoomPrice)
        private val removeFavoriteButton: ImageView = itemView.findViewById(R.id.ivRemoveFavorite)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position).id)
                }
            }

            removeFavoriteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRemoveFavorite(getItem(position).id)
                }
            }
        }

        fun bind(room: Room) {
            roomTypeTextView.text = room.roomType
            roomPriceTextView.text = "$${room.pricePerNight}/night"

            // Load room image
            val primaryImage = room.images?.find { it.isPrimary } ?: room.images?.firstOrNull()
            primaryImage?.let { image ->
                Glide.with(itemView.context)
                    .load(image.url)
                    .placeholder(R.drawable.placeholder_room)
                    .centerCrop()
                    .into(roomImageView)
            } ?: run {
                roomImageView.setImageResource(R.drawable.placeholder_room)
            }
        }
    }

    private class FavoriteRoomDiffCallback : DiffUtil.ItemCallback<Room>() {
        override fun areItemsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem == newItem
        }
    }
} 