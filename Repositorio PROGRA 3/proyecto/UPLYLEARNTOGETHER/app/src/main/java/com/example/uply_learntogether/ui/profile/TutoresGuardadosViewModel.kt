package com.example.uply_learntogether.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uply_learntogether.Models.Tutor
import com.example.uply_learntogether.data.TutoresFavoritosRepository
import com.example.uply_learntogether.data.local.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TutoresGuardadosUiState(
    val tutoresGuardados: List<Tutor> = emptyList(),
    val cargando: Boolean = false
)

class TutoresGuardadosViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TutoresFavoritosRepository(
        AppDatabase.obtener(application).tutorFavoritoDao()
    )

    val uiState: StateFlow<TutoresGuardadosUiState> = repository.observarFavoritos()
        .map { lista ->
            TutoresGuardadosUiState(tutoresGuardados = lista, cargando = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TutoresGuardadosUiState(cargando = true)
        )

    fun quitar(id: String) {
        viewModelScope.launch {
            repository.quitar(id)
        }
    }
}
