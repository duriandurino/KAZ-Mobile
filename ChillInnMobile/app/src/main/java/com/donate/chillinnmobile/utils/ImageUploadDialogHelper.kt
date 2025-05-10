package com.donate.chillinnmobile.utils

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.Image
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Helper class to handle image upload dialog and related operations
 */
class ImageUploadDialogHelper(private val fragment: Fragment) {

    private var dialog: Dialog? = null
    private var selectedImageUri: Uri? = null
    private val imagePicker by lazy { ImagePicker(fragment) }
    private val imageUploadManager by lazy { ImageUploadManager(fragment.requireContext()) }
    
    // Callback to be invoked when an image is successfully uploaded
    var onImageUploaded: ((Image) -> Unit)? = null
    
    /**
     * Show the image upload dialog
     * 
     * @param category The category for the uploaded image
     * @param entityId The ID of the entity (room, user, etc.) this image belongs to
     */
    fun showImageUploadDialog(category: String, entityId: String) {
        val context = fragment.requireContext()
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_upload_image, null)
        
        // Get references to views
        val titleTextView = dialogView.findViewById<TextView>(R.id.tvUploadTitle)
        val imagePreview = dialogView.findViewById<ImageView>(R.id.ivImagePreview)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.etImageDescription)
        val selectButton = dialogView.findViewById<Button>(R.id.btnSelectImage)
        val uploadButton = dialogView.findViewById<Button>(R.id.btnUploadImage)
        
        // Set up category-specific title
        when (category) {
            ImageUploadManager.CATEGORY_PROFILE -> titleTextView.text = "Update Profile Picture"
            ImageUploadManager.CATEGORY_ROOM -> titleTextView.text = "Add Room Image"
            ImageUploadManager.CATEGORY_REVIEW -> titleTextView.text = "Add Review Photo"
        }
        
        // Create dialog
        dialog = MaterialAlertDialogBuilder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        
        // Set up click listeners
        selectButton.setOnClickListener {
            showImageSourceDialog()
        }
        
        uploadButton.setOnClickListener {
            uploadImage(category, entityId, descriptionEditText.text.toString())
        }
        
        // Show dialog
        dialog?.show()
    }
    
    /**
     * Show dialog to choose between camera and gallery
     */
    private fun showImageSourceDialog() {
        val context = fragment.requireContext()
        val options = arrayOf(
            context.getString(R.string.take_photo),
            context.getString(R.string.choose_from_gallery),
            context.getString(R.string.cancel)
        )
        
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.select_image_source))
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> selectFromCamera()
                    1 -> selectFromGallery()
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }
    
    /**
     * Select image from camera
     */
    private fun selectFromCamera() {
        imagePicker.selectImageFromCamera { uri ->
            uri?.let { updateImagePreview(it) }
        }
    }
    
    /**
     * Select image from gallery
     */
    private fun selectFromGallery() {
        imagePicker.selectImageFromGallery { uri ->
            uri?.let { updateImagePreview(it) }
        }
    }
    
    /**
     * Update the image preview and enable upload button
     */
    private fun updateImagePreview(uri: Uri) {
        selectedImageUri = uri
        
        dialog?.findViewById<ImageView>(R.id.ivImagePreview)?.let { imageView ->
            Glide.with(fragment)
                .load(uri)
                .into(imageView)
        }
        
        // Enable upload button
        dialog?.findViewById<Button>(R.id.btnUploadImage)?.isEnabled = true
    }
    
    /**
     * Upload the selected image
     */
    private suspend fun uploadImage(category: String, entityId: String, description: String) {
        val context = fragment.requireContext()
        
        // Show loading
        showLoading(true)
        
        // Get the selected URI
        val imageUri = selectedImageUri
        if (imageUri == null) {
            fragment.showToast(context.getString(R.string.image_upload_error))
            showLoading(false)
            return
        }
        
        try {
            // Save image to local storage
            val savedUri = imageUploadManager.saveImageToLocalStorage(
                imageUri,
                category,
                entityId
            )
            
            if (savedUri != null) {
                // Create image object
                val image = Image(
                    id = System.currentTimeMillis().toString(),
                    url = savedUri.toString(),
                    description = description.takeIf { it.isNotBlank() },
                    roomId = entityId,
                    isPrimary = false
                )
                
                // Notify callback
                onImageUploaded?.invoke(image)
                
                // Show success and dismiss dialog
                fragment.showToast(context.getString(R.string.image_upload_success))
                dialog?.dismiss()
            } else {
                fragment.showToast(context.getString(R.string.image_upload_error))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fragment.showToast(context.getString(R.string.image_upload_error))
        } finally {
            showLoading(false)
        }
    }
    
    /**
     * Show or hide loading state in dialog
     */
    private fun showLoading(isLoading: Boolean) {
        dialog?.findViewById<Button>(R.id.btnUploadImage)?.isEnabled = !isLoading
        dialog?.findViewById<Button>(R.id.btnSelectImage)?.isEnabled = !isLoading
        
        // Could add a progress indicator here
    }
    
    /**
     * Dismiss the dialog
     */
    fun dismissDialog() {
        dialog?.dismiss()
        dialog = null
    }
} 