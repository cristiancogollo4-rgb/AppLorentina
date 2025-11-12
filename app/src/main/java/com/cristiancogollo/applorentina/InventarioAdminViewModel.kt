package com.cristiancogollo.applorentina

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class InventarioUiState(
    val productos: List<Producto> = emptyList(),
    val mensaje: String? = null,
    val isLoading: Boolean = false
)

class InventarioAdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(InventarioUiState())
    val uiState: StateFlow<InventarioUiState> = _uiState

    init { cargarInventario() }

    fun cargarInventario() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        db.collection("Productos")
            .whereEqualTo("estado", "en stock")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("InventarioVM", "Error: ${e.message}")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    return@addSnapshotListener
                }
                _uiState.value = InventarioUiState(
                    productos = snapshot?.toObjects(Producto::class.java) ?: emptyList(),
                    isLoading = false
                )
            }
    }

    fun actualizarStock(ref: String, talla: String, valor: Int) {
        viewModelScope.launch {
            try {
                val query = db.collection("Productos").whereEqualTo("referencia", ref).get().await()
                for (doc in query.documents) {
                    val stockActual = doc.get("stockPorTalla") as? Map<String, Long> ?: emptyMap()
                    val actualizado = stockActual.toMutableMap()
                    actualizado[talla] = valor.toLong()
                    db.collection("Productos").document(doc.id)
                        .update("stockPorTalla", actualizado).await()
                }
            } catch (e: Exception) {
                Log.e("InventarioVM", "Error al actualizar stock: ${e.message}")
            }
        }
    }
}
