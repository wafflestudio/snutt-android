package com.wafflestudio.snutt2.logging.compose

import androidx.compose.runtime.compositionLocalOf
import com.wafflestudio.snutt2.logging.AnalyticsEvent
import com.wafflestudio.snutt2.logging.AnalyticsLogger
import com.wafflestudio.snutt2.logging.AnalyticsScreen

val LocalAnalyticsLogger = compositionLocalOf<AnalyticsLogger> {
    object : AnalyticsLogger {
        override fun logEvent(event: AnalyticsEvent) {}
        override fun logScreen(screen: AnalyticsScreen) {}
    }
}
