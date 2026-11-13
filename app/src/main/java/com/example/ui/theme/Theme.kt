package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = CharcoalBackground,
    primaryContainer = CharcoalSurfaceElevated,
    onPrimaryContainer = CyanLight,
    secondary = CyanAccentVariant,
    onSecondary = CharcoalBackground,
    secondaryContainer = CharcoalSurfaceVariant,
    onSecondaryContainer = SlateTextPrimary,
    background = CharcoalBackground,
    onBackground = SlateTextPrimary,
    surface = CharcoalSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = CharcoalSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = CharcoalBorder,
    error = CrimsonRed,
    onError = SlateTextPrimary
)

// Light theme fallback for Flick Map (maintains dark charcoal map accent feel)
private val LightColorScheme = lightColorScheme(
    primary = CyanAccentVariant,
    onPrimary = SlateTextPrimary,
    primaryContainer = CyanLight,
    onPrimaryContainer = CharcoalBackground,
    secondary = CyanAccent,
    onSecondary = CharcoalBackground,
    background = CharcoalBackground,
    onBackground = SlateTextPrimary,
    surface = CharcoalSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = CharcoalSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = CharcoalBorder
)

@Composable
fun FlickMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Flick Map is styled primarily dark per brief

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Kept for backward compatibility with template references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    FlickMapTheme(darkTheme = true, content = content)
}
