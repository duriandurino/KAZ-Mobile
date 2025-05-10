package com.example.hotelapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.model.Promotion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

/**
 * ViewModel for managing promotions data.
 * Provides LiveData for promotions list, loading status, and errors.
 */
class PromotionViewModel : ViewModel() {

    private val _promotions = MutableLiveData<List<Promotion>>()
    val promotions: LiveData<List<Promotion>> = _promotions
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Load promotions from the data source
     */
    fun loadPromotions() {
        _loading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                // Simulate network delay
                delay(1500)
                
                // In a real app, this would be a repository call
                _promotions.value = getMockPromotions()
            } catch (e: Exception) {
                _error.value = "Failed to load promotions: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    /**
     * Refresh promotions data
     */
    fun refreshPromotions() {
        _error.value = null
        
        viewModelScope.launch {
            try {
                // Simulate network delay
                delay(1000)
                
                // In a real app, this would be a repository call
                _promotions.value = getMockPromotions()
            } catch (e: Exception) {
                _error.value = "Failed to refresh promotions: ${e.message}"
            }
        }
    }
    
    /**
     * Get promotion by ID
     * @param id Promotion ID
     * @return Promotion or null if not found
     */
    fun getPromotionById(id: String): Promotion? {
        return promotions.value?.find { it.id == id }
    }
    
    /**
     * Mock data generator for testing
     */
    private fun getMockPromotions(): List<Promotion> {
        val now = Date()
        val tomorrow = Date(now.time + 24 * 60 * 60 * 1000)
        val nextWeek = Date(now.time + 7 * 24 * 60 * 60 * 1000)
        val nextMonth = Date(now.time + 30 * 24 * 60 * 60 * 1000)
        
        return listOf(
            Promotion(
                id = "promo1",
                title = "Summer Exclusive",
                shortDescription = "30% off on all Deluxe Rooms",
                description = "Book any Deluxe Room for stays between June and August and enjoy 30% off the regular rate. Includes complimentary breakfast and late checkout.",
                discountPercentage = 30,
                startDate = now,
                endDate = nextMonth,
                imageUrl = "https://example.com/summer_promo.jpg",
                promoCode = "SUMMER30",
                isExclusive = true,
                badgeText = "Limited Time",
                targetRoomTypes = listOf("DELUXE"),
                minNights = 2,
                termsAndConditions = "Offer valid for stays between June 1 and August 31. Subject to availability."
            ),
            Promotion(
                id = "promo2",
                title = "Weekend Getaway",
                shortDescription = "20% off weekend bookings",
                description = "Enjoy a special weekend rate with 20% off when you book any room for Friday and Saturday night stays.",
                discountPercentage = 20,
                startDate = now,
                endDate = nextWeek,
                imageUrl = "https://example.com/weekend_promo.jpg",
                promoCode = "WEEKEND20",
                isExclusive = false,
                badgeText = "Weekends Only",
                targetRoomTypes = listOf("STANDARD", "DELUXE", "SUITE"),
                minNights = 2,
                termsAndConditions = "Valid for Friday and Saturday stays only. Must book both nights."
            ),
            Promotion(
                id = "promo3",
                title = "Last Minute Deal",
                shortDescription = "25% off for bookings within 48 hours",
                description = "Book within 48 hours of your stay and receive 25% off the regular rate on any room type.",
                discountPercentage = 25,
                startDate = now,
                endDate = tomorrow,
                imageUrl = "https://example.com/lastminute_promo.jpg",
                promoCode = "LAST25",
                isExclusive = false,
                badgeText = "Ends Soon",
                termsAndConditions = "Subject to availability. Cannot be combined with other offers."
            ),
            Promotion(
                id = "promo4",
                title = "Loyalty Members Special",
                shortDescription = "Extra 10% off for loyalty members",
                description = "Loyalty program members receive an additional 10% off any booking, combinable with other promotions.",
                discountPercentage = 10,
                startDate = now,
                endDate = nextMonth,
                imageUrl = "https://example.com/loyalty_promo.jpg",
                promoCode = "LOYAL10",
                isExclusive = true,
                badgeText = "Members Only",
                termsAndConditions = "Must be a registered loyalty program member prior to booking."
            ),
            Promotion(
                id = "promo5",
                title = "Extended Stay Discount",
                shortDescription = "Up to 35% off for 5+ night stays",
                description = "Book 5 or more consecutive nights and save up to 35% on your entire stay. The longer you stay, the more you save!",
                discountPercentage = 35,
                startDate = now,
                endDate = nextMonth,
                imageUrl = "https://example.com/extendedstay_promo.jpg",
                promoCode = "STAY5PLUS",
                isExclusive = false,
                badgeText = "Long Stay",
                minNights = 5,
                termsAndConditions = "Minimum 5-night stay required. Discount varies by length of stay and room type."
            ),
            // New promotion that would trigger a notification
            Promotion(
                id = UUID.randomUUID().toString(), // Random ID ensures it's seen as new each time
                title = "Flash Sale",
                shortDescription = "50% off Presidential Suite",
                description = "24-hour flash sale! Book our luxurious Presidential Suite at half price for stays in the next 3 months.",
                discountPercentage = 50,
                startDate = Date(), // Just started now
                endDate = Date(now.time + 24 * 60 * 60 * 1000), // Ends in 24 hours
                imageUrl = "https://example.com/flash_sale.jpg",
                promoCode = "FLASH50",
                isExclusive = true,
                badgeText = "Flash Sale",
                targetRoomTypes = listOf("PRESIDENTIAL_SUITE"),
                termsAndConditions = "Valid for 24 hours only. Subject to availability."
            )
        )
    }
} 