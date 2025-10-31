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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

@Composable
fun InventarioScreenAdmin() {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Título
        Text(
            text = "INVENTARIO",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Black,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🔍 Buscador
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar referencia....") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color(0xFFBDBDBD)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBDBDBD),
                unfocusedBorderColor = Color(0xFFBDBDBD),
                cursorColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🔹 Cabecera
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {}

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Lista de productos
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            items(3) { index ->
                InventarioItem(
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

        // 🔘 Botón GUARDAR inferior
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
                text = "GUARDAR",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InventarioItem(referencia: String, colorZapato: String, imagenId: Int) {
    var expandedTalla by remember { mutableStateOf(false) }
    var tallaSeleccionada by remember { mutableStateOf("Talla") }

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
            // 🖼️ Imagen del zapato
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

            // 🔘 Botón MODIFICAR gris
            Button(
                onClick = { /* acción futura */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Text(
                    text = "MODIFICAR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InventarioPreview() {
    AppLorentinaTheme {
        InventarioScreenAdmin()
    }
}
