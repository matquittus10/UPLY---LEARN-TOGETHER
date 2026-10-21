package com.example.uply_learntogether.data

import com.example.uply_learntogether.Models.Tutor
import com.example.uply_learntogether.data.local.TutorFavoritoDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TutoresFavoritosRepository(private val dao: TutorFavoritoDao) {
    fun observarFavoritos(): Flow<List<Tutor>> =
        dao.observarFavoritos().map { entidades -> entidades.map { it.aTutor() } }

    fun observarIds(): Flow<List<String>> =
        dao.observarIdsFavoritos()

    suspend fun agregar(tutor: Tutor) {
        dao.insertar(tutor.aEntidad())
    }

    suspend fun quitar(id: String) {
        dao.eliminarPorId(id)
    }

    suspend fun alternarFavorito(tutor: Tutor, esFavorito: Boolean) {
        if (esFavorito) {
            quitar(tutor.id)
        } else {
            agregar(tutor)
        }
    }
}
