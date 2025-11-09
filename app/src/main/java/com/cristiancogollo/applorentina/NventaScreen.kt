package me.oscarsanchez.myapplication

// ... (Imports requeridos: Icons, Material3, ViewModel, etc.)
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cristiancogollo.applorentina.Cliente
import com.cristiancogollo.applorentina.InputFieldWithIcon
import com.cristiancogollo.applorentina.NventaViewModel // Importa el ViewModel

// 🎨 Colores personalizados (asumidos del archivo original)
val ColorVerdeOscuro = Color(0xFFB5CC00)
val ColorVerdeClaroBoton = Color(0xFFC2D500)
val ColorGrisTexto = Color(0xFF5C5C5C)
val ColorFondoCard = Color(0xFFF8F8F8)

// Componentes Auxiliares (InputFieldWithIcon, ActionButton, ClienteSeleccionadoCard, etc.)
// ... (Se mantienen los componentes de la respuesta anterior para brevedad) ...

// =================================================================
// PANTALLA PRINCIPAL (Composable usado en la ruta de diálogo)
// =================================================================

@Composable
fun NventaDialogScreen(
    onSaveSuccess: () -> Unit,
    onAgregarClienteClick: () -> Unit,
    viewModel: NventaViewModel = viewModel() // Inyección
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatter = remember { java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()) }

    // El Box simula el contenedor del diálogo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Nueva Venta", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ColorGrisTexto)
            Spacer(modifier = Modifier.height(8.dp))

            // --- 1. SELECCIÓN DE CLIENTE ---
            Text("Cliente Asociado", fontWeight = FontWeight.SemiBold, color = ColorGrisTexto.copy(alpha = 0.8f), modifier = Modifier.align(Alignment.Start))

            // Campo de búsqueda
            InputFieldWithIcon(
                value = uiState.clienteBuscado,
                onValueChange = viewModel::onClienteBuscadoChange,
                placeholder = "Cédula o Nombre del Cliente",
                icon = Icons.Outlined.Person,
                trailingIcon = {
                    IconButton(onClick = { viewModel.buscarClientePorNombre(uiState.clienteBuscado) }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = ColorVerdeOscuro)
                    }
                }
            )

            // Resultado/Mensaje de cliente
            if (uiState.clienteSeleccionado != null) {
                ClienteSeleccionadoCard(cliente = uiState.clienteSeleccionado!!)
            } else if (uiState.mensajeClienteNoEncontrado != null) {
                MensajeClienteNoEncontrado(
                    mensaje = uiState.mensajeClienteNoEncontrado!!,
                    onAgregarClienteClick = onAgregarClienteClick // Usa el componente solicitado
                )
            }

            // --- 2. DETALLES DE VENTA (Campos solicitados) ---
            Text("Detalles de la Venta", fontWeight = FontWeight.SemiBold, color = ColorGrisTexto.copy(alpha = 0.8f), modifier = Modifier.align(Alignment.Start))

            // Precio de la Venta
            InputFieldWithIcon(
                value = uiState.precio,
                onValueChange = viewModel::onPrecioChange,
                placeholder = "Precio Total",
                icon = Icons.Default.AttachMoney,
            )

            // Fecha de la Venta
            InputFieldWithIcon(
                value = formatter.format(uiState.fechaVenta),
                onValueChange = {},
                placeholder = "Fecha de Venta",
                icon = Icons.Default.DateRange,
                readOnly = true,
                trailingIcon = { /* Icono para abrir DatePicker */ }
            )

            // Descripción (Opcional)
            InputFieldWithIcon(
                value = uiState.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                placeholder = "Descripción (Opcional)",
                icon = Icons.Default.Description
            )


            // --- 3. TIPO DE VENTA Y VENTA ESPECIAL ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(text = "Venta al Detal", isSelected = uiState.esDetal, onClick = { viewModel.toggleTipoVenta(true) }, modifier = Modifier.weight(1f))
                ActionButton(text = "Venta al Por Mayor", isSelected = !uiState.esDetal, onClick = { viewModel.toggleTipoVenta(false) }, modifier = Modifier.weight(1f))
            }

            // Selector de Venta Especial (Solo para Por Mayor)
            if (!uiState.esDetal) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Venta Especial (Deshabilita productos)", modifier = Modifier.weight(1f), color = ColorGrisTexto.copy(alpha = 0.8f))
                    Switch(checked = uiState.esVentaEspecial, onCheckedChange = viewModel::toggleVentaEspecial)
                }
            }

            // --- 4. AGREGAR PRODUCTO ---
            Button(
                onClick = { /* Lógica de navegación a productos */ },
                // Deshabilitado si: 1) No hay cliente seleccionado O 2) Es una Venta Especial
                enabled = uiState.clienteSeleccionado != null && !uiState.esVentaEspecial,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeClaroBoton)
            ) {
                Text("AGREGAR PRODUCTO (WIP)") // Sin funcionalidad aún, como se solicitó
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de Guardar
            Button(
                onClick = { viewModel.guardarVenta(onSaveSuccess) },
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

@Composable
fun ActionButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = if (isSelected) ColorVerdeOscuro else Color.White
    val contentColor = if (isSelected) Color.White else ColorGrisTexto.copy(alpha = 0.8f)
    val borderColor = if (isSelected) ColorVerdeOscuro else ColorVerdeOscuro.copy(alpha = 0.5f)

    // 🟢 ESTA ES LA IMPLEMENTACIÓN COMPLETA QUE FALTA SI TENÍAS UN TODO()
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
fun ClienteSeleccionadoCard(cliente: com.cristiancogollo.applorentina.Cliente) {
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

// 3. MENSAJE DE CLIENTE NO ENCONTRADO (Implementación simple)
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