package com.cristiancogollo.applorentina


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

@Composable
fun BuscarClienteScreen(onBackClick: () -> Unit,
                        onAddClientClick: () -> Unit) {
    val colorVerdeClaro = Color(0xFFC2D500)
    val colorVerdeOscuro = Color(0xFFB5CC00)
    val colorGrisTexto = Color(0xFF5C5C5C)

    var searchQuery by remember { mutableStateOf("") }

    val clientes = listOf(
        "Juan Perez",
        "Maria Gomez",
        "Carlos Ruiz",
        "Laura Sanchez",
        "Ana Lopez",
        "Pedro Ramirez",
        "Sofia Torres",
        "Luis Castro",
        "Elena Díaz",
        "Javier Soto",
        "Marta Vidal",
        "Ricardo Núñez" // Más clientes para que la lista desplace
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF)),
            //.padding(top = 16.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Contenedor principal para el contenido blanco
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            //elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            // Utilizamos Box para apilar el contenido principal (Column) y el FAB
            Box(modifier = Modifier.fillMaxSize()) {

                // 1. Contenido principal (Encabezado, Búsqueda, Filtros y Lista)
                Column(
                    modifier = Modifier
                        .fillMaxSize(), // El Column toma todo el espacio
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Encabezado verde - MODIFICADO para mostrar la IMAGEN compacta
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                colorVerdeClaro,
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            )
                            .padding(vertical = 15.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 8.dp)
                        ) {
                            IconButton(onClick = onBackClick) { // 👈 Llama a la acción de volver
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = Color.White,
                                    modifier = Modifier.size(35.dp)
                                )
                            }
                        }

                        // RESTAURACIÓN DE LA IMAGEN DE LOGO COMPLETA

                        Image(
                            painter = painterResource(id = R.drawable.lorenita),
                            contentDescription = "Logo Lorentina",
                            modifier = Modifier
                                .fillMaxWidth(0.6f) // 60% del ancho para un tamaño compacto
                                .height(200.dp), // Altura fija
                            contentScale = ContentScale.Fit
                        )

                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "BUSCAR CLIENTE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorGrisTexto
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Barra de búsqueda
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "BUSCAR CLIENTE....",
                                color = Color.Gray.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = Color.Gray.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorVerdeClaro,
                            unfocusedBorderColor = colorVerdeClaro,
                            cursorColor = colorVerdeOscuro
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtros
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterButton(
                            text = "NOMBRE",
                            isSelected = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterButton(
                            text = "C.C",
                            isSelected = false,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterButton(
                            text = "TELÉFONO",
                            isSelected = false,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Lista de clientes (Área desplazable)
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 24.dp)
                    ) {
                        items(clientes) { cliente ->
                            ClienteCard(nombre = cliente)
                        }
                    }

                    // Añadimos un padding inferior para que el último elemento no quede oculto por el FAB
                    Spacer(modifier = Modifier.height(80.dp))
                }

                // 3. Botón flotante (SIN MODIFICACIONES)
                FloatingActionButton(
                    onClick = onAddClientClick,
                    containerColor = colorVerdeOscuro,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Posicionamiento clave
                        .padding(end = 24.dp, bottom = 24.dp) // Márgenes finales
                        // Aumentamos el tamaño del FAB para que quepa el texto dentro
                        .size(90.dp) // Tamaño ajustado para envolver el contenido (icono + texto)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        // Eliminamos el padding interno ya que el .size() lo ajusta
                    ) {
                        // Icono de suma (+)
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar cliente",
                            modifier = Modifier.size(60.dp) // Tamaño del icono
                        )

                        // Texto "AGREGAR CLIENTE"
                        Text(
                            "AGREGAR CLIENTE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            // Pequeño ajuste en la línea de base para que el texto quede justo debajo del icono
                            lineHeight = 10.sp
                        )
                    }
                }
            } // Fin del Box
        } // Fin del Card
    } // Fin del Column exterior
}


@Composable
fun FilterButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    val containerColor = if (isSelected) Color(0xFFB5CC00) else Color(0xFFEFF5C9)
    val contentColor = if (isSelected) Color.White else Color(0xFF8AA100)
    val borderColor = if (isSelected) Color(0xFFB5CC00) else Color(0xFFEFF5C9)

    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(10.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(borderColor)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        modifier = modifier
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ClienteCard(nombre: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(nombre, fontWeight = FontWeight.Bold, color = Color(0xFF5C5C5C))
                Text("28/09/2025", fontSize = 12.sp, color = Color.Gray)
            }
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB5CC00),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("VER DETALLES", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BuscarClientePreview() {
    // Asegúrate de que tu tema principal esté correctamente definido
    // Para la vista previa, podemos usar un tema básico o tu AppLorentinaTheme
    // Si AppLorentinaTheme requiere un contexto, usa AndroidView para envolverlo.
    // Para simplificar la vista previa, usaré un Surface.
    AppLorentinaTheme {
        Surface(color = Color(0xFFEFEFEF)) { // El color de fondo de la preview para el "borde"
            BuscarClienteScreen(onBackClick = {},
                onAddClientClick = {})
        }
    }
}