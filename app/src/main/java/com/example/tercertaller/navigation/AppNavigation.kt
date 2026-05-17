package com.example.tercertaller.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.tercertaller.ui.screens.EditPerfilScreen
import com.example.tercertaller.ui.screens.LoginScreen
import com.example.tercertaller.ui.screens.MainScreen
import com.example.tercertaller.ui.screens.RegisterScreen

@Composable
fun AppNavigation(modifier: Modifier){

    val backStack = rememberNavBackStack(AppRoutes.Login)

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
                is AppRoutes.Register -> NavEntry(key) {
                    RegisterScreen()
                }

                is AppRoutes.Login -> NavEntry(key) {
                    LoginScreen(
                        onNavigateToRegister = { backStack.add(AppRoutes.Register) },
                        onIniciarSesion = { backStack.add(AppRoutes.Main) })
                }

                is AppRoutes.Main -> NavEntry(key) {
                    MainScreen(
                        onNavigateToEditProfile = { backStack.add(AppRoutes.EditPerfil) },
                        onNavigateToLogin = { backStack.add(AppRoutes.Login) }
                    )
                }

                is AppRoutes.EditPerfil -> NavEntry(key) {
                    EditPerfilScreen(
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                else -> {
                    error("Unknown route: $key")
                }
            }
        }
    )
}
