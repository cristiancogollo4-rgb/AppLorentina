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

// Definición de las comisiones (segun tu regla)
private const val COMISION_DETAL = 5000L
private const val COMISION_MAYOR = 2500L

class EstadisticasViewModel(
    private val repository: VentaRepository = VentaRepository() // Ahora inyectamos el repositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstadisticasUiState(isLoading = true))
    val uiState: StateFlow<EstadisticasUiState> = _uiState

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
                // 1. Obtener datos reales de Firebase a través del repositorio
                val ventasPorSemana = repository.getVentasPorTipoSemanal(tipo)

                // 2. Calcular las métricas
                val datosGrafico: List<Float> = labels.indices.map { index ->
                    val weekIndex = index + 1 // Semana 1, 2, 3, 4
                    val ventasDeLaSemana = ventasPorSemana[weekIndex] ?: emptyList()
                    // Sumar la cantidad de pares vendidos para esa semana
                    ventasDeLaSemana.sumOf { it.cantidadParesVendidos }.toFloat()
                }

                // 💡 USAR EL TOTAL DEL PERÍODO para la tarjeta y la comisión
                val paresVendidosTotales = datosGrafico.sum().toInt()
                val comision = calcularComision(tipo, paresVendidosTotales)
                val comisionFormateada = formatCurrency(comision)

                // 3. Actualizar el estado (Éxito)
                _uiState.update {
                    it.copy(
                        datosGrafico = datosGrafico,
                        paresVendidos = paresVendidosTotales,
                        comisionSemanal = comisionFormateada,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // 4. Manejo del error
                println("ERROR EN VIEWMODEL: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar las ventas: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun calcularComision(tipo: TipoVenta, cantidadPares: Int): Long {
        val comisionPorPar = if (tipo == TipoVenta.DETAL) COMISION_DETAL else COMISION_MAYOR
        return cantidadPares.toLong() * comisionPorPar
    }

    private fun formatCurrency(amount: Long): String {
        // Formato para moneda colombiana
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }
}

// Factory actualizado para inyectar el repositorio
class EstadisticasViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstadisticasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // 💡 Ahora llamamos al constructor sin pasar el repositorio, usando el valor por defecto.
            return EstadisticasViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}