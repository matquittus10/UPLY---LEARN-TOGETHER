package com.example.uply_learntogether.data

import com.example.uply_learntogether.Models.Usuario

object AuthRepository {

    private val usuarios = mutableListOf(
        Usuario(
            idUsuario = "u1",
            nombreUsuario = "Carlos Estudiante",
            correoInstitucional = "estudiante@uply.edu",
            contrasena = "123456",
            celular = "71234567",
            calificacionComoEstudiante = 5.0f
        ),
        Usuario(
            idUsuario = "u2",
            nombreUsuario = "Prof. Roberto García",
            correoInstitucional = "profesor@uply.edu",
            contrasena = "123456",
            celular = "78901234",
            tarifaHora = 25.0f,
            calificacionComoTutor = 4.8f,
            totalResenasRecibidas = 18
        )
    )

    fun iniciarSesion(
        correo: String,
        contrasena: String
    ): Result<Usuario> {
        val correoLimpio = correo.trim().lowercase()
        val contrasenaLimpia = contrasena.trim()

        if (correoLimpio.isEmpty() || contrasenaLimpia.isEmpty()) {
            return Result.failure(Exception("Por favor completa todos los campos."))
        }

        val usuario = usuarios.find { it.getCorreoInstitucional().lowercase() == correoLimpio }
            ?: return Result.failure(Exception("No existe una cuenta registrada con el correo: $correo"))

        if (usuario.getContrasena() != contrasenaLimpia) {
            return Result.failure(Exception("Contraseña incorrecta."))
        }

        return Result.success(usuario)
    }

    fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasena: String
    ): Result<Usuario> {
        val nombreLimpio = nombre.trim()
        val correoLimpio = correo.trim().lowercase()
        val contrasenaLimpia = contrasena.trim()

        if (nombreLimpio.isEmpty() || correoLimpio.isEmpty() || contrasenaLimpia.isEmpty()) {
            return Result.failure(Exception("Todos los campos son obligatorios."))
        }

        if (usuarios.any { it.getCorreoInstitucional().lowercase() == correoLimpio }) {
            return Result.failure(Exception("El correo ya se encuentra registrado."))
        }

        val nuevoUsuario = Usuario(
            idUsuario = "u_${System.currentTimeMillis()}",
            nombreUsuario = nombreLimpio,
            correoInstitucional = correoLimpio,
            contrasena = contrasenaLimpia
        )

        usuarios.add(nuevoUsuario)
        return Result.success(nuevoUsuario)
    }
}
