package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

// Asumiendo que esta es la constante definida globalmente



@Composable
fun ProduccionAdmin(
    onBackClick: () -> Unit = {},
    navTo: (String) -> Unit = {},
    viewModel: ProduccionViewModel = viewModel()
) {
    val productos by viewModel.productos.collectAsState()
    var search by remember { mutableStateOf("") }

    // 🟢 ESTADO DEL DIÁLOGO: Almacena el ID del producto que se está editando
    var stockEditProductId by remember { mutableStateOf<String?>(null) }

    // 🟢 Producto a editar (busca en la lista actual de productos)
    val productoToEdit = productos.find { it.id == stockEditProductId }

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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            text = "PRODUCCIÓN",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Black,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🔹 Barra de búsqueda
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

        // 🔹 Lista de productos desde Firestore (filtrados)
        val filtrados = productos.filter {
            it.referencia.contains(search, ignoreCase = true) ||
                    it.color.contains(search, ignoreCase = true)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            items(filtrados) { producto ->
                ProduccionItemFirestore(
                    producto = producto,
                    viewModel = viewModel,
                    // 🟢 Pasa la función para abrir el diálogo de edición
                    onEditStockClick = { id -> stockEditProductId = id }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // 🔹 Botón inferior — redirige a AgregarTareaScreenAdmin
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
            Text(
                text = "AGREGAR",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // 🟢 DIÁLOGO DE EDICIÓN DE STOCK (Se muestra si hay un producto seleccionado)
    if (productoToEdit != null) {
        EditStockDialog(
            producto = productoToEdit,
            onDismiss = { stockEditProductId = null }, // Cerrar el diálogo
            onSave = { newStockMap, newImageUrl ->
                viewModel.updateProductoData(
                    id = productoToEdit.id,
                    newStockMap = newStockMap,
                    newImageUrl = newImageUrl
                )
                stockEditProductId = null // Cerrar al guardar
            }
        )
    }
}

// -----------------------------------------------------------------
// COMPONENTE ITEM DE PRODUCCIÓN
// -----------------------------------------------------------------
@Composable
fun ProduccionItemFirestore(
    producto: Producto,
    viewModel: ProduccionViewModel,
    onEditStockClick: (String) -> Unit // 🟢 Nuevo callback
) {
    var expandedEstado by remember { mutableStateOf(false) }
    // Usamos el estado actual del producto, pero capitalizado
    var estadoSeleccionado by remember(producto.estado) { mutableStateOf(producto.estado.replaceFirstChar { it.uppercase() }) }

    // Se asegura de que los nombres de los estados se muestren capitalizados en el menú
    val estadosDisplay = listOf("Corte", "Armado", "Costura", "Soldadura", "Emplantilla", "En stock")
    // Se mapea a minúsculas para comparaciones internas y envío al VM
    val estadosMap = mapOf(
        "Corte" to "corte",
        "Armado" to "armado",
        "Costura" to "costura",
        "Soldadura" to "soladura",
        "Emplantilla" to "emplantillado",
        "En stock" to "en stock"
    )

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
            // 🖼️ Imagen del zapato (Se puede usar Coil o Glide aquí para cargar desde URL)
            // Por ahora, solo muestra un drawable de placeholder
            val drawableId = when {
                producto.imagenUrl.contains("zapato1", ignoreCase = true) -> R.drawable.zapato1
                producto.imagenUrl.contains("zapato2", ignoreCase = true) -> R.drawable.zapato2
                producto.imagenUrl.contains("zapato3", ignoreCase = true) -> R.drawable.zapato3
                else -> R.drawable.ic_launcher_foreground
            }

            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "Zapato ${producto.color}",
                modifier = Modifier
                    .size(70.dp)
                    .padding(8.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Ref: ${producto.referencia}", fontWeight = FontWeight.Bold)
                Text(producto.color, color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))

                // 🔸 Botón para Editar Stock (TALLAS Y URL)
                Box {
                    Button(
                        onClick = { onEditStockClick(producto.id) }, // 🟢 Abre el diálogo de edición
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        val totalStock = producto.stockPorTalla.values.sum()
                        Text("Stock: $totalStock", color = Color.White)
                        Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 🔸 Selector de estado
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

                DropdownMenu(
                    expanded = expandedEstado,
                    onDismissRequest = { expandedEstado = false }
                ) {
                    estadosDisplay.forEach { estadoDisplay ->
                        DropdownMenuItem(
                            text = { Text(estadoDisplay) },
                            onClick = {
                                estadoSeleccionado = estadoDisplay
                                expandedEstado = false

                                // Llama al VM con el ID y el valor en minúsculas
                                val estadoValue = estadosMap[estadoDisplay] ?: "corte"
                                viewModel.updateEstadoProducto(producto.id, estadoValue)
                            }
                        )
                    }
                }
            }
        }
    }
}


// -----------------------------------------------------------------
// 🟢 NUEVO COMPONENTE: DIÁLOGO PARA EDITAR EL STOCK Y LA IMAGEN
// -----------------------------------------------------------------
@Composable
fun EditStockDialog(
    producto: Producto,
    onDismiss: () -> Unit,
    onSave: (newStockMap: Map<String, Int>, newImageUrl: String) -> Unit
) {
    // Estado local para la URL
    var currentImageUrl by remember { mutableStateOf(producto.imagenUrl) }

    // Estado local para los inputs de stock (String para manejar la edición)
    val editableStock = remember {
        mutableStateMapOf<String, String>().apply {
            producto.stockPorTalla.forEach { (talla, stock) ->
                this[talla] = stock.toString()
            }
            // Asegurar que todas las tallas (35 a 42) estén presentes
            for (talla in 35..42) {
                if (!this.containsKey(talla.toString())) {
                    this[talla.toString()] = "0"
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Stock: ${producto.referencia}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Input para URL de Imagen (opcional)
                Text("URL de Imagen (Opcional)", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = currentImageUrl,
                    onValueChange = { currentImageUrl = it },
                    singleLine = true,
                    label = { Text("Link de Google Drive/Imagen") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                Text("Modificar Cantidades por Talla:", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth()) {
                    // Columna 1 (35-38)
                    Column(Modifier.weight(1f)) {
                        (35..38).forEach { talla ->
                            StockEditField(
                                talla = talla.toString(),
                                stock = editableStock[talla.toString()] ?: "0",
                                onStockChange = { newValue ->
                                    editableStock[talla.toString()] = newValue
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    // Columna 2 (39-42)
                    Column(Modifier.weight(1f)) {
                        (39..42).forEach { talla ->
                            StockEditField(
                                talla = talla.toString(),
                                stock = editableStock[talla.toString()] ?: "0",
                                onStockChange = { newValue ->
                                    editableStock[talla.toString()] = newValue
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // 1. Procesar el stock
                    val newStockMap = editableStock
                        .mapValues { (_, value) -> value.toIntOrNull() ?: 0 }
                        .filterValues { it >= 0 }

                    // 2. Guardar ambos datos
                    onSave(newStockMap, currentImageUrl)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// 🟢 COMPONENTE AUXILIAR PARA EL CAMPO DE STOCK EN EL DIÁLOGO
@Composable
fun StockEditField(talla: String, stock: String, onStockChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text("T $talla:", Modifier.width(40.dp))
        OutlinedTextField(
            value = stock,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                    onStockChange(newValue)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, textAlign = TextAlign.Center),
            modifier = Modifier.weight(1f).height(45.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBDBDBD),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProduccionPreview() {
    AppLorentinaTheme {
        // En un caso real, necesitarías un MockViewModel para que el Preview funcione
    }
}