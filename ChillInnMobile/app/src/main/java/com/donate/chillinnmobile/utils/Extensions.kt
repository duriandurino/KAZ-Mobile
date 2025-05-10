package com.donate.chillinnmobile.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Image
import java.text.SimpleDateFormat
import java.util.*
import java.text.NumberFormat

/**
 * Extension functions to be used throughout the app
 */

// Context extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// Fragment extensions
fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, duration)
}

// View extensions
fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

// ImageView extensions
fun ImageView.loadImage(url: String, placeholder: Int = R.drawable.placeholder_room) {
    // For demo purposes, if the URL is "placeholder", just show the placeholder
    if (url == "placeholder") {
        setImageResource(placeholder)
        return
    }
    
    // For demo purposes, we'll use some sample images since we don't have a backend
    val demoUrl = if (url.startsWith("http")) {
        url
    } else {
        getSampleImageUrl(url)
    }
    
    Glide.with(this.context)
        .load(demoUrl)
        .apply(RequestOptions().placeholder(placeholder).error(placeholder))
        .into(this)
}

// Sample room images for demo
fun getSampleImageUrl(identifier: String): String {
    // Some sample hotel room images from Unsplash
    val sampleImages = listOf(
        "https://images.unsplash.com/photo-1566665797739-1674de7a421a",
        "https://images.unsplash.com/photo-1590490360182-c33d57733427",
        "https://images.unsplash.com/photo-1618773928121-c32242e63f39",
        "https://images.unsplash.com/photo-1595576508898-0ad5c879a061",
        "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b"
    )
    
    // Use the hash code of the identifier to pick a consistent image
    val index = abs(identifier.hashCode() % sampleImages.size)
    return sampleImages[index]
}

// Create demo room images
fun createDemoRoomImages(roomId: String, count: Int = 3): List<Image> {
    return (0 until count).map { index ->
        Image(
            id = "$roomId-image-$index",
            url = "$roomId-$index", // This will be processed by getSampleImageUrl
            roomId = roomId,
            description = "Room image ${index + 1}",
            isPrimary = index == 0
        )
    }
}

// Date extensions
fun Date.formatToDisplay(): String {
    val formatter = SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault())
    return formatter.format(this)
}

fun Date.formatToApi(): String {
    val formatter = SimpleDateFormat(Constants.DATE_FORMAT_API, Locale.getDefault())
    return formatter.format(this)
}

fun String.parseFromApi(): Date? {
    return try {
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_API, Locale.getDefault())
        formatter.parse(this)
    } catch (e: Exception) {
        null
    }
}

// Price formatting
fun Double.formatPrice(): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(this)
}

// String extensions
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    }
}

// Math extensions
fun abs(value: Int): Int {
    return if (value < 0) -value else value
}

/**
 * Show a short toast message
 */
fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

/**
 * Show a short toast message
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Generate a random hotel room image URL
 */
fun getRandomRoomImageUrl(width: Int = 400, height: Int = 300): String {
    val imageId = (1..1000).random()
    return "https://picsum.photos/id/$imageId/$width/$height"
}

/**
 * Generate a list of random room images
 */
fun generateRandomRoomImages(count: Int): List<String> {
    return (1..count).map { getRandomRoomImageUrl() }
} 