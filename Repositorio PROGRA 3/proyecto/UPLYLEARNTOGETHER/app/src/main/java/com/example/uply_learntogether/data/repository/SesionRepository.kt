package com.example.uply_learntogether.data.repository

import com.example.uply_learntogether.data.local.dao.SesionDao
import com.example.uply_learntogether.data.local.entity.SesionEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.math.abs

class SesionRepository(private val sesionDao: SesionDao) {

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

    fun observarSesionPropuesta(nombreContacto: String): Flow<SesionEntity?> =
        sesionDao.observarSesionPropuesta(nombreContacto)

    fun observarProximaSesionConfirmada(): Flow<SesionEntity?> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: ""
        val listener = firestore.collection("sesiones")
            .whereEqualTo("estado", "CONFIRMADA")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || snapshot.isEmpty) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val sesion = snapshot.documents.mapNotNull { doc ->
                    val participantes = doc.get("participantes") as? List<*>
                    val usuarioUid = doc.getString("usuarioUid") ?: ""

                    val esParticipante = currentUserId.isBlank() ||
                            usuarioUid == currentUserId ||
                            participantes?.contains(currentUserId) == true ||
                            participantes.isNullOrEmpty()

                    if (!esParticipante) return@mapNotNull null

                    val nombre = doc.getString("nombreContacto") ?: return@mapNotNull null
                    val materia = doc.getString("materia") ?: ""
                    val mes = doc.getString("mesTexto") ?: ""
                    val dia = doc.getString("diaTexto") ?: ""
                    val horario = doc.getString("horarioTexto") ?: ""
                    val modalidad = doc.getString("modalidad") ?: ""
                    val precio = doc.getString("precioTexto") ?: ""
                    val resumen = doc.getString("resumenCompleto") ?: ""

                    SesionEntity(
                        id = abs(doc.id.hashCode()).toLong(),
                        nombreContacto = nombre,
                        materia = materia,
                        mesTexto = mes,
                        diaTexto = dia,
                        horarioTexto = horario,
                        modalidad = modalidad,
                        precioTexto = precio,
                        resumenCompleto = resumen,
                        estado = "CONFIRMADA"
                    )
                }.firstOrNull()

                trySend(sesion)
            }
        awaitClose { listener.remove() }
    }

    fun observarTodasLasSesiones(): Flow<List<SesionEntity>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: ""
        val listener = firestore.collection("sesiones")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                val sesiones = snapshot.documents.mapNotNull { doc ->
                    val participantes = doc.get("participantes") as? List<*>
                    val usuarioUid = doc.getString("usuarioUid") ?: ""

                    val esParticipante = currentUserId.isBlank() ||
                            usuarioUid == currentUserId ||
                            participantes?.contains(currentUserId) == true ||
                            participantes.isNullOrEmpty()

                    if (!esParticipante) return@mapNotNull null

                    val nombre = doc.getString("nombreContacto") ?: return@mapNotNull null
                    val materia = doc.getString("materia") ?: ""
                    val mes = doc.getString("mesTexto") ?: ""
                    val dia = doc.getString("diaTexto") ?: ""
                    val horario = doc.getString("horarioTexto") ?: ""
                    val modalidad = doc.getString("modalidad") ?: ""
                    val precio = doc.getString("precioTexto") ?: ""
                    val resumen = doc.getString("resumenCompleto") ?: ""
                    val estado = doc.getString("estado") ?: "PROPUESTA"

                    SesionEntity(
                        id = abs(doc.id.hashCode()).toLong(),
                        nombreContacto = nombre,
                        materia = materia,
                        mesTexto = mes,
                        diaTexto = dia,
                        horarioTexto = horario,
                        modalidad = modalidad,
                        precioTexto = precio,
                        resumenCompleto = resumen,
                        estado = estado
                    )
                }
                trySend(sesiones)
            }
        awaitClose { listener.remove() }
    }

    suspend fun proponerSesion(
        nombreContacto: String,
        targetUid: String = "",
        materia: String,
        mesTexto: String,
        diaTexto: String,
        horarioTexto: String,
        modalidad: String,
        precioTexto: String,
        resumenCompleto: String
    ) {
        val currentUserId = auth.currentUser?.uid ?: ""
        val chatId = getChatId(nombreContacto, targetUid)
        val participantes = listOfNotNull(currentUserId, targetUid.takeIf { it.isNotBlank() }).distinct()

        val sesionData = hashMapOf(
            "chatId" to chatId,
            "participantes" to participantes,
            "usuarioUid" to currentUserId,
            "nombreContacto" to nombreContacto,
            "materia" to materia,
            "mesTexto" to mesTexto,
            "diaTexto" to diaTexto,
            "horarioTexto" to horarioTexto,
            "modalidad" to modalidad,
            "precioTexto" to precioTexto,
            "resumenCompleto" to resumenCompleto,
            "estado" to "PROPUESTA",
            "timestamp" to System.currentTimeMillis()
        )

        try {
            firestore.collection("sesiones")
                .document(chatId)
                .set(sesionData, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            val sesion = SesionEntity(
                nombreContacto = nombreContacto,
                materia = materia,
                mesTexto = mesTexto,
                diaTexto = diaTexto,
                horarioTexto = horarioTexto,
                modalidad = modalidad,
                precioTexto = precioTexto,
                resumenCompleto = resumenCompleto,
                estado = "PROPUESTA"
            )
            sesionDao.insertarSesion(sesion)
        }
    }

    suspend fun confirmarSesionPropuesta(nombreContacto: String, targetUid: String = "") {
        val chatId = getChatId(nombreContacto, targetUid)
        try {
            firestore.collection("sesiones")
                .document(chatId)
                .update("estado", "CONFIRMADA")
                .await()
            sesionDao.confirmarSesionPropuesta(nombreContacto)
        } catch (e: Exception) {
            e.printStackTrace()
            sesionDao.confirmarSesionPropuesta(nombreContacto)
        }
    }

    suspend fun inicializarSesionesSiEstaVacio() {
        sesionDao.eliminarSesionEjemplo()
    }
}
