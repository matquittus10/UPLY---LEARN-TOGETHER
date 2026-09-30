package com.example.uply_learntogether.Models

import java.time.LocalDateTime

class Pago(
    private var idPago: String,
    private var monto: Float,
    private var metodo: MetodoPago,
    private var comprobanteQR: String,
    private var estado: EstadoPago,
    private var fechaPago: LocalDateTime,
    private val sesion: Sesion,
) {

    fun confirmarPago() {}

    fun retenerFondos() {}

    fun liberarFondosTutor() {}

    fun reembolsarTotal() {}

    fun capturarParcial(porcentaje: Float) {}
}