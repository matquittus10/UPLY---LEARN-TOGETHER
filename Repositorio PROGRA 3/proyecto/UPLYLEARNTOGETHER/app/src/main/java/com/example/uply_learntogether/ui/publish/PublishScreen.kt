package com.example.uply_learntogether.ui.publish

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uply_learntogether.ui.theme.UPLYLEARNTOGETHERTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishScreen(
    onHomeClick: () -> Unit = {},
    onExploreClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var selectedType by remember { mutableIntStateOf(0) } // 0: Necesito ayuda, 1: Ofrezco tutoría
    var titleText by remember { mutableStateOf("") }
    var selectedMateria by remember { mutableStateOf("Cálculo I") }
    var isMateriaExpanded by remember { mutableStateOf(false) }
    var descriptionText by remember { mutableStateOf("") }

    val materiasList = listOf(
        "Cálculo I",
        "Programación I",
        "Física II",
        "Álgebra Lineal",
        "Estadística",
        "Química General"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = onHomeClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explorar") },
                    label = { Text("Explorar") },
                    selected = false,
                    onClick = onExploreClick
                )
                NavigationBarItem(
                    icon = {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF4F46E5),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Publicar",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = "Publicar",
                            color = Color(0xFF4F46E5),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chats") },
                    label = { Text("Chats") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = { }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Header: Botón Atrás + Título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF1E293B)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Crear publicación",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            // Subtítulo
            Text(
                text = "Una publicación, muchas oportunidades.",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Opciones Tipo de Publicación (Necesito ayuda / Ofrezco tutoría)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val isHelpSelected = selectedType == 0
                Surface(
                    onClick = { selectedType = 0 },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isHelpSelected) Color(0xFFEEF2FF) else Color.White,
                    border = BorderStroke(
                        width = if (isHelpSelected) 1.5.dp else 1.dp,
                        color = if (isHelpSelected) Color(0xFF6366F1) else Color(0xFFE2E8F0)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Necesito ayuda",
                            fontWeight = FontWeight.Bold,
                            color = if (isHelpSelected) Color(0xFF4F46E5) else Color(0xFF334155),
                            fontSize = 14.sp
                        )
                    }
                }

                val isTutorSelected = selectedType == 1
                Surface(
                    onClick = { selectedType = 1 },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isTutorSelected) Color(0xFFEEF2FF) else Color.White,
                    border = BorderStroke(
                        width = if (isTutorSelected) 1.5.dp else 1.dp,
                        color = if (isTutorSelected) Color(0xFF6366F1) else Color(0xFFE2E8F0)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Ofrezco tutoría",
                            fontWeight = FontWeight.Bold,
                            color = if (isTutorSelected) Color(0xFF4F46E5) else Color(0xFF334155),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título de tu publicación
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Título de tu publicación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    placeholder = {
                        Text("Ej. Te ayudo a aprobar Cálculo I", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Materia
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Materia",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = isMateriaExpanded,
                    onExpandedChange = { isMateriaExpanded = !isMateriaExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedMateria,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMateriaExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = isMateriaExpanded,
                        onDismissRequest = { isMateriaExpanded = false }
                    ) {
                        materiasList.forEach { materia ->
                            DropdownMenuItem(
                                text = { Text(materia) },
                                onClick = {
                                    selectedMateria = materia
                                    isMateriaExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Descripción
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Descripción",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { if (it.length <= 500) descriptionText = it },
                    placeholder = {
                        Text(
                            "Cuéntale a la comunidad cómo puedes ayudar o qué necesitas...",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${descriptionText.length}/500",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección Vista Previa
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Vista previa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = if (selectedType == 0) "Solicitud de ayuda" else "Oferta de tutoría",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "Hace 2 h",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = titleText.ifBlank { "¿Me ayudas con Física II?" },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$selectedMateria  ·  2.º semestre",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = descriptionText.ifBlank { "Busco apoyo para mi parcial de electromagnetismo. ¡Aprendamos juntos!" },
                            fontSize = 14.sp,
                            color = Color(0xFF475569),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE0E7FF),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "CF",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4F46E5),
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = "Camila Flores",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1E293B)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Calificación",
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "4.9  ·  UMSA",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botones de acción
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { /* Sin funcionalidad */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5)
                    )
                ) {
                    Text(
                        text = "Publicar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onBackClick
                ) {
                    Text(
                        text = "Cancelar",
                        fontSize = 15.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PublishScreenPreview() {
    UPLYLEARNTOGETHERTheme {
        PublishScreen()
    }
}
