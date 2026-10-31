package com.example.uply_learntogether.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface SQLiteCloudApiService {

    @POST("v2/sql")
    suspend fun ejecutarSql(
        @Query("database") nombreBaseDatos: String,
        @Body request: SqlRequest
    ): SqlResponse
}
