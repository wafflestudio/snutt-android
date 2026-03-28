package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.ReviewDetailParameter
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetail

@Composable
fun SearchLectureDetailSheetContent(
    bottomSheetType: SearchUiState.BottomSheetType.LectureDetail,
    bookmarks: List<SearchedLecture>,
    vacancyList: List<SearchedLecture>,
    tableTheme: TableTheme,
    courseBook: CourseBook,
    disableMapFeature: Boolean,
    detailReviewSheetState: ModalBottomSheetState,
    detailReviewWebViewContainer: ReviewWebViewContainer,
    onDismiss: () -> Unit,
    onBookmarkToggle: (lecture: SearchedLecture, isBookmarked: Boolean) -> Unit,
    onVacancyToggle: (lecture: SearchedLecture, isVacancyRegistered: Boolean) -> Unit,
    onSyllabus: (SearchedLecture) -> Unit,
    onReviewFromDetail: () -> Unit,
    onCloseDetailReview: () -> Unit,
) {
    val analyticsLogger = LocalAnalyticsLogger.current
    val lecture = bottomSheetType.lecture
    val isBookmarked = bookmarks.any { it.id == lecture.id }
    val isVacancyRegistered = vacancyList.any { it.id == lecture.id }
    val showCategoryPre2025 = (courseBook.year * 10 + courseBook.semester) > 20250L

    LaunchedEffect(detailReviewSheetState.currentValue) {
        if (detailReviewSheetState.currentValue == ModalBottomSheetValue.Hidden) {
            onCloseDetailReview()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            LaunchedEffect(detailReviewSheetState.isVisible) {
                if (detailReviewSheetState.isVisible) {
                    analyticsLogger.logScreen(
                        AnalyticsScreen.ReviewDetail(
                            ReviewDetailParameter(
                                lectureId = lecture.id,
                                referrer = bottomSheetType.referrer,
                            ),
                        ),
                    )
                }
            }
            ReviewWebView(modifier = Modifier.fillMaxHeight(0.95f), reviewWebViewContainer = detailReviewWebViewContainer)
        },
        sheetState = detailReviewSheetState,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
        scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
        sheetGesturesEnabled = false,
    ) {
        LectureDetail(
            lecture = lecture,
            editMode = false,
            tableTheme = tableTheme,
            reviewInfo = lecture.reviewInfo,
            buildings = bottomSheetType.buildings,
            isBookmarked = isBookmarked,
            vacancyRegistered = isVacancyRegistered,
            showCategoryPre2025 = showCategoryPre2025,
            disableMapFeature = disableMapFeature,
            showLectureReminderPicker = false,
            lectureWithReminderOption = LectureWithReminderOption.Default,
            enableLectureReminderPicker = false,
            showFloatingButton = false,
            onBackPressed = {
                if (bottomSheetType.reviewVisible) onCloseDetailReview() else onDismiss()
            },
            onEditModeToggle = {},
            onBookmarkToggle = { onBookmarkToggle(lecture, isBookmarked) },
            onVacancyToggle = { onVacancyToggle(lecture, isVacancyRegistered) },
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
            onSyllabus = { onSyllabus(lecture) },
            onReview = onReviewFromDetail,
            onDelete = {},
            onReset = {},
            onFloatingButtonClick = {},
        )
    }
}
