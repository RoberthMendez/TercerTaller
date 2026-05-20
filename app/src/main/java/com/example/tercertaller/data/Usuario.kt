package com.example.tercertaller.data

data class Usuario(
    val nombre: String = "",
    val telefono: String = "",
    val enLinea: Boolean = false,
    val ubicacion: Ubicacion = Ubicacion(),
    val recorrido: Map<String, Ubicacion> = emptyMap()
)

data class Ubicacion(
    val latitud: Double = 0.0,
    val longitud: Double = 0.0
)