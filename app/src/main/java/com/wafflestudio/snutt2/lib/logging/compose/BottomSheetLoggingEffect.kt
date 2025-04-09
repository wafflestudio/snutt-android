package com.wafflestudio.snutt2.lib.logging.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun BottomSheetLoggingEffect(
    bottomSheetState: BottomSheet,
    analyticsScreen: AnalyticsScreen,
) {
    val logger = LocalAnalyticsLogger.current
    LaunchedEffect(bottomSheetState) {
        snapshotFlow { bottomSheetState.isVisible }
            .distinctUntilChanged()
            .collect { isOpen ->
                if (isOpen) {
                    logger.logScreen(analyticsScreen)
                }
            }
    }
}
