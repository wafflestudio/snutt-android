package com.wafflestudio.snutt2.feature.home.timetable

import app.cash.turbine.test
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domain.Unknown
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.ThemeReference
import com.wafflestudio.snutt2.domain.model.getFittingTrimParam
import com.wafflestudio.snutt2.fake.FakeCourseBookRepository
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeNotificationRepository
import com.wafflestudio.snutt2.fake.FakeRemoteConfig
import com.wafflestudio.snutt2.fake.FakeTableDisplayRepository
import com.wafflestudio.snutt2.fake.FakeTableRepository
import com.wafflestudio.snutt2.fake.FakeThemeRepository
import com.wafflestudio.snutt2.fixture.TestFixtures.courseBook2024_2
import com.wafflestudio.snutt2.fixture.TestFixtures.courseBook2025_1
import com.wafflestudio.snutt2.fixture.TestFixtures.syllabusLecture
import com.wafflestudio.snutt2.fixture.TestFixtures.table
import com.wafflestudio.snutt2.fixture.TestFixtures.tableSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimeTableViewModelTest {

    private lateinit var fakeTableDisplayRepository: FakeTableDisplayRepository
    private lateinit var fakeTableRepository: FakeTableRepository
    private lateinit var fakeThemeRepository: FakeThemeRepository
    private lateinit var fakeNotificationRepository: FakeNotificationRepository
    private lateinit var fakeCourseBookRepository: FakeCourseBookRepository
    private lateinit var fakeRemoteConfig: FakeRemoteConfig
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeTableDisplayRepository = FakeTableDisplayRepository()
        fakeTableRepository = FakeTableRepository()
        fakeThemeRepository = FakeThemeRepository()
        fakeNotificationRepository = FakeNotificationRepository()
        fakeCourseBookRepository = FakeCourseBookRepository()
        fakeRemoteConfig = FakeRemoteConfig()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = TimeTableViewModel(
        tableDisplayRepository = fakeTableDisplayRepository,
        getCurrentTableThemeUseCase = GetCurrentTableThemeUseCase(
            themeRepository = fakeThemeRepository,
            tableRepository = fakeTableRepository,
        ),
        tableRepository = fakeTableRepository,
        notificationRepository = fakeNotificationRepository,
        courseBookRepository = fakeCourseBookRepository,
        remoteConfig = fakeRemoteConfig,
        displayMessageResolver = fakeDisplayMessageResolver,
    )

    // region init combine

    @Test
    fun `init combine 시 모든 source가 emit되면 Loaded 상태가 된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        val testTrimParam = TableTrimParam(1, 5, 10, 20, forceFitLectures = false)
        val testLectureCustom = TableLectureCustom(title = true, place = false, lectureNumber = true, instructor = false)
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = testTrimParam
        fakeTableDisplayRepository.compactMode.value = true
        fakeTableDisplayRepository.tableLectureCustomOption.value = testLectureCustom
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = true

        val viewModel = createViewModel()

        assertEquals(
            TimeTableUiState.Loaded(
                table = testTable,
                theme = BuiltInTheme.SNUTT,
                previewTheme = null,
                tableTrimParam = testTrimParam,
                isCompactMode = true,
                tableLectureCustomOptions = testLectureCustom,
                newSemesterExist = false,
                uncheckedNotificationExist = false,
                vacancyNotificationBannerEnabled = true,
                isSessionlessLectureHintVisible = false,
                dialogState = TimeTableUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `init 시 currentTable이 null이면 combine이 emit하지 않아 Loading을 유지한다`() = runTest {
        fakeTableRepository.currentTable.value = null

        val viewModel = createViewModel()

        assertEquals(TimeTableUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `init combine 시 forceFitLectures가 true이면 tableTrimParam이 강의 기반으로 fit된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        val trimParam = TableTrimParam(0, 4, 9, 18, forceFitLectures = true)
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = trimParam
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false

        val viewModel = createViewModel()

        assertEquals(
            TimeTableUiState.Loaded(
                table = testTable,
                theme = BuiltInTheme.SNUTT,
                previewTheme = null,
                tableTrimParam = testTable.lectures.getFittingTrimParam(TableTrimParam.Default),
                isCompactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                newSemesterExist = false,
                uncheckedNotificationExist = false,
                vacancyNotificationBannerEnabled = false,
                isSessionlessLectureHintVisible = false,
                dialogState = TimeTableUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `init combine 시 가장 최근 코스북이 tableSummaryList에 없으면 newSemesterExist가 true이다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2024_2))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary) // 2024-2 만 존재
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1) // 가장 최근: 2025-1
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false

        val viewModel = createViewModel()

        assertEquals(
            TimeTableUiState.Loaded(
                table = testTable,
                theme = BuiltInTheme.SNUTT,
                previewTheme = null,
                tableTrimParam = TableTrimParam(0, 4, 9, 18, forceFitLectures = false),
                isCompactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                newSemesterExist = true,
                uncheckedNotificationExist = false,
                vacancyNotificationBannerEnabled = false,
                isSessionlessLectureHintVisible = false,
                dialogState = TimeTableUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `init combine 시 가장 최근 코스북이 tableSummaryList에 있으면 newSemesterExist가 false이다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false

        val viewModel = createViewModel()

        assertEquals(
            TimeTableUiState.Loaded(
                table = testTable,
                theme = BuiltInTheme.SNUTT,
                previewTheme = null,
                tableTrimParam = TableTrimParam(0, 4, 9, 18, forceFitLectures = false),
                isCompactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                newSemesterExist = false,
                uncheckedNotificationExist = false,
                vacancyNotificationBannerEnabled = false,
                isSessionlessLectureHintVisible = false,
                dialogState = TimeTableUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `init combine 시 courseBooks가 비어있으면 newSemesterExist가 false이다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = emptyList()
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false

        val viewModel = createViewModel()

        assertEquals(
            TimeTableUiState.Loaded(
                table = testTable,
                theme = BuiltInTheme.SNUTT,
                previewTheme = null,
                tableTrimParam = TableTrimParam(0, 4, 9, 18, forceFitLectures = false),
                isCompactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                newSemesterExist = false,
                uncheckedNotificationExist = false,
                vacancyNotificationBannerEnabled = false,
                isSessionlessLectureHintVisible = false,
                dialogState = TimeTableUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `init combine 시 notificationCount가 0보다 크면 uncheckedNotificationExist가 true이다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 3L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false

        val viewModel = createViewModel()

        assertEquals(
            TimeTableUiState.Loaded(
                table = testTable,
                theme = BuiltInTheme.SNUTT,
                previewTheme = null,
                tableTrimParam = TableTrimParam(0, 4, 9, 18, forceFitLectures = false),
                isCompactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                newSemesterExist = false,
                uncheckedNotificationExist = true,
                vacancyNotificationBannerEnabled = false,
                isSessionlessLectureHintVisible = false,
                dialogState = TimeTableUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `init combine 시 미방문이고 lectureSessions이 빈 강의가 있으면 isSessionlessLectureHintVisible이 true이다`() = runTest {
        val sessionlessLecture = syllabusLecture(id = "lec-1") // lectureSessions = emptyList
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
            .copy(lectures = listOf(sessionlessLecture))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false

        val viewModel = createViewModel()

        assertEquals(
            TimeTableUiState.Loaded(
                table = testTable,
                theme = BuiltInTheme.SNUTT,
                previewTheme = null,
                tableTrimParam = TableTrimParam(0, 4, 9, 18, forceFitLectures = false),
                isCompactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                newSemesterExist = false,
                uncheckedNotificationExist = false,
                vacancyNotificationBannerEnabled = false,
                isSessionlessLectureHintVisible = true,
                dialogState = TimeTableUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `init combine 시 isVisitedSessionlessLectureList가 true이면 isSessionlessLectureHintVisible이 false이다`() = runTest {
        val sessionlessLecture = syllabusLecture(id = "lec-1") // lectureSessions = emptyList
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
            .copy(lectures = listOf(sessionlessLecture))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false

        val viewModel = createViewModel()

        assertEquals(
            TimeTableUiState.Loaded(
                table = testTable,
                theme = BuiltInTheme.SNUTT,
                previewTheme = null,
                tableTrimParam = TableTrimParam(0, 4, 9, 18, forceFitLectures = false),
                isCompactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                newSemesterExist = false,
                uncheckedNotificationExist = false,
                vacancyNotificationBannerEnabled = false,
                isSessionlessLectureHintVisible = false,
                dialogState = TimeTableUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `init combine 시 모든 강의의 lectureSessions이 비어있지 않으면 isSessionlessLectureHintVisible이 false이다`() = runTest {
        val lectureWithSession = syllabusLecture(id = "lec-1")
            .copy(lectureSessions = listOf(LectureSession.Default))
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
            .copy(lectures = listOf(lectureWithSession))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false

        val viewModel = createViewModel()

        assertEquals(
            TimeTableUiState.Loaded(
                table = testTable,
                theme = BuiltInTheme.SNUTT,
                previewTheme = null,
                tableTrimParam = TableTrimParam(0, 4, 9, 18, forceFitLectures = false),
                isCompactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                newSemesterExist = false,
                uncheckedNotificationExist = false,
                vacancyNotificationBannerEnabled = false,
                isSessionlessLectureHintVisible = false,
                dialogState = TimeTableUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: currentTable

    @Test
    fun `currentTable이 변경되면 table이 갱신된다`() = runTest {
        val initialTable = table(summary = tableSummary(id = "t1", courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = initialTable
        fakeTableRepository.tableSummaryList.value = listOf(initialTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        val newTable = table(
            summary = tableSummary(id = "t2", courseBook = courseBook2025_1),
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeTableRepository.currentTable.value = newTable

        assertEquals(
            before.copy(table = newTable),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `currentTable의 themeRef가 변경되면 theme이 갱신된다`() = runTest {
        val initialTable = table(
            summary = tableSummary(id = "t1", courseBook = courseBook2025_1),
            themeRef = ThemeReference.BuiltIn(0), // SNUTT
        )
        fakeTableRepository.currentTable.value = initialTable
        fakeTableRepository.tableSummaryList.value = listOf(initialTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        val newTable = table(
            summary = tableSummary(id = "t1", courseBook = courseBook2025_1),
            themeRef = ThemeReference.BuiltIn(1), // MODERN
        )
        fakeTableRepository.currentTable.value = newTable

        assertEquals(
            before.copy(table = newTable, theme = BuiltInTheme.MODERN),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: tableTrimParam

    @Test
    fun `tableTrimParam이 변경되면 tableTrimParam이 갱신된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        val newTrimParam = TableTrimParam(1, 5, 10, 20, forceFitLectures = false)
        fakeTableDisplayRepository.tableTrimParam.value = newTrimParam

        assertEquals(
            before.copy(tableTrimParam = newTrimParam),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: compactMode

    @Test
    fun `compactMode가 변경되면 isCompactMode가 갱신된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        fakeTableDisplayRepository.compactMode.value = true

        assertEquals(
            before.copy(isCompactMode = true),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: tableLectureCustomOption

    @Test
    fun `tableLectureCustomOption이 변경되면 tableLectureCustomOptions가 갱신된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        val newCustom = TableLectureCustom(title = false, place = false, lectureNumber = true, instructor = true)
        fakeTableDisplayRepository.tableLectureCustomOption.value = newCustom

        assertEquals(
            before.copy(tableLectureCustomOptions = newCustom),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: tableSummaryList

    @Test
    fun `tableSummaryList가 변경되어 가장 최근 코스북이 빠지면 newSemesterExist가 true로 갱신된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2024_2))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(
            testTable.summary,
            tableSummary(id = "t2", courseBook = courseBook2025_1), // mostRecent 포함 → false
        )
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary) // mostRecent 빠짐 → true

        assertEquals(
            before.copy(newSemesterExist = true),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: courseBooks

    @Test
    fun `courseBooks가 변경되어 새 학기가 추가되면 newSemesterExist가 true로 갱신된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2024_2))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2024_2) // 같은 학기만 존재 → false
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1, courseBook2024_2) // 새 학기 추가 → true

        assertEquals(
            before.copy(newSemesterExist = true),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: notificationCount

    @Test
    fun `notificationCount가 0보다 커지면 uncheckedNotificationExist가 true로 갱신된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        fakeNotificationRepository.notificationCount.value = 7L

        assertEquals(
            before.copy(uncheckedNotificationExist = true),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: vacancyNotificationBannerEnabled

    @Test
    fun `vacancyNotificationBannerEnabled가 변경되면 UiState에 반영된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = true

        assertEquals(
            before.copy(vacancyNotificationBannerEnabled = true),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: isVisitedSessionlessLectureList

    @Test
    fun `isVisitedSessionlessLectureList가 true로 변경되면 isSessionlessLectureHintVisible이 false로 갱신된다`() = runTest {
        val sessionlessLecture = syllabusLecture(id = "lec-1")
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
            .copy(lectures = listOf(sessionlessLecture))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = true

        assertEquals(
            before.copy(isSessionlessLectureHintVisible = false),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region previewTheme/dialogState 보존

    @Test
    fun `source가 변경되어도 이전 previewTheme이 보존된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        viewModel.setPreviewTheme(BuiltInTheme.MODERN)
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        fakeNotificationRepository.notificationCount.value = 5L

        assertEquals(
            before.copy(uncheckedNotificationExist = true),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `source가 변경되어도 이전 dialogState가 보존된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        val dialogTarget = tableSummary(id = "t-dialog", courseBook = courseBook2025_1)
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        viewModel.showTableTitleChangeDialog(dialogTarget)
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        fakeNotificationRepository.notificationCount.value = 5L

        assertEquals(
            before.copy(uncheckedNotificationExist = true),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region refresh

    @Test
    fun `refresh 호출 시 notificationRepository의 fetchNotificationCount가 호출된다`() = runTest {
        fakeNotificationRepository.fetchNotificationCountResult = Result.Success(Unit)
        fakeCourseBookRepository.fetchCourseBooksResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.refresh()

        assertEquals(true, fakeNotificationRepository.fetchNotificationCountCalled)
    }

    @Test
    fun `refresh 호출 시 courseBookRepository의 fetchCourseBooks가 호출된다`() = runTest {
        fakeNotificationRepository.fetchNotificationCountResult = Result.Success(Unit)
        fakeCourseBookRepository.fetchCourseBooksResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.refresh()

        assertEquals(true, fakeCourseBookRepository.fetchCourseBooksCalled)
    }

    // endregion

    // region visitSessionlessLectureList

    @Test
    fun `visitSessionlessLectureList 호출 시 isSessionlessLectureHintVisible이 false가 된다`() = runTest {
        val sessionlessLecture = syllabusLecture(id = "lec-1")
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
            .copy(lectures = listOf(sessionlessLecture))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        viewModel.visitSessionlessLectureList()

        assertEquals(
            before.copy(isSessionlessLectureHintVisible = false),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `visitSessionlessLectureList 호출 시 repository의 visitSessionlessLectureList가 호출된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()

        viewModel.visitSessionlessLectureList()

        assertEquals(true, fakeTableDisplayRepository.visitSessionlessLectureListCalled)
    }

    // endregion

    // region setPreviewTheme

    @Test
    fun `setPreviewTheme 호출 시 previewTheme이 설정된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        viewModel.setPreviewTheme(BuiltInTheme.MODERN)

        assertEquals(
            before.copy(previewTheme = BuiltInTheme.MODERN),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region resetPreviewTheme

    @Test
    fun `resetPreviewTheme 호출 시 previewTheme이 null이 된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        viewModel.setPreviewTheme(BuiltInTheme.MODERN)
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        viewModel.resetPreviewTheme()

        assertEquals(
            before.copy(previewTheme = null),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region showTableTitleChangeDialog

    @Test
    fun `showTableTitleChangeDialog 호출 시 dialogState가 ChangeTableName이 된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        val target = tableSummary(id = "t-target", courseBook = courseBook2025_1)
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        viewModel.showTableTitleChangeDialog(target)

        assertEquals(
            before.copy(dialogState = TimeTableUiState.DialogState.ChangeTableName(target)),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region changeTableTitle

    @Test
    fun `changeTableTitle 호출 시 repository의 updateTableName을 호출한다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        val target = tableSummary(id = "t-target", courseBook = courseBook2025_1)
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        fakeTableRepository.updateTableNameResult = Result.Success(Unit)
        val viewModel = createViewModel()
        viewModel.showTableTitleChangeDialog(target)
        viewModel.onChangeTableNameTitleChange("새 제목")

        viewModel.changeTableTitle()

        assertEquals(target to "새 제목", fakeTableRepository.updateTableNameCalledWith)
    }

    @Test
    fun `changeTableTitle 성공 시 dialogState가 None으로 변경된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        val target = tableSummary(id = "t-target", courseBook = courseBook2025_1)
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        fakeTableRepository.updateTableNameResult = Result.Success(Unit)
        val viewModel = createViewModel()
        viewModel.showTableTitleChangeDialog(target)
        viewModel.onChangeTableNameTitleChange("새 제목")
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        viewModel.changeTableTitle()

        assertEquals(
            before.copy(dialogState = TimeTableUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `changeTableTitle 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        val target = tableSummary(id = "t-target", courseBook = courseBook2025_1)
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        fakeTableRepository.updateTableNameResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "이름 변경 실패"))
        val viewModel = createViewModel()
        viewModel.showTableTitleChangeDialog(target)
        viewModel.onChangeTableNameTitleChange("새 제목")

        viewModel.uiEvent.test {
            viewModel.changeTableTitle()
            assertEquals(TimeTableUiEvent.ShowToast("이름 변경 실패"), awaitItem())
        }
    }

    // endregion

    // region dismissDialog

    @Test
    fun `dismissDialog 호출 시 dialogState가 None이 된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        val target = tableSummary(id = "t-target", courseBook = courseBook2025_1)
        fakeTableRepository.currentTable.value = testTable
        fakeTableRepository.tableSummaryList.value = listOf(testTable.summary)
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.isVisitedSessionlessLectureList.value = false
        fakeThemeRepository.customThemes.value = emptyList()
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeNotificationRepository.notificationCount.value = 0L
        fakeRemoteConfig.vacancyNotificationBannerEnabled.value = false
        val viewModel = createViewModel()
        viewModel.showTableTitleChangeDialog(target)
        val before = viewModel.uiState.value as TimeTableUiState.Loaded

        viewModel.dismissDialog()

        assertEquals(
            before.copy(dialogState = TimeTableUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    // endregion
}
