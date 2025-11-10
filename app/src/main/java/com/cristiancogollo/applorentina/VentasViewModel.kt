package com.cristiancogollo.applorentina

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Locale

enum class VentasSort { CLIENTE, FECHA, CC }

data class VentasUiState(
    val ventas: List<Venta> = emptyList(),
    val filtered: List<Venta> = emptyList(),
    val query: String = "",
    val sort: VentasSort = VentasSort.CLIENTE,
    val isLoading: Boolean = false,
    val error: String? = null
)

class VentasViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _ui = MutableStateFlow(VentasUiState())
    val ui: StateFlow<VentasUiState> = _ui.asStateFlow()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        listenVentas()
    }

    /** Escucha "Ventas" en tiempo real */
    private fun listenVentas() {
        _ui.update { it.copy(isLoading = true, error = null) }

        db.collection("Ventas")
            .orderBy("fechaVenta", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    _ui.update { it.copy(isLoading = false, error = err.message) }
                    Log.e("VentasVM", "Error escuchar Ventas: ${err.message}")
                    return@addSnapshotListener
                }

                val list = snap?.documents?.mapNotNull { d ->
                    try {
                        d.toObject(Venta::class.java)
                    } catch (e: Exception) {
                        Log.e("VentasVM", "Mapeo venta ${d.id} falló: ${e.message}")
                        null
                    }
                }.orEmpty()

                _ui.update { it.copy(ventas = list, isLoading = false, error = null) }
                applyFilterAndSort()
            }
    }

    fun setQuery(q: String) {
        _ui.update { it.copy(query = q) }
        applyFilterAndSort()
    }

    fun setSort(s: VentasSort) {
        _ui.update { it.copy(sort = s) }
        applyFilterAndSort()
    }

    /** Filtra por texto y ordena según sort */
    private fun applyFilterAndSort() {
        val st = _ui.value
        val q = st.query.trim().lowercase()

        val possibleFormats = listOf(
            SimpleDateFormat("dd/MM/yy", Locale.getDefault()),
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )

        val filtered = if (q.isBlank()) st.ventas else st.ventas.filter { v ->
            val nombre = (v.cliente?.nombreApellido ?: "").lowercase()
            val cc = (v.cliente?.cedula ?: 0L).toString()
            val fecha = v.fechaVenta
            val desc = v.descripcion.lowercase()

            // Convertimos la fecha a diferentes formatos
            val formattedDates = possibleFormats.mapNotNull { fmt ->
                runCatching { fmt.format(fecha).lowercase() }.getOrNull()
            }

            nombre.contains(q) ||
                    cc.contains(q) ||
                    desc.contains(q) ||
                    formattedDates.any { it.contains(q) } // Compara con todos los formatos posibles
        }

        val sorted = when (st.sort) {
            VentasSort.CLIENTE -> filtered.sortedBy {
                (it.cliente?.nombreApellido ?: "").lowercase()
            }

            VentasSort.FECHA -> filtered.sortedBy { it.fechaVenta }
            VentasSort.CC -> filtered.sortedBy { (it.cliente?.cedula ?: 0L) }
        }

        _ui.update { it.copy(filtered = sorted) }
    }
}