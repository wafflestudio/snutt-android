package com.wafflestudio.snutt2.logging

import androidx.compose.runtime.compositionLocalOf

val LocalAnalyticsLogger = compositionLocalOf<AnalyticsLogger> {
    object : AnalyticsLogger {
        override fun logEvent(event: AnalyticsEvent) {}
        override fun logScreen(screen: AnalyticsScreen) {}
    }
}
