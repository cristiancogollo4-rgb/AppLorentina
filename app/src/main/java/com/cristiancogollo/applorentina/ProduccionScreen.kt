package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
// import com.example.app_lorentina.R // Recuerda importar tu R

// =================================================================
// 1. PANTALLA PRODUCCIÓN
// =================================================================

@Composable
fun ProduccionScreen() {
    val colorVerdeClaro = Color(0xFFC2D500)
    val colorVerdeOscuro = Color(0xFFB5CC00)
    val colorGrisTexto = Color(0xFF5C5C5C)

    var searchQuery by remember { mutableStateOf("") }

    // Datos de producción de ejemplo
    val productosEnProduccion = listOf(
        Pair("1028 COÑAC", "En Corte"),
        Pair("1006 BLANCO", "En Montaje"),
        Pair("1046 BLANCO X GOYA", "Terminado")
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
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Encabezado verde (Logo Lorentina)
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

                // 2. Título de la Pantalla (MODIFICADO)
                Text(
                    text = "REF. EN PRODUCCION",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C5C5C)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Barra de búsqueda (Buscar Referencia) - Borde verde claro
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("BUSCAR REFERENCIA....", color = Color.Gray.copy(alpha = 0.7f)) },
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

                // 4. Lista de Productos en Producción
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(productosEnProduccion) { (referencia, estado) ->
                        ProduccionCard(
                            referencia = referencia,
                            estado = estado,
                            colorVerdeClaro = colorVerdeClaro,
                            colorGrisTexto = colorGrisTexto
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// COMPONENTES AUXILIARES DE PRODUCCIÓN
// -----------------------------------------------------------------

@Composable
fun TallaStockDisplay(talla: String, stock: String, colorGrisTexto: Color) {
    // Componente de solo lectura que usamos en StockScreen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$talla : $stock",
            fontSize = 14.sp,
            color = colorGrisTexto,
            fontWeight = FontWeight.Normal,
        )
    }
}


@Composable
fun ProduccionCard(referencia: String, estado: String, colorVerdeClaro: Color, colorGrisTexto: Color) {
    // Definición de tallas simulada
    val stockSimulado = mapOf(
        "35" to "3", "36" to "3", "37" to "3", "38" to "3",
        "39" to "3", "40" to "3", "41" to "3", "42" to "3"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(2.dp, colorVerdeClaro, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna de Tallas y Stock (lado izquierdo)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    referencia,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = colorGrisTexto
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Distribución de Tallas (2 columnas de solo lectura)
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Columna 1
                    Column(modifier = Modifier.weight(1f)) {
                        TallaStockDisplay("35", stockSimulado["35"]!!, colorGrisTexto)
                        TallaStockDisplay("36", stockSimulado["36"]!!, colorGrisTexto)
                        TallaStockDisplay("39", stockSimulado["39"]!!, colorGrisTexto)
                        TallaStockDisplay("40", stockSimulado["40"]!!, colorGrisTexto)
                    }

                    // Columna 2
                    Column(modifier = Modifier.weight(1f)) {
                        TallaStockDisplay("37", stockSimulado["37"]!!, colorGrisTexto)
                        TallaStockDisplay("38", stockSimulado["38"]!!, colorGrisTexto)
                        TallaStockDisplay("41", stockSimulado["41"]!!, colorGrisTexto)
                        TallaStockDisplay("42", stockSimulado["42"]!!, colorGrisTexto)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // CUADRO DE ESTADO (Consulta de DB)
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(60.dp) // Altura y ancho para contener el texto de estado
                    .background(colorVerdeClaro, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ESTADO", // Texto fijo (simulando que el valor viene de la DB)
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}


// =================================================================
// 2. PREVIEW
// =================================================================

@Preview(showBackground = true)
@Composable
fun ProduccionScreenPreview() {
    Surface(color = Color(0xFFEFEFEF)) {
        ProduccionScreen()
    }
}