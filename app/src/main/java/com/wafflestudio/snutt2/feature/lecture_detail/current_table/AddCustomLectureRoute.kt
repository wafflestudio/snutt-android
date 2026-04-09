package com.wafflestudio.snutt2.feature.lecture_detail.current_table

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.wafflestudio.snutt2.ui.components.compose.BottomSheetDismissEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBarHostState
import com.wafflestudio.snutt2.ui.components.compose.snackbar.SnackBarScaffold
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.ui.util.toast
import com.wafflestudio.snutt2.navigation.NavigationDestination
import com.wafflestudio.snutt2.navigation.observeResult
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
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
        colorSelectorSavedStateHandle
            ?.observeResult<LectureColor>(NavigationDestination.LectureColorSelector.RESULT_KEY)
            ?.collect { vm.editColor(it) }
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

