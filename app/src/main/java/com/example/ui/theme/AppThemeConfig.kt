package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemePalette(val displayNameArabic: String, val displayNameEnglish: String, val previewColor: Color) {
    ROYAL_PURPLE("الأرجواني الملكي (الافتراضي)", "Royal Purple (Default)", Color(0xFFA855F7)),
    EMERALD_NIGHT("الزمرد الليلي الهادئ", "Emerald Night", Color(0xFF10B981)),
    OCEAN_DEPTHS("أعماق المحيط النيلي", "Ocean Depths", Color(0xFF38BDF8)),
    MIDNIGHT_SLATE("الرمادي الداكن الأصيل", "Midnight Slate", Color(0xFF94A3B8)),
    ROSE_GOLD("الوردي النضِر", "Rose Gold", Color(0xFFFB7185))
}

enum class ThemeMode(val displayNameArabic: String, val displayNameEnglish: String) {
    DARK("الوضع الداكن (مستحسن)", "Dark Mode (Recommended)"),
    LIGHT("الوضع الفاتح", "Light Mode")
}

enum class CardDensity(val displayNameArabic: String, val displayNameEnglish: String) {
    COMFORTABLE("مريح (تفاصيل كاملة)", "Comfortable (Full)"),
    COMPACT("مدمج (عرض سريع)", "Compact (Fast)")
}

enum class FontScalePreference(val displayNameArabic: String, val displayNameEnglish: String, val scaleMultiplier: Float) {
    SMALL("صغير (مكثف)", "Small", 0.9f),
    MEDIUM("متوسط (الافتراضي)", "Medium (Default)", 1.0f),
    LARGE("كبير (سهل القراءة)", "Large", 1.15f)
}

data class AppThemeColors(
    val primary: Color,
    val primaryVariant: Color,
    val background: Color,
    val surface: Color,
    val card: Color,
    val cardElevated: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val border: Color,
    val highlight: Color,
    val accent: Color
)

data class AppThemeSettings(
    val palette: AppThemePalette = AppThemePalette.ROYAL_PURPLE,
    val mode: ThemeMode = ThemeMode.DARK,
    val cardDensity: CardDensity = CardDensity.COMFORTABLE,
    val fontScale: FontScalePreference = FontScalePreference.MEDIUM
)

