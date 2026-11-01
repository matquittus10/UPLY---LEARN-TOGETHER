package com.example.uply_learntogether.data.repository

import com.example.uply_learntogether.data.local.dao.PublicacionDao
import com.example.uply_learntogether.data.local.entity.PublicacionEntity
import com.example.uply_learntogether.data.remote.client.SQLiteCloudClient
import com.example.uply_learntogether.data.remote.dto.SqlRequest
import kotlinx.coroutines.flow.Flow

class PublicacionRepository(private val dao: PublicacionDao) {

    private fun String.escapeSql(): String = this.replace("'", "''")

    fun observarPublicaciones(): Flow<List<PublicacionEntity>> =
        dao.obtenerPublicaciones()

    suspend fun insertarPublicacion(
        tipo: String,
        titulo: String,
        materia: String,
        descripcion: String,
        nombreAutor: String = "Usuario UPLY"
    ) {
        val entidad = PublicacionEntity(
            tipo = tipo,
            titulo = titulo,
            materia = materia,
            descripcion = descripcion,
            nombreAutor = nombreAutor
        )
        dao.insertarPublicacion(entidad)

        try {
            val t = entidad.tipo.escapeSql()
            val tit = entidad.titulo.escapeSql()
            val mat = entidad.materia.escapeSql()
            val desc = entidad.descripcion.escapeSql()
            val autor = entidad.nombreAutor.escapeSql()
            val fecha = entidad.fechaCreacion

            val sql = "INSERT INTO publicaciones (tipo, titulo, materia, descripcion, nombreAutor, fechaCreacion) " +
                    "VALUES ('$t', '$tit', '$mat', '$desc', '$autor', $fecha);"

            SQLiteCloudClient.apiService.ejecutarSql("uply_learntogether_db.sqlite", SqlRequest(sql))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun eliminarPublicacion(entidad: PublicacionEntity) {
        dao.eliminarPublicacion(entidad)

        try {
            val sql = "DELETE FROM publicaciones WHERE id = ${entidad.id};"
            SQLiteCloudClient.apiService.ejecutarSql("uply_learntogether_db.sqlite", SqlRequest(sql))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
