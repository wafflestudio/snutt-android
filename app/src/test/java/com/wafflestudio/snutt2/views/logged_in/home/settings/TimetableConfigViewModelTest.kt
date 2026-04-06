package com.wafflestudio.snutt2.views.logged_in.home.settings

import app.cash.turbine.test
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeTableDisplayRepository
import com.wafflestudio.snutt2.fake.FakeTableRepository
import com.wafflestudio.snutt2.fake.FakeThemeRepository
import com.wafflestudio.snutt2.fake.FakeUserRepository
import com.wafflestudio.snutt2.fixture.TestFixtures.table
import com.wafflestudio.snutt2.fixture.TestFixtures.tableSummary
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.domainmodel.ThemeReference
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.Unknown
import com.wafflestudio.snutt2.lib.network.WrongUserToken
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
class TimetableConfigViewModelTest {

    private lateinit var fakeTableDisplayRepository: FakeTableDisplayRepository
    private lateinit var fakeTableRepository: FakeTableRepository
    private lateinit var fakeThemeRepository: FakeThemeRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeTableDisplayRepository = FakeTableDisplayRepository()
        fakeTableRepository = FakeTableRepository()
        fakeThemeRepository = FakeThemeRepository()
        fakeUserRepository = FakeUserRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = TimetableConfigViewModel(
        tableDisplayRepository = fakeTableDisplayRepository,
        userRepository = fakeUserRepository,
        tableRepository = fakeTableRepository,
        getCurrentTableThemeUseCase = GetCurrentTableThemeUseCase(
            themeRepository = fakeThemeRepository,
            tableRepository = fakeTableRepository,
        ),
        displayMessageResolver = fakeDisplayMessageResolver,
    )

    // region init — combine(tableTrimParam, compactMode, tableLectureCustom, currentTable, theme)

    @Test
    fun `init 시 source들이 UiState에 반영된다`() = runTest {
        val trimParam = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.tableTrimParam.value = trimParam
        fakeTableDisplayRepository.compactMode.value = true
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom(
            title = true, place = false, lectureNumber = true, instructor = false,
        )
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t1"),
            themeRef = ThemeReference.BuiltIn(0), // → BuiltInTheme.SNUTT
        )

        val viewModel = createViewModel()

