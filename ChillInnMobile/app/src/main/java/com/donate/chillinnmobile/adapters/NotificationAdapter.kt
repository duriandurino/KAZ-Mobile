package com.donate.chillinnmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Notification
import com.donate.chillinnmobile.model.NotificationType
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter for displaying notifications in a RecyclerView
 */
class NotificationAdapter(
    private val context: Context,
    private val onItemClick: (Notification) -> Unit,
    private val onDeleteClick: (Notification) -> Unit
) : ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = getItem(position)
        holder.bind(notification)
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.notification_title)
        private val messageTextView: TextView = itemView.findViewById(R.id.notification_message)
        private val timeTextView: TextView = itemView.findViewById(R.id.notification_time)
        private val typeIconView: ImageView = itemView.findViewById(R.id.notification_type_icon)
        private val deleteButton: ImageView = itemView.findViewById(R.id.notification_delete_button)
        private val unreadIndicator: View = itemView.findViewById(R.id.unread_indicator)
        
        fun bind(notification: Notification) {
            titleTextView.text = notification.title
            messageTextView.text = notification.message
            timeTextView.text = dateFormat.format(notification.timestamp)
            
            // Set type icon based on notification type
            val iconResId = when (notification.type) {
                NotificationType.BOOKING -> R.drawable.ic_notification_booking
                NotificationType.PROMOTION -> R.drawable.ic_notification_promotion
                NotificationType.GENERAL -> R.drawable.ic_notification_general
            }
            typeIconView.setImageResource(iconResId)
            
            // Set background color based on read status
            if (notification.isRead) {
                unreadIndicator.visibility = View.GONE
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.notificationReadBackground))
            } else {
                unreadIndicator.visibility = View.VISIBLE
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.notificationUnreadBackground))
            }
            
            // Set click listeners
            itemView.setOnClickListener {
                onItemClick(notification)
            }
            
            deleteButton.setOnClickListener {
                onDeleteClick(notification)
            }
        }
    }

    /**
     * DiffUtil callback for efficient RecyclerView updates
     */
    class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }
} 