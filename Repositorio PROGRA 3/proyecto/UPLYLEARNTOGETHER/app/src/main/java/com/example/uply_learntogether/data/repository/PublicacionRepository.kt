package com.example.uply_learntogether.data.repository

import com.example.uply_learntogether.data.local.dao.PublicacionDao
import com.example.uply_learntogether.data.local.entity.PublicacionEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PublicacionRepository(private val dao: PublicacionDao) {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    /**
     * Observa las publicaciones en tiempo real desde la colección "publicaciones" de Firestore.
     */
    fun observarPublicaciones(): Flow<List<PublicacionEntity>> = callbackFlow {
        val listener = firestore.collection("publicaciones")
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if ((error != null) || (snapshot == null)) {
                    return@addSnapshotListener
                }

                val lista = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id.hashCode().toLong()
                    val autorUid = doc.getString("autorUid") ?: ""
                    val tipo = doc.getString("tipo") ?: "Oferta de tutoría"
                    val titulo = doc.getString("titulo") ?: ""
                    val materia = doc.getString("materia") ?: ""
                    val descripcion = doc.getString("descripcion") ?: ""
                    val nombreAutor = doc.getString("nombreAutor") ?: "Estudiante UPLY"
                    val fechaCreacion = doc.getLong("fechaCreacion") ?: System.currentTimeMillis()

                    PublicacionEntity(
                        id = id,
                        autorUid = autorUid,
                        tipo = tipo,
                        titulo = titulo,
                        materia = materia,
                        descripcion = descripcion,
                        nombreAutor = nombreAutor,
                        fechaCreacion = fechaCreacion,
                    )
                }

                trySend(lista)
            }

        awaitClose { listener.remove() }
    }

    // Inserta la publicación en Firestore y en Room localmente
    suspend fun insertarPublicacion(
        tipo: String,
        titulo: String,
        materia: String,
        descripcion: String,
        nombreAutor: String = "Usuario UPLY",
    ) {
        val fechaActual = System.currentTimeMillis()
        val currentUid = auth.currentUser?.uid ?: ""

        // 1. Guardar en Firestore para sincronización global
        try {
            val pubMap = hashMapOf(
                "tipo" to tipo,
                "titulo" to titulo,
                "materia" to materia,
                "descripcion" to descripcion,
                "nombreAutor" to nombreAutor,
                "fechaCreacion" to fechaActual,
                "autorUid" to currentUid,
            )
            firestore.collection("publicaciones").add(pubMap).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Guardar también localmente en Room
        val entidad = PublicacionEntity(
            autorUid = currentUid,
            tipo = tipo,
            titulo = titulo,
            materia = materia,
            descripcion = descripcion,
            nombreAutor = nombreAutor,
            fechaCreacion = fechaActual,
        )
        dao.insertarPublicacion(entidad)
    }

    // Elimina la publicación tanto de Room localmente como de Firestore en la nube
    suspend fun eliminarPublicacion(entidad: PublicacionEntity) {
        // 1. Eliminar localmente de Room
        dao.eliminarPublicacion(entidad)

        // 2. Eliminar de Firestore buscando por título y fecha de creación
        try {
            val querySnapshot = firestore.collection("publicaciones")
                .whereEqualTo("titulo", entidad.titulo)
                .whereEqualTo("fechaCreacion", entidad.fechaCreacion)
                .get()
                .await()

            for (doc in querySnapshot.documents) {
                doc.reference.delete().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
