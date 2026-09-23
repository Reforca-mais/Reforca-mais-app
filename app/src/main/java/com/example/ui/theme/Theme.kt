package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ReforcaPurpleLight,
    onPrimary = Color.White,
    primaryContainer = ReforcaPurple,
    onPrimaryContainer = Color.White,
    secondary = ReforcaGoldLight,
    onSecondary = Color.Black,
    secondaryContainer = ReforcaGoldDark,
    onSecondaryContainer = Color.White,
    background = Color(0xFF1B1522),
    surface = Color(0xFF261E2F),
    onBackground = Color(0xFFEDE7F2),
    onSurface = Color(0xFFEDE7F2),
    surfaceVariant = Color(0xFF382D42),
    onSurfaceVariant = Color(0xFFD6CEDC)
)

private val LightColorScheme = lightColorScheme(
    primary = ReforcaPurple,
    onPrimary = Color.White,
    primaryContainer = ReforcaPurpleContainer,
    onPrimaryContainer = ReforcaPurpleOnContainer,
    secondary = ReforcaGold,
    onSecondary = Color.White,
    secondaryContainer = ReforcaGoldContainer,
    onSecondaryContainer = ReforcaGoldOnContainer,
    background = ReforcaBackground,
    surface = ReforcaSurface,
    onBackground = ReforcaTextPrimary,
    onSurface = ReforcaTextPrimary,
    surfaceVariant = ReforcaSurfaceVariant,
    onSurfaceVariant = ReforcaTextSecondary,
    outline = Color(0xFFD9D0DF),
    outlineVariant = Color(0xFFEDE8F1)
)

@Composable
fun ReforcaPlusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
