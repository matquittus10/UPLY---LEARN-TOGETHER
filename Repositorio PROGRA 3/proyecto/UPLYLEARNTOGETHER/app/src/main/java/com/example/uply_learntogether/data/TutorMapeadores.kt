package com.example.uply_learntogether.data

import com.example.uply_learntogether.Models.Tutor
import com.example.uply_learntogether.data.local.TutorFavoritoEntity

fun TutorFavoritoEntity.aTutor(): Tutor =
    Tutor(
        id = id,
        nombre = nombre,
        materia = materia,
        calificacion = calificacion,
        resenas = resenas,
        precio = precio
    )

fun Tutor.aEntidad(): TutorFavoritoEntity =
    TutorFavoritoEntity(
        id = id,
        nombre = nombre,
        materia = materia,
        calificacion = calificacion,
        resenas = resenas,
        precio = precio
    )
