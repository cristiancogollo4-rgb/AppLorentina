package com.cristiancogollo.applorentina


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

@Composable
fun EstadisticasScreen() {
    // Estado para cambiar entre "detal" y "mayor"
    var tipoVista by remember { mutableStateOf("detal") }

    // Datos para cada tipo
    val datos = if (tipoVista == "detal") {
        listOf(40f, 30f, 60f, 50f)
    } else {
        listOf(20f, 45f, 35f, 70f)
    }

    val paresVendidos = if (tipoVista == "detal") 75 else 120
    val comision = if (tipoVista == "detal") "$200.000" else "$350.000"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra superior con imagen
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

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "TUS ESTADÍSTICAS",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D4C41),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Gráfico de barras
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            BarChartCompose(
                values = datos,
                labels = listOf("Semana 1", "Semana 2", "Semana 3", "Semana 4")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de selección
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { tipoVista = "detal" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (tipoVista == "detal") Color(0xFF00BCD4) else Color(0xFFB2EBF2)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("#PARES AL DETAL", color = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { tipoVista = "mayor" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (tipoVista == "mayor") Color(0xFF00BCD4) else Color(0xFFB2EBF2)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("#PARES AL POR MAYOR", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Estadísticas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EstadisticaCard(paresVendidos.toString(), "PARES VENDIDOS\nESTA SEMANA")
            EstadisticaCard(comision, "TU COMISIÓN DE\nESTA SEMANA")
        }
    }
}

@Composable
fun EstadisticaCard(valor: String, descripcion: String) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .background(Color(0xFFD2E100), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = descripcion,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}
@Composable
fun BarChartCompose(values: List<Float>, labels: List<String>) {
    val maxVal = (values.maxOrNull() ?: 0f).coerceAtLeast(10f)
    val barColor = Color(0xFF4DD0E1)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val barWidth = size.width / (values.size * 2)
        val leftPadding = 1f     // espacio para los números del eje Y
        val bottomPadding = 40f   // espacio para las etiquetas
        val topPadding = 10f
        val chartHeight = size.height - bottomPadding - topPadding

        // ===== EJE Y con líneas y valores =====
        val step = 10
        val steps = (maxVal / step).toInt()

        for (i in 0..steps) {
            val yValue = i * step
            val yPos = size.height - bottomPadding - (yValue / maxVal) * chartHeight

            // Línea horizontal
            drawLine(
                color = Color(0xFFE0E0E0),
                start = Offset(leftPadding, yPos),
                end = Offset(size.width, yPos),
                strokeWidth = 2f
            )

            // Texto eje Y
            drawContext.canvas.nativeCanvas.drawText(
                yValue.toString(),
                leftPadding - 15,
                yPos + 10,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }

        // ===== DIBUJAR BARRAS =====
        values.forEachIndexed { i, value ->
            val barHeight = (value / maxVal) * chartHeight
            val x = leftPadding + (i * 2 + 1) * barWidth
            val y = size.height - bottomPadding - barHeight

            // Barra
            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(20f, 20f)
            )

            // Valor encima
            drawContext.canvas.nativeCanvas.drawText(
                value.toInt().toString(),
                x + barWidth / 2,
                y - 10,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )

            // Etiqueta semana
            drawContext.canvas.nativeCanvas.drawText(
                labels[i],
                x + barWidth / 2,
                size.height - 10,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }

        // Eje Y principal
        drawLine(
            color = Color.Gray,
            start = Offset(leftPadding, topPadding),
            end = Offset(leftPadding, size.height - bottomPadding),
            strokeWidth = 3f
        )
    }
}
@Preview(showBackground = true)
@Composable
fun EstadisticasPreview() {
    AppLorentinaTheme {
        EstadisticasScreen()
    }
}