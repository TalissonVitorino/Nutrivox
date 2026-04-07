package com.kotlincrossplatform.nutrivox.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Teal600,
    onPrimary = SurfaceLight,
    primaryContainer = Teal100,
    onPrimaryContainer = Teal900,
    secondary = Teal400,
    onSecondary = SurfaceLight,
    secondaryContainer = Teal50,
    onSecondaryContainer = Teal800,
    tertiary = AIPurple,
    onTertiary = SurfaceLight,
    tertiaryContainer = AIPurpleSurface,
    onTertiaryContainer = AIPurpleDark,
    error = Error,
    onError = SurfaceLight,
    errorContainer = ErrorLight,
    onErrorContainer = Error,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = Neutral,
    outlineVariant = NeutralLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Teal400,
    onPrimary = Teal900,
    primaryContainer = Teal700,
    onPrimaryContainer = Teal100,
    secondary = Teal200,
    onSecondary = Teal900,
    secondaryContainer = Teal800,
    onSecondaryContainer = Teal100,
    tertiary = AIPurpleLight,
    onTertiary = AIPurpleDark,
    tertiaryContainer = AIPurpleDark,
    onTertiaryContainer = AIPurpleLight,
    error = Error,
    onError = SurfaceDark,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = ErrorLight,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = Neutral,
    outlineVariant = SurfaceVariantDark
)

@Composable
fun NutrivoxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NutrivoxTypography,
        shapes = NutrivoxShapes,
        content = content
    )
}
