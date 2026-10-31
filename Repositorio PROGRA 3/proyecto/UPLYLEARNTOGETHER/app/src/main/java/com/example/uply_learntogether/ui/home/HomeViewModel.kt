package com.example.uply_learntogether.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uply_learntogether.Models.Tutor
import com.example.uply_learntogether.data.PublicacionRepository
import com.example.uply_learntogether.data.TutorRepository
import com.example.uply_learntogether.data.TutoresFavoritosRepository
import com.example.uply_learntogether.data.local.AppDatabase
import com.example.uply_learntogether.data.local.PublicacionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val tutoresDestacados: List<Tutor> = emptyList(),
    val favoritosIds: List<String> = emptyList(),
    val publicacionesRecientes: List<PublicacionEntity> = emptyList()
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.obtener(application)
    private val tutorRepository = TutorRepository(database.tutorCacheDao())
    private val favoritosRepository = TutoresFavoritosRepository(database.tutorFavoritoDao())
    private val publicacionRepository = PublicacionRepository(database.publicacionDao())

    val uiState: StateFlow<HomeUiState> = combine(
        tutorRepository.observarTutores(),
        favoritosRepository.observarIds(),
        publicacionRepository.observarPublicaciones()
    ) { tutores, favoritosIds, publicaciones ->
        HomeUiState(
            tutoresDestacados = tutores,
            favoritosIds = favoritosIds,
            publicacionesRecientes = publicaciones
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            if (!tutorRepository.hayTutoresGuardados()) {
                val initialTutores = listOf(
                    Tutor("1", "Diego Vargas", "Cálculo I - Álgebra", "4.9", "24 reseñas", "Bs 35 / hora"),
                    Tutor("2", "Valeria Rojas", "Programación I", "4.8", "24 reseñas", "Bs 40 / hora"),
                    Tutor("3", "Carlos Méndez", "Física II", "4.7", "18 reseñas", "Bs 35 / hora")
                )
                tutorRepository.guardarTutoresIniciales(initialTutores)
            }
        }
    }

    fun alternarFavorito(tutor: Tutor) {
        viewModelScope.launch {
            val esFavorito = tutor.id in uiState.value.favoritosIds
            favoritosRepository.alternarFavorito(tutor, esFavorito)
        }
    }
}
