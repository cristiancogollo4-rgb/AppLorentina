package com.cristiancogollo.applorentina


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

@Composable
fun BuscarClienteScreen() {
    val colorVerde = Color(0xFFB5CC00)
    var searchQuery by remember { mutableStateOf("") }

    val clientes = listOf(
        "Juan Perez", "Maria Gomez", "Carlos Ruiz", "Laura Sanchez", "Juan Perez"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado verde
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFC2D500))
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.lorenita),
                contentDescription = "Logo Lorentina",
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp)
            )
        }


        Spacer(modifier = Modifier.height(20.dp))

        // Título
        Text(
            text = "BUSCAR CLIENTE",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5C5C5C)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar cliente...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filtros
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterButton("NOMBRE")
            FilterButton("C.C")
            FilterButton("TELÉFONO")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de clientes
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(clientes) { cliente ->
                ClienteCard(nombre = cliente)
            }
        }

        // Botón flotante
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { /* TODO: agregar cliente */ },
                containerColor = colorVerde,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar cliente")
            }
        }
    }
}

@Composable
fun FilterButton(text: String) {
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEFF5C9),
            contentColor = Color(0xFF8AA100)
        ),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ClienteCard(nombre: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(nombre, fontWeight = FontWeight.Bold)
                Text("28/09/2025", fontSize = 12.sp, color = Color.Gray)
            }
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB5CC00),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("VER DETALLES", fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BuscarClientePreview() {
    AppLorentinaTheme {
        BuscarClienteScreen()
    }
}