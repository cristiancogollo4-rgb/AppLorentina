package com.cristiancogollo.applorentina

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.map // 👈 Añadir este
import kotlinx.coroutines.flow.SharingStarted // 👈 Añadir este
import kotlinx.coroutines.flow.stateIn // 👈 Añadir este

// ========================
// 🗺️ Estructuras de Datos
// ========================

// Estado del formulario de Agregar Cliente
data class ClienteFormState(
    val nombre: String = "",
    val cedula: String = "",
    val telefono: String = "",
    val correo: String = "",
    val isDetalSelected: Boolean = true, // Tipo Cliente: true = Detal, false = Por Mayor
    val departamentoSeleccionado: String = "",
    val municipioSeleccionado: String = ""
)

// Estado de la inicialización de Firebase
data class FirebaseInitializationState(
    val db: FirebaseFirestore? = null,
    val userId: String = "",
    val appId: String = "",
    val isInitialized: Boolean = false,
    val error: String? = null
)

// ========================
// 🧠 VIEWMODEL
// ========================

class ClienteViewModel(private val context: Context) : ViewModel() {

    // --- ESTADOS ---
    private val _formState = MutableStateFlow(ClienteFormState())
    val formState: StateFlow<ClienteFormState> = _formState

    private val _firebaseState = MutableStateFlow(FirebaseInitializationState())
    val firebaseState: StateFlow<FirebaseInitializationState> = _firebaseState // Usado para habilitar/deshabilitar la UI

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Lista de clientes para la pantalla de búsqueda/listado
    private val _clientesList = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val clientesList: StateFlow<List<Map<String, Any>>> = _clientesList
    val isFormValid: StateFlow<Boolean> = formState.map { form ->
        // La validación comprueba que los campos obligatorios no estén vacíos
        form.nombre.isNotBlank() &&
                form.cedula.isNotBlank() &&
                form.telefono.isNotBlank() &&
                form.departamentoSeleccionado.isNotBlank() &&
                form.municipioSeleccionado.isNotBlank()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // --- DATOS DE UBICACIÓN ---
    private val _departamentosMap = MutableStateFlow<Map<String, List<String>>>(emptyMap())

    val departamentos: List<String>
        get() = _departamentosMap.value.keys.toList().sorted()

    val municipios: List<String>
        get() = _departamentosMap.value[_formState.value.departamentoSeleccionado] ?: emptyList()


    init {
        initializeFirebaseAndLoadData()
    }

    // ===============================================
    // ⚙️ Inicialización de Firebase (con .await() y Fallbacks)
    // ===============================================

    private fun initializeFirebaseAndLoadData() {
        viewModelScope.launch {
            try {
                // 1. Cargar datos de ubicación
                val map = leerDepartamentos(context)
                _departamentosMap.value = map

                // 2. Obtener referencias
                val auth = Firebase.auth
                val db = Firebase.firestore
                val resources = context.resources
                val packageName = context.packageName

                // 🌟 Manejo seguro de recursos (Soluciona el error 0x0)
                val authTokenId = resources.getIdentifier("__initial_auth_token", "string", packageName)
                val appIdId = resources.getIdentifier("__app_id", "string", packageName)

                val authToken = if (authTokenId != 0) resources.getString(authTokenId) else "null"
                val appId = if (appIdId != 0) resources.getString(appIdId) else "test-app-id"

                // 3. Intentar autenticación (usando await() para Coroutines)
                val userId = if (authToken.isNotEmpty() && authToken != "null") {
                    // Intenta sign-in con token
                    auth.signInWithCustomToken(authToken).await().user?.uid ?: "anonymous"
                } else {
                    // Intenta sign-in anónima si no hay token válido
                    auth.signInAnonymously().await().user?.uid ?: "anonymous"
                }

                _firebaseState.value = FirebaseInitializationState(
                    db = db,
                    userId = userId,
                    appId = appId,
                    isInitialized = true
                )
                Log.d("ClienteViewModel", "Firebase inicializado con éxito. UserID: $userId, AppId: $appId")

            } catch (e: Exception) {
                // Captura errores de Auth (Task is not yet complete) o permisos
                Log.e("ClienteViewModel", "Error al inicializar Firebase o Auth: ${e.message}")
                _firebaseState.value = _firebaseState.value.copy(error = "Error de conexión: ${e.message}")
                _message.value = "Error de conexión: ${e.message}"
            }
        }
    }

    // ===============================================
    // 🖊️ Handlers de Formulario
    // ===============================================

    fun updateNombre(nombre: String) { _formState.value = _formState.value.copy(nombre = nombre) }
    fun updateCedula(cedula: String) { _formState.value = _formState.value.copy(cedula = cedula) }
    fun updateTelefono(telefono: String) { _formState.value = _formState.value.copy(telefono = telefono) }
    fun updateCorreo(correo: String) { _formState.value = _formState.value.copy(correo = correo) }

    fun setTipoCliente(isDetal: Boolean) {
        _formState.value = _formState.value.copy(isDetalSelected = isDetal)
    }

    fun updateDepartamento(departamento: String) {
        _formState.value = _formState.value.copy(
            departamentoSeleccionado = departamento,
            municipioSeleccionado = ""
        )
    }

    fun updateMunicipio(municipio: String) {
        _formState.value = _formState.value.copy(municipioSeleccionado = municipio)
    }

    fun clearMessage() {
        _message.value = null
    }

    // ===============================================
    // 💾 CRUD CLIENTES (usando Coroutines)
    // ===============================================

    // 🌟 1. GUARDAR/AGREGAR CLIENTE (CREATE)
    fun saveCliente() {
        val state = _firebaseState.value
        val form = _formState.value

        if (!state.isInitialized || state.db == null || state.userId.isEmpty()) {
            _message.value = "Error: La conexión a la base de datos no está lista. Intente de nuevo."
            return
        }

        if (form.nombre.isBlank() || form.cedula.isBlank() || form.telefono.isBlank() || form.departamentoSeleccionado.isBlank() || form.municipioSeleccionado.isBlank()) {
            _message.value = "Por favor, complete todos los campos obligatorios."
            return
        }

        viewModelScope.launch {
            try {
                // Claves de Firestore ajustadas a tus imágenes
                val clienteData: Map<String, Any> = mapOf(
                    "Nombre_Apellido" to form.nombre.trim(),
                    "C.C" to form.cedula.trim(),
                    "Telefono" to form.telefono.trim(),
                    "Correo" to form.correo.trim(),
                    "Departamento" to form.departamentoSeleccionado,
                    "Municipio" to form.municipioSeleccionado,
                    "TipoCliente" to form.isDetalSelected,
                    "timestamp" to Timestamp.now()
                )

                // Ruta: /artifacts/{appId}/users/{userId}/clientes
                val path = "Clientes"
                val collectionRef = state.db.collection(path)

                // USANDO .await() para guardado
                collectionRef.add(clienteData).await()

                _message.value = "Cliente '${form.nombre.trim()}' agregado con éxito!"
                _formState.value = ClienteFormState() // Limpiar formulario

            } catch (e: Exception) {
                _message.value = "Error al guardar: ${e.message}"
            }
        }
    }

    // 🌟 2. OBTENER/LISTAR CLIENTES (READ)
    fun fetchClientes() {
        val state = _firebaseState.value
        if (!state.isInitialized || state.db == null || state.userId.isEmpty()) {
            Log.e("ClienteViewModel", "No se puede obtener clientes, DB no inicializada.")
            return
        }

        viewModelScope.launch {
            try {
                // Ruta de la colección
                val path = "Clientes"

                // USANDO .await() para obtener documentos
                val result = state.db.collection(path).get().await()

                val clientes = result.documents.map { doc ->
                    // Mapea el documento a un Map<String, Any> e incluye el ID de Firestore
                    doc.data?.toMutableMap()?.apply {
                        put("documentId", doc.id)
                    } ?: mapOf("documentId" to doc.id)
                }

                @Suppress("UNCHECKED_CAST")
                _clientesList.value = clientes as List<Map<String, Any>>
                Log.d("ClienteViewModel", "Clientes obtenidos: ${_clientesList.value.size}")

            } catch (e: Exception) {
                Log.e("ClienteViewModel", "Error al obtener clientes: ${e.message}")
                _clientesList.value = emptyList()
            }
        }
    }

    // ... (Aquí irían las funciones de editar y eliminar cliente si fueran necesarias)
}

// ===============================================
// 🛠️ Función de Lectura de JSON (Utilidad)
// ===============================================

fun leerDepartamentos(context: Context): Map<String, List<String>> {
    return try {
        // Asegúrate de que colombia.json esté en la carpeta assets/
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

class ClienteViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClienteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClienteViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}