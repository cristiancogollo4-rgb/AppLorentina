package com.cristiancogollo.applorentina

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

// Define el estado principal de la UI
data class EstadisticasUiState(
    val tipoVista: TipoVenta = TipoVenta.DETAL,
    val datosGrafico: List<Float> = emptyList(),
    val paresVendidos: Int = 0,
    val comisionSemanal: String = "$0",
    val isLoading: Boolean = false,
    val error: String? = null
)

// Define los tipos de vista de ventas
enum class TipoVenta {
    DETAL,
    MAYOR
}

// Define la estructura mínima de la venta que necesitamos para estadísticas
data class VentaEstadistica(
    val idVenta: String,
    val esDetal: Boolean,
    val fechaVenta: Date,
    val cantidadParesVendidos: Int // Asumiremos que se calcula del campo 'productos'
)

class VentaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val ventaCollection = db.collection("Ventas")

    // Número de semanas a mostrar en el gráfico
    private val NUM_WEEKS = 4

    /**
     * Obtiene y agrupa las ventas de las últimas 4 semanas, filtradas por tipo (Detal/Mayor).
     */
    suspend fun getVentasPorTipoSemanal(tipo: TipoVenta): Map<Int, List<VentaEstadistica>> {
        val esDetal = tipo == TipoVenta.DETAL
        val calendar = Calendar.getInstance()

        // 💡 CORRECCIÓN 1: Calcular la fecha de inicio (hace 28 días)
        // Restamos 28 días (4 semanas) para asegurarnos que abarcamos el rango completo.
        calendar.add(Calendar.DAY_OF_YEAR, -(NUM_WEEKS * 7) + 1)

        // 💡 CORRECCIÓN 2: Poner la hora a CERO (Esto es correcto, pero debe ser en la zona horaria local)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startDate = calendar.time
        println("DEBUG_VENTA: Buscando ventas donde esDetal=$esDetal desde fecha: $startDate")

        return try {
            val snapshot: QuerySnapshot = ventaCollection
                .whereEqualTo("esDetal", esDetal)
                .whereGreaterThan(
                    "fechaVenta",
                    startDate
                ) // ⬅️ Usamos > para que no se incluya la hora exacta.
                .orderBy("fechaVenta", Query.Direction.ASCENDING)
                // .orderBy(FieldPath.documentId(), Query.Direction.ASCENDING) // Esta línea es opcional
                .get()
                .await()
            println("DEBUG_VENTA: Documentos de venta encontrados: ${snapshot.documents.size}")

            val ventas = snapshot.documents.mapNotNull { document ->

                // 💡 CORRECCIÓN 3: MANEJO DEL CAMPO 'productos' (ASUMIMOS FORMATO ARRAY/LISTA)
                // Usamos List<*> porque tu ViewModel guarda una Lista de objetos/mapas
                val productosList =
                    document.get("productos") as? List<Map<String, Any>> ?: emptyList()

                // Sumar el campo 'cantidad' de cada producto en la LISTA
                val cantidadPares = productosList.sumOf { producto ->
                    // Firestore guarda los números como Long por defecto. Convertimos a Int.
                    (producto["cantidad"] as? Long)?.toInt() ?: 0
                }

                println("DEBUG_VENTA: Venta ID ${document.id} | Pares calculados: $cantidadPares")

                VentaEstadistica(
                    idVenta = document.id,
                    esDetal = esDetal,
                    fechaVenta = document.getDate("fechaVenta") ?: Date(),
                    cantidadParesVendidos = cantidadPares
                )
            }

            // Agrupar y mapear al índice semanal del gráfico
            agruparVentasPorSemana(ventas, startDate)

        } catch (e: Exception) {
            println("Error al obtener ventas de Firebase: ${e.message}")
            emptyMap()
        }
    }

    // Función auxiliar para agrupar ventas en las 4 semanas del gráfico
    // VentaRepository.kt - Reemplazar la función de agrupación

    private fun agruparVentasPorSemana(ventas: List<VentaEstadistica>, startDate: Date): Map<Int, List<VentaEstadistica>> {
        val groupedSales = mutableMapOf<Int, MutableList<VentaEstadistica>>()

        // El punto de inicio de la Semana 1, normalizado a 00:00:00 en la zona horaria local.
        val startCal = Calendar.getInstance()
        startCal.time = startDate
        startCal.set(Calendar.HOUR_OF_DAY, 0)
        startCal.set(Calendar.MINUTE, 0)
        startCal.set(Calendar.SECOND, 0)
        startCal.set(Calendar.MILLISECOND, 0)
        val startTimestamp = startCal.timeInMillis

        // Una venta es en una semana diferente si tiene una diferencia de 7 días.
        val millisecondsInAWeek = TimeUnit.DAYS.toMillis(7)

        for (venta in ventas) {
            // Normalizar la fecha de la venta a 00:00:00 en la zona horaria local para la comparación
            val ventaCal = Calendar.getInstance()
            ventaCal.time = venta.fechaVenta // Firestore la devuelve como UTC/GMT

            // La normalizamos a medianoche de ESE DÍA en nuestra zona horaria
            ventaCal.set(Calendar.HOUR_OF_DAY, 0)
            ventaCal.set(Calendar.MINUTE, 0)
            ventaCal.set(Calendar.SECOND, 0)
            ventaCal.set(Calendar.MILLISECOND, 0)

            val diffTime = ventaCal.timeInMillis - startTimestamp

            // 💡 CÁLCULO FINAL: Usamos la división entera de días (diferencia / días en una semana)
            val diffWeeks = diffTime / millisecondsInAWeek

            // El índice de la semana (1, 2, 3, 4)
            val weekIndex = diffWeeks.toInt() + 1

            println("DEBUG_SEMANA_FINAL: Venta ID ${venta.idVenta} | Fecha Venta GMT: ${venta.fechaVenta} | Venta Normalizada: ${ventaCal.time} | Index Calculado: $weekIndex")

            if (weekIndex in 1..NUM_WEEKS) {
                groupedSales.getOrPut(weekIndex) { mutableListOf() }.add(venta)
            } else {
                println("DEBUG_SEMANA_FINAL: Venta ${venta.idVenta} descartada (Index $weekIndex fuera de 1..4).")
            }
        }

        // Asegura que las 4 semanas estén presentes en el mapa
        (1..NUM_WEEKS).forEach { i -> groupedSales.putIfAbsent(i, mutableListOf()) }

        val finalMap = groupedSales.mapValues { (_, v) -> v.sumOf { it.cantidadParesVendidos } }
        println("DEBUG_SEMANA_FINAL: Resultado Final de Agrupación (Pares): $finalMap")

        return groupedSales
    }
}