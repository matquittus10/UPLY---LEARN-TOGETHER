package com.example.uply_learntogether.data.repository

import com.example.uply_learntogether.data.local.dao.MensajeDao
import com.example.uply_learntogether.data.local.entity.MensajeEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ConversacionResumen(
    val nombreContacto: String,
    val ultimoMensaje: String,
    val horaTexto: String,
    val mensajesNoLeidos: Int = 0
)

class ChatRepository(private val mensajeDao: MensajeDao) {

    fun observarMensajesPorContacto(nombreContacto: String): Flow<List<MensajeEntity>> =
        mensajeDao.obtenerMensajesPorContacto(nombreContacto)

    fun observarTodasLasConversaciones(): Flow<List<MensajeEntity>> =
        mensajeDao.obtenerTodosLosMensajes()

    suspend fun enviarMensaje(
        nombreContacto: String,
        contenido: String,
        esRemitenteUsuario: Boolean = true
    ) {
        val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val mensaje = MensajeEntity(
            nombreContacto = nombreContacto,
            contenido = contenido,
            fechaHoraTexto = horaActual,
            fechaTimestamp = System.currentTimeMillis(),
            esRemitenteUsuario = esRemitenteUsuario
        )
        mensajeDao.insertarMensaje(mensaje)
    }

    suspend fun inicializarMensajesSiEstaVacio() {
        if (mensajeDao.contarMensajes() == 0) {
            val mensajesIniciales = listOf(
                MensajeEntity(
                    nombreContacto = "Diego Vargas",
                    contenido = "¡Hola! ¿Coordinamos la sesión de Cálculo I?",
                    fechaHoraTexto = "14:20",
                    fechaTimestamp = System.currentTimeMillis() - 3600000,
                    esRemitenteUsuario = false
                ),
                MensajeEntity(
                    nombreContacto = "Diego Vargas",
                    contenido = "¡Perfecto! Nos vemos el lunes 😊",
                    fechaHoraTexto = "14:32",
                    fechaTimestamp = System.currentTimeMillis() - 1800000,
                    esRemitenteUsuario = true
                ),
                MensajeEntity(
                    nombreContacto = "Valeria Rojas",
                    contenido = "¡Hola! Vi que necesitas ayuda con tus estudios. ¿Qué tema te cuesta más?",
                    fechaHoraTexto = "14:25",
                    fechaTimestamp = System.currentTimeMillis() - 7200000,
                    esRemitenteUsuario = false
                ),
                MensajeEntity(
                    nombreContacto = "Valeria Rojas",
                    contenido = "¡Hola! Tengo parcial la próxima semana y necesito apoyo.",
                    fechaHoraTexto = "14:26",
                    fechaTimestamp = System.currentTimeMillis() - 7100000,
                    esRemitenteUsuario = true
                ),
                MensajeEntity(
                    nombreContacto = "Valeria Rojas",
                    contenido = "¡Lo practicamos juntos! ¿Te parece bien coordinar una sesión?",
                    fechaHoraTexto = "14:27",
                    fechaTimestamp = System.currentTimeMillis() - 7000000,
                    esRemitenteUsuario = false
                ),
                MensajeEntity(
                    nombreContacto = "Camila Flores",
                    contenido = "¡Gracias por la sesión de hoy!",
                    fechaHoraTexto = "Ayer",
                    fechaTimestamp = System.currentTimeMillis() - 86400000,
                    esRemitenteUsuario = false
                )
            )
            mensajeDao.insertarMensajes(mensajesIniciales)
        }
    }
}
