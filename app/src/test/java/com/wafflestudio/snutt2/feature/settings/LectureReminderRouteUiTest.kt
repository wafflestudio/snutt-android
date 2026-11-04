package com.wafflestudio.snutt2.feature.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelStore
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.LectureTimeMissing
import com.wafflestudio.snutt2.domain.Unknown
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.TimetableLectureReminders
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeSemesterStatusRepository
import com.wafflestudio.snutt2.fake.FakeTableRepository
import com.wafflestudio.snutt2.fixture.TestFixtures
import com.wafflestudio.snutt2.ui.theme.SNUTTTheme
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "ko-rKR-w411dp-h891dp")
class LectureReminderRouteUiTest {
    @get:Rule val composeRule = createComposeRule()

    private fun text(id: Int): String =
        RuntimeEnvironment.getApplication().getString(id)
    private lateinit var tables: FakeTableRepository
    private lateinit var semesters: FakeSemesterStatusRepository
    private lateinit var messages: FakeDisplayMessageResolver
    private lateinit var store: ViewModelStore

    @Before
    fun setup() {
        tables = FakeTableRepository()
        semesters = FakeSemesterStatusRepository()
        messages = FakeDisplayMessageResolver()
        store = ViewModelStore()
    }

    @After
    fun tearDown() { store.clear() }

    private fun showRoute(): LectureReminderViewModel {
        lateinit var vm: LectureReminderViewModel
        composeRule.setContent {
            vm = androidx.compose.runtime.remember {
                LectureReminderViewModel(tables, semesters, messages).also { store.put("reminder", it) }
            }
            SNUTTTheme { LectureReminderRoute(onNavigateBack = {}, viewModel = vm) }
        }
        composeRule.waitForIdle()
        return vm
    }

    // 10분 전 선택을 서버에 저장하고 성공 스낵바를 표시한다
    @Test
    fun savesBeforeAndShowsSnackbar() {
        val current = CourseBook(1, 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "과목 1", LectureReminderOffset.NONE)
        val second = TestFixtures.lectureReminder("lecture-2", "과목 2", LectureReminderOffset.TEN_MINUTES_BEFORE)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, CourseBook(2, 2025))
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = LectureReminderOffset.TEN_MINUTES_BEFORE))
        showRoute()
        composeRule.onAllNodesWithText(text(R.string.settings_lecture_reminder_ten_minutes_before))[0].performClick()
        composeRule.onNodeWithText(text(R.string.settings_lecture_reminder_update_success_ten_minutes_before)).assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(listOf(Triple(primary.id, first.lectureId, LectureReminderOffset.TEN_MINUTES_BEFORE)), tables.updateTimetableLectureReminderCalls)
        }
    }

    // 수업 시작 시 선택을 서버에 저장하고 성공 스낵바를 표시한다
    @Test
    fun savesAtStartAndShowsSnackbar() {
        val current = CourseBook(1, 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "과목 1", LectureReminderOffset.NONE)
        val second = TestFixtures.lectureReminder("lecture-2", "과목 2", LectureReminderOffset.TEN_MINUTES_BEFORE)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, CourseBook(2, 2025))
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = LectureReminderOffset.AT_START_TIME))
        showRoute()
        composeRule.onAllNodesWithText(text(R.string.settings_lecture_reminder_at_start_time))[0].performClick()
        composeRule.onNodeWithText(text(R.string.settings_lecture_reminder_update_success_at_start_time)).assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(listOf(Triple(primary.id, first.lectureId, LectureReminderOffset.AT_START_TIME)), tables.updateTimetableLectureReminderCalls)
        }
    }

    // 10분 후 선택을 서버에 저장하고 성공 스낵바를 표시한다
    @Test
    fun savesAfterAndShowsSnackbar() {
        val current = CourseBook(1, 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "과목 1", LectureReminderOffset.NONE)
        val second = TestFixtures.lectureReminder("lecture-2", "과목 2", LectureReminderOffset.TEN_MINUTES_BEFORE)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, CourseBook(2, 2025))
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = LectureReminderOffset.TEN_MINUTES_AFTER))
        showRoute()
        composeRule.onAllNodesWithText(text(R.string.settings_lecture_reminder_ten_minutes_after))[0].performClick()
        composeRule.onNodeWithText(text(R.string.settings_lecture_reminder_update_success_ten_minutes_after)).assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(listOf(Triple(primary.id, first.lectureId, LectureReminderOffset.TEN_MINUTES_AFTER)), tables.updateTimetableLectureReminderCalls)
        }
    }

    // 저장 실패 시 기존 선택과 목록을 유지하고 오류 토스트를 표시한다
    @Test
    fun saveFailurePreservesSelectionAndList() {
        val current = CourseBook(1, 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "과목 1", LectureReminderOffset.NONE)
        val second = TestFixtures.lectureReminder("lecture-2", "과목 2", LectureReminderOffset.TEN_MINUTES_BEFORE)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, CourseBook(2, 2025))
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        tables.updateTimetableLectureReminderResult = Result.Fail(LectureTimeMissing("", "시간 정보가 없어요"))
        val vm = showRoute()
        composeRule.onAllNodesWithText(text(R.string.settings_lecture_reminder_at_start_time))[0].performClick()
        composeRule.runOnIdle {
            assertEquals(LectureReminderUiState.Loaded(primary.id, listOf(first, second)), vm.uiState.value)
            assertEquals("시간 정보가 없어요", ShadowToast.getTextOfLatestToast())
            }
        composeRule.onNodeWithText("과목 1").assertIsDisplayed()
    }

    // 오류 화면에서 다시 불러오기를 누르면 목록으로 복구한다
    @Test
    fun retryRecoversList() {
        val current = CourseBook(1, 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "과목 1", LectureReminderOffset.NONE)
        val second = TestFixtures.lectureReminder("lecture-2", "과목 2", LectureReminderOffset.TEN_MINUTES_BEFORE)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, CourseBook(2, 2025))
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        tables.getTimetableRemindersResult = Result.Fail(Unknown("", "일시적인 조회 오류"))
        showRoute()
        composeRule.onNodeWithText("일시적인 조회 오류").assertIsDisplayed()
        composeRule.runOnIdle {
            tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        }
        composeRule.onNodeWithText(text(R.string.reviews_error_retry)).performClick()
        composeRule.onNodeWithText("과목 1").assertIsDisplayed()
        composeRule.onNodeWithText("일시적인 조회 오류").assertDoesNotExist()
    }
}
