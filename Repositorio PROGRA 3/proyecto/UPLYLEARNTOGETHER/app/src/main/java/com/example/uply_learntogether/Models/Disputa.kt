package com.example.uply_learntogether.Models

import java.time.LocalDateTime

class Disputa(
    private val idDisputa: String,
    private val motivo: String,
    private val estado: EstadoDisputa,
    private val resolucion: String,
    private val fechaReporte: LocalDateTime,
    private val sesion: Sesion,
    private val usuario: Usuario
) {
    fun reportar() {}
    fun resolver() {}
}