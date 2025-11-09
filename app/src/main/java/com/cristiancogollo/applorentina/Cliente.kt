package com.cristiancogollo.applorentina

import com.google.firebase.Timestamp

data class Cliente(
    val cedula: Long = 0L,
    val nombreApellido: String = "",
    val telefono: Long = 0L,
    val correo: String = "",
    val departamento: String = "",
    val municipio: String = "",
    val tipoCliente: Boolean = false,
    val timestamp: Timestamp? = null
)
