package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterialApi::class)
interface BottomSheet {

    val state: ModalBottomSheetState

    var content: @Composable ColumnScope.() -> Unit

    val isVisible: Boolean get() = state.isVisible

    fun setSheetContent(n: @Composable ColumnScope.() -> Unit) { content = n }

    suspend fun show() = state.show()

    suspend fun hide()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(): BottomSheet {
    return object : BottomSheet {
        override val state: ModalBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )

        override var content by remember {
            mutableStateOf<@Composable ColumnScope.() -> Unit>({
                ModalBottomSheetPlaceholder()
            },)
        }

        override suspend fun hide() {
            state.hide()
            content = { ModalBottomSheetPlaceholder() }
        }
    }
}
