package com.example.tercertaller.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditProfileUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val telefono: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
)

class EditProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    fun cargarPerfil(nombre: String, email: String, telefono: String) {
        _uiState.update {
            it.copy(nombre = nombre, email = email, telefono = telefono)
        }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, errorMessage = null, saveSuccess = false) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null, saveSuccess = false) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null, saveSuccess = false) }
    }

    fun onTelefonoChange(value: String) {
        _uiState.update { it.copy(telefono = value, errorMessage = null, saveSuccess = false) }
    }

    fun guardarCambios() {
        val state = uiState.value

        if (state.nombre.isBlank() || state.email.isBlank() || state.telefono.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor completa todos los campos.") }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
    }
}