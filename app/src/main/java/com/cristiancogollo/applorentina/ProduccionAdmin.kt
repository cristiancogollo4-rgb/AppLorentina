package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

@Composable
fun ProduccionAdmin(
    onBackClick: () -> Unit = {},
    navTo: (String) -> Unit = {},
    viewModel: ProduccionViewModel = viewModel()
) {
    val productos by viewModel.productos.collectAsState()
    var search by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Barra superior
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

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "PRODUCCIÓN",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Black,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Búsqueda
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
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

        // Filtrados por búsqueda
        val filtrados = productos.filter {
            it.referencia.contains(search, ignoreCase = true) ||
                    it.color.contains(search, ignoreCase = true) ||
                    it.nombreModelo.contains(search, ignoreCase = true)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            items(filtrados) { producto ->
                ProduccionItemFirestore(
                    producto = producto,
                    viewModel = viewModel
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Botón AGREGAR
        Button(
            onClick = { navTo(Screen.AgregarTareaAdmin.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(60.dp)
                .padding(bottom = 20.dp)
        ) {
            Text("AGREGAR", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProduccionItemFirestore(
    producto: Producto,
    viewModel: ProduccionViewModel
) {
    var expandedTalla by remember { mutableStateOf(false) }
    var tallaSeleccionada by remember { mutableStateOf("TALLA") }

    var expandedEstado by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf(producto.estado.ifBlank { "en producción" }) }

    val estados = listOf("Corte", "Armado", "Costura", "Soldadura", "Emplantilla", "en stock")

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
            // Imagen (Coil soporta android.resource:// o url)
            val painter = rememberAsyncImagePainter(
                model = if (producto.imagenUrl.isNotBlank()) producto.imagenUrl else R.drawable.ic_launcher_foreground
            )
            Image(
                painter = painter,
                contentDescription = "Zapato",
                modifier = Modifier.size(70.dp).padding(8.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Ref: ${producto.referencia}", fontWeight = FontWeight.Bold)
                Text("Color: ${producto.color}", color = Color.Gray, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(4.dp))

                // Selector talla (visual)
                Box {
                    Button(
                        onClick = { expandedTalla = !expandedTalla },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(tallaSeleccionada, color = Color.White)
                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                    }

                    DropdownMenu(expanded = expandedTalla, onDismissRequest = { expandedTalla = false }) {
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

            Spacer(modifier = Modifier.width(8.dp))

            // Selector de estado (puede pasar a "en stock")
            Box {
                Button(
                    onClick = { expandedEstado = !expandedEstado },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(estadoSeleccionado, color = Color.White)
                    Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                }

                DropdownMenu(expanded = expandedEstado, onDismissRequest = { expandedEstado = false }) {
                    estados.forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado) },
                            onClick = {
                                estadoSeleccionado = estado
                                expandedEstado = false
                                if (estado == "en stock") {
                                    viewModel.actualizarEstadoAStock(producto.referencia)
                                }
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
    AppLorentinaTheme { ProduccionAdmin() }
}
