package com.example.tercertaller.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isEmailError: Boolean = false,
    val isPassError: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

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

}

