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
    val activeFilter: String = "REFE", // Puede ser "REFE", "COLOR", "TALLA"
    val selectedTallaFilter: String? = null,
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
     * Carga y escucha en tiempo real los productos desde Firestore, FILTRANDO por estado "en stock".
     */
    private fun fetchProductos() {
        _uiState.update { it.copy(isLoading = true) }

        db.collection("Productos")
            // 🟢 FILTRO AGREGADO: Solo carga productos con estado "en stock"
            .whereEqualTo("estado", "en stock")
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
     * Busca por referencia, modelo o color.
     */
    private fun applyFilter(query: String) {
        val trimmedQuery = query.trim()
        val currentState = uiState.value

        // 1. Actualiza el estado de la búsqueda
        _uiState.update { it.copy(searchQuery = query) }

        // 2. Comienza con todos los productos de stock
        var filtered = currentState.productos

        // 3. Aplicar Filtro Activo sobre el texto de búsqueda (trimmedQuery)
        if (trimmedQuery.isNotBlank()) {
            filtered = filtered.filter { producto ->
                val q = trimmedQuery.lowercase()
                when (currentState.activeFilter) {
                    "REFE" -> producto.referencia.startsWith(q, ignoreCase = true)
                    "COLOR" -> producto.color.startsWith(q, ignoreCase = true)
                    "TALLA" -> {
                        // Si se está filtrando por TALLA, se busca la talla en el mapa de stock
                        // y debe tener stock > 0.
                        val stock = producto.stockPorTalla[q] ?: 0
                        stock > 0
                    }
                    else -> false // Caso por defecto
                }
            }
        }

        // 4. Aplicar filtro específico de talla (si está activo y seleccionado)
        // Esto es útil si quieres un Dropdown en la UI para seleccionar la talla
        // y mostrar solo los productos que tengan esa talla con stock > 0.
        val selectedTalla = currentState.selectedTallaFilter
        if (currentState.activeFilter == "TALLA" && selectedTalla != null) {
            filtered = filtered.filter { producto ->
                val stock = producto.stockPorTalla[selectedTalla] ?: 0
                stock > 0
            }
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

    fun onFilterTypeChange(filterType: String) {
        // Si el filtro es por talla, reseteamos la talla seleccionada
        if (filterType != "TALLA") {
            _uiState.update { it.copy(activeFilter = filterType, selectedTallaFilter = null) }
        } else {
            _uiState.update { it.copy(activeFilter = filterType) }
        }
        // Re-aplica el filtro inmediatamente
        applyFilter(uiState.value.searchQuery)
    }

    /**
     * Actualiza la talla específica seleccionada cuando el filtro activo es TALLA.
     */
    fun onTallaFilterChange(talla: String?) {
        _uiState.update { it.copy(selectedTallaFilter = talla) }
        // Re-aplica el filtro
        applyFilter(uiState.value.searchQuery)
    }
}


