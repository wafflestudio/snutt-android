package com.wafflestudio.snutt2.views

import androidx.compose.runtime.compositionLocalOf
import com.wafflestudio.snutt2.ui.ThemeMode

val LocalThemeState = compositionLocalOf {
    ThemeMode.AUTO
}
