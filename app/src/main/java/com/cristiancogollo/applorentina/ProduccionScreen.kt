package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


// =================================================================
// 1. PANTALLA PRODUCCIÓN
// =================================================================

@Composable
fun ProduccionScreen(
    onBackClick: () -> Unit = {},
    // 🟢 Inyección del ViewModel de solo lectura para producción
    viewModel: ProduccionVendedorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondoScreen)
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
                    text = "REF. EN PRODUCCION",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorGrisTexto
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Barra de búsqueda (Buscar Referencia)
                OutlinedTextField(
                    value = uiState.searchQuery, // 🟢 Usar el estado del VM
                    onValueChange = viewModel::onSearchQueryChange, // 🟢 Usar la función del VM
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

                // 4. Filtros de Producción (REFE, COLOR, TALLA)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProduccionFilterButton(
                        text = "REFE.",
                        isSelected = uiState.activeFilter == "REFE",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onFilterTypeChange("REFE") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ProduccionFilterButton(
                        text = "COLOR",
                        isSelected = uiState.activeFilter == "COLOR",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onFilterTypeChange("COLOR") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ProduccionFilterButton(
                        text = "TALLA",
                        isSelected = uiState.activeFilter == "TALLA",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onFilterTypeChange("TALLA") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Lista de Productos en Producción
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = ColorVerdeOscuro)
                } else if (uiState.errorMessage != null) {
                    Text(uiState.errorMessage!!, color = Color.Red, modifier = Modifier.padding(16.dp))
                } else if (uiState.filteredProductos.isEmpty() && uiState.searchQuery.isNotBlank()) {
                    Text("No se encontraron resultados para '${uiState.searchQuery}' bajo el filtro ${uiState.activeFilter}.",
                        color = ColorGrisTexto, modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 24.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.filteredProductos) { producto -> // 🟢 Usar la lista filtrada del VM
                            ProduccionCard(
                                producto = producto, // 🟢 Pasar el objeto Producto completo
                                colorVerdeClaro = ColorVerdeClaro,
                                colorGrisTexto = ColorGrisTexto
                            )
                        }
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
fun ProduccionFilterButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
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


@Composable
fun TallaStockDisplay(talla: String, stock: Int, colorGrisTexto: Color) {
    // 🟢 MODIFICADO para usar Int de stock
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Talla $talla : ",
            fontSize = 14.sp,
            color = colorGrisTexto,
            fontWeight = FontWeight.Normal,
        )
        Text(
            text = stock.toString(),
            fontSize = 14.sp,
            // Resalta en rojo si no hay stock
            color = if (stock > 0) colorGrisTexto else Color.Red,
            fontWeight = FontWeight.Bold,
        )
    }
}


@Composable
fun ProduccionCard(
    producto: Producto, // 🟢 Recibe el objeto Producto
    colorVerdeClaro: Color,
    colorGrisTexto: Color
) {
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
                    // Muestra Referencia y Modelo
                    "${producto.referencia} ${producto.nombreModelo}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = colorGrisTexto
                )
                Text(
                    "Color: ${producto.color}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Distribución de Tallas (Usando el stock real del producto)
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Columna 1 (Tallas 35-38)
                    Column(modifier = Modifier.weight(1f)) {
                        (35..38).forEach { talla ->
                            val stock = producto.stockPorTalla[talla.toString()] ?: 0
                            TallaStockDisplay(talla.toString(), stock, colorGrisTexto)
                        }
                    }

                    // Columna 2 (Tallas 39-42)
                    Column(modifier = Modifier.weight(1f)) {
                        (39..42).forEach { talla ->
                            val stock = producto.stockPorTalla[talla.toString()] ?: 0
                            TallaStockDisplay(talla.toString(), stock, colorGrisTexto)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // CUADRO DE ESTADO (Muestra el estado real)
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(60.dp)
                    .background(colorVerdeClaro, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    // 🟢 Muestra el estado real
                    text = producto.estado.uppercase(),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 12.sp
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
    Surface(color = ColorFondoScreen) {
        // En el Preview se debe usar un MockViewModel
        // ProduccionScreen()
    }
}