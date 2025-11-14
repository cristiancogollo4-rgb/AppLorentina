package com.cristiancogollo.applorentina

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import java.util.Date

data class AdminEstadisticasUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val datosGrafico: List<Float> = emptyList(),
    val paresVendidosSemana: Int = 0,
    val gananciasSemanaFormateada: String = "$0",
    val tipoVista: TipoVenta = TipoVenta.DETAL
)

class AdminEstadisticasViewModel(
    private val repository: VentaRepository = VentaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminEstadisticasUiState())
    val uiState: StateFlow<AdminEstadisticasUiState> = _uiState

    val labels: List<String> = listOf("Semana 1", "Semana 2", "Semana 3", "Semana 4")

    init {
        cargarDatos(TipoVenta.DETAL)
    }

    fun cambiarTipoVista(nuevoTipo: TipoVenta) {
        if (nuevoTipo != _uiState.value.tipoVista) {
            cargarDatos(nuevoTipo)
        }
    }

    private fun cargarDatos(tipo: TipoVenta) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, tipoVista = tipo, error = null) }

            try {
                // 🔹 Mapa: número de semana -> lista de ventas
                val ventasPorSemana: Map<Int, List<Venta>> =
                    repository.getVentasPorTipoSemanal(tipo)

                // ===== DATOS PARA LA GRÁFICA =====
                // En el admin mostramos PARES vendidos por semana
                val datosGrafico: List<Float> = labels.indices.map { index ->
                    val weekIndex = index + 1  // 1,2,3,4

                    val ventasDeLaSemana: List<Venta> =
                        ventasPorSemana[weekIndex] ?: emptyList()

                    val paresSemana = ventasDeLaSemana.sumOf { contarParesDeVenta(it) }

                    paresSemana.toFloat()
                }

                // ===== TARJETAS: SEMANA ACTUAL =====
                val semanaActualIndex = repository.getWeekOfMonth(Date())

                val ventasSemanaActual: List<Venta> =
                    ventasPorSemana[semanaActualIndex] ?: emptyList()

                // 🔢 Total de pares vendidos en la semana actual
                val paresSemanaActual =
                    ventasSemanaActual.sumOf { contarParesDeVenta(it) }

                // 💰 Ganancia total = suma de precioTotal
                val gananciasSemanaActual: Double =
                    ventasSemanaActual.sumOf { it.precioTotal }

                val gananciasFormateada = formatCurrency(gananciasSemanaActual)

                _uiState.update {
                    it.copy(
                        datosGrafico = datosGrafico,
                        paresVendidosSemana = paresSemanaActual,
                        gananciasSemanaFormateada = gananciasFormateada,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar las ventas (admin): ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Cuenta cuántos pares hay en una venta, según el array "productos"
     * tal como lo guardas en Firestore:
     *
     * productos: [
     *   { cantidad: 1, nombreModelo: "Romanas", ... },
     *   { cantidad: 2, nombreModelo: "Otro", ... }
     * ]
     */
    private fun contarParesDeVenta(venta: Venta): Int {
        // En tu data class Venta tienes: productos: List<Any>
        val productosList = venta.productos as? List<Map<String, Any?>> ?: return 0

        return productosList.sumOf { producto ->
            // Firestore guarda números como Long por defecto
            (producto["cantidad"] as? Long)?.toInt() ?: 0
        }
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }
}

class AdminEstadisticasViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminEstadisticasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminEstadisticasViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
