package com.donate.chillinnmobile.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * ApiClient singleton for creating and configuring Retrofit instance
 */
object ApiClient {
    private const val BASE_URL = "https://api.chillinn.example.com/" // Replace with actual API URL
    private const val TIMEOUT = 30L

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(NetworkInterceptor())
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
} 