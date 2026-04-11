package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.logging.AnalyticsEvent
import com.wafflestudio.snutt2.logging.AnalyticsLogger
import com.wafflestudio.snutt2.logging.AnalyticsScreen

class FakeAnalyticsLogger : AnalyticsLogger {

    val loggedEvents = mutableListOf<AnalyticsEvent>()

    override fun logEvent(event: AnalyticsEvent) {
        loggedEvents.add(event)
    }

    override fun logScreen(screen: AnalyticsScreen) {
        // no-op
    }
}
