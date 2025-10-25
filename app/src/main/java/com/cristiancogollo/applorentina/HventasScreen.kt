package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
// import com.example.app_lorentina.R // Recuerda importar tu R

// =================================================================
// 1. PANTALLA HISTORIAL DE VENTAS (HventasScreen)
// =================================================================

@Composable
fun HventasScreen() {
    val colorVerdeClaro = Color(0xFFC2D500)
    val colorVerdeOscuro = Color(0xFFB5CC00)
    val colorCafeTexto = Color(0xFF6B4226) // Color para el título
    val colorGrisTexto = Color(0xFF5C5C5C)

    var searchQuery by remember { mutableStateOf("") }

    // Datos de ventas de ejemplo
    val ventas = listOf(
        Triple("001", "JUAN PEREZ", "$180.000"),
        Triple("002", "MARIA GOMEZ", "$120.000"),
        Triple("003", "CARLOS RUIZ", "$250.000"),
        Triple("004", "ANA LOPEZ", "$180.000"),
        Triple("005", "PEDRO RAMIREZ", "$90.000")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF))
            .padding(top = 16.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Encabezado verde (Logo Lorentina)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorVerdeClaro, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .padding(vertical = 15.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.lorenita),
                            contentDescription = "Logo Lorentina",
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(200.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Título de la Pantalla
                    Text(
                        text = "HISTORIAL DE VENTAS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF5C5C5C)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Barra de búsqueda (Buscar Venta)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("BUSCAR VENTA....", color = Color.Gray.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = colorVerdeClaro) },
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
                        // Usamos un nuevo componente de filtro para replicar el estilo de Historial de Ventas
                        HventasFilterButton(text = "CLIENTE", isSelected = true, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        HventasFilterButton(text = "FECHA", isSelected = false, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        HventasFilterButton(text = "C.C", isSelected = false, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de ventas
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 24.dp)
                    ) {
                        items(ventas) { (numVenta, nombre, monto) ->
                            VentaCard(
                                numVenta = numVenta,
                                nombreCliente = nombre,
                                monto = monto,
                                colorVerdeClaro = colorVerdeClaro
                            )
                        }
                    }

                    // Spacer para dejar espacio al FAB
                    Spacer(modifier = Modifier.height(80.dp))
                }

                // Botón flotante (FAB) - AGREGAR VENTA
                FloatingActionButton(
                    onClick = { /* TODO: agregar venta */ },
                    containerColor = colorVerdeClaro,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 24.dp)
                        .size(90.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar venta",
                            modifier = Modifier.size(60.dp)
                        )
                        Text(
                            "AGREGAR VENTA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            lineHeight = 10.sp
                        )
                    }
                }
            }
        }
    }
}

// =================================================================
// 2. COMPONENTES AUXILIARES
// =================================================================

@Composable
fun HventasFilterButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier) {
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
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
        modifier = modifier.height(35.dp) // Altura ajustada para el mockup
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun VentaCard(numVenta: String, nombreCliente: String, monto: String, colorVerdeClaro: Color) {
    val colorGrisTexto = Color(0xFF5C5C5C)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            // Borde verde claro igual al mockup
            .border(2.dp, colorVerdeClaro, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Sin sombra para un look plano
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna 1: Número de Venta
            Text(
                numVenta,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colorGrisTexto,
                modifier = Modifier.width(30.dp) // Ancho fijo para alinear
            )
            Spacer(modifier = Modifier.width(10.dp))

            // Columna 2: Nombre del Cliente (Expande)
            Text(
                nombreCliente,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = colorGrisTexto,
                modifier = Modifier.weight(1f)
            )

            // Columna 3: Monto
            Text(
                monto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colorGrisTexto
            )
        }
    }
}

// =================================================================
// 3. PREVIEW
// =================================================================

@Preview(showBackground = true)
@Composable
fun HventasScreenPreview() {
    Surface(color = Color(0xFFEFEFEF)) {
        HventasScreen()
    }
}