package com.cristiancogollo.applorentina

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

// Definición de colores del proyecto
val ColorVerdeOscuro = Color(0xFFB5CC00)
val ColorVerdeClaroBoton = Color(0xFFC2D500)
val ColorGrisTexto = Color(0xFF5C5C5C)
val ColorFondoCard = Color(0xFFF8F8F8) // Usado para simular el fondo

@Composable
fun AgregarClienteScreen() {
    // Usamos un Box y Card para simular la elevación y el fondo blanco del mockup
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(20.dp),
        contentAlignment = Alignment.Center // Centrar el "diálogo"
    ) {
        AgregarClienteDialogContent()
    }
}

@Composable
fun AgregarClienteDialogContent() {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f) // Ocupa la mayor parte del ancho
            .wrapContentHeight(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Simula una ventana flotante
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
                color = ColorGrisTexto.copy(alpha = 0.8f) // Color más oscuro y fuerte
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campos de entrada
            // NOMBRE CLIENTE
            InputFieldWithIcon(
                placeholder = "NOMBRE CLIENTE....",
                icon = Icons.Outlined.Person // Icono de persona
            )
            Spacer(modifier = Modifier.height(10.dp))

            // C.C.
            InputFieldWithIcon(
                placeholder = "C.C....",
                // Usamos un icono de Badge para simular C.C. o una identificación
                icon = Icons.Default.Badge
            )
            Spacer(modifier = Modifier.height(10.dp))

            // TELÉFONO
            InputFieldWithIcon(
                placeholder = "TELÉFONO....",
                icon = Icons.Outlined.Phone // Icono de teléfono
            )
            Spacer(modifier = Modifier.height(10.dp))

            // CORREO
            InputFieldWithIcon(
                placeholder = "CORREO...",
                icon = Icons.Default.MailOutline // Icono de correo
            )
            Spacer(modifier = Modifier.height(10.dp))

            // DEPARTAMENTO (Spinner/Dropdown)
            DropdownField(
                placeholder = "DEPARTAMENTO...."
            )
            Spacer(modifier = Modifier.height(10.dp))

            // MUNICIPIO (Spinner/Dropdown)
            DropdownField(
                placeholder = "MUNICIPIO..."
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Botones DETAL y POR MAYOR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // DETAL (Seleccionado en el mockup)
                ActionButton(
                    text = "DETAL",
                    isSelected = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))

                // POR MAYOR
                ActionButton(
                    text = "POR MAYOR",
                    isSelected = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón AGREGAR (Botón principal)
            Button(
                onClick = { /* Lógica de AGREGAR */ },
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
// COMPONENTES REUTILIZABLES
// =================================================================

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
                modifier = Modifier.size(32.dp)// Color verde para los iconos
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorVerdeClaroBoton,
            unfocusedBorderColor = ColorVerdeClaroBoton, // Borde siempre verde claro
            cursorColor = ColorVerdeOscuro,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        )
    )
}

@Composable
fun DropdownField(placeholder: String) {
    OutlinedTextField(
        value = "", // Sin lógica
        onValueChange = { },
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
        readOnly = true, // Simula que es un campo de selección
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
fun ActionButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    val containerColor = if (isSelected) ColorVerdeOscuro else Color.White
    val contentColor = if (isSelected) Color.White else ColorGrisTexto.copy(alpha = 0.8f)
    val borderColor = if (isSelected) ColorVerdeOscuro else ColorVerdeOscuro.copy(alpha = 0.5f) // Borde sutil verde

    Button(
        onClick = { },
        modifier = modifier
            .height(50.dp)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp)), // Borde más pronunciado
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
    // Usamos Surface para simular un fondo para el diálogo
    Surface(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.fillMaxSize()) {
        AgregarClienteScreen()
    }
}

