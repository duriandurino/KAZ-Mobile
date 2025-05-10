package com.donate.chillinnmobile.repository

import android.content.Context
import android.content.SharedPreferences
import com.donate.chillinnmobile.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Collections

/**
 * Repository for managing search history data
 */
class SearchHistoryRepository(context: Context) {

    companion object {
        private const val PREFS_NAME = "search_history_prefs"
        private const val KEY_SEARCH_HISTORY = "search_history"
        private const val KEY_POPULAR_SEARCHES = "popular_searches"
        private const val MAX_HISTORY_ITEMS = 15
        private const val MAX_POPULAR_ITEMS = 10
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    /**
     * Add a query to search history
     * Also updates the popular searches count
     */
    suspend fun addToHistory(query: String) = withContext(Dispatchers.IO) {
        if (query.isBlank() || query.length < 2) return@withContext

        val trimmedQuery = query.trim().lowercase()
        val history = getSearchHistory().toMutableList()

        // Remove if exists (to avoid duplicates)
        history.remove(trimmedQuery)

        // Add to the start of the list
        history.add(0, trimmedQuery)

        // Trim list if too long
        while (history.size > MAX_HISTORY_ITEMS) {
            history.removeAt(history.size - 1)
        }

        // Save updated history
        saveSearchHistory(history)

        // Update popular searches
        updatePopularSearches(trimmedQuery)
    }

    /**
     * Get search history list
     */
    suspend fun getSearchHistory(): List<String> = withContext(Dispatchers.IO) {
        val historyJson = sharedPreferences.getString(KEY_SEARCH_HISTORY, null) ?: return@withContext emptyList()

        try {
            val jsonArray = JSONArray(historyJson)
            val history = mutableListOf<String>()

            for (i in 0 until jsonArray.length()) {
                history.add(jsonArray.getString(i))
            }

            return@withContext history
        } catch (e: JSONException) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    /**
     * Get popular searches with their counts
     */
    suspend fun getPopularSearches(limit: Int = MAX_POPULAR_ITEMS): List<Pair<String, Int>> = withContext(Dispatchers.IO) {
        val popularJson = sharedPreferences.getString(KEY_POPULAR_SEARCHES, null) ?: return@withContext emptyList()

        try {
            val result = mutableListOf<Pair<String, Int>>()
            val jsonArray = JSONArray(popularJson)

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val query = item.getString("query")
                val count = item.getInt("count")
                result.add(Pair(query, count))
            }

            // Sort by count (descending) and limit
            return@withContext result
                .sortedByDescending { it.second }
                .take(limit)
        } catch (e: JSONException) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    /**
     * Clear search history
     */
    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        sharedPreferences.edit().remove(KEY_SEARCH_HISTORY).apply()
    }

    /**
     * Update popular searches with a new query
     */
    private fun updatePopularSearches(query: String) {
        val popularJson = sharedPreferences.getString(KEY_POPULAR_SEARCHES, null)
        val popularMap = mutableMapOf<String, Int>()

        // Parse existing data
        if (popularJson != null) {
            try {
                val jsonArray = JSONArray(popularJson)

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val existingQuery = item.getString("query")
                    val count = item.getInt("count")
                    popularMap[existingQuery] = count
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        // Update count for this query
        popularMap[query] = (popularMap[query] ?: 0) + 1

        // Convert back to JSON
        val jsonArray = JSONArray()
        for ((q, count) in popularMap) {
            val item = JSONObject().apply {
                put("query", q)
                put("count", count)
            }
            jsonArray.put(item)
        }

        // Save updated data
        sharedPreferences.edit().putString(KEY_POPULAR_SEARCHES, jsonArray.toString()).apply()
    }

    /**
     * Save search history list to storage
     */
    private fun saveSearchHistory(history: List<String>) {
        val jsonArray = JSONArray()
        for (item in history) {
            jsonArray.put(item)
        }

        sharedPreferences.edit().putString(KEY_SEARCH_HISTORY, jsonArray.toString()).apply()
    }
} 