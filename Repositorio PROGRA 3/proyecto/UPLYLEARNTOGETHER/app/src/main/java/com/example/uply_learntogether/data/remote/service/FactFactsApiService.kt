package com.example.uply_learntogether.data.remote.service

import com.example.uply_learntogether.data.remote.dto.FactApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FactFactsApiService {

    @GET("api.php")
    suspend fun obtenerDatosCuriosos(
        @Query("count") cantidad: Int,
    ): FactApiResponse
}
