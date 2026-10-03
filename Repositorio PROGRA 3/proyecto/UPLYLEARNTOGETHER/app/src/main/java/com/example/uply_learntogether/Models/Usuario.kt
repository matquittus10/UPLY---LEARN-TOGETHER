package com.example.uply_learntogether.Models

import java.time.LocalDateTime

class Usuario (
    private val idUsuario: String,
    private val nombreUsuario: String,
    private val correoInstitucional: String,
    private val celular: String,
    private val contrasena: String,
    private val fotoPerfil: String,
    private val tarifaHora: Float,
    private val calificacionComoTutor: Float,
    private val calificacionComoEstudiante: Float,
    private val totalResenasRecibidas: Int,
    private val fechaRegistro: LocalDateTime,
    private val publicacion: Publicacion
    ){
    fun registrarse() {}
    fun iniciarSesionCuenta() {}
    fun cerrarSesionCuenta() {}
    fun actualizarPerfil() {}
    fun definirTarifa(monto: Float) {}
    fun agregarMateriaEnsenada(idMateria: String) {}
    fun publicar(publicar: Publicacion) {}
    fun enviarSolicitudChat(idPublicacion: String) {}
    fun aceptarSolicitudChat(idSolicitud: String) {}
    fun rechazarSolicitudChat(idSolicitud: String) {}
    fun proponerConfiguracionSesion() {}
    fun crearCalificacion() {}

}