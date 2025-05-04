package com.wafflestudio.snutt2.lib.logging

interface AnalyticsLogger {
    fun logEvent(event: AnalyticsEvent)
    fun logScreen(screen: AnalyticsScreen)
}
