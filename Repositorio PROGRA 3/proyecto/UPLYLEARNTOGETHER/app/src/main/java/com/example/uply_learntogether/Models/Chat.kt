package com.example.uply_learntogether.Models

import java.util.Date

class Chat (
    private val idChat: String,
    private val fechaCreacion: Date,
    private var activo: Boolean,
    private val mensaje: Mensaje
) {
    fun enviarMensaje(mensaje: Mensaje) {}
    fun crearSesion(sesion: Sesion) {}
    fun cancelarSesion(idUsuario: String) {}
    fun cerrarChat() {}
}