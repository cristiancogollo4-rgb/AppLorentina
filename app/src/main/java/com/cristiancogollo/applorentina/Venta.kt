package com.cristiancogollo.applorentina

import java.util.Date

data class Venta(
    val idVenta: String = "",
    val cliente: Cliente? = null,      // Asociación al cliente seleccionado
    val fechaVenta: Date = Date(),
    val precioTotal: Double = 0.0,
    val descripcion: String = "",      // Campo opcional
    val esDetal: Boolean = true,       // true: Detal, false: Por Mayor
    val esVentaEspecial: Boolean = false, // Solo aplica para 'Por Mayor'
    val productos: List<Any> = emptyList(), // Se deja vacío por ahora

    // 👇 NUEVO CAMPO: cantidad de pares vendidos en la venta
    val cantidadParesVendidos: Int = 0
)
