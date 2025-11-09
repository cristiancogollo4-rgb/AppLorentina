package com.cristiancogollo.applorentina

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import me.oscarsanchez.myapplication.ActionButton

// Definición de colores del proyecto
val ColorVerdeOscuro = Color(0xFFB5CC00)
val ColorVerdeClaroBoton = Color(0xFFC2D500)
val ColorGrisTexto = Color(0xFF5C5C5C)
val ColorFondoCard = Color(0xFFF8F8F8)


// =================================================================
// PANTALLA PRINCIPAL (Sin cambios aquí)
// =================================================================

@Composable
fun AgregarClienteScreen(
    // CORRECTO
    viewModel: AgregarClienteViewModel = viewModel(factory = AgregarClienteViewModelFactory(LocalContext.current))
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()
    val formState = uiState.formState
    val departamentos = viewModel.departamentos
    val municipios = viewModel.municipios

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        AgregarClienteDialogContent(
            formState = formState,
            departamentos = departamentos,
            municipios = municipios,
            onNombreChange = viewModel::updateNombre,
            onCedulaChange = viewModel::updateCedula,
            onTelefonoChange = viewModel::updateTelefono,
            onCorreoChange = viewModel::updateCorreo,
            onDepartamentoChange = viewModel::updateDepartamento,
            onMunicipioChange = viewModel::updateMunicipio,
            isSaving = uiState.isLoading,
            isFormValid = isFormValid,
            onDetalSelect = { viewModel.setTipoCliente(true) },
            onMayorSelect = { viewModel.setTipoCliente(false) },
            onAgregar = viewModel::saveCliente
        )

        uiState.message?.let { msg ->
            Snackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text(msg)
            }
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(3000L)
                viewModel.clearMessage()
            }
        }
    }
}

// =================================================================
// CONTENIDO DEL DIÁLOGO (CON CAMBIOS CLAVE)
// =================================================================

@Composable
fun AgregarClienteDialogContent(
    formState: ClienteFormState,
    departamentos: List<String>,
    municipios: List<String>,
    onNombreChange: (String) -> Unit,
    onCedulaChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onDepartamentoChange: (String) -> Unit,
    onMunicipioChange: (String) -> Unit,
    onDetalSelect: () -> Unit,
    onMayorSelect: () -> Unit,
    onAgregar: () -> Unit,
    isSaving: Boolean,
    isFormValid: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AGREGAR CLIENTE",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorGrisTexto.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            InputFieldWithIcon(
                value = formState.nombre,
                onValueChange = onNombreChange,
                placeholder = "NOMBRE CLIENTE....",
                icon = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 🌟 CAMBIO 1: Campo Cédula con validación de números
            InputFieldWithIcon(
                value = formState.cedula,
                onValueChange = { newValue ->
                    // Solo actualiza si el nuevo valor son todos dígitos
                    if (newValue.all { it.isDigit() }) {
                        onCedulaChange(newValue)
                    }
                },
                placeholder = "C.C....",
                icon = Icons.Default.CreditCard,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Muestra teclado numérico
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 🌟 CAMBIO 2: Campo Teléfono con validación de números
            InputFieldWithIcon(
                value = formState.telefono,
                onValueChange = { newValue ->
                    // Solo actualiza si el nuevo valor son todos dígitos
                    if (newValue.all { it.isDigit() }) {
                        onTelefonoChange(newValue)
                    }
                },
                placeholder = "TELÉFONO....",
                icon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Muestra teclado numérico
            )
            Spacer(modifier = Modifier.height(10.dp))

            InputFieldWithIcon(
                value = formState.correo,
                onValueChange = onCorreoChange,
                placeholder = "CORREO...",
                icon = Icons.Default.Email
            )
            Spacer(modifier = Modifier.height(10.dp))

            DropdownSelector(
                label = "DEPARTAMENTO....",
                opciones = departamentos,
                seleccionActual = formState.departamentoSeleccionado,
                onSeleccion = onDepartamentoChange,
                isEnabled = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            DropdownSelector(
                label = "MUNICIPIO...",
                opciones = municipios,
                seleccionActual = formState.municipioSeleccionado,
                onSeleccion = onMunicipioChange,
                isEnabled = formState.departamentoSeleccionado.isNotEmpty()
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    text = "DETAL",
                    isSelected = formState.isDetalSelected,
                    onClick = onDetalSelect,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                ActionButton(
                    text = "POR MAYOR",
                    isSelected = !formState.isDetalSelected,
                    onClick = onMayorSelect,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onAgregar,
                enabled = !isSaving && isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeOscuro),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(10.dp)
            ) {
                Text(
                    text = if (isSaving) "Guardando..." else "AGREGAR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if (isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp))
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DropdownSelector(
    label: String,
    opciones: List<String>,
    seleccionActual: String,
    onSeleccion: (String) -> Unit,
    isEnabled: Boolean = true // <-- Vuelve a ser un booleano simple
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = seleccionActual,
            onValueChange = { },
            placeholder = { Text(label, color = Color.Gray.copy(alpha = 0.7f), fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isEnabled) { expanded = !expanded },
            readOnly = true,
            enabled = isEnabled, // Usa el booleano que le pasan
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable(enabled = isEnabled) { expanded = !expanded },
                    tint = if (isEnabled) ColorVerdeClaroBoton else Color.Gray
                )
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorVerdeClaroBoton,
                unfocusedBorderColor = ColorVerdeClaroBoton,
                disabledBorderColor = Color.Gray.copy(alpha = 0.5f),
                cursorColor = ColorVerdeOscuro,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        DropdownMenu(
            expanded = expanded && isEnabled,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}

// =================================================================
// COMPONENTES REUTILIZABLES (CON CAMBIO EN InputFieldWithIcon)
// =================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFieldWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    // 🌟 CAMBIO 3: Añadir keyboardOptions como parámetro
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.7f), fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = ColorVerdeClaroBoton,
                modifier = Modifier.size(24.dp)
            )
        },
        // 🌟 CAMBIO 4: Aplicar las keyboardOptions
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorVerdeClaroBoton,
            unfocusedBorderColor = ColorVerdeClaroBoton,
            cursorColor = ColorVerdeOscuro,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        )
    )
}

// ... (El resto de los componentes como DropdownSelector y ActionButton no necesitan cambios)