package com.wafflestudio.snutt2.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightThemeColors = lightColors(
    primary = Black900,
    primaryVariant = Gray400,
    onPrimary = Color.White,
    secondary = Black900,
    secondaryVariant = Gray400,
    onSecondary = Color.White,
    error = White900,
    onBackground = Color.Black,
)

@Composable
fun SnuttTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = LightThemeColors,
        typography = SNUTTTypography,
        content = content
    )
}
