package com.reco.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.reco.app.data.repository.MovieRepository
import com.reco.app.ui.components.RecoBottomNavBar
import com.reco.app.ui.screens.home.HomeScreen
import com.reco.app.ui.screens.home.HomeViewModel

@Composable
fun MainScaffold(
    movieRepository: MovieRepository,
) {
    val mainNavController = rememberNavController()
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
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
        },
    ) { padding ->
        NavHost(
            navController = mainNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(movieRepository),
                )
                HomeScreen(
                    viewModel = viewModel,
                    onMovieTap = { _, _ -> /* Detalle en iteración futura */ },
                )
            }
            composable(Screen.Search.route) {
                PlaceholderTab(text = "Buscar — próximamente")
            }
            composable(Screen.Lists.route) {
                PlaceholderTab(text = "Listas — próximamente")
            }
            composable(Screen.Settings.route) {
                PlaceholderTab(text = "Ajustes — próximamente")
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
