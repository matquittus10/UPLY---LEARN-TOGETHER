package com.example.uply_learntogether.data

import com.example.uply_learntogether.data.local.PublicacionDao
import com.example.uply_learntogether.data.local.PublicacionEntity
import com.example.uply_learntogether.data.remote.SQLiteCloudClient
import com.example.uply_learntogether.data.remote.SqlRequest
import kotlinx.coroutines.flow.Flow

class PublicacionRepository(private val dao: PublicacionDao) {

    private fun String.escapeSql(): String = this.replace("'", "''")

    fun observarPublicaciones(): Flow<List<PublicacionEntity>> =
        dao.obtenerPublicaciones()

    suspend fun insertarPublicacion(tipo: String, titulo: String, materia: String, descripcion: String) {
        val entidad = PublicacionEntity(
            tipo = tipo,
            titulo = titulo,
            materia = materia,
            descripcion = descripcion
        )
        // Guardar localmente en Room
        dao.insertarPublicacion(entidad)

        // Sincronizar con SQLite Cloud (en try-catch para no interrumpir si no hay internet)
        try {
            val t = entidad.tipo.escapeSql()
            val tit = entidad.titulo.escapeSql()
            val mat = entidad.materia.escapeSql()
            val desc = entidad.descripcion.escapeSql()
            val fecha = entidad.fechaCreacion

            val sql = "INSERT INTO publicaciones (tipo, titulo, materia, descripcion, fechaCreacion) " +
                    "VALUES ('$t', '$tit', '$mat', '$desc', $fecha);"

            SQLiteCloudClient.apiService.ejecutarSql("uply_learntogether_db.sqlite", SqlRequest(sql))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun eliminarPublicacion(entidad: PublicacionEntity) {
        // Eliminar localmente en Room
        dao.eliminarPublicacion(entidad)

        // Sincronizar con SQLite Cloud
        try {
            val sql = "DELETE FROM publicaciones WHERE id = ${entidad.id};"
            SQLiteCloudClient.apiService.ejecutarSql("uply_learntogether_db.sqlite", SqlRequest(sql))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
