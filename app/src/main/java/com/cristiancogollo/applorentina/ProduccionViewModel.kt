package com.cristiancogollo.applorentina

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// 🔹 Estado del formulario de producción
data class ProduccionFormState(
    val referencia: String = "",
    val nombreModelo: String = "",
    val color: String = "",
    val descripcion: String = "",
    val precioDetal: Double = 0.0,
    val precioMayor: Double = 0.0,
    val estado: String = "en producción",
    val imagenUrl: String = "",
    val mensaje: String? = null
)

class ProduccionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // 🧩 Lista de productos en producción
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // 🧩 Estado del formulario
    private val _formState = MutableStateFlow(ProduccionFormState())
    val formState: StateFlow<ProduccionFormState> = _formState

    init {
        cargarProductos()
    }

    // ============================================================
    // 🟢 CARGAR PRODUCTOS EN PRODUCCIÓN
    // ============================================================
    fun cargarProductos() {
        db.collection("Productos")
            .whereEqualTo("estado", "en producción")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ProduccionVM", "Error al escuchar productos: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Producto::class.java)
                    }
                    _productos.value = lista
                }
            }
    }

    // ============================================================
    // 🟢 CREAR NUEVO PRODUCTO
    // ============================================================
    fun crearProducto() {
        val data = _formState.value

        if (data.referencia.isBlank() || data.color.isBlank()) {
            _formState.value = data.copy(mensaje = "Referencia y color son obligatorios")
            return
        }

        val producto = hashMapOf(
            "referencia" to data.referencia,
            "nombreModelo" to data.nombreModelo,
            "color" to data.color,
            "descripcion" to data.descripcion,
            "precioDetal" to data.precioDetal,
            "precioMayor" to data.precioMayor,
            "estado" to "en producción",
            "imagenUrl" to data.imagenUrl,
            "stockPorTalla" to getDefaultStockMap(),
            "timestamp" to Timestamp.now()
        )

        viewModelScope.launch {
            try {
                db.collection("Productos").add(producto).await()
                _formState.value = ProduccionFormState(mensaje = "Producto creado exitosamente")
            } catch (e: Exception) {
                _formState.value = data.copy(mensaje = "Error: ${e.message}")
                Log.e("ProduccionVM", "Error al crear producto: ${e.message}")
            }
        }
    }

    // ============================================================
    // 🟡 ACTUALIZAR ESTADO A "EN STOCK"
    // ============================================================
    fun actualizarEstadoAStock(referencia: String) {
        viewModelScope.launch {
            try {
                val query = db.collection("Productos")
                    .whereEqualTo("referencia", referencia)
                    .get()
                    .await()

                for (doc in query.documents) {
                    db.collection("Productos").document(doc.id)
                        .update("estado", "en stock")
                        .await()
                }

                Log.d("ProduccionVM", "Producto $referencia actualizado a 'en stock'")
            } catch (e: Exception) {
                Log.e("ProduccionVM", "Error al actualizar estado: ${e.message}")
            }
        }
    }

    // ============================================================
    // 🟣 Actualización de campos del formulario
    // ============================================================
    fun updateReferencia(valor: String) { _formState.value = _formState.value.copy(referencia = valor) }
    fun updateNombre(valor: String) { _formState.value = _formState.value.copy(nombreModelo = valor) }
    fun updateColor(valor: String) { _formState.value = _formState.value.copy(color = valor) }
    fun updateDescripcion(valor: String) { _formState.value = _formState.value.copy(descripcion = valor) }
    fun updatePrecioDetal(valor: String) { _formState.value = _formState.value.copy(precioDetal = valor.toDoubleOrNull() ?: 0.0) }
    fun updatePrecioMayor(valor: String) { _formState.value = _formState.value.copy(precioMayor = valor.toDoubleOrNull() ?: 0.0) }
}

