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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uply_learntogether.ui.chat.ChatScreen
import com.example.uply_learntogether.ui.chat.ConversationsScreen
import com.example.uply_learntogether.ui.chat.ScreenConfiguration
import com.example.uply_learntogether.ui.explore.ExploreScreen
import com.example.uply_learntogether.ui.home.HomeScreen
import com.example.uply_learntogether.ui.login.LoginScreen
import com.example.uply_learntogether.ui.login.LoginViewModel
import com.example.uply_learntogether.ui.publish.PublishScreen
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
    var currentScreen by remember { mutableStateOf("home") }
    var selectedContactName by remember { mutableStateOf("Valeria Rojas") }

    val proposedSessionsMap = remember { mutableStateMapOf<String, Boolean>() }
    val proposedSessionsTextMap = remember { mutableStateMapOf<String, String>() }

    val usuario = usuarioActual
    if (usuario == null || !usuario.isSesionIniciada()) {
        LoginScreen(viewModel = loginViewModel)
    } else {
        when (currentScreen) {
            "home" -> HomeScreen(
                usuario = usuario,
                onCerrarSesion = { loginViewModel.cerrarSesion() },
                onExploreClick = { currentScreen = "explore" },
                onPublishClick = { currentScreen = "publish" },
                onChatClick = { currentScreen = "conversations" }
            )
            "explore" -> ExploreScreen(
                onHomeClick = { currentScreen = "home" },
                onBackClick = { currentScreen = "home" },
                onPublishClick = { currentScreen = "publish" },
                onChatClick = { currentScreen = "conversations" }
            )
            "publish" -> PublishScreen(
                onHomeClick = { currentScreen = "home" },
                onExploreClick = { currentScreen = "explore" },
                onBackClick = { currentScreen = "home" },
                onChatClick = { currentScreen = "conversations" }
            )
            "conversations" -> ConversationsScreen(
                onHomeClick = { currentScreen = "home" },
                onExploreClick = { currentScreen = "explore" },
                onPublishClick = { currentScreen = "publish" },
                onBackClick = { currentScreen = "home" },
                onConversationClick = { contact ->
                    selectedContactName = contact
                    currentScreen = "chat_detail"
                }
            )
            "chat_detail" -> ChatScreen(
                contactName = selectedContactName,
                hasProposedSession = proposedSessionsMap[selectedContactName] == true,
                proposedSessionSummary = proposedSessionsTextMap[selectedContactName] ?: "Lunes 5 oct. · 16:00 · Virtual · Bs 35",
                onHomeClick = { currentScreen = "home" },
                onExploreClick = { currentScreen = "explore" },
                onPublishClick = { currentScreen = "publish" },
                onBackClick = { currentScreen = "conversations" },
                onConfigureSessionClick = { currentScreen = "configure_session" }
            )
            "configure_session" -> ScreenConfiguration(
                contactName = selectedContactName,
                onHomeClick = { currentScreen = "home" },
                onExploreClick = { currentScreen = "explore" },
                onPublishClick = { currentScreen = "publish" },
                onChatClick = { currentScreen = "conversations" },
                onBackClick = { currentScreen = "chat_detail" },
                onConfirmSession = { summary ->
                    proposedSessionsMap[selectedContactName] = true
                    proposedSessionsTextMap[selectedContactName] = summary
                    currentScreen = "chat_detail"
                }
            )
        }
    }
}
