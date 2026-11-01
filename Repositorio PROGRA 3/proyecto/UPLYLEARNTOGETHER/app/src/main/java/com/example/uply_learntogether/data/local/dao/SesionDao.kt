package com.example.uply_learntogether.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uply_learntogether.data.local.entity.SesionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SesionDao {

    @Query("SELECT * FROM sesiones WHERE nombreContacto = :nombreContacto AND estado = 'PROPUESTA' ORDER BY id DESC LIMIT 1")
    fun observarSesionPropuesta(nombreContacto: String): Flow<SesionEntity?>

    @Query("SELECT * FROM sesiones WHERE estado = 'CONFIRMADA' ORDER BY id DESC LIMIT 1")
    fun observarProximaSesionConfirmada(): Flow<SesionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSesion(sesion: SesionEntity): Long

    @Query("UPDATE sesiones SET estado = 'CONFIRMADA' WHERE nombreContacto = :nombreContacto AND estado = 'PROPUESTA'")
    suspend fun confirmarSesionPropuesta(nombreContacto: String)

    @Query("SELECT COUNT(*) FROM sesiones")
    suspend fun contarSesiones(): Int
}
