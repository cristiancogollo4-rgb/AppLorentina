package com.cristiancogollo.applorentina

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState



private val ColorVerdeOscuroo = Color(0xFFB5CC00)
@Composable
fun AgregarClienteDialog(
    onDismiss: () -> Unit,

    viewModel: AgregarClienteViewModel = viewModel(factory = AgregarClienteViewModelFactory(LocalContext.current))
) {
    // 💡 Obtenemos estados del ViewModel
    val uiState = viewModel.uiState.collectAsState()
    val isFormValid = viewModel.isFormValid.collectAsState()
    val formState = uiState.value.formState
    val departamentos = viewModel.departamentos
    val municipios = viewModel.municipios
    val coroutineScope = rememberCoroutineScope()


    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (isFormValid.value) {
                        viewModel.saveCliente(onSuccess = {
                            coroutineScope.launch {
                                // Espera breve antes de cerrar para feedback visual
                                kotlinx.coroutines.delay(500)
                                onDismiss()
                            }
                        })
                    }
                },
                // Lógica de habilitación usando el estado del VM
                enabled = !uiState.value.isLoading && isFormValid.value,
                // Mantenemos el color original hardcodeado
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid.value) ColorVerdeOscuroo else Color(0xFFBDBDBD))
            ) {
                Text(
                    text = if (uiState.value.isLoading) "Guardando..." else "Guardar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {

                Text("Cancelar", color = Color(0xFF777777))
            }
        },
        title = {
            Text(
                text = "Agregar Cliente",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF6A4E23),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.value.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp))
                }

                // 💡 Input: Nombre (Usando InputFieldWithIcon y estado del VM)
                InputFieldWithIcon(
                    value = formState.nombre,
                    onValueChange = viewModel::updateNombre,
                    placeholder = "Nombre completo",
                    icon = Icons.Default.Person
                )
                Spacer(Modifier.height(8.dp))

                // 💡 Input: Cédula
                InputFieldWithIcon(
                    value = formState.cedula,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) viewModel.updateCedula(newValue)
                    },
                    placeholder = "Cédula",
                    icon = Icons.Default.CreditCard,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(8.dp))

                // 💡 Input: Teléfono
                InputFieldWithIcon(
                    value = formState.telefono,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) viewModel.updateTelefono(newValue)
                    },
                    placeholder = "Teléfono",
                    icon = Icons.Default.Phone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(8.dp))

                // 💡 Input: Correo
                InputFieldWithIcon(
                    value = formState.correo,
                    onValueChange = viewModel::updateCorreo,
                    placeholder = "Correo (opcional)",
                    icon = Icons.Default.Email
                )
                Spacer(Modifier.height(8.dp))

                // 🚀 Dropdown: DEPARTAMENTO (Lógica MVVM)
                DropdownSelector(
                    label = "DEPARTAMENTO....",
                    opciones = departamentos, // Lista del ViewModel
                    seleccionActual = formState.departamentoSeleccionado,
                    onSeleccion = viewModel::updateDepartamento, // Llama a la función que actualiza y resetea el municipio
                    isEnabled = !uiState.value.isLoading
                )
                Spacer(Modifier.height(8.dp))

                // 🚀 Dropdown: MUNICIPIO (Lógica MVVM)
                DropdownSelector(
                    label = "MUNICIPIO...",
                    opciones = municipios, // Lista dinámica del ViewModel
                    seleccionActual = formState.municipioSeleccionado,
                    onSeleccion = viewModel::updateMunicipio,
                    // Habilitado solo si hay un departamento seleccionado
                    isEnabled = formState.departamentoSeleccionado.isNotEmpty() && !uiState.value.isLoading
                )
                Spacer(Modifier.height(12.dp))

                // 🔘 Tipo de cliente
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tipo de cliente:", color = Color.Gray, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = formState.isDetalSelected,
                            onClick = { viewModel.setTipoCliente(true) },
                            // Mantenemos el color original hardcodeado
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFB5CC00))
                        )
                        Text("Detal")
                        Spacer(Modifier.width(10.dp))
                        RadioButton(
                            selected = !formState.isDetalSelected,
                            onClick = { viewModel.setTipoCliente(false) },
                            // Mantenemos el color original hardcodeado
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFB5CC00))
                        )
                        Text("Por mayor")
                    }
                }
            }
        },
        shape = RoundedCornerShape(15.dp),
        containerColor = Color.White,
        tonalElevation = 10.dp
    )
}

// =================================================================
// 🚀 COMPONENTES REUTILIZABLES (Usando colores hardcodeados)
// Se usan los colores de AgregarClienteScreen.kt (Verde Oscuro: 0xFFB5CC00, Verde Claro Botón: 0xFFC2D500)
// =================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFieldWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.7f), fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                // Usando ColorVerdeClaroBoton hardcodeado: 0xFFC2D500
                tint = Color(0xFFC2D500),
                modifier = Modifier.size(24.dp)
            )
        },
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            // Usando ColorVerdeClaroBoton hardcodeado: 0xFFC2D500
            focusedBorderColor = Color(0xFFC2D500),
            unfocusedBorderColor = Color(0xFFC2D500),
            // Usando ColorVerdeOscuro hardcodeado: 0xFFB5CC00
            cursorColor = Color(0xFFB5CC00),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        )
    )
}

