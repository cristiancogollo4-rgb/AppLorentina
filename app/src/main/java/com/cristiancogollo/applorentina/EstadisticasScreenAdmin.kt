package com.cristiancogollo.applorentina

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.nativeCanvas
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

@Composable
fun EstadisticasScreenAdmin() {
    // Datos
    val valores = listOf(40f, 30f, 60f, 50f)
    val semanas = listOf("Semana 1", "Semana 2", "Semana 3", "Semana 4")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🩶 Barra superior gris
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFBDBDBD))
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.lorenita),
                contentDescription = "Logo Lorentina",
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🔹 Título
        Text(
            text = "TUS ESTADÍSTICAS",
            color = Color(0xFF6A4E23),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📊 Gráfico de barras
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(240.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(10.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = size.width / 10f
                val spacing = barWidth * 1.3f
                val maxVal = valores.maxOrNull() ?: 0f
                val scale = (size.height - 60f) / maxVal
                val baseY = size.height - 40f

                // Línea base verde
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, baseY),
                    end = Offset(size.width, baseY),
                    strokeWidth = 6f
                )

                // Dibujar barras y etiquetas
                valores.forEachIndexed { index, valor ->
                    val barHeight = valor * scale
                    val left = index * (barWidth + spacing) + spacing / 1.5f
                    val top = baseY - barHeight

                    // Barra
                    drawRoundRect(
                        color = Color(0xFF67E8F9),
                        topLeft = Offset(left, top),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )

                    // Número arriba de la barra
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            valor.toInt().toString(),
                            left + barWidth / 3,
                            top - 8,
                            android.graphics.Paint().apply {
                                textSize = 28f
                                color = android.graphics.Color.BLACK
                                textAlign = android.graphics.Paint.Align.LEFT
                            }
                        )
                    }

                    // Semana debajo
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            semanas[index],
                            left - 10,
                            baseY + 30,
                            android.graphics.Paint().apply {
                                textSize = 26f
                                color = android.graphics.Color.GRAY
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔘 Botones tipo etiqueta (centrados y unidos visualmente)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            EstatButton("#TOTAL DE STOCK")
            EstatButton("#PARES VENDIDOS")
        }

        Spacer(modifier = Modifier.height(25.dp))

        // 🟩 Tarjetas de métricas
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            EstatCard("750", "PARES VENDIDOS\nESTA SEMANA")
            EstatCard("$6.000.000", "GANANCIAS POR\nSEMANA")
        }
    }
}

@Composable
fun EstatButton(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF00BFFF),
        shadowElevation = 4.dp
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun EstatCard(valor: String, descripcion: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFBDBDBD),
        shadowElevation = 6.dp,
        modifier = Modifier
            .width(150.dp)
            .height(110.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = descripcion,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EstadisticasPreviewAdmin() {
    AppLorentinaTheme {
        EstadisticasScreenAdmin()
    }
}
