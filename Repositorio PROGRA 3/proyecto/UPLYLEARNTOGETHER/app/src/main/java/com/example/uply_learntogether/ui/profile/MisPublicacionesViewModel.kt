package com.example.uply_learntogether.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uply_learntogether.data.local.AppDatabase
import com.example.uply_learntogether.data.local.entity.PublicacionEntity
import com.example.uply_learntogether.data.repository.PublicacionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MisPublicacionesUiState(
    val publicaciones: List<PublicacionEntity> = emptyList(),
    val cargando: Boolean = false
)

class MisPublicacionesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PublicacionRepository(
        AppDatabase.obtener(application).publicacionDao()
    )
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val uiState: StateFlow<MisPublicacionesUiState> = repository.observarPublicaciones()
        .map { lista ->
            val currentUid = auth.currentUser?.uid ?: ""
            val currentName = auth.currentUser?.displayName
                ?: auth.currentUser?.email?.substringBefore("@")
                ?: ""

            // Filtrar únicamente las publicaciones del usuario actual
            val misPublicaciones = lista.filter { pub ->
                when {
                    pub.autorUid.isNotBlank() && currentUid.isNotBlank() -> pub.autorUid == currentUid
                    currentName.isNotBlank() -> pub.nombreAutor.equals(currentName, ignoreCase = true)
                    else -> false
                }
            }

            MisPublicacionesUiState(publicaciones = misPublicaciones, cargando = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MisPublicacionesUiState(cargando = true)
        )

    fun eliminar(publicacion: PublicacionEntity) {
        viewModelScope.launch {
            repository.eliminarPublicacion(publicacion)
        }
    }
}
