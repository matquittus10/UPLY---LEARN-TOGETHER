package com.example.uply_learntogether.Models

import java.time.LocalDateTime

class ConfiguracionSesion(
    private var idConfiguracion: String,
    private var modalidad: Modalidad,
    private var fechaHora: LocalDateTime,
    private var duracionMinutos: Int,
    private var aceptadaPorEstudiante: Boolean,
    private var aceptadaPorTutor: Boolean,
    private var fechaPropuesta: LocalDateTime
) {

    fun aceptar(idUsuario: String) {}

    fun estaVigente() {}
}