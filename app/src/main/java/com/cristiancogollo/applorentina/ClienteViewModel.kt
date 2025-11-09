package com.cristiancogollo.applorentina

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray

data class ClienteFormState(
    val nombre: String = "",
    val cedula: String = "",
    val telefono: String = "",
    val correo: String = "",
    val isDetalSelected: Boolean = true,
    val departamentoSeleccionado: String = "",
    val municipioSeleccionado: String = ""
)

data class AgregarClienteUiState(
    val formState: ClienteFormState = ClienteFormState(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val isSaveSuccessful: Boolean = false
)

class AgregarClienteViewModel(private val context: Context) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(AgregarClienteUiState())
    val uiState: StateFlow<AgregarClienteUiState> = _uiState.asStateFlow()

    private val _departamentosMap = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val departamentos: List<String>
        get() = _departamentosMap.value.keys.toList().sorted()
    val municipios: List<String>
        get() = _departamentosMap.value[_uiState.value.formState.departamentoSeleccionado] ?: emptyList()

    val isFormValid: StateFlow<Boolean> = _uiState.map { state ->
        state.formState.nombre.isNotBlank() &&
                state.formState.cedula.isNotBlank() &&
                state.formState.telefono.isNotBlank() &&
                state.formState.departamentoSeleccionado.isNotBlank() &&
                state.formState.municipioSeleccionado.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        cargarDatosDeUbicacion()
    }

    private fun cargarDatosDeUbicacion() {
        viewModelScope.launch {
            try {
                val map = leerDepartamentos(context)
                _departamentosMap.value = map
                Log.d("AgregarClienteVM", "Datos de ubicación cargados correctamente.")
            } catch (e: Exception) {
                Log.e("AgregarClienteVM", "Error al cargar datos: ${e.message}")
                _uiState.update { it.copy(message = "Error al cargar ubicaciones.") }
            }
        }
    }

    fun updateNombre(nombre: String) = _uiState.update { it.copy(formState = it.formState.copy(nombre = nombre)) }
    fun updateCedula(cedula: String) = _uiState.update { it.copy(formState = it.formState.copy(cedula = cedula)) }
    fun updateTelefono(telefono: String) = _uiState.update { it.copy(formState = it.formState.copy(telefono = telefono)) }
    fun updateCorreo(correo: String) = _uiState.update { it.copy(formState = it.formState.copy(correo = correo)) }
    fun setTipoCliente(isDetal: Boolean) = _uiState.update { it.copy(formState = it.formState.copy(isDetalSelected = isDetal)) }

    fun updateDepartamento(departamento: String) {
        _uiState.update {
            it.copy(
                formState = it.formState.copy(
                    departamentoSeleccionado = departamento,
                    municipioSeleccionado = ""
                )
            )
        }
    }

    fun updateMunicipio(municipio: String) = _uiState.update { it.copy(formState = it.formState.copy(municipioSeleccionado = municipio)) }

    fun clearMessage() = _uiState.update { it.copy(message = null) }
    fun resetSaveState() = _uiState.update { it.copy(isSaveSuccessful = false) }

    fun saveCliente() {
        val form = _uiState.value.formState

        if (!isFormValid.value) {
            _uiState.update { it.copy(message = "Por favor, complete todos los campos obligatorios.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, message = null) }

        viewModelScope.launch {
            try {
                val cedulaAsLong = form.cedula.toLongOrNull()
                val telefonoAsLong = form.telefono.toLongOrNull()

                if (cedulaAsLong == null || telefonoAsLong == null) {
                    _uiState.update { it.copy(isLoading = false, message = "Cédula o teléfono no válidos.") }
                    return@launch
                }

                val clienteData = mapOf(
                    "cedula" to cedulaAsLong,
                    "nombreApellido" to form.nombre.trim(),
                    "telefono" to telefonoAsLong,
                    "correo" to form.correo.trim(),
                    "departamento" to form.departamentoSeleccionado,
                    "municipio" to form.municipioSeleccionado,
                    "tipoCliente" to form.isDetalSelected,
                    "timestamp" to Timestamp.now()
                )

                db.collection("Clientes").add(clienteData).await()

                _uiState.update {
                    AgregarClienteUiState(
                        message = "Cliente '${form.nombre.trim()}' agregado con éxito!",
                        isSaveSuccessful = true
                    )
                }
                Log.d("AgregarClienteVM", "Cliente guardado exitosamente.")
            } catch (e: Exception) {
                Log.e("AgregarClienteVM", "Error al guardar cliente", e)
                _uiState.update { it.copy(isLoading = false, message = "Error al guardar: ${e.message}") }
            }
        }
    }
}

fun leerDepartamentos(context: Context): Map<String, List<String>> {
    return try {
        val jsonText = context.assets.open("colombia.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonText)
        val map = mutableMapOf<String, List<String>>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val departamento = item.getString("departamento")
            val municipiosJson = item.getJSONArray("ciudades")
            val municipios = mutableListOf<String>()
            for (j in 0 until municipiosJson.length()) {
                municipios.add(municipiosJson.getString(j))
            }
            map[departamento] = municipios
        }
        map
    } catch (e: Exception) {
        Log.e("LeerJSON", "Error leyendo colombia.json: ${e.message}")
        emptyMap()
    }
}

class AgregarClienteViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgregarClienteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgregarClienteViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
