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
// 🟢 CONSTANTES DE ESTADOS DE PRODUCCIÓN
// ============================================================
val PRODUCCION_STATES = listOf("corte", "armado", "costura", "soladura", "emplantillado")
val PRODUCTO_STATES_ALL = PRODUCCION_STATES + listOf("en stock")

// Nota: La clase Producto y getDefaultStockMap ya están definidas en otro archivo
// y deben incluir el campo 'id' y 'imagenUrl'.

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
    val estado: String = PRODUCCION_STATES.first(), // Estado inicial: corte
    val imagenUrl: String = "",
    val mensaje: String? = null,
    // Almacena el input del usuario como String para luego convertirlo a Int
    val stockPorTalla: Map<String, String> = (35..42).associate { it.toString() to "" },
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
    // 🔗 LÓGICA DE URL DE IMAGEN
    // ============================================================

    /**
     * Transforma una URL compartida de Google Drive a una URL de descarga directa.
     */
    fun transformGoogleDriveUrl(url: String): String {
        if (url.isBlank()) return ""

        // Patrón de Google Drive: https://drive.google.com/file/d/{FILE_ID}/view?usp=sharing
        val driveRegex = Regex("""/d/([a-zA-Z0-9_-]+)/view""")

        val match = driveRegex.find(url)

        return if (match != null) {
            val fileId = match.groupValues[1]
            // Formato de descarga directa para usar con Coil o Glide
            "https://drive.google.com/uc?export=download&id=$fileId"
        } else if (url.contains("drive.google.com/uc?export=download")) {
            // Si ya es un link de descarga directa, retornarlo tal cual
            url
        } else {
            // Para URLs que no son de Drive o son URLs estándar
            url
        }
    }

    /**
     * 🟢 Actualiza el campo de imagen en el formulario.
     */
    fun updateImagenUrl(v: String) {
        _formState.value = _formState.value.copy(imagenUrl = v)
    }


    // ============================================================
    // 🔹 CARGA EN TIEMPO REAL DE PRODUCTOS EN PRODUCCIÓN
    // ============================================================
    fun cargarProductos() {
        db.collection("Productos")
            // Cargar solo los que estén en un estado de PRODUCCION_STATES (excluye "en stock")
            .whereIn("estado", PRODUCCION_STATES)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ProduccionVM", "Error: ${e.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    _productos.value = snapshot.documents.mapNotNull { doc ->
                        // Asignar el ID del documento al objeto Producto
                        doc.toObject(Producto::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    // ============================================================
    // 🟢 CREAR NUEVO PRODUCTO EN FIRESTORE
    // ============================================================
    fun crearProducto() {
        val data = _formState.value

        if (data.referencia.isBlank() || data.color.isBlank() || data.nombreModelo.isBlank()) {
            _formState.value = data.copy(mensaje = "Referencia, modelo y color son obligatorios")
            return
        }

        // PROCESAR STOCK POR TALLA: Convertir a Int y filtrar tallas con stock 0 o vacío
        val stockMapInt = data.stockPorTalla
            .mapValues { (_, value) -> value.toIntOrNull() ?: 0 }
            .filter { it.value > 0 }

        val imagenTransformada = transformGoogleDriveUrl(data.imagenUrl)

        if (stockMapInt.isEmpty()) {
            _formState.value = data.copy(mensaje = "Debe ingresar al menos una talla con stock inicial.")
            return
        }

        val producto = hashMapOf(
            "referencia" to data.referencia.trim(),
            "nombreModelo" to data.nombreModelo.trim(),
            "color" to data.color.trim(),
            "descripcion" to data.descripcion.trim(),
            "precioDetal" to data.precioDetal,
            "precioMayor" to data.precioMayor,
            "estado" to PRODUCCION_STATES.first(), // El estado inicial siempre es "corte"
            "imagenUrl" to imagenTransformada,
            "stockPorTalla" to stockMapInt,
            "timestamp" to Timestamp.now()
        )

        viewModelScope.launch {
            try {
                db.collection("Productos").add(producto).await()
                // Limpiar el formulario y mostrar mensaje
                _formState.value = ProduccionFormState(
                    mensaje = "✅ Producto guardado correctamente"
                )
            } catch (e: Exception) {
                _formState.value = data.copy(mensaje = "Error al guardar: ${e.message}")
            }
        }
    }

    // ============================================================
    // 🛠️ FUNCIONES DE ACTUALIZACIÓN DESDE ProduccionAdmin.kt
    // ============================================================

    /**
     * 🟢 NUEVA FUNCIÓN: Actualiza el stock por talla y la URL de la imagen de un producto.
     */
    fun updateProductoData(id: String, newStockMap: Map<String, Int>, newImageUrl: String) {
        viewModelScope.launch {
            try {
                val transformedUrl = transformGoogleDriveUrl(newImageUrl)

                val updates = hashMapOf<String, Any>(
                    "stockPorTalla" to newStockMap,
                    "imagenUrl" to transformedUrl
                )

                db.collection("Productos").document(id)
                    .update(updates)
                    .await()
                Log.d("ProduccionVM", "Datos de producto $id actualizados correctamente.")
            } catch (e: Exception) {
                Log.e("ProduccionVM", "Error al actualizar datos de producto $id: ${e.message}")
            }
        }
    }

    /**
     * Actualiza el estado de producción de un producto (incluye el paso a "en stock").
     */
    fun updateEstadoProducto(productoId: String, nuevoEstado: String) {
        if (!PRODUCTO_STATES_ALL.contains(nuevoEstado)) {
            Log.e("ProduccionVM", "Estado no válido: $nuevoEstado")
            return
        }
        viewModelScope.launch {
            try {
                db.collection("Productos").document(productoId)
                    .update("estado", nuevoEstado)
                    .await()
            } catch (e: Exception) {
                Log.e("ProduccionVM", "Error al actualizar estado: ${e.message}")
            }
        }
    }

    // ============================================================
    // ✏️ FUNCIONES DE ACTUALIZACIÓN DEL FORMULARIO
    // ============================================================

    fun updateStockForTalla(talla: String, stock: String) {
        _formState.value = _formState.value.copy(
            stockPorTalla = _formState.value.stockPorTalla.toMutableMap().apply { this[talla] = stock }
        )
    }

    fun updateReferencia(v: String) {
        _formState.value = _formState.value.copy(referencia = v)
    }

    fun updateColor(v: String) {
        _formState.value = _formState.value.copy(color = v)
    }

    fun updateNombre(v: String) {
        _formState.value = _formState.value.copy(nombreModelo = v)
    }

    fun updateDescripcion(v: String) {
        _formState.value = _formState.value.copy(descripcion = v)
    }

    fun updatePrecioDetal(v: String) {
        _formState.value = _formState.value.copy(precioDetal = v.toDoubleOrNull() ?: 0.0)
    }

    fun updatePrecioMayor(v: String) {
        _formState.value = _formState.value.copy(precioMayor = v.toDoubleOrNull() ?: 0.0)
    }

    // ============================================================
    // ASIGNAR IMAGEN AUTOMÁTICA SEGÚN COLOR
    // ============================================================
    fun asignarImagenPorColor(color: String): String {
        return when (color.lowercase().trim()) {
            "coñac", "conac" -> "drawable/zapato1"
            "blanco" -> "drawable/zapato2"
            "negro" -> "drawable/zapato3"
            else -> "drawable/ic_launcher_foreground"
        }
    }
}