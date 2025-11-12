package com.cristiancogollo.applorentina

import com.google.firebase.Timestamp

data class Producto(
    val id: String = "", // ID del documento de Firestore
    // Referencia o Nombre (para la búsqueda)
    val referencia: String = "",
    val nombreModelo: String = "", // Nombre del modelo
    val color: String = "",
    val descripcion: String = "",
    val precioDetal: Double = 0.0,
    val precioMayor: Double = 0.0,

    // 🟢 Estado del producto: "en producción", "en stock", "agotado", etc.
    val estado: String = "en producción", //

    // 🟢 CLAVE: Mapa donde la clave es la talla ("35" a "42") y el valor es la cantidad
    val stockPorTalla: Map<String, Int> = getDefaultStockMap(),

    val imagenUrl: String = "",
    val timestamp: Timestamp? = null
)

// Función auxiliar para inicializar el mapa en Firestore con tallas de 35 a 42 en 0.
fun getDefaultStockMap(): Map<String, Int> {
    val defaultMap = mutableMapOf<String, Int>()
    for (talla in 35..42) {
        defaultMap[talla.toString()] = 0
    }
    return defaultMap
}



