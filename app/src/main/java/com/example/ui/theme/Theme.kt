package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkPurpleColorScheme = darkColorScheme(
    primary = VividPurple,
    onPrimary = Color(0xFF130526),
    primaryContainer = RoyalPurpleHighlight,
    onPrimaryContainer = LightLilac,
    secondary = BrightLilac,
    onSecondary = Color(0xFF1B0736),
    secondaryContainer = RoyalPurpleElevated,
    onSecondaryContainer = LightLilac,
    tertiary = RadiantMagenta,
    onTertiary = Color(0xFF2E0038),
    tertiaryContainer = Color(0xFF4A105C),
    onTertiaryContainer = Color(0xFFFCE7F3),
    background = MidnightPurple,
    onBackground = TextPrimaryLight,
    surface = DeepVioletSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = RoyalPurpleCard,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderPurple,
    outlineVariant = BorderLightPurple,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Strictly adhere to the requested dark purple theme
    MaterialTheme(
        colorScheme = DarkPurpleColorScheme,
        typography = Typography,
        content = content
    )
}
