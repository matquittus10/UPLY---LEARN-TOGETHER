package com.example.uply_learntogether.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mensajes")
data class MensajeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreContacto: String,
    val contenido: String,
    val fechaHoraTexto: String,
    val fechaTimestamp: Long = System.currentTimeMillis(),
    val esRemitenteUsuario: Boolean
)
