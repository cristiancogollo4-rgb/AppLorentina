package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Si tu paquete es el mismo, este import de R es opcional. Déjalo si el IDE lo pide.
import com.cristiancogollo.applorentina.R

// ================================================================
// 0) Enum del filtro
// ================================================================
enum class VentasFilter { CLIENTE, FECHA, CC }

// ================================================================
// 1) Pantalla Historial de Ventas
// ================================================================
@Composable
fun HventasScreen(
    onBackClick: () -> Unit = {},
    onNewVentaClick: () -> Unit
) {
    val colorVerdeClaro = Color(0xFFC2D500)
    val colorVerdeOscuro = Color(0xFFB5CC00)
    val colorGrisTexto = Color(0xFF5C5C5C)

    var searchQuery by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf(VentasFilter.CLIENTE) }

    // Datos de ventas de ejemplo (Triple: Nº, Cliente, Monto)
    val ventas = listOf(
        Triple("001", "JUAN PEREZ", "$180.000"),
        Triple("002", "MARIA GOMEZ", "$120.000"),
        Triple("003", "CARLOS RUIZ", "$250.000"),
        Triple("004", "ANA LOPEZ", "$180.000"),
        Triple("005", "PEDRO RAMIREZ", "$90.000")
    )

    // Orden dinámico según el filtro seleccionado
    val ventasOrdenadas = remember(filtroSeleccionado, ventas) {
        when (filtroSeleccionado) {
            // Orden por nombre (second)
            VentasFilter.CLIENTE -> ventas.sortedBy { it.second.lowercase() }
            // Aquí usamos el primer campo (id como “FECHA” de maqueta). Cuando conectes con datos reales,
            // cambia a la fecha real (Date/String parseado).
            VentasFilter.FECHA -> ventas.sortedBy { it.first }
            // Simulación de C.C.: aquí usarías tu campo de documento; como no está en el Triple,
            // lo dejamos en third (monto) solo para demostrar el cambio de orden.
            VentasFilter.CC -> ventas.sortedBy { it.third }
        }
    }

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
                    // Encabezado verde con botón back y logo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorVerdeClaro, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .padding(vertical = 15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 8.dp)
                        ) {
                            androidx.compose.material3.IconButton(onClick = onBackClick) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Filled.ArrowBack,
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

                    // Título
                    Text(
                        text = "HISTORIAL DE VENTAS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorGrisTexto
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buscador
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("BUSCAR VENTA....", color = Color.Gray.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            androidx.compose.material3.Icon(
                                Icons.Filled.Search,
                                contentDescription = "Buscar",
                                tint = colorVerdeClaro
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

                    // Botones de filtro
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HventasFilterButton(
                            text = "CLIENTE",
                            isSelected = filtroSeleccionado == VentasFilter.CLIENTE,
                            onClick = { filtroSeleccionado = VentasFilter.CLIENTE },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        HventasFilterButton(
                            text = "FECHA",
                            isSelected = filtroSeleccionado == VentasFilter.FECHA,
                            onClick = { filtroSeleccionado = VentasFilter.FECHA },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        HventasFilterButton(
                            text = "C.C",
                            isSelected = filtroSeleccionado == VentasFilter.CC,
                            onClick = { filtroSeleccionado = VentasFilter.CC },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de ventas (usa la lista ORDENADA)
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 24.dp)
                    ) {
                        items(ventasOrdenadas) { (numVenta, nombre, monto) ->
                            VentaCard(
                                numVenta = numVenta,
                                nombreCliente = nombre,
                                monto = monto,
                                colorVerdeClaro = colorVerdeClaro
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp)) // espacio para el FAB
                }

                // FAB Agregar Venta
                FloatingActionButton(
                    onClick = onNewVentaClick,
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
                        androidx.compose.material3.Icon(
                            Icons.Filled.Add,
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

// ================================================================
// 2) Componentes auxiliares
// ================================================================
@Composable
fun HventasFilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) Color(0xFFB5CC00) else Color(0xFFEFF5C9)
    val contentColor = if (isSelected) Color.White else Color(0xFF8AA100)
    val borderColor = if (isSelected) Color(0xFFB5CC00) else Color(0xFFEFF5C9)

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
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
        modifier = modifier.height(35.dp) // altura según mockup
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
            .border(2.dp, colorVerdeClaro, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                numVenta,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colorGrisTexto,
                modifier = Modifier.width(30.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                nombreCliente,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = colorGrisTexto,
                modifier = Modifier.weight(1f)
            )
            Text(
                monto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colorGrisTexto
            )
        }
    }
}

// ================================================================
// 3) Preview
// ================================================================
@Preview(showBackground = true)
@Composable
fun HventasScreenPreview() {
    Surface(color = Color(0xFFEFEFEF)) {
        HventasScreen(
            onBackClick = { },
            onNewVentaClick = { }
        )
    }
}
