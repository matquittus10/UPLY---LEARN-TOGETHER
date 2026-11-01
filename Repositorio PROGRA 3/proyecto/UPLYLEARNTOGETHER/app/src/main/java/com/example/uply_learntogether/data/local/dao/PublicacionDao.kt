package com.example.uply_learntogether.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uply_learntogether.data.local.entity.PublicacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicacionDao {
    @Query("SELECT * FROM publicaciones ORDER BY fechaCreacion DESC")
    fun obtenerPublicaciones(): Flow<List<PublicacionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPublicacion(publicacion: PublicacionEntity)

    @Delete
    suspend fun eliminarPublicacion(publicacion: PublicacionEntity)
}
