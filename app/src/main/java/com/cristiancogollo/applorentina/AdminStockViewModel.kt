package com.cristiancogollo.applorentina

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminStockUiState(
    val productos: List<Producto> = emptyList(),
    val filteredProductos: List<Producto> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val mensajeAccion: String? = null
)

class AdminStockViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(AdminStockUiState())
    val uiState: StateFlow<AdminStockUiState> = _uiState.asStateFlow()

    private val _searchQueryFlow = MutableStateFlow("")

    init {
        fetchProductos()
        collectSearchQueryWithDebounce()
    }

    // ============================================================
    // 🧾 OBTENER PRODUCTOS (Lectura en tiempo real)
    // ============================================================
    private fun fetchProductos() {
        _uiState.update { it.copy(isLoading = true) }

        db.collection("Productos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AdminStockVM", "Error al cargar productos: ${error.message}")
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error al cargar productos")
                    }
                    return@addSnapshotListener
                }

                val productos = snapshot?.toObjects(Producto::class.java) ?: emptyList()
                _uiState.update {
                    it.copy(
                        productos = productos,
                        filteredProductos = productos,
                        isLoading = false
                    )
                }
                applyFilter(uiState.value.searchQuery)
            }
    }

    // ============================================================
    // 🧠 BÚSQUEDA CON DEBOUNCE
    // ============================================================
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

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQueryFlow.value = query
    }

    private fun applyFilter(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            _uiState.update { it.copy(filteredProductos = it.productos) }
            return
        }

        val filtered = uiState.value.productos.filter {
            it.referencia.startsWith(trimmed, true) ||
                    it.nombreModelo.startsWith(trimmed, true) ||
                    it.color.startsWith(trimmed, true)
        }

        _uiState.update { it.copy(filteredProductos = filtered) }
    }

    // ============================================================
    // 🧩 CREAR NUEVO PRODUCTO
    // ============================================================
    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                db.collection("Productos").add(producto).await()
                _uiState.update { it.copy(mensajeAccion = "✅ Producto agregado con éxito") }
            } catch (e: Exception) {
                Log.e("AdminStockVM", "Error al agregar producto", e)
                _uiState.update { it.copy(mensajeAccion = "❌ Error al guardar producto") }
            }
        }
    }

    // ============================================================
    // 🔁 ACTUALIZAR ESTADO ("en producción" → "en stock")
    // ============================================================
    fun actualizarEstado(referencia: String, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                val query = db.collection("Productos")
                    .whereEqualTo("referencia", referencia)
                    .get()
                    .await()

                for (doc in query.documents) {
                    db.collection("Productos").document(doc.id)
                        .update("estado", nuevoEstado)
                        .await()
                }

                _uiState.update { it.copy(mensajeAccion = "🟢 Estado actualizado a $nuevoEstado") }
            } catch (e: Exception) {
                Log.e("AdminStockVM", "Error al actualizar estado", e)
                _uiState.update { it.copy(mensajeAccion = "❌ Error al actualizar estado") }
            }
        }
    }

    // ============================================================
    // 📦 ACTUALIZAR STOCK POR TALLA
    // ============================================================
    fun actualizarStockPorTalla(referencia: String, talla: String, nuevoValor: Int) {
        viewModelScope.launch {
            try {
                val query = db.collection("Productos")
                    .whereEqualTo("referencia", referencia)
                    .get()
                    .await()

                for (doc in query.documents) {
                    val producto = doc.toObject(Producto::class.java)
                    val stockMap = producto?.stockPorTalla?.toMutableMap() ?: mutableMapOf()
                    stockMap[talla] = nuevoValor

                    db.collection("Productos").document(doc.id)
                        .update("stockPorTalla", stockMap)
                        .await()
                }

                _uiState.update { it.copy(mensajeAccion = "📦 Stock actualizado correctamente") }
            } catch (e: Exception) {
                Log.e("AdminStockVM", "Error al actualizar stock", e)
                _uiState.update { it.copy(mensajeAccion = "❌ Error al actualizar stock") }
            }
        }
    }

    fun clearMensaje() {
        _uiState.update { it.copy(mensajeAccion = null) }
    }
}
