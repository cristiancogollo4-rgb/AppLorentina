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
fun InventarioScreenAdmin(
    onBackClick: () -> Unit = {},
    vm: InventarioAdminViewModel = viewModel(),
    produccionViewModel: ProduccionViewModel = viewModel()
) {
    val ui by vm.uiState.collectAsState()
    var search by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 🩶 Barra superior gris con logo y botón volver
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
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Buscar referencia...") },
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

        // 🔹 Lista de productos desde Firestore (solo en stock)
        val filtrados = ui.productos.filter {
            it.referencia.contains(search, ignoreCase = true) ||
                    it.color.contains(search, ignoreCase = true)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            items(filtrados) { producto ->
                InventarioItemFirestore(producto)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // 🔘 Botón CONSULTAR
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(60.dp)
                .padding(bottom = 20.dp)
        ) {
            Text(
                text = "CONSULTAR",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 🪟 Ventana emergente de consulta
        if (showDialog) {
            ConsultaPorReferenciaDialog(
                onDismiss = { showDialog = false },
                vm = vm
            )
        }
    }
}

// 🔹 Ítem visual de inventario
@Composable
fun InventarioItemFirestore(producto: Producto) {
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
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val painter = rememberAsyncImagePainter(
                model = producto.imagenUrl.ifEmpty { R.drawable.ic_launcher_foreground }
            )
            Image(
                painter = painter,
                contentDescription = "Zapato",
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text("Ref: ${producto.referencia}", fontWeight = FontWeight.Bold)
                Text("Color: ${producto.color}", color = Color.Gray)
                Text("Estado: ${producto.estado}", color = Color.DarkGray)
            }
        }
    }
}

// 🪟 Diálogo para buscar producto por referencia
@Composable
fun ConsultaPorReferenciaDialog(
    onDismiss: () -> Unit,
    vm: InventarioAdminViewModel
) {
    var referencia by remember { mutableStateOf("") }
    var producto by remember { mutableStateOf<Producto?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Consultar referencia") },
        text = {
            Column {
                OutlinedTextField(
                    value = referencia,
                    onValueChange = { referencia = it },
                    label = { Text("Referencia") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { vm.consultarPorReferencia(referencia) { producto = it } },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("BUSCAR", color = Color.White)
                }

                if (producto != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Color: ${producto!!.color}", fontWeight = FontWeight.Bold)
                    Text("Stock actual:")
                    producto!!.stockPorTalla.forEach { (t, c) ->
                        Text(text = "Talla $t → $c pares")
                    }
                } else if (referencia.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No se encontró la referencia.", color = Color.Red)
                }
            }
        }
    )
}

// 🔍 PREVIEW para ver antes de ejecutar
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InventarioPreview() {
    AppLorentinaTheme {
        InventarioScreenAdmin()
    }
}
