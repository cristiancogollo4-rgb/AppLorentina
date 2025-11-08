package com.cristiancogollo.applorentina

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // 🌟 Importante para obtener el ViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import android.util.Log
import androidx.navigation.NavController // Import necesario si lo usas en la firma


// Definición de colores del proyecto
val ColorVerdeOscuro = Color(0xFFB5CC00)
val ColorVerdeClaroBoton = Color(0xFFC2D500)
val ColorGrisTexto = Color(0xFF5C5C5C)
val ColorFondoCard = Color(0xFFF8F8F8)


// =================================================================
// PANTALLA PRINCIPAL (Conectada al ViewModel)
// =================================================================

@Composable
fun AgregarClienteScreen(
    // ⚠️ Si no usas navegación, puedes omitir NavController en la firma y el cuerpo
    viewModel: ClienteViewModel = viewModel(factory = ClienteViewModelFactory(LocalContext.current))
) {
    val coroutineScope = rememberCoroutineScope()

    // Observa los estados del ViewModel
    val formState by viewModel.formState.collectAsState()
    val message by viewModel.message.collectAsState()
    val departamentos = viewModel.departamentos
    val municipios = viewModel.municipios

    // Observa el estado de inicialización de Firebase
    val firebaseState by viewModel.firebaseState.collectAsState()
    // Determina si la UI debe estar en modo "Cargando/Inicializando"
    val isDatabaseLoading = !firebaseState.isInitialized

    // 🌟 ESTADO CLAVE: La validación del formulario del ViewModel
    val isFormValid by viewModel.isFormValid.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        // 🌟 3. Pasa los estados y handlers al componente de contenido
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
            isSaving = isDatabaseLoading,
            isFormValid = isFormValid, // 🌟 MODIFICACIÓN 1: Pasar el estado de validez
            onDetalSelect = { viewModel.setTipoCliente(true) },
            onMayorSelect = { viewModel.setTipoCliente(false) },
            onAgregar = viewModel::saveCliente // Llama a la lógica de guardado en Firestore
        )

        // Mostrar mensajes (Snackbar)
        message?.let { msg ->
            Snackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text(msg)
            }
            // Ocultar mensaje después de un tiempo y limpiarlo del ViewModel
            LaunchedEffect(msg) {
                coroutineScope.launch {
                    kotlinx.coroutines.delay(3000L)
                    viewModel.clearMessage()
                }
            }
        }
    }
}

// =================================================================
// CONTENIDO DEL DIÁLOGO (Modificado)
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
    isFormValid: Boolean // 🌟 MODIFICACIÓN 1: Recibe el estado de validez
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
            // Título
            Text(
                text = "AGREGAR CLIENTE",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorGrisTexto.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campos de entrada (conectados al estado)
            InputFieldWithIcon(
                value = formState.nombre,
                onValueChange = onNombreChange,
                placeholder = "NOMBRE CLIENTE....",
                icon = Icons.Outlined.Person
            )
            Spacer(modifier = Modifier.height(10.dp))
            InputFieldWithIcon(
                value = formState.cedula,
                onValueChange = onCedulaChange,
                placeholder = "C.C....",
                icon = Icons.Default.Badge
            )
            Spacer(modifier = Modifier.height(10.dp))
            InputFieldWithIcon(
                value = formState.telefono,
                onValueChange = onTelefonoChange,
                placeholder = "TELÉFONO....",
                icon = Icons.Outlined.Phone
            )
            Spacer(modifier = Modifier.height(10.dp))
            InputFieldWithIcon(
                value = formState.correo,
                onValueChange = onCorreoChange,
                placeholder = "CORREO...",
                icon = Icons.Default.MailOutline
            )
            Spacer(modifier = Modifier.height(10.dp))

            // DEPARTAMENTO
            DropdownSelector(
                label = "DEPARTAMENTO....",
                opciones = departamentos,
                seleccionActual = formState.departamentoSeleccionado,
                onSeleccion = onDepartamentoChange
            )
            Spacer(modifier = Modifier.height(10.dp))

            // MUNICIPIO
            DropdownSelector(
                label = "MUNICIPIO...",
                opciones = municipios,
                seleccionActual = formState.municipioSeleccionado,
                onSeleccion = onMunicipioChange,
                isEnabled = formState.departamentoSeleccionado.isNotEmpty()
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Botones DETAL y POR MAYOR
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

            // Botón AGREGAR
            Button(
                onClick = onAgregar,
                // 🌟 MODIFICACIÓN 2: Habilitado solo si la DB está lista Y el formulario es válido
                enabled = !isSaving && isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeOscuro),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(10.dp)
            ) {
                Text(
                    text = if (isSaving) "Conectando..." else "AGREGAR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Indicador de Carga/Guardado
            if (isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp))
            }
        }
    }
}

// =================================================================
// COMPONENTES REUTILIZABLES
// =================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFieldWithIcon(value: String, onValueChange: (String) -> Unit, placeholder: String, icon: ImageVector) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    opciones: List<String>,
    seleccionActual: String,
    onSeleccion: (String) -> Unit,
    isEnabled: Boolean = true
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
            enabled = isEnabled,
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