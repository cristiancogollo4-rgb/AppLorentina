package com.cristiancogollo.applorentina

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class NventaUiState(
    val clienteBuscado: String = "",
    val clienteSeleccionado: Cliente? = null,
    val mensajeClienteNoEncontrado: String? = null,

    val allClientes: List<Cliente> = emptyList(),
    val clientesFiltrados: List<Cliente> = emptyList(),
    val isDropdownExpanded: Boolean = false,
    val isClientesLoading: Boolean = false,

    val precio: String = "",
    val fechaVenta: Date = Date(),
    val descripcion: String = "",
    val esDetal: Boolean = true,
    val esVentaEspecial: Boolean = false,
    val isSaving: Boolean = false
)

class NventaViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(NventaUiState())
    val uiState: StateFlow<NventaUiState> = _uiState.asStateFlow()

    private val _searchQueryFlow = MutableStateFlow("")

    init {
        fetchClientes()
        collectSearchQueryWithDebounce()
    }

    /** Escucha Clientes en tiempo real */
    private fun fetchClientes() {
        _uiState.update { it.copy(isClientesLoading = true) }

        db.collection("Clientes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NventaVM", "Error escuchar clientes: ${error.message}", error)
                    _uiState.update { it.copy(isClientesLoading = false) }
                    return@addSnapshotListener
                }

                val clientes = snapshot?.toObjects(Cliente::class.java).orEmpty()
                _uiState.update { it.copy(allClientes = clientes, isClientesLoading = false) }
            }
    }

    /** Debounce para autocompletar */
    private fun collectSearchQueryWithDebounce() {
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    val trimmed = query.trim()
                    val filtered = performFilter(trimmed)
                    _uiState.update {
                        it.copy(
                            clientesFiltrados = filtered,
                            isDropdownExpanded = filtered.isNotEmpty() && trimmed.isNotEmpty()
                        )
                    }
                }
        }
    }

    fun onClienteBuscadoChange(query: String) {
        _uiState.update {
            it.copy(
                clienteBuscado = query,
                clienteSeleccionado = null,
                mensajeClienteNoEncontrado = null
            )
        }
        _searchQueryFlow.value = query
        if (query.isBlank()) _uiState.update { it.copy(isDropdownExpanded = false) }
    }

    private fun performFilter(query: String): List<Cliente> {
        if (query.isBlank()) return emptyList()
        return uiState.value.allClientes.filter { c ->
            c.nombreApellido.startsWith(query, true) ||
                    c.cedula.toString().startsWith(query)
        }.take(10)
    }

    fun seleccionarCliente(cliente: Cliente?) {
        _uiState.update {
            if (cliente == null) it.copy(
                clienteSeleccionado = null,
                clienteBuscado = "",
                isDropdownExpanded = false,
                mensajeClienteNoEncontrado = null
            ) else it.copy(
                clienteSeleccionado = cliente,
                clienteBuscado = cliente.nombreApellido,
                isDropdownExpanded = false,
                mensajeClienteNoEncontrado = null
            )
        }
    }

    fun buscarClientePorNombre(query: String) {
        val q = query.trim()
        _uiState.update { it.copy(isDropdownExpanded = false) }
        val encontrado = uiState.value.allClientes.find {
            it.nombreApellido.equals(q, true) || it.cedula.toString() == q
        }
        if (encontrado != null) seleccionarCliente(encontrado) else
            _uiState.update { it.copy(mensajeClienteNoEncontrado = "Cliente no encontrado. Agrega uno nuevo.") }
    }

    fun dismissDropdown() = _uiState.update { it.copy(isDropdownExpanded = false) }

    fun onPrecioChange(nuevo: String) {
        if (nuevo.all { it.isDigit() || it == '.' }) _uiState.update { it.copy(precio = nuevo) }
    }
    fun onFechaChange(nueva: Date) = _uiState.update { it.copy(fechaVenta = nueva) }
    fun onDescripcionChange(desc: String) = _uiState.update { it.copy(descripcion = desc) }
    fun toggleTipoVenta(esDetal: Boolean) = _uiState.update { it.copy(esDetal = esDetal) }
    fun toggleVentaEspecial(isSpecial: Boolean) = _uiState.update {
        if (!it.esDetal) it.copy(esVentaEspecial = isSpecial) else it.copy(esVentaEspecial = false)
    }

    /** Guarda la venta en Firestore */
    fun guardarVenta(onSaveSuccess: () -> Unit, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val s = uiState.value
            val cli = s.clienteSeleccionado
            val precio = s.precio.toDoubleOrNull()

            if (cli == null || precio == null) {
                _uiState.update { it.copy(isSaving = false) }
                onError("Debe seleccionar un cliente y un precio válido.")
                return@launch
            }

            val venta = Venta(
                idVenta = db.collection("Ventas").document().id,
                cliente = cli,
                fechaVenta = s.fechaVenta,
                precioTotal = precio,
                descripcion = s.descripcion,
                esDetal = s.esDetal,
                esVentaEspecial = s.esVentaEspecial,
                productos = emptyList()
            )

            db.collection("Ventas")
                .document(venta.idVenta)
                .set(venta)
                .addOnSuccessListener {
                    Log.d("NventaVM", "Venta guardada.")
                    _uiState.update { NventaUiState(allClientes = it.allClientes) }
                    onSaveSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("NventaVM", "Error guardar venta: ${e.message}", e)
                    _uiState.update { it.copy(isSaving = false) }
                    onError(e.message ?: "Error desconocido")
                }
        }
    }
}
