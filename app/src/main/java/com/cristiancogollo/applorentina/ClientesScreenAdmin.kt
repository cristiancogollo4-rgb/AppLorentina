package com.cristiancogollo.applorentina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.unit.LayoutDirection // Necesario para calcular padding start/end

@Composable
fun ClientesScreenAdmin(
    onBackClick: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var clientes by remember { mutableStateOf(listOf<Cliente>()) }

    // 🔥 Cargar clientes desde Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("Clientes").get().await()
        clientes = snapshot.documents.mapNotNull { it.toObject(Cliente::class.java) }
    }

    // 🔹 Filtro de búsqueda
    val filteredClientes = clientes.filter {
        it.nombreApellido.contains(searchText, ignoreCase = true)
    }

    // 1. **Envuelve toda la pantalla en un Scaffold**
    Scaffold(
        // 2. **bottomBar: Aquí colocamos el botón fijo "AGREGAR"**
        bottomBar = {
            // 🩶 Botón AGREGAR - Ahora es parte del BottomBar fijo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 35.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Ajustado para ser más ancho pero con padding
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
            }
        }
    ) { paddingValues -> // 3. **Recibe el padding necesario para el contenido**

        // 4. **Contenido principal (todo lo que debe hacer scroll)**
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                // 5. **CORRECCIÓN:** Aplicamos solo el padding inferior, ignorando el superior
                .padding(
                    top = 0.dp, // <-- ¡CAMBIO CLAVE! Esto elimina el espacio blanco superior
                    bottom = paddingValues.calculateBottomPadding(),
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🟩 Barra superior (Se mantiene igual)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFBDBDBD))
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                // 🔙 Flecha atrás
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

                Image(
                    // ASUME QUE R.drawable.lorenita EXISTE
                    painter = painterResource(id = R.drawable.lorenita),
                    contentDescription = "Logo Lorentina",
                    modifier = Modifier
                        .height(180.dp)
                        .width(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

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

            // 6. **LazyColumn para la lista (scroll)**
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 10.dp), // Espacio al final de la lista
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(filteredClientes) { cliente ->
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
                            // ASUME QUE Cliente tiene propiedades nombreApellido, cedula, telefono
                            Text(cliente.nombreApellido, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(cliente.cedula.toString(), fontSize = 13.sp)
                            Text(cliente.telefono.toString(), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }

    // 🪟 Diálogo de agregar cliente (Se mantiene igual)
    if (showAddDialog) {
        // ASUME QUE AgregarClienteDialog y sus parámetros son correctos
        AgregarClienteDialog(
            onDismiss = { showAddDialog = false },
            onSave = { nombre, cedula, telefono, correo, departamento, municipio, tipo ->
                val db = FirebaseFirestore.getInstance()
                val cliente = hashMapOf(
                    "nombreApellido" to nombre,
                    "cedula" to cedula.toLongOrNull(),
                    "telefono" to telefono.toLongOrNull(),
                    "correo" to correo,
                    "departamento" to departamento,
                    "municipio" to municipio,
                    "tipoCliente" to tipo
                )
                db.collection("Clientes").add(cliente)
                showAddDialog = false
            }
        )
    }
}