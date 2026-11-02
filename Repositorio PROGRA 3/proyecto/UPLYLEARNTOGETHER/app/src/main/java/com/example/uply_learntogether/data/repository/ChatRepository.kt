package com.example.uply_learntogether.data.repository

import com.example.uply_learntogether.Models.Usuario
import com.example.uply_learntogether.data.local.dao.MensajeDao
import com.example.uply_learntogether.data.local.entity.MensajeEntity
import com.example.uply_learntogether.data.local.entity.SesionEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

data class ConversacionResumen(
    val nombreContacto: String,
    val ultimoMensaje: String,
    val horaTexto: String,
    val mensajesNoLeidos: Int = 0
)

class ChatRepository(private val mensajeDao: MensajeDao) {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private fun getChatId(nombreContacto: String, targetUid: String = ""): String {
        val currentUid = auth.currentUser?.uid ?: ""
        if (targetUid.isNotBlank() && currentUid.isNotBlank()) {
            return if (currentUid < targetUid) "${currentUid}_${targetUid}" else "${targetUid}_${currentUid}"
        }

        val currentUser = auth.currentUser
        val myIdentifier = currentUser?.displayName
            ?: currentUser?.email?.substringBefore("@")
            ?: currentUser?.uid
            ?: "usuario"

        val u1 = myIdentifier.lowercase().trim().replace(" ", "_")
        val u2 = nombreContacto.lowercase().trim().replace(" ", "_")
        return if (u1 < u2) "${u1}__${u2}" else "${u2}__${u1}"
    }

