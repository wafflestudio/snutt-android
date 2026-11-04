package com.wafflestudio.snutt2.feature.lecturedetail.currenttable

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.fixture.TestFixtures
import com.wafflestudio.snutt2.ui.theme.SNUTTTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "ko-rKR-w411dp-h891dp")
class LectureReminderDetailUiTest {
    @get:Rule val composeRule = createComposeRule()

    private fun text(id: Int): String =
        RuntimeEnvironment.getApplication().getString(id)

    private fun showDetail(
        state: CurrentTableLectureDetailUiState,
        onReminderOptionChange: (LectureWithReminderOption) -> Unit = {},
    ) {
        composeRule.setContent {
            SNUTTTheme {
                CurrentTableLectureDetailScreen(
                    uiState = state,
                    onBackPressed = {},
                    onEditModeToggle = {},
                    onBookmarkToggle = {},
                    onVacancyToggle = {},
                    onCourseTitleChange = {},
                    onInstructorChange = {},
                    onColorClick = {},
                    onReminderOptionChange = onReminderOptionChange,
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
    }

    // 시간이 있는 이번 학기 일반 강의에 선택 버튼을 표시한다
    @Test
    fun showsPickerForEligibleLecture() {
        val lecture = TestFixtures.syllabusLecture(id = "lecture-1", courseTitle = "자료구조").copy(
            lectureSessions = listOf(LectureSession.Default),
        )
        val reminder = TestFixtures.lectureReminder(lecture.id, lecture.courseTitle, LectureReminderOffset.NONE)
        val state = CurrentTableLectureDetailUiState(
            lecture = lecture,
            editMode = false,
            showLectureReminderPicker = true,
            lectureWithReminderOption = reminder,
            isReminderLoaded = true,
            isPrimaryTable = true,
            disableMapFeature = true,
        )
        showDetail(state)
        composeRule.onNodeWithText(text(R.string.lecture_detail_lecture_reminder_title)).assertIsDisplayed()
        listOf(text(R.string.settings_lecture_reminder_none), text(R.string.settings_lecture_reminder_ten_minutes_before), text(R.string.settings_lecture_reminder_at_start_time), text(R.string.settings_lecture_reminder_ten_minutes_after)).forEach {
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    // 편집 모드에서는 선택 버튼을 숨긴다
    @Test
    fun hidesPickerWhileEditing() {
        val lecture = TestFixtures.syllabusLecture(id = "lecture-1", courseTitle = "자료구조").copy(
            lectureSessions = listOf(LectureSession.Default),
        )
        val reminder = TestFixtures.lectureReminder(lecture.id, lecture.courseTitle, LectureReminderOffset.NONE)
        val state = CurrentTableLectureDetailUiState(
            lecture = lecture,
            editMode = false,
            showLectureReminderPicker = true,
            lectureWithReminderOption = reminder,
            isReminderLoaded = true,
            isPrimaryTable = true,
            disableMapFeature = true,
        )
        showDetail(state.copy(editMode = true))
        composeRule.onNodeWithText(text(R.string.lecture_detail_lecture_reminder_title)).assertDoesNotExist()
    }

    // 커스텀 강의는 시간이 있어도 선택 버튼을 숨긴다
    @Test
    fun hidesPickerForCustomLecture() {
        val lecture = TestFixtures.syllabusLecture(id = "lecture-1", courseTitle = "자료구조").copy(
            lectureSessions = listOf(LectureSession.Default),
        )
        val reminder = TestFixtures.lectureReminder(lecture.id, lecture.courseTitle, LectureReminderOffset.NONE)
        val state = CurrentTableLectureDetailUiState(
            lecture = lecture,
            editMode = false,
            showLectureReminderPicker = true,
            lectureWithReminderOption = reminder,
            isReminderLoaded = true,
            isPrimaryTable = true,
            disableMapFeature = true,
        )
        val custom = CustomLecture.Empty.copy(
            id = "custom", courseTitle = "나의 수업", lectureSessions = listOf(LectureSession.Default),
        )
        showDetail(state.copy(lecture = custom))
        composeRule.onNodeWithText(text(R.string.lecture_detail_lecture_reminder_title)).assertDoesNotExist()
    }

    // 시간 정보가 없는 일반 강의는 선택 버튼을 숨긴다
    @Test
    fun hidesPickerWithoutTime() {
        val lecture = TestFixtures.syllabusLecture(id = "lecture-1", courseTitle = "자료구조").copy(
            lectureSessions = listOf(LectureSession.Default),
        )
        val reminder = TestFixtures.lectureReminder(lecture.id, lecture.courseTitle, LectureReminderOffset.NONE)
        val state = CurrentTableLectureDetailUiState(
            lecture = lecture,
            editMode = false,
            showLectureReminderPicker = true,
            lectureWithReminderOption = reminder,
            isReminderLoaded = true,
            isPrimaryTable = true,
            disableMapFeature = true,
        )
        showDetail(state.copy(lecture = lecture.copy(lectureSessions = emptyList())))
        composeRule.onNodeWithText(text(R.string.lecture_detail_lecture_reminder_title)).assertDoesNotExist()
    }

    // 이번 학기 표시 조건이 꺼지면 선택 버튼을 숨긴다
    @Test
    fun hidesPickerOutsideActiveSemester() {
        val lecture = TestFixtures.syllabusLecture(id = "lecture-1", courseTitle = "자료구조").copy(
            lectureSessions = listOf(LectureSession.Default),
        )
        val reminder = TestFixtures.lectureReminder(lecture.id, lecture.courseTitle, LectureReminderOffset.NONE)
        val state = CurrentTableLectureDetailUiState(
            lecture = lecture,
            editMode = false,
            showLectureReminderPicker = true,
            lectureWithReminderOption = reminder,
            isReminderLoaded = true,
            isPrimaryTable = true,
            disableMapFeature = true,
        )
        showDetail(state.copy(showLectureReminderPicker = false))
        composeRule.onNodeWithText(text(R.string.lecture_detail_lecture_reminder_title)).assertDoesNotExist()
    }

    // 대표시간표가 아니면 대표시간표 안내를 표시한다
    @Test
    fun showsGuideForNonPrimaryTable() {
        val lecture = TestFixtures.syllabusLecture(id = "lecture-1", courseTitle = "자료구조").copy(
            lectureSessions = listOf(LectureSession.Default),
        )
        val reminder = TestFixtures.lectureReminder(lecture.id, lecture.courseTitle, LectureReminderOffset.NONE)
        val state = CurrentTableLectureDetailUiState(
            lecture = lecture,
            editMode = false,
            showLectureReminderPicker = true,
            lectureWithReminderOption = reminder,
            isReminderLoaded = true,
            isPrimaryTable = true,
            disableMapFeature = true,
        )
        showDetail(state.copy(isPrimaryTable = false))
        composeRule.onNodeWithText(text(R.string.lecture_detail_lecture_reminder_guide_bold1), substring = true).assertIsDisplayed()
    }

    // 대표시간표에는 비대표시간표 안내를 표시하지 않는다
    @Test
    fun hidesGuideForPrimaryTable() {
        val lecture = TestFixtures.syllabusLecture(id = "lecture-1", courseTitle = "자료구조").copy(
            lectureSessions = listOf(LectureSession.Default),
        )
        val reminder = TestFixtures.lectureReminder(lecture.id, lecture.courseTitle, LectureReminderOffset.NONE)
        val state = CurrentTableLectureDetailUiState(
            lecture = lecture,
            editMode = false,
            showLectureReminderPicker = true,
            lectureWithReminderOption = reminder,
            isReminderLoaded = true,
            isPrimaryTable = true,
            disableMapFeature = true,
        )
        showDetail(state)
        composeRule.onNodeWithText(text(R.string.lecture_detail_lecture_reminder_guide_bold1), substring = true).assertDoesNotExist()
    }

    // 상세 화면의 선택은 해당 강의와 새 옵션을 전달한다
    @Test
    fun pickerPassesLectureAndOption() {
        val lecture = TestFixtures.syllabusLecture(id = "lecture-1", courseTitle = "자료구조").copy(
            lectureSessions = listOf(LectureSession.Default),
        )
        val reminder = TestFixtures.lectureReminder(lecture.id, lecture.courseTitle, LectureReminderOffset.NONE)
        val state = CurrentTableLectureDetailUiState(
            lecture = lecture,
            editMode = false,
            showLectureReminderPicker = true,
            lectureWithReminderOption = reminder,
            isReminderLoaded = true,
            isPrimaryTable = true,
            disableMapFeature = true,
        )
        var selected: LectureWithReminderOption? = null
        showDetail(state, onReminderOptionChange = { selected = it })
        composeRule.onNodeWithText(text(R.string.settings_lecture_reminder_ten_minutes_after)).performClick()
        composeRule.runOnIdle {
            assertEquals(reminder.copy(lectureReminderOffset = LectureReminderOffset.TEN_MINUTES_AFTER), selected)
        }
    }
}
