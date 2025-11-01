package com.cristiancogollo.applorentina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme


@Composable
fun HomeScreen(navController: NavHostController, onLogoutClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra superior con imagen y Botón de Cerrar Sesión
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFC2D500))
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            // Botón de Cerrar Sesión (Atrás)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart) // Alinear solo a la izquierda
                    .padding(start = 8.dp)
            ) {
                IconButton(onClick = onLogoutClick) { // Llama al callback al hacer clic
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Cerrar Sesión",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.lorenita),
                contentDescription = "Logo Lorentina",
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        // Texto de saludo
        Text(
            text = "¡HOLA, [NOMBRE]!",
            color = Color(0xFF6A4E23),
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Cuadrícula de botones
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 📞 CLIENTES
                HomeCardButton("CLIENTES", R.drawable.ic_group, iconSize = 90.dp,
                    onClick = { navController.navigate(Screen.BuscarCliente.route) })

                // 📦 INVENTARIO
                HomeCardButton("INVENTARIO", R.drawable.ic_inventory_2, iconSize = 90.dp,
                    onClick = { navController.navigate(Screen.Stock.route) })
            }

            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 💰 VENTAS
                HomeCardButton("VENTAS", R.drawable.ic_shopping_bag, iconSize = 90.dp,
                    onClick = { navController.navigate(Screen.Hventas.route) })

                // 📈 ESTADÍSTICAS
                HomeCardButton("ESTADÍSTICAS", R.drawable.ic_bar_chart, iconSize = 90.dp,
                    onClick = { navController.navigate(Screen.Estadisticas.route)})
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón de producción (Usando el onClick del Button)
        Button(
            onClick = { navController.navigate(Screen.Produccion.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2D500)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(80.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.chanc),
                    contentDescription = "Producción",
                    tint = Color.White,
                    modifier = Modifier.size(90.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "PRODUCCIÓN",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(35.dp))
    }
}

@Composable
fun HomeCardButton(text: String, iconId: Int, iconSize: Dp,
                   onClick: () -> Unit) {
    // 💡 CORREGIDO: Se refactoriza para usar el componente Button, que garantiza el manejo de clics.
    Button(
        onClick = onClick, // 👈 Se usa el onClick del Button, que es más robusto
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2D500)),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        contentPadding = PaddingValues(10.dp), // Ajusta el padding interno
        modifier = Modifier
            .width(165.dp)
            .height(145.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomePreview() {
    AppLorentinaTheme {
        HomeScreen(navController = rememberNavController())
    }
}
