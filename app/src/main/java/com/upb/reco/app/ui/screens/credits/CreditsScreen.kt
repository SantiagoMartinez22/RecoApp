package com.upb.reco.app.ui.screens.credits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun CreditsScreen(
    onBack: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val teamMembers = listOf(
        TeamMember(
            name = "Santiago Martinez",
            role = "Desarrollador — Ingeniería de Software",
        ),
        TeamMember(
            name = "Valeria Zuluaga",
            role = "Desarrolladora — Ingeniería de Software",
        ),
        TeamMember(
            name = "Miguel Aristizabal",
            role = "Desarrollador — Ingeniería de Software",
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(0.2f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "RECO", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Proyecto académico - Kotlin / Android",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Equipo", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        teamMembers.forEach { member ->
            TeamCard(name = member.name, role = member.role, email = member.email)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = "Institución", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Universidad Pontificia Bolivariana",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "2026",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TeamCard(
    name: String,
    role: String,
    email: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(text = role, style = MaterialTheme.typography.bodyMedium)
            if (email.isNotBlank()) {
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private data class TeamMember(
    val name: String,
    val role: String,
    val email: String = "",
)
