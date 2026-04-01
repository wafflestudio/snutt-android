package com.wafflestudio.snutt2.views.logged_in.lecture_detail.current_table

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.wafflestudio.snutt2.components.compose.BottomSheetDismissEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBarHostState
import com.wafflestudio.snutt2.components.compose.snackbar.SnackBarScaffold
import com.wafflestudio.snutt2.domainmodel.LectureColor
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
fun AddCustomLectureRoute(
    vm: AddCustomLectureViewModel = hiltViewModel(),
    colorSelectorSavedStateHandle: androidx.lifecycle.SavedStateHandle? = null,
    onNavigateBack: () -> Unit,
    onNavigateColorSelector: (LectureColor) -> Unit,
    onNavigateOnboard: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        // FIXME: ColorSelector 동작 전반적으로 다시 고민하기
        val savedStateHandle = colorSelectorSavedStateHandle ?: return@LaunchedEffect
        savedStateHandle.getStateFlow(LectureColorSelectorViewModel.RESULT_COLOR_INDEX, Int.MIN_VALUE)
            .collect { colorIndex ->
                if (colorIndex == Int.MIN_VALUE) return@collect
                val fg = savedStateHandle.get<Int>(LectureColorSelectorViewModel.RESULT_FG) ?: return@collect
                val bg = savedStateHandle.get<Int>(LectureColorSelectorViewModel.RESULT_BG) ?: return@collect
                val color = if (colorIndex == -1) LectureColor.Custom(fg, bg) else LectureColor.BuiltIn(colorIndex)
                vm.editColor(color)
                savedStateHandle.remove<Int>(LectureColorSelectorViewModel.RESULT_COLOR_INDEX)
            }
    }

    val snackBarHostState = remember { CustomSnackBarHostState() }
    val hazeState = rememberHazeState()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    BackHandler(enabled = uiState.sheetType != AddCustomLectureUiState.SheetType.None) {
        vm.closeSheet()
    }

    BottomSheetDismissEffect(sheetState, vm::onSheetDismissed)

    LaunchedEffect(Unit) {
        vm.uiEvent.collect { event ->
            when (event) {
                is AddCustomLectureUiEvent.ShowToast -> {
                    if (event.message.isNotEmpty()) context.toast(event.message)
                }

                is AddCustomLectureUiEvent.OpenBottomSheet -> {
                    scope.launch { sheetState.show() }
                }

                is AddCustomLectureUiEvent.CloseBottomSheet -> {
                    scope.launch { sheetState.hide() }
                }

                is AddCustomLectureUiEvent.LectureCreated -> {
                    onNavigateBack()
                }

                is AddCustomLectureUiEvent.LoggedOut -> {
                    onNavigateOnboard()
                }
            }
        }
    }

    SnackBarScaffold(
        snackBarHostState = snackBarHostState,
        hazeState = hazeState,
    ) { contentPadding ->
        AddCustomLectureBottomSheetLayout(
            uiState = uiState,
            sheetState = sheetState,
            onCloseSheet = vm::closeSheet,
            onEditSessionTime = vm::editSessionTime,
            modifier = Modifier
                .padding(contentPadding)
                .hazeSource(hazeState)
                .logImpression(AnalyticsScreen.LectureCreate),
        ) {
            AddCustomLectureScreen(
                uiState = uiState,
                onDismissDialog = vm::dismissDialog,
                onConfirmDeleteSession = vm::confirmDeleteSession,
                onConfirmForceCreate = vm::confirmForceCreateLecture,
                onBackPressed = {
                    if (uiState.sheetType != AddCustomLectureUiState.SheetType.None) {
                        vm.closeSheet()
                    } else {
                        onNavigateBack()
                    }
                },
                onSave = {
                    focusManager.clearFocus()
                    vm.saveLecture()
                },
                onColorClick = {
                    onNavigateColorSelector(uiState.lecture.color)
                },
                onCreditChange = vm::editCredit,
                onCourseTitleChange = vm::editCourseTitle,
                onInstructorChange = vm::editInstructor,
                onRemarkChange = vm::editRemark,
                onEditTime = { index, _ ->
                    focusManager.clearFocus()
                    vm.openTimePicker(index)
                },
                onLocationChange = vm::editSessionLocation,
                onDeleteSession = vm::requestDeleteSessionDialog,
                onAddSession = vm::addSession,
            )
        }
    }
}

