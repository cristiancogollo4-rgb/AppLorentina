package com.cristiancogollo.applorentina

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class ProduccionVendedorViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(StockUiState())
    val uiState: StateFlow<StockUiState> = _uiState.asStateFlow()

    private val _searchQueryFlow = MutableStateFlow("")

    init {
        fetchProductosEnProduccion() // 🟢 Llama a la función de carga filtrada
        collectSearchQueryWithDebounce()
    }

    /**
     * Carga y escucha en tiempo real SOLO los productos en estado de PRODUCCIÓN.
     */
    private fun fetchProductosEnProduccion() {
        _uiState.update { it.copy(isLoading = true) }

        db.collection("Productos")
            //  Carga todos los estados definidos como "en producción"
            .whereIn("estado", PRODUCCION_STATES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ProduccionVendedorVM", "Error al cargar producción: ${error.message}", error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión") }
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val productos = snapshot.documents.mapNotNull { doc ->
                        // Es vital mapear el ID para consistencia
                        doc.toObject(Producto::class.java)?.copy(id = doc.id)
                    }
                    _uiState.update {
                        it.copy(
                            productos = productos,
                            filteredProductos = productos,
                            isLoading = false
                        )
                    }
                    // Re-aplica el filtro si hay una búsqueda activa
                    applyFilter(uiState.value.searchQuery)
                }
            }
    }

    // Funciones de Búsqueda y Debounce

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
     * Filtra la lista según el activeFilter y la query (REFE, COLOR, TALLA).
     */
    private fun applyFilter(query: String) {
        val trimmedQuery = query.trim()
        val currentState = uiState.value

        _uiState.update { it.copy(searchQuery = query) }

        if (trimmedQuery.isBlank()) {
            _uiState.update { it.copy(filteredProductos = it.productos) }
            return
        }

        val filtered = currentState.productos.filter { producto ->
            val q = trimmedQuery.lowercase()
            val activeFilter = currentState.activeFilter

            when (activeFilter) {
                "REFE" -> producto.referencia.startsWith(q, ignoreCase = true) ||
                        producto.nombreModelo.startsWith(q, ignoreCase = true)
                "COLOR" -> producto.color.startsWith(q, ignoreCase = true)
                "TALLA" -> (producto.stockPorTalla[q] ?: 0) > 0 // Buscar talla con stock > 0
                else -> false
            }
        }

        _uiState.update { it.copy(filteredProductos = filtered) }
    }

    /**
     * Llama al debounce para aplicar el filtro de búsqueda.
     */
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQueryFlow.value = query
    }

    /**
     * Cambia el tipo de filtro activo (REFE, COLOR, TALLA) y re-aplica el filtro.
     */
    fun onFilterTypeChange(filterType: String) {
        _uiState.update { it.copy(activeFilter = filterType) }
        applyFilter(uiState.value.searchQuery)
    }
}