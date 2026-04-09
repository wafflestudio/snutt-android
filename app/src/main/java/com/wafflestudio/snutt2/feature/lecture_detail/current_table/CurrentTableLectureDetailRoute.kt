package com.wafflestudio.snutt2.feature.lecture_detail.current_table

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.logging.LectureDetailParameter
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.navigation.NavigationDestination
import com.wafflestudio.snutt2.navigation.observeResult
import com.wafflestudio.snutt2.ui.components.compose.BottomSheetDismissEffect
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBarDuration
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBarHostState
import com.wafflestudio.snutt2.ui.components.compose.snackbar.SnackBarScaffold
import com.wafflestudio.snutt2.ui.components.compose.snackbar.dismiss
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

    @SuppressLint("LocalContextGetResourceValueCall")
    LaunchedEffect(Unit) {
        vm.uiEvent.collect { event ->
            when (event) {
                is CurrentTableLectureDetailUiEvent.ShowToast -> {
                    if (event.message.isNotEmpty()) context.toast(event.message)
                }

                is CurrentTableLectureDetailUiEvent.ReminderUpdateSuccess -> {
                    val message = when (event.offset) {
                        LectureReminderOffset.NONE -> ""
                        LectureReminderOffset.TEN_MINUTES_BEFORE ->
                            context.getString(R.string.settings_lecture_reminder_update_success_ten_minutes_before)

                        LectureReminderOffset.AT_START_TIME ->
                            context.getString(R.string.settings_lecture_reminder_update_success_at_start_time)

                        LectureReminderOffset.TEN_MINUTES_AFTER ->
                            context.getString(R.string.settings_lecture_reminder_update_success_ten_minutes_after)
                    }
                    if (message.isNotEmpty()) {
                        launch {
                            snackBarHostState.currentSnackBarData.dismiss()
                            val result = snackBarHostState.showSnackBar(
                                message = message,
                                actionLabel = context.getString(R.string.lecture_detail_lecture_reminder_snackbar_navigate),
                                duration = CustomSnackBarDuration(
                                    fadeIn = 500L,
                                    inBetween = 3000L,
                                    fadeOut = 500L,
                                ),
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                onNavigateLectureReminder()
                            }
                        }
                    }
                }

                is CurrentTableLectureDetailUiEvent.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, event.url.toUri())
                    context.startActivity(intent)
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
                onReminderOptionChange = vm::changeLectureReminderOption,
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

