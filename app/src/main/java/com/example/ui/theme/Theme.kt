package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CoinringColorScheme = darkColorScheme(
  primary = GoldPrimary,
  onPrimary = Color.Black,
  primaryContainer = GoldDark,
  onPrimaryContainer = GoldLight,
  secondary = GoldLight,
  onSecondary = Color.Black,
  background = Obsidian,
  onBackground = TextGold,
  surface = SurfaceDark,
  onSurface = TextGold,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = TextMuted
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = CoinringColorScheme,
    typography = Typography,
    content = content
  )
}
