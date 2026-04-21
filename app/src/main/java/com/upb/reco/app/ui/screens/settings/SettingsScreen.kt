package com.upb.reco.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upb.reco.app.data.model.Platform
import com.upb.reco.app.data.preferences.ThemeMode
import com.upb.reco.app.ui.components.RecoPrimaryButton
import com.upb.reco.app.ui.components.RecoSecondaryButton
import com.upb.reco.app.ui.components.RecoTextField
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
    val scrollState = rememberScrollState()
    var showEditNameDialog by rememberSaveable { mutableStateOf(false) }
    var draftName by rememberSaveable { mutableStateOf("") }

    androidx.compose.material3.Scaffold(
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
        ) {
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 14.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = profileInitial(uiModel.userName),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                ) {
                    Text(text = uiModel.userName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = uiModel.userEmail.ifBlank { "Sin correo configurado" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(
                    onClick = {
                        draftName = uiModel.userName
                        showEditNameDialog = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar nombre",
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
                        val email = uiModel.userEmail.trim()
                        if (email.isBlank()) {
                            snackbarHostState.showSnackbar("No hay correo configurado")
                        } else {
                            val result = viewModel.sendPasswordResetEmail(email)
                            val message = result.fold(
                                onSuccess = { "Correo de restablecimiento enviado" },
                                onFailure = { it.message ?: "No se pudo enviar el correo" },
                            )
                            snackbarHostState.showSnackbar(message)
                        }
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
            RecoPrimaryButton(
                text = "Cerrar sesión",
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Editar nombre") },
            text = {
                RecoTextField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tu nombre") },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = draftName.trim()
                        if (trimmed.isNotBlank()) {
                            viewModel.setUserName(trimmed)
                        }
                        showEditNameDialog = false
                    },
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

private fun profileInitial(userName: String): String {
    val t = userName.trim()
    if (t.isEmpty()) return "?"
    return t.first().uppercaseChar().toString()
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
