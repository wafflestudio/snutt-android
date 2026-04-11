package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter

/**
 * 바텀시트가 닫힌 뒤(애니메이션 완료 후) [onDismissed]를 호출하는 사이드이펙트.
 *
 * ViewModel의 UiState(sheetType 등)를 바텀시트 닫기 애니메이션이 끝난 뒤에 정리할 때 사용한다.
 * 닫기 전에 UiState를 즉시 변경하면 sheetContent가 먼저 비워져 UI가 어색해지는 문제를 방지한다.
 */
@Composable
fun BottomSheetDismissEffect(
    sheetState: ModalBottomSheetState,
    onDismissed: () -> Unit,
) {
    val currentOnDismissed by rememberUpdatedState(onDismissed)
    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }
            .distinctUntilChanged()
            .drop(1)
            .filter { it == ModalBottomSheetValue.Hidden }
            .collect { currentOnDismissed() }
    }
}
