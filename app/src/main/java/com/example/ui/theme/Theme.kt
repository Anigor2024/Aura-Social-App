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
    primary = AuraChampagne,
    onPrimary = ObsidianVoid,
    primaryContainer = AuraViolet,
    onPrimaryContainer = TextPrimaryDark,
    secondary = AuraCyan,
    onSecondary = ObsidianVoid,
    secondaryContainer = ObsidianElevated,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = AuraRose,
    background = ObsidianVoid,
    onBackground = TextPrimaryDark,
    surface = ObsidianSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ObsidianElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = ObsidianBorder,
    outlineVariant = ObsidianBorder
)

private val LightColorScheme = lightColorScheme(
    primary = AuraChampagne,
    onPrimary = ObsidianVoid,
    primaryContainer = AuraVioletGlow,
    onPrimaryContainer = ObsidianVoid,
    secondary = AuraCyan,
    onSecondary = ObsidianVoid,
    secondaryContainer = AlabasterElevated,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = AuraRose,
    background = AlabasterBackground,
    onBackground = TextPrimaryLight,
    surface = AlabasterSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = AlabasterElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = AlabasterBorder,
    outlineVariant = AlabasterBorder
)

@Composable
fun AuraTheme(
    darkTheme: Boolean = true, // Luxury dark by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AuraTypography,
        content = content
    )
}
