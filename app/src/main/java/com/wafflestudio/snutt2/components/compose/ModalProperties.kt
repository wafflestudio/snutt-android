package com.wafflestudio.snutt2.components.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

data class ModalProperties(
    val onDismiss: () -> Unit,
    val onConfirm: () -> Unit,
    val title: String? = null,
    val positiveButton: String? = null,
    val negativeButton: String? = null,
    val width: Dp? = null,
    val content: @Composable () -> Unit,
)
