package com.wafflestudio.snutt2.views.logged_in.home.bookmark

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import com.wafflestudio.snutt2.components.compose.ModalBottomSheetPlaceholder
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.refactor.LectureDetail

@Composable
fun BookmarkBottomSheetLayout(
    uiState: BookmarkUiState,
    sheetState: ModalBottomSheetState,
    detailReviewSheetState: ModalBottomSheetState,
    detailReviewWebViewContainer: ReviewWebViewContainer,
    reviewWebViewContainer: ReviewWebViewContainer,
    onDismiss: () -> Unit,
    onBookmarkToggle: (lecture: SearchedLecture) -> Unit,
    onVacancyToggle: (lecture: SearchedLecture) -> Unit,
    onSyllabus: (SearchedLecture) -> Unit,
    onReviewFromDetail: () -> Unit,
    onCloseDetailReview: () -> Unit,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            onDismiss()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            val successState = uiState as? BookmarkUiState.Success
            when (val bottomSheetType = successState?.bottomSheetType) {
                is BookmarkUiState.BottomSheetType.LectureDetail -> {
                    BookmarkLectureDetailSheetContent(
                        bottomSheetType = bottomSheetType,
                        tableTheme = successState.tableTheme,
                        courseBook = successState.currentTable.summary.courseBook,
                        disableMapFeature = successState.disableMapFeature,
                        detailReviewSheetState = detailReviewSheetState,
                        detailReviewWebViewContainer = detailReviewWebViewContainer,
                        onDismiss = onDismiss,
                        onBookmarkToggle = onBookmarkToggle,
                        onVacancyToggle = onVacancyToggle,
                        onSyllabus = onSyllabus,
                        onReviewFromDetail = onReviewFromDetail,
                        onCloseDetailReview = onCloseDetailReview,
                    )
                }

                is BookmarkUiState.BottomSheetType.Review -> {
                    CompositionLocalProvider(LocalReviewWebView provides reviewWebViewContainer) {
                        ReviewWebView(height = 0.95f)
                    }
                }

                else -> {
                    ModalBottomSheetPlaceholder()
                }
            }
        },
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
        scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
        sheetGesturesEnabled = false,
    ) {
        content()
    }
}

@Composable
private fun BookmarkLectureDetailSheetContent(
    bottomSheetType: BookmarkUiState.BottomSheetType.LectureDetail,
    tableTheme: TableTheme,
    courseBook: CourseBook,
    disableMapFeature: Boolean,
    detailReviewSheetState: ModalBottomSheetState,
    detailReviewWebViewContainer: ReviewWebViewContainer,
    onDismiss: () -> Unit,
    onBookmarkToggle: (lecture: SearchedLecture) -> Unit,
    onVacancyToggle: (lecture: SearchedLecture) -> Unit,
    onSyllabus: (SearchedLecture) -> Unit,
    onReviewFromDetail: () -> Unit,
    onCloseDetailReview: () -> Unit,
) {
    val lecture = bottomSheetType.lecture
    val showCategoryPre2025 = (courseBook.year * 10 + courseBook.semester) > 20250L

    LaunchedEffect(detailReviewSheetState.currentValue) {
        if (detailReviewSheetState.currentValue == ModalBottomSheetValue.Hidden) {
            onCloseDetailReview()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            CompositionLocalProvider(LocalReviewWebView provides detailReviewWebViewContainer) {
                ReviewWebView(height = 0.95f)
            }
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
            isBookmarked = bottomSheetType.isBookmarked,
            vacancyRegistered = bottomSheetType.isVacancyRegistered,
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
            onBookmarkToggle = { onBookmarkToggle(lecture) },
            onVacancyToggle = { onVacancyToggle(lecture) },
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
