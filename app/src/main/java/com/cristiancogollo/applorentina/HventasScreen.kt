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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HventasScreen(
    onBackClick: () -> Unit = {},
    onNewVentaClick: () -> Unit,
    vm: VentasViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()

    val colorVerdeClaro = Color(0xFFC2D500)
    val colorVerdeOscuro = Color(0xFFB5CC00)
    val colorGrisTexto = Color(0xFF5C5C5C)
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val money = remember { NumberFormat.getCurrencyInstance(Locale("es","CO")) }

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
                    // Encabezado
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
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(35.dp))
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

                    Text("HISTORIAL DE VENTAS", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = colorGrisTexto)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buscador
                    OutlinedTextField(
                        value = ui.query,
                        onValueChange = vm::setQuery,
                        placeholder = { Text("BUSCAR VENTA....", color = Color.Gray.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = colorVerdeClaro) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorVerdeClaro,
                            unfocusedBorderColor = colorVerdeClaro,
                            cursorColor = colorVerdeOscuro
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtros
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterButtonVentas(
                            text = "CLIENTE",
                            isSelected = ui.sort == VentasSort.CLIENTE,
                            onClick = { vm.setSort(VentasSort.CLIENTE) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterButtonVentas(
                            text = "FECHA",
                            isSelected = ui.sort == VentasSort.FECHA,
                            onClick = { vm.setSort(VentasSort.FECHA) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterButtonVentas(
                            text = "C.C",
                            isSelected = ui.sort == VentasSort.CC,
                            onClick = { vm.setSort(VentasSort.CC) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista
                    when {
                        ui.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = colorVerdeClaro)
                        }
                        ui.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(ui.error ?: "Error", color = MaterialTheme.colorScheme.error)
                        }
                        ui.filtered.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                            Text("Sin resultados para \"${ui.query}\"", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
                        }
                        else -> LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 24.dp)
                        ) {
                            items(ui.filtered) { v ->
                                VentaCardUI(
                                    numVenta = v.idVenta.ifBlank { "—" },
                                    nombreCliente = v.cliente?.nombreApellido ?: "Sin cliente",
                                    monto = runCatching { money.format(v.precioTotal) }.getOrDefault("$${v.precioTotal}"),
                                    fecha = runCatching { sdf.format(v.fechaVenta) }.getOrDefault(""),
                                    cc = (v.cliente?.cedula ?: 0L).toString(),
                                    colorVerdeClaro = colorVerdeClaro
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }

                // FAB
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar venta", modifier = Modifier.size(60.dp))
                        Text("AGREGAR VENTA", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.White, lineHeight = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterButtonVentas(
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
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        shape = RoundedCornerShape(10.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(borderColor)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = modifier.height(35.dp)
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun VentaCardUI(
    numVenta: String,
    nombreCliente: String,
    monto: String,
    fecha: String,
    cc: String,
    colorVerdeClaro: Color
) {
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
                numVenta.takeLast(3).padStart(3, '0'),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colorGrisTexto,
                modifier = Modifier.width(40.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(nombreCliente, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colorGrisTexto, modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(monto, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colorGrisTexto)
                if (fecha.isNotBlank()) Text(fecha, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HventasScreenPreview() {
    HventasScreen(onNewVentaClick = { })
}
