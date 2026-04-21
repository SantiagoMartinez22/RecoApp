package com.upb.reco.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upb.reco.app.ui.components.RecoPrimaryButton
import com.upb.reco.app.ui.components.RecoTextField

@Composable
fun EditUserScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    var draftName by rememberSaveable(uiModel.userName) { androidx.compose.runtime.mutableStateOf(uiModel.userName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "Editar usuario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Perfil",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(10.dp))

        RecoTextField(
            value = draftName,
            onValueChange = { draftName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre") },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Correo: ${uiModel.userEmail.ifBlank { "Sin correo configurado" }}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(18.dp))
        RecoPrimaryButton(
            text = "Guardar cambios",
            onClick = {
                val trimmed = draftName.trim()
                if (trimmed.isNotBlank()) {
                    viewModel.setUserName(trimmed)
                }
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

