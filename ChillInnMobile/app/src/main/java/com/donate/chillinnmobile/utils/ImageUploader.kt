package com.donate.chillinnmobile.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.CloudinaryAndroid
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Utility for uploading images to Cloudinary
 */
object ImageUploader {
    private const val TAG = "ImageUploader"
    private const val CLOUDINARY_CLOUD_NAME = "your_cloud_name" // Replace with actual cloud name
    private const val UPLOAD_PRESET = "hotel_app" // Replace with actual upload preset
    
    private lateinit var cloudinary: CloudinaryAndroid
    
    /**
     * Initialize Cloudinary with application context
     */
    fun init(context: Context) {
        cloudinary = CloudinaryAndroid.init(context, CLOUDINARY_CLOUD_NAME)
    }
    
    /**
     * Upload an image file to Cloudinary
     * @param file The file to upload
     * @param folder Optional folder to store the image in
     * @return URL of the uploaded image or null if upload failed
     */
    suspend fun uploadImage(file: File, folder: String = "hotel_images"): String? {
        val deferred = CompletableDeferred<String?>()
        
        try {
            cloudinary.upload(file.absolutePath)
                .option("folder", folder)
                .unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d(TAG, "Upload started for $requestId")
                    }
                    
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = (bytes * 100) / totalBytes
                        Log.d(TAG, "Upload progress for $requestId: $progress%")
                    }
                    
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        Log.d(TAG, "Upload success for $requestId: $url")
                        deferred.complete(url)
                    }
                    
                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e(TAG, "Upload error for $requestId: ${error.description}")
                        deferred.complete(null)
                    }
                    
                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        Log.d(TAG, "Upload rescheduled for $requestId: ${error.description}")
                    }
                })
                .dispatch()
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading image", e)
            deferred.complete(null)
        }
        
        return deferred.await()
    }
    
    /**
     * Convert a content URI to a File
     */
    suspend fun uriToFile(context: Context, uri: Uri, fileName: String): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                val file = File(context.cacheDir, fileName)
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(4 * 1024) // 4k buffer
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                }
                return@withContext file
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error converting URI to file", e)
        }
        return@withContext null
    }
    
    /**
     * Delete an image from Cloudinary
     * @param publicId The public ID of the image to delete
     * @return Whether deletion was successful
     */
    suspend fun deleteImage(publicId: String): Boolean {
        // Note: Cloudinary Android SDK doesn't directly support deletion
        // This would typically be handled via a server endpoint
        // For demonstration purposes, this is a placeholder
        return true
    }
} 