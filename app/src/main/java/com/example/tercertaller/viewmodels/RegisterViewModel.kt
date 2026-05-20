package com.example.tercertaller.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.tercertaller.data.Ubicacion
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
    val photoUri: Uri? = null,
    var isEmailError: Boolean = false,
    var isPassError: Boolean = false,
    var isNombreError: Boolean = false,
    var isTelefonoError: Boolean = false
)

class RegisterViewModel : ViewModel(){

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

    fun onPhotoUriChange(uri: Uri?) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun getUsuario(): Usuario {
        val state = uiState.value
        val nuevoUsuario = Usuario(
            nombre = state.nombre,
            telefono = state.telefono,
            enLinea = false,
            ubicacion = Ubicacion(0.0, 0.0)
        )
        return nuevoUsuario
    }

}