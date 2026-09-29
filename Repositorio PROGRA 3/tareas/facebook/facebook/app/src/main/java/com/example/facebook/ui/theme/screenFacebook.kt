package com.example.emptyactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Color(0xFF18191A),
                    surface = Color(0xFF242526),
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                FacebookApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacebookApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF18191A),
                modifier = Modifier.width(320.dp)
            ) {
                DrawerMenuContent()
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "facebook",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Add, contentDescription = "Crear", tint = Color.White)
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Send, contentDescription = "Messenger", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF18191A))
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF242526)) {
                    NavigationBarItem(selected = true, onClick = { }, icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") })
                    NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Outlined.OndemandVideo, contentDescription = "Video") })
                    NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Outlined.People, contentDescription = "Amigos") })
                    NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Outlined.Storefront, contentDescription = "Marketplace") })
                    NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones") })
                    NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = "Perfil") })
                }
            },
            containerColor = Color(0xFF18191A)
        ) { innerPadding ->
            MainFeedContent(modifier = Modifier.padding(innerPadding))
        }
    }
}

// ---------------------------------------------------------------------------
// CONTENIDO DE LA PANTALLA PRINCIPAL (FEEDS, HISTORIAS Y SUGERENCIAS)
// ---------------------------------------------------------------------------

@Composable
fun MainFeedContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Sección "¿Qué estás pensando?"
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color.Gray
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Gray))
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                        Text("¿Qué estás pensando?", color = Color.LightGray, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Image, contentDescription = "Foto", tint = Color.Green)
                }
            }
            HorizontalDivider(color = Color(0xFF3E4042), thickness = 1.dp)
        }

        // Sección de Historias
        item {
            LazyRow(
                modifier = Modifier.padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                item { CreateStoryCard() }
                item { StoryCard(title = "Instituto Domingo Savi...", subtitle = "Feliz Cumpleaños") }
                item { StoryCard(title = "Busca amigos en tus contactos", subtitle = "") }
            }
            HorizontalDivider(color = Color(0xFF3E4042), thickness = 1.dp)
        }

        // Sección "Personas que quizá conozcas"
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, contentDescription = null, tint = Color.LightGray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Personas que quizá conozcas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                    Row {
                        IconButton(onClick = { }) { Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = Color.Gray) }
                        IconButton(onClick = { }) { Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray) }
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    item { FriendSuggestionCard("Tung tung sahur") }
                    item { FriendSuggestionCard("Jose Miranda Clave 67") }
                    item { FriendSuggestionCard("Leon S. Kennedy de Temu") }
                }
            }
        }
    }
}

@Composable
fun CreateStoryCard() {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(180.dp)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.2f)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.White)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                        .padding(8.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text("Crear historia", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            SmallFloatingActionButton(
                onClick = { },
                shape = CircleShape,
                containerColor = Color(0xFF1877F2),
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 10.dp)
                    .size(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    }
}

@Composable
fun StoryCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(180.dp)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 2)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, color = Color.Gray, fontSize = 10.sp, maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun FriendSuggestionCard(name: String) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF242526))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar a...", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                FilledTonalButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF3A3B3C), contentColor = Color.White)
                ) {
                    Text("Elimi...", fontSize = 12.sp)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// CONTENIDO DEL MENÚ LATERAL (SEGUNDA IMAGEN)
// ---------------------------------------------------------------------------

@Composable
fun DrawerMenuContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color.Gray
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Alan Jose Turing", fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242526)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Crear página de Facebook", fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item { MenuItemRow(Icons.Default.AutoAwesome, "Meta AI") }
        item { MenuItemRow(Icons.Outlined.Bookmark, "Guardado") }
        item { MenuItemRow(Icons.Outlined.History, "Recuerdos") }
        item { MenuItemRow(Icons.Outlined.Storefront, "Marketplace") }
        item { MenuItemRow(Icons.Outlined.FavoriteBorder, "Parejas") }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3B3C)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ver más", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item { ExpandableMenuItem("Ayuda y soporte técnico", Icons.Outlined.HelpOutline) }
        item { ExpandableMenuItem("Configuración y privacidad", Icons.Outlined.Settings) }
        item { ExpandableMenuItem("Mejoras", Icons.Outlined.GridView) }
    }
}

@Composable
fun MenuItemRow(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ExpandableMenuItem(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
    }
}