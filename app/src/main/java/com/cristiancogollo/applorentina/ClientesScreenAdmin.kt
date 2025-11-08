package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

@Composable
fun ClientesScreenAdmin(
    onBackClick: () -> Unit = {} // Callback para la flecha de retroceso
) {
    var searchText by remember { mutableStateOf("") }

    // Datos de ejemplo
    val clientes = listOf(
        Triple("JUAN M.", "1097912", "3134567"),
        Triple("OSCAR S.", "1098574", "3145697"),
        Triple("JUAN C.", "10995632", "3564792"),
        Triple("DAVID D.", "91227055", "3156489")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🟩 Barra superior gris con flecha y logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFBDBDBD))
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            // 🔙 Botón de retroceso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }

            // 🖼️ Logo
            Image(
                painter = painterResource(id = R.drawable.lorenita),
                contentDescription = "Logo Lorentina",
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // 🟤 Título
        Text(
            text = "CLIENTES",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🔍 Buscador
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(45.dp)
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color(0xFFBDBDBD),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) { innerTextField ->
                    if (searchText.isEmpty()) {
                        Text("BUSCAR CLIENTES", color = Color(0xFFBDBDBD), fontSize = 14.sp)
                    }
                    innerTextField()
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🧾 Cabecera
        Row(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("NOMBRE", fontWeight = FontWeight.Bold, color = Color.Gray)
            Text("C.C", fontWeight = FontWeight.Bold, color = Color.Gray)
            Text("TELÉFONO", fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🔹 Lista de clientes
        clientes.forEach { (nombre, cc, telefono) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(vertical = 4.dp)
                    .shadow(3.dp, RoundedCornerShape(10.dp))
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(cc, fontSize = 13.sp)
                    Text(telefono, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        // 🩶 Botón AGREGAR
        Button(
            onClick = { /* Acción futura */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
                .shadow(6.dp, RoundedCornerShape(12.dp))
        ) {
            Text(
                text = "AGREGAR",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(35.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ClientesPreview() {
    AppLorentinaTheme {
        ClientesScreenAdmin()
    }
}

