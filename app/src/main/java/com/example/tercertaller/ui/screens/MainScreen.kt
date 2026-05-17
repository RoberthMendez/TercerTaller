package com.example.tercertaller.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tercertaller.ui.components.main.CardUbicacion
import com.example.tercertaller.ui.components.main.ContenidoMapa
import com.example.tercertaller.ui.components.main.TopBar
import com.example.tercertaller.viewmodels.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            isMenuExpanded = uiState.isMenuExpanded,
            onMenuExpandedChanged = viewModel::onMenuExpandedChanged,
            onEditProfile = onNavigateToEditProfile,
            onLogout = onNavigateToLogin
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ContenidoMapa()

            CardUbicacion(
                isLocationSharingEnabled = uiState.isLocationSharingEnabled,
                onLocationSharingChanged = viewModel::onUbicacionCompartidaChanged,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}



