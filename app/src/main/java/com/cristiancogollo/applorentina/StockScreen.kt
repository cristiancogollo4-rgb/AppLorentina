package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter // Importación requerida para cargar imágenes desde URL

// =================================================================
// 🎨 DEFINICIÓN DE COLORES (Definidas globalmente para evitar conflictos)
// =================================================================
val ColorVerdeClaro = Color(0xFFC2D500)

val ColorFondoScreen = Color(0xFFEFEFEF)


// =================================================================
// 1. PANTALLA PRINCIPAL DE STOCK
// =================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
    onBackClick: () -> Unit = {},
    viewModel: StockViewModel = viewModel() // 🟢 Inyección del ViewModel
) {
    // 🟢 Obtener el estado de la UI del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondoScreen)
            .padding(top = 16.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Contenedor principal para el contenido blanco
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
                // 1. Encabezado verde (Logo Lorentina) - Se mantiene el diseño original
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            ColorVerdeClaro,
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
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White,
                                modifier = Modifier.size(35.dp)
                            )
                        }
                    }
                    // Nota: Asumimos que R.drawable.lorenita existe o se ignora
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

                // 2. Título de la Pantalla
                Text(
                    text = "INVENTARIO",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorGrisTexto
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Barra de búsqueda (Buscar Referencia)
                OutlinedTextField(
                    // 🟢 Usar el estado de búsqueda del ViewModel
                    value = uiState.searchQuery,
                    // 🟢 Usar la función de cambio del ViewModel
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = {
                        Text(
                            "BUSCAR REFERENCIA....",
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = ColorVerdeClaro
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorVerdeClaro,
                        unfocusedBorderColor = ColorVerdeClaro,
                        cursorColor = ColorVerdeOscuro
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Filtros de Stock
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StockFilterButton(
                        text = "REFE.",
                        isSelected = uiState.activeFilter == "REFE",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onFilterTypeChange("REFE") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StockFilterButton(
                        text = "COLOR",
                        isSelected = uiState.activeFilter == "COLOR",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onFilterTypeChange("COLOR") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StockFilterButton(
                        text = "TALLA",
                        isSelected = uiState.activeFilter == "TALLA",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onFilterTypeChange("TALLA") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Lista de Inventario (LazyColumn)
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp),
                        color = ColorVerdeOscuro
                    )
                } else if (uiState.errorMessage != null) {
                    Text(
                        uiState.errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                } else if (uiState.filteredProductos.isEmpty() && uiState.searchQuery.isNotBlank()) {
                    Text(
                        "No se encontraron resultados para '${uiState.searchQuery}'.",
                        color = ColorGrisTexto, modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 24.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 🟢 Usar la lista filtrada del ViewModel
                        items(uiState.filteredProductos) { producto ->
                            StockCard(producto = producto) // 🟢 Pasar el objeto Producto completo
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// COMPONENTES AUXILIARES
// -----------------------------------------------------------------

@Composable
fun StockFilterButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) ColorVerdeOscuro else Color(0xFFEFF5C9)
    val contentColor = if (isSelected) Color.White else Color(0xFF8AA100)
    val borderColor = if (isSelected) ColorVerdeOscuro else Color(0xFFEFF5C9)
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(10.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(borderColor)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        modifier = modifier
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

// 🟢 FUNCIÓN ACTUALIZADA para usar Int y ColorGrisTexto global
@Composable
fun TallaStockDisplay(talla: String, stock: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Talla (Etiqueta fija) y Stock (Valor que viene de la DB)
        Text(
            // Formato "TALLA : STOCK"
            text = "Talla $talla : ", // Separamos el stock del formato para darle color
            fontSize = 14.sp,
            color = ColorGrisTexto,
            fontWeight = FontWeight.Normal,
        )
        Text(
            text = stock.toString(),
            fontSize = 14.sp,
            // Resalta en rojo si no hay stock
            color = if (stock > 0) ColorGrisTexto else Color.Red,
            fontWeight = FontWeight.Bold,
        )
    }
}


@Composable
fun StockCard(producto: Producto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top // Cambiado a Top para mejor alineación
        ) {
            // Columna de Tallas y Stock
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 🟢 Usar datos del Producto
                Text(
                    producto.referencia + " " + producto.nombreModelo,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = ColorGrisTexto
                )
                Text("Color: ${producto.color}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Detal: $${producto.precioDetal}", fontSize = 14.sp, color = ColorVerdeOscuro)
                Text("Mayor: $${producto.precioMayor}", fontSize = 14.sp, color = ColorVerdeOscuro)
                Spacer(modifier = Modifier.height(10.dp))

                // Distribución de Tallas (Texto de solo lectura)
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Columna 1 (Tallas 35-39)
                    Column(modifier = Modifier.weight(1f)) {
                        (35..39).forEach { talla ->
                            // 🟢 Usar stock real del mapa
                            val stock = producto.stockPorTalla[talla.toString()] ?: 0
                            TallaStockDisplay(talla.toString(), stock)
                        }
                    }

                    // Columna 2 (Tallas 40-42)
                    Column(modifier = Modifier.weight(1f)) {
                        (40..42).forEach { talla ->
                            // 🟢 Usar stock real del mapa
                            val stock = producto.stockPorTalla[talla.toString()] ?: 0
                            TallaStockDisplay(talla.toString(), stock)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Cuadro de Imagen con Coil
            val painter = rememberAsyncImagePainter(
                model = producto.imagenUrl,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder
                error = painterResource(id = R.drawable.ic_launcher_foreground) // Imagen en caso de error
            )

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(ColorVerdeClaro.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = producto.nombreModelo,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StockScreenPreview() {
    Surface(color = ColorFondoScreen) {
        // En el Preview, el ViewModel se inicializa con datos de ejemplo (Mock data)
        StockScreen()
    }
}