package com.cristiancogollo.applorentina

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 🔹 Diálogo para agregar un nuevo cliente (sin KeyboardOptions).
 * Compatible con Firestore a través del ViewModel.
 */
@Composable
fun AgregarClienteDialog(
    onDismiss: () -> Unit,
    onSave: (
        nombre: String,
        cedula: String,
        telefono: String,
        correo: String,
        departamento: String,
        municipio: String,
        esDetal: Boolean
    ) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var municipio by remember { mutableStateOf("") }
    var tipoClienteDetal by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank() && cedula.isNotBlank() && telefono.isNotBlank()) {
                        onSave(nombre, cedula, telefono, correo, departamento, municipio, tipoClienteDetal)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD))
            ) {
                Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
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
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = cedula,
                    onValueChange = { cedula = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Cédula") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = departamento,
                    onValueChange = { departamento = it },
                    label = { Text("Departamento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = municipio,
                    onValueChange = { municipio = it },
                    label = { Text("Municipio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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
                            selected = tipoClienteDetal,
                            onClick = { tipoClienteDetal = true },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFB5CC00))
                        )
                        Text("Detal")
                        Spacer(Modifier.width(10.dp))
                        RadioButton(
                            selected = !tipoClienteDetal,
                            onClick = { tipoClienteDetal = false },
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
