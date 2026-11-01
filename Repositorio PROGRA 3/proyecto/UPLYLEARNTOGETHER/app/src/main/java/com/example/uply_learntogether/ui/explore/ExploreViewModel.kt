package com.example.uply_learntogether.ui.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uply_learntogether.data.local.AppDatabase
import com.example.uply_learntogether.data.repository.PublicacionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExploreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PublicacionRepository(
        AppDatabase.obtener(application).publicacionDao()
    )

    val userPublications: StateFlow<List<ExploreItem>> = repository.observarPublicaciones()
        .map { lista ->
            lista.map { pub ->
                val isOffer = pub.tipo.contains("Oferta", ignoreCase = true) || pub.tipo.contains("tutoría", ignoreCase = true)
                ExploreItem(
                    title = pub.titulo,
                    course = pub.materia,
                    description = pub.descripcion,
                    author = pub.nombreAutor.ifBlank { "Estudiante UPLY" },
                    rating = "5.0 · UPB",
                    price = if (isOffer) "Bs 35 /h" else "",
                    isOffer = isOffer
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
