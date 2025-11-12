package com.cristiancogollo.applorentina

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class FilterType { NOMBRE, CEDULA, TELEFONO, DEPARTAMENTO }

data class ClientesUiState(
    val clientes: List<Cliente> = emptyList(),
    val filteredClientes: List<Cliente> = emptyList(),
    val searchQuery: String = "",
    val filterType: FilterType = FilterType.NOMBRE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showAddDialog: Boolean = false,      // controlar diálogo agregar
    val selectedCliente: Cliente? = null,     // para ver detalles
    val showDetailsDialog: Boolean = false    // controlar detalles
)

class ClientesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(ClientesUiState())
    val uiState: StateFlow<ClientesUiState> = _uiState.asStateFlow()

    init { fetchClientes() }

    // -------------------------
    // Realtime fetch
    // -------------------------
    fun fetchClientes() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        db.collection("Clientes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ClientesViewModel", "❌ Error escuchando cambios: ${error.message}")
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al escuchar datos: ${error.message}") }
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val clientesList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val c = doc.toObject(Cliente::class.java)
                            c?.copy(documentId = doc.id)
                        } catch (e: Exception) {
                            Log.e("ClientesViewModel", "Error mapeando ${doc.id}: ${e.message}")
                            null
                        }
                    }

                    _uiState.update { current ->
                        current.copy(
                            clientes = clientesList,
                            filteredClientes = applyFilterList(clientesList, current.searchQuery, current.filterType),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
    }

    // -------------------------
    // Search/Filter
    // -------------------------
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    fun updateFilterType(filterType: FilterType) {
        _uiState.update { it.copy(filterType = filterType) }
        applyFilter()
    }

    private fun applyFilter() {
        val current = _uiState.value
        _uiState.update {
            it.copy(
                filteredClientes = applyFilterList(current.clientes, current.searchQuery, current.filterType)
            )
        }
    }

    private fun applyFilterList(
        base: List<Cliente>,
        queryRaw: String,
        type: FilterType
    ): List<Cliente> {
        val q = queryRaw.lowercase().trim()
        if (q.isBlank()) return base

        return when (type) {
            FilterType.NOMBRE -> base.filter { it.nombreApellido.lowercase().contains(q) }
            FilterType.CEDULA -> base.filter { it.cedula.toString().contains(q) }
            FilterType.TELEFONO -> base.filter { it.telefono.toString().contains(q) }
            FilterType.DEPARTAMENTO -> base.filter { it.departamento.lowercase().contains(q) }
        }
    }

    // -------------------------
    // CRUD
    // -------------------------
    fun addCliente(
        nombreApellido: String,
        cedula: String,
        telefono: String,
        correo: String,
        departamento: String,
        municipio: String,
        tipoCliente: Boolean,
        onResult: (Boolean, String?) -> Unit
    ) {
        val cedulaLong = cedula.toLongOrNull()
        val telLong = telefono.toLongOrNull()
        if (nombreApellido.isBlank() || cedulaLong == null || telLong == null) {
            onResult(false, "Nombre, cédula y teléfono son obligatorios y deben ser válidos.")
            return
        }

        val data = hashMapOf(
            "nombreApellido" to nombreApellido.trim(),
            "cedula" to cedulaLong,
            "telefono" to telLong,
            "correo" to correo.trim(),
            "departamento" to departamento.trim(),
            "municipio" to municipio.trim(),
            "tipoCliente" to tipoCliente,
            "timestamp" to Timestamp.now()
        )

        db.collection("Clientes")
            .add(data)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun deleteCliente(documentId: String, onError: (String) -> Unit = {}) {
        if (documentId.isBlank()) return
        db.collection("Clientes").document(documentId)
            .delete()
            .addOnFailureListener { e ->
                onError(e.message ?: "Error eliminando")
            }
    }

    // -------------------------
    // UI Dialogs control
    // -------------------------
    fun openAddDialog() { _uiState.update { it.copy(showAddDialog = true) } }
    fun closeAddDialog() { _uiState.update { it.copy(showAddDialog = false) } }

    fun openDetailsDialog(cliente: Cliente) {
        _uiState.update { it.copy(selectedCliente = cliente, showDetailsDialog = true) }
    }

    fun closeDetailsDialog() {
        _uiState.update { it.copy(selectedCliente = null, showDetailsDialog = false) } }
}