        assertEquals(
            TimeTableConfigUiState(
                tableTrimParam = trimParam,
                compactMode = true,
                tableLectureCustom = TableLectureCustom(
                    title = true, place = false, lectureNumber = true, instructor = false,
                ),
                lectures = emptyList(),
                theme = BuiltInTheme.SNUTT,
                fittedTrimParam = trimParam, // forceFitLectures=false → tableTrimParam 그대로
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `forceFitLectures가 true이면 fittedTrimParam이 강의 기반으로 계산된다`() = runTest {
        val trimParam = TableTrimParam(0, 4, 9, 18, forceFitLectures = true)
        fakeTableDisplayRepository.tableTrimParam.value = trimParam
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        val currentTable = table(
            summary = tableSummary(id = "t1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeTableRepository.currentTable.value = currentTable

        val viewModel = createViewModel()

        val expectedFitted = currentTable.lectures.getFittingTrimParam(TableTrimParam.Default)
        assertEquals(
            TimeTableConfigUiState(
                tableTrimParam = trimParam,
                compactMode = false,
                tableLectureCustom = TableLectureCustom.Default,
                lectures = emptyList(),
                theme = BuiltInTheme.SNUTT,
                fittedTrimParam = expectedFitted,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `currentTable이 null이면 combine이 emit하지 않아 Default 상태를 유지한다`() = runTest {
        fakeTableRepository.currentTable.value = null

        val viewModel = createViewModel()

        assertEquals(TimeTableConfigUiState.Default, viewModel.uiState.value)
    }

    // endregion

    // region Source 반응 — tableTrimParam 변경 시 fittedTrimParam 재계산

    @Test
    fun `tableTrimParam이 변경되면 fittedTrimParam이 재계산된다`() = runTest {
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        val newTrimParam = TableTrimParam(0, 6, 8, 22, forceFitLectures = false)
        fakeTableDisplayRepository.tableTrimParam.value = newTrimParam

        assertEquals(
            before.copy(
                tableTrimParam = newTrimParam,
                fittedTrimParam = newTrimParam, // forceFit=false → 그대로
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — compactMode 변경

    @Test
    fun `compactMode가 변경되면 UiState에 반영된다`() = runTest {
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam.Default
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        fakeTableDisplayRepository.compactMode.value = true

        assertEquals(
            before.copy(compactMode = true),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — tableLectureCustomOption 변경

    @Test
    fun `tableLectureCustomOption이 변경되면 UiState에 반영된다`() = runTest {
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam.Default
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        val newCustom = TableLectureCustom(title = true, place = false, lectureNumber = true, instructor = false)
        fakeTableDisplayRepository.tableLectureCustomOption.value = newCustom

        assertEquals(
            before.copy(tableLectureCustom = newCustom),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — currentTable 변경 시 lectures 갱신

    @Test
    fun `currentTable이 변경되면 lectures가 갱신된다`() = runTest {
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        val newTable = table(
            summary = tableSummary(id = "t2"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeTableRepository.currentTable.value = newTable

        assertEquals(
            before.copy(lectures = newTable.lectures),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — theme 변경

    @Test
    fun `currentTable의 themeRef가 변경되면 theme이 갱신된다`() = runTest {
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam(0, 4, 9, 18, forceFitLectures = false)
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t1"),
            themeRef = ThemeReference.BuiltIn(0), // SNUTT
        )
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t1"),
            themeRef = ThemeReference.BuiltIn(1), // MODERN
        )

        assertEquals(
            before.copy(theme = BuiltInTheme.MODERN),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region toggleAutoTrim

    @Test
    fun `toggleAutoTrim 호출 시 repository의 toggleForceFit을 호출한다`() = runTest {
        fakeTableDisplayRepository.toggleForceFitResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.toggleAutoTrim()

        assertEquals(true, fakeTableDisplayRepository.toggleForceFitCalled)
    }

    @Test
    fun `toggleAutoTrim 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableDisplayRepository.toggleForceFitResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.toggleAutoTrim()
            assertEquals(TimetableConfigUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    @Test
    fun `toggleAutoTrim AuthError 실패 시 ShowToast와 NavigateToOnboard 이벤트가 순서대로 발생한다`() = runTest {
        fakeTableDisplayRepository.toggleForceFitResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.toggleAutoTrim()
            assertEquals(TimetableConfigUiEvent.ShowToast("인증 만료"), awaitItem())
            assertEquals(TimetableConfigUiEvent.NavigateToOnboard, awaitItem())
        }
    }

    @Test
    fun `toggleAutoTrim AuthError 실패 시 performLogout이 호출된다`() = runTest {
        fakeTableDisplayRepository.toggleForceFitResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        val viewModel = createViewModel()

        viewModel.toggleAutoTrim()

        assertEquals(true, fakeUserRepository.performLogoutCalled)
    }

    // endregion

    // region setDayOfWeekRange

    @Test
    fun `setDayOfWeekRange 호출 시 repository를 호출한다`() = runTest {
        fakeTableDisplayRepository.setDayOfWeekRangeResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.setDayOfWeekRange(1, 5)

        assertEquals(1 to 5, fakeTableDisplayRepository.setDayOfWeekRangeCalledWith)
    }

    @Test
    fun `setDayOfWeekRange 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableDisplayRepository.setDayOfWeekRangeResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.setDayOfWeekRange(1, 5)
            assertEquals(TimetableConfigUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region setHourRange

    @Test
    fun `setHourRange 호출 시 repository를 호출한다`() = runTest {
        fakeTableDisplayRepository.setHourRangeResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.setHourRange(8, 22)

        assertEquals(8 to 22, fakeTableDisplayRepository.setHourRangeCalledWith)
    }

    @Test
    fun `setHourRange 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableDisplayRepository.setHourRangeResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.setHourRange(8, 22)
            assertEquals(TimetableConfigUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region toggleCompactMode

    @Test
    fun `toggleCompactMode 호출 시 repository를 호출한다`() = runTest {
        fakeTableDisplayRepository.toggleCompactModeResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.toggleCompactMode()

        assertEquals(true, fakeTableDisplayRepository.toggleCompactModeCalled)
    }

    @Test
    fun `toggleCompactMode 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableDisplayRepository.toggleCompactModeResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.toggleCompactMode()
            assertEquals(TimetableConfigUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region toggleTitleVisible

    @Test
    fun `toggleTitleVisible 호출 시 repository를 호출한다`() = runTest {
        fakeTableDisplayRepository.toggleTitleVisibleResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.toggleTitleVisible()

        assertEquals(true, fakeTableDisplayRepository.toggleTitleVisibleCalled)
    }

    @Test
    fun `toggleTitleVisible 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableDisplayRepository.toggleTitleVisibleResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.toggleTitleVisible()
            assertEquals(TimetableConfigUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region togglePlaceVisible

    @Test
    fun `togglePlaceVisible 호출 시 repository를 호출한다`() = runTest {
        fakeTableDisplayRepository.togglePlaceVisibleResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.togglePlaceVisible()

        assertEquals(true, fakeTableDisplayRepository.togglePlaceVisibleCalled)
    }

    @Test
    fun `togglePlaceVisible 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableDisplayRepository.togglePlaceVisibleResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.togglePlaceVisible()
            assertEquals(TimetableConfigUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region toggleLectureNumberVisible

    @Test
    fun `toggleLectureNumberVisible 호출 시 repository를 호출한다`() = runTest {
        fakeTableDisplayRepository.toggleLectureNumberVisibleResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.toggleLectureNumberVisible()

        assertEquals(true, fakeTableDisplayRepository.toggleLectureNumberVisibleCalled)
    }

    @Test
    fun `toggleLectureNumberVisible 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableDisplayRepository.toggleLectureNumberVisibleResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.toggleLectureNumberVisible()
            assertEquals(TimetableConfigUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region toggleInstructorVisible

    @Test
    fun `toggleInstructorVisible 호출 시 repository를 호출한다`() = runTest {
        fakeTableDisplayRepository.toggleInstructorVisibleResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.toggleInstructorVisible()

        assertEquals(true, fakeTableDisplayRepository.toggleInstructorVisibleCalled)
    }

    @Test
    fun `toggleInstructorVisible 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableDisplayRepository.toggleInstructorVisibleResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.toggleInstructorVisible()
            assertEquals(TimetableConfigUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion
}
