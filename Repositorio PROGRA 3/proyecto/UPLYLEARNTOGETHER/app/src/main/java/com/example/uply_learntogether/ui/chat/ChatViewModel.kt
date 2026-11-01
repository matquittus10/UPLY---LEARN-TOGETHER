package com.example.uply_learntogether.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uply_learntogether.data.local.AppDatabase
import com.example.uply_learntogether.data.local.entity.MensajeEntity
import com.example.uply_learntogether.data.local.entity.SesionEntity
import com.example.uply_learntogether.data.repository.ChatRepository
import com.example.uply_learntogether.data.repository.SesionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ConversacionUiState(
    val id: Long,
    val name: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.obtener(application)
    private val chatRepository = ChatRepository(db.mensajeDao())
    private val sesionRepository = SesionRepository(db.sesionDao())

    val conversacionesState: StateFlow<List<ConversacionUiState>> = chatRepository.observarTodasLasConversaciones()
        .map { mensajes ->
            mensajes.groupBy { it.nombreContacto }
                .map { (contacto, lista) ->
                    val ultimo = lista.maxByOrNull { it.fechaTimestamp }
                    ConversacionUiState(
                        id = ultimo?.id ?: 0,
                        name = contacto,
                        lastMessage = ultimo?.contenido ?: "",
                        time = ultimo?.fechaHoraTexto ?: "",
                        unreadCount = 0
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            chatRepository.inicializarMensajesSiEstaVacio()
            sesionRepository.inicializarSesionesSiEstaVacio()
        }
    }

    fun obtenerMensajesPorContacto(nombreContacto: String): StateFlow<List<MensajeEntity>> {
        return chatRepository.observarMensajesPorContacto(nombreContacto)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun obtenerSesionPropuesta(nombreContacto: String): StateFlow<SesionEntity?> {
        return sesionRepository.observarSesionPropuesta(nombreContacto)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    fun enviarMensaje(nombreContacto: String, contenido: String) {
        if (contenido.isBlank()) return
        viewModelScope.launch {
            chatRepository.enviarMensaje(nombreContacto, contenido, esRemitenteUsuario = true)
        }
    }

    fun proponerSesion(
        nombreContacto: String,
        materia: String,
        mesTexto: String,
        diaTexto: String,
        horarioTexto: String,
        modalidad: String,
        precioTexto: String,
        resumenCompleto: String
    ) {
        viewModelScope.launch {
            sesionRepository.proponerSesion(
                nombreContacto = nombreContacto,
                materia = materia,
                mesTexto = mesTexto,
                diaTexto = diaTexto,
                horarioTexto = horarioTexto,
                modalidad = modalidad,
                precioTexto = precioTexto,
                resumenCompleto = resumenCompleto
            )
        }
    }

    fun confirmarSesion(nombreContacto: String) {
        viewModelScope.launch {
            sesionRepository.confirmarSesionPropuesta(nombreContacto)
        }
    }
}
