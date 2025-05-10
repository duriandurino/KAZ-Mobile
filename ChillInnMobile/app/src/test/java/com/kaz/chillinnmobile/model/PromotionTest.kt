package com.kaz.chillinnmobile.model

import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

class PromotionTest {

    @Test
    fun `isActive returns true when current time is between start and end date`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val startDate = currentTime - TimeUnit.DAYS.toMillis(1) // 1 day ago
        val endDate = currentTime + TimeUnit.DAYS.toMillis(10) // 10 days from now
        
        val promotion = Promotion(
            id = "1",
            title = "Test Promotion",
            description = "Test Description",
            discount = "20% OFF",
            imageUrl = "https://example.com/image.jpg",
            badgeText = "Limited Time",
            startDate = startDate,
            endDate = endDate,
            promotionCode = "TEST20"
        )
        
        // When
        val isActive = promotion.isActive()
        
        // Then
        assertTrue(isActive)
    }
    
    @Test
    fun `isActive returns false when current time is after end date`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val startDate = currentTime - TimeUnit.DAYS.toMillis(10) // 10 days ago
        val endDate = currentTime - TimeUnit.DAYS.toMillis(1) // 1 day ago
        
        val promotion = Promotion(
            id = "1",
            title = "Test Promotion",
            description = "Test Description",
            discount = "20% OFF",
            imageUrl = "https://example.com/image.jpg",
            badgeText = "Limited Time",
            startDate = startDate,
            endDate = endDate,
            promotionCode = "TEST20"
        )
        
        // When
        val isActive = promotion.isActive()
        
        // Then
        assertFalse(isActive)
    }
    
    @Test
    fun `isActive returns false when current time is before start date`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val startDate = currentTime + TimeUnit.DAYS.toMillis(1) // 1 day from now
        val endDate = currentTime + TimeUnit.DAYS.toMillis(10) // 10 days from now
        
        val promotion = Promotion(
            id = "1",
            title = "Test Promotion",
            description = "Test Description",
            discount = "20% OFF",
            imageUrl = "https://example.com/image.jpg",
            badgeText = "Limited Time",
            startDate = startDate,
            endDate = endDate,
            promotionCode = "TEST20"
        )
        
        // When
        val isActive = promotion.isActive()
        
        // Then
        assertFalse(isActive)
    }
    
    @Test
    fun `getDurationInDays returns correct number of days`() {
        // Given
        val startDate = System.currentTimeMillis()
        val endDate = startDate + TimeUnit.DAYS.toMillis(30) // 30 days later
        
        val promotion = Promotion(
            id = "1",
            title = "Test Promotion",
            description = "Test Description",
            discount = "20% OFF",
            imageUrl = "https://example.com/image.jpg",
            badgeText = "Limited Time",
            startDate = startDate,
            endDate = endDate,
            promotionCode = "TEST20"
        )
        
        // When
        val durationInDays = promotion.getDurationInDays()
        
        // Then
        assertEquals(30, durationInDays)
    }
    
    @Test
    fun `getDaysRemaining returns correct days remaining when promotion is active`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val startDate = currentTime - TimeUnit.DAYS.toMillis(5) // 5 days ago
        val endDate = currentTime + TimeUnit.DAYS.toMillis(10) // 10 days from now
        
        val promotion = Promotion(
            id = "1",
            title = "Test Promotion",
            description = "Test Description",
            discount = "20% OFF",
            imageUrl = "https://example.com/image.jpg",
            badgeText = "Limited Time",
            startDate = startDate,
            endDate = endDate,
            promotionCode = "TEST20"
        )
        
        // When
        val daysRemaining = promotion.getDaysRemaining()
        
        // Then
        // Note: This is approximate due to millisecond precision
        assertTrue(daysRemaining in 9..10)
    }
    
    @Test
    fun `getDaysRemaining returns zero when promotion has ended`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val startDate = currentTime - TimeUnit.DAYS.toMillis(20) // 20 days ago
        val endDate = currentTime - TimeUnit.DAYS.toMillis(10) // 10 days ago
        
        val promotion = Promotion(
            id = "1",
            title = "Test Promotion",
            description = "Test Description",
            discount = "20% OFF",
            imageUrl = "https://example.com/image.jpg",
            badgeText = "Limited Time",
            startDate = startDate,
            endDate = endDate,
            promotionCode = "TEST20"
        )
        
        // When
        val daysRemaining = promotion.getDaysRemaining()
        
        // Then
        assertEquals(0, daysRemaining)
    }
} 