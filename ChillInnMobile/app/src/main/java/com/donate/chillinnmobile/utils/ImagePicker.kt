package com.donate.chillinnmobile.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility class to handle image picking from gallery or camera
 */
class ImagePicker(private val fragment: Fragment) {

    companion object {
        const val PERMISSION_CAMERA = Manifest.permission.CAMERA
        const val PERMISSION_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
        const val PROVIDER_AUTHORITY_SUFFIX = ".fileprovider"
    }

    private var currentPhotoPath: String? = null
    private var imagePickerCallback: ((Uri?) -> Unit)? = null

    // Permission request launchers
    private val requestCameraPermission = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePictureFromCamera()
        } else {
            imagePickerCallback?.invoke(null)
        }
    }

    private val requestStoragePermission = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageFromGallery()
        } else {
            imagePickerCallback?.invoke(null)
        }
    }

    // Activity result launchers
    private val takePictureLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoPath?.let { path ->
                val photoFile = File(path)
                val photoUri = FileProvider.getUriForFile(
                    fragment.requireContext(),
                    fragment.requireContext().packageName + PROVIDER_AUTHORITY_SUFFIX,
                    photoFile
                )
                imagePickerCallback?.invoke(photoUri)
            } ?: run {
                imagePickerCallback?.invoke(null)
            }
        } else {
            imagePickerCallback?.invoke(null)
        }
    }

    private val galleryPickerLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            imagePickerCallback?.invoke(selectedImageUri)
        } else {
            imagePickerCallback?.invoke(null)
        }
    }

    /**
     * Check and request camera permission if needed, then launch camera intent
     */
    fun selectImageFromCamera(callback: (Uri?) -> Unit) {
        imagePickerCallback = callback
        
        when {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                PERMISSION_CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePictureFromCamera()
            }
            else -> {
                requestCameraPermission.launch(PERMISSION_CAMERA)
            }
        }
    }

    /**
     * Check and request storage permission if needed, then launch gallery intent
     */
    fun selectImageFromGallery(callback: (Uri?) -> Unit) {
        imagePickerCallback = callback
        
        when {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                PERMISSION_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickImageFromGallery()
            }
            else -> {
                requestStoragePermission.launch(PERMISSION_STORAGE)
            }
        }
    }

    /**
     * Show image source picker dialog to let user choose camera or gallery
     */
    fun showImageSourceDialog(callback: (Uri?) -> Unit) {
        imagePickerCallback = callback
        
        // In a real implementation, this would show a dialog
        // For now, we'll just go to gallery for simplicity
        selectImageFromGallery(callback)
    }

    /**
     * Launch camera intent
     */
    private fun takePictureFromCamera() {
        val photoFile = createImageFile()
        photoFile?.let {
            val photoURI = FileProvider.getUriForFile(
                fragment.requireContext(),
                fragment.requireContext().packageName + PROVIDER_AUTHORITY_SUFFIX,
                it
            )
            
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            takePictureLauncher.launch(takePictureIntent)
        }
    }

    /**
     * Launch gallery intent
     */
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryPickerLauncher.launch(intent)
    }

    /**
     * Create a temporary file for storing camera images
     */
    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
            currentPhotoPath = file.absolutePath
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
} 