    fun observarUsuariosRegistrados(): Flow<List<Usuario>> = callbackFlow {
        val currentUid = auth.currentUser?.uid ?: ""
        val listener = firestore.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val usuarios = snapshot.documents.mapNotNull { doc ->
                    val uid = doc.getString("uid") ?: doc.id
                    if (uid == currentUid) return@mapNotNull null

                    val nombre = doc.getString("nombreUsuario") ?: "Usuario"
                    val correo = doc.getString("correo") ?: ""
                    Usuario(
                        idUsuario = uid,
                        nombreUsuario = nombre,
                        correoInstitucional = correo
                    )
                }
                trySend(usuarios)
            }
        awaitClose { listener.remove() }
    }

    fun observarMensajesPorContacto(nombreContacto: String, targetUid: String = ""): Flow<List<MensajeEntity>> = callbackFlow {
        val chatId = getChatId(nombreContacto, targetUid)
        val currentUserId = auth.currentUser?.uid ?: ""

        val listener = firestore.collection("chats")
            .document(chatId)
            .collection("mensajes")
            .orderBy("fechaTimestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val mensajes = snapshot.documents.mapNotNull { doc ->
                    val contenido = doc.getString("contenido") ?: return@mapNotNull null
                    val fechaHora = doc.getString("fechaHoraTexto") ?: ""
                    val timestamp = doc.getLong("fechaTimestamp") ?: System.currentTimeMillis()
                    val remitenteUid = doc.getString("remitenteUid") ?: ""

                    MensajeEntity(
                        id = abs(doc.id.hashCode()).toLong(),
                        nombreContacto = nombreContacto,
                        contenido = contenido,
                        fechaHoraTexto = fechaHora,
                        fechaTimestamp = timestamp,
                        esRemitenteUsuario = (remitenteUid == currentUserId)
                    )
                }
                trySend(mensajes)
            }

        awaitClose { listener.remove() }
    }

    fun observarTodasLasConversaciones(): Flow<List<MensajeEntity>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: ""
        val currentUserName = auth.currentUser?.displayName
            ?: auth.currentUser?.email?.substringBefore("@") ?: ""

        val listener = firestore.collection("chats")
            .orderBy("fechaTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val conversaciones = snapshot.documents.mapNotNull { doc ->
                    val participantes = doc.get("participantes") as? List<*> ?: emptyList<Any>()
                    val esParticipante = participantes.isEmpty() ||
                            participantes.contains(currentUserId) ||
                            participantes.contains(currentUserName)

                    if (!esParticipante) return@mapNotNull null

                    val nombresMap = doc.get("nombresParticipantes") as? Map<*, *> ?: emptyMap<Any, Any>()
                    val otroNombre = nombresMap.entries.firstOrNull {
                        it.key != currentUserId && it.key != currentUserName
                    }?.value as? String

                    val nombreContactoMostrar: String = when {
                        !otroNombre.isNullOrBlank() && !otroNombre.equals(currentUserName, ignoreCase = true) -> otroNombre
                        else -> {
                            val alt = doc.getString("nombreContacto") ?: "Contacto"
                            if (alt.equals(currentUserName, ignoreCase = true)) return@mapNotNull null else alt
                        }
                    }

                    if (nombreContactoMostrar.equals(currentUserName, ignoreCase = true)) {
                        return@mapNotNull null
                    }

                    val ultimoMensaje = doc.getString("ultimoMensaje") ?: return@mapNotNull null
                    val fechaHora = doc.getString("fechaHoraTexto") ?: ""
                    val timestamp = doc.getLong("fechaTimestamp") ?: System.currentTimeMillis()

                    MensajeEntity(
                        id = abs(doc.id.hashCode()).toLong(),
                        nombreContacto = nombreContactoMostrar,
                        contenido = ultimoMensaje,
                        fechaHoraTexto = fechaHora,
                        fechaTimestamp = timestamp,
                        esRemitenteUsuario = false
                    )
                }
                trySend(conversaciones)
            }

        awaitClose { listener.remove() }
    }

    fun observarSesionPropuesta(nombreContacto: String, targetUid: String = ""): Flow<SesionEntity?> = callbackFlow {
        val chatId = getChatId(nombreContacto, targetUid)

        val listener = firestore.collection("chats")
            .document(chatId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val sesionMap = snapshot.get("sesionPropuesta") as? Map<*, *>
                if (sesionMap == null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val resumen = sesionMap["resumenCompleto"] as? String ?: ""
                val estado = sesionMap["estado"] as? String ?: "PROPUESTA"

                if (resumen.isNotBlank()) {
                    val sesion = SesionEntity(
                        nombreContacto = nombreContacto,
                        materia = "Tutoría",
                        mesTexto = "",
                        diaTexto = "",
                        horarioTexto = "",
                        modalidad = "",
                        precioTexto = "",
                        resumenCompleto = resumen,
                        estado = estado
                    )
                    trySend(sesion)
                } else {
                    trySend(null)
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun proponerSesion(nombreContacto: String, targetUid: String = "", resumenCompleto: String) {
        val chatId = getChatId(nombreContacto, targetUid)
        val currentUserId = auth.currentUser?.uid ?: ""

        val sesionData = hashMapOf(
            "resumenCompleto" to resumenCompleto,
            "estado" to "PROPUESTA",
            "propuestaPorUid" to currentUserId,
            "nombreContacto" to nombreContacto
        )

        try {
            firestore.collection("chats").document(chatId).set(
                hashMapOf("sesionPropuesta" to sesionData),
                SetOptions.merge()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun confirmarSesionPropuesta(nombreContacto: String, targetUid: String = "") {
        val chatId = getChatId(nombreContacto, targetUid)

        try {
            firestore.collection("chats").document(chatId).update(
                "sesionPropuesta.estado", "CONFIRMADA"
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun enviarMensaje(
        nombreContacto: String,
        contenido: String,
        targetUid: String = "",
        esRemitenteUsuario: Boolean = true
    ) {
        val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val timestamp = System.currentTimeMillis()
        val currentUserId = auth.currentUser?.uid ?: ""
        val currentUserName = auth.currentUser?.displayName
            ?: auth.currentUser?.email?.substringBefore("@") ?: "Usuario"

        val chatId = getChatId(nombreContacto, targetUid)

        // Enviar mensaje a Firebase Firestore para comunicación en tiempo real
        try {
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

            val nombresMap = hashMapOf(
                currentUserId to currentUserName,
                (targetUid.ifBlank { nombreContacto }) to nombreContacto
            )

            val chatSummary = hashMapOf(
                "ultimoMensaje" to contenido,
                "fechaHoraTexto" to horaActual,
                "fechaTimestamp" to timestamp,
                "participantes" to listOf(currentUserId, targetUid.ifBlank { nombreContacto }),
                "nombresParticipantes" to nombresMap
            )
            chatRef.set(chatSummary, SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
            // En caso de fallo o fallo de red, guardar localmente
            val mensajeLocal = MensajeEntity(
                nombreContacto = nombreContacto,
                contenido = contenido,
                fechaHoraTexto = horaActual,
                fechaTimestamp = timestamp,
                esRemitenteUsuario = esRemitenteUsuario
            )
            mensajeDao.insertarMensaje(mensajeLocal)
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
