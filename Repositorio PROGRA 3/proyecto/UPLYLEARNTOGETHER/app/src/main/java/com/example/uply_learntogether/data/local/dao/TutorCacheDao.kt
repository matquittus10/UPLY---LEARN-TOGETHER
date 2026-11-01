package com.example.uply_learntogether.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uply_learntogether.data.local.entity.TutorCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TutorCacheDao {
    @Query("SELECT * FROM tutor_cache")
    fun observarTodos(): Flow<List<TutorCacheEntity>>

    @Query("SELECT COUNT(*) FROM tutor_cache")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(tutores: List<TutorCacheEntity>)
}
