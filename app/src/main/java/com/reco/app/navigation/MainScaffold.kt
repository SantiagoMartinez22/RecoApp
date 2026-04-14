package com.reco.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.reco.app.data.preferences.ThemePreferences
import com.reco.app.data.preferences.UserPreferences
import com.reco.app.data.repository.AuthRepository
import com.reco.app.data.repository.MovieRepository
import com.reco.app.ui.components.RecoBottomNavBar
import com.reco.app.ui.screens.detail.DetailScreen
import com.reco.app.ui.screens.detail.DetailViewModel
import com.reco.app.ui.screens.home.HomeScreen
import com.reco.app.ui.screens.home.HomeViewModel
import com.reco.app.ui.screens.search.SearchScreen
import com.reco.app.ui.screens.search.SearchViewModel
import com.reco.app.ui.screens.settings.SettingsScreen
import com.reco.app.ui.screens.settings.SettingsViewModel
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun MainScaffold(
    movieRepository: MovieRepository,
    authRepository: AuthRepository,
    onNavigateToCredits: () -> Unit,
    onSignOut: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { UserPreferences(context.applicationContext) }
    val themePreferences = remember { ThemePreferences(context.applicationContext) }

    val mainNavController = rememberNavController()
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in setOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Lists.route,
        Screen.Settings.route,
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                RecoBottomNavBar(
                    currentRoute = currentRoute,
                    onItemSelected = { route ->
                        mainNavController.navigate(route) {
                            popUpTo(mainNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = mainNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(movieRepository, userPreferences),
                )
                HomeScreen(
                    viewModel = viewModel,
                    onMovieTap = { mediaType, id ->
                        mainNavController.navigate(Screen.Detail.buildRoute(mediaType, id))
                    },
                    onSeeAllTrending = {
                        mainNavController.navigate(Screen.Search.route)
                    },
                    onSeeAllByGenre = {
                        mainNavController.navigate(Screen.Search.route)
                    },
                )
            }
            composable(Screen.Search.route) {
                val viewModel: SearchViewModel = viewModel(
                    factory = SearchViewModel.Factory(movieRepository),
                )
                SearchScreen(
                    viewModel = viewModel,
                    onMovieTap = { mediaType, id ->
                        mainNavController.navigate(Screen.Detail.buildRoute(mediaType, id))
                    },
                )
            }
            composable(Screen.Lists.route) {
                PlaceholderTab(text = "Listas — próximamente")
            }
            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModel.Factory(
                        userPreferences = userPreferences,
                        themePreferences = themePreferences,
                        authRepository = authRepository,
                    ),
                )
                SettingsScreen(
                    viewModel = viewModel,
                    onSignOut = {
                        scope.launch {
                            authRepository.signOut()
                            onSignOut()
                        }
                    },
                    onNavigateToCredits = onNavigateToCredits,
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument(Screen.Detail.MEDIA_TYPE_ARG) { type = NavType.StringType },
                    navArgument(Screen.Detail.ID_ARG) { type = NavType.IntType },
                ),
            ) { backStackEntry ->
                val mediaType = backStackEntry.arguments?.getString(Screen.Detail.MEDIA_TYPE_ARG) ?: "movie"
                val id = backStackEntry.arguments?.getInt(Screen.Detail.ID_ARG) ?: 0
                val viewModel: DetailViewModel = viewModel(
                    key = "detail-$mediaType-$id",
                    factory = DetailViewModel.Factory(
                        repository = movieRepository,
                        mediaType = mediaType,
                        id = id,
                    ),
                )
                DetailScreen(
                    viewModel = viewModel,
                    onBack = { mainNavController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun PlaceholderTab(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
