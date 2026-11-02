package com.example.uply_learntogether.data.repository

import com.example.uply_learntogether.data.local.dao.MensajeDao
import com.example.uply_learntogether.data.local.entity.MensajeEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private fun getChatId(nombreContacto: String): String {
        val currentUserId = auth.currentUser?.uid ?: "usuario_local"
        val cleanContact = nombreContacto.lowercase().trim().replace(" ", "_")
        val cleanUser = currentUserId.lowercase().trim().replace(" ", "_")
        return if (cleanUser < cleanContact) "${cleanUser}_${cleanContact}" else "${cleanContact}_${cleanUser}"
    }

    fun observarMensajesPorContacto(nombreContacto: String): Flow<List<MensajeEntity>> {
        val chatId = getChatId(nombreContacto)
        val currentUserId = auth.currentUser?.uid ?: ""

        try {
            firestore.collection("chats")
                .document(chatId)
                .collection("mensajes")
                .orderBy("fechaTimestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener

                    val nuevosMensajes = snapshot.documents.mapNotNull { doc ->
                        val contenido = doc.getString("contenido") ?: return@mapNotNull null
                        val fechaHora = doc.getString("fechaHoraTexto") ?: ""
                        val timestamp = doc.getLong("fechaTimestamp") ?: System.currentTimeMillis()
                        val remitenteUid = doc.getString("remitenteUid") ?: ""

                        MensajeEntity(
                            nombreContacto = nombreContacto,
                            contenido = contenido,
                            fechaHoraTexto = fechaHora,
                            fechaTimestamp = timestamp,
                            esRemitenteUsuario = (remitenteUid == currentUserId)
                        )
                    }

                    if (nuevosMensajes.isNotEmpty()) {
                        repositoryScope.launch {
                            try {
                                mensajeDao.insertarMensajes(nuevosMensajes)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mensajeDao.obtenerMensajesPorContacto(nombreContacto)
    }

    fun observarTodasLasConversaciones(): Flow<List<MensajeEntity>> =
        mensajeDao.obtenerTodosLosMensajes()

    suspend fun enviarMensaje(
        nombreContacto: String,
        contenido: String,
        esRemitenteUsuario: Boolean = true
    ) {
        val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val timestamp = System.currentTimeMillis()
        val currentUserId = auth.currentUser?.uid ?: ""
        val currentUserName = auth.currentUser?.displayName
            ?: auth.currentUser?.email?.substringBefore("@") ?: "Usuario"

        // 1. Guardar mensaje en la base de datos local Room
        val mensajeLocal = MensajeEntity(
            nombreContacto = nombreContacto,
            contenido = contenido,
            fechaHoraTexto = horaActual,
            fechaTimestamp = timestamp,
            esRemitenteUsuario = esRemitenteUsuario
        )
        mensajeDao.insertarMensaje(mensajeLocal)

        // 2. Enviar mensaje a Firebase Firestore para comunicación en tiempo real
        try {
            val chatId = getChatId(nombreContacto)
            val mensajeData = hashMapOf(
                "nombreContacto" to nombreContacto,
                "remitenteUid" to currentUserId,
                "remitenteNombre" to currentUserName,
                "contenido" to contenido,
                "fechaHoraTexto" to horaActual,
                "fechaTimestamp" to timestamp
            )

            val chatRef = firestore.collection("chats").document(chatId)
            chatRef.collection("mensajes").add(mensajeData).await()

            val chatSummary = hashMapOf(
                "nombreContacto" to nombreContacto,
                "ultimoMensaje" to contenido,
                "fechaHoraTexto" to horaActual,
                "fechaTimestamp" to timestamp,
                "participantes" to listOf(currentUserId, nombreContacto)
            )
            chatRef.set(chatSummary, SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

