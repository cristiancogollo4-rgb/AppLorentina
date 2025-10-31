package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowDropDown
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
    // Estados de los campos
    var referencia by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }

    // Tallas (35 a 42)
    val tallas = (35..42).toList()
    val tallasSeleccionadas = remember { mutableStateMapOf<Int, String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🩶 Barra superior gris con imagen
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

        Spacer(modifier = Modifier.height(20.dp))

        // 🟤 Título principal
        Text(
            text = "AGREGAR TAREA",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF6A4E23),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

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
                focusedBorderColor = Color(0xFFC2D500),
                unfocusedBorderColor = Color(0xFFC2D500)
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
                focusedBorderColor = Color(0xFFC2D500),
                unfocusedBorderColor = Color(0xFFC2D500)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // 🔹 Título de tallas
        Text(
            text = "Selecciona las tallas disponibles:",
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 30.dp, bottom = 8.dp)
        )

        // 🔸 Cuadrícula de tallas (2 columnas)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
        ) {
            for (i in tallas.chunked(2)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    i.forEach { talla ->
                        var expanded by remember { mutableStateOf(false) }
                        var seleccion by remember { mutableStateOf("Talla $talla") }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.48f)
                                .padding(4.dp)
                        ) {
                            OutlinedButton(
                                onClick = { expanded = true },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Black
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    width = 1.dp,
                                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFC2D500))
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(seleccion, fontSize = 14.sp)
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFFC2D500)
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                listOf("Disponible", "No disponible").forEach { estado ->
                                    DropdownMenuItem(
                                        text = { Text(estado) },
                                        onClick = {
                                            seleccion = "Talla $talla ($estado)"
                                            expanded = false
                                            tallasSeleccionadas[talla] = estado
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 🟩 Botón ENVIAR
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
