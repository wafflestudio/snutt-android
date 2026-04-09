package com.wafflestudio.snutt2.views.logged_in.lecture_detail.current_table

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetail

@Composable
fun AddCustomLectureScreen(
    uiState: AddCustomLectureUiState,
    onBackPressed: () -> Unit,
    onSave: () -> Unit,
    onColorClick: () -> Unit,
    onCreditChange: (Long) -> Unit,
    onCourseTitleChange: (String) -> Unit,
    onInstructorChange: (String) -> Unit,
    onRemarkChange: (String) -> Unit,
    onEditTime: (index: Int, session: LectureSession) -> Unit,
    onLocationChange: (index: Int, location: String) -> Unit,
    onDeleteSession: (index: Int) -> Unit,
    onAddSession: () -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDeleteSession: () -> Unit,
    onConfirmForceCreate: () -> Unit,
) {
    AddCustomLectureDialogs(
        uiState = uiState,
        onDismiss = onDismissDialog,
        onConfirmDeleteSession = onConfirmDeleteSession,
        onConfirmForceCreate = onConfirmForceCreate,
    )

    LectureDetail(
        lecture = uiState.lecture,
        editMode = true,
        hideEditButton = false,
        tableTheme = uiState.tableTheme,
        reviewInfo = null,
        buildings = emptyList(),
        isBookmarked = false,
        vacancyRegistered = false,
        showCategoryPre2025 = false,
        disableMapFeature = uiState.disableMapFeature,
        showLectureReminderPicker = false,
        lectureWithReminderOption = LectureWithReminderOption.Default,
        enableLectureReminderPicker = false,
        showFloatingButton = false,
        hideDeleteButton = true,
        onBackPressed = onBackPressed,
        onEditModeToggle = onSave,
        onBookmarkToggle = {},
        onVacancyToggle = {},
        onCourseTitleChange = onCourseTitleChange,
        onInstructorChange = onInstructorChange,
        onColorClick = onColorClick,
        onReminderOptionChange = {},
        onCreditChange = onCreditChange,
        onDepartmentChange = {},
        onAcademicYearChange = {},
        onClassificationChange = {},
        onCategoryChange = {},
        onCategoryPre2025Change = {},
        onRemarkChange = onRemarkChange,
        onEditTime = onEditTime,
        onLocationChange = onLocationChange,
        onDeleteSession = onDeleteSession,
        onAddSession = onAddSession,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
        onFloatingButtonClick = {},
    )
}

@Composable
private fun AddCustomLectureDialogs(
    uiState: AddCustomLectureUiState,
    onDismiss: () -> Unit,
    onConfirmDeleteSession: () -> Unit,
    onConfirmForceCreate: () -> Unit,
) {
    when (val dialogState = uiState.dialogState) {
        AddCustomLectureUiState.DialogState.None -> {}

        is AddCustomLectureUiState.DialogState.DeleteSession -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = onConfirmDeleteSession,
                title = stringResource(R.string.lecture_detail_delete_class_time_message),
            ) {}
        }

        is AddCustomLectureUiState.DialogState.LectureTimeOverlap -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = onConfirmForceCreate,
                title = stringResource(R.string.lecture_overlap_error_message),
            ) {
                Text(
                    text = dialogState.message,
                    style = SNUTTTypography.body1,
                )
            }
        }
    }
}
