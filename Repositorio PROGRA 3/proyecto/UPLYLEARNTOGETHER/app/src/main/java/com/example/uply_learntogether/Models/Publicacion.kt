package com.example.uply_learntogether.Models

import java.util.Date

class Publicacion (
    private val idPublicacion: String,
    private val tipo: TipoPublicacion,
    private val titulo: String,
    private val descripcion: String,
    private val estado: EstadoPublicacion,
    private val fechaCreacion: Date,
    private val materia: Materia,
    private val solicitudChat: SolicitudChat
) {
    fun crear() {}
    fun editar() {}
    fun cerrar() {}
}