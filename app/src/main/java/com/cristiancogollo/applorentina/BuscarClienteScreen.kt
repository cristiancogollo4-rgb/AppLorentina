package com.cristiancogollo.applorentina


import Cliente
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

@Composable
fun BuscarClienteScreen(
    onBackClick: () -> Unit,
    onAddClientClick: () -> Unit,
    // Obtenemos una instancia del ViewModel. ViewModelProvider.Factory no es necesario aquí
    // a menos que necesites pasar dependencias complejas.
    viewModel: ClientesViewModel = viewModel()
) {
    val colorVerdeClaro = Color(0xFFC2D500)
    val colorVerdeOscuro = Color(0xFFB5CC00)
    val colorGrisTexto = Color(0xFF5C5C5C)

    // Recopilamos el estado del ViewModel
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
                    // Encabezado verde (sin cambios)
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
                        Box(modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)) {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(35.dp))
                            }
                        }
                        Image(
                            painter = painterResource(id = R.drawable.lorenita),
                            contentDescription = "Logo Lorentina",
                            modifier = Modifier.fillMaxWidth(0.6f).height(200.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("BUSCAR CLIENTE", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colorGrisTexto)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Barra de búsqueda (ahora conectada al ViewModel)
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) }, // 👈 Llama al ViewModel
                        placeholder = { Text("BUSCAR CLIENTE....", color = Color.Gray.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.Gray.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorVerdeClaro,
                            unfocusedBorderColor = colorVerdeClaro,
                            cursorColor = colorVerdeOscuro
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtros (ahora conectados al ViewModel)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterButton(
                            text = "NOMBRE",
                            isSelected = uiState.filterType == FilterType.NOMBRE, // 👈 Estado desde el ViewModel
                            onClick = { viewModel.updateFilterType(FilterType.NOMBRE) }, // 👈 Acción del ViewModel
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

                    // Lista de clientes (ahora usa los datos del ViewModel y maneja estados)
                    Box(modifier = Modifier.weight(1f).padding(horizontal = 24.dp)) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        } else if (uiState.errorMessage != null) {
                            Text(
                                text = uiState.errorMessage!!,
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(uiState.filteredClientes) { cliente -> // 👈 Usa la lista filtrada
                                    ClienteCard(cliente = cliente) // 👈 Pasa el objeto completo
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }

                // Botón flotante (sin cambios)
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar cliente", modifier = Modifier.size(60.dp))
                        Text("AGREGAR CLIENTE", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.White, lineHeight = 10.sp)
                    }
                }
            }
        }
    }
}


// MODIFICACIÓN: FilterButton ahora necesita un onClick
@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = if (isSelected) Color(0xFFB5CC00) else Color(0xFFEFF5C9)
    val contentColor = if (isSelected) Color.White else Color(0xFF8AA100)
    val borderColor = if (isSelected) Color(0xFFB5CC00) else Color(0xFFEFF5C9)

    Button(
        onClick = onClick, // 👈 Usa el onClick que le pasan
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        shape = RoundedCornerShape(10.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(borderColor)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        modifier = modifier
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

// MODIFICACIÓN: ClienteCard ahora recibe un objeto Cliente
@Composable
fun ClienteCard(cliente: Cliente) { // 👈 Recibe el objeto completo
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(cliente.nombre, fontWeight = FontWeight.Bold, color = Color(0xFF5C5C5C)) // 👈 Usa los datos del objeto
                Text(cliente.cedula.toString(), fontSize = 12.sp, color = Color.Gray)
            }
            Button(
                onClick = { /* TODO: Navegar a detalles del cliente */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB5CC00), contentColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("VER DETALLES", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BuscarClientePreview() {
    AppLorentinaTheme {
        Surface(color = Color(0xFFEFEFEF)) {
            BuscarClienteScreen(onBackClick = {}, onAddClientClick = {})
        }
    }
}