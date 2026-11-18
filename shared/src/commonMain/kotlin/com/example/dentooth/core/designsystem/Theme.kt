package com.example.dentooth.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = DentoothColors.Primary,
    onPrimary = DentoothColors.OnPrimary,
    primaryContainer = DentoothColors.PrimaryContainer,
    onPrimaryContainer = DentoothColors.OnPrimaryContainer,
    secondary = DentoothColors.Secondary,
    onSecondary = DentoothColors.OnSecondary,
    secondaryContainer = DentoothColors.SecondaryContainer,
    onSecondaryContainer = DentoothColors.OnSecondaryContainer,
    tertiary = DentoothColors.Tertiary,
    onTertiary = DentoothColors.OnTertiary,
    tertiaryContainer = DentoothColors.TertiaryContainer,
    onTertiaryContainer = DentoothColors.OnTertiaryContainer,
    error = DentoothColors.Error,
    onError = DentoothColors.OnError,
    errorContainer = DentoothColors.ErrorContainer,
    onErrorContainer = DentoothColors.OnErrorContainer,
    background = DentoothColors.Background,
    onBackground = DentoothColors.OnBackground,
    surface = DentoothColors.Surface,
    onSurface = DentoothColors.OnSurface,
    surfaceVariant = DentoothColors.SurfaceVariant,
    onSurfaceVariant = DentoothColors.OnSurfaceVariant,
    outline = DentoothColors.Outline,
    outlineVariant = DentoothColors.OutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = DentoothColors.PrimaryDark,
    onPrimary = DentoothColors.OnPrimaryDark,
    primaryContainer = DentoothColors.PrimaryContainerDark,
    onPrimaryContainer = DentoothColors.OnPrimaryContainerDark,
    secondary = DentoothColors.Secondary,
    onSecondary = DentoothColors.OnSecondary,
    tertiary = DentoothColors.Tertiary,
    onTertiary = DentoothColors.OnTertiary,
    error = DentoothColors.Error,
    onError = DentoothColors.OnError,
    background = DentoothColors.BackgroundDark,
    onBackground = DentoothColors.OnBackgroundDark,
    surface = DentoothColors.SurfaceDark,
    onSurface = DentoothColors.OnSurfaceDark,
    surfaceVariant = DentoothColors.SurfaceVariantDark,
    onSurfaceVariant = DentoothColors.OnSurfaceVariantDark,
    outline = DentoothColors.Outline,
    outlineVariant = DentoothColors.OutlineVariant
)

@Composable
fun DentoothTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val spacing = DentoothSpacing()

    CompositionLocalProvider(
        LocalDentoothSpacing provides spacing
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = DentoothShapes,
            content = content
        )
    }
}
