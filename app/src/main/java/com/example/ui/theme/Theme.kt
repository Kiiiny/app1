package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
    themeSettings: AppThemeSettings = AppThemeSettings(),
    content: @Composable () -> Unit
) {
    val appColors = getAppColors(themeSettings.palette, themeSettings.mode)

    val colorScheme = if (themeSettings.mode == ThemeMode.DARK) {
        darkColorScheme(
            primary = appColors.primary,
            secondary = appColors.primaryVariant,
            tertiary = appColors.accent,
            background = appColors.background,
            surface = appColors.surface,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.White,
            onBackground = appColors.textPrimary,
            onSurface = appColors.textPrimary
        )
    } else {
        lightColorScheme(
            primary = appColors.primary,
            secondary = appColors.primaryVariant,
            tertiary = appColors.accent,
            background = appColors.background,
            surface = appColors.surface,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.White,
            onBackground = appColors.textPrimary,
            onSurface = appColors.textPrimary
        )
    }

    CompositionLocalProvider(
        LocalAppThemeColors provides appColors,
        LocalCardDensity provides themeSettings.cardDensity,
        LocalFontScale provides themeSettings.fontScale
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
