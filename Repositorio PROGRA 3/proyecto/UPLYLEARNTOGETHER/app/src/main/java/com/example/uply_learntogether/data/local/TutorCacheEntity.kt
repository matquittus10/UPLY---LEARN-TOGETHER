package com.example.uply_learntogether.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tutor_cache")
data class TutorCacheEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val materia: String,
    val calificacion: String,
    val resenas: String,
    val precio: String
)
