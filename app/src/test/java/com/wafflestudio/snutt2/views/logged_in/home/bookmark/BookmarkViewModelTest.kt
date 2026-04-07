package com.wafflestudio.snutt2.views.logged_in.home.bookmark

import app.cash.turbine.test
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.fake.FakeAnalyticsLogger
import com.wafflestudio.snutt2.fake.FakeBookmarkRepository
import com.wafflestudio.snutt2.fake.FakeCurrentTableLectureRepository
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeLectureInfoRepository
import com.wafflestudio.snutt2.fake.FakeNotificationRepository
import com.wafflestudio.snutt2.fake.FakeRemoteConfig
import com.wafflestudio.snutt2.fake.FakeTableDisplayRepository
import com.wafflestudio.snutt2.fake.FakeTableRepository
import com.wafflestudio.snutt2.fake.FakeThemeRepository
import com.wafflestudio.snutt2.fake.FakeUserRepository
import com.wafflestudio.snutt2.fake.FakeVacancyRepository
import com.wafflestudio.snutt2.fixture.TestFixtures.building
import com.wafflestudio.snutt2.fixture.TestFixtures.courseBook2025_1
import com.wafflestudio.snutt2.fixture.TestFixtures.lecture1
import com.wafflestudio.snutt2.fixture.TestFixtures.lecture2
import com.wafflestudio.snutt2.fixture.TestFixtures.searchedLecture
import com.wafflestudio.snutt2.fixture.TestFixtures.syllabusLecture
import com.wafflestudio.snutt2.fixture.TestFixtures.table
import com.wafflestudio.snutt2.fixture.TestFixtures.tableSummary
import com.wafflestudio.snutt2.lib.network.LectureOverlap
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.Unknown
import com.wafflestudio.snutt2.lib.network.WrongUserToken
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureState
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
class BookmarkViewModelTest {

    private lateinit var fakeCurrentTableLectureRepository: FakeCurrentTableLectureRepository
    private lateinit var fakeTableRepository: FakeTableRepository
    private lateinit var fakeBookmarkRepository: FakeBookmarkRepository
    private lateinit var fakeTableDisplayRepository: FakeTableDisplayRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeVacancyRepository: FakeVacancyRepository
    private lateinit var fakeNotificationRepository: FakeNotificationRepository
    private lateinit var fakeLectureInfoRepository: FakeLectureInfoRepository
    private lateinit var fakeThemeRepository: FakeThemeRepository
    private lateinit var fakeRemoteConfig: FakeRemoteConfig
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver
    private lateinit var fakeAnalyticsLogger: FakeAnalyticsLogger

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeCurrentTableLectureRepository = FakeCurrentTableLectureRepository()
        fakeTableRepository = FakeTableRepository()
        fakeBookmarkRepository = FakeBookmarkRepository()
        fakeTableDisplayRepository = FakeTableDisplayRepository()
        fakeUserRepository = FakeUserRepository()
        fakeVacancyRepository = FakeVacancyRepository()
        fakeNotificationRepository = FakeNotificationRepository()
        fakeLectureInfoRepository = FakeLectureInfoRepository()
        fakeThemeRepository = FakeThemeRepository()
        fakeRemoteConfig = FakeRemoteConfig()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
        fakeAnalyticsLogger = FakeAnalyticsLogger()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = BookmarkViewModel(
        currentTableLectureRepository = fakeCurrentTableLectureRepository,
        tableRepository = fakeTableRepository,
        bookmarkRepository = fakeBookmarkRepository,
        tableDisplayRepository = fakeTableDisplayRepository,
        userRepository = fakeUserRepository,
        vacancyRepository = fakeVacancyRepository,
        notificationRepository = fakeNotificationRepository,
        lectureInfoRepository = fakeLectureInfoRepository,
        getCurrentTableThemeUseCase = GetCurrentTableThemeUseCase(
            themeRepository = fakeThemeRepository,
            tableRepository = fakeTableRepository,
        ),
        remoteConfig = fakeRemoteConfig,
        displayMessageResolver = fakeDisplayMessageResolver,
        analyticsLogger = fakeAnalyticsLogger,
    )

    // region init

