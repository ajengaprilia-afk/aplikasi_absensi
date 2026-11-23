package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val GsysDarkColorScheme = darkColorScheme(
    primary = GsysCyan,
    onPrimary = GsysDeepNavy,
    primaryContainer = GsysSurfaceVariant,
    onPrimaryContainer = GsysCyan,
    secondary = GsysTeal,
    onSecondary = GsysDeepNavy,
    secondaryContainer = Color(0xFF102A3A),
    onSecondaryContainer = GsysTeal,
    tertiary = GsysIndigo,
    onTertiary = Color.White,
    background = GsysDeepNavy,
    onBackground = GsysTextPrimary,
    surface = GsysMidnightSurface,
    onSurface = GsysTextPrimary,
    surfaceVariant = GsysSurfaceVariant,
    onSurfaceVariant = GsysTextSecondary,
    outline = GsysGlassBorderBright,
    outlineVariant = GsysGlassBorderSubtle,
    error = GsysError,
    onError = GsysDeepNavy
)

val GsysShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun GsysTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GsysDarkColorScheme,
        typography = GsysTypography,
        shapes = GsysShapes,
        content = content
    )
}
