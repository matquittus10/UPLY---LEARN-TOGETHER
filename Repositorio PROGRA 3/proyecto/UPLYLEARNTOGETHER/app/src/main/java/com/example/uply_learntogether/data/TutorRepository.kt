package com.example.uply_learntogether.data

import com.example.uply_learntogether.Models.Tutor
import com.example.uply_learntogether.data.local.TutorCacheDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TutorRepository(private val cacheDao: TutorCacheDao) {
    fun observarTutores(): Flow<List<Tutor>> =
        cacheDao.observarTodos().map { entidades -> entidades.map { it.aTutor() } }

    suspend fun hayTutoresGuardados(): Boolean = cacheDao.contar() > 0

    suspend fun guardarTutoresIniciales(tutores: List<Tutor>) {
        cacheDao.insertarTodos(tutores.map { it.aEntidadCache() })
    }
}