    @Test
    fun `init 시 모든 source가 제공되면 Success 상태가 된다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        val testTrimParam = TableTrimParam(1, 5, 10, 20, false)
        val testLectureCustom = TableLectureCustom(title = true, place = false, lectureNumber = true, instructor = false)
        fakeTableRepository.currentTable.value = testTable
        fakeTableDisplayRepository.tableTrimParam.value = testTrimParam
        fakeTableDisplayRepository.tableLectureCustomOption.value = testLectureCustom
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))

        val viewModel = createViewModel()

        assertEquals(
            BookmarkUiState.Success(
                currentTable = testTable,
                tableTheme = BuiltInTheme.SNUTT,
                bookmarkList = listOf(
                    lecture1.toDataWithState(
                        LectureState(selected = false, contained = false, isBookmarked = true, isVacancyRegistered = false),
                    ),
                ),
                selectedLecture = null,
                tableTrimParam = testTrimParam.copy(forceFitLectures = true),
                tableLectureCustomOptions = testLectureCustom,
                isCompactMode = true,
                uncheckedNotificationCount = 5L,
                disableMapFeature = false,
                vacancyList = emptyList(),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `init 시 테이블에 포함된 강의는 bookmarkList에서 contained가 true이다`() = runTest {
        val syllabusLec = syllabusLecture(id = "syllabus-1", originalLectureId = "lec-1")
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1)).copy(lectures = listOf(syllabusLec))
        val testTrimParam = TableTrimParam(1, 5, 10, 20, false)
        val testLectureCustom = TableLectureCustom(title = true, place = false, lectureNumber = true, instructor = false)
        fakeTableRepository.currentTable.value = testTable
        fakeTableDisplayRepository.tableTrimParam.value = testTrimParam
        fakeTableDisplayRepository.tableLectureCustomOption.value = testLectureCustom
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))

        val viewModel = createViewModel()

        assertEquals(
            BookmarkUiState.Success(
                currentTable = testTable,
                tableTheme = BuiltInTheme.SNUTT,
                bookmarkList = listOf(
                    lecture1.toDataWithState(
                        LectureState(selected = false, contained = true, isBookmarked = true, isVacancyRegistered = false),
                    ),
                ),
                selectedLecture = null,
                tableTrimParam = testTrimParam.copy(forceFitLectures = true),
                tableLectureCustomOptions = testLectureCustom,
                isCompactMode = true,
                uncheckedNotificationCount = 5L,
                disableMapFeature = false,
                vacancyList = emptyList(),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `init 시 vacancy에 등록된 강의는 bookmarkList에서 isVacancyRegistered가 true이다`() = runTest {
        val testTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        val testTrimParam = TableTrimParam(1, 5, 10, 20, false)
        val testLectureCustom = TableLectureCustom(title = true, place = false, lectureNumber = true, instructor = false)
        fakeTableRepository.currentTable.value = testTable
        fakeTableDisplayRepository.tableTrimParam.value = testTrimParam
        fakeTableDisplayRepository.tableLectureCustomOption.value = testLectureCustom
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))

        val viewModel = createViewModel()

        assertEquals(
            BookmarkUiState.Success(
                currentTable = testTable,
                tableTheme = BuiltInTheme.SNUTT,
                bookmarkList = listOf(
                    lecture1.toDataWithState(
                        LectureState(selected = false, contained = false, isBookmarked = true, isVacancyRegistered = true),
                    ),
                ),
                selectedLecture = null,
                tableTrimParam = testTrimParam.copy(forceFitLectures = true),
                tableLectureCustomOptions = testLectureCustom,
                isCompactMode = true,
                uncheckedNotificationCount = 5L,
                disableMapFeature = false,
                vacancyList = listOf(lecture1),
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: tableTrimParam

    @Test
    fun `tableTrimParam이 변경되면 tableTrimParam이 갱신된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        val newTrimParam = TableTrimParam(0, 6, 8, 22, true)
        fakeTableDisplayRepository.tableTrimParam.value = newTrimParam

