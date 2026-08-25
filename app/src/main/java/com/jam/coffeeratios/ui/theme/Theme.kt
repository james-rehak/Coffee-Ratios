package com.jam.coffeeratios.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = Crema,
  onPrimary = Bean,
  secondary = CremaLight,
  background = Bean,
  surface = Bean,
  onBackground = Foam,
  onSurface = Foam,
)

private val LightColorScheme = lightColorScheme(
  primary = Espresso,
  onPrimary = Foam,
  secondary = Roast,
  background = Foam,
  surface = Foam,
  onBackground = Bean,
  onSurface = Bean,
)

@Composable
fun CoffeeRatiosTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
    typography = Typography,
    content = content,
  )
}
