package com.cristiancogollo.applorentina

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue   // 👈 para el `by`
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cristiancogollo.applorentina.ui.theme.AppLorentinaTheme

@Composable
fun EstadisticasAdminScreen(
    onBackClick: () -> Unit = {},
    viewModel: AdminEstadisticasViewModel = viewModel(
        factory = AdminEstadisticasViewModelFactory()
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val labels = viewModel.labels

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ====== BARRA SUPERIOR (GRIS COMO HOME ADMIN) ======
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0)) // GRIS
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.DarkGray,
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

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "TUS ESTADÍSTICAS (ADMIN)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // ====== GRÁFICO ======
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                BarChartCompose(
                    values = uiState.datosGrafico,
                    labels = labels
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ====== BOTONES DETAL / MAYOR EN GRIS ======
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { viewModel.cambiarTipoVista(TipoVenta.DETAL) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.tipoVista == TipoVenta.DETAL)
                        Color.Gray          // activo
                    else
                        Color(0xFFBDBDBD)   // inactivo
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("#PARES AL DETAL", color = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { viewModel.cambiarTipoVista(TipoVenta.MAYOR) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.tipoVista == TipoVenta.MAYOR)
                        Color.Gray
                    else
                        Color(0xFFBDBDBD)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("#PARES AL POR MAYOR", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ====== TARJETAS DE ESTADÍSTICAS (ADMIN) EN GRIS ======
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 1. PARES VENDIDOS ESTA SEMANA (global para ese tipo)
            EstadisticaAdminCard(
                valor = uiState.paresVendidosSemana.toString(),
                descripcion = "PARES VENDIDOS\nESTA SEMANA"
            )

            // 2. GANANCIAS POR SEMANA (global para ese tipo)
            EstadisticaAdminCard(
                valor = uiState.gananciasSemanaFormateada,
                descripcion = "GANANCIAS POR\nSEMANA"
            )
        }

        // ====== ERRORES ======
        uiState.error?.let { mensaje ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = mensaje,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * Card SOLO para el ADMIN en gris, para no afectar la card del vendedor.
 */
@Composable
fun EstadisticaAdminCard(
    valor: String,
    descripcion: String
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .background(Color(0xFFBDBDBD), RoundedCornerShape(12.dp)) // GRIS
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valor,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = descripcion,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminEstadisticasPreview() {
    AppLorentinaTheme {
        EstadisticasAdminScreen()
    }
}
