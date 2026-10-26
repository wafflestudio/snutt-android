package com.wafflestudio.snutt2.feature.lecturedetail.currenttable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.logging.LectureDetailParameter
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.navigation.NavigationDestination
import com.wafflestudio.snutt2.navigation.observeResult
import com.wafflestudio.snutt2.ui.components.compose.BottomSheetDismissEffect
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBarHostState
import com.wafflestudio.snutt2.ui.components.compose.snackbar.SnackBarScaffold
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import com.wafflestudio.snutt2.ui.util.openCustomTab
import com.wafflestudio.snutt2.ui.util.toast
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
fun CurrentTableLectureDetailRoute(
    vm: CurrentTableLectureDetailViewModel = hiltViewModel(),
    referrer: DetailScreenReferrer? = null,
    colorSelectorSavedStateHandle: androidx.lifecycle.SavedStateHandle? = null,
    onNavigateBack: () -> Unit,
    onNavigateColorSelector: (LectureColor) -> Unit,
    onNavigateLectureReminder: () -> Unit,
    onNavigateOnboard: () -> Unit,
    onNavigateToReview: (reviewId: String, lectureId: String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val isDarkMode by rememberUpdatedState(isDarkMode())

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

    val handleBack = {
        if (uiState.sheetType != CurrentTableLectureDetailUiState.SheetType.None) {
            vm.closeSheet()
        } else if (uiState.editMode) {
            vm.requestExitEditMode()
        } else {
            onNavigateBack()
        }
    }

    BackHandler {
        handleBack()
    }

    BottomSheetDismissEffect(sheetState, vm::onSheetDismissed)

    LaunchedEffect(Unit) {
        vm.uiEvent.collect { event ->
            when (event) {
                is CurrentTableLectureDetailUiEvent.ShowToast -> {
                    if (event.message.isNotEmpty()) context.toast(event.message)
                }

                is CurrentTableLectureDetailUiEvent.OpenUrl -> {
                    context.openCustomTab(event.url, isDarkMode)
                }

                is CurrentTableLectureDetailUiEvent.OpenBottomSheet -> {
                    scope.launch {
                        sheetState.show()
                    }
                }

                is CurrentTableLectureDetailUiEvent.CloseBottomSheet -> {
                    scope.launch {
                        sheetState.hide()
                    }
                }

                is CurrentTableLectureDetailUiEvent.LectureDeleted -> {
                    onNavigateBack()
                }

                is CurrentTableLectureDetailUiEvent.LoggedOut -> {
                    onNavigateOnboard()
                }
            }
        }
    }

    SnackBarScaffold(
        snackBarHostState = snackBarHostState,
        hazeState = hazeState,
    ) { contentPadding ->
        CurrentTableLectureDetailBottomSheetLayout(
            uiState = uiState,
            sheetState = sheetState,
            onCloseSheet = vm::closeSheet,
            onEditSessionTime = vm::editSessionTime,
            modifier = Modifier
                .padding(contentPadding)
                .hazeSource(hazeState)
                .logImpression(
                    AnalyticsScreen.LectureDetail(
                        LectureDetailParameter(
                            lectureId = vm.getLoggingLectureId(),
                            referrer = referrer,
                        ),
                    ),
                ),
        ) {
            CurrentTableLectureDetailScreen(
                uiState = uiState,
                onDismissDialog = vm::dismissDialog,
                onConfirmExitEditMode = vm::confirmExitEditMode,
                onConfirmDeleteSession = vm::confirmDeleteSession,
                onConfirmDeleteLecture = vm::confirmDeleteLecture,
                onConfirmResetLecture = vm::confirmResetLecture,
                onConfirmForceUpdate = vm::confirmForceUpdateLecture,
                onBackPressed = handleBack,
                onEditModeToggle = {
                    focusManager.clearFocus()
                    vm.toggleEditMode()
                },
                onBookmarkToggle = vm::toggleBookmark,
                onVacancyToggle = vm::toggleVacancy,
                onCourseTitleChange = vm::editCourseTitle,
                onInstructorChange = vm::editInstructor,
                onColorClick = {
                    onNavigateColorSelector(uiState.lecture.color)
                },
                onReminderOptionChange = {},
                onCreditChange = vm::editCredit,
                onDepartmentChange = vm::editDepartment,
                onAcademicYearChange = vm::editAcademicYear,
                onClassificationChange = vm::editClassification,
                onCategoryChange = vm::editCategory,
                onCategoryPre2025Change = vm::editCategoryPre2025,
                onRemarkChange = vm::editRemark,
                onEditTime = { index, _ ->
                    focusManager.clearFocus()
                    vm.openTimePicker(index)
                },
                onLocationChange = vm::editSessionLocation,
                onDeleteSession = vm::requestDeleteSessionDialog,
                onAddSession = vm::addSession,
                onSyllabus = vm::openSyllabus,
                onReview = {
                    uiState.reviewInfo?.id?.let { reviewId ->
                        onNavigateToReview(reviewId, vm.getLoggingLectureId())
                    }
                },
                onDelete = { vm.requestDeleteLecture() },
                onReset = { vm.requestResetLecture() },
                onFloatingButtonClick = {},
            )
        }
    }
}
