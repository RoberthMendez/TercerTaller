package com.example.tercertaller.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    val isLocationSharingEnabled: Boolean = false,
    val isMenuExpanded: Boolean = false
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onUbicacionCompartidaChanged(enabled: Boolean) {
        _uiState.update { it.copy(isLocationSharingEnabled = enabled) }
    }

    fun onMenuExpandedChanged(expanded: Boolean) {
        _uiState.update { it.copy(isMenuExpanded = expanded) }
    }
}