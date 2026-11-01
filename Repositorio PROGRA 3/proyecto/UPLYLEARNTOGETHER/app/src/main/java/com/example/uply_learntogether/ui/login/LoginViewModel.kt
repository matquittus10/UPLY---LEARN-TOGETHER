package com.example.uply_learntogether.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uply_learntogether.Models.Usuario
import com.example.uply_learntogether.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _correoInstitucional = MutableStateFlow("")
    val correoInstitucional: StateFlow<String> = _correoInstitucional.asStateFlow()

    private val _contrasena = MutableStateFlow("")
    val contrasena: StateFlow<String> = _contrasena.asStateFlow()

    private val _esRegistro = MutableStateFlow(false)
    val esRegistro: StateFlow<Boolean> = _esRegistro.asStateFlow()

    private val _errorMensaje = MutableStateFlow<String?>(null)
    val errorMensaje: StateFlow<String?> = _errorMensaje.asStateFlow()

    private val _infoMensaje = MutableStateFlow<String?>(null)
    val infoMensaje: StateFlow<String?> = _infoMensaje.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _contrasenaVisible = MutableStateFlow(false)
    val contrasenaVisible: StateFlow<Boolean> = _contrasenaVisible.asStateFlow()

    private val authStateListener: FirebaseAuth.AuthStateListener

    init {
        val currentUser = AuthRepository.getCurrentUser()
        if (currentUser != null) {
            currentUser.iniciarSesionCuenta()
            _usuarioActual.value = currentUser
        }

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val usuario = Usuario(
                    idUsuario = firebaseUser.uid,
                    nombreUsuario = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "Usuario",
                    correoInstitucional = firebaseUser.email ?: ""
                )
                usuario.iniciarSesionCuenta()
                _usuarioActual.value = usuario
            } else {
                _usuarioActual.value = null
            }
        }
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

    fun onNombreChanged(nuevoNombre: String) {
        _nombre.value = nuevoNombre
        _errorMensaje.value = null
    }

    fun onCorreoChanged(nuevoCorreo: String) {
        _correoInstitucional.value = nuevoCorreo
        _errorMensaje.value = null
        _infoMensaje.value = null
    }

    fun onContrasenaChanged(nuevaContrasena: String) {
        _contrasena.value = nuevaContrasena
        _errorMensaje.value = null
    }

    fun toggleContrasenaVisible() {
        _contrasenaVisible.value = !_contrasenaVisible.value
    }

    fun toggleModoRegistro() {
        _esRegistro.value = !_esRegistro.value
        _errorMensaje.value = null
        _infoMensaje.value = null
    }

    fun iniciarSesion() {
        val correo = _correoInstitucional.value.trim()
        val pass = _contrasena.value.trim()

        if (correo.isEmpty() || pass.isEmpty()) {
            _errorMensaje.value = "Por favor, ingresa tu correo y contraseña."
            return
        }

        _cargando.value = true
        _errorMensaje.value = null

        viewModelScope.launch {
            val result = AuthRepository.iniciarSesion(correo, pass)
            _cargando.value = false
            result.onSuccess { usuario ->
                _usuarioActual.value = usuario
                _errorMensaje.value = null
            }.onFailure { exception ->
                _errorMensaje.value = exception.localizedMessage ?: "Error al iniciar sesión"
            }
        }
    }

    fun registrarUsuario() {
        val nom = _nombre.value.trim()
        val correo = _correoInstitucional.value.trim()
        val pass = _contrasena.value.trim()

        if (nom.isEmpty() || correo.isEmpty() || pass.isEmpty()) {
            _errorMensaje.value = "Todos los campos son obligatorios para registrarse."
            return
        }

        if (pass.length < 6) {
            _errorMensaje.value = "La contraseña debe tener al menos 6 caracteres."
            return
        }

        _cargando.value = true
        _errorMensaje.value = null

        viewModelScope.launch {
            val result = AuthRepository.registrarUsuario(nom, correo, pass)
            _cargando.value = false
            result.onSuccess { usuario ->
                _usuarioActual.value = usuario
                _errorMensaje.value = null
            }.onFailure { exception ->
                _errorMensaje.value = exception.localizedMessage ?: "Error al registrar usuario"
            }
        }
    }

    fun enviarCorreoDeRecuperacion() {
        val correo = _correoInstitucional.value.trim()
        if (correo.isEmpty()) {
            _errorMensaje.value = "Por favor, ingresa tu correo institucional arriba para recuperar tu contraseña."
            return
        }

        _cargando.value = true
        _errorMensaje.value = null
        _infoMensaje.value = null

        viewModelScope.launch {
            val result = AuthRepository.enviarCorreoDeRecuperacion(correo)
            _cargando.value = false
            result.onSuccess {
                _infoMensaje.value = "Correo de recuperación enviado. Revisa tu bandeja de entrada."
            }.onFailure { exception ->
                _errorMensaje.value = exception.localizedMessage ?: "Error al enviar correo de recuperación"
            }
        }
    }

    fun cerrarSesion() {
        AuthRepository.cerrarSesion()
        _usuarioActual.value?.cerrarSesionCuenta()
        _usuarioActual.value = null
        _correoInstitucional.value = ""
        _contrasena.value = ""
        _nombre.value = ""
        _errorMensaje.value = null
        _infoMensaje.value = null
    }
}
