package com.example.uply_learntogether.Models

import java.time.LocalDateTime

class Calificacion (
    private val idCalificacion: String,
    private val rolEvaluado: Rol,
    private val puntuacionL: Int,
    private val comentario: String,
    private val fecha: LocalDateTime,
    private val sesion: Sesion,
    private val usuario: Usuario
){

}