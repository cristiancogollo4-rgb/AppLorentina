package com.cristiancogollo.applorentina

import java.util.Date

// =======================
// ENUMS Y DATA CLASSES
// =======================

// Tipos de venta: detal / mayor
enum class TipoVenta {
    DETAL,
    MAYOR
}

// Estructura mínima que usamos para estadísticas (Vendedor)
data class VentaEstadistica(
    val idVenta: String,
    val esDetal: Boolean,
    val fechaVenta: Date,
    val cantidadParesVendidos: Int
)

// ⚠️ NOTA: La clase Venta completa está en el archivo Venta.kt y es importada.


// =======================
// ESTADOS DE LA UI
// =======================

// Estado principal de la UI (VENDEDOR)
data class EstadisticasUiState(
    val tipoVista: TipoVenta = TipoVenta.DETAL,
    val datosGrafico: List<Float> = emptyList(),
    val paresVendidos: Int = 0,
    val comisionSemanal: String = "$0",
    val isLoading: Boolean = false,
    val error: String? = null
)

// Estado principal de la UI (ADMINISTRADOR)
data class AdminEstadisticasUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val datosGrafico: List<Float> = emptyList(), // Pares vendidos (4 semanas)
    val paresVendidosSemana: Int = 0, // Pares de la SEMANA ACTUAL
    val gananciasSemanaFormateada: String = "$0", // Ganancias de la SEMANA ACTUAL
    val tipoVista: TipoVenta = TipoVenta.DETAL
)