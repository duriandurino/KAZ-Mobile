package com.donate.chillinnmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Booking
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter for displaying booking history in the profile
 */
class BookingHistoryAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<Booking, BookingHistoryAdapter.BookingViewHolder>(BookingDiffCallback()) {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_history, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roomTypeTextView: TextView = itemView.findViewById(R.id.tvRoomType)
        private val bookingDatesTextView: TextView = itemView.findViewById(R.id.tvBookingDates)
        private val bookingStatusTextView: TextView = itemView.findViewById(R.id.tvBookingStatus)
        private val totalPriceTextView: TextView = itemView.findViewById(R.id.tvTotalPrice)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position).id)
                }
            }
        }

        fun bind(booking: Booking) {
            roomTypeTextView.text = booking.roomType ?: "Room"
            
            val checkinDate = dateFormat.format(booking.checkInDate)
            val checkoutDate = dateFormat.format(booking.checkOutDate)
            bookingDatesTextView.text = "$checkinDate - $checkoutDate"
            
            bookingStatusTextView.text = booking.status
            
            // Set status text color based on booking status
            val statusColor = when (booking.status) {
                "CONFIRMED" -> R.color.status_confirmed
                "CANCELLED" -> R.color.status_cancelled
                "COMPLETED" -> R.color.status_completed
                "PENDING" -> R.color.status_pending
                else -> R.color.status_pending
            }
            bookingStatusTextView.setTextColor(itemView.context.getColor(statusColor))
            
            totalPriceTextView.text = "$${booking.totalPrice}"
        }
    }

    private class BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem == newItem
        }
    }
} 