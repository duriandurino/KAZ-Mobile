package com.donate.chillinnmobile.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Manager class to handle image uploads and local storage
 */
class ImageUploadManager(private val context: Context) {

    companion object {
        private const val IMAGE_QUALITY = 85
        private const val MAX_IMAGE_WIDTH = 1200
        private const val MAX_IMAGE_HEIGHT = 1200
        private const val PROVIDER_AUTHORITY_SUFFIX = ".fileprovider"
        private const val IMAGES_DIR = "uploaded_images"
        
        // Image categories
        const val CATEGORY_PROFILE = "profile"
        const val CATEGORY_ROOM = "room"
        const val CATEGORY_REVIEW = "review"
    }
    
    /**
     * Save an image from Uri to local storage
     * 
     * @param imageUri The uri of the selected image
     * @param category The category folder to save to (profile, room, etc)
     * @param imageId Optional ID for the image, will generate one if not provided
     * @return The local Uri of the saved image or null if failed
     */
    suspend fun saveImageToLocalStorage(
        imageUri: Uri,
        category: String,
        imageId: String = UUID.randomUUID().toString()
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            // Get the input stream from uri
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            // Compress the bitmap
            val compressedBitmap = compressBitmap(bitmap)
            
            // Create directory if it doesn't exist
            val directory = File(context.filesDir, "$IMAGES_DIR/$category")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            
            // Generate filename with timestamp
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${category}_${timeStamp}_${imageId}.jpg"
            
            // Create the file and write the compressed bitmap
            val file = File(directory, fileName)
            val outputStream = FileOutputStream(file)
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)
            outputStream.flush()
            outputStream.close()
            
            // Create and return a content URI for the saved file
            FileProvider.getUriForFile(
                context,
                context.packageName + PROVIDER_AUTHORITY_SUFFIX,
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Delete an image from local storage
     * 
     * @param imageUri The uri of the image to delete
     * @return True if deleted successfully, false otherwise
     */
    suspend fun deleteImage(imageUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(imageUri.path ?: "")
            if (file.exists()) {
                file.delete()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Get all images from a specific category
     * 
     * @param category The category to get images from
     * @return List of image Uris
     */
    suspend fun getImagesFromCategory(category: String): List<Uri> = withContext(Dispatchers.IO) {
        val directory = File(context.filesDir, "$IMAGES_DIR/$category")
        if (!directory.exists()) {
            return@withContext emptyList<Uri>()
        }
        
        directory.listFiles()?.mapNotNull { file ->
            if (file.isFile && (file.name.endsWith(".jpg") || file.name.endsWith(".jpeg") || file.name.endsWith(".png"))) {
                FileProvider.getUriForFile(
                    context,
                    context.packageName + PROVIDER_AUTHORITY_SUFFIX,
                    file
                )
            } else {
                null
            }
        } ?: emptyList()
    }
    
    /**
     * Compress bitmap to reduce file size
     * 
     * @param original The original bitmap
     * @return Compressed bitmap
     */
    private fun compressBitmap(original: Bitmap): Bitmap {
        // Calculate scaling factor to keep aspect ratio
        var width = original.width
        var height = original.height
        
        var scaleFactor = 1.0f
        
        if (width > MAX_IMAGE_WIDTH || height > MAX_IMAGE_HEIGHT) {
            val widthScaleFactor = MAX_IMAGE_WIDTH.toFloat() / width
            val heightScaleFactor = MAX_IMAGE_HEIGHT.toFloat() / height
            
            scaleFactor = widthScaleFactor.coerceAtMost(heightScaleFactor)
            
            width = (width * scaleFactor).toInt()
            height = (height * scaleFactor).toInt()
        }
        
        return if (scaleFactor < 1.0f) {
            Bitmap.createScaledBitmap(original, width, height, true)
        } else {
            original
        }
    }
} 