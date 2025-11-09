import com.google.firebase.firestore.PropertyName

data class Cliente(
    val id: String = "",
    @PropertyName("C.C.") val cedula: Long = 0L,
    @PropertyName("Nombre_Apellido") val nombre: String = "",
    @PropertyName("Telefono") val telefono: Long = 0L,
    @PropertyName("Correo") val correo: String = "",
    @PropertyName("Departamento") val departamento: String = "",
    @PropertyName("Municipio") val municipio: String = "",
    @PropertyName("TipoCliente") val tipoCliente: Boolean = false,
    val timestamp: com.google.firebase.Timestamp? = null
)