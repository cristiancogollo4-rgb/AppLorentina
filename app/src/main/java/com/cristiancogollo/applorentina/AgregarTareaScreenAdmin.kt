package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AgregarTareaScreenAdmin(
    onBackClick: () -> Unit = {},
    navTo: (String) -> Unit = {},
    viewModel: ProduccionViewModel = viewModel()
) {
    var referencia by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }

    var expandedEstado by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf("en producción") }
    val estados = listOf("en producción", "Corte", "Armado", "Costura", "Soldadura", "Emplantilla")

    val formState by viewModel.formState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra superior con flecha + logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFBDBDBD))
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(35.dp)
                )
            }
            Image(
                painter = painterResource(id = R.drawable.lorenita),
                contentDescription = "Logo Lorentina",
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "AGREGAR TAREA",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF6A4E23),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Referencia
        OutlinedTextField(
            value = referencia,
            onValueChange = {
                referencia = it
                viewModel.updateReferencia(it)
            },
            placeholder = { Text("Referencia") },
            leadingIcon = { Icon(Icons.Default.Style, contentDescription = null, tint = Color(0xFFC2D500)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBDBDBD),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Color
        OutlinedTextField(
            value = color,
            onValueChange = {
                color = it
                viewModel.updateColor(it)
            },
            placeholder = { Text("Color... (COÑAC / BLANCO / NEGRO)") },
            leadingIcon = { Icon(Icons.Default.Brush, contentDescription = null, tint = Color(0xFFC2D500)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBDBDBD),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Estado del proceso
        Text(
            text = "Estado del proceso:",
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 30.dp, bottom = 6.dp)
        )

        Box(modifier = Modifier.fillMaxWidth(0.85f)) {
            Button(
                onClick = { expandedEstado = !expandedEstado },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Text(estadoSeleccionado, color = Color.White, fontSize = 14.sp)
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
            }

            DropdownMenu(expanded = expandedEstado, onDismissRequest = { expandedEstado = false }) {
                estados.forEach { e ->
                    DropdownMenuItem(
                        text = { Text(e) },
                        onClick = {
                            estadoSeleccionado = e
                            expandedEstado = false
                            viewModel.updateEstado(e)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // ENVIAR → guarda en Firestore
        Button(
            onClick = {
                viewModel.crearProducto()
                // si deseas, navega directo a Inventario:
                // navTo(Screen.InventarioAdmin.route)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(55.dp)
                .shadow(4.dp, RoundedCornerShape(10.dp))
        ) {
            Text("ENVIAR", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mensaje
        formState.mensaje?.let { mensaje ->
            Text(
                text = mensaje,
                color = if (mensaje.contains("Error")) Color.Red else Color(0xFF4CAF50),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
