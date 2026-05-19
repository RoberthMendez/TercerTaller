package com.example.tercertaller.viewmodels

import android.net.Uri
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
    val photoUri: Uri? = null,
    val isNombreError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isTelefonoError: Boolean = false,
    val isPasswordChangeEnabled: Boolean = false
)

class EditProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value) }
        if (value.isBlank()) {
            _uiState.update { it.copy(isNombreError = true) }
        } else {
            _uiState.update { it.copy(isNombreError = false) }
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
        if (value.isEmpty() || value.length < 6) {
            _uiState.update { it.copy(isPasswordError = true) }
        } else {
            _uiState.update { it.copy(isPasswordError = false) }
        }
    }

    fun onTelefonoChange(value: String) {
        _uiState.update { it.copy(telefono = value) }
        if (value.isEmpty() || value.length < 6) {
            _uiState.update { it.copy(isPasswordError = true) }
        } else {
            _uiState.update { it.copy(isPasswordError = false) }
        }
    }

    fun setIsPasswordChangeEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isPasswordChangeEnabled = enabled) }
    }

    fun datosValidos(): Boolean {
        val state = uiState.value
        return state.nombre.isNotBlank() && !state.isNombreError &&
                (!state.isPasswordChangeEnabled || (state.password.isNotEmpty() && !state.isPasswordError)) &&
                (!state.isTelefonoError)
    }

    fun cleanPassword(){
        _uiState.update { it.copy(password = "", isPasswordError = false) }
    }

    fun onPhotoUriChange(uri: Uri?) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun clearPhotoUri() {
        _uiState.update { it.copy(photoUri = null) }
    }
}