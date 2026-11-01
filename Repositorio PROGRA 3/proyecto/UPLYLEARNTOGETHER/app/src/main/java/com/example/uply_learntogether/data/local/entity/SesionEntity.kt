package com.example.uply_learntogether.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sesiones")
data class SesionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreContacto: String,
    val materia: String,
    val mesTexto: String,
    val diaTexto: String,
    val horarioTexto: String,
    val modalidad: String,
    val precioTexto: String,
    val resumenCompleto: String,
    val estado: String
)
