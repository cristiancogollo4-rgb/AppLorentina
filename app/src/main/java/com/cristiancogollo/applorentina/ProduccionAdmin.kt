package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
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
fun ProduccionAdmin() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 🟩 Barra superior gris con imagen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFBDBDBD))
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.lorenita), // tu logo
                contentDescription = "Logo Lorentina",
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Título
        Text(
            text = "PRODUCCIÓN",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Black,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Barra de búsqueda
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Buscar referencia...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBDBDBD),
                unfocusedBorderColor = Color(0xFFBDBDBD),
                cursorColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🔹 Cabecera tabla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("REF.", fontWeight = FontWeight.Bold)
            Text("COLOR", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Lista de productos
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            items(3) { index ->
                ProduccionItem(
                    referencia = when (index) {
                        0 -> "1063"
                        1 -> "1073"
                        else -> "1093"
                    },
                    colorZapato = when (index) {
                        0 -> "COÑAC"
                        1 -> "BLANCO"
                        else -> "NEGRO"
                    },
                    imagenId = when (index) {
                        0 -> R.drawable.zapato1
                        1 -> R.drawable.zapato2
                        else -> R.drawable.zapato3
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // 🔹 Botón inferior
        Button(
            onClick = { /* Acción futura */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(60.dp)
                .padding(bottom = 20.dp)
        ) {
            Text(
                text = "AGREGAR",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProduccionItem(referencia: String, colorZapato: String, imagenId: Int) {
    var expandedTalla by remember { mutableStateOf(false) }
    var tallaSeleccionada by remember { mutableStateOf("TALLA") }

    var expandedEstado by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf("ESTADO") }

    // Lista de estados del proceso
    val estados = listOf("Corte", "Armado", "Costura", "Soldadura", "Emplantilla")

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del zapato
            Image(
                painter = painterResource(id = imagenId),
                contentDescription = "Zapato",
                modifier = Modifier
                    .size(70.dp)
                    .padding(8.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Ref: $referencia", fontWeight = FontWeight.Bold)
                Text(colorZapato, color = Color.Gray)

                Spacer(modifier = Modifier.height(6.dp))

                // 🔸 Selector de talla
                Box {
                    Button(
                        onClick = { expandedTalla = !expandedTalla },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(tallaSeleccionada, color = Color.White)
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
                        (35..42).forEach { talla ->
                            DropdownMenuItem(
                                text = { Text("Talla $talla") },
                                onClick = {
                                    tallaSeleccionada = "Talla $talla"
                                    expandedTalla = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // 🔸 Selector de estado
            Box {
                Button(
                    onClick = { expandedEstado = !expandedEstado },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(estadoSeleccionado, color = Color.White)
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
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProduccionPreview() {
    AppLorentinaTheme {
        ProduccionAdmin()
    }
}

