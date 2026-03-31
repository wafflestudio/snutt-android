package com.wafflestudio.snutt2.views.logged_in.lecture_detail.current_table

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SnackbarResult
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
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBarDuration
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBarHostState
import com.wafflestudio.snutt2.components.compose.snackbar.SnackBarScaffold
import com.wafflestudio.snutt2.components.compose.snackbar.dismiss
import com.wafflestudio.snutt2.domainmodel.LectureColor
import com.wafflestudio.snutt2.domainmodel.LectureReminderOffset
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.getReviewUrl
import com.wafflestudio.snutt2.lib.logging.AddToBookmarkParameter
import com.wafflestudio.snutt2.lib.logging.AddToVacancyParameter
import com.wafflestudio.snutt2.lib.logging.AnalyticsEvent
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.lib.logging.LectureActionReferrer
import com.wafflestudio.snutt2.lib.logging.LectureDetailParameter
import com.wafflestudio.snutt2.lib.logging.LectureSyllabusParameter
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
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
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val analyticsLogger = LocalAnalyticsLogger.current

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        // FIXME: ColorSelector 방식 다시 고민하기
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

    // TODO: 강의평 웹뷰 컨테이너 (나중에 고민)
    val isDarkMode = isDarkMode()
    val reviewWebViewContainer = remember {
        ReviewWebViewContainer(context, vm.accessToken, isDarkMode).apply {
            webView.addJavascriptInterface(
                CloseBridge(onClose = { vm.closeSheet() }),
                "Snutt",
            )
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
            reviewWebViewContainer = reviewWebViewContainer,
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
                onBackPressed = {
                    if (uiState.sheetType !is CurrentTableLectureDetailUiState.SheetType.None) {
                        vm.closeSheet()
                    } else if (uiState.editMode) {
                        vm.requestExitEditMode()
                    } else {
                        onNavigateBack()
                    }
                },
                onEditModeToggle = {
                    focusManager.clearFocus()
                    vm.toggleEditMode()
                },
                onBookmarkToggle = {
                    if (!uiState.isBookmarked) {
                        analyticsLogger.logEvent(
                            AnalyticsEvent.AddToBookmark(
                                AddToBookmarkParameter(
                                    lectureId = vm.getLoggingLectureId(),
                                    referrer = LectureActionReferrer.LectureDetail,
                                ),
                            ),
                        )
                    }
                    vm.toggleBookmark()
                },
                onVacancyToggle = {
                    if (!uiState.vacancyRegistered) {
                        analyticsLogger.logEvent(
                            AnalyticsEvent.AddToVacancy(
                                AddToVacancyParameter(
                                    lectureId = vm.getLoggingLectureId(),
                                    referrer = LectureActionReferrer.LectureDetail,
                                ),
                            ),
                        )
                    }
                    vm.toggleVacancy()
                },
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
                onSyllabus = {
                    analyticsLogger.logScreen(
                        AnalyticsScreen.LectureSyllabus(
                            LectureSyllabusParameter(
                                lectureId = vm.getLoggingLectureId(),
                            ),
                        ),
                    )
                    vm.openSyllabus()
                },
                onReview = {
                    val url = uiState.reviewInfo?.getReviewUrl(context)
                    scope.launch { reviewWebViewContainer.openPage("$url&on_back=close") }
                    vm.openReview()
                },
                onDelete = { vm.requestDeleteLecture() },
                onReset = { vm.requestResetLecture() },
                onFloatingButtonClick = {},
            )
        }
    }
}

