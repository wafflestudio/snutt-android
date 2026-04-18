package com.wafflestudio.snutt2.logging

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsLogger @Inject constructor(
    context: Context,
) : AnalyticsLogger {
    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    /**
     * 이벤트를 기록한다.
     */
    override fun logEvent(event: AnalyticsEvent) {
        val eventName = event::class.simpleName?.toSnakeCase() ?: return
        val parameters = event.getExtraParameters()

        logEvent(eventName, parameters)
    }

    /**
     * `screen_view` 이벤트를 기록한다.
     *
     * 스크린 뷰 이벤트에 추가 매개변수가 있는 경우, `screen_view` 이벤트와 함께 보내면 Firebase 콘솔에서 해당 매개변수를 분석하기 어려운 문제가 있습니다.
     * 따라서 추가 매개변수는 [logEvent] 메서드를 사용하여 중복 기록합니다.
     */
    override fun logScreen(screen: AnalyticsScreen) {
        val screenName = screen::class.simpleName?.toSnakeCase() ?: return
        val parameters = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }

        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, parameters)

        screen.getExtraParameters()?.let {
            logEvent(screenName, it)
        }
    }

    private fun logEvent(eventName: String, parameters: Bundle) {
        firebaseAnalytics.logEvent(eventName, parameters)
        logLocalTrace("[AnalyticsEvent] $eventName recorded with $parameters.")
    }

    private fun String.toSnakeCase(): String = replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()

    private fun logLocalTrace(message: String) {
        Timber.d(message)
    }
}
