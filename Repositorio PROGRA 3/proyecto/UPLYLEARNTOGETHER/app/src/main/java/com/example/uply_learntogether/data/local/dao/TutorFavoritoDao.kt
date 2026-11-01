package com.example.uply_learntogether.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uply_learntogether.data.local.entity.TutorFavoritoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TutorFavoritoDao {
    @Query("SELECT * FROM tutores_favoritos ORDER BY agregadoEn DESC")
    fun observarFavoritos(): Flow<List<TutorFavoritoEntity>>

    @Query("SELECT id FROM tutores_favoritos")
    fun observarIdsFavoritos(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tutor: TutorFavoritoEntity)

    @Query("DELETE FROM tutores_favoritos WHERE id = :id")
    suspend fun eliminarPorId(id: String)
}
