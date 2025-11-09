package com.cristiancogollo.applorentina

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StockUiState(
    val productos: List<Producto> = emptyList(), // Lista completa del inventario
    val filteredProductos: List<Producto> = emptyList(), // Lista filtrada para la UI
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class StockViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(StockUiState())
    val uiState: StateFlow<StockUiState> = _uiState.asStateFlow()

    // Flow para el Debounce (igual que en NventaViewModel)
    private val _searchQueryFlow = MutableStateFlow("")

    init {
        fetchProductos()
        collectSearchQueryWithDebounce()
    }

    /**
     * Carga y escucha en tiempo real los productos desde Firestore.
     */
    private fun fetchProductos() {
        _uiState.update { it.copy(isLoading = true) }

        db.collection("Productos") // Asumimos que tienes una colección llamada "Inventario"
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("StockViewModel", "Error al cargar inventario: ${error.message}", error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión") }
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val productos = snapshot.toObjects(Producto::class.java)
                    _uiState.update {
                        it.copy(
                            productos = productos,
                            filteredProductos = productos, // Inicialmente, todos los productos son visibles
                            isLoading = false
                        )
                    }
                    // Re-aplicar el filtro si ya había una búsqueda activa
                    applyFilter(uiState.value.searchQuery)
                }
            }
    }

    /**
     * Configura el debounce para evitar lag al escribir.
     */
    private fun collectSearchQueryWithDebounce() {
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(300L)
                .distinctUntilChanged()
                .collect { query ->
                    applyFilter(query)
                }
        }
    }

    /**
     * 🟢 FUNCIÓN DE BÚSQUEDA Y FILTRADO
     * Busca por nombre o ID del producto.
     */
    private fun applyFilter(query: String) {
        val trimmedQuery = query.trim()

        // 1. Actualiza el estado de la búsqueda (se hace aquí después del debounce)
        _uiState.update { it.copy(searchQuery = query) }

        if (trimmedQuery.isBlank()) {
            _uiState.update { it.copy(filteredProductos = it.productos) }
            return
        }

        val filtered = uiState.value.productos.filter { producto ->
            val q = trimmedQuery.lowercase()

            // Filtra si la referencia, el nombre del modelo O el color COINCIDEN con el inicio de la búsqueda.
            producto.referencia.startsWith(q, ignoreCase = true) ||
                    producto.nombreModelo.startsWith(q, ignoreCase = true) ||
                    producto.color.startsWith(q, ignoreCase = true)
        }

        _uiState.update { it.copy(filteredProductos = filtered) }
    }

    /**
     * Actualiza el campo de búsqueda e inicia el debounce.
     */
    fun onSearchQueryChange(query: String) {
        // Actualiza el campo de texto de inmediato para una escritura fluida
        _uiState.update { it.copy(searchQuery = query) }
        // Envía el valor al flow para que se aplique el debounce
        _searchQueryFlow.value = query
    }
}