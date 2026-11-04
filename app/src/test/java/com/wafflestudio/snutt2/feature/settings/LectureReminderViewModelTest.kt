package com.wafflestudio.snutt2.feature.settings

import androidx.lifecycle.ViewModelStore
import app.cash.turbine.test
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.LectureTimeMissing
import com.wafflestudio.snutt2.domain.Unknown
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset.*
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.domain.model.TimetableLectureReminders
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeSemesterStatusRepository
import com.wafflestudio.snutt2.fake.FakeTableRepository
import com.wafflestudio.snutt2.fixture.TestFixtures
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LectureReminderViewModelTest {
    private lateinit var tables: FakeTableRepository
    private lateinit var semesters: FakeSemesterStatusRepository
    private lateinit var messages: FakeDisplayMessageResolver
    private lateinit var store: ViewModelStore

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        tables = FakeTableRepository()
        semesters = FakeSemesterStatusRepository()
        messages = FakeDisplayMessageResolver()
        store = ViewModelStore()
    }

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    private fun createViewModel() = LectureReminderViewModel(tables, semesters, messages).also {
        store.put("reminder", it)
    }

    // region init

    @Test
    fun `초기 조회 중에는 Loading을 유지한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.getTimetableRemindersHandler = { CompletableDeferred<Result<TimetableLectureReminders>>().await() }
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Loading, vm.uiState.value)
    }

    @Test
    fun `시간표 목록 fetch가 끝나기 전에는 캐시로 목록을 표시하지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.fetchTableListDeferred = CompletableDeferred()
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Loading, vm.uiState.value)
    }

    @Test
    fun `학기 정보 fetch가 끝나기 전에는 캐시로 목록을 표시하지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        semesters.fetchSemesterStatusDeferred = CompletableDeferred()
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Loading, vm.uiState.value)
    }

    @Test
    fun `학기 정보가 null이면 유효한 첫 값을 기다린다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        semesters.semesterStatus.value = null
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Loading, vm.uiState.value)
    }

    @Test
    fun `이번 학기 대표시간표의 강의와 설정으로 Loaded를 구성한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.tableSummaryList.value = listOf(
            primary.copy(id = "past", courseBook = CourseBook(2, 2024)),
            primary.copy(id = "ordinary", isPrimary = false),
            primary,
        )
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Loaded(primary.id, listOf(first, second)), vm.uiState.value)
    }

    @Test
    fun `선택된 시간표와 무관하게 이번 학기 대표시간표 ID로 조회한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.currentTable.value = TestFixtures.table(summary = primary.copy(id = "selected", isPrimary = false))
        val vm = createViewModel()
        runCurrent()
        assertEquals(listOf(primary.id), tables.getTimetableRemindersCalls)
    }

    @Test
    fun `진입 시 시간표 목록을 갱신한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        assertEquals(1, tables.fetchTableListCallCount)
    }

    @Test
    fun `진입 시 학기 정보를 갱신한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        assertEquals(1, semesters.fetchSemesterStatusCallCount)
    }

    @Test
    fun `대표시간표가 없으면 Empty를 표시한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.tableSummaryList.value = listOf(primary.copy(isPrimary = false))
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Empty, vm.uiState.value)
    }

    @Test
    fun `과거 학기 대표시간표만 있으면 Empty를 표시한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.tableSummaryList.value = listOf(primary.copy(courseBook = CourseBook(2, 2024)))
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Empty, vm.uiState.value)
    }

    @Test
    fun `등록된 강의가 없으면 Empty를 표시한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, emptyList()))
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Empty, vm.uiState.value)
    }

    @Test
    fun `대표시간표가 없으면 리마인더 API를 호출하지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.tableSummaryList.value = emptyList()
        val vm = createViewModel()
        runCurrent()
        assertEquals(emptyList<String>(), tables.getTimetableRemindersCalls)
    }

    @Test
    fun `현재 학기가 없는 기간에는 다음 학기의 대표시간표를 선택한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        semesters.semesterStatus.value = TestFixtures.semesterStatus(null, current)
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Loaded(primary.id, listOf(first, second)), vm.uiState.value)
    }

    @Test
    fun `시간표 목록 조회 실패 시 서버 메시지로 Error를 표시한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.fetchTableListResult = Result.Fail(Unknown("", "조회 실패"))
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Error("조회 실패"), vm.uiState.value)
    }

    @Test
    fun `학기 정보 조회 실패 시 서버 메시지로 Error를 표시한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        semesters.fetchSemesterStatusResult = Result.Fail(Unknown("", "조회 실패"))
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Error("조회 실패"), vm.uiState.value)
    }

    @Test
    fun `리마인더 조회 실패 시 서버 메시지로 Error를 표시한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.getTimetableRemindersResult = Result.Fail(Unknown("", "조회 실패"))
        val vm = createViewModel()
        runCurrent()
        assertEquals(LectureReminderUiState.Error("조회 실패"), vm.uiState.value)
    }

    // endregion

    // region source: tableSummaryList

    @Test
    fun `대표시간표가 바뀌면 새 시간표의 목록으로 교체한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        val replacement = primary.copy(id = "replacement")
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(replacement.id, listOf(second)))
        tables.tableSummaryList.value = listOf(replacement)
        runCurrent()
        assertEquals(LectureReminderUiState.Loaded(replacement.id, listOf(second)), vm.uiState.value)
    }

    @Test
    fun `대표시간표 해제 시 Empty로 전환한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        tables.tableSummaryList.value = listOf(primary.copy(isPrimary = false))
        runCurrent()
        assertEquals(LectureReminderUiState.Empty, vm.uiState.value)
    }

    @Test
    fun `대표시간표 재지정 시 서버의 기존 설정을 다시 표시한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        tables.tableSummaryList.value = listOf(primary.copy(isPrimary = false))
        runCurrent()
        tables.tableSummaryList.value = listOf(primary)
        runCurrent()
        assertEquals(LectureReminderUiState.Loaded(primary.id, listOf(first, second)), vm.uiState.value)
    }

    @Test
    fun `대표시간표 ID가 같으면 제목 변경으로 중복 조회하지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        tables.tableSummaryList.value = listOf(primary.copy(title = "새 제목"))
        runCurrent()
        assertEquals(listOf(primary.id), tables.getTimetableRemindersCalls)
    }

    // endregion

    // region source: semesterStatus

    @Test
    fun `현재 학기가 바뀌면 새 학기 대표시간표를 조회한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        val future = primary.copy(id = "future", courseBook = next)
        tables.tableSummaryList.value = listOf(primary, future)
        runCurrent()
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(future.id, listOf(second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(next, CourseBook(1, 2026))
        runCurrent()
        assertEquals(LectureReminderUiState.Loaded(future.id, listOf(second)), vm.uiState.value)
    }

    @Test
    fun `현재 학기에 대표시간표가 없으면 Empty로 바뀐다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        semesters.semesterStatus.value = TestFixtures.semesterStatus(next, CourseBook(1, 2026))
        runCurrent()
        assertEquals(LectureReminderUiState.Empty, vm.uiState.value)
    }

    @Test
    fun `학기 상태가 null로 바뀌면 기존 콘텐츠를 유지한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        val before = vm.uiState.value
        semesters.semesterStatus.value = null
        runCurrent()
        assertEquals(before, vm.uiState.value)
    }

    @Test
    fun `학기 정보가 나중에 도착하면 대기 중이던 목록을 표시한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        semesters.semesterStatus.value = null
        val vm = createViewModel()
        runCurrent()
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        runCurrent()
        assertEquals(LectureReminderUiState.Loaded(primary.id, listOf(first, second)), vm.uiState.value)
    }

    // endregion

    // region retry

    @Test
    fun `재시도는 실패 상태에서 목록을 다시 로드한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.getTimetableRemindersResult = Result.Fail(Unknown("", "일시 오류"))
        val vm = createViewModel()
        runCurrent()
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        vm.retry()
        runCurrent()
        assertEquals(LectureReminderUiState.Loaded(primary.id, listOf(first, second)), vm.uiState.value)
    }

    @Test
    fun `재시도 시 시간표 목록을 다시 요청한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        vm.retry()
        runCurrent()
        assertEquals(2, tables.fetchTableListCallCount)
    }

    @Test
    fun `재시도 시 학기 상태를 다시 요청한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        vm.retry()
        runCurrent()
        assertEquals(2, semesters.fetchSemesterStatusCallCount)
    }

    @Test
    fun `재시도 요청 중에는 이전 목록 대신 Loading을 표시한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        val vm = createViewModel()
        runCurrent()
        tables.getTimetableRemindersHandler = { CompletableDeferred<Result<TimetableLectureReminders>>().await() }
        vm.retry()
        runCurrent()
        assertEquals(LectureReminderUiState.Loading, vm.uiState.value)
    }

    // endregion

    // region onSelectOffset

    @Test
    fun `로딩 상태에서는 저장 요청을 보내지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.getTimetableRemindersHandler = { CompletableDeferred<Result<TimetableLectureReminders>>().await() }
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        runCurrent()
        assertEquals(emptyList<Triple<String, String, com.wafflestudio.snutt2.domain.model.LectureReminderOffset>>(), tables.updateTimetableLectureReminderCalls)
    }

    @Test
    fun `빈 목록 상태에서는 저장 요청을 보내지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, emptyList()))
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        runCurrent()
        assertEquals(emptyList<Triple<String, String, com.wafflestudio.snutt2.domain.model.LectureReminderOffset>>(), tables.updateTimetableLectureReminderCalls)
    }

    @Test
    fun `조회 실패 상태에서는 저장 요청을 보내지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.getTimetableRemindersResult = Result.Fail(Unknown("", "조회 실패"))
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        runCurrent()
        assertEquals(emptyList<Triple<String, String, com.wafflestudio.snutt2.domain.model.LectureReminderOffset>>(), tables.updateTimetableLectureReminderCalls)
    }

    @Test
    fun `알 수 없는 강의는 저장하지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset("deleted", AT_START_TIME)
        runCurrent()
        assertEquals(emptyList<Triple<String, String, com.wafflestudio.snutt2.domain.model.LectureReminderOffset>>(), tables.updateTimetableLectureReminderCalls)
    }

    @Test
    fun `이미 선택된 옵션은 저장하지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset(first.lectureId, TEN_MINUTES_BEFORE)
        runCurrent()
        assertEquals(emptyList<Triple<String, String, com.wafflestudio.snutt2.domain.model.LectureReminderOffset>>(), tables.updateTimetableLectureReminderCalls)
    }

    @Test
    fun `시간표와 강의 ID 및 선택 옵션을 전달한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        runCurrent()
        assertEquals(listOf(Triple(primary.id, first.lectureId, AT_START_TIME)), tables.updateTimetableLectureReminderCalls)
    }

    @Test
    fun `없음을 선택해도 NONE 옵션으로 저장을 요청한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = NONE))
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset(first.lectureId, NONE)
        runCurrent()
        assertEquals(listOf(Triple(primary.id, first.lectureId, NONE)), tables.updateTimetableLectureReminderCalls)
    }

    @Test
    fun `저장 중에는 선택값을 유지하고 해당 강의만 저장 중으로 표시한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val pending = CompletableDeferred<Result<LectureWithReminderOption>>()
        tables.updateTimetableLectureReminderHandler = { _, _, _ -> pending.await() }
        val vm = createViewModel()
        runCurrent()
        val before = vm.uiState.value as LectureReminderUiState.Loaded
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        runCurrent()
        assertEquals(before.copy(savingLectureIds = setOf(first.lectureId)), vm.uiState.value)
    }

    @Test
    fun `같은 강의의 저장 중 중복 요청을 차단한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val pending = CompletableDeferred<Result<LectureWithReminderOption>>()
        tables.updateTimetableLectureReminderHandler = { _, _, _ -> pending.await() }
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        vm.onSelectOffset(first.lectureId, NONE)
        runCurrent()
        assertEquals(listOf(Triple(primary.id, first.lectureId, AT_START_TIME)), tables.updateTimetableLectureReminderCalls)
    }

    @Test
    fun `성공하면 서버가 반환한 옵션으로 해당 강의만 갱신한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = NONE))
        val vm = createViewModel()
        runCurrent()
        val before = vm.uiState.value as LectureReminderUiState.Loaded
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        runCurrent()
        assertEquals(before.copy(lectures = listOf(first.copy(lectureReminderOffset = NONE), second)), vm.uiState.value)
    }

    @Test
    fun `NONE 저장 성공 시 서버 옵션으로 성공 이벤트를 보낸다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = NONE))
        val vm = createViewModel()
        runCurrent()
        vm.uiEvent.test {
            vm.onSelectOffset(first.lectureId, NONE)
            runCurrent()
            assertEquals(LectureReminderUiEvent.ReminderUpdated(NONE), awaitItem())
        }
    }

    @Test
    fun `TEN_MINUTES_BEFORE 저장 성공 시 서버 옵션으로 성공 이벤트를 보낸다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        tables.updateTimetableLectureReminderResult = Result.Success(second.copy(lectureReminderOffset = TEN_MINUTES_BEFORE))
        val vm = createViewModel()
        runCurrent()
        vm.uiEvent.test {
            vm.onSelectOffset(second.lectureId, TEN_MINUTES_BEFORE)
            runCurrent()
            assertEquals(LectureReminderUiEvent.ReminderUpdated(TEN_MINUTES_BEFORE), awaitItem())
        }
    }

    @Test
    fun `AT_START_TIME 저장 성공 시 서버 옵션으로 성공 이벤트를 보낸다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val vm = createViewModel()
        runCurrent()
        vm.uiEvent.test {
            vm.onSelectOffset(first.lectureId, AT_START_TIME)
            runCurrent()
            assertEquals(LectureReminderUiEvent.ReminderUpdated(AT_START_TIME), awaitItem())
        }
    }

    @Test
    fun `TEN_MINUTES_AFTER 저장 성공 시 서버 옵션으로 성공 이벤트를 보낸다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = TEN_MINUTES_AFTER))
        val vm = createViewModel()
        runCurrent()
        vm.uiEvent.test {
            vm.onSelectOffset(first.lectureId, TEN_MINUTES_AFTER)
            runCurrent()
            assertEquals(LectureReminderUiEvent.ReminderUpdated(TEN_MINUTES_AFTER), awaitItem())
        }
    }

    @Test
    fun `일반 오류 시 기존 선택을 유지하고 저장 중 표시를 해제한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        tables.updateTimetableLectureReminderResult = Result.Fail(Unknown("", "저장 실패"))
        val vm = createViewModel()
        runCurrent()
        val before = vm.uiState.value
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        runCurrent()
        assertEquals(before, vm.uiState.value)
    }

    @Test
    fun `일반 오류 시 사용자 메시지 이벤트를 보낸다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        tables.updateTimetableLectureReminderResult = Result.Fail(Unknown("", "저장 실패"))
        val vm = createViewModel()
        runCurrent()
        vm.uiEvent.test {
            vm.onSelectOffset(first.lectureId, AT_START_TIME)
            runCurrent()
            assertEquals(LectureReminderUiEvent.ShowToast("저장 실패"), awaitItem())
        }
    }

    @Test
    fun `시간 정보 없음 오류 시 기존 선택을 유지하고 저장 중 표시를 해제한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        tables.updateTimetableLectureReminderResult = Result.Fail(LectureTimeMissing("", "시간 정보가 없어요"))
        val vm = createViewModel()
        runCurrent()
        val before = vm.uiState.value
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        runCurrent()
        assertEquals(before, vm.uiState.value)
    }

    @Test
    fun `시간 정보 없음 오류 시 사용자 메시지 이벤트를 보낸다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        tables.updateTimetableLectureReminderResult = Result.Fail(LectureTimeMissing("", "시간 정보가 없어요"))
        val vm = createViewModel()
        runCurrent()
        vm.uiEvent.test {
            vm.onSelectOffset(first.lectureId, AT_START_TIME)
            runCurrent()
            assertEquals(LectureReminderUiEvent.ShowToast("시간 정보가 없어요"), awaitItem())
        }
    }

    @Test
    fun `다른 강의는 첫 강의 저장 중에도 독립적으로 요청한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val pendingFirst = CompletableDeferred<Result<LectureWithReminderOption>>()
        val pendingSecond = CompletableDeferred<Result<LectureWithReminderOption>>()
        tables.updateTimetableLectureReminderHandler = { _, id, _ ->
            if (id == first.lectureId) pendingFirst.await() else pendingSecond.await()
        }
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        vm.onSelectOffset(second.lectureId, NONE)
        runCurrent()
        assertEquals(
            listOf(Triple(primary.id, first.lectureId, AT_START_TIME), Triple(primary.id, second.lectureId, NONE)),
            tables.updateTimetableLectureReminderCalls,
        )
    }

    @Test
    fun `서로 다른 강의의 응답이 역순으로 와도 두 결과를 보존한다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val pendingFirst = CompletableDeferred<Result<LectureWithReminderOption>>()
        val pendingSecond = CompletableDeferred<Result<LectureWithReminderOption>>()
        tables.updateTimetableLectureReminderHandler = { _, id, _ ->
            if (id == first.lectureId) pendingFirst.await() else pendingSecond.await()
        }
        val vm = createViewModel()
        runCurrent()
        val before = vm.uiState.value as LectureReminderUiState.Loaded
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        vm.onSelectOffset(second.lectureId, NONE)
        pendingSecond.complete(Result.Success(second.copy(lectureReminderOffset = NONE)))
        runCurrent()
        pendingFirst.complete(Result.Success(first.copy(lectureReminderOffset = AT_START_TIME)))
        runCurrent()
        assertEquals(
            before.copy(lectures = listOf(first.copy(lectureReminderOffset = AT_START_TIME), second.copy(lectureReminderOffset = NONE))),
            vm.uiState.value,
        )
    }

    @Test
    fun `한 강의 저장 실패가 다른 강의의 성공 결과를 되돌리지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val pendingFirst = CompletableDeferred<Result<LectureWithReminderOption>>()
        val pendingSecond = CompletableDeferred<Result<LectureWithReminderOption>>()
        tables.updateTimetableLectureReminderHandler = { _, id, _ ->
            if (id == first.lectureId) pendingFirst.await() else pendingSecond.await()
        }
        val vm = createViewModel()
        runCurrent()
        val before = vm.uiState.value as LectureReminderUiState.Loaded
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        vm.onSelectOffset(second.lectureId, NONE)
        pendingSecond.complete(Result.Success(second.copy(lectureReminderOffset = NONE)))
        runCurrent()
        pendingFirst.complete(Result.Fail(Unknown("", "저장 실패")))
        runCurrent()
        assertEquals(before.copy(lectures = listOf(first, second.copy(lectureReminderOffset = NONE))), vm.uiState.value)
    }

    @Test
    fun `재조회 후 도착한 이전 저장 성공은 새 상태를 덮어쓰지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val pending = CompletableDeferred<Result<LectureWithReminderOption>>()
        tables.updateTimetableLectureReminderHandler = { _, _, _ -> pending.await() }
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        vm.retry()
        runCurrent()
        val before = vm.uiState.value
        pending.complete(Result.Success(first.copy(lectureReminderOffset = AT_START_TIME)))
        runCurrent()
        assertEquals(before, vm.uiState.value)
    }

    @Test
    fun `재조회 후 도착한 이전 저장 성공은 이벤트를 보내지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val pending = CompletableDeferred<Result<LectureWithReminderOption>>()
        tables.updateTimetableLectureReminderHandler = { _, _, _ -> pending.await() }
        val vm = createViewModel()
        runCurrent()
        vm.uiEvent.test {
            vm.onSelectOffset(first.lectureId, AT_START_TIME)
            vm.retry()
            runCurrent()
            pending.complete(Result.Success(first.copy(lectureReminderOffset = AT_START_TIME)))
            runCurrent()
            expectNoEvents()
        }
    }

    @Test
    fun `재조회 후 도착한 이전 저장 실패은 새 상태를 덮어쓰지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val pending = CompletableDeferred<Result<LectureWithReminderOption>>()
        tables.updateTimetableLectureReminderHandler = { _, _, _ -> pending.await() }
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        vm.retry()
        runCurrent()
        val before = vm.uiState.value
        pending.complete(Result.Fail(Unknown("", "이전 요청 실패")))
        runCurrent()
        assertEquals(before, vm.uiState.value)
    }

    @Test
    fun `재조회 후 도착한 이전 저장 실패은 이벤트를 보내지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val pending = CompletableDeferred<Result<LectureWithReminderOption>>()
        tables.updateTimetableLectureReminderHandler = { _, _, _ -> pending.await() }
        val vm = createViewModel()
        runCurrent()
        vm.uiEvent.test {
            vm.onSelectOffset(first.lectureId, AT_START_TIME)
            vm.retry()
            runCurrent()
            pending.complete(Result.Fail(Unknown("", "이전 요청 실패")))
            runCurrent()
            expectNoEvents()
        }
    }

    @Test
    fun `대표시간표 변경 후 도착한 저장 결과는 새 시간표를 덮어쓰지 않는다`() = runTest {
        val current = CourseBook(semester = 1, year = 2025)
        val next = CourseBook(semester = 2, year = 2025)
        val primary = TestFixtures.tableSummary(id = "primary", courseBook = current, isPrimary = true)
        val first = TestFixtures.lectureReminder("lecture-1", "자료구조", TEN_MINUTES_BEFORE)
        val second = TestFixtures.lectureReminder("lecture-2", "운영체제", TEN_MINUTES_AFTER)
        tables.tableSummaryList.value = listOf(primary)
        tables.fetchTableListResult = Result.Success(Unit)
        tables.fetchTableListDeferred = null
        tables.getTimetableRemindersHandler = null
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(primary.id, listOf(first, second)))
        semesters.semesterStatus.value = TestFixtures.semesterStatus(current, next)
        semesters.fetchSemesterStatusResult = Result.Success(Unit)
        semesters.fetchSemesterStatusDeferred = null
        tables.updateTimetableLectureReminderHandler = null
        tables.updateTimetableLectureReminderResult = Result.Success(first.copy(lectureReminderOffset = AT_START_TIME))
        val pending = CompletableDeferred<Result<LectureWithReminderOption>>()
        tables.updateTimetableLectureReminderHandler = { _, _, _ -> pending.await() }
        val vm = createViewModel()
        runCurrent()
        vm.onSelectOffset(first.lectureId, AT_START_TIME)
        val replacement = primary.copy(id = "replacement")
        tables.getTimetableRemindersResult = Result.Success(TimetableLectureReminders(replacement.id, listOf(second)))
        tables.tableSummaryList.value = listOf(replacement)
        runCurrent()
        pending.complete(Result.Success(first.copy(lectureReminderOffset = AT_START_TIME)))
        runCurrent()
        assertEquals(LectureReminderUiState.Loaded(replacement.id, listOf(second)), vm.uiState.value)
    }

    // endregion
}
