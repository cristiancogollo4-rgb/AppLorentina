package com.cristiancogollo.applorentina

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

// Número de semanas a mostrar en el gráfico
private const val NUM_WEEKS = 4

class VentaRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val ventaCollection = db.collection("Ventas")

    // 📌 FUNCIÓN AUXILIAR (Calcula la semana del mes. Usada solo para tarjeta de resumen)
    fun getWeekOfMonth(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.WEEK_OF_MONTH)
    }

    /**
     * Cuenta cuántos pares hay en una venta, leyendo manualmente la lista "productos".
     * Esta función garantiza que obtenemos el conteo real aunque el campo cantidadParesVendidos esté a 0.
     */
    private fun contarParesDeVenta(venta: Venta): Int {
        // Asegura que 'productos' se trate como una lista de mapas (que es lo que devuelve Firestore)
        val productosList = venta.productos as? List<Map<String, Any?>> ?: return 0
        return productosList.sumOf { producto ->
            // Firestore guarda Ints como Long, necesitamos castear
            (producto["cantidad"] as? Long)?.toInt() ?: 0
        }
    }

    // =========================================================
    // 1. FUNCIÓN BASE COMÚN (Consulta Firestore y agrupa por 4 semanas FIJAS)
    // =========================================================

    /**
     * Obtiene y filtra las ventas de las últimas 4 semanas, devolviendo Map<Índice de Semana (1-4), List<Venta>>.
     */
    private suspend fun getVentasBaseConFiltro(tipo: TipoVenta): Map<Int, List<Venta>> {
        val esDetal = tipo == TipoVenta.DETAL
        val calendar = Calendar.getInstance()

        // 1. Calcular la fecha de inicio (hace 28 días)
        calendar.add(Calendar.DAY_OF_YEAR, -(NUM_WEEKS * 7) + 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.time

        return try {
            val snapshot: QuerySnapshot = ventaCollection
                .whereEqualTo("esDetal", esDetal)
                .whereGreaterThan("fechaVenta", startDate)
                .orderBy("fechaVenta", Query.Direction.ASCENDING)
                .get()
                .await()

            val ventas: List<Venta> = snapshot.toObjects(Venta::class.java)
            agruparVentasPorSemanaFija(ventas, startDate)

        } catch (e: Exception) {
            println("Error al obtener ventas de Firebase: ${e.message}")
            emptyMap()
        }
    }

    // =========================================================
    // 2. FUNCIÓN PARA EL VENDEDOR (EstadisticasViewModel.kt) - AHORA CON CONTEO MANUAL
    // =========================================================

    /**
     * Obtiene y agrupa las ventas de las últimas 4 semanas y las mapea a VentaEstadistica.
     */
    suspend fun getVentasPorTipoSemanal(tipo: TipoVenta): Map<Int, List<VentaEstadistica>> {
        val ventasBase: Map<Int, List<Venta>> = getVentasBaseConFiltro(tipo)

        // Convertir a VentaEstadistica, usando el conteo manual para asegurar la precisión
        return ventasBase.mapValues { (_, listaVentas) ->
            listaVentas.map { venta ->
                val paresContados = contarParesDeVenta(venta) // 👈 USAMOS EL CONTEO MANUAL
                VentaEstadistica(
                    idVenta = venta.idVenta,
                    esDetal = venta.esDetal,
                    fechaVenta = venta.fechaVenta,
                    cantidadParesVendidos = paresContados // 👈 GARANTIZAMOS EL VALOR CORRECTO
                )
            }
        }
    }

    // =========================================================
    // 3. FUNCIÓN PARA EL ADMINISTRADOR (AdminEstadisticasViewModel.kt)
    // =========================================================

    /**
     * Versión para ADMIN. Devuelve el objeto Venta completo.
     */
    suspend fun getVentasDeLasUltimas4Semanas(tipo: TipoVenta): Map<Int, List<Venta>> {
        return getVentasBaseConFiltro(tipo)
    }

    // =========================================================
    // 4. LÓGICA DE AGRUPACIÓN (4 semanas FIJAS)
    // =========================================================

    private fun agruparVentasPorSemanaFija(ventas: List<Venta>, startDate: Date): Map<Int, List<Venta>> {
        val groupedSales = mutableMapOf<Int, MutableList<Venta>>()

        val startCal = Calendar.getInstance()
        startCal.time = startDate
        startCal.set(Calendar.HOUR_OF_DAY, 0); startCal.set(Calendar.MINUTE, 0); startCal.set(Calendar.SECOND, 0); startCal.set(Calendar.MILLISECOND, 0)
        val startTimestamp = startCal.timeInMillis

        val millisecondsInAWeek = TimeUnit.DAYS.toMillis(7)

        for (venta in ventas) {
            val ventaCal = Calendar.getInstance()
            ventaCal.time = venta.fechaVenta
            ventaCal.set(Calendar.HOUR_OF_DAY, 0); ventaCal.set(Calendar.MINUTE, 0); ventaCal.set(Calendar.SECOND, 0); ventaCal.set(Calendar.MILLISECOND, 0)

            val diffTime = ventaCal.timeInMillis - startTimestamp
            val diffWeeks = diffTime / millisecondsInAWeek
            val weekIndex = diffWeeks.toInt() + 1

            if (weekIndex in 1..NUM_WEEKS) {
                groupedSales.getOrPut(weekIndex) { mutableListOf() }.add(venta)
            }
        }
        (1..NUM_WEEKS).forEach { i ->
            groupedSales.putIfAbsent(i, mutableListOf())
        }

        return groupedSales
    }
}