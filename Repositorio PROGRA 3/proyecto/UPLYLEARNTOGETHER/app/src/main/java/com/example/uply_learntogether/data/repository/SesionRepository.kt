package com.example.uply_learntogether.data.repository

import com.example.uply_learntogether.data.local.dao.SesionDao
import com.example.uply_learntogether.data.local.entity.SesionEntity
import kotlinx.coroutines.flow.Flow

class SesionRepository(private val sesionDao: SesionDao) {

    fun observarSesionPropuesta(nombreContacto: String): Flow<SesionEntity?> =
        sesionDao.observarSesionPropuesta(nombreContacto)

    fun observarProximaSesionConfirmada(): Flow<SesionEntity?> =
        sesionDao.observarProximaSesionConfirmada()

    fun observarTodasLasSesiones(): Flow<List<SesionEntity>> =
        sesionDao.observarTodasLasSesiones()

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
        sesionDao.eliminarSesionEjemplo()
    }
}
