package com.example.uply_learntogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uply_learntogether.ui.home.HomeScreen
import com.example.uply_learntogether.ui.login.LoginScreen
import com.example.uply_learntogether.ui.login.LoginViewModel
import com.example.uply_learntogether.ui.theme.UPLYLEARNTOGETHERTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UPLYLEARNTOGETHERTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UplyApp()
                }
            }
        }
    }
}

@Composable
fun UplyApp(loginViewModel: LoginViewModel = viewModel()) {
    val usuarioActual by loginViewModel.usuarioActual.collectAsState()

    val usuario = usuarioActual
    if (usuario == null || !usuario.isSesionIniciada()) {
        LoginScreen(viewModel = loginViewModel)
    } else {
        HomeScreen(
            usuario = usuario,
            onCerrarSesion = { loginViewModel.cerrarSesion() }
        )
    }
}
