package com.reco.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reco.app.data.model.Platform
import com.reco.app.data.preferences.ThemeMode
import com.reco.app.ui.components.RecoSecondaryButton
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onSignOut: () -> Unit,
    onNavigateToCredits: () -> Unit,
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    androidx.compose.material3.Scaffold(
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 14.dp),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .height(64.dp)
                        .fillMaxWidth(0.2f),
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = uiModel.userName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "demo@reco.app",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            SettingsSwitchRow(
                title = "Nuevas recomendaciones",
                checked = uiModel.notifRecommendations,
                onCheckedChange = viewModel::setNotifRecommendations,
            )
            SettingsSwitchRow(
                title = "Estrenos en tus géneros",
                checked = uiModel.notifReleases,
                onCheckedChange = viewModel::setNotifReleases,
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Mis plataformas", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Platform.entries.forEach { platform ->
                SettingsSwitchRow(
                    title = platform.label,
                    checked = platform.label in uiModel.platformSubscriptions,
                    onCheckedChange = { viewModel.togglePlatform(platform) },
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(text = "Apariencia", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = uiModel.themeMode == mode,
                        onClick = { viewModel.setThemeMode(mode) },
                        label = {
                            Text(
                                when (mode) {
                                    ThemeMode.SYSTEM -> "Sistema"
                                    ThemeMode.LIGHT -> "Claro"
                                    ThemeMode.DARK -> "Oscuro"
                                },
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            RecoSecondaryButton(
                text = "Cambiar contraseña",
                onClick = {
                    scope.launch {
                        val result = viewModel.sendPasswordResetEmail("demo@reco.app")
                        val message = result.fold(
                            onSuccess = { "Correo de restablecimiento enviado" },
                            onFailure = { it.message ?: "No se pudo enviar el correo" },
                        )
                        snackbarHostState.showSnackbar(message)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecoSecondaryButton(
                text = "Créditos del proyecto",
                onClick = onNavigateToCredits,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecoSecondaryButton(
                text = "Cerrar sesión",
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
