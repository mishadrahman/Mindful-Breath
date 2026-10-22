package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = SageGreenPrimaryDark,
  onPrimary = SageGreenOnPrimaryDark,
  primaryContainer = SageGreenContainerDark,
  onPrimaryContainer = SageGreenOnContainerDark,
  secondary = SoftTealSecondaryDark,
  onSecondary = SoftTealOnSecondaryDark,
  secondaryContainer = SoftTealContainerDark,
  onSecondaryContainer = SoftTealOnContainerDark,
  tertiary = CalmTertiaryDark,
  onTertiary = CalmOnTertiaryDark,
  tertiaryContainer = CalmTertiaryContainerDark,
  onTertiaryContainer = CalmOnTertiaryContainerDark,
  background = DeepSlateBackgroundDark,
  onBackground = OnSurfaceDark,
  surface = DeepSlateSurfaceDark,
  onSurface = OnSurfaceDark,
  surfaceVariant = DeepSlateSurfaceVariantDark,
  onSurfaceVariant = OnSurfaceDark.copy(alpha = 0.8f),
  outline = OutlineDark,
  outlineVariant = OutlineDark.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
  primary = SageGreenPrimaryLight,
  onPrimary = SageGreenOnPrimaryLight,
  primaryContainer = SageGreenContainerLight,
  onPrimaryContainer = SageGreenOnContainerLight,
  secondary = SoftTealSecondaryLight,
  onSecondary = SoftTealOnSecondaryLight,
  secondaryContainer = SoftTealContainerLight,
  onSecondaryContainer = SoftTealOnContainerLight,
  tertiary = CalmTertiaryLight,
  onTertiary = CalmOnTertiaryLight,
  tertiaryContainer = CalmTertiaryContainerLight,
  onTertiaryContainer = CalmOnTertiaryContainerLight,
  background = GentleBackgroundLight,
  onBackground = OnSurfaceLight,
  surface = GentleSurfaceLight,
  onSurface = OnSurfaceLight,
  surfaceVariant = GentleSurfaceVariantLight,
  onSurfaceVariant = OnSurfaceLight.copy(alpha = 0.8f),
  outline = OutlineLight,
  outlineVariant = OutlineLight.copy(alpha = 0.5f)
)

@Composable
fun MindfulBreathTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
