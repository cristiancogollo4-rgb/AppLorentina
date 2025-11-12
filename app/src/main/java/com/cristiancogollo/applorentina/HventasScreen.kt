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
// 💡 CLASES DE DATOS (Necesarias para que el Dialog funcione)
// =================================================================
data class ProductoVendido(
    // ID para vincular al producto original, si es necesario
    val idProducto: String = "",

    // Propiedades relevantes para el recibo y la descripción
    val referencia: String = "",
    val talla: String = "",
    val color: String = "",

    // Cantidad y precio final
    val cantidad: Int = 0,
    val precioUnidadVendido: Double = 0.0 // Precio al que se vendió realmente
)
// =================================================================
@Composable
fun HventasScreen(
    onBackClick: () -> Unit = {},
    onNewVentaClick: () -> Unit,
    // ❌ ELIMINADO: onVentaClick (Ya no se necesita para la navegación)
    vm: VentasViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val money = remember { NumberFormat.getCurrencyInstance(Locale("es","CO")) }

    // 💡 AÑADIDO: Estado para controlar qué venta se muestra en el diálogo
    var selectedVenta by remember { mutableStateOf<Venta?>(null) }

    // ... (El resto del código de Column, Card, Encabezado, Buscador y Filtros sin cambios) ...

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
                    // Encabezado (sin cambios)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ColorVerdeClaro, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
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

                    Text("HISTORIAL DE VENTAS", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF5C5C5C))

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buscador (sin cambios)
                    OutlinedTextField(
                        value = ui.query,
                        onValueChange = vm::setQuery,
                        placeholder = { Text("BUSCAR VENTA....", color = Color.Gray.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = ColorVerdeClaro) },
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

                    // Filtros (sin cambios)
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

                    // Lista (adaptada al nuevo estado)
                    when {
                        ui.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ColorVerdeClaro)
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
                                    idVenta = v.idVenta,
                                    numVentaDisplay = v.idVenta.takeLast(5).padStart(5, '0'),
                                    nombreCliente = v.cliente?.nombreApellido ?: "Sin cliente",
                                    monto = runCatching { money.format(v.precioTotal) }.getOrDefault("$${v.precioTotal}"),
                                    fecha = runCatching { sdf.format(v.fechaVenta) }.getOrDefault(""),
                                    cc = (v.cliente?.cedula ?: 0L).toString(),
                                    colorVerdeClaro = ColorVerdeClaro,
                                    // 💡 CAMBIO: Actualiza el estado local en lugar de llamar a onVentaClick
                                    onClick = { selectedVenta = v }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }

                // FAB (sin cambios)
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar venta", modifier = Modifier.size(60.dp))
                        Text("AGREGAR VENTA", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.White, lineHeight = 10.sp)
                    }
                }
            }
        }
    }

    // 💡 AÑADIDO: Muestra el diálogo de detalles si hay una venta seleccionada
    selectedVenta?.let { venta ->
        VentaDetailsDialog(
            venta = venta,
            onDismiss = { selectedVenta = null } // Cierra el diálogo y resetea el estado
        )
    }
}

// ... (FilterButtonVentas - sin cambios) ...

// ... (VentaCardUI - sin cambios) ...

@Composable
private fun VentaCardUI(
    idVenta: String, // ID completo de la venta
    numVentaDisplay: String, // Número para mostrar en la UI
    nombreCliente: String,
    monto: String,
    fecha: String,
    cc: String,
    colorVerdeClaro: Color,
    onClick: () -> Unit // 💡 NUEVO: Handler para el clic
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
                    "#${numVentaDisplay}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = colorVerdeClaro,
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(nombreCliente, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = colorGrisTexto)
                Text("CC: $cc", fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(monto, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colorGrisTexto)
                if (fecha.isNotBlank()) Text(fecha, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

// =================================================================
// 💡 COMPOSABLES DEL DIÁLOGO DE DETALLES (AÑADIDOS)
// =================================================================

@Composable
fun VentaDetailsDialog(
    venta: Venta,
    onDismiss: () -> Unit
) {
    val money = remember { NumberFormat.getCurrencyInstance(Locale("es","CO")) }
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault()) }

    val montoFormat = runCatching { money.format(venta.precioTotal) }.getOrDefault("$${venta.precioTotal}")
    val dateFormat = runCatching { sdf.format(venta.fechaVenta) }.getOrDefault("")

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
                // Título y botón de cerrar
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
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // Información General
                DetailRow("Cliente:", venta.cliente?.nombreApellido ?: "—", ColorGrisTexto, FontWeight.SemiBold)
                DetailRow("Cédula:", venta.cliente?.cedula?.toString() ?: "—", Color.Gray, FontWeight.Normal)
                DetailRow("Fecha:", dateFormat, Color.Gray, FontWeight.Normal)
                DetailRow("Tipo:", if (venta.esDetal) "Detal" else "Mayorista", Color.Black, FontWeight.SemiBold)
                DetailRow("Total:", montoFormat, ColorVerdeOscuro, FontWeight.ExtraBold, 20.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Detalles de Productos (si no es venta especial)
                if (!venta.esVentaEspecial && venta.productos.isNotEmpty()) {
                    Text("PRODUCTOS VENDIDOS:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorGrisTexto)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Realizamos un casting seguro (as? List<ProductoVendido>) para garantizar el tipo
                    val productosVendidos = venta.productos.filterIsInstance<ProductoVendido>()

                    Column(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).background(Color(0xFFF7F7F7), RoundedCornerShape(8.dp)).padding(8.dp)) {

                        // Iteramos sobre la lista ya tipada
                        productosVendidos.forEach { p ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // ✅ Ahora Kotlin sabe que 'p' tiene 'cantidad', 'referencia', 'talla'
                                Text("${p.cantidad} x ${p.referencia} (T${p.talla})", fontSize = 13.sp, color = Color.Black)

                                // ✅ La referencia 'precioUnidadVendido' funciona
                                val subtotal = runCatching { money.format(p.cantidad * p.precioUnidadVendido) }.getOrDefault("")

                                Text(subtotal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ColorGrisTexto)
                            }
                        }
                    }
                } else if (venta.esVentaEspecial) {
                    Text("Tipo: Venta Especial", fontWeight = FontWeight.SemiBold, color = Color.Red.copy(alpha = 0.8f))
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
private fun DetailRow(label: String, value: String, valueColor: Color, valueWeight: FontWeight, valueSize: androidx.compose.ui.unit.TextUnit = 16.sp) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Normal, color = Color.Gray)
        Text(value, fontWeight = valueWeight, fontSize = valueSize, color = valueColor)
    }
}
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

@Preview(showBackground = true)
@Composable
fun HventasScreenPreview() {
    HventasScreen(
        onNewVentaClick = { }
    )
}