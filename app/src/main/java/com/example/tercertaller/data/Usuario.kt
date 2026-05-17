package com.example.tercertaller.data

data class Usuario(
    val nombre: String,
    val email: String,
    val password: String,
    val telefono: String,
    val enLinea: Boolean,
    val ubicacion: Pair<Double, Double>
)