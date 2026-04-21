package com.upb.reco.app.navigation

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

    data object Credits : Screen("credits")
    data object Detail : Screen("detail/{mediaType}/{id}") {
        const val MEDIA_TYPE_ARG = "mediaType"
        const val ID_ARG = "id"

        fun buildRoute(mediaType: String, id: Int): String = "detail/$mediaType/$id"
    }
}
