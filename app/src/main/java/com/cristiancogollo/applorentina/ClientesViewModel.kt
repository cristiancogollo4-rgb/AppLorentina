package com.cristiancogollo.applorentina

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FilterType {
    NOMBRE, CEDULA, TELEFONO, DEPARTAMENTO
}

data class ClientesUiState(
    val clientes: List<Cliente> = emptyList(),
    val filteredClientes: List<Cliente> = emptyList(),
    val searchQuery: String = "",
    val filterType: FilterType = FilterType.NOMBRE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ClientesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(ClientesUiState())
    val uiState: StateFlow<ClientesUiState> = _uiState.asStateFlow()

    init {
        fetchClientes()
    }

    fun fetchClientes() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        try {
            // 🔥 Escucha en tiempo real los cambios en la colección
            db.collection("Clientes")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ClientesViewModel", "❌ Error escuchando cambios: ${error.message}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Error al escuchar datos: ${error.message}"
                            )
                        }
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        // 🔍 Log de depuración
                        snapshot.documents.forEach { doc ->
                            Log.d("ClientesViewModel", "📄 DOC ${doc.id} -> ${doc.data}")
                        }

                        val clientesList = snapshot.documents.mapNotNull { doc ->
                            try {
                                doc.toObject(Cliente::class.java)

                            } catch (e: Exception) {
                                Log.e("ClientesViewModel", "Error mapeando doc ${doc.id}: ${e.message}")
                                null
                            }
                        }

                        _uiState.update {
                            it.copy(
                                clientes = clientesList,
                                filteredClientes = clientesList,
                                isLoading = false,
                                errorMessage = null
                            )
                        }

                        Log.d("ClientesViewModel", "✅ Lista actualizada en tiempo real (${clientesList.size} clientes)")
                    }
                }

        } catch (e: Exception) {
            Log.e("ClientesViewModel", "Excepción general en fetchClientes()", e)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Error inesperado: ${e.message}"
                )
            }
        }
    }


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
        val query = current.searchQuery.lowercase().trim()

        if (query.isBlank()) {
            _uiState.update { it.copy(filteredClientes = current.clientes) }
            return
        }

        val filteredList = when (current.filterType) {
            FilterType.NOMBRE -> current.clientes.filter { it.nombreApellido.lowercase().contains(query) }
            FilterType.CEDULA -> current.clientes.filter { it.cedula.toString().contains(query) }
            FilterType.TELEFONO -> current.clientes.filter { it.telefono.toString().contains(query) }
            FilterType.DEPARTAMENTO -> current.clientes.filter { it.departamento.lowercase().contains(query) }
        }

        _uiState.update { it.copy(filteredClientes = filteredList) }
    }
}
