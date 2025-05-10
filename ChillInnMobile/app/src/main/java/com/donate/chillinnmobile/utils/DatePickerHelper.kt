package com.donate.chillinnmobile.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class for handling date selection using DatePickerDialog.
 * Provides methods for showing date picker dialogs and formatting dates,
 * specifically for booking date selections.
 */
object DatePickerHelper {

    private const val DATE_FORMAT = "MMM dd, yyyy"

    /**
     * Interface for date selection callback
     */
    interface OnDateSelectedListener {
        fun onDateSelected(date: Date)
    }

    /**
     * Show date picker dialog
     * @param context Context
     * @param editText EditText to display the selected date
     * @param initialDate Optional initial date to show in the picker
     * @param minDate Optional minimum selectable date
     * @param maxDate Optional maximum selectable date
     * @param listener Optional callback for date selection
     */
    fun showDatePicker(
        context: Context,
        editText: EditText,
        initialDate: Date? = null,
        minDate: Date? = null,
        maxDate: Date? = null,
        listener: OnDateSelectedListener? = null
    ) {
        val calendar = Calendar.getInstance()
        
        // Use initialDate if provided, otherwise use current date
        initialDate?.let {
            calendar.time = it
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                
                val selectedDate = selectedCalendar.time
                
                // Format and display the selected date
                editText.setText(formatDate(selectedDate))
                
                // Notify listener
                listener?.onDateSelected(selectedDate)
            },
            year,
            month,
            day
        )

        // Set min date if provided
        minDate?.let {
            datePickerDialog.datePicker.minDate = it.time
        }

        // Set max date if provided
        maxDate?.let {
            datePickerDialog.datePicker.maxDate = it.time
        }

        datePickerDialog.show()
    }

    /**
     * Format date as a readable string
     * @param date Date to format
     * @param format Optional date format pattern (defaults to "MMM dd, yyyy")
     * @return Formatted date string
     */
    fun formatDate(date: Date, format: String = DATE_FORMAT): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * Parse date string to Date object
     * @param dateString Date string
     * @param format Format pattern for parsing (defaults to "MMM dd, yyyy")
     * @return Date object or null if parsing fails
     */
    fun parseDate(dateString: String, format: String = DATE_FORMAT): Date? {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get today's date with time set to 00:00:00
     * @return Today's date at midnight
     */
    fun getToday(): Date {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    /**
     * Get a date that is a certain number of days from today
     * @param days Number of days to add to today
     * @return Date that is n days from today
     */
    fun getTodayPlus(days: Int): Date {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, days)
        }.time
    }

    /**
     * Calculate the number of nights between two dates
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @return Number of nights, or 0 if dates are invalid
     */
    fun calculateNights(checkInDate: Date, checkOutDate: Date): Int {
        if (checkOutDate.before(checkInDate) || checkInDate == checkOutDate) {
            return 0
        }
        
        val diffMillis = checkOutDate.time - checkInDate.time
        return (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    }
} 