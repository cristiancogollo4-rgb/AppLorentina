package com.cristiancogollo.applorentina


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.net.URLEncoder

@Composable
fun BuscarClienteScreen(
    navController: NavController, // 👈 Recibimos el navController
    onBackClick: () -> Unit,
    onAddClientClick: () -> Unit,
    viewModel: ClientesViewModel = viewModel()
) {
    val colorVerdeClaro = Color(0xFFC2D500)
    val colorVerdeOscuro = Color(0xFFB5CC00)
    val colorGrisTexto = Color(0xFF5C5C5C)

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 🔹 Encabezado
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                colorVerdeClaro,
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            )
                            .padding(vertical = 15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 8.dp)
                        ) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = Color.White,
                                    modifier = Modifier.size(35.dp)
                                )
                            }
                        }
                        Image(
                            painter = painterResource(id = R.drawable.lorenita),
                            contentDescription = "Logo Lorentina",
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(200.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        "BUSCAR CLIENTE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorGrisTexto
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🔹 Barra de búsqueda
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("BUSCAR CLIENTE....", color = Color.Gray.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.Gray.copy(alpha = 0.7f))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorVerdeClaro,
                            unfocusedBorderColor = colorVerdeClaro,
                            cursorColor = colorVerdeOscuro
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🔹 Filtros
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterButton(
                            text = "NOMBRE",
                            isSelected = uiState.filterType == FilterType.NOMBRE,
                            onClick = { viewModel.updateFilterType(FilterType.NOMBRE) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterButton(
                            text = "C.C",
                            isSelected = uiState.filterType == FilterType.CEDULA,
                            onClick = { viewModel.updateFilterType(FilterType.CEDULA) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterButton(
                            text = "TELÉFONO",
                            isSelected = uiState.filterType == FilterType.TELEFONO,
                            onClick = { viewModel.updateFilterType(FilterType.TELEFONO) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🔹 Lista de clientes
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 24.dp)
                    ) {
                        when {
                            uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            uiState.errorMessage != null -> Text(
                                text = uiState.errorMessage!!,
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(uiState.filteredClientes) { cliente ->
                                    ClienteCard(cliente = cliente, onVerDetalles = {
                                        // ✅ Construimos la ruta con todos los datos del cliente
                                        // Usamos URLEncoder para evitar problemas si algún dato tiene caracteres especiales como espacios o "/"
                                        val nombreEncoded = URLEncoder.encode(cliente.nombreApellido ?: "Sin Nombre", "UTF-8")
                                        val cedulaEncoded = URLEncoder.encode(if (cliente.cedula == 0L) "" else cliente.cedula.toString(), "UTF-8")
                                        val telefonoEncoded = URLEncoder.encode(if (cliente.telefono == 0L) "" else cliente.telefono.toString(), "UTF-8")
                                        val correoEncoded = URLEncoder.encode(cliente.correo ?: "", "UTF-8") // <-- MANEJO DE NULOS
                                        val departamentoEncoded = URLEncoder.encode(cliente.departamento ?: "", "UTF-8")
                                        val municipioEncoded = URLEncoder.encode(cliente.municipio ?: "", "UTF-8")
                                        val tipoClienteEncoded = URLEncoder.encode(cliente.tipoCliente.toString(), "UTF-8")

                                        navController.navigate("detalle_cliente_screen/$nombreEncoded/$cedulaEncoded/$telefonoEncoded/$correoEncoded/$departamentoEncoded/$municipioEncoded/$tipoClienteEncoded")
                                    })
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }

                // 🔹 Botón flotante
                FloatingActionButton(
                    onClick = onAddClientClick,
                    containerColor = colorVerdeOscuro,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 24.dp)
                        .size(90.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar cliente", modifier = Modifier.size(60.dp))
                        Text(
                            "AGREGAR CLIENTE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            lineHeight = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = if (isSelected) Color(0xFFB5CC00) else Color(0xFFEFF5C9)
    val contentColor = if (isSelected) Color.White else Color(0xFF8AA100)
    val borderColor = if (isSelected) Color(0xFFB5CC00) else Color(0xFFEFF5C9)

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        shape = RoundedCornerShape(10.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(borderColor)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        modifier = modifier
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ClienteCard(cliente: Cliente, onVerDetalles: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(cliente.nombreApellido, fontWeight = FontWeight.Bold, color = Color(0xFF5C5C5C))
                Text(
                    if (cliente.tipoCliente) "Cliente Detal" else "Cliente por Mayor",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Button(
                onClick = onVerDetalles,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB5CC00), contentColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("VER DETALLES", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