fun getAppColors(palette: AppThemePalette, mode: ThemeMode): AppThemeColors {
    val isDark = mode == ThemeMode.DARK

    return when (palette) {
        AppThemePalette.ROYAL_PURPLE -> {
            if (isDark) {
                AppThemeColors(
                    primary = Color(0xFFA855F7),
                    primaryVariant = Color(0xFFC084FC),
                    background = Color(0xFF0F0B1E),
                    surface = Color(0xFF18122B),
                    card = Color(0xFF21193D),
                    cardElevated = Color(0xFF2D2254),
                    textPrimary = Color(0xFFF8FAFC),
                    textSecondary = Color(0xFFCBD5E1),
                    textMuted = Color(0xFF94A3B8),
                    border = Color(0xFF3B2D6E),
                    highlight = Color(0xFF4C3A8B),
                    accent = Color(0xFFE879F9)
                )
            } else {
                AppThemeColors(
                    primary = Color(0xFF9333EA),
                    primaryVariant = Color(0xFFA855F7),
                    background = Color(0xFFFAF5FF),
                    surface = Color(0xFFF3E8FF),
                    card = Color(0xFFFFFFFF),
                    cardElevated = Color(0xFFF5EEFD),
                    textPrimary = Color(0xFF1E1B4B),
                    textSecondary = Color(0xFF4B5563),
                    textMuted = Color(0xFF6B7280),
                    border = Color(0xFFE9D5FF),
                    highlight = Color(0xFFD8B4FE),
                    accent = Color(0xFFC026D3)
                )
            }
        }
        AppThemePalette.EMERALD_NIGHT -> {
            if (isDark) {
                AppThemeColors(
                    primary = Color(0xFF10B981),
                    primaryVariant = Color(0xFF34D399),
                    background = Color(0xFF061A14),
                    surface = Color(0xFF0D281E),
                    card = Color(0xFF13382B),
                    cardElevated = Color(0xFF1B4D3C),
                    textPrimary = Color(0xFFECFDF5),
                    textSecondary = Color(0xFFA7F3D0),
                    textMuted = Color(0xFF6EE7B7),
                    border = Color(0xFF047857),
                    highlight = Color(0xFF065F46),
                    accent = Color(0xFF6EE7B7)
                )
            } else {
                AppThemeColors(
                    primary = Color(0xFF059669),
                    primaryVariant = Color(0xFF10B981),
                    background = Color(0xFFF0FDF4),
                    surface = Color(0xFFDCFCE7),
                    card = Color(0xFFFFFFFF),
                    cardElevated = Color(0xFFF0FDF4),
                    textPrimary = Color(0xFF064E3B),
                    textSecondary = Color(0xFF047857),
                    textMuted = Color(0xFF059669),
                    border = Color(0xFFA7F3D0),
                    highlight = Color(0xFF6EE7B7),
                    accent = Color(0xFF047857)
                )
            }
        }
        AppThemePalette.OCEAN_DEPTHS -> {
            if (isDark) {
                AppThemeColors(
                    primary = Color(0xFF38BDF8),
                    primaryVariant = Color(0xFF7DD3FC),
                    background = Color(0xFF081528),
                    surface = Color(0xFF0E223D),
                    card = Color(0xFF142F54),
                    cardElevated = Color(0xFF1C3F70),
                    textPrimary = Color(0xFFF0F9FF),
                    textSecondary = Color(0xFFBAE6FD),
                    textMuted = Color(0xFF7DD3FC),
                    border = Color(0xFF0369A1),
                    highlight = Color(0xFF075985),
                    accent = Color(0xFF0EA5E9)
                )
            } else {
                AppThemeColors(
                    primary = Color(0xFF0284C7),
                    primaryVariant = Color(0xFF38BDF8),
                    background = Color(0xFFF0F9FF),
                    surface = Color(0xFFE0F2FE),
                    card = Color(0xFFFFFFFF),
                    cardElevated = Color(0xFFF0F9FF),
                    textPrimary = Color(0xFF0C4A6E),
                    textSecondary = Color(0xFF0369A1),
                    textMuted = Color(0xFF0284C7),
                    border = Color(0xFFBAE6FD),
                    highlight = Color(0xFF7DD3FC),
                    accent = Color(0xFF0284C7)
                )
            }
        }
        AppThemePalette.MIDNIGHT_SLATE -> {
            if (isDark) {
                AppThemeColors(
                    primary = Color(0xFF94A3B8),
                    primaryVariant = Color(0xFFCBD5E1),
                    background = Color(0xFF0F172A),
                    surface = Color(0xFF1E293B),
                    card = Color(0xFF334155),
                    cardElevated = Color(0xFF475569),
                    textPrimary = Color(0xFFF8FAFC),
                    textSecondary = Color(0xFFCBD5E1),
                    textMuted = Color(0xFF94A3B8),
                    border = Color(0xFF475569),
                    highlight = Color(0xFF64748B),
                    accent = Color(0xFFE2E8F0)
                )
            } else {
                AppThemeColors(
                    primary = Color(0xFF475569),
                    primaryVariant = Color(0xFF64748B),
                    background = Color(0xFFF8FAFC),
                    surface = Color(0xFFF1F5F9),
                    card = Color(0xFFFFFFFF),
                    cardElevated = Color(0xFFF1F5F9),
                    textPrimary = Color(0xFF0F172A),
                    textSecondary = Color(0xFF334155),
                    textMuted = Color(0xFF64748B),
                    border = Color(0xFFCBD5E1),
                    highlight = Color(0xFF94A3B8),
                    accent = Color(0xFF334155)
                )
            }
        }
        AppThemePalette.ROSE_GOLD -> {
            if (isDark) {
                AppThemeColors(
                    primary = Color(0xFFFB7185),
                    primaryVariant = Color(0xFFFDA4AF),
                    background = Color(0xFF1E0A12),
                    surface = Color(0xFF2C101B),
                    card = Color(0xFF3E1727),
                    cardElevated = Color(0xFF521E34),
                    textPrimary = Color(0xFFFFF1F2),
                    textSecondary = Color(0xFFFECDD3),
                    textMuted = Color(0xFFFDA4AF),
                    border = Color(0xFF9F1239),
                    highlight = Color(0xFFBE123C),
                    accent = Color(0xFFF43F5E)
                )
            } else {
                AppThemeColors(
                    primary = Color(0xFFE11D48),
                    primaryVariant = Color(0xFFFB7185),
                    background = Color(0xFFFFF1F2),
                    surface = Color(0xFFFFE4E6),
                    card = Color(0xFFFFFFFF),
                    cardElevated = Color(0xFFFFF1F2),
                    textPrimary = Color(0xFF881337),
                    textSecondary = Color(0xFF9F1239),
                    textMuted = Color(0xFFBE123C),
                    border = Color(0xFFFECDD3),
                    highlight = Color(0xFFFDA4AF),
                    accent = Color(0xFFE11D48)
                )
            }
        }
    }
}

val LocalAppThemeColors = staticCompositionLocalOf {
    getAppColors(AppThemePalette.ROYAL_PURPLE, ThemeMode.DARK)
}

val LocalCardDensity = staticCompositionLocalOf {
    CardDensity.COMFORTABLE
}

val LocalFontScale = staticCompositionLocalOf {
    FontScalePreference.MEDIUM
}

object AppTheme {
    val colors: AppThemeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppThemeColors.current

    val cardDensity: CardDensity
        @Composable
        @ReadOnlyComposable
        get() = LocalCardDensity.current

    val fontScale: FontScalePreference
        @Composable
        @ReadOnlyComposable
        get() = LocalFontScale.current
}
