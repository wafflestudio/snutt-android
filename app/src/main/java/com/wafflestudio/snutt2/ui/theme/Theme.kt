package com.wafflestudio.snutt2.ui.theme

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.views.LocalThemeState

private val LightThemeColors @Composable get() = lightColors(
    primary = SNUTTColors.White,
    primaryVariant = SNUTTColors.Gray400,
    onPrimary = SNUTTColors.Black,
    secondary = SNUTTColors.MainBlue,
    onSecondary = SNUTTColors.White,
    error = SNUTTColors.Red,
    background = SNUTTColors.Gray,
    onBackground = SNUTTColors.DarkGray,
    surface = SNUTTColors.White,
    onSurface = SNUTTColors.Black,
)

private val DarkThemeColors @Composable get() = darkColors(
    primary = SNUTTColors.ExtraDarkGray,
    primaryVariant = SNUTTColors.Gray400,
    onPrimary = SNUTTColors.White,
    secondary = SNUTTColors.DarkMainBlue,
    onSecondary = SNUTTColors.White,
    error = SNUTTColors.Red,
    background = SNUTTColors.Gray900,
    onBackground = SNUTTColors.Gray30,
    surface = SNUTTColors.ExtraDarkGray,
    onSurface = SNUTTColors.White,
)

val Colors.onSurfaceVariant: Color
    get() = if (isLight) SNUTTColors.DarkerGray else SNUTTColors.Gray30

enum class ThemeMode(@param:StringRes val labelResId: Int) {
    DARK(R.string.theme_mode_dark),
    LIGHT(R.string.theme_mode_light),
    AUTO(R.string.theme_mode_auto),
    ;
}

@Composable
fun isDarkMode(): Boolean {
    return when (LocalThemeState.current) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.AUTO -> isSystemInDarkTheme()
    }
}

fun isDarkMode(
    context: Context,
    theme: ThemeMode,
): Boolean {
    return when (theme) {
        ThemeMode.AUTO -> isSystemDarkMode(context)
        else -> (theme == ThemeMode.DARK)
    }
}

fun isSystemDarkMode(context: Context): Boolean {
    return when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        else -> false
    }
}

@Composable
fun SNUTTTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (isDarkMode()) DarkThemeColors else LightThemeColors,
        typography = SNUTTTypography,
        content = content,
    )
}
