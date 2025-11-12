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
        db.collection("Productos").whereEqualTo("estado", "en stock")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                _uiState.value = InventarioUiState(
                    productos = snapshot?.toObjects(Producto::class.java) ?: emptyList()
                )
            }
    }

    // === CONSULTA POR REFERENCIA ===
    fun consultarPorReferencia(ref: String, onResult: (Producto?) -> Unit) {
        viewModelScope.launch {
            try {
                val query = db.collection("Productos")
                    .whereEqualTo("referencia", ref)
                    .whereEqualTo("estado", "en stock")
                    .get().await()
                onResult(query.documents.firstOrNull()?.toObject(Producto::class.java))
            } catch (e: Exception) {
                Log.e("InventarioVM", "Error: ${e.message}")
                onResult(null)
            }
        }
    }
}
