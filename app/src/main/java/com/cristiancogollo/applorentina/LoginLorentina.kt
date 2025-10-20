package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// --- NUEVA ENUMERACIÓN ---
enum class UserRole { VENDEDOR, ADMINISTRADOR }
// -------------------------

@Composable
fun LorentinaLoginScreen(
    onLoginClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    // Definición de Colores
    val ColorLorentinaPrimary = Color(0xFFC7E534)
    val ColorLorentinaFilter = Color(0x99C7E534)
    val ColorGris = Color(0xFFa6a6a6) // Gris medio para las etiquetas

    // Color específico del borde y texto inactivo (Marrón/Tostado)
    val ColorInactiveText = Color(0xFF8B6B38)

    // --- ESTADO DEL SELECTOR ---
    var selectedRole by remember { mutableStateOf(UserRole.VENDEDOR) }
    // --------------------------

    // 1. Contenedor Principal (APLICA fillMaxSize() aquí para que los weights funcionen)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)) // Fondo ligeramente grisáceo de la pantalla
    ) {
        // --- 1. ÁREA SUPERIOR: Imagen y Logo (weight: 1f) ---
        // ... (Tu código para el Box superior) ...
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // 1.1 IMAGEN DE FONDO
            Image(
                painter = painterResource(id = R.drawable.fondologin),
                contentDescription = "Fondo de Calzado Lorentina",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 35.dp,
                            bottomEnd = 35.dp
                        )
                    ), // Ajusta la curva
                contentScale = ContentScale.Crop
            )

            // 1.2 CAPA DE FILTRO CON TRANSPARENCIA
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 35.dp,
                            bottomEnd = 35.dp
                        )
                    ) // Ajusta la curva
                    .background(ColorLorentinaFilter)
            )

            // 1.3 LOGO Y NOMBRE (Superpuestos y centrados)
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-20).dp), // Subido para centrar solo el logo en la parte superior
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lorentinalogo),
                    contentDescription = "Logo Corona Lorentina",
                    modifier = Modifier.size(300.dp) // Reducido para que no ocupe todo el espacio
                )
            }
        }

        // --- 2. ÁREA DE LOGIN: Tarjeta Blanca (weight: 1.5f) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
                .offset(y = (-35).dp)// Offset igual al radio de la esquina para la superposición
                .padding(bottom = 30.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(
                    topStart = 35.dp,
                    topEnd = 35.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 25.dp)
            ) {
                // **COLUMNA DE CONTENIDO (Única y sin anidamiento)**
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Selector de Rol (VENDEDOR / ADMINISTRADOR) ---
                    val ColorSelector = Color(0xFFC2D706)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            // Forma de Cápsula: Usamos 24.dp, que es la mitad de la altura 48.dp
                            .border(1.dp, ColorSelector, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        // VENDEDOR (Activo/Inactivo según el estado)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                // Cambia el color de fondo si está seleccionado
                                .background(if (selectedRole == UserRole.VENDEDOR) ColorSelector else Color.White)
                                // HABILITA LA FUNCIÓN DE SELECCIÓN
                                .clickable { selectedRole = UserRole.VENDEDOR },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VENDEDOR",
                                // Cambia el color del texto si está seleccionado
                                color = if (selectedRole == UserRole.VENDEDOR) Color.White else ColorInactiveText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        // ADMINISTRADOR (Activo/Inactivo según el estado)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                // Cambia el color de fondo si está seleccionado
                                .background(if (selectedRole == UserRole.ADMINISTRADOR) ColorSelector else Color.White)
                                // HABILITA LA FUNCIÓN DE SELECCIÓN
                                .clickable { selectedRole = UserRole.ADMINISTRADOR },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ADMINISTRADOR",
                                // Cambia el color del texto si está seleccionado
                                color = if (selectedRole == UserRole.ADMINISTRADOR) Color.White else ColorInactiveText,
                                fontWeight = FontWeight.Bold, // Usamos Bold para coincidir con el diseño
                                fontSize = 14.sp
                            )
                        }
                    }

                    // --- Campos de Usuario y Contraseña (usando ColorSelector y ColorGris) ---
                    Spacer(modifier = Modifier.height(30.dp))

                    OutlinedTextField(
                        value = "", onValueChange = {},
                        label = { Text("USUARIO", fontSize = 14.sp, color = ColorGris) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person, contentDescription = "Usuario",
                                tint = ColorSelector, modifier = Modifier.size(32.dp)
                            ) // Corregido size
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorSelector, unfocusedBorderColor = ColorSelector
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = "", onValueChange = {},
                        label = { Text("CONTRASEÑA", fontSize = 14.sp, color = ColorGris) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock, contentDescription = "Contraseña",
                                tint = ColorSelector, modifier = Modifier.size(32.dp)
                            ) // Corregido size
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorSelector, unfocusedBorderColor = ColorSelector
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Botón de Ingresar ---
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorSelector)
                    ) {
                        Text(
                            text = "INGRESAR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Enlace de Contraseña Olvidada ---
                    TextButton(onClick = onForgotPasswordClick) {
                        Text(
                            text = "¿OLVIDASTE LA CONTRASEÑA?",
                            color = Color(0xFF666666),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}



@Preview

@Composable

fun PreviewLorentinaLogin() {

    LorentinaLoginScreen()
}
