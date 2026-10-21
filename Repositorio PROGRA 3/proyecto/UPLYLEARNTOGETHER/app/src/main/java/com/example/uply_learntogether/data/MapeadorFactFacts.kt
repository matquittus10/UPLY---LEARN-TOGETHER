package com.example.uply_learntogether.data

import com.example.uply_learntogether.Models.DatoCurioso
import com.example.uply_learntogether.data.remote.FactApiResponse

object MapeadorFactFacts {

    fun mapearApiResponse(response: FactApiResponse): List<DatoCurioso> {
        return response.facts?.map { factDto ->
            DatoCurioso(
                texto = factDto.text ?: "Sin texto",
                categoria = factDto.category ?: "General"
            )
        } ?: emptyList()
    }
}
