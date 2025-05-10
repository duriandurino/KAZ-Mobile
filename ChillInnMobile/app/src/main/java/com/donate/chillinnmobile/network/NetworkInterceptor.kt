package com.donate.chillinnmobile.network

import android.util.Log
import com.donate.chillinnmobile.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Network interceptor for adding authentication headers and logging
 */
class NetworkInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        
        // Add common headers
        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("Accept", "application/json")
        
        // Add auth token if available
        val token = SessionManager.getAuthToken()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        val request = requestBuilder.build()
        
        // Log outgoing request
        Log.d(TAG, "Request: ${request.method} ${request.url}")
        
        // Proceed with the request
        val response = chain.proceed(request)
        
        // Log response
        Log.d(TAG, "Response: ${response.code} for ${request.url}")
        
        return response
    }
    
    companion object {
        private const val TAG = "NetworkInterceptor"
    }
} 