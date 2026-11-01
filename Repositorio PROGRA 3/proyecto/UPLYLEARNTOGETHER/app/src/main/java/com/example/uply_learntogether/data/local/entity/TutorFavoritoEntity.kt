package com.example.uply_learntogether.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tutores_favoritos")
data class TutorFavoritoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val materia: String,
    val calificacion: String,
    val resenas: String,
    val precio: String,
    val agregadoEn: Long = System.currentTimeMillis()
)
