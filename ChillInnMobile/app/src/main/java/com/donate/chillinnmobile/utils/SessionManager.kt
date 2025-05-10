package com.donate.chillinnmobile.utils

import android.content.Context
import android.content.SharedPreferences
import com.donate.chillinnmobile.model.User
import com.donate.chillinnmobile.model.UserType
import com.google.gson.Gson

/**
 * SessionManager for managing user authentication and session data
 */
object SessionManager {
    private const val PREF_NAME = "ChillInnSession"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_TYPE = "user_type"
    private const val KEY_USER_PROFILE = "user_profile"
    
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    
    /**
     * Initialize the SessionManager with application context
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Save user authentication data
     */
    fun saveAuthData(token: String, userId: String, email: String, userType: UserType) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_TYPE, userType.name)
        editor.apply()
    }
    
    /**
     * Save full user profile
     */
    fun saveUserProfile(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString(KEY_USER_PROFILE, userJson).apply()
    }
    
    /**
     * Get the authentication token
     */
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }
    
    /**
     * Get the user ID
     */
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
    
    /**
     * Get the user email
     */
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }
    
    /**
     * Get the user type
     */
    fun getUserType(): UserType? {
        val userTypeStr = sharedPreferences.getString(KEY_USER_TYPE, null)
        return userTypeStr?.let { UserType.valueOf(it) }
    }
    
    /**
     * Get the full user profile
     */
    fun getUserProfile(): User? {
        val userJson = sharedPreferences.getString(KEY_USER_PROFILE, null)
        return userJson?.let { gson.fromJson(it, User::class.java) }
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }
    
    /**
     * Check if user is admin
     */
    fun isAdmin(): Boolean {
        return getUserType() == UserType.ADMIN
    }
    
    /**
     * Clear all session data (logout)
     */
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
} 