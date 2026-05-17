package com.example.tercertaller.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tercertaller.data.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val telefono: String = ""
)

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onTelefonoChange(value: String) {
        _uiState.update { it.copy(telefono = value) }
    }

    fun registrarUsuario() {
        val state = uiState.value
        val nuevoUsuario = Usuario(
            nombre = state.nombre,
            email = state.email,
            password = state.password,
            telefono = state.telefono,
            enLinea = false,
            ubicacion = Pair(0.0, 0.0)
        )
        println("Usuario registrado: $nuevoUsuario")
    }
}