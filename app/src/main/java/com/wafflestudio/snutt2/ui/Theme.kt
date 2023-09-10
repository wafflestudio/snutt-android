package com.wafflestudio.snutt2.ui

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.views.LocalThemeState

private val LightThemeColors @Composable get() = lightColors(
    primary = SNUTTColors.White900,
    primaryVariant = SNUTTColors.Gray400,
    onPrimary = SNUTTColors.Black900,
    error = SNUTTColors.Red,
    background = SNUTTColors.White900,
    onBackground = SNUTTColors.Black900,
    surface = SNUTTColors.White900,
    onSurface = SNUTTColors.Black900,
)

private val DarkThemeColors @Composable get() = darkColors(
    primary = SNUTTColors.White900,
    primaryVariant = SNUTTColors.Gray400,
    onPrimary = SNUTTColors.Black900,
    error = SNUTTColors.Red,
    background = SNUTTColors.White900,
    onBackground = SNUTTColors.Black900,
    surface = SNUTTColors.White900,
    onSurface = SNUTTColors.Black900,
)

enum class ThemeMode {
    DARK, LIGHT, AUTO, ;

    override fun toString(): String {
        return when (this) {
            DARK -> "다크"
            LIGHT -> "라이트"
            AUTO -> "자동"
        }
    }
}

@Composable
fun isDarkMode(): Boolean {
    return when (LocalThemeState.current) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.AUTO -> isSystemInDarkTheme()
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
    /* <다크모드에서 내비게이션 시 흰색 깜빡이는 이슈 해결>
     * 내비게이션 시 액티비티 배경색인 흰색(styles.xml에서 android:windowBackground 로 지정된 색)이 잠깐 노출된다.
     * 원래는 values-night/styles.xml를 통해 다크모드의 색을 지정하지만, 우리는 시스템의 테마와 앱의 테마를
     * 다르게 설정할 수 있기 때문에 여기서 직접 설정해 준다.
     */
    (LocalContext.current as Activity).window.setBackgroundDrawableResource(
        if (isDarkMode()) {
            R.color.black_dark
        } else {
            R.color.white
        },
    )
    MaterialTheme(
        colors = if (isDarkMode()) DarkThemeColors else LightThemeColors,
        typography = SNUTTTypography,
        content = content,
    )
}
