package com.example.tercertaller.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.tercertaller.ui.screens.EditPerfilScreen
import com.example.tercertaller.ui.screens.LoadingScreen
import com.example.tercertaller.ui.screens.LoginScreen
import com.example.tercertaller.ui.screens.MainScreen
import com.example.tercertaller.ui.screens.RegisterScreen
import com.example.tercertaller.viewmodels.AuthViewModel
import com.example.tercertaller.viewmodels.UserViewModel

@Composable
fun AppNavigation(modifier: Modifier){

    val authViewModel: AuthViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val backStack = rememberNavBackStack(AppRoutes.Loading)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(
                targetOffsetX = { -it })
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                targetOffsetX = { it })
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                targetOffsetX = { it })
        },
        entryProvider = { key ->
            when (key) {
                is AppRoutes.Loading -> NavEntry(key) {
                    LoadingScreen(
                        onAuthenticated = { backStack.add(AppRoutes.Main) },
                        onUnauthenticated = { backStack.add(AppRoutes.Login) },
                        authViewModel = authViewModel
                    )
                }
                is AppRoutes.Register -> NavEntry(key) {
                    RegisterScreen(
                        onIniciarSesion = { backStack.add(AppRoutes.Main) },
                        userViewModel = userViewModel,
                        authViewModel = authViewModel
                    )
                }

                is AppRoutes.Login -> NavEntry(key) {
                    LoginScreen(
                        onNavigateToRegister = { backStack.add(AppRoutes.Register) },
                        onIniciarSesion = { backStack.add(AppRoutes.Main) },
                        authViewModel = authViewModel)
                }

                is AppRoutes.Main -> NavEntry(key) {
                    MainScreen(
                        onNavigateToEditProfile = { backStack.add(AppRoutes.EditPerfil) },
                        onNavigateToLogin = { backStack.add(AppRoutes.Login) },
                        userViewModel = userViewModel,
                        authViewModel = authViewModel
                    )
                }

                is AppRoutes.EditPerfil -> NavEntry(key) {
                    EditPerfilScreen(
                        onNavigateBack = { backStack.removeLastOrNull() },
                        userViewModel = userViewModel,
                        authViewModel = authViewModel
                    )
                }

                else -> {
                    error("Unknown route: $key")
                }
            }
        }
    )
}
