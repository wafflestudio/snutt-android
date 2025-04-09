package com.wafflestudio.snutt2.lib.logging.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * 검색 옵션 바텀시트가 실제로 화면에 보이는 시점에 logScreen하기 위해
 * SearchOptionSheet의 modifier에 analyticsScreen을 바로 붙이지 않고
 * 별도 컴포저블으로 분리함.
 */
@Composable
fun SearchOptionSheetLogEffect(bottomSheetState: BottomSheet) {
    val logger = LocalAnalyticsLogger.current
    LaunchedEffect(bottomSheetState) {
        snapshotFlow { bottomSheetState.isVisible }
            .distinctUntilChanged()
            .collect { isOpen ->
                if (isOpen) {
                    // 실제로 drawerContent가 화면에 보이기 시작한 시점
                    logger.logScreen(AnalyticsScreen.SearchFilter)
                }
            }
    }
}
