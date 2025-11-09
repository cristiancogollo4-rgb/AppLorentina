package com.cristiancogollo.applorentina


import Cliente
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FilterType {
    NOMBRE, CEDULA, TELEFONO, DEPARTAMENTO
}
// Estado de la UI para la pantalla de búsqueda
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

    // Estado privado que solo el ViewModel puede modificar
    private val _uiState = MutableStateFlow(ClientesUiState())
    // Estado público que la UI observará
    val uiState: StateFlow<ClientesUiState> = _uiState.asStateFlow()

    init {
        fetchClientes()
    }

    /**
     * Obtiene la lista de clientes desde Firestore.
     * IMPORTANTE: Debes ajustar la ruta a tu colección.
     */
    fun fetchClientes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // 👇 AJUSTA ESTA RUTA a la de tu base de datos
                // Ejemplo: "artifacts/{appId}/users/{userId}/clientes"
                db.collection("Clientes")
                    .get()
                    .addOnSuccessListener { result ->
                        val clientesList = result.documents.mapNotNull { doc ->
                            doc.toObject(Cliente::class.java)?.copy(id = doc.id)
                        }
                        _uiState.update {
                            it.copy(
                                clientes = clientesList,
                                filteredClientes = clientesList, // Inicialmente, la lista filtrada es la completa
                                isLoading = false
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ClientesViewModel", "Error al obtener clientes", exception)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Error al cargar los clientes: ${exception.message}"
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("ClientesViewModel", "Excepción al obtener clientes", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Actualiza la consulta de búsqueda y aplica el filtro.
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    /**
     * Actualiza el tipo de filtro y vuelve a aplicar el filtro actual.
     */
    fun updateFilterType(filterType: FilterType) {
        _uiState.update { it.copy(filterType = filterType) }
        applyFilter()
    }

    /**
     * Lógica principal de filtrado. Se ejecuta cada vez que cambia la consulta o el tipo de filtro.
     */
    private fun applyFilter() {
        val currentState = _uiState.value
        val query = currentState.searchQuery.lowercase().trim()

        if (query.isBlank()) {
            _uiState.update { it.copy(filteredClientes = currentState.clientes) }
            return
        }

        val filteredList = when (currentState.filterType) {
            // El nombre es String, así que la lógica es la misma
            FilterType.NOMBRE -> currentState.clientes.filter { it.nombre.lowercase().contains(query) }

            // Convierte el Long 'cedula' a String para poder buscar en él
            FilterType.CEDULA -> currentState.clientes.filter { it.cedula.toString().contains(query) }

            // Convierte el Long 'telefono' a String para poder buscar en él
            FilterType.TELEFONO -> currentState.clientes.filter { it.telefono.toString().contains(query) }

            // El departamento es String, así que la lógica es la misma que para el nombre
            FilterType.DEPARTAMENTO -> currentState.clientes.filter { it.departamento.lowercase().contains(query) }
        }

        _uiState.update { it.copy(filteredClientes = filteredList) }
    }
}
