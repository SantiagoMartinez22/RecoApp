package com.reco.app.ui.screens.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reco.app.ui.components.RecoPrimaryButton
import com.reco.app.ui.theme.RecoAccent
import com.reco.app.ui.theme.RecoAccentDark

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "splashPulse")
    val scale by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "logoScale",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = RecoAccent.copy(alpha = 0.45f),
                )
                .size(100.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(RecoAccent, RecoAccentDark),
                    ),
                    shape = RoundedCornerShape(28.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "R",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-2).sp,
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "RECO",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Películas y series personalizadas según tus gustos. Encuéntralas en todas tus plataformas.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(48.dp))
        RecoPrimaryButton(
            text = "Comenzar",
            onClick = onGetStarted,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "¿Ya tienes cuenta? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = RecoAccent,
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier.clickable { onLoginClick() },
            )
        }
    }
}
