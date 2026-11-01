package com.example.uply_learntogether.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uply_learntogether.data.local.entity.MensajeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MensajeDao {

    @Query("SELECT * FROM mensajes WHERE nombreContacto = :nombreContacto ORDER BY fechaTimestamp ASC")
    fun obtenerMensajesPorContacto(nombreContacto: String): Flow<List<MensajeEntity>>

    @Query("SELECT * FROM mensajes ORDER BY fechaTimestamp DESC")
    fun obtenerTodosLosMensajes(): Flow<List<MensajeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMensaje(mensaje: MensajeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMensajes(mensajes: List<MensajeEntity>)

    @Query("SELECT COUNT(*) FROM mensajes")
    suspend fun contarMensajes(): Int
}
