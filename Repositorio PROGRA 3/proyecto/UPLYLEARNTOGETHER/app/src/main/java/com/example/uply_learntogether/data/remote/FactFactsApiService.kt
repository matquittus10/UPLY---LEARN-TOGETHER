package com.example.uply_learntogether.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface FactFactsApiService {

    @GET("api.php")
    suspend fun obtenerDatosCuriosos(
        @Query("count") cantidad: Int,
    ): FactApiResponse
}
