package com.example.uply_learntogether.Models

import java.time.LocalDateTime

class Sesion(
    private var idSesion: String,
    private var fechaHoraProgramada: LocalDateTime,
    private var fechaHoraInicioReal: LocalDateTime,
    private var fechaHoraFin: LocalDateTime,
    private var duracionMinutos: Int,
    private var extensionMinutos: Int,
    private var modalidad: Modalidad,
    private var ubicacionOEnlace: String,
    private var estado: EstadoSesion,
    private var canceladaPor: Rol,
    private var fechaCancelacion: LocalDateTime,
    private var aceptaInicioEstudiante: Boolean,
    private var aceptaInicioTutor: Boolean,
    private var chat: Chat,
    private val configuracionSesion: ConfiguracionSesion
) {

    fun confirmarInicio(idUsuario: String) {}

    fun iniciarCronometro() {}

    fun extenderSesion(minutos: Int) {}

    fun finalizarSesion() {}

    fun marcarNoShowTutor() {}
}