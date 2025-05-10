package com.donate.chillinnmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Booking
import com.donate.chillinnmobile.model.BookingStatus
import com.donate.chillinnmobile.utils.formatPrice
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter for displaying Booking items in a RecyclerView
 */
class BookingAdapter(
    private val onItemClicked: (Booking) -> Unit,
    private val onCancelClicked: (Booking) -> Unit,
    private val onPaymentClicked: (Booking) -> Unit
) : ListAdapter<Booking, BookingAdapter.BookingViewHolder>(BookingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view, onItemClicked, onCancelClicked, onPaymentClicked)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = getItem(position)
        holder.bind(booking)
    }

    class BookingViewHolder(
        itemView: View,
        private val onItemClicked: (Booking) -> Unit,
        private val onCancelClicked: (Booking) -> Unit,
        private val onPaymentClicked: (Booking) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        
        private val roomInfoTextView: TextView = itemView.findViewById(R.id.room_info_text_view)
        private val bookingDatesTextView: TextView = itemView.findViewById(R.id.booking_dates_text_view)
        private val statusTextView: TextView = itemView.findViewById(R.id.status_text_view)
        private val priceTextView: TextView = itemView.findViewById(R.id.price_text_view)
        private val guestsTextView: TextView = itemView.findViewById(R.id.guests_text_view)
        private val cancelButton: Button = itemView.findViewById(R.id.cancel_button)
        private val paymentButton: Button = itemView.findViewById(R.id.payment_button)
        
        private var currentBooking: Booking? = null
        
        init {
            itemView.setOnClickListener {
                currentBooking?.let {
                    onItemClicked(it)
                }
            }
            
            cancelButton.setOnClickListener {
                currentBooking?.let {
                    onCancelClicked(it)
                }
            }
            
            paymentButton.setOnClickListener {
                currentBooking?.let {
                    onPaymentClicked(it)
                }
            }
        }
        
        fun bind(booking: Booking) {
            currentBooking = booking
            
            // Set booking details
            roomInfoTextView.text = "Room #${booking.roomId}" // In a real app, this would be the actual room info
            
            val checkInFormatted = dateFormat.format(booking.checkInDate)
            val checkOutFormatted = dateFormat.format(booking.checkOutDate)
            bookingDatesTextView.text = "Check-in: $checkInFormatted\nCheck-out: $checkOutFormatted"
            
            statusTextView.text = "Status: ${booking.status.name}"
            statusTextView.setTextColor(getStatusColor(booking.status))
            
            priceTextView.text = "Total: ${booking.totalPrice.formatPrice()}"
            guestsTextView.text = "Guests: ${booking.numberOfGuests}"
            
            // Set button visibility based on booking status
            setCancelButtonVisibility(booking.status)
            setPaymentButtonVisibility(booking)
        }
        
        private fun getStatusColor(status: BookingStatus): Int {
            return when (status) {
                BookingStatus.PENDING -> itemView.context.getColor(R.color.pending_yellow)
                BookingStatus.CONFIRMED -> itemView.context.getColor(R.color.confirmed_green)
                BookingStatus.CHECKED_IN -> itemView.context.getColor(R.color.checked_in_blue)
                BookingStatus.CHECKED_OUT -> itemView.context.getColor(R.color.checked_out_purple)
                BookingStatus.CANCELLED -> itemView.context.getColor(R.color.cancelled_red)
                BookingStatus.NO_SHOW -> itemView.context.getColor(R.color.no_show_gray)
            }
        }
        
        private fun setCancelButtonVisibility(status: BookingStatus) {
            // Booking can be cancelled if it's pending or confirmed
            cancelButton.visibility = when (status) {
                BookingStatus.PENDING, BookingStatus.CONFIRMED -> View.VISIBLE
                else -> View.GONE
            }
        }
        
        private fun setPaymentButtonVisibility(booking: Booking) {
            // Show payment button only if booking is pending or confirmed and payment is not completed
            val showPaymentButton = (booking.status == BookingStatus.PENDING || booking.status == BookingStatus.CONFIRMED) 
                && (booking.payment == null || booking.payment.status != com.donate.chillinnmobile.model.PaymentStatus.COMPLETED)
            
            paymentButton.visibility = if (showPaymentButton) View.VISIBLE else View.GONE
        }
    }

    class BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem == newItem
        }
    }
} 