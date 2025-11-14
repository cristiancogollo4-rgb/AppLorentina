package com.cristiancogollo.applorentina


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

class VentaRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Obtiene las ventas de Firestore, filtradas por tipo (DETAL / MAYOR),
     * y las agrupa por semana del mes.
     *
     * Esta versión la usará el VENDEDOR.
     */
    suspend fun getVentasPorTipoSemanal(
        tipo: TipoVenta
    ): Map<Int, List<Venta>> {

        val esDetal = (tipo == TipoVenta.DETAL)

        val snapshot = db.collection("Ventas")   // 👈 YA BIEN
            .whereEqualTo("esDetal", esDetal)
            .get()
            .await()

        val ventas: List<Venta> = snapshot.toObjects(Venta::class.java)

        return ventas.groupBy { venta ->
            getWeekOfMonth(venta.fechaVenta)
        }
    }

    /**
     * Versión para ADMIN.
     *
     * Usamos la misma lógica de getVentasPorTipoSemanal,
     * pero la convertimos a VentaEstadistica (por si luego quieres otro cálculo).
     */
    suspend fun getVentasPorTipoSemanalAdmin(
        tipo: TipoVenta
    ): Map<Int, List<VentaEstadistica>> {

        val ventasBase: Map<Int, List<Venta>> = getVentasPorTipoSemanal(tipo)

        val ventasConvertidas: Map<Int, List<VentaEstadistica>> =
            ventasBase.mapValues { (_, listaVentas) ->
                listaVentas.map { venta ->
                    VentaEstadistica(
                        idVenta = venta.idVenta,
                        esDetal = venta.esDetal,
                        fechaVenta = venta.fechaVenta,
                        cantidadParesVendidos = venta.cantidadParesVendidos
                    )
                }
            }

        return ventasConvertidas
    }

    // 📌 Convierte una fecha en su número de semana del mes (1..4)
    fun getWeekOfMonth(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.WEEK_OF_MONTH)
    }
}
