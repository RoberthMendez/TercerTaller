package com.example.tercertaller.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
    val telefono: String = "",
    var isEmailError: Boolean = false,
    var isPassError: Boolean = false,
    var isNombreError: Boolean = false,
    var isTelefonoError: Boolean = false,
)

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()




    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value) }
        if (value.isBlank()) {
            _uiState.update { it.copy(isNombreError = true) }
        } else {
            _uiState.update { it.copy(isNombreError = false) }

        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
        if (value.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            _uiState.update { it.copy(isEmailError = true) }
        } else {
            _uiState.update { it.copy(isEmailError = false) }
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
        if (value.isEmpty() || value.length < 6) {
            _uiState.update { it.copy(isPassError = true) }
        } else {
            _uiState.update { it.copy(isPassError = false) }
        }
    }

    fun onTelefonoChange(value: String) {
        _uiState.update { it.copy(telefono = value) }
        if (value.isBlank() || !value.matches(Regex("^[+]?[0-9]{10,15}\$"))) {
            _uiState.update { it.copy(isTelefonoError = true) }
        } else {
            _uiState.update { it.copy(isTelefonoError = false) }
        }
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

    fun validarFormulario(): Boolean {
        val state = uiState.value
        return state.nombre.isNotBlank() &&
                state.email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches() &&
                state.password.isNotEmpty() && state.password.length >= 6 &&
                state.telefono.isNotBlank() && state.telefono.matches(Regex("^[+]?[0-9]{10,15}\$"))
    }

}