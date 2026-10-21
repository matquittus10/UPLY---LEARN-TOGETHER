package com.example.uply_learntogether.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "publicaciones")
data class PublicacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tipo: String,
    val titulo: String,
    val materia: String,
    val descripcion: String,
    val fechaCreacion: Long = System.currentTimeMillis()
)
