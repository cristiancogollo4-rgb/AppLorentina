package com.cristiancogollo.applorentina

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

enum class UserRole { VENDEDOR, ADMINISTRADOR }

@Composable
fun LorentinaLoginScreen(
    onLoginClick: (UserRole) -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    val ColorLorentinaFilter = Color(0x99C7E534)
    val ColorGris = Color(0xFFa6a6a6)
    val ColorInactiveText = Color(0xFF8B6B38)
    val ColorSelector = Color(0xFFC2D706)

    var selectedRole by remember { mutableStateOf(UserRole.VENDEDOR) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        // Parte superior (imagen + logo)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondologin),
                contentDescription = "Fondo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 35.dp, bottomEnd = 35.dp)),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 35.dp, bottomEnd = 35.dp))
                    .background(ColorLorentinaFilter)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-20).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lorentinalogo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(300.dp)
                )
            }
        }

        // Área de login
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
                .offset(y = (-35).dp)
                .padding(bottom = 30.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 25.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Selector de rol
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(1.dp, ColorSelector, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (selectedRole == UserRole.VENDEDOR) ColorSelector else Color.White)
                                .clickable { selectedRole = UserRole.VENDEDOR },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VENDEDOR",
                                color = if (selectedRole == UserRole.VENDEDOR) Color.White else ColorInactiveText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (selectedRole == UserRole.ADMINISTRADOR) ColorSelector else Color.White)
                                .clickable { selectedRole = UserRole.ADMINISTRADOR },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ADMINISTRADOR",
                                color = if (selectedRole == UserRole.ADMINISTRADOR) Color.White else ColorInactiveText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Campo correo
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null // limpia error al escribir
                        },
                        label = { Text("CORREO", fontSize = 14.sp, color = ColorGris) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Correo",
                                tint = ColorSelector,
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorSelector,
                            unfocusedBorderColor = ColorSelector
                        )
                    )
                    // Mensaje de error debajo del correo
                    if (emailError != null) {
                        Text(
                            text = emailError!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, start = 4.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null // limpia error al escribir
                        },
                        label = { Text("CONTRASEÑA", fontSize = 14.sp, color = ColorGris) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Contraseña",
                                tint = ColorSelector,
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorSelector,
                            unfocusedBorderColor = ColorSelector
                        )
                    )
                    // Mensaje de error debajo de la contraseña
                    if (passwordError != null) {
                        Text(
                            text = passwordError!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, start = 4.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón ingresar con validación
                    Button(
                        onClick = {
                            // Limpia errores previos
                            emailError = null
                            passwordError = null

                            if (email.isEmpty()) {
                                emailError = "Por favor ingresa el correo"
                                return@Button
                            }
                            if (password.isEmpty()) {
                                passwordError = "Por favor ingresa la contraseña"
                                return@Button
                            }

                            val isValid = when (selectedRole) {
                                UserRole.ADMINISTRADOR -> email.endsWith("@admin.com")
                                UserRole.VENDEDOR -> (
                                        email.endsWith("@gmail.com") ||
                                                email.endsWith("@hotmail.com")
                                        )
                            }

                            if (!isValid) {
                                emailError = "Correo inválido para el rol seleccionado"
                                return@Button
                            }

                            FirebaseAuth.getInstance()
                                .signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Inicio de sesión exitoso",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onLoginClick(selectedRole)
                                    } else {
                                        when (task.exception) {
                                            is FirebaseAuthInvalidUserException -> {
                                                emailError = "El correo no está registrado"
                                            }
                                            is FirebaseAuthInvalidCredentialsException -> {
                                                passwordError = "Contraseña incorrecta"
                                            }
                                            else -> {
                                                passwordError = "Correo o contraseña incorrectos"
                                            }
                                        }
                                    }
                                }
                        },
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
