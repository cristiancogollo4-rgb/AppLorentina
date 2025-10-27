package me.oscarsanchez.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 🎨 Colores personalizados
val ColorVerdeOscuro = Color(0xFFB5CC00)
val ColorVerdeClaroBoton = Color(0xFFC2D500)
val ColorGrisTexto = Color(0xFF5C5C5C)
val ColorFondoCard = Color(0xFFF8F8F8)

// 🔽 Campo desplegable reutilizable
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label, color = ColorGrisTexto) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            readOnly = true
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// 🔘 Botón de acción reutilizable
@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeClaroBoton),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// 🌿 Pantalla principal
@Composable
fun NventaScreen() {
    var selectedProducto by remember { mutableStateOf("Seleccione un producto") }
    var selectedCliente by remember { mutableStateOf("Seleccione un cliente") }
    var cantidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nueva Venta",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorVerdeOscuro)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta de formulario
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorFondoCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DropdownField(
                            label = "Producto",
                            options = listOf("Chocolate", "Leche", "Pan", "Café"),
                            selectedOption = selectedProducto,
                            onOptionSelected = { selectedProducto = it }
                        )

                        DropdownField(
                            label = "Cliente",
                            options = listOf("Juan", "Ana", "Luis", "Carla"),
                            selectedOption = selectedCliente,
                            onOptionSelected = { selectedCliente = it }
                        )

                        OutlinedTextField(
                            value = cantidad,
                            onValueChange = { cantidad = it },
                            label = { Text("Cantidad") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = precio,
                            onValueChange = { precio = it },
                            label = { Text("Precio unitario") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Botón Guardar
                ActionButton(
                    text = "Guardar Venta",
                    onClick = { /* Aquí va la acción de guardar */ }
                )
            }
        }
    }
}

// 🚀 Actividad principal
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NventaScreen()
        }
    }
}