package com.example.uply_learntogether.Models

import java.util.Date

class Mensaje (
    private val idMensaje: String,
    private var contenido: String,
    private val tipo: TipoMensaje,
    private val fechaEnvio: Date,
    private val leido: Boolean
) {
}