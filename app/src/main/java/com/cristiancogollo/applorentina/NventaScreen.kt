package me.oscarsanchez.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 🎨 Colores personalizados
val ColorVerdeOscuro = Color(0xFFB5CC00)
val ColorVerdeClaroBoton = Color(0xFFC2D500)
val ColorGrisTexto = Color(0xFF5C5C5C)
val ColorFondoCard = Color(0xFFF8F8F8)


// =================================================================
// 1. PANTALLA PRINCIPAL DE LA VENTA (Simula el diálogo flotante)
// =================================================================

@Composable
fun NventaDialogScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondoCard)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        NventaDialogContent()
    }
}

@Composable
fun NventaDialogContent() {
    // Variables de estado (sin cambios)
    var nombreCliente by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var municipio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var productos by remember { mutableStateOf("") }

    var isDetalSelected by remember { mutableStateOf(true) }
    var isSpecialPurchase by remember { mutableStateOf(false) } // Estado para Compra Especial

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
                text = "AGREGAR VENTA",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorGrisTexto.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. NOMBRE CLIENTE (Sin cambios)
            InputFieldWithIcon(
                value = nombreCliente,
                onValueChange = { nombreCliente = it },
                placeholder = "NOMBRE CLIENTE....",
                icon = Icons.Outlined.Person
            )

            // Etiqueta "AGREGAR CLIENTE NUEVO" (Sin cambios)
            Text(
                text = "AGREGAR CLIENTE NUEVO",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorVerdeClaroBoton,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp)
            )

            // 2. CAMPO AGREGAR PRODUCTOS
            InputFieldWithIcon(
                value = productos,
                onValueChange = { productos = it },
                placeholder = "AGREGAR PRODUCTOS....",
                icon = Icons.Default.AddShoppingCart,
                // LÓGICA DE INHABILITACIÓN
                readOnly = isSpecialPurchase,
                modifier = Modifier.fillMaxWidth()
            )

            // 3. SWITCH COMPRA ESPECIAL (Movido debajo, alineado a la derecha)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.Start, // Alinea el switch a la derecha
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isSpecialPurchase,
                    onCheckedChange = { isSpecialPurchase = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ColorVerdeOscuro,
                        checkedTrackColor = ColorVerdeClaroBoton,
                        uncheckedThumbColor = ColorGrisTexto.copy(alpha = 0.5f),
                        uncheckedTrackColor = ColorGrisTexto.copy(alpha = 0.2f)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "COMPRA ESPECIAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorGrisTexto,
                )
            }

            // 4. PRECIO Y FECHA (En una fila, sin cambios)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InputFieldWithIcon(
                    value = precio,
                    onValueChange = { precio = it },
                    placeholder = "PRECIO...",
                    icon = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f)
                )

                InputFieldWithIcon(
                    value = fecha,
                    onValueChange = { fecha = it },
                    placeholder = "FECHA...",
                    icon = Icons.Default.CalendarToday,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // 5. DEPARTAMENTO (Dropdown) (Sin cambios)
            DropdownField(
                value = departamento,
                onValueChange = { departamento = it },
                placeholder = "DEPARTAMENTO...."
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 6. MUNICIPIO (Dropdown) (Sin cambios)
            DropdownField(
                value = municipio,
                onValueChange = { municipio = it },
                placeholder = "MUNICIPIO..."
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 7. DESCRIPCIÓN (Sin cambios)
            InputFieldWithIcon(
                value = descripcion,
                onValueChange = { descripcion = it },
                placeholder = "DESCRIPCIÓN...",
                icon = null,
                singleLine = false,
                minLines = 3
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 8. Botones DETAL y POR MAYOR (Sin cambios)
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

            // 9. Botón AGREGAR (Sin cambios)
            Button(
                onClick = { /* Lógica de AGREGAR VENTA */ },
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
// COMPONENTES REUTILIZABLES (ACTUALIZADOS)
// =================================================================

@Composable
fun InputFieldWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
    singleLine: Boolean = true,
    minLines: Int = 1,
    // Añadimos el parámetro readOnly (para deshabilitar el campo)
    readOnly: Boolean = false
) {
    // Determinar los colores del borde según el estado
    val borderColor = if (readOnly) Color.LightGray else ColorVerdeClaroBoton
    val textColor = if (readOnly) Color.Gray else ColorGrisTexto

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.7f), fontSize = 14.sp) },
        leadingIcon = icon?.let {
            {
                Icon(
                    it,
                    contentDescription = null,
                    tint = borderColor, // El color del ícono refleja el estado
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        singleLine = singleLine,
        minLines = minLines,
        readOnly = readOnly, // Aplicamos el estado de solo lectura aquí
        textStyle = LocalTextStyle.current.copy(color = textColor), // Ajustamos el color del texto
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            cursorColor = ColorVerdeOscuro,
            focusedContainerColor = if (readOnly) ColorFondoCard else Color.White,
            unfocusedContainerColor = if (readOnly) ColorFondoCard else Color.White,
            disabledContainerColor = ColorFondoCard // Color de fondo cuando está deshabilitado
        )
    )
}

// (DropdownField y ActionButton se mantienen igual)
@Composable
fun DropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.7f), fontSize = 14.sp) },
        trailingIcon = {
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = ColorVerdeClaroBoton
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        readOnly = true,
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
fun NventaDialogPreview() {
    Surface(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.fillMaxSize()) {
        NventaDialogScreen()
    }
}