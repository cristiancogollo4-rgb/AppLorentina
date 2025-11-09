package com.cristiancogollo.applorentina

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray

// ========================
// 🗺️ Estructuras de Datos
// ========================

// Estado del formulario de Agregar Cliente
data class ClienteFormState(
    val nombre: String = "",
    val cedula: String = "", // Se mantiene como String en el formulario, pero se convierte a Long al guardar
    val telefono: String = "", // Se mantiene como String en el formulario, pero se convierte a Long al guardar
    val correo: String = "",
    val isDetalSelected: Boolean = true, // Tipo Cliente: true = Detal, false = Por Mayor
    val departamentoSeleccionado: String = "",
    val municipioSeleccionado: String = ""
)

// Estado general de la UI para esta pantalla
data class AgregarClienteUiState(
    val formState: ClienteFormState = ClienteFormState(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val isSaveSuccessful: Boolean = false
)

// ========================
// 🧠 VIEWMODEL
// ========================

class AgregarClienteViewModel(private val context: Context) : ViewModel() {

    // Instancia de Firestore, obtenida de forma simple
    private val db = FirebaseFirestore.getInstance()

    // --- ESTADOS ---
    private val _uiState = MutableStateFlow(AgregarClienteUiState())
    val uiState: StateFlow<AgregarClienteUiState> = _uiState.asStateFlow()

    // --- DATOS DE UBICACIÓN ---
    private val _departamentosMap = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val departamentos: List<String>
        get() = _departamentosMap.value.keys.toList().sorted()

    val municipios: List<String>
        get() = _departamentosMap.value[_uiState.value.formState.departamentoSeleccionado] ?: emptyList()

    // Estado derivado para validar el formulario de forma reactiva
    val isFormValid: StateFlow<Boolean> = _uiState.map { state ->
        state.formState.nombre.isNotBlank() &&
                state.formState.cedula.isNotBlank() &&
                state.formState.telefono.isNotBlank() &&
                state.formState.departamentoSeleccionado.isNotBlank() &&
                state.formState.municipioSeleccionado.isNotBlank()
    }.stateIn( // <-- LÍNEA CORRECTA
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        cargarDatosDeUbicacion()
    }

    // ===============================================
    // 🗂️ Cargar Datos de Ubicación desde JSON
    // ===============================================
    private fun cargarDatosDeUbicacion() {
        viewModelScope.launch {
            try {
                val map = leerDepartamentos(context)
                _departamentosMap.value = map
                Log.d("AgregarClienteVM", "Datos de ubicación cargados correctamente.")
            } catch (e: Exception) {
                Log.e("AgregarClienteVM", "Error al cargar datos de ubicación: ${e.message}")
                _uiState.update { it.copy(message = "No se pudieron cargar los departamentos.") }
            }
        }
    }

    // ===============================================
    // 🖊️ Handlers de Formulario
    // ===============================================
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
                    municipioSeleccionado = "" // Se limpia el municipio al cambiar de departamento
                )
            )
        }
    }

    fun updateMunicipio(municipio: String) = _uiState.update { it.copy(formState = it.formState.copy(municipioSeleccionado = municipio)) }

    fun clearMessage() = _uiState.update { it.copy(message = null) }
    fun resetSaveState() = _uiState.update { it.copy(isSaveSuccessful = false) }

    // ===============================================
    // 💾 GUARDAR CLIENTE (CORREGIDO Y SIMPLIFICADO)
    // ===============================================
    fun saveCliente() {
        val form = _uiState.value.formState

        // 1. Validar que el formulario sea válido (aunque el botón ya debería estar deshabilitado)
        if (!isFormValid.value) {
            _uiState.update { it.copy(message = "Por favor, complete todos los campos obligatorios.") }
            return
        }

        // 2. Mostrar estado de carga
        _uiState.update { it.copy(isLoading = true, message = null) }

        viewModelScope.launch {
            try {
                // 3. CORRECCIÓN CLAVE: Convertir cédula y teléfono a Long
                val cedulaAsLong = form.cedula.toLongOrNull()
                val telefonoAsLong = form.telefono.toLongOrNull()

                if (cedulaAsLong == null) {
                    _uiState.update { it.copy(isLoading = false, message = "La cédula debe ser un número válido.") }
                    return@launch
                }
                if (telefonoAsLong == null) {
                    _uiState.update { it.copy(isLoading = false, message = "El teléfono debe ser un número válido.") }
                    return@launch
                }

                // 4. Crear el mapa de datos con los TIPOS y NOMBRES CORRECTOS
                val clienteData = mapOf(
                    "C.C." to cedulaAsLong, // NOMBRE CORRECTO y TIPO Long
                    "Nombre_Apellido" to form.nombre.trim(),
                    "Telefono" to telefonoAsLong, // TIPO Long
                    "Correo" to form.correo.trim(),
                    "Departamento" to form.departamentoSeleccionado,
                    "Municipio" to form.municipioSeleccionado,
                    "TipoCliente" to form.isDetalSelected,
                    "timestamp" to Timestamp.now()
                )

                // 5. Guardar en Firestore
                db.collection("Clientes").add(clienteData).await()

                // 6. Éxito: Limpiar formulario y notificar al usuario
                _uiState.update {
                    AgregarClienteUiState( // Resetea el estado completo
                        message = "Cliente '${form.nombre.trim()}' agregado con éxito!",
                        isSaveSuccessful = true
                    )
                }
                Log.d("AgregarClienteVM", "Cliente guardado exitosamente.")

            } catch (e: Exception) {
                // 7. Error: Notificar al usuario
                Log.e("AgregarClienteVM", "Error al guardar cliente", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = "Error al guardar: ${e.message}"
                    )
                }
            }
        }
    }
}

// ===============================================
// 🛠️ Función de Lectura de JSON (Sin cambios)
// ===============================================
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

// ===============================================
// 🏭 ViewModelFactory (Necesario por el Context)
// ===============================================
class AgregarClienteViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgregarClienteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgregarClienteViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}