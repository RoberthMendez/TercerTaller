package com.example.tercertaller.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoutes(): NavKey {
    @Serializable data object Register : AppRoutes()
    @Serializable data object Login : AppRoutes()
    @Serializable data object Main : AppRoutes()
    @Serializable data object EditPerfil : AppRoutes()
}