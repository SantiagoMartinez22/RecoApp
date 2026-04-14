package com.reco.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.reco.app.data.repository.AuthRepository
import com.reco.app.navigation.RecoNavHost
import com.reco.app.navigation.Screen
import com.reco.app.ui.theme.RecoTheme
import com.reco.app.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application),
            )
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

            RecoTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                val authRepository = remember { AuthRepository() }
                // Sin Firebase no hay sesión persistente; siempre arranca en Splash.
                val startDestination = Screen.Splash.route
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    RecoNavHost(
                        navController = navController,
                        startDestination = startDestination,
                        authRepository = authRepository,
                    )
                }
            }
        }
    }
}
