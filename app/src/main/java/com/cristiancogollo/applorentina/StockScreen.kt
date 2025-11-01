import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.cristiancogollo.applorentina.R

// Asegúrate de que tu ruta R es accesible
// import com.example.app_lorentina.R

@Composable
fun StockScreen(onBackClick: () -> Unit = {}) {
    val colorVerdeClaro = Color(0xFFC2D500)
    val colorVerdeOscuro = Color(0xFFB5CC00)
    val colorGrisTexto = Color(0xFF5C5C5C)

    var searchQuery by remember { mutableStateOf("") }

    // Datos de inventario de ejemplo (el 0 simula un R.drawable.id)
    val productos = listOf(
        Pair("1028 COÑAC", 0),
        Pair("1006 BLANCO", 0),
        Pair("1046 BLANCO X GOYA", 0)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF))
            .padding(top = 16.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Contenedor principal para el contenido blanco
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Encabezado verde (Logo Lorentina)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorVerdeClaro, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    IconButton(onClick = onBackClick) { // Llama a la acción de volver
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
                    // Nota: Si R.drawable.lorenita falla, comenta el bloque Image.
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

                // 2. Título de la Pantalla
                Text(
                    text = "INVENTARIO",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorGrisTexto
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Barra de búsqueda (Buscar Referencia) - BORDE UNIFICADO
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("BUSCAR REFERENCIA....", color = Color.Gray.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = colorVerdeClaro) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorVerdeClaro,
                        unfocusedBorderColor = colorVerdeClaro, // <-- CAMBIO: Borde verde constante
                        cursorColor = colorVerdeOscuro
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Filtros de Stock
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // StockFilterButton AHORA ACEPTA modifier.weight()
                    StockFilterButton(text = "REFE.", isSelected = true, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    StockFilterButton(text = "COLOR", isSelected = false, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    StockFilterButton(text = "TALLA", isSelected = false, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Lista de Inventario (LazyColumn)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(productos) { (referencia, imageRes) ->
                        StockCard(
                            referencia = referencia,
                            colorVerdeClaro = colorVerdeClaro,
                            colorGrisTexto = colorGrisTexto
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// COMPONENTES AUXILIARES
// -----------------------------------------------------------------

@Composable
fun StockFilterButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier) { // <-- FIRMA CORREGIDA
    val containerColor = if (isSelected) Color(0xFFB5CC00) else Color(0xFFEFF5C9)
    val contentColor = if (isSelected) Color.White else Color(0xFF8AA100)
    val borderColor = if (isSelected) Color(0xFFB5CC00) else Color(0xFFEFF5C9)
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(10.dp), // Ajuste a 10.dp
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(borderColor)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp), // Ajuste de padding
        modifier = modifier // <-- MODIFIER APLICADO CORRECTAMENTE
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TallaStockDisplay(talla: String, stock: String, colorGrisTexto: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Talla (Etiqueta fija) y Stock (Valor que viene de la DB)
        Text(
            // Formato "TALLA : STOCK"
            text = "$talla : $stock",
            fontSize = 14.sp,
            color = colorGrisTexto,
            fontWeight = FontWeight.Normal,
        )
    }
}


@Composable
fun StockCard(referencia: String, colorVerdeClaro: Color, colorGrisTexto: Color) {
    // Definición de stock simulada para la visualización de solo lectura
    val stockSimulado = mapOf(
        "35" to "3", "36" to "3", "37" to "3", "38" to "3", "39" to "3",
        "40" to "3", "41" to "3", "42" to "3"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            //.border(2.dp, colorVerdeClaro, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna de Tallas y Stock
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    referencia,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = colorGrisTexto
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Distribución de Tallas (Texto de solo lectura)
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Columna 1 (Tallas 35-39)
                    Column(modifier = Modifier.weight(1f)) {
                        TallaStockDisplay("35", stockSimulado["35"]!!, colorGrisTexto)
                        TallaStockDisplay("36", stockSimulado["36"]!!, colorGrisTexto)
                        TallaStockDisplay("37", stockSimulado["37"]!!, colorGrisTexto)
                        TallaStockDisplay("38", stockSimulado["38"]!!, colorGrisTexto)
                        TallaStockDisplay("39", stockSimulado["39"]!!, colorGrisTexto)
                    }

                    // Columna 2 (Tallas 40-42)
                    Column(modifier = Modifier.weight(1f)) {
                        TallaStockDisplay("40", stockSimulado["40"]!!, colorGrisTexto)
                        TallaStockDisplay("41", stockSimulado["41"]!!, colorGrisTexto)
                        TallaStockDisplay("42", stockSimulado["42"]!!, colorGrisTexto)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Cuadro Verde de simulación de Imagen desde DB
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(colorVerdeClaro, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                // ...
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StockScreenPreview() {

    Surface(color = Color(0xFFEFEFEF)) { // Usando el color de fondo exterior de tu diseño
        StockScreen()
    }
}