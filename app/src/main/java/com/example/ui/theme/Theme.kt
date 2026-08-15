package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAppAccentTheme = staticCompositionLocalOf { AppAccentTheme.PURPLE }
val LocalPowerSaveMode = staticCompositionLocalOf { false }

@Composable
fun MyApplicationTheme(
    accentTheme: AppAccentTheme = AppAccentTheme.PURPLE,
    powerSaveMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkColorScheme = darkColorScheme(
        primary = accentTheme.primary,
        onPrimary = Color.Black,
        primaryContainer = accentTheme.primary.copy(alpha = 0.2f),
        onPrimaryContainer = accentTheme.primary,
        secondary = accentTheme.secondary,
        onSecondary = Color.Black,
        secondaryContainer = accentTheme.secondary.copy(alpha = 0.2f),
        onSecondaryContainer = accentTheme.secondary,
        tertiary = NeonAmber,
        onTertiary = Color.Black,
        background = DarkBg,
        onBackground = TextPrimary,
        surface = DarkSurface,
        onSurface = TextPrimary,
        surfaceVariant = DarkSurfaceElevated,
        onSurfaceVariant = TextSecondary,
        outline = DarkCardBorder,
        outlineVariant = DarkCardBorderGlow
    )

    CompositionLocalProvider(
        LocalAppAccentTheme provides accentTheme,
        LocalPowerSaveMode provides powerSaveMode
    ) {
        MaterialTheme(
            colorScheme = darkColorScheme,
            typography = Typography,
            content = content
        )
    }
}
