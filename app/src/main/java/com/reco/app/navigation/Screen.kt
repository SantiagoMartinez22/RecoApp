package com.reco.app.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")

    /** Contenedor con BottomNavBar (Home, Buscar, Listas, Ajustes). */
    data object Main : Screen("main")

    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Lists : Screen("lists")
    data object Settings : Screen("settings")
}
