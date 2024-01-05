package com.wafflestudio.snutt2.components.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp

class ModalState {
    var isVisible: Boolean by mutableStateOf(false)
    var onDismiss: () -> Unit by mutableStateOf({})
    var onConfirm: () -> Unit by mutableStateOf({})
    var title: String? by mutableStateOf(null)
    var positiveButtonText: String? by mutableStateOf(null)
    var negativeButtonText: String? by mutableStateOf(null)
    var width: Dp? by mutableStateOf(null)
    var content: @Composable () -> Unit by mutableStateOf({})

    fun show() {
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }

    /*
    * onDismiss: 모달이 닫힐 때 & negative 버튼을 누를 때 수행할 동작
    * onConfirm: positive 버튼을 누를 때 수행할 동작
    * title: 다이얼로그의 제목 (null이면 제목 없는 모달)
    * positiveButton: onConfirm을 수행하는 버튼 텍스트
    * negativeButton: onDismiss를 수행하는 버튼 텍스트
    * width: 다이얼로그의 너비 dp (null이면 기본값: 화면 넓이에서 좌우 패딩 50.dp)
    * content: 다이얼로그 내부 컴포저블
    */
    fun set(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit = {},
        title: String? = null,
        positiveButton: String? = null,
        negativeButton: String? = null,
        width: Dp? = null,
        content: @Composable () -> Unit,
    ): ModalState {
        return this.apply {
            this.onDismiss = onDismiss
            this.onConfirm = onConfirm
            this.title = title
            this.positiveButtonText = positiveButton
            this.negativeButtonText = negativeButton
            this.width = width
            this.content = content
        }
    }

    fun setOkCancel(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit,
        title: String,
    ): ModalState {
        return this.apply {
            this.onDismiss = onDismiss
            this.onConfirm = onConfirm
            this.title = title
            this.width = null
            this.content = {}
        }
    }

    companion object {
        fun Saver() = Saver<ModalState, Boolean>(save = { it.isVisible }, restore = { null })
    }
}

@Composable
fun ShowModal(
    state: ModalState,
) {
    if (state.isVisible) {
        CustomDialog(
            onDismiss = state.onDismiss,
            onConfirm = state.onConfirm,
            title = state.title,
            positiveButtonText = state.positiveButtonText,
            negativeButtonText = state.negativeButtonText,
            width = state.width,
        ) {
            state.content()
        }
    }
}

@Composable
fun rememberModalState(): ModalState {
    return rememberSaveable(saver = ModalState.Saver()) {
        ModalState()
    }
}
