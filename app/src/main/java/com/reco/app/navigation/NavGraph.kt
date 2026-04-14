package com.reco.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.reco.app.data.repository.AuthRepository
import com.reco.app.data.repository.MovieRepository
import com.reco.app.ui.screens.auth.LoginScreen
import com.reco.app.ui.screens.auth.LoginViewModel
import com.reco.app.ui.screens.auth.RegisterScreen
import com.reco.app.ui.screens.auth.RegisterViewModel
import com.reco.app.ui.screens.credits.CreditsScreen
import com.reco.app.ui.screens.splash.SplashScreen

@Composable
fun RecoNavHost(
    navController: NavHostController,
    startDestination: String,
    authRepository: AuthRepository,
    modifier: Modifier = Modifier,
) {
    val movieRepository = remember { MovieRepository() }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onGetStarted = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
            )
        }
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.Factory(authRepository),
            )
            LoginScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onAuthSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Register.route) {
            val viewModel: RegisterViewModel = viewModel(
                factory = RegisterViewModel.Factory(authRepository),
            )
            RegisterScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Main.route) {
            MainScaffold(
                movieRepository = movieRepository,
                authRepository = authRepository,
                onNavigateToCredits = {
                    navController.navigate(Screen.Credits.route)
                },
                onSignOut = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Credits.route) {
            CreditsScreen(onBack = { navController.popBackStack() })
        }
    }
}
