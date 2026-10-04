package com.example.uply_learntogether.ui.login

import androidx.lifecycle.ViewModel
import com.example.uply_learntogether.Models.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

    // Usando directamente las variables correoInstitucional y contrasena del modelo Usuario
    private val _correoInstitucional = MutableStateFlow("")
    val correoInstitucional: StateFlow<String> = _correoInstitucional.asStateFlow()

    private val _contrasena = MutableStateFlow("")
    val contrasena: StateFlow<String> = _contrasena.asStateFlow()

    private val _errorMensaje = MutableStateFlow<String?>(null)
    val errorMensaje: StateFlow<String?> = _errorMensaje.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _contrasenaVisible = MutableStateFlow(false)
    val contrasenaVisible: StateFlow<Boolean> = _contrasenaVisible.asStateFlow()

    fun onCorreoChanged(nuevoCorreo: String) {
        _correoInstitucional.value = nuevoCorreo
        _errorMensaje.value = null
    }

    fun onContrasenaChanged(nuevaContrasena: String) {
        _contrasena.value = nuevaContrasena
        _errorMensaje.value = null
    }

    fun toggleContrasenaVisible() {
        _contrasenaVisible.value = !_contrasenaVisible.value
    }

    fun cargarPrueba() {
        _correoInstitucional.value = "estudiante@uply.edu"
        _contrasena.value = "123456"
        _errorMensaje.value = null
    }

    fun iniciarSesion() {
        val correo = _correoInstitucional.value.trim()
        val pass = _contrasena.value.trim()

        if (correo.isEmpty() || pass.isEmpty()) {
            _errorMensaje.value = "Por favor, ingresa tu correo institucional y contraseña."
            return
        }

        if (pass.length < 4) {
            _errorMensaje.value = "La contraseña debe tener al menos 4 caracteres."
            return
        }

        // Instanciamos la clase Usuario con sus variables de modelo
        val usuario = Usuario(
            idUsuario = "usr_${System.currentTimeMillis()}",
            nombreUsuario = if (correo.contains("@")) correo.substringBefore("@") else correo,
            correoInstitucional = correo,
            contrasena = pass
        )

        // Usamos la función de la clase Usuario que el usuario ya tenía definida
        usuario.iniciarSesionCuenta()

        _usuarioActual.value = usuario
        _errorMensaje.value = null
    }

    fun cerrarSesion() {
        // Ejecutamos cerrarSesionCuenta() de la clase Usuario
        _usuarioActual.value?.cerrarSesionCuenta()
        _usuarioActual.value = null
        _correoInstitucional.value = ""
        _contrasena.value = ""
        _errorMensaje.value = null
    }
}
