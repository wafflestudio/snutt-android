package com.wafflestudio.snutt2.views.logged_in.home.bookmark

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.components.compose.ModalBottomSheetPlaceholder
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.logging.LectureDetailParameter
import com.wafflestudio.snutt2.logging.logImpression
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetail
import androidx.compose.foundation.layout.Box

@Composable
fun BookmarkBottomSheetLayout(
    uiState: BookmarkUiState,
    sheetState: ModalBottomSheetState,
    onDismiss: () -> Unit,
    onBookmarkToggle: (lecture: SearchedLecture) -> Unit,
    onVacancyToggle: (lecture: SearchedLecture) -> Unit,
    onSyllabus: (SearchedLecture) -> Unit,
    onReview: (SearchedLecture) -> Unit,
    content: @Composable () -> Unit,
) {
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
                        onDismiss = onDismiss,
                        onBookmarkToggle = onBookmarkToggle,
                        onVacancyToggle = onVacancyToggle,
                        onSyllabus = onSyllabus,
                        onReview = onReview,
                    )
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
    onDismiss: () -> Unit,
    onBookmarkToggle: (lecture: SearchedLecture) -> Unit,
    onVacancyToggle: (lecture: SearchedLecture) -> Unit,
    onSyllabus: (SearchedLecture) -> Unit,
    onReview: (SearchedLecture) -> Unit,
) {
    val lecture = bottomSheetType.lecture
    val showCategoryPre2025 = (courseBook.year * 10 + courseBook.semester) > 20250L

    Box(
        modifier = Modifier.logImpression(
            AnalyticsScreen.LectureDetail(
                LectureDetailParameter(
                    lectureId = lecture.id,
                    referrer = DetailScreenReferrer.Bookmark,
                ),
            ),
        ),
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
        onBackPressed = onDismiss,
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
        onReview = { onReview(lecture) },
        onDelete = {},
        onReset = {},
        onFloatingButtonClick = {},
    )
    }
}
