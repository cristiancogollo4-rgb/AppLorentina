package com.cristiancogollo.applorentina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme


@Composable
fun HomeAdmin() {
    // 🟢 Nombre dinámico (puedes cambiarlo o traerlo desde base de datos)
    var userName by remember { mutableStateOf("Angélica") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🟩 Barra superior gris con imagen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFBDBDBD))
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.lorenita), // tu logo
                contentDescription = "Logo Lorentina",
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 🟤 Texto ADMINISTRADOR
        Text(
            text = "ROL ADMINISTRADOR",
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 🟠 Texto de saludo dinámico
        Text(
            text = "¡HOLA, ${userName.uppercase()}!",
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 🔹 Cuadrícula de botones grises
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HomeCardButtom("PRODUCCIÓN", R.drawable.ic_group, iconSize = 90.dp)
                HomeCardButtom("CLIENTES", R.drawable.ic_inventory_2, iconSize = 90.dp)
            }

            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HomeCardButtom("INVENTARIO", R.drawable.ic_shopping_bag, iconSize = 90.dp)
                HomeCardButtom("VENTAS", R.drawable.ic_bar_chart, iconSize = 90.dp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 🟩 Botón inferior (también gris)
        Button(
            onClick = { /* Acción futura */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
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
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "PRODUCCIÓN",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(35.dp))
    }
}

// 🟦 Composable para cada tarjeta (botones intermedios)
@Composable
fun HomeCardButtom(text: String, iconId: Int, iconSize: Dp) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFFBDBDBD), // 🔹 Botones grises
        shadowElevation = 8.dp,
        modifier = Modifier
            .width(165.dp)
            .height(145.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeAdminPreview() {
    AppLorentinaTheme {
        HomeAdmin()
    }
}
