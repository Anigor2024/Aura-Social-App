package com.samr.social.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.samr.social.core.util.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = SamrChampagne,
    onPrimary = ObsidianVoid,
    primaryContainer = SamrEmerald,
    onPrimaryContainer = TextPrimaryDark,
    secondary = SamrCyan,
    onSecondary = ObsidianVoid,
    secondaryContainer = ObsidianElevated,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = SamrRose,
    background = ObsidianVoid,
    onBackground = TextPrimaryDark,
    surface = ObsidianSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ObsidianElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = ObsidianBorder,
    outlineVariant = ObsidianBorder
)

private val OledColorScheme = darkColorScheme(
    primary = SamrChampagne,
    onPrimary = ObsidianVoid,
    primaryContainer = SamrEmerald,
    onPrimaryContainer = TextPrimaryDark,
    secondary = SamrCyan,
    onSecondary = ObsidianVoid,
    secondaryContainer = Color(0xFF111111),
    onSecondaryContainer = TextPrimaryDark,
    tertiary = SamrRose,
    background = Color(0xFF000000),
    onBackground = TextPrimaryDark,
    surface = Color(0xFF000000),
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF141414),
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF242424),
    outlineVariant = Color(0xFF1C1C1C)
)

private val LightColorScheme = lightColorScheme(
    primary = SamrChampagne,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = SamrEmerald,
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = SamrCyan,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = AlabasterElevated,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = SamrRose,
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
fun SamrTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK, AppThemeMode.OLED -> true
    }

    val colorScheme = when (themeMode) {
        AppThemeMode.SYSTEM -> if (isSystemDark) DarkColorScheme else LightColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.OLED -> OledColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SamrTypography,
        content = content
    )
}

@Composable
fun SamrTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) = SamrTheme(
    themeMode = if (darkTheme) AppThemeMode.DARK else AppThemeMode.LIGHT,
    content = content
)
