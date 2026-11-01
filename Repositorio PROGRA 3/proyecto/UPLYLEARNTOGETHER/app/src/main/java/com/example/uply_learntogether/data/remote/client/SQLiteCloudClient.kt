package com.example.uply_learntogether.data.remote.client

import com.example.uply_learntogether.data.remote.service.SQLiteCloudApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SQLiteCloudClient {
    private const val BASE_URL_SQLiteCloud = "https://cuy38fc3dk.g1.gateway.sqlite.cloud/"
    private const val API_KEY = "Cd3VodudKOod0RLsO2HJrfMAW9iPfNYLJbBDFtCZeUE"

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer $API_KEY")
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val apiService: SQLiteCloudApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_SQLiteCloud)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SQLiteCloudApiService::class.java)
    }
}
