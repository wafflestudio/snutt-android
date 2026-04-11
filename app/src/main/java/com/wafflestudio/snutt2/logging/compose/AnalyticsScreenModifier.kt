package com.wafflestudio.snutt2.logging.compose

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.wafflestudio.snutt2.logging.AnalyticsScreen

fun Modifier.logImpression(
    analyticsScreen: AnalyticsScreen,
): Modifier = composed {
    val analyticsLogger = LocalAnalyticsLogger.current
    LaunchedEffect(Unit) {
        analyticsLogger.logScreen(analyticsScreen)
    }
    this
}