        assertEquals(
            before.copy(tableTrimParam = newTrimParam.copy(forceFitLectures = true)),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: tableLectureCustomOption

    @Test
    fun `tableLectureCustomOption이 변경되면 tableLectureCustomOptions가 갱신된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        val newLectureCustom = TableLectureCustom(false, true, false, true)
        fakeTableDisplayRepository.tableLectureCustomOption.value = newLectureCustom

        assertEquals(
            before.copy(tableLectureCustomOptions = newLectureCustom),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: compactMode

    @Test
    fun `compactMode가 변경되면 isCompactMode가 갱신된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        fakeTableDisplayRepository.compactMode.value = false

        assertEquals(
            before.copy(isCompactMode = false),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: notificationCount

    @Test
    fun `notificationCount가 변경되면 uncheckedNotificationCount가 갱신된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        fakeNotificationRepository.notificationCount.value = 12L

        assertEquals(
            before.copy(uncheckedNotificationCount = 12L),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: disableMapFeature

    @Test
    fun `disableMapFeature가 변경되면 disableMapFeature가 갱신된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        fakeRemoteConfig.disableMapFeature.value = true

        assertEquals(
            before.copy(disableMapFeature = true),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: vacancyLectures

    @Test
    fun `vacancyLectures가 변경되면 vacancyList와 bookmarkList의 isVacancyRegistered가 갱신된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1, lecture2))
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)

        assertEquals(
            before.copy(
                vacancyList = listOf(lecture1),
                bookmarkList = before.bookmarkList.map { item ->
                    item.copy(state = item.state.copy(isVacancyRegistered = item.item.id == lecture1.id))
                },
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `bottomSheetType이 LectureDetail인 상태에서 vacancyLectures가 변경되면 isVacancyRegistered가 갱신된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeLectureInfoRepository.getBuildingsResult = Result.Success(emptyList())
        val viewModel = createViewModel()
        viewModel.openLectureDetailSheet(lecture1)
        val before = viewModel.uiState.value as BookmarkUiState.Success

        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)

        val beforeSheet = before.bottomSheetType as BookmarkUiState.BottomSheetType.LectureDetail
        assertEquals(
            before.copy(
                vacancyList = listOf(lecture1),
                bookmarkList = before.bookmarkList.map { item ->
                    item.copy(state = item.state.copy(isVacancyRegistered = item.item.id == lecture1.id))
                },
                bottomSheetType = beforeSheet.copy(isVacancyRegistered = true),
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region source: bookmarks

    @Test
    fun `bottomSheetType이 LectureDetail인 상태에서 bookmarks가 변경되면 isBookmarked가 갱신된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeLectureInfoRepository.getBuildingsResult = Result.Success(emptyList())
        val viewModel = createViewModel()
        viewModel.openLectureDetailSheet(lecture1)
        val before = viewModel.uiState.value as BookmarkUiState.Success

        // bookmark에서 lecture1 제거
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to emptyList())

        val beforeSheet = before.bottomSheetType as BookmarkUiState.BottomSheetType.LectureDetail
        assertEquals(
            before.copy(
                bookmarkList = emptyList(),
                bottomSheetType = beforeSheet.copy(isBookmarked = false),
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onToggleLectureSelection

    @Test
    fun `onToggleLectureSelection 호출 시 해당 강의가 선택된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1, lecture2))
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onToggleLectureSelection(lecture1)

        assertEquals(
            before.copy(
                selectedLecture = lecture1,
                bookmarkList = before.bookmarkList.map { item ->
                    item.copy(state = item.state.copy(selected = item.item.id == lecture1.id))
                },
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `이미 선택된 강의에 onToggleLectureSelection 호출 시 선택이 해제된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()
        viewModel.onToggleLectureSelection(lecture1)
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onToggleLectureSelection(lecture1)

        assertEquals(
            before.copy(
                selectedLecture = null,
                bookmarkList = before.bookmarkList.map { item ->
                    item.copy(state = item.state.copy(selected = false))
                },
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onClickBookmark

    @Test
    fun `bookmarked된 강의에 onClickBookmark 호출 시 DeleteBookmark 다이얼로그가 열린다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onClickBookmark(lecture1)

        assertEquals(
            before.copy(dialogState = BookmarkUiState.DialogState.DeleteBookmark(lecture1)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `bookmarked되지 않은 강의에 onClickBookmark 호출 시 addBookmark이 호출된다`() = runTest {
        val unbookmarkedLecture = searchedLecture(id = "lec-99", courseTitle = "물리학")
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeBookmarkRepository.addBookmarkResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.onClickBookmark(unbookmarkedLecture)

        assertEquals(courseBook2025_1 to unbookmarkedLecture, fakeBookmarkRepository.addBookmarkCalledWith)
    }

    @Test
    fun `onClickBookmark에서 addBookmark 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val unbookmarkedLecture = searchedLecture(id = "lec-99", courseTitle = "물리학")
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeBookmarkRepository.addBookmarkResult = Result.Fail(Unknown(displayTitle = "", displayMessage = "추가 실패"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickBookmark(unbookmarkedLecture)
            assertEquals(BookmarkUiEvent.ShowToast("추가 실패"), awaitItem())
        }
    }

    @Test
    fun `onClickBookmark에서 addBookmark AuthError 시 ShowToast와 NavigateToOnboard 이벤트가 발생한다`() = runTest {
        val unbookmarkedLecture = searchedLecture(id = "lec-99", courseTitle = "물리학")
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeBookmarkRepository.addBookmarkResult = Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        fakeUserRepository.postForceLogoutResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickBookmark(unbookmarkedLecture)
            assertEquals(BookmarkUiEvent.ShowToast("인증 만료"), awaitItem())
            assertEquals(BookmarkUiEvent.NavigateToOnboard, awaitItem())
        }
    }

    @Test
    fun `onClickBookmark에서 addBookmark AuthError 시 postForceLogout이 호출된다`() = runTest {
        val unbookmarkedLecture = searchedLecture(id = "lec-99", courseTitle = "물리학")
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeBookmarkRepository.addBookmarkResult = Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        fakeUserRepository.postForceLogoutResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.onClickBookmark(unbookmarkedLecture)

        assertEquals(true, fakeUserRepository.postForceLogoutCalled)
    }

    // endregion

    // region onConfirmDeleteBookmark

    @Test
    fun `onConfirmDeleteBookmark 호출 시 deleteBookmark이 호출된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeBookmarkRepository.deleteBookmarkResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.onConfirmDeleteBookmark(lecture1)

        assertEquals(courseBook2025_1 to lecture1, fakeBookmarkRepository.deleteBookmarkCalledWith)
    }

    @Test
    fun `onConfirmDeleteBookmark 성공 시 selectedLecture가 삭제된 강의이면 다이얼로그가 닫히고 선택이 해제된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeBookmarkRepository.deleteBookmarkResult = Result.Success(Unit)
        val viewModel = createViewModel()
        // 강의 선택 후 삭제 다이얼로그 열기
        viewModel.onToggleLectureSelection(lecture1)
        viewModel.onClickBookmark(lecture1)
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onConfirmDeleteBookmark(lecture1)

        assertEquals(
            before.copy(
                dialogState = BookmarkUiState.DialogState.None,
                selectedLecture = null,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `onConfirmDeleteBookmark 성공 시 selectedLecture가 다른 강의이면 다이얼로그가 닫히고 선택이 유지된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1, lecture2))
        fakeBookmarkRepository.deleteBookmarkResult = Result.Success(Unit)
        val viewModel = createViewModel()
        // lecture2를 선택, lecture1 삭제 다이얼로그 열기
        viewModel.onToggleLectureSelection(lecture2)
        viewModel.onClickBookmark(lecture1)
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onConfirmDeleteBookmark(lecture1)

        assertEquals(
            before.copy(dialogState = BookmarkUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `onConfirmDeleteBookmark 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeBookmarkRepository.deleteBookmarkResult = Result.Fail(Unknown(displayTitle = "", displayMessage = "삭제 실패"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onConfirmDeleteBookmark(lecture1)
            assertEquals(BookmarkUiEvent.ShowToast("삭제 실패"), awaitItem())
        }
    }

    // endregion

    // region onClickVacancy

    @Test
    fun `vacancy 등록된 강의에 onClickVacancy 호출 시 DeleteVacancyNotification 다이얼로그가 열린다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onClickVacancy(lecture1)

        assertEquals(
            before.copy(dialogState = BookmarkUiState.DialogState.DeleteVacancyNotification(lecture1)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `vacancy 미등록 강의에 onClickVacancy 호출 시 addVacancyLecture가 호출된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.addVacancyLectureResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()

        viewModel.onClickVacancy(lecture1)

        assertEquals(lecture1, fakeVacancyRepository.addVacancyLectureCalledWith)
    }

    @Test
    fun `onClickVacancy에서 addVacancyLecture 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.addVacancyLectureResult = Result.Fail(Unknown(displayTitle = "", displayMessage = "빈자리 등록 실패"))
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickVacancy(lecture1)
            assertEquals(BookmarkUiEvent.ShowToast("빈자리 등록 실패"), awaitItem())
        }
    }

    // endregion

    // region onConfirmDeleteVacancyNotification

    @Test
    fun `onConfirmDeleteVacancyNotification 호출 시 다이얼로그가 닫힌다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.removeVacancyLectureResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()
        viewModel.onClickVacancy(lecture1) // 다이얼로그 열기
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onConfirmDeleteVacancyNotification(lecture1)

        assertEquals(
            before.copy(dialogState = BookmarkUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `onConfirmDeleteVacancyNotification 호출 시 removeVacancyLecture가 호출된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.removeVacancyLectureResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()

        viewModel.onConfirmDeleteVacancyNotification(lecture1)

        assertEquals(listOf(lecture1), fakeVacancyRepository.removeVacancyLectureCalledWith)
    }

    @Test
    fun `onConfirmDeleteVacancyNotification에서 removeVacancyLecture 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.removeVacancyLectureResult = Result.Fail(Unknown(displayTitle = "", displayMessage = "삭제 실패"))
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onConfirmDeleteVacancyNotification(lecture1)
            assertEquals(BookmarkUiEvent.ShowToast("삭제 실패"), awaitItem())
        }
    }

    // endregion

    // region onToggleLectureContained

    @Test
    fun `이미 포함된 강의에 onToggleLectureContained 호출 시 removeLecture가 호출된다`() = runTest {
        val syllabusLec = syllabusLecture(id = "syllabus-1", originalLectureId = "lec-1")
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
            .copy(lectures = listOf(syllabusLec))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeCurrentTableLectureRepository.removeLectureSearchedResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.onToggleLectureContained(lecture1)

        assertEquals(lecture1, fakeCurrentTableLectureRepository.removeLectureSearchedCalledWith)
    }

    @Test
    fun `포함된 강의 removeLecture 성공 시 해당 강의 선택이 토글된다`() = runTest {
        val syllabusLec = syllabusLecture(id = "syllabus-1", originalLectureId = "lec-1")
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
            .copy(lectures = listOf(syllabusLec))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeCurrentTableLectureRepository.removeLectureSearchedResult = Result.Success(Unit)
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onToggleLectureContained(lecture1)

        assertEquals(
            before.copy(
                selectedLecture = lecture1,
                bookmarkList = before.bookmarkList.map { item ->
                    item.copy(state = item.state.copy(selected = item.item.id == lecture1.id))
                },
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `포함된 강의 removeLecture 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val syllabusLec = syllabusLecture(id = "syllabus-1", originalLectureId = "lec-1")
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
            .copy(lectures = listOf(syllabusLec))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeCurrentTableLectureRepository.removeLectureSearchedResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "삭제 실패"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onToggleLectureContained(lecture1)
            assertEquals(BookmarkUiEvent.ShowToast("삭제 실패"), awaitItem())
        }
    }

    @Test
    fun `미포함 강의에 onToggleLectureContained 호출 시 addLecture가 호출된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeCurrentTableLectureRepository.addLectureResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.onToggleLectureContained(lecture1)

        assertEquals(lecture1 to false, fakeCurrentTableLectureRepository.addLectureCalledWith)
    }

    @Test
    fun `미포함 강의 addLecture 성공 시 해당 강의 선택이 토글된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeCurrentTableLectureRepository.addLectureResult = Result.Success(Unit)
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onToggleLectureContained(lecture1)

        assertEquals(
            before.copy(
                selectedLecture = lecture1,
                bookmarkList = before.bookmarkList.map { item ->
                    item.copy(state = item.state.copy(selected = item.item.id == lecture1.id))
                },
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `미포함 강의 addLecture 실패 시 LectureOverlap이면 LectureTimeOverlap 다이얼로그가 열린다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeCurrentTableLectureRepository.addLectureResult =
            Result.Fail(LectureOverlap(displayTitle = "", displayMessage = "시간이 겹칩니다"))
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onToggleLectureContained(lecture1)

        assertEquals(
            before.copy(
                dialogState = BookmarkUiState.DialogState.LectureTimeOverlap(lecture1, "시간이 겹칩니다"),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `미포함 강의 addLecture 실패 시 일반 에러이면 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeCurrentTableLectureRepository.addLectureResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "추가 실패"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onToggleLectureContained(lecture1)
            assertEquals(BookmarkUiEvent.ShowToast("추가 실패"), awaitItem())
        }
    }

    // endregion

    // region onConfirmForceAddLecture

    @Test
    fun `onConfirmForceAddLecture 호출 시 addLecture가 isForced=true로 호출된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        // 먼저 LectureOverlap으로 다이얼로그 열기
        fakeCurrentTableLectureRepository.addLectureResult =
            Result.Fail(LectureOverlap(displayTitle = "", displayMessage = "시간이 겹칩니다"))
        val viewModel = createViewModel()
        viewModel.onToggleLectureContained(lecture1) // LectureTimeOverlap 다이얼로그 열림

        fakeCurrentTableLectureRepository.addLectureResult = Result.Success(Unit)
        viewModel.onConfirmForceAddLecture(lecture1)

        assertEquals(lecture1 to true, fakeCurrentTableLectureRepository.addLectureCalledWith)
    }

    @Test
    fun `onConfirmForceAddLecture 성공 시 다이얼로그가 닫히고 선택이 토글된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        // 먼저 LectureOverlap으로 다이얼로그 열기
        fakeCurrentTableLectureRepository.addLectureResult =
            Result.Fail(LectureOverlap(displayTitle = "", displayMessage = "시간이 겹칩니다"))
        val viewModel = createViewModel()
        viewModel.onToggleLectureContained(lecture1)
        val before = viewModel.uiState.value as BookmarkUiState.Success

        fakeCurrentTableLectureRepository.addLectureResult = Result.Success(Unit)
        viewModel.onConfirmForceAddLecture(lecture1)

        assertEquals(
            before.copy(
                dialogState = BookmarkUiState.DialogState.None,
                selectedLecture = lecture1,
                bookmarkList = before.bookmarkList.map { item ->
                    item.copy(state = item.state.copy(selected = item.item.id == lecture1.id))
                },
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onDismissDialog

    @Test
    fun `onDismissDialog 호출 시 다이얼로그가 닫힌다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()
        viewModel.onClickBookmark(lecture1) // DeleteBookmark 다이얼로그 열기
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onDismissDialog()

        assertEquals(
            before.copy(dialogState = BookmarkUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region openLectureDetailSheet

    @Test
    fun `openLectureDetailSheet 호출 시 bottomSheetType이 LectureDetail로 변경되고 buildings가 로드된다`() = runTest {
        val testBuilding = building(buildingNumber = "302", buildingNameKor = "제2공학관")
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeLectureInfoRepository.getBuildingsResult = Result.Success(listOf(testBuilding))
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.openLectureDetailSheet(lecture1)

        assertEquals(
            before.copy(
                bottomSheetType = BookmarkUiState.BottomSheetType.LectureDetail(
                    lecture = lecture1,
                    buildings = listOf(testBuilding),
                    isBookmarked = true,
                    isVacancyRegistered = false,
                ),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `openLectureDetailSheet 호출 시 OpenBottomSheet 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeLectureInfoRepository.getBuildingsResult = Result.Success(emptyList())
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.openLectureDetailSheet(lecture1)
            assertEquals(BookmarkUiEvent.OpenBottomSheet, awaitItem())
        }
    }

    // endregion

    // region closeBottomSheet

    @Test
    fun `closeBottomSheet 호출 시 CloseBottomSheet 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.closeBottomSheet()
            assertEquals(BookmarkUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    // endregion

    // region onSheetDismissed

    @Test
    fun `onSheetDismissed 호출 시 bottomSheetType이 None으로 변경된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeLectureInfoRepository.getBuildingsResult = Result.Success(emptyList())
        val viewModel = createViewModel()
        viewModel.openLectureDetailSheet(lecture1) // 바텀시트 열기
        val before = viewModel.uiState.value as BookmarkUiState.Success

        viewModel.onSheetDismissed()

        assertEquals(
            before.copy(bottomSheetType = BookmarkUiState.BottomSheetType.None),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region openSyllabus

    @Test
    fun `openSyllabus 성공 시 OpenUrl 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeLectureInfoRepository.getSyllabusUrlResult = Result.Success("https://sugang.snu.ac.kr/syllabus/123")
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.openSyllabus(lecture1)
            assertEquals(BookmarkUiEvent.OpenUrl("https://sugang.snu.ac.kr/syllabus/123"), awaitItem())
        }
    }

    @Test
    fun `openSyllabus 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(1, 5, 10, 20, false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(true, false, true, false)
        fakeTableDisplayRepository.compactMode.value = true
        fakeThemeRepository.customThemes.value = emptyList()
        fakeNotificationRepository.notificationCount.value = 5L
        fakeRemoteConfig.disableMapFeature.value = false
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeBookmarkRepository.fetchBookmarksResult = Result.Success(emptyList())
        fakeBookmarkRepository.bookmarks.value = mapOf(courseBook2025_1 to listOf(lecture1))
        fakeLectureInfoRepository.getSyllabusUrlResult = Result.Fail(Unknown(displayTitle = "", displayMessage = "강의계획서 조회 실패"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.openSyllabus(lecture1)
            assertEquals(BookmarkUiEvent.ShowToast("강의계획서 조회 실패"), awaitItem())
        }
    }

    // endregion
}
