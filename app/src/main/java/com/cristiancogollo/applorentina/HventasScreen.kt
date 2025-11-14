package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

// =================================================================
// 💡 CLASES DE DATOS (necesarias para el diálogo)
// =================================================================
data class ProductoVendido(
    val idProducto: String = "",
    val referencia: String = "",
    val talla: String = "",
    val color: String = "",
    val cantidad: Int = 0,
    val precioUnidadVendido: Double = 0.0
)

// =================================================================
@Composable
fun HventasScreen(
    onBackClick: () -> Unit = {},
    onNewVentaClick: () -> Unit,
    vm: VentasViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val money = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }

    // Venta seleccionada para mostrar en el diálogo
    var selectedVenta by remember { mutableStateOf<Venta?>(null) }

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
                    // ================= ENCABEZADO =================
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
                                    Icons.Filled.ArrowBack,
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

                    Text(
                        "HISTORIAL DE VENTAS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF5C5C5C)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ================= BUSCADOR =================
                    OutlinedTextField(
                        value = ui.query,
                        onValueChange = vm::setQuery,
                        placeholder = {
                            Text(
                                "BUSCAR VENTA....",
                                color = Color.Gray.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search,
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
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ================= FILTROS =================
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

                    // ================= LISTA DE VENTAS =================
                    when {
                        ui.isLoading -> Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ColorVerdeClaro)
                        }

                        ui.error != null -> Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(ui.error ?: "Error", color = MaterialTheme.colorScheme.error)
                        }

                        ui.filtered.isEmpty() -> Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                "Sin resultados para \"${ui.query}\"",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }

                        else -> {
                            // 1️⃣ Orden histórico (más antiguas primero) para numeración
                            val ventasHistoricas = ui.filtered.sortedBy { it.fechaVenta }

                            // 2️⃣ Orden visual (más nuevas arriba)
                            val ventasOrdenadas = ventasHistoricas.asReversed()

                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 24.dp)
                            ) {
                                items(ventasOrdenadas) { v ->

                                    // 3️⃣ Buscamos la posición histórica de esa venta
                                    val indexHistorico =
                                        ventasHistoricas.indexOfFirst { it.idVenta == v.idVenta }

                                    // 4️⃣ Número con 3 dígitos: #001, #002, #003...
                                    val numVenta = String.format("%03d", indexHistorico + 1)

                                    VentaCardUI(
                                        idVenta = v.idVenta,
                                        numVentaDisplay = numVenta,
                                        nombreCliente = v.cliente?.nombreApellido ?: "Sin cliente",
                                        monto = runCatching { money.format(v.precioTotal) }
                                            .getOrDefault("$${v.precioTotal}"),
                                        fecha = runCatching { sdf.format(v.fechaVenta) }
                                            .getOrDefault(""),
                                        cc = (v.cliente?.cedula ?: 0L).toString(),
                                        colorVerdeClaro = ColorVerdeClaro,
                                        onClick = { selectedVenta = v }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }

                // ================= FAB AGREGAR VENTA =================
                FloatingActionButton(
                    onClick = onNewVentaClick,
                    containerColor = ColorVerdeClaro,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 24.dp)
                        .size(90.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
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

    // ================= DIÁLOGO DE DETALLES =================
    selectedVenta?.let { venta ->
        VentaDetailsDialog(
            venta = venta,
            onDismiss = { selectedVenta = null }
        )
    }
}

// =================================================================
// CARD DE CADA VENTA
// =================================================================
@Composable
private fun VentaCardUI(
    idVenta: String,
    numVentaDisplay: String,
    nombreCliente: String,
    monto: String,
    fecha: String,
    cc: String,
    colorVerdeClaro: Color,
    onClick: () -> Unit
) {
    val colorGrisTexto = Color(0xFF5C5C5C)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
            .border(2.dp, colorVerdeClaro, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(40.dp)
                    .background(colorVerdeClaro.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$numVentaDisplay",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = colorVerdeClaro
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    nombreCliente,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = colorGrisTexto
                )
                Text("CC: $cc", fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(monto, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colorGrisTexto)
                if (fecha.isNotBlank()) {
                    Text(fecha, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

// =================================================================
// DIÁLOGO DETALLES DE VENTA
// =================================================================
@Composable
fun VentaDetailsDialog(
    venta: Venta,
    onDismiss: () -> Unit
) {
    val money = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault()) }

    val montoFormat =
        runCatching { money.format(venta.precioTotal) }.getOrDefault("$${venta.precioTotal}")
    val dateFormat =
        runCatching { sdf.format(venta.fechaVenta) }.getOrDefault("")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.95f),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DETALLES VENTA #${venta.idVenta.takeLast(5).padStart(5, '0')}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ColorVerdeOscuro
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(
                    label = "Cliente:",
                    value = venta.cliente?.nombreApellido ?: "—",
                    valueColor = ColorGrisTexto,
                    valueWeight = FontWeight.SemiBold
                )
                DetailRow(
                    label = "Cédula:",
                    value = venta.cliente?.cedula?.toString() ?: "—",
                    valueColor = Color.Gray,
                    valueWeight = FontWeight.Normal
                )
                DetailRow(
                    label = "Fecha:",
                    value = dateFormat,
                    valueColor = Color.Gray,
                    valueWeight = FontWeight.Normal
                )
                DetailRow(
                    label = "Tipo:",
                    value = if (venta.esDetal) "Detal" else "Mayorista",
                    valueColor = Color.Black,
                    valueWeight = FontWeight.SemiBold
                )
                DetailRow(
                    label = "Total:",
                    value = montoFormat,
                    valueColor = ColorVerdeOscuro,
                    valueWeight = FontWeight.ExtraBold,
                    valueSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!venta.esVentaEspecial && venta.productos.isNotEmpty()) {
                    Text(
                        "PRODUCTOS VENDIDOS:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ColorGrisTexto
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val productosVendidos =
                        venta.productos.filterIsInstance<ProductoVendido>()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .background(Color(0xFFF7F7F7), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        productosVendidos.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${p.cantidad} x ${p.referencia} (T${p.talla})",
                                    fontSize = 13.sp,
                                    color = Color.Black
                                )

                                val subtotal = runCatching {
                                    money.format(p.cantidad * p.precioUnidadVendido)
                                }.getOrDefault("")

                                Text(
                                    subtotal,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColorGrisTexto
                                )
                            }
                        }
                    }
                } else if (venta.esVentaEspecial) {
                    Text(
                        "Tipo: Venta Especial",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Red.copy(alpha = 0.8f)
                    )
                    Text("Descripción: ${venta.descripcion}", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeOscuro)
                ) {
                    Text("Aceptar")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color,
    valueWeight: FontWeight,
    valueSize: androidx.compose.ui.unit.TextUnit = 16.sp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Normal, color = Color.Gray)
        Text(value, fontWeight = valueWeight, fontSize = valueSize, color = valueColor)
    }
}

// =================================================================
// BOTÓN DE FILTRO
// =================================================================
@Composable
private fun FilterButtonVentas(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = modifier.height(35.dp)
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun HventasScreenPreview() {
    HventasScreen(
        onNewVentaClick = { }
    )
}
