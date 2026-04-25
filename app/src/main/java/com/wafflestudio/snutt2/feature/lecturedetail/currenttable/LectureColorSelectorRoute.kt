package com.wafflestudio.snutt2.feature.lecturedetail.currenttable

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    BackHandler { vm.onBackPressed() }

    LaunchedEffect(Unit) {
        vm.uiEvent.collect { event ->
            when (event) {
                is LectureColorSelectorUiEvent.NavigateBackWithResult ->
                    onNavigateBackWithResult(event.selectedColor)
            }
        }
    }

    LectureColorSelectorScreen(
        uiState = uiState,
        onBackPressed = vm::onBackPressed,
        onSelectPalette = vm::selectPaletteColor,
        onSelectPicker = vm::selectPickerColor,
        onOpenFgPicker = vm::openFgPicker,
        onOpenBgPicker = vm::openBgPicker,
        onDismissDialog = vm::dismissDialog,
        onPickFgColor = vm::pickFgColor,
        onPickBgColor = vm::pickBgColor,
    )
}
