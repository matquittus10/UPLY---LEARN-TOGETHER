package com.example.uply_learntogether.data.repository

import com.example.uply_learntogether.Models.DatoCurioso
import com.example.uply_learntogether.data.mapper.MapeadorFactFacts
import com.example.uply_learntogether.data.remote.client.RetrofitClient

object FactFactsRepository {

    suspend fun obtenerDatosCuriosos(cantidad: Int = 5): Result<List<DatoCurioso>> {
        return try {
            val apiResponse = RetrofitClient.FactFactsApi.obtenerDatosCuriosos(cantidad)
            val lista = MapeadorFactFacts.mapearApiResponse(apiResponse)
            if (lista.isNotEmpty()) {
                Result.success(lista)
            } else {
                Result.success(listOf(DatoCurioso("No hay datos curiosos disponibles.", "General")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
