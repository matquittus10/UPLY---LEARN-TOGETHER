package com.example.uply_learntogether.Models

import java.util.Date

class SolicitudChat (
    private val idSolicitud: String,
    private var mensajeInicial: String,
    private var estado: EstadoSolicitud,
    private val fechaEnvio: Date

) {
    fun estaVigente() {}
}