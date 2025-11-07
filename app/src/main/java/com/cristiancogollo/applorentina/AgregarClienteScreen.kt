import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext // 🌟 Necesario para acceder a Assets
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray // 🌟 Necesario para parsear JSON
import android.util.Log // 🌟 Para manejar errores de lectura de JSON


// Definición de colores del proyecto
val ColorVerdeOscuro = Color(0xFFB5CC00)
val ColorVerdeClaroBoton = Color(0xFFC2D500)
val ColorGrisTexto = Color(0xFF5C5C5C)
val ColorFondoCard = Color(0xFFF8F8F8)

// =================================================================
// 🌟 1. FUNCIÓN PARA LEER EL JSON DESDE ASSETS
// =================================================================

/**
 * Lee el archivo colombia.json desde los assets y lo convierte en un mapa.
 * Estructura: Map<Departamento, List<Municipios>>
 */
fun leerDepartamentos(context: Context): Map<String, List<String>> {
    return try {
        // Accede al archivo usando el AssetManager
        val jsonText = context.assets.open("colombia.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonText)
        val map = mutableMapOf<String, List<String>>()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val departamento = item.getString("departamento")
            val municipiosJson = item.getJSONArray("ciudades")
            val municipios = mutableListOf<String>()

            for (j in 0 until municipiosJson.length()) {
                municipios.add(municipiosJson.getString(j))
            }
            map[departamento] = municipios
        }
        map
    } catch (e: Exception) {
        // En caso de error (archivo no encontrado, JSON inválido, etc.)
        Log.e("LeerJSON", "Error leyendo colombia.json: ${e.message}")
        emptyMap()
    }
}


// =================================================================
// PANTALLAS Y CONTENIDO
// =================================================================

@Composable
fun AgregarClienteScreen() {
    val context = LocalContext.current

    // 🌟 Cargamos los datos del JSON una sola vez
    // Esto es un Map<String, List<String>>
    val departamentosMap = remember { leerDepartamentos(context) }

    // Usamos un Box y Card para simular la elevación y el fondo blanco del mockup
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(20.dp),
        contentAlignment = Alignment.Center // Centrar el "diálogo"
    ) {
        AgregarClienteDialogContent(departamentosMap)
    }
}

@Composable
fun AgregarClienteDialogContent(departamentosMap: Map<String, List<String>>) {
    // 🌟 ESTADOS PARA LOS COMPONENTES FUNCIONALES
    var isDetalSelected by remember { mutableStateOf(true) }
    var departamentoSeleccionado by remember { mutableStateOf("") }
    var municipioSeleccionado by remember { mutableStateOf("") }

    // 🌟 LISTAS DINÁMICAS
    val departamentos = remember { departamentosMap.keys.toList().sorted() }
    val municipios = remember(departamentoSeleccionado) {
        // Filtra los municipios basándose en el departamento seleccionado
        departamentosMap[departamentoSeleccionado] ?: emptyList()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "AGREGAR CLIENTE",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorGrisTexto.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campos de entrada (sin lógica de estado, solo visual)
            InputFieldWithIcon(placeholder = "NOMBRE CLIENTE....", icon = Icons.Outlined.Person)
            Spacer(modifier = Modifier.height(10.dp))
            InputFieldWithIcon(placeholder = "C.C....", icon = Icons.Default.Badge)
            Spacer(modifier = Modifier.height(10.dp))
            InputFieldWithIcon(placeholder = "TELÉFONO....", icon = Icons.Outlined.Phone)
            Spacer(modifier = Modifier.height(10.dp))
            InputFieldWithIcon(placeholder = "CORREO...", icon = Icons.Default.MailOutline)
            Spacer(modifier = Modifier.height(10.dp))

            // 🌟 DEPARTAMENTO (Selector funcional)
            DropdownSelector(
                label = "DEPARTAMENTO....",
                opciones = departamentos,
                seleccionActual = departamentoSeleccionado,
                onSeleccion = { nuevoDepto ->
                    departamentoSeleccionado = nuevoDepto
                    municipioSeleccionado = "" // Resetear municipio al cambiar de departamento
                }
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 🌟 MUNICIPIO (Selector funcional y dependiente)
            DropdownSelector(
                label = "MUNICIPIO...",
                opciones = municipios, // Usa la lista filtrada
                seleccionActual = municipioSeleccionado,
                onSeleccion = { nuevoMuni -> municipioSeleccionado = nuevoMuni },
                // Deshabilitar si no se ha seleccionado un departamento
                isEnabled = departamentoSeleccionado.isNotEmpty()
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Botones DETAL y POR MAYOR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    text = "DETAL",
                    isSelected = isDetalSelected,
                    onClick = { isDetalSelected = true },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                ActionButton(
                    text = "POR MAYOR",
                    isSelected = !isDetalSelected,
                    onClick = { isDetalSelected = false },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón AGREGAR (Botón principal)
            Button(
                onClick = {
                    Log.d("ClienteData", "Departamento: $departamentoSeleccionado, Municipio: $municipioSeleccionado, EsDetal: $isDetalSelected")
                    // Lógica de AGREGAR a Firebase iría aquí
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeOscuro),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(10.dp)
            ) {
                Text(
                    text = "AGREGAR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// =================================================================
// 🌟 2. COMPONENTE DropdownSelector (REEMPLAZA A DropdownField)
// =================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    opciones: List<String>,
    seleccionActual: String,
    onSeleccion: (String) -> Unit,
    isEnabled: Boolean = true // Añadimos el estado de habilitado
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = seleccionActual,
            onValueChange = { },
            placeholder = { Text(label, color = Color.Gray.copy(alpha = 0.7f), fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isEnabled) { expanded = !expanded }, // Abre al tocar
            readOnly = true,
            enabled = isEnabled,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable(enabled = isEnabled) { expanded = !expanded },
                    tint = if (isEnabled) ColorVerdeClaroBoton else Color.Gray
                )
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorVerdeClaroBoton,
                unfocusedBorderColor = ColorVerdeClaroBoton,
                disabledBorderColor = Color.Gray.copy(alpha = 0.5f), // Estilo para deshabilitado
                cursorColor = ColorVerdeOscuro,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.8f) // Ajusta el ancho del menú desplegable
                .background(Color.White)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}


// =================================================================
// COMPONENTES REUTILIZABLES (sin cambios grandes)
// =================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFieldWithIcon(placeholder: String, icon: ImageVector) {
    OutlinedTextField(
        value = "",
        onValueChange = { },
        placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.7f), fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = ColorVerdeClaroBoton,
                modifier = Modifier.size(24.dp)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorVerdeClaroBoton,
            unfocusedBorderColor = ColorVerdeClaroBoton,
            cursorColor = ColorVerdeOscuro,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        )
    )
}

@Composable
fun ActionButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = if (isSelected) ColorVerdeOscuro else Color.White
    val contentColor = if (isSelected) Color.White else ColorGrisTexto.copy(alpha = 0.8f)
    val borderColor = if (isSelected) ColorVerdeOscuro else ColorVerdeOscuro.copy(alpha = 0.5f)

    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

// =================================================================
// PREVIEW
// =================================================================
@Preview(showBackground = true)
@Composable
fun AgregarClientePreview() {
    // Definimos un mapa de ejemplo para el Preview, ya que no puede acceder a Assets
    val sampleMap = mapOf(
        "Antioquia" to listOf("Medellín", "Envigado", "Bello"),
        "Santander" to listOf("Bucaramanga", "Floridablanca", "Girón")
    )
    Surface(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.fillMaxSize()) {
        AgregarClienteDialogContent(sampleMap)
    }
}