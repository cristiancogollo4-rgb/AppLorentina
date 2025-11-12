package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

// =================================================================
// 🎨 COLORES DE REFERENCIA
// =================================================================
val ColorGrisBoton = Color(0xFFBDBDBD)
val ColorCafeTexto = Color(0xFF6A4E23)
val ColorVerdeIcono = Color(0xFFC2D500)

val MODELOS = listOf("Romanas", "Clasicas", "Zaras")

// Asumo que PRODUCCION_STATES está definido en otro archivo y es accesible

@Composable
fun AgregarTareaScreenAdmin(
    onBackClick: () -> Unit = {},
    navTo: (String) -> Unit = {},
    viewModel: ProduccionViewModel = viewModel()
) {
    var referencia by remember { mutableStateOf("") }
    var expandedModelo by remember { mutableStateOf(false) }
    var modeloSeleccionado by remember { mutableStateOf(MODELOS.first()) }
    var color by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }

    // 🟢 Nuevos estados para Precios (usamos String para el TextField)
    var precioDetalString by remember { mutableStateOf("180000") } // Default para Romanas/Clasicas
    var precioMayorString by remember { mutableStateOf("102000") } // Default para Romanas/Clasicas

    // 🟢 Lógica para actualizar precios y ViewModel según el modelo
    val updatePricesBasedOnModel: (String) -> Unit = { selectedModel ->
        val (detal, mayor) = when (selectedModel) {
            "Zaras" -> "195000" to "116000"
            else -> "180000" to "102000"
        }

        // Actualizar estados locales (UI)
        precioDetalString = detal
        precioMayorString = mayor

        // Actualizar ViewModel (convierte a Double internamente)
        viewModel.updateNombre(selectedModel)
        viewModel.updatePrecioDetal(detal)
        viewModel.updatePrecioMayor(mayor)
        viewModel.updateDescripcion(descripcion) // Actualizar descripción también
        viewModel.updateReferencia(referencia)
        viewModel.updateColor(color)
    }

    // Ejecutar la lógica inicial al componerse para establecer los valores por defecto en el ViewModel
    LaunchedEffect(Unit) {
        updatePricesBasedOnModel(modeloSeleccionado)
    }


    // 🟢 ESTADO LOCAL: Mapa para manejar el stock por talla (35-42)
    val tallasRange = remember { (35..42).map { it.toString() } }
    val stockInputs = viewModel.formState.collectAsState().value.stockPorTalla

    // Estado del proceso
    var expandedEstado by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf(PRODUCCION_STATES.first().replaceFirstChar { it.uppercase() }) } // Inicia en "Corte"
    val estados = remember { PRODUCCION_STATES.map { it.replaceFirstChar { it.uppercase() } } }

    val formState by viewModel.formState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🩶 Barra superior gris con flecha
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorGrisBoton)
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

        Spacer(modifier = Modifier.height(15.dp))

        // 🟤 Título principal
        Text(
            text = "AGREGAR TAREA",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ColorCafeTexto,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Registra una nueva referencia, color y stock de producción",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Nombre/Modelo
        Text(
            text = "Modelo:",
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 30.dp, bottom = 6.dp)
        )

        Box(modifier = Modifier.fillMaxWidth(0.85f)) {
            Button(
                onClick = { expandedModelo = !expandedModelo },
                colors = ButtonDefaults.buttonColors(containerColor = ColorGrisBoton),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(modeloSeleccionado, color = Color.White)
                Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
            }

            DropdownMenu(
                expanded = expandedModelo,
                onDismissRequest = { expandedModelo = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                MODELOS.forEach { modelo ->
                    DropdownMenuItem(
                        text = { Text(modelo) },
                        onClick = {
                            modeloSeleccionado = modelo
                            expandedModelo = false
                            // 🟢 Llamada a la función de actualización de precios y ViewModel
                            updatePricesBasedOnModel(modelo)
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Campo Referencia
        OutlinedTextField(
            value = referencia,
            onValueChange = {
                referencia = it
                viewModel.updateReferencia(it)
            },
            placeholder = { Text("Referencia") },
            leadingIcon = { Icon(Icons.Default.Style, null, tint = ColorVerdeIcono) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorGrisBoton,
                unfocusedBorderColor = ColorGrisBoton
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo Color
        OutlinedTextField(
            value = color,
            onValueChange = {
                color = it
                viewModel.updateColor(it)
            },
            placeholder = { Text("Color...") },
            leadingIcon = { Icon(Icons.Default.Brush, null, tint = ColorVerdeIcono) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorGrisBoton,
                unfocusedBorderColor = ColorGrisBoton
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(10.dp))
        // 🟢 Campo URL de Imagen (Opcional)
        OutlinedTextField(
            value = imagenUrl,
            onValueChange = {
                imagenUrl = it
                viewModel.updateImagenUrl(it) // Llamada al nuevo método del ViewModel
            },
            placeholder = { Text("URL de la Imagen (Opcional)") },
            leadingIcon = { Icon(Icons.Default.Link, null, tint = ColorVerdeIcono) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorGrisBoton,
                unfocusedBorderColor = ColorGrisBoton
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🟢 Campo Descripción (Opcional)
        OutlinedTextField(
            value = descripcion,
            onValueChange = {
                descripcion = it
                viewModel.updateDescripcion(it)
            },
            placeholder = { Text("Descripción (Opcional)") },
            leadingIcon = { Icon(Icons.Default.Description, null, tint = ColorVerdeIcono) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorGrisBoton,
                unfocusedBorderColor = ColorGrisBoton
            ),
            shape = RoundedCornerShape(8.dp),
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // 🟢 SECCIÓN DE PRECIOS
        Text(
            text = "Precios:",
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 30.dp, bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Precio Detal
            OutlinedTextField(
                value = precioDetalString,
                onValueChange = {
                    precioDetalString = it.filter { char -> char.isDigit() } // Solo números
                    viewModel.updatePrecioDetal(precioDetalString)
                },
                placeholder = { Text("Detal") },
                label = { Text("Precio Detal") },
                leadingIcon = { Text("$", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorGrisBoton,
                    unfocusedBorderColor = ColorGrisBoton
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Precio Mayor
            OutlinedTextField(
                value = precioMayorString,
                onValueChange = {
                    precioMayorString = it.filter { char -> char.isDigit() } // Solo números
                    viewModel.updatePrecioMayor(precioMayorString)
                },
                placeholder = { Text("Mayor") },
                label = { Text("Precio Mayor") },
                leadingIcon = { Text("$", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorGrisBoton,
                    unfocusedBorderColor = ColorGrisBoton
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        // 🟢 SECCIÓN DE STOCK POR TALLA
        Text(
            text = "Stock Inicial por Talla (35-42):",
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 30.dp, bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Columna 1 de Tallas (35-38)
            Column(modifier = Modifier.weight(1f)) {
                tallasRange.take(4).forEach { talla ->
                    StockInputRow(
                        talla = talla,
                        stock = stockInputs[talla] ?: "",
                        onStockChange = { newValue ->
                            viewModel.updateStockForTalla(talla, newValue)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Columna 2 de Tallas (39-42)
            Column(modifier = Modifier.weight(1f)) {
                tallasRange.drop(4).forEach { talla ->
                    StockInputRow(
                        talla = talla,
                        stock = stockInputs[talla] ?: "",
                        onStockChange = { newValue ->
                            viewModel.updateStockForTalla(talla, newValue)
                        }
                    )
                }
            }
        }

        // Estado
        Text(
            text = "Estado inicial del proceso:",
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
                colors = ButtonDefaults.buttonColors(containerColor = ColorGrisBoton),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Text(estadoSeleccionado, color = Color.White)
                Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
            }

            DropdownMenu(
                expanded = expandedEstado,
                onDismissRequest = { expandedEstado = false }
            ) {
                estados.forEach { e ->
                    DropdownMenuItem(
                        text = { Text(text = e) },
                        onClick = {
                            estadoSeleccionado = e
                            expandedEstado = false
                            // Nota: El estado se actualiza en el ViewModel solo en crearProducto
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 🔘 Botón ENVIAR → Guarda en Firestore
        Button(
            onClick = {
                viewModel.crearProducto()
            },
            colors = ButtonDefaults.buttonColors(containerColor = ColorGrisBoton),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(55.dp)
                .shadow(4.dp, RoundedCornerShape(10.dp))
        ) {
            Text("ENVIAR", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🔹 Mensaje de confirmación/error
        formState.mensaje?.let { mensaje ->
            Text(
                text = mensaje,
                color = if (mensaje.contains("Error")) Color.Red else Color(0xFF4CAF50),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// -----------------------------------------------------------------
// COMPONENTE AUXILIAR PARA LA ENTRADA DE STOCK
// -----------------------------------------------------------------
@Composable
fun StockInputRow(talla: String, stock: String, onStockChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Talla $talla:", modifier = Modifier.width(60.dp), color = Color.Gray, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = stock,
            onValueChange = {
                // Acepta solo números y limita la longitud si es necesario
                if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                    onStockChange(it)
                }
            },
            modifier = Modifier.fillMaxWidth().height(40.dp), // Ajuste de altura para hacerlo compacto
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorGrisBoton,
                unfocusedBorderColor = ColorGrisBoton.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(4.dp),
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AgregarTareaPreview() {
    AppLorentinaTheme {
        AgregarTareaScreenAdmin()
    }
}