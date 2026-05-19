package com.example.tercertaller.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.example.tercertaller.R
import com.example.tercertaller.ui.components.main.CardUbicacion
import com.example.tercertaller.ui.components.main.ContenidoMapa
import com.example.tercertaller.ui.components.main.TopBar
import com.example.tercertaller.viewmodels.AuthViewModel
import com.example.tercertaller.viewmodels.MainViewModel
import com.example.tercertaller.viewmodels.UserViewModel

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val mainUiState by mainViewModel.uiState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()
    val userUiState by userViewModel.uiState.collectAsState()

    LaunchedEffect(authUiState.isAuthenticated) {
        if (!authUiState.isAuthenticated) {
            onNavigateToLogin()
        }
    }

    LaunchedEffect(Unit) {
        if (!authUiState.isAuthenticated) {
            onNavigateToLogin()
        } else if(userUiState.usuario == null) {
            userViewModel.cargarUsuario()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            isMenuExpanded = mainUiState.isMenuExpanded,
            onMenuExpandedChanged = mainViewModel::onMenuExpandedChanged,
            onEditProfile = onNavigateToEditProfile,
            onGoTologin = onNavigateToLogin,
            onLogout = {
                authViewModel.singOut()
                userViewModel.clear()
            }
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            ContenidoMapa(
                userViewModel = userViewModel
            )

            CardUbicacion(
                isLocationSharingEnabled = mainUiState.isLocationSharingEnabled,
                onLocationSharingChanged = mainViewModel::onUbicacionCompartidaChanged,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}



