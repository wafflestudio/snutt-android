package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
fun bottomSheet(): BottomSheet {
    return object : BottomSheet {
        override val state: ModalBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )

        override var content by remember {
            mutableStateOf<@Composable ColumnScope.() -> Unit>({
                Box(modifier = Modifier.size(1.dp))
            })
        }

        override suspend fun hide() {
            state.hide()
            content = { Box(modifier = Modifier.size(1.dp)) }
        }
    }
}
