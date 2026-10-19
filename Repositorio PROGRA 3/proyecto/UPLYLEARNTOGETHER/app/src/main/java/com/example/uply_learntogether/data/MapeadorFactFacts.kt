package com.example.uply_learntogether.data

import com.example.uply_learntogether.Models.DatoCurioso
import com.example.uply_learntogether.data.remote.FactDto

object MapeadorFactFacts {

    fun mapearDtoADatoCurioso(dto: FactDto): DatoCurioso {
        return DatoCurioso(
            texto = dto.text,
            categoria = dto.category
        )
    }

    fun mapearListaDtoAListaModelo(dtos: List<FactDto>): List<DatoCurioso> {
        return dtos.map { mapearDtoADatoCurioso(it) }
    }
}
