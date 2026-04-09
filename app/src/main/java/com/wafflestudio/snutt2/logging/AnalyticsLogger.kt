package com.wafflestudio.snutt2.logging

interface AnalyticsLogger {
    fun logEvent(event: AnalyticsEvent)
    fun logScreen(screen: AnalyticsScreen)
}
