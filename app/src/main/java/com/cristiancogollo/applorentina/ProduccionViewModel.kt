package com.cristiancogollo.applorentina

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _formState = MutableStateFlow(ProduccionFormState())
    val formState: StateFlow<ProduccionFormState> = _formState

    init { cargarProductos() }

    // === LECTURA EN TIEMPO REAL SOLO "en producción"
    fun cargarProductos() {
        db.collection("Productos")
            .whereEqualTo("estado", "en producción")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ProduccionVM", "Error: ${e.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    _productos.value = snapshot.toObjects(Producto::class.java)
                }
            }
    }

    // === Crear producto en Firestore
    fun crearProducto() {
        val data = _formState.value

        if (data.referencia.isBlank() || data.color.isBlank()) {
            _formState.value = data.copy(mensaje = "Referencia y color son obligatorios")
            return
        }

        val imagenUri = colorToDrawableAndroidResourceUri(data.color)

        val producto = hashMapOf(
            "referencia" to data.referencia.trim(),
            "nombreModelo" to data.nombreModelo.trim(),
            "color" to data.color.trim(),
            "descripcion" to data.descripcion.trim(),
            "precioDetal" to data.precioDetal,
            "precioMayor" to data.precioMayor,
            "estado" to (data.estado.ifBlank { "en producción" }),
            "imagenUrl" to imagenUri, // URI android.resource para Coil
            "stockPorTalla" to getDefaultStockMap(),
            "timestamp" to Timestamp.now()
        )

        viewModelScope.launch {
            try {
                db.collection("Productos").add(producto).await()
                _formState.value = ProduccionFormState(mensaje = "Producto creado exitosamente")
            } catch (e: Exception) {
                _formState.value = data.copy(mensaje = "Error: ${e.message}")
            }
        }
    }

    // === Cambiar estado a "en stock" por referencia
    fun actualizarEstadoAStock(referencia: String) {
        viewModelScope.launch {
            try {
                val query = db.collection("Productos")
                    .whereEqualTo("referencia", referencia)
                    .get().await()

                for (doc in query.documents) {
                    db.collection("Productos").document(doc.id)
                        .update("estado", "en stock").await()
                }
            } catch (e: Exception) {
                Log.e("ProduccionVM", "Error al actualizar: ${e.message}")
            }
        }
    }

    // === Updates para el form
    fun updateReferencia(v: String) { _formState.value = _formState.value.copy(referencia = v) }
    fun updateColor(v: String) { _formState.value = _formState.value.copy(color = v) }
    fun updateDescripcion(v: String) { _formState.value = _formState.value.copy(descripcion = v) }
    fun updatePrecioDetal(v: String) { _formState.value = _formState.value.copy(precioDetal = v.toDoubleOrNull() ?: 0.0) }
    fun updatePrecioMayor(v: String) { _formState.value = _formState.value.copy(precioMayor = v.toDoubleOrNull() ?: 0.0) }
    fun updateNombre(v: String) { _formState.value = _formState.value.copy(nombreModelo = v) }
    fun updateEstado(v: String) { _formState.value = _formState.value.copy(estado = v) }

    // === Mapear color → drawable → android.resource:// URI (para Coil)
    private fun colorToDrawableAndroidResourceUri(color: String): String {
        val key = when (color.trim().uppercase()) {
            "COÑAC", "CONAC", "COÑAC " -> "zapato1"
            "BLANCO" -> "zapato2"
            "NEGRO"  -> "zapato3"
            else     -> "" // sin imagen conocida
        }
        if (key.isBlank()) return ""

        // android.resource://<package>/drawable/<name>
        val pkg = "com.cristiancogollo.applorentina"
        return Uri.parse("android.resource://$pkg/drawable/$key").toString()
    }
}
