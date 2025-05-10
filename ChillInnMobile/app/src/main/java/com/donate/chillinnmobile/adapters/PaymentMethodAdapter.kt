package com.donate.chillinnmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.PaymentMethod
import com.donate.chillinnmobile.model.PaymentMethodType
import com.google.android.material.chip.Chip

/**
 * Adapter for displaying payment methods in a RecyclerView
 */
class PaymentMethodAdapter(
    private val onRemoveClick: (PaymentMethod) -> Unit
) : ListAdapter<PaymentMethod, PaymentMethodAdapter.PaymentMethodViewHolder>(PaymentMethodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_method, parent, false)
        return PaymentMethodViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        val paymentMethod = getItem(position)
        holder.bind(paymentMethod)
    }

    inner class PaymentMethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardLogoImageView: ImageView = itemView.findViewById(R.id.ivCardLogo)
        private val cardNumberTextView: TextView = itemView.findViewById(R.id.tvCardNumber)
        private val cardholderNameTextView: TextView = itemView.findViewById(R.id.tvCardholderName)
        private val expiryDateTextView: TextView = itemView.findViewById(R.id.tvExpiryDate)
        private val defaultChip: Chip = itemView.findViewById(R.id.chipDefault)
        private val removeButton: ImageButton = itemView.findViewById(R.id.btnRemoveCard)

        fun bind(paymentMethod: PaymentMethod) {
            // Set card logo based on type
            val logoResId = when (paymentMethod.type) {
                PaymentMethodType.VISA -> R.drawable.ic_visa
                PaymentMethodType.MASTERCARD -> R.drawable.ic_mastercard
                PaymentMethodType.AMEX -> R.drawable.ic_amex
                PaymentMethodType.DISCOVER -> R.drawable.ic_discover
                PaymentMethodType.PAYPAL -> R.drawable.ic_paypal
                PaymentMethodType.OTHER -> R.drawable.ic_card_generic
            }
            cardLogoImageView.setImageResource(logoResId)

            // Set card details
            cardNumberTextView.text = paymentMethod.getDisplayCardNumber()
            cardholderNameTextView.text = paymentMethod.cardholderName ?: "Card Owner"
            expiryDateTextView.text = "Exp: ${paymentMethod.getExpiryDate()}"

            // Show default chip if this is the default payment method
            defaultChip.visibility = if (paymentMethod.isDefault) View.VISIBLE else View.GONE

            // Set click listener for remove button
            removeButton.setOnClickListener {
                onRemoveClick(paymentMethod)
            }
        }
    }

    /**
     * DiffUtil callback for efficient RecyclerView updates
     */
    class PaymentMethodDiffCallback : DiffUtil.ItemCallback<PaymentMethod>() {
        override fun areItemsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
            return oldItem == newItem
        }
    }
} 