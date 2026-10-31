package com.example.uply_learntogether.data

import com.example.uply_learntogether.Models.Tutor
import com.example.uply_learntogether.data.local.TutorFavoritoDao
import com.example.uply_learntogether.data.remote.SQLiteCloudClient
import com.example.uply_learntogether.data.remote.SqlRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TutoresFavoritosRepository(private val dao: TutorFavoritoDao) {

    private fun String.escapeSql(): String = this.replace("'", "''")

    fun observarFavoritos(): Flow<List<Tutor>> =
        dao.observarFavoritos().map { entidades -> entidades.map { it.aTutor() } }

    fun observarIds(): Flow<List<String>> =
        dao.observarIdsFavoritos()

    suspend fun agregar(tutor: Tutor) {
        // Guardar localmente en Room
        dao.insertar(tutor.aEntidad())

        // Sincronizar con SQLite Cloud
        try {
            val id = tutor.id.escapeSql()
            val nombre = tutor.nombre.escapeSql()
            val materia = tutor.materia.escapeSql()
            val calificacion = tutor.calificacion.escapeSql()
            val resenas = tutor.resenas.escapeSql()
            val precio = tutor.precio.escapeSql()

            val sql = "INSERT OR REPLACE INTO tutor_cache (id, nombre, materia, calificacion, resenas, precio) " +
                    "VALUES ('$id', '$nombre', '$materia', '$calificacion', '$resenas', '$precio');"

            SQLiteCloudClient.apiService.ejecutarSql("uply_learntogether_db.sqlite", SqlRequest(sql))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun quitar(id: String) {
        // Eliminar localmente en Room
        dao.eliminarPorId(id)

        // Sincronizar con SQLite Cloud
        try {
            val cleanId = id.escapeSql()
            val sql = "DELETE FROM tutor_cache WHERE id = '$cleanId';"
            SQLiteCloudClient.apiService.ejecutarSql("uply_learntogether_db.sqlite", SqlRequest(sql))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun alternarFavorito(tutor: Tutor, esFavorito: Boolean) {
        if (esFavorito) {
            quitar(tutor.id)
        } else {
            agregar(tutor)
        }
    }
}
