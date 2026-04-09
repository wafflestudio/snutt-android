package com.wafflestudio.snutt2.feature.lecture_detail.deeplink

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.domain.model.SyllabusLecture
import com.wafflestudio.snutt2.ui.util.toast
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.logging.LectureDetailParameter
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.feature.lecture_detail.LectureDetail

@Composable
fun DeeplinkTimetableLectureDetailRoute(
    vm: DeeplinkTimetableLectureDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToReview: (reviewId: String, lectureId: String) -> Unit,
) {
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsStateWithLifecycle()

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
            val loggingLectureId = (state.lecture as? SyllabusLecture)?.originalLectureId ?: state.lecture.id
            Box(
                modifier = Modifier.logImpression(
                    AnalyticsScreen.LectureDetail(
                        LectureDetailParameter(
                            lectureId = loggingLectureId,
                            referrer = DetailScreenReferrer.Notification,
                        ),
                    ),
                ),
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
                        state.reviewInfo?.id?.let { reviewId ->
                            onNavigateToReview(reviewId, loggingLectureId)
                        }
                    },
                    onDelete = {},
                    onReset = {},
                    onFloatingButtonClick = vm::onFloatingButtonClick,
                )
            }
        }
    }
}
