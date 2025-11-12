package com.cristiancogollo.applorentina

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DetalleClienteDialog(
    navController: NavController,
    nombre: String,
    cedula: String,
    telefono: String,
    correo: String,
    departamento: String,
    municipio: String,
    tipoCliente: String
) {
    AlertDialog(
        onDismissRequest = { navController.popBackStack() },
        confirmButton = {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB5CC00))
            ) {
                Text("Cerrar", color = Color.White)
            }
        },
        title = {
            Text(
                text = nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF5C5C5C)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Cédula: $cedula")
                Text("Teléfono: $telefono")
                Text("Correo: $correo")
                Text("Departamento: $departamento")
                Text("Municipio: $municipio")
                Text(
                    "Tipo de cliente: ${if (tipoCliente.toBoolean()) "Detal" else "Por Mayor"}",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        containerColor = Color.White,
        tonalElevation = 8.dp
    )
}

/**
 * 🔹 Nuevo método auxiliar: permite abrir este diálogo fácilmente desde cualquier parte
 * pasando directamente el objeto Cliente completo (sin cambiar tu estructura actual).
 */
@Composable
fun DetalleClienteDialogFromObject(
    navController: NavController,
    cliente: Cliente
) {
    DetalleClienteDialog(
        navController = navController,
        nombre = cliente.nombreApellido,
        cedula = cliente.cedula.toString(),
        telefono = cliente.telefono.toString(),
        correo = cliente.correo,
        departamento = cliente.departamento,
        municipio = cliente.municipio,
        tipoCliente = cliente.tipoCliente.toString()
    )
}
