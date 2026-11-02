package com.example.uply_learntogether.data.repository

import com.example.uply_learntogether.data.local.dao.SesionDao
import com.example.uply_learntogether.data.local.entity.SesionEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.math.abs

class SesionRepository(private val sesionDao: SesionDao) {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun observarSesionPropuesta(nombreContacto: String): Flow<SesionEntity?> =
        sesionDao.observarSesionPropuesta(nombreContacto)

    fun observarProximaSesionConfirmada(): Flow<SesionEntity?> = callbackFlow {
        val listener = firestore.collection("sesiones")
            .whereEqualTo("estado", "CONFIRMADA")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || snapshot.isEmpty) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val doc = snapshot.documents.first()
                val nombre = doc.getString("nombreContacto") ?: ""
                val materia = doc.getString("materia") ?: ""
                val mes = doc.getString("mesTexto") ?: ""
                val dia = doc.getString("diaTexto") ?: ""
                val horario = doc.getString("horarioTexto") ?: ""
                val modalidad = doc.getString("modalidad") ?: ""
                val precio = doc.getString("precioTexto") ?: ""
                val resumen = doc.getString("resumenCompleto") ?: ""
                val estado = doc.getString("estado") ?: "CONFIRMADA"

                val sesion = SesionEntity(
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
                trySend(sesion)
            }
        awaitClose { listener.remove() }
    }

    fun observarTodasLasSesiones(): Flow<List<SesionEntity>> = callbackFlow {
        val listener = firestore.collection("sesiones")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                val sesiones = snapshot.documents.mapNotNull { doc ->
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
        materia: String,
        mesTexto: String,
        diaTexto: String,
        horarioTexto: String,
        modalidad: String,
        precioTexto: String,
        resumenCompleto: String
    ) {
        val currentUserId = auth.currentUser?.uid ?: ""
        val sesionId = "${currentUserId}_${nombreContacto.lowercase().replace(" ", "_")}_${System.currentTimeMillis()}"

        val sesionData = hashMapOf(
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
                .document(sesionId)
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

    suspend fun confirmarSesionPropuesta(nombreContacto: String) {
        try {
            val querySnapshot = firestore.collection("sesiones")
                .whereEqualTo("nombreContacto", nombreContacto)
                .whereEqualTo("estado", "PROPUESTA")
                .get()
                .await()

            for (doc in querySnapshot.documents) {
                doc.reference.update("estado", "CONFIRMADA").await()
            }
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
