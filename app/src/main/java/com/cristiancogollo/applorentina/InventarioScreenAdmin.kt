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
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

@Composable
fun InventarioScreenAdmin(
    onBackClick: () -> Unit = {},
    vm: InventarioAdminViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val ui by vm.uiState.collectAsState()

    // Estados de totales que aparecen al presionar CONSULTAR
    var totalesPorTalla by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var totalesPorColor by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var mostrarTotales by remember { mutableStateOf(false) }

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
                modifier = Modifier.height(180.dp).width(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "INVENTARIO",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Black,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Buscador
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

        if (ui.isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6A4E23))
            }
        }

        val filtrados = ui.productos.filter {
            it.referencia.contains(searchQuery, ignoreCase = true) ||
                    it.color.contains(searchQuery, ignoreCase = true) ||
                    it.nombreModelo.contains(searchQuery, ignoreCase = true)
        }

        // Lista de productos
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            items(filtrados) { p ->
                InventarioItemFirestore(p)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // CONSULTAR → Totales por talla y por color (sobre la lista filtrada)
        Button(
            onClick = {
                val totTalla = mutableMapOf<String, Int>()
                val totColor = mutableMapOf<String, Int>()

                filtrados.forEach { prod ->
                    // por talla
                    prod.stockPorTalla.forEach { (talla, cant) ->
                        totTalla[talla] = (totTalla[talla] ?: 0) + cant
                    }
                    // por color: suma total del producto (todas las tallas)
                    val suma = prod.stockPorTalla.values.sum()
                    totColor[prod.color] = (totColor[prod.color] ?: 0) + suma
                }

                totalesPorTalla = totTalla.toSortedMap(compareBy { it.toIntOrNull() ?: 0 })
                totalesPorColor = totColor.toSortedMap()
                mostrarTotales = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(60.dp)
                .padding(bottom = 12.dp)
        ) {
            Text("CONSULTAR", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (mostrarTotales) {
            // Bloque de totales con tu estilo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text("Totales por talla", fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(Modifier.height(6.dp))
                FlowRowTallas(totalesPorTalla)

                Spacer(Modifier.height(12.dp))
                Text("Totales por color", fontWeight = FontWeight.Bold, color = Color.Gray)
                totalesPorColor.forEach { (color, total) ->
                    Text("• $color: $total pares", color = Color.Black)
                }

                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun InventarioItemFirestore(producto: Producto) {
    // Reusa tu card simple (solo visual). Aquí no se edita stock (si lo quieres, se puede agregar TextField por talla).
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
        Column(Modifier.fillMaxWidth()) {
            Text("Ref: ${producto.referencia}", fontWeight = FontWeight.Bold)
            Text("Color: ${producto.color}", color = Color.Gray)
            Spacer(Modifier.height(6.dp))

            // Muestra las tallas 35–42 con su stock
            Column {
                (35..42).chunked(4).forEach { bloque ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        bloque.forEach { t ->
                            val cantidad = producto.stockPorTalla[t.toString()] ?: 0
                            Text("T$t: $cantidad")
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun FlowRowTallas(totales: Map<String, Int>) {
    Column {
        (35..42).chunked(4).forEach { bloque ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                bloque.forEach { t ->
                    val v = totales[t.toString()] ?: 0
                    Text("T$t: $v")
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InventarioPreview() {
    AppLorentinaTheme { InventarioScreenAdmin(onBackClick = {}) }
}
