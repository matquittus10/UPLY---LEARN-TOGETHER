package com.example.uply_learntogether.Models

import java.time.LocalDateTime

class Usuario(
    private val idUsuario: String = "",
    private val nombreUsuario: String = "",
    private val correoInstitucional: String = "",
    private val celular: String = "",
    private val contrasena: String = "",
    private val fotoPerfil: String = "",
    private val tarifaHora: Float = 0f,
    private val calificacionComoTutor: Float = 0f,
    private val calificacionComoEstudiante: Float = 0f,
    private val totalResenasRecibidas: Int = 0,
    private val fechaRegistro: LocalDateTime = LocalDateTime.of(2025, 1, 1, 0, 0),
    private val publicacion: Publicacion? = null
) {
    private var sesionIniciada: Boolean = false

    // Métodos de acceso (getters) para la encapsulación
    fun getIdUsuario(): String = idUsuario
    fun getNombreUsuario(): String = nombreUsuario
    fun getCorreoInstitucional(): String = correoInstitucional
    fun getCelular(): String = celular
    fun getContrasena(): String = contrasena
    fun getFotoPerfil(): String = fotoPerfil
    fun getTarifaHora(): Float = tarifaHora
    fun getCalificacionComoTutor(): Float = calificacionComoTutor
    fun getCalificacionComoEstudiante(): Float = calificacionComoEstudiante
    fun getTotalResenasRecibidas(): Int = totalResenasRecibidas
    fun getFechaRegistro(): LocalDateTime = fechaRegistro
    fun getPublicacion(): Publicacion? = publicacion
    fun isSesionIniciada(): Boolean = sesionIniciada

    fun registrarse(): Boolean {
        return correoInstitucional.isNotEmpty() && contrasena.isNotEmpty()
    }

    fun iniciarSesionCuenta(): Boolean {
        sesionIniciada = true
        return sesionIniciada
    }

    fun cerrarSesionCuenta() {
        sesionIniciada = false
    }

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
