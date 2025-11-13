package com.cristiancogollo.applorentina

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import com.google.firebase.firestore.FirebaseFirestoreException

// =================================================================
// ESTRUCTURAS DE DATOS REQUERIDAS (Asumidas y Definidas para la compilación)
// =================================================================

data class VentaProductoItem(
    val idProducto: String = "",
    val referencia: String = "",
    val nombreModelo: String = "",
    val talla: String = "",
    val cantidad: Int = 1, // Se asume 1 unidad por item
    val precioUnitario: Double = 0.0 // Precio real aplicado en la venta
)


// =================================================================
// UI STATE (ACTUALIZADO CON CAMPOS DE PRODUCTO)
// =================================================================

data class NventaUiState(
    // Cliente
    val clienteBuscado: String = "",
    val clienteSeleccionado: Cliente? = null,
    val mensajeClienteNoEncontrado: String? = null,
    val allClientes: List<Cliente> = emptyList(),
    val clientesFiltrados: List<Cliente> = emptyList(),
    val isDropdownExpanded: Boolean = false,
    val isClientesLoading: Boolean = false,

    // 🟢 Productos (NUEVOS CAMPOS)
    val allProductos: List<Producto> = emptyList(), // Todos los productos "en stock"
    val productosDisponibles: List<Producto> = emptyList(), // Productos filtrados para el dropdown
    val productoBuscado: String = "",
    val productoSeleccionado: Producto? = null,
    val isProductoDropdownExpanded: Boolean = false,
    val tallaSeleccionada: String? = null,
    val isTallaDropdownExpanded: Boolean = false,
    val productosEnVenta: List<VentaProductoItem> = emptyList(), // Productos ya añadidos a la venta

    // Venta
    val precio: String = "", // Total de la venta
    val fechaVenta: Date = Date(),
    val descripcion: String = "",
    val esDetal: Boolean = true,
    val esVentaEspecial: Boolean = false,
    val isSaving: Boolean = false,
    val mensajeError: String? = null
)

// =================================================================
// VIEW MODEL (Lógica de Cliente y Producto)
// =================================================================

class NventaViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(NventaUiState())
    val uiState: StateFlow<NventaUiState> = _uiState.asStateFlow()

    private val _clienteSearchQueryFlow = MutableStateFlow("")
    private val _productoSearchQueryFlow =
        MutableStateFlow("") // 🟢 Nuevo flow para búsqueda de producto

    init {
        fetchClientes()
        fetchProductos() // 🟢 Nueva llamada
        collectSearchQueryWithDebounce() // Cliente
        collectProductoSearchQueryWithDebounce() // 🟢 Producto
    }

    // --- 1. LÓGICA DE CARGA DE DATOS ---

    /** Escucha Clientes en tiempo real */
    private fun fetchClientes() {
        _uiState.update { it.copy(isClientesLoading = true) }
        db.collection("Clientes").addSnapshotListener { snapshot, error ->
            // ... (Lógica de cliente existente) ...
            if (error != null) {
                Log.e("NventaVM", "Error escuchar clientes: ${error.message}", error)
                _uiState.update { it.copy(isClientesLoading = false) }
                return@addSnapshotListener
            }
            val clientes = snapshot?.toObjects(Cliente::class.java).orEmpty()
            _uiState.update { it.copy(allClientes = clientes, isClientesLoading = false) }
        }
    }

    /** 🟢 Escucha Productos "en stock" en tiempo real */
    private fun fetchProductos() {
        db.collection("Productos")
            .whereEqualTo("estado", "en stock") // Solo productos disponibles para venta inmediata
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NventaVM", "Error escuchar productos: ${error.message}", error)
                    return@addSnapshotListener
                }

                val productos = snapshot?.documents // 1. Accede de forma segura (da List<...>?)
                    .orEmpty()                      // 2. Si es nulo, devuelve una lista vacía (da List<...>)
                    .mapNotNull { doc ->            // 3. Ahora el mapeo es seguro
                        doc.toObject(Producto::class.java)?.copy(id = doc.id)
                    }.orEmpty()
                _uiState.update { it.copy(allProductos = productos) }
                // Re-filtrar productos después de la actualización de la lista maestra
                performProductoFilter(uiState.value.productoBuscado)
            }
    }


    // --- 2. LÓGICA DE BÚSQUEDA Y FILTRADO DE CLIENTE ---

    /** Debounce para autocompletar de cliente */
    private fun collectSearchQueryWithDebounce() {
        viewModelScope.launch {
            _clienteSearchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    val trimmed = query.trim()
                    val filtered = performClienteFilter(trimmed)
                    _uiState.update {
                        it.copy(
                            clientesFiltrados = filtered,
                            isDropdownExpanded = filtered.isNotEmpty() && trimmed.isNotEmpty()
                        )
                    }
                }
        }
    }

    fun onClienteBuscadoChange(query: String) {
        _uiState.update {
            it.copy(
                clienteBuscado = query,
                clienteSeleccionado = null,
                mensajeClienteNoEncontrado = null
            )
        }
        _clienteSearchQueryFlow.value = query
        if (query.isBlank()) _uiState.update { it.copy(isDropdownExpanded = false) }
    }

    private fun performClienteFilter(query: String): List<Cliente> {
        if (query.isBlank()) return emptyList()
        return uiState.value.allClientes.filter { c ->
            c.nombreApellido.startsWith(query, true) ||
                    c.cedula.toString().startsWith(query)
        }.take(10)
    }

    fun seleccionarCliente(cliente: Cliente?) {
        _uiState.update {
            if (cliente == null) it.copy(
                clienteSeleccionado = null,
                clienteBuscado = "",
                isDropdownExpanded = false,
                mensajeClienteNoEncontrado = null
            ) else it.copy(
                clienteSeleccionado = cliente,
                clienteBuscado = cliente.nombreApellido,
                isDropdownExpanded = false,
                mensajeClienteNoEncontrado = null
            )
        }
    }

    fun buscarClientePorNombre(query: String) {
        val q = query.trim()
        _uiState.update { it.copy(isDropdownExpanded = false) }
        val encontrado = uiState.value.allClientes.find {
            it.nombreApellido.equals(q, true) || it.cedula.toString() == q
        }
        if (encontrado != null) seleccionarCliente(encontrado) else
            _uiState.update { it.copy(mensajeClienteNoEncontrado = "Cliente no encontrado. Agrega uno nuevo.") }
    }

    fun dismissDropdown() = _uiState.update { it.copy(isDropdownExpanded = false) }


    // --- 3. 🟢 LÓGICA DE BÚSQUEDA Y SELECCIÓN DE PRODUCTO ---

    /** Debounce para productos */
    private fun collectProductoSearchQueryWithDebounce() {
        viewModelScope.launch {
            _productoSearchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    performProductoFilter(query.trim())
                }
        }
    }

    /** Lógica de filtrado de productos */
    private fun performProductoFilter(query: String) {
        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    productosDisponibles = it.allProductos.take(10),
                    isProductoDropdownExpanded = false
                )
            }
            return
        }

        val filtered = uiState.value.allProductos.filter { p ->
            p.referencia.startsWith(query, true) ||
                    p.nombreModelo.startsWith(query, true) ||
                    p.color.startsWith(query, true)
        }.take(10)

        _uiState.update {
            it.copy(
                productosDisponibles = filtered,
                isProductoDropdownExpanded = filtered.isNotEmpty()
            )
        }
    }

    fun onProductoBuscadoChange(query: String) {
        _uiState.update {
            it.copy(
                productoBuscado = query,
                productoSeleccionado = null,
                tallaSeleccionada = null // Limpiar talla si se cambia la búsqueda de producto
            )
        }
        _productoSearchQueryFlow.value = query
        if (query.isBlank()) _uiState.update { it.copy(isProductoDropdownExpanded = false) }
    }

    fun seleccionarProducto(producto: Producto?) {
        _uiState.update {
            it.copy(
                productoSeleccionado = producto,
                tallaSeleccionada = null, // Siempre limpiar talla cuando cambia el producto
                productoBuscado = producto?.referencia ?: "",
                isProductoDropdownExpanded = false,
                isTallaDropdownExpanded = false
            )
        }
    }

    fun seleccionarTalla(talla: String) {
        _uiState.update { it.copy(tallaSeleccionada = talla) }
    }

    fun toggleProductoDropdown(expanded: Boolean) =
        _uiState.update { it.copy(isProductoDropdownExpanded = expanded) }

    fun toggleTallaDropdown(expanded: Boolean) =
        _uiState.update { it.copy(isTallaDropdownExpanded = expanded) }

    /** 🟢 Añadir el producto seleccionado a la lista temporal de la venta */
    fun agregarProductoAVenta() {
        val s = uiState.value
        val prod = s.productoSeleccionado
        val talla = s.tallaSeleccionada

        if (prod == null || talla == null || s.esVentaEspecial) {
            _uiState.update { it.copy(mensajeError = "Seleccione producto y talla, o desactive Venta Especial.") }
            return
        }

        // Determinar el precio unitario
        val precioUnitario = if (s.esDetal) prod.precioDetal else prod.precioMayor

        val item = VentaProductoItem(
            idProducto = prod.id,
            referencia = prod.referencia,
            nombreModelo = prod.nombreModelo,
            talla = talla,
            cantidad = 1,
            precioUnitario = precioUnitario
        )

        // Reiniciar campos de selección y añadir item a la lista
        _uiState.update {
            it.copy(
                productosEnVenta = it.productosEnVenta + item,
                productoSeleccionado = null,
                tallaSeleccionada = null,
                productoBuscado = "",
                mensajeError = null
            )
        }
        _productoSearchQueryFlow.value = ""
    }


    // --- 4. LÓGICA DE FORMULARIO Y GUARDADO ---

    fun onPrecioChange(nuevo: String) {
        if (nuevo.all { it.isDigit() || it == '.' }) _uiState.update { it.copy(precio = nuevo) }
    }

    fun onFechaChange(nueva: Date) = _uiState.update { it.copy(fechaVenta = nueva) }
    fun onDescripcionChange(desc: String) = _uiState.update { it.copy(descripcion = desc) }
    fun toggleTipoVenta(esDetal: Boolean) = _uiState.update { it.copy(esDetal = esDetal) }
    fun toggleVentaEspecial(isSpecial: Boolean) = _uiState.update {
        // Solo puede ser especial si es por mayor
        if (!it.esDetal) it.copy(esVentaEspecial = isSpecial) else it.copy(esVentaEspecial = false)
    }

    /** Guarda la venta en Firestore */
    fun guardarVenta(onSaveSuccess: () -> Unit, onError: (String) -> Unit = {}) {
        Log.d("VentaDebug", "Función guardarVenta() del ViewModel ejecutada.")
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, mensajeError = null) }

            val s = uiState.value
            val cli = s.clienteSeleccionado
            val precioNullable = s.precio.toDoubleOrNull()

            // VALIDACIÓN: (Mantiene las validaciones existentes)
            // ... (Tu código de validación existente) ...

            if (cli == null || precioNullable == null || (!s.esVentaEspecial && s.productosEnVenta.isEmpty())) {
                // ... (Tu manejo de error existente) ...
                return@launch
            }

            // FIX CLAVE: Pre-generamos la referencia y el ID ANTES de la transacción
            // Esto asegura que la referencia sea válida y que el ID exista.
            val ventaRef = db.collection("Ventas").document()
            val newVentaId = ventaRef.id // ID único generado

            val precioFinal: Double = precioNullable!!

            // 1. Iniciar la transacción de Firestore
            db.runTransaction { transaction ->

                // --- FASE 1: LECTURA DE DOCUMENTOS (READS) ---
                val productosAfectados = mutableMapOf<String, Producto>()
                val nuevosStock = mutableMapOf<String, Map<String, Int>>()

                if (!s.esVentaEspecial) {
                    // ... (Tu lógica de lectura de productos y cálculo de stock sigue aquí) ...
                    // Asegúrate de que TODAS las llamadas a transaction.get() se hagan antes de cualquier escritura.
                    s.productosEnVenta.forEach { itemVendido ->
                        val productoRef = db.collection("Productos").document(itemVendido.idProducto)

                        // 🟢 LECTURA
                        val productoDoc = transaction.get(productoRef)

                        // ... (Validación de stock y cálculo de nuevoMapaStock) ...

                        val productoActual = productoDoc.toObject(Producto::class.java)
                            ?: throw FirebaseFirestoreException(
                                "Producto no encontrado: ${itemVendido.idProducto}",
                                FirebaseFirestoreException.Code.ABORTED
                            )

                        // ... (Cálculo del nuevo stock y guardado en nuevosStock) ...
                        val stockActual = productoActual.stockPorTalla[itemVendido.talla] ?: 0

                        if (stockActual < itemVendido.cantidad) {
                            throw FirebaseFirestoreException(
                                "Stock insuficiente para ${itemVendido.referencia} talla ${itemVendido.talla}",
                                FirebaseFirestoreException.Code.ABORTED
                            )
                        }

                        val nuevoStockTalla = stockActual - itemVendido.cantidad
                        val nuevoMapaStock = productoActual.stockPorTalla.toMutableMap()
                        nuevoMapaStock[itemVendido.talla] = nuevoStockTalla

                        nuevosStock[itemVendido.idProducto] = nuevoMapaStock.toMap() as Map<String, Int>
                    }
                }

                // --- FASE 2: ESCRITURA DE DOCUMENTOS (WRITES) ---

                // 1. ACTUALIZAR PRODUCTOS
                if (!s.esVentaEspecial) {
                    nuevosStock.forEach { (idProducto, stockMap) ->
                        val productoRef = db.collection("Productos").document(idProducto)
                        // 🟢 ESCRITURA 1: Update de stock
                        transaction.update(
                            productoRef,
                            "stockPorTalla", stockMap
                        )
                    }
                }

                // 2. CREAR VENTA
                val venta = Venta(
                    idVenta = newVentaId, // ⬅️ Usamos el ID pre-generado
                    cliente = cli,
                    fechaVenta = s.fechaVenta,
                    precioTotal = precioFinal,
                    descripcion = s.descripcion,
                    esDetal = s.esDetal,
                    esVentaEspecial = s.esVentaEspecial,
                    productos = s.productosEnVenta
                )

                // 🟢 ESCRITURA 2: Creación de Venta. Usamos la referencia pre-generada.
                transaction.set(ventaRef, venta)

                null
                      // Retorna null al finalizar la transacción exitosamente
            }
                // ******************************************************
                // 💡 AÑADIDO: Manejo de éxito y fracaso para la transacción
                // ******************************************************
                .addOnSuccessListener {
                    Log.d("VentaDebug", "✅ Venta guardada con éxito mediante Transacción.")
                    _uiState.update { it.copy(isSaving = false) }
                    onSaveSuccess()
                }
                .addOnFailureListener { e ->
                    // 🔴 ESTE Log.e CAPTURARÁ EL ERROR SILENCIOSO (p. ej., DEVELOPER_ERROR o stock insuficiente)
                    Log.e("VentaDebug", "❌ ERROR al guardar la venta en Firestore. Causa:", e)

                    val errorMsg = when (e) {
                        is FirebaseFirestoreException -> e.message
                            ?: "Error de transacción (código: ${e.code.name})."

                        else -> "Error de conexión o inesperado al guardar la venta: ${e.localizedMessage}"
                    }

                    _uiState.update { it.copy(isSaving = false, mensajeError = "Error: $errorMsg") }
                    onError(errorMsg)
                }
        }
    }
}