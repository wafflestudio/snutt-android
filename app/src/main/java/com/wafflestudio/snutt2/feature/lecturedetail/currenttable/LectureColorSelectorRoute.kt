package com.wafflestudio.snutt2.feature.lecturedetail.currenttable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.domain.model.LectureColor

@Composable
fun LectureColorSelectorRoute(
    vm: LectureColorSelectorViewModel = hiltViewModel(),
    onNavigateBackWithResult: (LectureColor) -> Unit,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LectureColorSelectorScreen(
        uiState = uiState,
        onBackPressed = {
            val selected = (uiState.contentState as? LectureColorSelectorUiState.ContentState.Loaded)
                ?.selectedColor ?: LectureColor.BuiltIn(0)
            onNavigateBackWithResult(selected)
        },
        onSelectPalette = vm::selectPaletteColor,
        onSelectCustom = vm::selectCustom,
        onOpenFgPicker = vm::openFgPicker,
        onOpenBgPicker = vm::openBgPicker,
        onDismissDialog = vm::dismissDialog,
        onPickFgColor = vm::pickFgColor,
        onPickBgColor = vm::pickBgColor,
    )
}
