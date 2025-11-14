package com.cristiancogollo.applorentina

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

// Estado principal de la UI (vendedor)
data class EstadisticasUiState(
    val tipoVista: TipoVenta = TipoVenta.DETAL,
    val datosGrafico: List<Float> = emptyList(),
    val paresVendidos: Int = 0,
    val comisionSemanal: String = "$0",
    val isLoading: Boolean = false,
    val error: String? = null
)

// Tipos de venta: detal / mayor
enum class TipoVenta {
    DETAL,
    MAYOR
}

// Estructura mínima que usamos para estadísticas
data class VentaEstadistica(
    val idVenta: String,
    val esDetal: Boolean,
    val fechaVenta: Date,
    val cantidadParesVendidos: Int
)

