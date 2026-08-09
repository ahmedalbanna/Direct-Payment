package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = NaturalPrimaryDark,
    onPrimary = NaturalOnPrimaryDark,
    primaryContainer = NaturalPrimaryContainerDark,
    onPrimaryContainer = NaturalOnPrimaryContainerDark,
    inversePrimary = NaturalPrimary,
    secondary = NaturalSecondaryDark,
    onSecondary = NaturalOnSecondaryDark,
    secondaryContainer = NaturalSecondaryContainerDark,
    onSecondaryContainer = NaturalOnSecondaryContainerDark,
    tertiary = NaturalTertiaryDark,
    onTertiary = NaturalOnTertiaryDark,
    tertiaryContainer = NaturalTertiaryContainerDark,
    onTertiaryContainer = NaturalOnTertiaryContainerDark,
    background = NaturalBackgroundDark,
    onBackground = NaturalOnBackgroundDark,
    surface = NaturalSurfaceDark,
    onSurface = NaturalOnSurfaceDark,
    surfaceVariant = NaturalSurfaceVariantDark,
    onSurfaceVariant = NaturalOnSurfaceVariantDark,
    surfaceTint = NaturalPrimaryDark,
    inverseSurface = NaturalOnSurface,
    inverseOnSurface = NaturalSurface,
    error = NaturalErrorDark,
    onError = NaturalOnErrorDark,
    errorContainer = NaturalErrorContainerDark,
    onErrorContainer = NaturalOnErrorContainerDark,
    outline = NaturalOutlineDark,
    outlineVariant = NaturalOutlineVariantDark,
    scrim = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = NaturalPrimary,
    onPrimary = NaturalOnPrimary,
    primaryContainer = NaturalPrimaryContainer,
    onPrimaryContainer = NaturalOnPrimaryContainer,
    inversePrimary = NaturalPrimaryDark,
    secondary = NaturalSecondary,
    onSecondary = NaturalOnSecondary,
    secondaryContainer = NaturalSecondaryContainer,
    onSecondaryContainer = NaturalOnSecondaryContainer,
    tertiary = NaturalTertiary,
    onTertiary = NaturalOnTertiary,
    tertiaryContainer = NaturalTertiaryContainer,
    onTertiaryContainer = NaturalOnTertiaryContainer,
    background = NaturalBackground,
    onBackground = NaturalOnBackground,
    surface = NaturalSurface,
    onSurface = NaturalOnSurface,
    surfaceVariant = NaturalSurfaceVariant,
    onSurfaceVariant = NaturalOnSurfaceVariant,
    surfaceTint = NaturalPrimary,
    inverseSurface = NaturalOnSurfaceDark,
    inverseOnSurface = NaturalSurfaceDark,
    error = NaturalError,
    onError = NaturalOnError,
    errorContainer = NaturalErrorContainer,
    onErrorContainer = NaturalOnErrorContainer,
    outline = NaturalOutline,
    outlineVariant = NaturalOutlineVariant,
    scrim = Color.Black
)

@Composable
fun AlJanoubSadadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Force Right-To-Left (RTL) for Arabic UI layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

