package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.MindWellThemeId

private val MidnightIndigoScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoSoft,
    onPrimaryContainer = IndigoOnSoft,
    secondary = TealSecondary,
    onSecondary = Color.White,
    secondaryContainer = TealSoft,
    onSecondaryContainer = TealOnSoft,
    tertiary = RoseAccent,
    onTertiary = Color.White,
    tertiaryContainer = RoseSoft,
    onTertiaryContainer = RoseOnSoft,
    background = SurfaceBackground,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,
    outline = OutlineSubtle,
    error = CrisisRed,
    onError = Color.White,
    errorContainer = CrisisRedSoft,
    onErrorContainer = CrisisRed
)

private val SageForestScheme = lightColorScheme(
    primary = SagePrimary,
    onPrimary = Color.White,
    primaryContainer = SageContainer,
    onPrimaryContainer = Color(0xFF16382B),
    secondary = SageSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4EFE8),
    onSecondaryContainer = Color(0xFF1F3A2A),
    background = SageBg,
    onBackground = TextPrimary,
    surface = SageSurface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFEAF0EB),
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFFD0DDD4),
    error = CrisisRed,
    onError = Color.White,
    errorContainer = CrisisRedSoft,
    onErrorContainer = CrisisRed
)

private val WarmSandScheme = lightColorScheme(
    primary = SandPrimary,
    onPrimary = Color.White,
    primaryContainer = SandContainer,
    onPrimaryContainer = Color(0xFF78350F),
    secondary = SandSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = Color(0xFF7C2D12),
    background = SandBg,
    onBackground = Color(0xFF292524),
    surface = SandSurface,
    onSurface = Color(0xFF292524),
    surfaceVariant = Color(0xFFF5ECE1),
    onSurfaceVariant = Color(0xFF57534E),
    outline = Color(0xFFE7D8C9),
    error = CrisisRed,
    onError = Color.White,
    errorContainer = CrisisRedSoft,
    onErrorContainer = CrisisRed
)

private val HighContrastDarkScheme = darkColorScheme(
    primary = DarkContrastPrimary,
    onPrimary = DarkContrastBg,
    primaryContainer = DarkContrastContainer,
    onPrimaryContainer = DarkContrastText,
    secondary = DarkContrastSecondary,
    onSecondary = DarkContrastBg,
    secondaryContainer = Color(0xFF134E4A),
    onSecondaryContainer = DarkContrastText,
    background = DarkContrastBg,
    onBackground = DarkContrastText,
    surface = DarkContrastSurface,
    onSurface = DarkContrastText,
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = DarkContrastMuted,
    outline = Color(0xFF4B5563),
    error = Color(0xFFF87171),
    onError = DarkContrastBg,
    errorContainer = Color(0xFF450A0A),
    onErrorContainer = Color(0xFFFEE2E2)
)

val MindWellShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

private fun Typography.scaled(factor: Float): Typography {
    if (factor == 1f) return this
    return copy(
        headlineMedium = headlineMedium.copy(fontSize = headlineMedium.fontSize * factor),
        titleLarge = titleLarge.copy(fontSize = titleLarge.fontSize * factor),
        titleMedium = titleMedium.copy(fontSize = titleMedium.fontSize * factor),
        bodyLarge = bodyLarge.copy(fontSize = bodyLarge.fontSize * factor),
        bodyMedium = bodyMedium.copy(fontSize = bodyMedium.fontSize * factor),
        labelLarge = labelLarge.copy(fontSize = labelLarge.fontSize * factor)
    )
}

@Composable
fun MindWellTheme(
    themeId: MindWellThemeId = MindWellThemeId.MIDNIGHT_INDIGO,
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val scheme = when (themeId) {
        MindWellThemeId.MIDNIGHT_INDIGO -> MidnightIndigoScheme
        MindWellThemeId.SAGE_FOREST -> SageForestScheme
        MindWellThemeId.WARM_SAND -> WarmSandScheme
        MindWellThemeId.HIGH_CONTRAST_DARK -> HighContrastDarkScheme
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = MindWellTypography.scaled(fontScale.coerceIn(0.85f, 1.35f)),
        shapes = MindWellShapes,
        content = content
    )
}
