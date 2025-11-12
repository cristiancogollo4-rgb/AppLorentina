package me.oscarsanchez.myapplication

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cristiancogollo.applorentina.* // Importa modelos (Cliente, Producto, etc.) y NventaViewModel
import java.text.NumberFormat
import java.util.Locale

// 🎨 Colores personalizados (asumidos)
val ColorVerdeOscuro = Color(0xFFB5CC00)
val ColorVerdeClaroBoton = Color(0xFFC2D500)
val ColorGrisTexto = Color(0xFF5C5C5C)
val ColorFondoCard = Color(0xFFF8F8F8)

// =================================================================
// 1. PANTALLA PRINCIPAL (Composable usado en la ruta de diálogo)
// =================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NventaDialogScreen(
    onSaveSuccess: () -> Unit,
    onAgregarClienteClick: () -> Unit,
    viewModel: NventaViewModel = viewModel() // Inyección
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatter = remember { java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()) }
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()).apply { maximumFractionDigits = 0 } }

    // Usamos un LazyColumn dentro del Box para permitir el desplazamiento si el contenido es largo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Text("Nueva Venta", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ColorGrisTexto)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- 1. SELECCIÓN DE CLIENTE ---
            item {
                Text("Cliente Asociado", fontWeight = FontWeight.SemiBold, color = ColorGrisTexto.copy(alpha = 0.8f), modifier = Modifier.fillMaxWidth())

                // Campo de búsqueda de cliente
                Box(modifier = Modifier.fillMaxWidth()) {
                    InputFieldWithIcon(
                        value = uiState.clienteBuscado,
                        onValueChange = viewModel::onClienteBuscadoChange,
                        placeholder = "Cédula o Nombre del Cliente",
                        icon = Icons.Outlined.Person,
                        readOnly = uiState.clienteSeleccionado != null,
                        trailingIcon = {
                            Row {
                                if (uiState.clienteSeleccionado != null || uiState.clienteBuscado.isNotBlank()) {
                                    IconButton(onClick = { viewModel.seleccionarCliente(null) }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = ColorGrisTexto)
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.buscarClientePorNombre(uiState.clienteBuscado) },
                                    enabled = uiState.clienteSeleccionado == null
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = ColorVerdeOscuro)
                                }
                            }
                        }
                    )

                    // DropdownMenu para sugerencias de autocompletado de cliente
                    DropdownMenu(
                        expanded = uiState.isDropdownExpanded,
                        onDismissRequest = { viewModel.dismissDropdown() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isClientesLoading) {
                            DropdownMenuItem(text = { Text("Cargando clientes...") }, onClick = {})
                        } else if (uiState.clientesFiltrados.isEmpty() && uiState.clienteBuscado.isNotBlank()) {
                            DropdownMenuItem(text = { Text("No se encontraron coincidencias") }, onClick = {})
                        } else {
                            uiState.clientesFiltrados.forEach { cliente ->
                                DropdownMenuItem(
                                    text = { Text("${cliente.nombreApellido} (${cliente.cedula})", color = ColorGrisTexto) },
                                    onClick = { viewModel.seleccionarCliente(cliente) }
                                )
                            }
                        }
                    }
                }

                // Resultado/Mensaje de cliente
                if (uiState.clienteSeleccionado != null) {
                    ClienteSeleccionadoCard(cliente = uiState.clienteSeleccionado!!)
                } else if (uiState.mensajeClienteNoEncontrado != null) {
                    MensajeClienteNoEncontrado(
                        mensaje = uiState.mensajeClienteNoEncontrado!!,
                        onAgregarClienteClick = onAgregarClienteClick
                    )
                }
            }

            // --- 2. DETALLES DE VENTA ---
            item {
                Text("Detalles de la Venta", fontWeight = FontWeight.SemiBold, color = ColorGrisTexto.copy(alpha = 0.8f), modifier = Modifier.fillMaxWidth())

                InputFieldWithIcon(
                    value = uiState.precio,
                    onValueChange = viewModel::onPrecioChange,
                    placeholder = "Precio Total",
                    icon = Icons.Default.AttachMoney,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                InputFieldWithIcon(
                    value = formatter.format(uiState.fechaVenta),
                    onValueChange = {},
                    placeholder = "Fecha de Venta",
                    icon = Icons.Default.DateRange,
                    readOnly = true,
                    // Se podría implementar un DatePickerDialog aquí en el trailingIcon
                )

                InputFieldWithIcon(
                    value = uiState.descripcion,
                    onValueChange = viewModel::onDescripcionChange,
                    placeholder = "Descripción (Opcional)",
                    icon = Icons.Default.Description
                )

                // --- 3. TIPO DE VENTA Y VENTA ESPECIAL ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton(text = "Venta Detal", isSelected = uiState.esDetal, onClick = { viewModel.toggleTipoVenta(true) }, modifier = Modifier.weight(1f))
                    ActionButton(text = "Venta Mayor", isSelected = !uiState.esDetal, onClick = { viewModel.toggleTipoVenta(false) }, modifier = Modifier.weight(1f))
                }

                if (!uiState.esDetal) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Venta Especial (Sin productos)", modifier = Modifier.weight(1f), color = ColorGrisTexto.copy(alpha = 0.8f))
                        Switch(checked = uiState.esVentaEspecial, onCheckedChange = viewModel::toggleVentaEspecial, colors = SwitchDefaults.colors(checkedThumbColor = ColorVerdeOscuro, checkedTrackColor = ColorVerdeClaroBoton))
                    }
                }
            }

            // --- 4. SELECCIÓN DE PRODUCTO Y TALLA ---
            if (!uiState.esVentaEspecial) {
                item {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Añadir Producto", fontWeight = FontWeight.SemiBold, color = ColorGrisTexto.copy(alpha = 0.8f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // 4a. Selector de Producto (Búsqueda + Dropdown)
                            Box(modifier = Modifier.weight(3f)) {
                                InputFieldWithIcon(
                                    value = uiState.productoSeleccionado?.let { "${it.referencia} ${it.nombreModelo}" } ?: uiState.productoBuscado,
                                    onValueChange = { viewModel.onProductoBuscadoChange(it) },
                                    placeholder = "Buscar Producto/Referencia",
                                    icon = Icons.Default.Style,
                                    readOnly = uiState.productoSeleccionado != null,
                                    trailingIcon = {
                                        if (uiState.productoSeleccionado != null) {
                                            IconButton(onClick = { viewModel.seleccionarProducto(null) }) {
                                                Icon(Icons.Default.Clear, contentDescription = "Limpiar Producto", tint = ColorGrisTexto)
                                            }
                                        } else {
                                            IconButton(onClick = { viewModel.toggleProductoDropdown(true) }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Mostrar productos", tint = ColorVerdeOscuro)
                                            }
                                        }
                                    }
                                )

                                DropdownMenu(
                                    expanded = uiState.isProductoDropdownExpanded && uiState.productoSeleccionado == null,
                                    onDismissRequest = { viewModel.toggleProductoDropdown(false) },
                                    modifier = Modifier.width(300.dp)
                                ) {
                                    uiState.productosDisponibles.forEach { producto ->
                                        DropdownMenuItem(
                                            text = { Text("${producto.referencia} ${producto.nombreModelo} (${producto.color})", color = ColorGrisTexto) },
                                            onClick = { viewModel.seleccionarProducto(producto) }
                                        )
                                    }
                                }
                            }

                            // 4b. Selector de Talla
                            Box(modifier = Modifier.weight(1f).height(50.dp)) {
                                val tallaText = uiState.tallaSeleccionada ?: "Talla"
                                Button(
                                    onClick = { if (uiState.productoSeleccionado != null) viewModel.toggleTallaDropdown(true) },
                                    enabled = uiState.productoSeleccionado != null,
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeClaroBoton),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text(tallaText, fontSize = 14.sp)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar talla")
                                }

                                DropdownMenu(
                                    expanded = uiState.isTallaDropdownExpanded,
                                    onDismissRequest = { viewModel.toggleTallaDropdown(false) }
                                ) {
                                    uiState.productoSeleccionado?.stockPorTalla?.filter { it.value > 0 }?.toSortedMap()?.forEach { (talla, stock) ->
                                        DropdownMenuItem(
                                            text = { Text("Talla $talla (Stock: $stock)", color = ColorGrisTexto) },
                                            onClick = { viewModel.seleccionarTalla(talla); viewModel.toggleTallaDropdown(false) }
                                        )
                                    }
                                }
                            }
                        }

                        // Botón de Añadir Producto a la Venta
                        Button(
                            onClick = { viewModel.agregarProductoAVenta() },
                            enabled = uiState.clienteSeleccionado != null &&
                                    uiState.productoSeleccionado != null &&
                                    uiState.tallaSeleccionada != null,
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeClaroBoton.copy(alpha = 0.8f))
                        ) {
                            Text("AÑADIR A LA VENTA", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }


            // --- 5. LISTA DE PRODUCTOS AÑADIDOS ---
            if (uiState.productosEnVenta.isNotEmpty()) {
                item {
                    Text("Items en Carrito", fontWeight = FontWeight.SemiBold, color = ColorGrisTexto.copy(alpha = 0.8f), modifier = Modifier.fillMaxWidth())
                }

                // Usamos items() para la lista dentro del LazyColumn principal
                items(uiState.productosEnVenta) { item ->
                    ProductoVentaItemCard(item = item, currencyFormatter = currencyFormatter)
                }
            }

            // Mensaje de Error
            item {
                uiState.mensajeError?.let {
                    Text(it, color = Color.Red, fontSize = 14.sp)
                }
            }

            // Espaciador para empujar el botón al fondo
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- 6. BOTÓN DE GUARDAR ---
            item {
                Button(

                    onClick = { viewModel.guardarVenta(onSaveSuccess)
                        Log.d("VentaDebug", "Clic en Botón Guardar - Intentando llamar a ViewModel")},
                    enabled = uiState.clienteSeleccionado != null && uiState.precio.toDoubleOrNull() != null && !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeOscuro)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("GUARDAR VENTA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
fun ActionButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = if (isSelected) ColorVerdeOscuro else Color.White
    val contentColor = if (isSelected) Color.White else ColorGrisTexto.copy(alpha = 0.8f)
    val borderColor = if (isSelected) ColorVerdeOscuro else ColorVerdeOscuro.copy(alpha = 0.5f)

    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ClienteSeleccionadoCard(cliente: Cliente) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ColorFondoCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Cliente Seleccionado:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorGrisTexto)
            Spacer(Modifier.height(4.dp))
            Text("Nombre: ${cliente.nombreApellido}", fontSize = 14.sp)
            Text("Cédula: ${cliente.cedula}", fontSize = 14.sp)
            Text("Tipo: ${if (cliente.tipoCliente) "Detal" else "Por Mayor"}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorVerdeOscuro
            )
        }
    }
}

