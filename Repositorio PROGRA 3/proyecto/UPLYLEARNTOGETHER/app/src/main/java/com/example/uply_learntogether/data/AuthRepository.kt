package com.example.uply_learntogether.data

import com.example.uply_learntogether.Models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

object AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): Usuario? {
        val firebaseUser = auth.currentUser ?: return null
        return Usuario(
            idUsuario = firebaseUser.uid,
            nombreUsuario = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "Usuario",
            correoInstitucional = firebaseUser.email ?: ""
        )
    }

    fun observarSesion(): Flow<Usuario?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            val usuario = firebaseUser?.let {
                Usuario(
                    idUsuario = it.uid,
                    nombreUsuario = it.displayName ?: it.email?.substringBefore("@") ?: "Usuario",
                    correoInstitucional = it.email ?: ""
                ).apply { iniciarSesionCuenta() }
            }
            trySend(usuario)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): Result<Usuario> {
        val correoLimpio = correo.trim().lowercase()
        val contrasenaLimpia = contrasena.trim()

        if (correoLimpio.isEmpty() || contrasenaLimpia.isEmpty()) {
            return Result.failure(Exception("Por favor completa todos los campos."))
        }

        return try {
            val authResult = auth.signInWithEmailAndPassword(correoLimpio, contrasenaLimpia).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val usuario = Usuario(
                    idUsuario = firebaseUser.uid,
                    nombreUsuario = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "Usuario",
                    correoInstitucional = firebaseUser.email ?: correoLimpio
                )
                usuario.iniciarSesionCuenta()
                Result.success(usuario)
            } else {
                Result.failure(Exception("No se pudo obtener el usuario de Firebase."))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(traducirErrorFirebase(e)))
        }
    }

    suspend fun registrarUsuario(
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

        return try {
            val authResult = auth.createUserWithEmailAndPassword(correoLimpio, contrasenaLimpia).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nombreLimpio)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                val usuario = Usuario(
                    idUsuario = firebaseUser.uid,
                    nombreUsuario = nombreLimpio,
                    correoInstitucional = firebaseUser.email ?: correoLimpio
                )
                usuario.iniciarSesionCuenta()
                Result.success(usuario)
            } else {
                Result.failure(Exception("No se pudo registrar el usuario en Firebase."))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(traducirErrorFirebase(e)))
        }
    }

    suspend fun enviarCorreoDeRecuperacion(correo: String): Result<Unit> {
        val correoLimpio = correo.trim().lowercase()
        if (correoLimpio.isEmpty()) {
            return Result.failure(Exception("Por favor ingresa tu correo institucional."))
        }
        return try {
            auth.sendPasswordResetEmail(correoLimpio).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(traducirErrorFirebase(e)))
        }
    }

    fun cerrarSesion() {
        auth.signOut()
    }

    private fun traducirErrorFirebase(e: Exception): String {
        return when (e) {
            is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil. Debe tener al menos 6 caracteres."
            is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña inválidos."
            is FirebaseAuthUserCollisionException -> "Ya existe una cuenta registrada con este correo."
            is FirebaseAuthInvalidUserException -> "No existe una cuenta registrada con este correo."
            else -> e.localizedMessage ?: "Ocurrió un error inesperado en la autenticación."
        }
    }
}
