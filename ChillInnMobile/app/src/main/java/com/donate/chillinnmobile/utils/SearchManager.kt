package com.donate.chillinnmobile.utils

import com.donate.chillinnmobile.model.Room
import java.util.Locale

/**
 * Utility class for handling complex search operations with fuzzy matching
 */
class SearchManager {

    companion object {
        private const val FUZZY_THRESHOLD = 0.7 // Match score threshold for fuzzy matching
        
        /**
         * Search rooms using fuzzy matching algorithm
         * 
         * @param query Search query
         * @param rooms List of rooms to search in
         * @param fieldWeights Map of field names to their respective weights in search
         * @return List of rooms sorted by relevance
         */
        fun searchRooms(
            query: String,
            rooms: List<Room>,
            fieldWeights: Map<String, Float> = defaultFieldWeights()
        ): List<Room> {
            if (query.isBlank()) return rooms
            
            val lowercaseQuery = query.lowercase(Locale.getDefault())
            val results = mutableListOf<Pair<Room, Float>>() // Room with its relevance score
            
            // Calculate relevance score for each room
            for (room in rooms) {
                val score = calculateRelevanceScore(lowercaseQuery, room, fieldWeights)
                
                // Only include rooms with score above threshold
                if (score > FUZZY_THRESHOLD) {
                    results.add(Pair(room, score))
                }
            }
            
            // Sort by relevance score (highest first)
            return results.sortedByDescending { it.second }.map { it.first }
        }
        
        /**
         * Calculate relevance score for a room based on query
         */
        private fun calculateRelevanceScore(
            query: String,
            room: Room,
            fieldWeights: Map<String, Float>
        ): Float {
            var totalScore = 0f
            var totalWeight = 0f
            
            // Check room name
            fieldWeights["name"]?.let { weight ->
                totalWeight += weight
                val nameScore = fuzzyMatch(query, room.name.lowercase(Locale.getDefault()))
                totalScore += nameScore * weight
            }
            
            // Check room type
            fieldWeights["roomType"]?.let { weight ->
                totalWeight += weight
                val typeScore = fuzzyMatch(query, room.roomType.lowercase(Locale.getDefault()))
                totalScore += typeScore * weight
            }
            
            // Check room description
            fieldWeights["description"]?.let { weight ->
                totalWeight += weight
                val descScore = fuzzyMatch(query, room.description.lowercase(Locale.getDefault()))
                totalScore += descScore * weight
            }
            
            // Check amenities
            fieldWeights["amenities"]?.let { weight ->
                room.amenities?.let { amenities ->
                    if (amenities.isNotEmpty()) {
                        totalWeight += weight
                        
                        // Find best matching amenity
                        var bestAmenityScore = 0f
                        for (amenity in amenities) {
                            val amenityScore = fuzzyMatch(query, amenity.lowercase(Locale.getDefault()))
                            if (amenityScore > bestAmenityScore) {
                                bestAmenityScore = amenityScore
                            }
                            
                            // Optimization: if we found a perfect match, no need to check others
                            if (bestAmenityScore >= 1f) break
                        }
                        
                        totalScore += bestAmenityScore * weight
                    }
                }
            }
            
            // Return normalized score (0-1 range)
            return if (totalWeight > 0) totalScore / totalWeight else 0f
        }
        
        /**
         * Perform fuzzy matching between query and text
         * Uses Levenshtein distance for fuzzy matching
         * 
         * @return Score between 0 (no match) and 1 (perfect match)
         */
        private fun fuzzyMatch(query: String, text: String): Float {
            // 1. Check for exact match
            if (text.contains(query)) {
                return 1.0f
            }
            
            // 2. Check for word match (any word in text matches query)
            val words = text.split(" ", "-", ",", ".", ":", ";")
            for (word in words) {
                if (word == query) {
                    return 1.0f
                }
            }
            
            // 3. Check for partial match (query is part of any word)
            for (word in words) {
                if (word.contains(query)) {
                    return 0.9f
                }
            }
            
            // 4. Check for prefix match (any word starts with query)
            for (word in words) {
                if (word.startsWith(query)) {
                    return 0.8f
                }
            }
            
            // 5. Use Levenshtein distance for other matches
            if (query.length <= 3) {
                // For short queries, use whole query
                return levenshteinScore(query, text)
            } else {
                // For longer queries, check individual words in text
                var bestScore = 0f
                for (word in words) {
                    val score = levenshteinScore(query, word)
                    if (score > bestScore) {
                        bestScore = score
                    }
                    
                    // Optimization: if we found a very good match, stop checking
                    if (bestScore > 0.8f) break
                }
                return bestScore
            }
        }
        
        /**
         * Calculate Levenshtein distance score between two strings
         * @return Normalized score between 0 (no match) and 1 (perfect match)
         */
        private fun levenshteinScore(s1: String, s2: String): Float {
            // Empty strings edge case
            if (s1.isEmpty()) return if (s2.isEmpty()) 1.0f else 0.0f
            if (s2.isEmpty()) return 0.0f
            
            // Initialize distance matrix
            val s1Length = s1.length
            val s2Length = s2.length
            val distances = Array(s1Length + 1) { IntArray(s2Length + 1) }
            
            // Initialize first row and column
            for (i in 0..s1Length) {
                distances[i][0] = i
            }
            for (j in 0..s2Length) {
                distances[0][j] = j
            }
            
            // Fill distance matrix
            for (i in 1..s1Length) {
                for (j in 1..s2Length) {
                    val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                    distances[i][j] = minOf(
                        distances[i - 1][j] + 1,      // deletion
                        distances[i][j - 1] + 1,      // insertion
                        distances[i - 1][j - 1] + cost // substitution
                    )
                }
            }
            
            // Get distance
            val distance = distances[s1Length][s2Length]
            
            // Normalize to 0-1 range (1 is perfect match)
            val maxDistance = maxOf(s1Length, s2Length)
            return if (maxDistance == 0) 1.0f else 1.0f - (distance.toFloat() / maxDistance)
        }
        
        /**
         * Default weight for each searchable field
         */
        private fun defaultFieldWeights(): Map<String, Float> {
            return mapOf(
                "name" to 1.0f,
                "roomType" to 0.8f,
                "description" to 0.5f,
                "amenities" to 0.7f
            )
        }
    }
} 