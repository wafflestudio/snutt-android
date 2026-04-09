package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.LectureDetailParameter
import com.wafflestudio.snutt2.logging.logImpression
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetail

@Composable
fun SearchLectureDetailSheetContent(
    bottomSheetType: SearchUiState.BottomSheetType.LectureDetail,
    bookmarks: List<SearchedLecture>,
    vacancyList: List<SearchedLecture>,
    tableTheme: TableTheme,
    courseBook: CourseBook,
    disableMapFeature: Boolean,
    onDismiss: () -> Unit,
    onBookmarkToggle: (lecture: SearchedLecture, isBookmarked: Boolean) -> Unit,
    onVacancyToggle: (lecture: SearchedLecture, isVacancyRegistered: Boolean) -> Unit,
    onSyllabus: (SearchedLecture) -> Unit,
    onReview: (SearchedLecture) -> Unit,
) {
    val lecture = bottomSheetType.lecture
    val isBookmarked = bookmarks.any { it.id == lecture.id }
    val isVacancyRegistered = vacancyList.any { it.id == lecture.id }
    val showCategoryPre2025 = (courseBook.year * 10 + courseBook.semester) > 20250L

    Box(
        modifier = Modifier.logImpression(
            AnalyticsScreen.LectureDetail(
                LectureDetailParameter(
                    lectureId = lecture.id,
                    referrer = bottomSheetType.referrer,
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
            isBookmarked = isBookmarked,
            vacancyRegistered = isVacancyRegistered,
            showCategoryPre2025 = showCategoryPre2025,
            disableMapFeature = disableMapFeature,
            showLectureReminderPicker = false,
            lectureWithReminderOption = LectureWithReminderOption.Default,
            enableLectureReminderPicker = false,
            showFloatingButton = false,
            onBackPressed = onDismiss,
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
            onReview = { onReview(lecture) },
            onDelete = {},
            onReset = {},
            onFloatingButtonClick = {},
        )
    }
}
