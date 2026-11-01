package com.example.uply_learntogether.data.repository

import com.example.uply_learntogether.data.local.dao.SesionDao
import com.example.uply_learntogether.data.local.entity.SesionEntity
import kotlinx.coroutines.flow.Flow

class SesionRepository(private val sesionDao: SesionDao) {

    fun observarSesionPropuesta(nombreContacto: String): Flow<SesionEntity?> =
        sesionDao.observarSesionPropuesta(nombreContacto)

    fun observarProximaSesionConfirmada(): Flow<SesionEntity?> =
        sesionDao.observarProximaSesionConfirmada()

    suspend fun proponerSesion(
        nombreContacto: String,
        materia: String,
        mesTexto: String,
        diaTexto: String,
        horarioTexto: String,
        modalidad: String,
        precioTexto: String,
        resumenCompleto: String
    ) {
        val sesion = SesionEntity(
            nombreContacto = nombreContacto,
            materia = materia,
            mesTexto = mesTexto,
            diaTexto = diaTexto,
            horarioTexto = horarioTexto,
            modalidad = modalidad,
            precioTexto = precioTexto,
            resumenCompleto = resumenCompleto,
            estado = "PROPUESTA"
        )
        sesionDao.insertarSesion(sesion)
    }

    suspend fun confirmarSesionPropuesta(nombreContacto: String) {
        sesionDao.confirmarSesionPropuesta(nombreContacto)
    }

    suspend fun inicializarSesionesSiEstaVacio() {
        if (sesionDao.contarSesiones() == 0) {
            val sesionInicial = SesionEntity(
                nombreContacto = "Diego Vargas",
                materia = "Cálculo I con Diego",
                mesTexto = "OCT",
                diaTexto = "05",
                horarioTexto = "16:00 - 17:00",
                modalidad = "Virtual",
                precioTexto = "Bs 35",
                resumenCompleto = "Lunes 5 oct. · 16:00 · Virtual · Bs 35",
                estado = "CONFIRMADA"
            )
            sesionDao.insertarSesion(sesionInicial)
        }
    }
}
