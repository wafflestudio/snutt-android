package com.wafflestudio.snutt2.lib.logging.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger

/**
 * Popup 로그는 각 팝업 이미지마다 따로 기록되어야 하므로
 * Popup의 modifier에 analyticsScreen을 바로 붙이지 않고
 * LaunchedEffect를 이용하여 따로 기록한다.
 */
@Composable
fun PopupLoggingEffect(
    imageUri: String,
) {
    val logger = LocalAnalyticsLogger.current
    LaunchedEffect(imageUri) {
        logger.logScreen(AnalyticsScreen.Popup)
    }
}
