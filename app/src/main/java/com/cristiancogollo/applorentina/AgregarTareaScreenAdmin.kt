package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

@Composable
fun AgregarTareaScreenAdmin() {
    var referencia by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }

    // Estado del proceso
    var expandedEstado by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf("Seleccione estado") }
    val estados = listOf("Corte", "Armado", "Costura", "Soldadura", "Emplantilla")

    // Selector único de talla
    var expandedTalla by remember { mutableStateOf(false) }
    var tallaSeleccionada by remember { mutableStateOf("Talla") }
    val tallas = listOf("35", "36", "37", "38", "39", "40", "41", "42")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🩶 Barra superior gris
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFBDBDBD))
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.lorenita),
                contentDescription = "Logo Lorentina",
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // 🟤 Título principal
        Text(
            text = "AGREGAR TAREA",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF6A4E23),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Registra una nueva referencia y color",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Referencia
        OutlinedTextField(
            value = referencia,
            onValueChange = { referencia = it },
            placeholder = { Text("Referencia") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFFC2D500)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBDBDBD),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo Color
        OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            placeholder = { Text("Color...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Brush,
                    contentDescription = null,
                    tint = Color(0xFFC2D500)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBDBDBD),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // 🔹 Selector único de Talla
        Text(
            text = "Selecciona la talla:",
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 30.dp, bottom = 6.dp)
        )

        Box(modifier = Modifier.fillMaxWidth(0.85f)) {
            Button(
                onClick = { expandedTalla = !expandedTalla },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBDBDBD)
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Text(tallaSeleccionada, color = Color.White, fontSize = 14.sp)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar talla",
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = expandedTalla,
                onDismissRequest = { expandedTalla = false }
            ) {
                tallas.forEach { talla ->
                    DropdownMenuItem(
                        text = { Text(talla) },
                        onClick = {
                            tallaSeleccionada = "Talla $talla"
                            expandedTalla = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        // 🔹 Estado del proceso
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBDBDBD)
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Text(estadoSeleccionado, color = Color.White, fontSize = 14.sp)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar estado",
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = expandedEstado,
                onDismissRequest = { expandedEstado = false }
            ) {
                estados.forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado) },
                        onClick = {
                            estadoSeleccionado = estado
                            expandedEstado = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 🩶 Botón ENVIAR
        Button(
            onClick = { /* Acción futura */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(55.dp)
                .shadow(4.dp, RoundedCornerShape(10.dp))
        ) {
            Text(
                text = "ENVIAR",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AgregarTareaPreview() {
    AppLorentinaTheme {
        AgregarTareaScreenAdmin()
    }
}
