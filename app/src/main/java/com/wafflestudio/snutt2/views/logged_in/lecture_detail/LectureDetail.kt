package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.components.compose.embed_map.FoldableEmbedMap
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.LectureReviewInfo
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LectureSyllabusInfo
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.domain.model.Building
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
fun LectureDetail(
    lecture: Lecture,
    editMode: Boolean,
    hideEditButton: Boolean = false,
    tableTheme: TableTheme,
    reviewInfo: LectureReviewInfo?,
    buildings: List<Building>,
    isBookmarked: Boolean,
    vacancyRegistered: Boolean,
    showCategoryPre2025: Boolean,
    disableMapFeature: Boolean,
    showLectureReminderPicker: Boolean,
    lectureWithReminderOption: LectureWithReminderOption,
    enableLectureReminderPicker: Boolean,
    showFloatingButton: Boolean,
    hideDeleteButton: Boolean = false,

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
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SNUTTColors.Gray100),
        ) {
            LectureDetailTopBar(
                lecture = lecture,
                editMode = editMode,
                hideEditButton = hideEditButton,
                isBookmarked = isBookmarked,
                isVacancyRegistered = vacancyRegistered,
                onBackPressed = onBackPressed,
                onEditModeToggle = {
                    focusManager.clearFocus()
                    onEditModeToggle()
                },
                onBookmarkToggle = onBookmarkToggle,
                onVacancyToggle = onVacancyToggle,
            )

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // 1. 강의명, 교수, 색상
                LectureHeaderFields(
                    lecture = lecture,
                    editMode = editMode,
                    tableTheme = tableTheme,
                    onCourseTitleChange = onCourseTitleChange,
                    onInstructorChange = onInstructorChange,
                    onColorClick = onColorClick,
                )
                Spacer(modifier = Modifier.height(10.dp))

                // 2. 강의 리마인더
                AnimatedVisibility(visible = showLectureReminderPicker && !editMode) {
                    Column {
                        LectureReminderSection(
                            lectureWithReminderOption = lectureWithReminderOption,
                            enableLectureReminderPicker = enableLectureReminderPicker,
                            onReminderOptionChange = onReminderOptionChange,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // 3. 강의 평점
                AnimatedVisibility(visible = lecture is LectureSyllabusInfo && !editMode) {
                    Column {
                        LectureReviewRatingField(reviewInfo = reviewInfo)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // 4. 상세 정보: 학과, 학점, 분류, 비고 등
                LectureDetailInfoFields(
                    lecture = lecture,
                    editMode = editMode,
                    showCategoryPre2025 = showCategoryPre2025,
                    onDepartmentChange = onDepartmentChange,
                    onAcademicYearChange = onAcademicYearChange,
                    onCreditChange = onCreditChange,
                    onClassificationChange = onClassificationChange,
                    onCategoryChange = onCategoryChange,
                    onCategoryPre2025Change = onCategoryPre2025Change,
                    onRemarkChange = onRemarkChange,
                )
                Spacer(modifier = Modifier.height(10.dp))

                // 5. 시간/장소 + 지도
                LectureSessionListSection(
                    sessions = lecture.lectureSessions,
                    editMode = editMode,
                    onEditTime = onEditTime,
                    onLocationChange = onLocationChange,
                    onDeleteSession = onDeleteSession,
                    onAddSession = onAddSession,
                )
                AnimatedVisibility(visible = editMode.not() && disableMapFeature.not()) {
                    FoldableEmbedMap(
                        modifier = Modifier
                            .background(SNUTTColors.White900)
                            .padding(vertical = 8.dp),
                        buildings = buildings,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                // 6. 버튼 그룹: 강의계획서, 강의평, 삭제, 초기화
                LectureActionButtons(
                    lecture = lecture,
                    editMode = editMode,
                    hideDeleteButton = hideDeleteButton,
                    onSyllabus = onSyllabus,
                    onReview = onReview,
                    onDelete = onDelete,
                    onReset = onReset,
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        if (showFloatingButton) {
            FloatingButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = onFloatingButtonClick,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1400)
@Composable
private fun LectureDetailPreview() {
    LectureDetail(
        lecture = PreviewData.syllabusLecture,
        editMode = false,
        tableTheme = BuiltInTheme.SNUTT,
        reviewInfo = PreviewData.sampleReviewInfo,
        buildings = emptyList(),
        isBookmarked = true,
        vacancyRegistered = false,
        showCategoryPre2025 = true,
        disableMapFeature = true,
        showLectureReminderPicker = true,
        lectureWithReminderOption = PreviewData.sampleReminderOption,
        enableLectureReminderPicker = true,
        showFloatingButton = true,
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
    )
}
