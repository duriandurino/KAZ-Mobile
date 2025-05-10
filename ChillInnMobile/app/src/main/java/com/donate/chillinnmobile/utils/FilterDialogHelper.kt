package com.donate.chillinnmobile.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.RatingBar
import android.widget.TextView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.model.RoomFilterModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.RangeSlider
import java.util.Locale

/**
 * Helper class for handling room filter dialog
 */
class FilterDialogHelper {
    companion object {
        /**
         * Shows a filter dialog with the provided current filter values
         * @param context The context
         * @param currentFilter The current filter values to pre-populate
         * @param onFilterApplied Callback when filters are applied
         */
        fun showFilterDialog(
            context: Context,
            currentFilter: RoomFilterModel = RoomFilterModel.DEFAULT,
            onFilterApplied: (RoomFilterModel) -> Unit
        ) {
            // Inflate the dialog layout
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_filter_rooms, null)
            
            // Initialize dialog components
            val priceRangeSlider = dialogView.findViewById<RangeSlider>(R.id.priceRangeSlider)
            val tvPriceRange = dialogView.findViewById<TextView>(R.id.tvPriceRange)
            val roomTypeChipGroup = dialogView.findViewById<ChipGroup>(R.id.roomTypeChipGroup)
            val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
            
            // Initialize amenity checkboxes
            val checkBoxWifi = dialogView.findViewById<CheckBox>(R.id.checkBoxWifi)
            val checkBoxBreakfast = dialogView.findViewById<CheckBox>(R.id.checkBoxBreakfast)
            val checkBoxParking = dialogView.findViewById<CheckBox>(R.id.checkBoxParking)
            val checkBoxAirCon = dialogView.findViewById<CheckBox>(R.id.checkBoxAirCon)
            val checkBoxSwimmingPool = dialogView.findViewById<CheckBox>(R.id.checkBoxSwimmingPool)
            
            // Set up the dialog builder
            val dialogBuilder = MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
            
            // Create the dialog
            val dialog = dialogBuilder.create()
            
            // Set initial values from current filter
            priceRangeSlider.setValues(currentFilter.minPrice.toFloat(), currentFilter.maxPrice.toFloat())
            updatePriceRangeText(tvPriceRange, currentFilter.minPrice, currentFilter.maxPrice)
            
            // Set up room type chips based on current filter
            setUpRoomTypeChips(roomTypeChipGroup, currentFilter.roomTypes)
            
            // Set up amenity checkboxes based on current filter
            setUpAmenityCheckboxes(
                currentFilter.amenities,
                checkBoxWifi,
                checkBoxBreakfast,
                checkBoxParking,
                checkBoxAirCon,
                checkBoxSwimmingPool
            )
            
            // Set rating
            ratingBar.rating = currentFilter.minRating
            
            // Set up price range slider change listener
            priceRangeSlider.addOnChangeListener { slider, _, _ ->
                val values = slider.values
                val min = values[0].toDouble()
                val max = values[1].toDouble()
                updatePriceRangeText(tvPriceRange, min, max)
            }
            
            // Set up button click listeners
            dialogView.findViewById<Button>(R.id.btnReset).setOnClickListener {
                // Reset to default values
                priceRangeSlider.setValues(50f, 500f)
                updatePriceRangeText(tvPriceRange, 50.0, 500.0)
                
                // Reset room type chips
                for (i in 0 until roomTypeChipGroup.childCount) {
                    val chip = roomTypeChipGroup.getChildAt(i) as? Chip
                    chip?.isChecked = false
                }
                
                // Reset amenity checkboxes
                checkBoxWifi.isChecked = false
                checkBoxBreakfast.isChecked = false
                checkBoxParking.isChecked = false
                checkBoxAirCon.isChecked = false
                checkBoxSwimmingPool.isChecked = false
                
                // Reset rating
                ratingBar.rating = 0f
            }
            
            dialogView.findViewById<Button>(R.id.btnApply).setOnClickListener {
                // Get selected filter values
                val values = priceRangeSlider.values
                val minPrice = values[0].toDouble()
                val maxPrice = values[1].toDouble()
                
                // Get selected room types
                val selectedRoomTypes = getSelectedRoomTypes(roomTypeChipGroup)
                
                // Get selected amenities
                val selectedAmenities = getSelectedAmenities(
                    checkBoxWifi,
                    checkBoxBreakfast,
                    checkBoxParking,
                    checkBoxAirCon,
                    checkBoxSwimmingPool
                )
                
                // Create new filter model
                val newFilter = RoomFilterModel(
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    roomTypes = selectedRoomTypes,
                    amenities = selectedAmenities,
                    minRating = ratingBar.rating
                )
                
                // Call the callback with the new filter
                onFilterApplied(newFilter)
                
                // Dismiss the dialog
                dialog.dismiss()
            }
            
            // Show the dialog
            dialog.show()
        }
        
        private fun updatePriceRangeText(textView: TextView, min: Double, max: Double) {
            textView.text = String.format(Locale.US, "$%.0f - $%.0f", min, max)
        }
        
        private fun setUpRoomTypeChips(chipGroup: ChipGroup, selectedRoomTypes: List<String>) {
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as? Chip
                chip?.let {
                    it.isChecked = selectedRoomTypes.contains(it.text.toString())
                }
            }
        }
        
        private fun getSelectedRoomTypes(chipGroup: ChipGroup): List<String> {
            val selectedTypes = mutableListOf<String>()
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as? Chip
                if (chip?.isChecked == true) {
                    selectedTypes.add(chip.text.toString())
                }
            }
            return selectedTypes
        }
        
        private fun setUpAmenityCheckboxes(
            selectedAmenities: List<String>,
            vararg checkBoxes: CheckBox
        ) {
            checkBoxes.forEach { checkBox ->
                checkBox.isChecked = selectedAmenities.contains(checkBox.text.toString())
            }
        }
        
        private fun getSelectedAmenities(vararg checkBoxes: CheckBox): List<String> {
            return checkBoxes.filter { it.isChecked }.map { it.text.toString() }
        }
    }
} 