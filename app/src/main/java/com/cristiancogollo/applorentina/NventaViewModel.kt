package com.cristiancogollo.applorentina

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException // Import necesario
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay // Necesario para el Debounce y la simulación
import java.util.Date

// =================================================================
// ESTRUCTURA DE ESTADO
// =================================================================

data class NventaUiState(
    val clienteBuscado: String = "",
    val clienteSeleccionado: Cliente? = null,
    val mensajeClienteNoEncontrado: String? = null,

    // ESTADOS PARA EL DESPLEGABLE Y FILTRADO
    val allClientes: List<Cliente> = emptyList(), // Lista completa de la DB
    val clientesFiltrados: List<Cliente> = emptyList(), // Resultados del filtro
    val isDropdownExpanded: Boolean = false,
    val isClientesLoading: Boolean = false, // Indica si la lista de clientes está cargando

    val precio: String = "",
    val fechaVenta: Date = Date(),
    val descripcion: String = "",
    val esDetal: Boolean = true,
    val esVentaEspecial: Boolean = false,
    val isSaving: Boolean = false
)

// =================================================================
// VIEWMODEL
// =================================================================

class NventaViewModel : ViewModel() {

    // 🟢 INSTANCIA DE FIRESTORE
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(NventaUiState())
    val uiState: StateFlow<NventaUiState> = _uiState.asStateFlow()

    private val _searchQueryFlow = MutableStateFlow("")

    init {
        // 1. Inicia la carga de clientes de la DB
        fetchClientes()
        // 2. Inicia la recolección con Debounce para el autocompletado
        collectSearchQueryWithDebounce()
    }

    /**
     * 🟢 NUEVA FUNCIÓN: Escucha los cambios en la colección "Clientes" de Firestore.
     */
    private fun fetchClientes() {
        _uiState.update { it.copy(isClientesLoading = true) }

        db.collection("Clientes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NventaViewModel", "Error al escuchar clientes: ${error.message}", error)
                    _uiState.update {
                        it.copy(isClientesLoading = false)
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val clientes = snapshot.toObjects(Cliente::class.java)
                    _uiState.update {
                        it.copy(
                            allClientes = clientes,
                            isClientesLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            allClientes = emptyList(),
                            isClientesLoading = false
                        )
                    }
                }
            }
    }


    /**
     * Configura el debounce y el filtrado en un coroutine.
     */
    private fun collectSearchQueryWithDebounce() {
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(300L)
                .distinctUntilChanged()
                .collect { query ->
                    val filteredList = performFilter(query)

                    val trimmedQuery = query.trim()
                    _uiState.update {
                        it.copy(
                            clientesFiltrados = filteredList,
                            isDropdownExpanded = filteredList.isNotEmpty() && trimmedQuery.isNotEmpty()
                        )
                    }
                }
        }
    }

    // --- FUNCIONES DE BÚSQUEDA Y SELECCIÓN ---

    fun onClienteBuscadoChange(query: String) {
        _uiState.update {
            it.copy(
                clienteBuscado = query,
                clienteSeleccionado = null,
                mensajeClienteNoEncontrado = null
            )
        }
        _searchQueryFlow.value = query

        if (query.isBlank()) {
            _uiState.update { it.copy(isDropdownExpanded = false) }
        }
    }

    /**
     * Lógica de filtrado con startsWith (búsqueda secuencial)
     */
    private fun performFilter(query: String): List<Cliente> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return emptyList()

        // 🟢 USAMOS LA LISTA CARGADA POR FIRESTORE
        return uiState.value.allClientes.filter { cliente ->
            val nombreMatches = cliente.nombreApellido.startsWith(trimmedQuery, ignoreCase = true)
            // Asegúrate de que la cédula sea tratada como String para startsWith
            val cedulaMatches = cliente.cedula.toString().startsWith(trimmedQuery)

            nombreMatches || cedulaMatches
        }.take(10)
    }

    fun seleccionarCliente(cliente: Cliente?) {
        _uiState.update {
            if (cliente == null) {
                it.copy(
                    clienteSeleccionado = null,
                    clienteBuscado = "",
                    isDropdownExpanded = false,
                    mensajeClienteNoEncontrado = null
                )
            } else {
                it.copy(
                    clienteSeleccionado = cliente,
                    clienteBuscado = cliente.nombreApellido,
                    isDropdownExpanded = false,
                    mensajeClienteNoEncontrado = null
                )
            }
        }
    }

    fun buscarClientePorNombre(query: String) {
        val trimmedQuery = query.trim()

        _uiState.update { it.copy(isDropdownExpanded = false) }

        // 🟢 USAMOS LA LISTA CARGADA POR FIRESTORE
        val clienteEncontrado = uiState.value.allClientes.find {
            it.nombreApellido.equals(trimmedQuery, ignoreCase = true) || it.cedula.toString() == trimmedQuery
        }

        if (clienteEncontrado != null) {
            seleccionarCliente(clienteEncontrado)
        } else {
            _uiState.update {
                it.copy(
                    clienteSeleccionado = null,
                    mensajeClienteNoEncontrado = "Cliente no encontrado. Por favor, agregue uno nuevo."
                )
            }
        }
    }

    fun dismissDropdown() {
        _uiState.update { it.copy(isDropdownExpanded = false) }
    }

    // --- RESTO DE LAS FUNCIONES DE ESTADO (Mantenidas) ---

    fun onPrecioChange(nuevoPrecio: String) {
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

    fun toggleTipoVenta(esDetal: Boolean) {
        _uiState.update { it.copy(esDetal = esDetal) }
    }

    fun toggleVentaEspecial(isSpecial: Boolean) {
        _uiState.update {
            if (!it.esDetal) {
                it.copy(esVentaEspecial = isSpecial)
            } else {
                it.copy(esVentaEspecial = false)
            }
        }
    }

    /**
     * Simulación de la lógica de guardado de la venta (ajustada).
     */
    fun guardarVenta(onSaveSuccess: () -> Unit, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val state = uiState.value

            val clienteSeleccionado = state.clienteSeleccionado
            val precio = state.precio.toDoubleOrNull()

            if (clienteSeleccionado == null || precio == null) {
                _uiState.update { it.copy(isSaving = false) }
                onError("Debe seleccionar un cliente y agregar un precio válido.")
                return@launch
            }

            // 🔹 Crear el objeto venta
            val nuevaVenta = Venta(
                idVenta = db.collection("Ventas").document().id, // genera ID automáticamente
                cliente = clienteSeleccionado,
                fechaVenta = state.fechaVenta,
                precioTotal = precio,
                descripcion = state.descripcion,
                esDetal = state.esDetal,
                esVentaEspecial = state.esVentaEspecial,
                productos = emptyList() // por ahora vacío
            )

            // 🔹 Guardar en Firestore
            db.collection("Ventas")
                .document(nuevaVenta.idVenta)
                .set(nuevaVenta)
                .addOnSuccessListener {
                    Log.d("NventaViewModel", "✅ Venta guardada correctamente en Firestore.")
                    _uiState.update { NventaUiState(allClientes = it.allClientes) } // Resetea el formulario
                    onSaveSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("NventaViewModel", "❌ Error al guardar la venta: ${e.message}", e)
                    _uiState.update { it.copy(isSaving = false) }
                    onError(e.message ?: "Error desconocido al guardar la venta")
                }
        }
    }
}