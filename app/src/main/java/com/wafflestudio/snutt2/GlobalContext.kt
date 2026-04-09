package com.wafflestudio.snutt2

import androidx.compose.runtime.compositionLocalOf
import com.wafflestudio.snutt2.ui.theme.ThemeMode

val LocalThemeState = compositionLocalOf {
    ThemeMode.AUTO
}
