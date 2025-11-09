package com.cristiancogollo.applorentina

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

// =================================================================
// ESTRUCTURA DE ESTADO
// =================================================================

data class NventaUiState(
    val clienteBuscado: String = "",
    val clienteSeleccionado: Cliente? = null,
    // Mensaje solicitado "porfavor agregar cliente o algo similar"
    val mensajeClienteNoEncontrado: String? = null,
    val precio: String = "",
    val fechaVenta: Date = Date(),
    val descripcion: String = "",
    val esDetal: Boolean = true, // true: Detal, false: Por Mayor
    val esVentaEspecial: Boolean = false, // Deshabilita agregar producto
    val isSaving: Boolean = false
)

// =================================================================
// VIEWMODEL (Contenido del archivo MVVMventa.kt)
// =================================================================

class NventaViewModel : ViewModel() {

    // Simulación de una lista de clientes existentes (MODEL ACCESS)
    // En una aplicación real, esto provendría de un Repository (ej. Firestore/Room)
    private val listaClientesSimulada = listOf(
        Cliente(cedula = 12345678, nombreApellido = "Juan Pérez", tipoCliente = true), // Detal
        Cliente(cedula = 98765432, nombreApellido = "Maria García", tipoCliente = false), // Por Mayor
        Cliente(cedula = 11223344, nombreApellido = "Carlos López", tipoCliente = true)
    )

    private val _uiState = MutableStateFlow(NventaUiState())
    val uiState: StateFlow<NventaUiState> = _uiState.asStateFlow()

    fun onClienteBuscadoChange(nombre: String) {
        _uiState.update {
            it.copy(
                clienteBuscado = nombre,
                clienteSeleccionado = null,
                mensajeClienteNoEncontrado = null
            )
        }
    }

    /**
     * Busca el cliente y actualiza el estado. Si no lo encuentra, muestra el mensaje.
     */
    fun buscarClientePorNombre(nombre: String) {
        val clienteEncontrado = listaClientesSimulada.find {
            it.nombreApellido.equals(nombre, ignoreCase = true)
        }

        _uiState.update {
            if (clienteEncontrado != null) {
                // Si el cliente es Por Mayor (tipoCliente=false), ajusta el toggle inicial
                val esDetal = clienteEncontrado.tipoCliente
                it.copy(
                    clienteSeleccionado = clienteEncontrado,
                    mensajeClienteNoEncontrado = null,
                    esDetal = esDetal
                )
            } else {
                it.copy(
                    clienteSeleccionado = null,
                    // Mensaje solicitado
                    mensajeClienteNoEncontrado = "Cliente no encontrado. Por favor, agregue uno nuevo."
                )
            }
        }
    }

    fun onPrecioChange(nuevoPrecio: String) {
        // Permite solo dígitos y el punto decimal
        if (nuevoPrecio.all { it.isDigit() || it == '.' }) {
            _uiState.update { it.copy(precio = nuevoPrecio) }
        }
    }

    fun onFechaChange(nuevaFecha: Date) {
        _uiState.update { it.copy(fechaVenta = nuevaFecha) }
    }

    fun onDescripcionChange(desc: String) {
        _uiState.update { it.copy(descripcion = desc) }
    }

    /**
     * Alterna entre Venta al Detal y Por Mayor.
     */
    fun toggleTipoVenta(esDetal: Boolean) {
        _uiState.update { it.copy(esDetal = esDetal) }
    }

    /**
     * Activa/Desactiva la Venta Especial (solo si es Por Mayor) y deshabilita productos.
     */
    fun toggleVentaEspecial(isSpecial: Boolean) {
        _uiState.update {
            // Solo se permite Venta Especial si la opción 'esDetal' es false (Por Mayor)
            if (!it.esDetal) {
                it.copy(esVentaEspecial = isSpecial)
            } else {
                it.copy(esVentaEspecial = false)
            }
        }
    }

    /**
     * Simulación de la lógica de guardado de la venta.
     */
    fun guardarVenta(onSaveSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            // Validación mínima
            if (uiState.value.clienteSeleccionado == null || uiState.value.precio.toDoubleOrNull() == null) {
                _uiState.update { it.copy(isSaving = false) }
                // Lógica para mostrar error al usuario
                return@launch
            }

            // ... Lógica para guardar en Firestore o la DB ...
            kotlinx.coroutines.delay(1000) // Simulación de retraso

            _uiState.update { NventaUiState() } // Resetear el estado
            onSaveSuccess() // Navegar o cerrar el diálogo
        }
    }
}