package com.wafflestudio.snutt2.feature.lecturedetail.currenttable

import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.config.FeatureFlag
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.ui.preview.PreviewData
import com.wafflestudio.snutt2.feature.lecturedetail.LectureDetail
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface

@Composable
fun CurrentTableLectureDetailScreen(
    uiState: CurrentTableLectureDetailUiState,
    onBackPressed: () -> Unit,
    onEditModeToggle: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onVacancyToggle: () -> Unit,
    onCourseTitleChange: (String) -> Unit,
    onInstructorChange: (String) -> Unit,
    onColorClick: () -> Unit,
    onReminderOptionChange: (LectureWithReminderOption) -> Unit,
    onCreditChange: (Long) -> Unit,
    onDepartmentChange: (String) -> Unit,
    onAcademicYearChange: (String) -> Unit,
    onClassificationChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onCategoryPre2025Change: (String) -> Unit,
    onRemarkChange: (String) -> Unit,
    onEditTime: (index: Int, session: LectureSession) -> Unit,
    onLocationChange: (index: Int, location: String) -> Unit,
    onDeleteSession: (index: Int) -> Unit,
    onAddSession: () -> Unit,
    onSyllabus: () -> Unit,
    onReview: () -> Unit,
    onDelete: () -> Unit,
    onReset: () -> Unit,
    onFloatingButtonClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmExitEditMode: () -> Unit,
    onConfirmDeleteSession: () -> Unit,
    onConfirmDeleteLecture: () -> Unit,
    onConfirmResetLecture: () -> Unit,
    onConfirmForceUpdate: () -> Unit,
) {
    CurrentTableLectureDetailDialogs(
        uiState = uiState,
        onDismiss = onDismissDialog,
        onConfirmExitEditMode = onConfirmExitEditMode,
        onConfirmDeleteSession = onConfirmDeleteSession,
        onConfirmDeleteLecture = onConfirmDeleteLecture,
        onConfirmResetLecture = onConfirmResetLecture,
        onConfirmForceUpdate = onConfirmForceUpdate,
    )

    LectureDetail(
        lecture = uiState.lecture,
        editMode = uiState.editMode,
        tableTheme = uiState.tableTheme,
        reviewInfo = uiState.reviewInfo,
        buildings = uiState.buildings,
        isBookmarked = uiState.isBookmarked,
        vacancyRegistered = uiState.vacancyRegistered,
        showCategoryPre2025 = uiState.showCategoryPre2025,
        disableMapFeature = uiState.disableMapFeature,
        showLectureReminderPicker = uiState.showLectureReminderPicker && FeatureFlag.LECTURE_REMINDER.isEnabled,
        lectureWithReminderOption = uiState.lectureWithReminderOption,
        enableLectureReminderPicker = uiState.enableLectureReminderPicker,
        showFloatingButton = false,
        onBackPressed = onBackPressed,
        onEditModeToggle = onEditModeToggle,
        onBookmarkToggle = onBookmarkToggle,
        onVacancyToggle = onVacancyToggle,
        onCourseTitleChange = onCourseTitleChange,
        onInstructorChange = onInstructorChange,
        onColorClick = onColorClick,
        onReminderOptionChange = onReminderOptionChange,
        onCreditChange = onCreditChange,
        onDepartmentChange = onDepartmentChange,
        onAcademicYearChange = onAcademicYearChange,
        onClassificationChange = onClassificationChange,
        onCategoryChange = onCategoryChange,
        onCategoryPre2025Change = onCategoryPre2025Change,
        onRemarkChange = onRemarkChange,
        onEditTime = onEditTime,
        onLocationChange = onLocationChange,
        onDeleteSession = onDeleteSession,
        onAddSession = onAddSession,
        onSyllabus = onSyllabus,
        onReview = onReview,
        onDelete = onDelete,
        onReset = onReset,
        onFloatingButtonClick = onFloatingButtonClick,
    )
}

@SnuttPreview
@Composable
private fun CurrentTableLectureDetailScreen_ViewMode() {
    SnuttPreviewSurface {
        CurrentTableLectureDetailScreen(
            uiState = CurrentTableLectureDetailUiState(
                lecture = PreviewData.syllabusLecture,
                editMode = false,
                reviewInfo = PreviewData.sampleReviewInfo,
                isBookmarked = true,
                showCategoryPre2025 = true,
                disableMapFeature = true,
            ),
            onBackPressed = {},
            onEditModeToggle = {},
            onBookmarkToggle = {},
            onVacancyToggle = {},
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
            onSyllabus = {},
            onReview = {},
            onDelete = {},
            onReset = {},
            onFloatingButtonClick = {},
            onDismissDialog = {},
            onConfirmExitEditMode = {},
            onConfirmDeleteSession = {},
            onConfirmDeleteLecture = {},
            onConfirmResetLecture = {},
            onConfirmForceUpdate = {},
        )
    }
}
