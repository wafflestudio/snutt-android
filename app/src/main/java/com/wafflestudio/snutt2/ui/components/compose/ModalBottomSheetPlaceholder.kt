package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * ModalBottomSheetLayout의 sheetContent가 "아무것도 표시할 것이 없는" 상태일 때 사용하는 플레이스홀더.
 *
 * sheetContent가 완전히 비어 있으면(height = 0) ModalBottomSheetLayout의 swipeable 앵커가
 * Hidden/Expanded 모두 동일한 위치(fullHeight)로 설정된다. 이 상태에서 show()를 호출하면
 * 애니메이션 이동 거리가 0이 되어 슬라이드업 없이 즉시 snap된다.
 * 최소 1dp의 높이를 확보해 앵커가 서로 다른 위치를 갖도록 한다.
 */
@Composable
fun ModalBottomSheetPlaceholder() {
    Box(modifier = Modifier.size(1.dp))
}
