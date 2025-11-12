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

// ============================================================
// 🟢 ESTRUCTURA DEL FORMULARIO DE PRODUCCIÓN
// ============================================================
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

// ============================================================
// 🧠 VIEWMODEL PRINCIPAL DE PRODUCCIÓN
// ============================================================
class ProduccionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _formState = MutableStateFlow(ProduccionFormState())
    val formState: StateFlow<ProduccionFormState> = _formState

    init {
        cargarProductos()
    }

    // ============================================================
    // 🔹 CARGA EN TIEMPO REAL DE PRODUCTOS EN PRODUCCIÓN
    // ============================================================
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

    // ============================================================
    // 🟢 CREAR NUEVO PRODUCTO EN FIRESTORE
    // ============================================================
    fun crearProducto() {
        val data = _formState.value

        if (data.referencia.isBlank() || data.color.isBlank()) {
            _formState.value = data.copy(mensaje = "Referencia y color son obligatorios")
            return
        }

        // Asignar imagen automáticamente según el color
        val imagenAsignada = asignarImagenPorColor(data.color)

        val producto = hashMapOf(
            "referencia" to data.referencia,
            "nombreModelo" to data.nombreModelo,
            "color" to data.color,
            "descripcion" to data.descripcion,
            "precioDetal" to data.precioDetal,
            "precioMayor" to data.precioMayor,
            "estado" to "en producción",
            "imagenUrl" to imagenAsignada,
            "stockPorTalla" to getDefaultStockMap(),
            "timestamp" to Timestamp.now()
        )

        viewModelScope.launch {
            try {
                db.collection("Productos").add(producto).await()
                _formState.value = ProduccionFormState(
                    mensaje = "✅ Producto guardado correctamente"
                )
            } catch (e: Exception) {
                _formState.value = data.copy(mensaje = "Error: ${e.message}")
            }
        }
    }

    // ============================================================
    // 🟡 CAMBIAR PRODUCTO A "EN STOCK" (MOVER A INVENTARIO)
    // ============================================================
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

                Log.d("ProduccionVM", "Producto $referencia movido a stock correctamente")
            } catch (e: Exception) {
                Log.e("ProduccionVM", "Error al actualizar estado: ${e.message}")
            }
        }
    }

    // ============================================================
    // 🎨 ASIGNAR IMAGEN AUTOMÁTICA SEGÚN COLOR
    // ============================================================
    fun asignarImagenPorColor(color: String): String {
        return when (color.lowercase().trim()) {
            "CONAC" -> "drawable/zapato1"
            "BLANCO" -> "drawable/zapato2"
            "NEGRO" -> "drawable/zapato3"
            else -> "drawable/ic_launcher_foreground"
        }
    }

    // ============================================================
    // 🧱 MAPA DE STOCK INICIAL POR TALLA
    // ============================================================
    fun getDefaultStockMap(): Map<String, Int> {
        val stock = mutableMapOf<String, Int>()
        for (talla in 35..42) {
            stock[talla.toString()] = 0
        }
        return stock
    }

    // ============================================================
    // ✏️ FUNCIONES DE ACTUALIZACIÓN DEL FORMULARIO
    // ============================================================
    fun updateReferencia(v: String) {
        _formState.value = _formState.value.copy(referencia = v)
    }

    fun updateColor(v: String) {
        _formState.value = _formState.value.copy(color = v)
    }

    fun updateDescripcion(v: String) {
        _formState.value = _formState.value.copy(descripcion = v)
    }

    fun updatePrecioDetal(v: String) {
        _formState.value =
            _formState.value.copy(precioDetal = v.toDoubleOrNull() ?: 0.0)
    }

    fun updatePrecioMayor(v: String) {
        _formState.value =
            _formState.value.copy(precioMayor = v.toDoubleOrNull() ?: 0.0)
    }

    fun updateNombre(v: String) {
        _formState.value = _formState.value.copy(nombreModelo = v)
    }
}
