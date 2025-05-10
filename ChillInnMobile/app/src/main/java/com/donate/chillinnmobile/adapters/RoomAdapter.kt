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
import com.donate.chillinnmobile.model.Room
import com.donate.chillinnmobile.model.RoomStatus
import com.donate.chillinnmobile.utils.formatPrice
import com.donate.chillinnmobile.utils.loadImage

/**
 * Adapter for displaying Room items in a RecyclerView
 */
class RoomAdapter(private val onItemClicked: (Room) -> Unit) : 
    ListAdapter<Room, RoomAdapter.RoomViewHolder>(RoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = getItem(position)
        holder.bind(room)
    }

    class RoomViewHolder(
        itemView: View,
        private val onItemClicked: (Room) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val roomImageView: ImageView = itemView.findViewById(R.id.room_image_view)
        private val roomTypeTextView: TextView = itemView.findViewById(R.id.room_type_text_view)
        private val roomNumberTextView: TextView = itemView.findViewById(R.id.room_number_text_view)
        private val priceTextView: TextView = itemView.findViewById(R.id.price_text_view)
        private val statusTextView: TextView = itemView.findViewById(R.id.status_text_view)
        private val capacityTextView: TextView = itemView.findViewById(R.id.capacity_text_view)
        private val roomDescriptionTextView: TextView = itemView.findViewById(R.id.room_description_text_view)
        
        private var currentRoom: Room? = null
        
        init {
            itemView.setOnClickListener {
                currentRoom?.let {
                    onItemClicked(it)
                }
            }
        }
        
        fun bind(room: Room) {
            currentRoom = room
            
            // Load room image
            if (room.images?.isNotEmpty() == true) {
                val primaryImage = room.images.find { it.isPrimary } ?: room.images.first()
                roomImageView.loadImage(primaryImage.url)
            } else {
                roomImageView.setImageResource(R.drawable.placeholder_image)
            }
            
            // Set room details
            roomTypeTextView.text = room.roomType.name
            roomNumberTextView.text = "Room #${room.roomNumber}"
            priceTextView.text = room.price.formatPrice()
            capacityTextView.text = "${room.capacity} guests"
            roomDescriptionTextView.text = room.description
            
            // Set status with appropriate color
            statusTextView.text = room.status.name
            statusTextView.setTextColor(
                when (room.status) {
                    RoomStatus.AVAILABLE -> itemView.context.getColor(R.color.available_green)
                    RoomStatus.BOOKED -> itemView.context.getColor(R.color.booked_red)
                    RoomStatus.MAINTENANCE -> itemView.context.getColor(R.color.maintenance_orange)
                    RoomStatus.CLEANING -> itemView.context.getColor(R.color.cleaning_blue)
                }
            )
        }
    }

    class RoomDiffCallback : DiffUtil.ItemCallback<Room>() {
        override fun areItemsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem == newItem
        }
    }
} 