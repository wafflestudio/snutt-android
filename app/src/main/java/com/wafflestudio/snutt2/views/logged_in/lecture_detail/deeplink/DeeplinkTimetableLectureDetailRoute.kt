package com.wafflestudio.snutt2.views.logged_in.lecture_detail.deeplink

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.getReviewUrl
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebViewNew
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.refactor.LectureDetail
import kotlinx.coroutines.launch

@Composable
fun DeeplinkTimetableLectureDetailRoute(
    vm: DeeplinkTimetableLectureDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val isDarkMode = isDarkMode()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val reviewWebViewContainer = remember {
        ReviewWebViewContainer(context, vm.accessToken, isDarkMode).apply {
            webView.addJavascriptInterface(
                CloseBridge(onClose = { vm.closeReview() }),
                "Snutt",
            )
        }
    }

    LaunchedEffect(Unit) {
        vm.uiEvent.collect { event ->
            when (event) {
                is DeeplinkTimetableLectureDetailUiEvent.NavigateToHome -> onNavigateHome()
                is DeeplinkTimetableLectureDetailUiEvent.ShowToastAndNavigateBack -> {
                    context.toast(event.message)
                    onNavigateBack()
                }

                is DeeplinkTimetableLectureDetailUiEvent.ShowToast -> {
                    context.toast(event.message)
                }

                is DeeplinkTimetableLectureDetailUiEvent.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, event.url.toUri())
                    context.startActivity(intent)
                }

                is DeeplinkTimetableLectureDetailUiEvent.OpenReviewSheet -> {
                    scope.launch { sheetState.show() }
                }

                is DeeplinkTimetableLectureDetailUiEvent.CloseReviewSheet -> {
                    scope.launch { sheetState.hide() }
                }
            }
        }
    }

    // TODO: Screen 분리
    when (val state = uiState) {
        is DeeplinkTimetableLectureDetailUiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SNUTTColors.Gray100),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(color = SNUTTColors.Black900)
            }
        }

        is DeeplinkTimetableLectureDetailUiState.Success -> {
            ModalBottomSheetLayout(
                sheetContent = {
                    ReviewWebViewNew(modifier = Modifier.fillMaxHeight(0.95f), reviewWebViewContainer = reviewWebViewContainer)
                },
                sheetState = sheetState,
                sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
                scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
                sheetGesturesEnabled = false,
            ) {
                LectureDetail(
                    lecture = state.lecture,
                    editMode = false,
                    hideEditButton = true,
                    tableTheme = BuiltInTheme.SNUTT,
                    reviewInfo = state.reviewInfo,
                    buildings = state.buildings,
                    isBookmarked = state.isBookmarked,
                    vacancyRegistered = state.vacancyRegistered,
                    showCategoryPre2025 = state.showCategoryPre2025,
                    disableMapFeature = state.disableMapFeature,
                    showLectureReminderPicker = false,
                    lectureWithReminderOption = LectureWithReminderOption.Default,
                    enableLectureReminderPicker = false,
                    showFloatingButton = true,
                    hideDeleteButton = true,
                    onBackPressed = onNavigateBack,
                    onEditModeToggle = {},
                    onBookmarkToggle = vm::toggleBookmark,
                    onVacancyToggle = vm::toggleVacancy,
                    onCourseTitleChange = {},
                    onInstructorChange = {},
                    onColorClick = {},
                    onReminderOptionChange = {},
                    onCreditChange = {},
                    onDepartmentChange = {},
                    onAcademicYearChange = {},
                    onClassificationChange = {},
                    onCategoryChange = {},
                    onCategoryPre2025Change = {},
                    onRemarkChange = {},
                    onEditTime = { _, _ -> },
                    onLocationChange = { _, _ -> },
                    onDeleteSession = {},
                    onAddSession = {},
                    onSyllabus = vm::openSyllabus,
                    onReview = {
                        val url = state.reviewInfo?.getReviewUrl(context)
                        scope.launch { reviewWebViewContainer.openPage("$url&on_back=close") }
                        vm.openReview()
                    },
                    onDelete = {},
                    onReset = {},
                    onFloatingButtonClick = vm::onFloatingButtonClick,
                )
            }
        }
    }
}
