package com.example.uply_learntogether.data

import com.example.uply_learntogether.Models.DatoCurioso
import com.example.uply_learntogether.data.remote.RetrofitClient

object FactFactsRepository {

    suspend fun obtenerDatosCuriosos(cantidad: Int = 5): Result<List<DatoCurioso>> {
        return try {
            val response = RetrofitClient.FactFactsApi.obtenerDatosCuriosos(cantidad)
            val datosCuriosos = MapeadorFactFacts.mapearListaDtoAListaModelo(response.fact)
            Result.success(datosCuriosos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
