package com.example.uply_learntogether.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicacionDao {
    @Query("SELECT * FROM publicaciones ORDER BY fechaCreacion DESC")
    fun obtenerPublicaciones(): Flow<List<PublicacionEntity>>

   //guardar registros
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPublicacion(publicacion: PublicacionEntity)

    @Delete
    suspend fun eliminarPublicacion(publicacion: PublicacionEntity)
}
