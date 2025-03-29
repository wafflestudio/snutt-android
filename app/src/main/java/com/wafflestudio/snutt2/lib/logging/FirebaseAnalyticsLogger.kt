package com.wafflestudio.snutt2.lib.logging

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsLogger @Inject constructor(
    context: Context
) : AnalyticsLogger {
    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logEvent(event: AnalyticsEvent) {
        val eventName = event::class.simpleName?.toSnakeCase() ?: return
        val parameters = event.getExtraParameters()

        firebaseAnalytics.logEvent(eventName, parameters)
        logLocalTrace("[AnalyticsEvent] $eventName recorded with $parameters.")
    }

    override fun logScreen(screen: AnalyticsScreen) {
        val screenName = screen::class.simpleName?.toSnakeCase() ?: return
        val parameters = screen.getExtraParameters().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, parameters)
        logLocalTrace("[AnalyticsScreen] $screenName recorded with $parameters.")
    }

    private fun String.toSnakeCase(): String {
        return replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
    }

    private fun logLocalTrace(message: String) {
        Timber.d(message)
    }
}
