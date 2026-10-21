package com.example.uply_learntogether.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uply_learntogether.data.PublicacionRepository
import com.example.uply_learntogether.data.local.AppDatabase
import com.example.uply_learntogether.data.local.PublicacionEntity
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

    val uiState: StateFlow<MisPublicacionesUiState> = repository.observarPublicaciones()
        .map { lista ->
            MisPublicacionesUiState(publicaciones = lista, cargando = false)
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
