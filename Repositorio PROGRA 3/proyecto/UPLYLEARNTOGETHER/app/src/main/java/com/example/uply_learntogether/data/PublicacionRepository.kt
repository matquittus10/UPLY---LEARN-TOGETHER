package com.example.uply_learntogether.data

import com.example.uply_learntogether.data.local.PublicacionDao
import com.example.uply_learntogether.data.local.PublicacionEntity
import kotlinx.coroutines.flow.Flow

class PublicacionRepository(private val dao: PublicacionDao) {

    fun observarPublicaciones(): Flow<List<PublicacionEntity>> =
        dao.obtenerPublicaciones()

    suspend fun insertarPublicacion(tipo: String, titulo: String, materia: String, descripcion: String) {
        val entidad = PublicacionEntity(
            tipo = tipo,
            titulo = titulo,
            materia = materia,
            descripcion = descripcion
        )
        dao.insertarPublicacion(entidad)
    }

    suspend fun eliminarPublicacion(entidad: PublicacionEntity) {
        dao.eliminarPublicacion(entidad)
    }
}
