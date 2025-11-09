package com.cristiancogollo.applorentina

data class Producto(
    // 🟢 CLAVE: Referencia o Nombre (para la búsqueda)
    val referencia: String = "",
    val nombreModelo: String = "", // Nombre del modelo (ej: 'Tenis Urbano')
    val color: String = "",
    val descripcion: String = "",
    val precioDetal: Double = 0.0,
    val precioMayor: Double = 0.0,

    // 🟢 CLAVE: Mapa donde la clave es la talla ("35" a "42") y el valor es la cantidad
    val stockPorTalla: Map<String, Int> = getDefaultStockMap(),

    val imagenUrl: String = "",
    val categoria: String = "" // (ej: 'Tenis', 'Botín', 'Sandalia')
)

// Función auxiliar para inicializar el mapa en Firestore con tallas de 35 a 42 en 0.
fun getDefaultStockMap(): Map<String, Int> {
    val defaultMap = mutableMapOf<String, Int>()
    for (talla in 35..42) {
        defaultMap[talla.toString()] = 0
    }
    return defaultMap
}