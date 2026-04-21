package com.upb.reco.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.upb.reco.app.data.preferences.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = RecoAccent,
    onPrimary = Color.White,
    primaryContainer = RecoDarkSurfaceElevated,
    onPrimaryContainer = Color.White,
    secondary = RecoDarkSurfaceElevated,
    onSecondary = Color.White,
    background = RecoDarkBg,
    onBackground = Color.White,
    surface = RecoDarkSurface,
    onSurface = Color.White,
    surfaceVariant = RecoDarkSurfaceElevated,
    onSurfaceVariant = RecoDarkTextMuted,
    outline = RecoDarkBorder,
    outlineVariant = RecoDarkTextDim,
)

private val LightColorScheme = lightColorScheme(
    primary = RecoAccent,
    onPrimary = Color.White,
    primaryContainer = RecoLightSurfaceElevated,
    onPrimaryContainer = Color(0xFF121218),
    secondary = RecoLightSurfaceElevated,
    onSecondary = Color(0xFF121218),
    background = RecoLightBg,
    onBackground = Color(0xFF121218),
    surface = RecoLightSurface,
    onSurface = Color(0xFF121218),
    surfaceVariant = RecoLightSurfaceElevated,
    onSurfaceVariant = RecoLightTextMuted,
    outline = RecoLightBorder,
    outlineVariant = RecoLightTextDim,
)

@Composable
fun RecoTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RecoTypography,
        shapes = RecoShapes,
        content = content,
    )
}