@Composable
fun MensajeClienteNoEncontrado(mensaje: String, onAgregarClienteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            mensaje,
            color = Color.Red.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f).padding(end = 8.dp),
            fontSize = 14.sp
        )
        Button(
            onClick = onAgregarClienteClick,
            colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeClaroBoton)
        ) {
            Text("Agregar", fontSize = 14.sp)
        }
    }
}

@Composable
fun ProductoVentaItemCard(item: VentaProductoItem, currencyFormatter: NumberFormat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = ColorFondoCard),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${item.referencia} - Talla ${item.talla}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorGrisTexto)
                Text(item.nombreModelo, fontSize = 12.sp, color = ColorGrisTexto.copy(alpha = 0.7f))
            }
            Text(
                currencyFormatter.format(item.precioUnitario),
                fontWeight = FontWeight.SemiBold,
                color = ColorVerdeOscuro
            )
            Spacer(modifier = Modifier.width(10.dp))
            // Puedes añadir un botón de eliminar aquí si lo deseas.
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NventaDialogScreenPreview() {
    Surface(color = Color.White) {
        // NOTA: Para el Preview, necesitarías un Mock NventaViewModel con datos de ejemplo.
        // NventaDialogScreen(onSaveSuccess = {}, onAgregarClienteClick = {})
        Text("Preview NventaDialogScreen")
    }
}