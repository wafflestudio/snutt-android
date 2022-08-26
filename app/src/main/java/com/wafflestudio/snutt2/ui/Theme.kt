package com.wafflestudio.snutt2.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightThemeColors = lightColors(
    primary = SNUTTColors.White900,
    primaryVariant = Gray400,
    onPrimary = SNUTTColors.Black900,
    error = SNUTTColors.Red,
    background = SNUTTColors.White900,
    onBackground = SNUTTColors.Black900,
    surface = SNUTTColors.White900,
    onSurface = SNUTTColors.Black900,
)

@Composable
fun SNUTTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = LightThemeColors,
        typography = SNUTTTypography,
        content = content
    )
}
