package com.wafflestudio.snutt2.views

import androidx.compose.runtime.compositionLocalOf
import com.wafflestudio.snutt2.lib.logging.AnalyticsEvent
import com.wafflestudio.snutt2.lib.logging.AnalyticsLogger
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.ui.ThemeMode

val LocalThemeState = compositionLocalOf {
    ThemeMode.AUTO
}

val LocalAnalyticsLogger = compositionLocalOf<AnalyticsLogger> {
    object : AnalyticsLogger {
        override fun logEvent(event: AnalyticsEvent) {}
        override fun logScreen(screen: AnalyticsScreen) {}
    }
}
