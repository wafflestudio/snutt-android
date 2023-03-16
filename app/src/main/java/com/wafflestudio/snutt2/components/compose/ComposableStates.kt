package com.wafflestudio.snutt2.components.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.LocalModalState
import kotlinx.coroutines.CoroutineScope

/* onClick에 Composable이 아닌 일반 함수를 써야 할 경우, 필요한 CompositionLocal들과 scope를 한번에 전달하기 위한 데이터 클래스 */
data class ComposableStates(
    val scope: CoroutineScope,
    val context: Context,
    val modalState: ModalState,
    val bottomSheet: BottomSheet,
    val apiOnProgress: ApiOnProgress,
    val apiOnError: ApiOnError
)

@Composable
fun ComposableStatesWithScope(scope: CoroutineScope) = ComposableStates(
    scope,
    LocalContext.current,
    LocalModalState.current,
    LocalBottomSheetState.current,
    LocalApiOnProgress.current,
    LocalApiOnError.current
)
