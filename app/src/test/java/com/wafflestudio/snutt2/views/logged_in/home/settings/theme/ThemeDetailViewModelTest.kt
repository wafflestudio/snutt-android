package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.EditingTheme
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.domainmodel.ThemeReference
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeTableDisplayRepository
import com.wafflestudio.snutt2.fake.FakeTableRepository
import com.wafflestudio.snutt2.fake.FakeThemeRepository
import com.wafflestudio.snutt2.fake.FakeUserRepository
import com.wafflestudio.snutt2.fixture.TestFixtures.customTheme
import com.wafflestudio.snutt2.fixture.TestFixtures.table
import com.wafflestudio.snutt2.fixture.TestFixtures.tableSummary
import com.wafflestudio.snutt2.domainmodel.ThemeColor
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.Unknown
import com.wafflestudio.snutt2.domain.WrongUserToken
import com.wafflestudio.snutt2.lib.toDataWithState
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
class ThemeDetailViewModelTest {

    private lateinit var fakeThemeRepository: FakeThemeRepository
    private lateinit var fakeTableRepository: FakeTableRepository
    private lateinit var fakeTableDisplayRepository: FakeTableDisplayRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeThemeRepository = FakeThemeRepository()
        fakeTableRepository = FakeTableRepository()
        fakeTableDisplayRepository = FakeTableDisplayRepository()
        fakeUserRepository = FakeUserRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        themeId: String = "",
        themeCode: Int = -1,
    ): ThemeDetailViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf("themeId" to themeId, "theme" to themeCode),
        )
        return ThemeDetailViewModel(
            savedStateHandle = savedStateHandle,
            themeRepository = fakeThemeRepository,
            tableRepository = fakeTableRepository,
            tableDisplayRepository = fakeTableDisplayRepository,
            userRepository = fakeUserRepository,
            getCurrentTableThemeUseCase = GetCurrentTableThemeUseCase(
                themeRepository = fakeThemeRepository,
                tableRepository = fakeTableRepository,
            ),
            displayMessageResolver = fakeDisplayMessageResolver,
        )
    }

    // region init — SavedStateHandle 파라미터별 초기 상태

    @Test
    fun `커스텀 테마 id로 열면 해당 테마의 EditingTheme로 Success가 된다`() = runTest {
        val theme = customTheme(id = "my-1", name = "내 테마")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0), // → UseCase가 BuiltInTheme.SNUTT 반환
        )
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam.Default
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.compactMode.value = false

        val viewModel = createViewModel(themeId = "my-1")

        val editingTheme = EditingTheme.fromTableTheme(theme)
        assertEquals(
            ThemeDetailUiState.Success(
                editingTheme = editingTheme,
                lectures = emptyList(),
                theme = BuiltInTheme.SNUTT,
                previewTheme = editingTheme.toTableTheme(),
                fittedTrimParam = TableTrimParam.Default,
                tableLectureCustomOptions = TableLectureCustom.Default,
                compactMode = false,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `빌트인 테마 code로 열면 해당 빌트인의 EditingTheme로 Success가 된다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam.Default
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.compactMode.value = false

        val viewModel = createViewModel(themeCode = BuiltInTheme.SNUTT.code)

        val editingTheme = EditingTheme.fromTableTheme(BuiltInTheme.SNUTT)
        assertEquals(
            ThemeDetailUiState.Success(
                editingTheme = editingTheme,
                lectures = emptyList(),
                theme = BuiltInTheme.SNUTT,
                previewTheme = editingTheme.toTableTheme(),
                fittedTrimParam = TableTrimParam.Default,
                tableLectureCustomOptions = TableLectureCustom.Default,
                compactMode = false,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `새 테마 생성 모드로 열면 CustomTheme_Default의 EditingTheme로 Success가 된다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam.Default
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.compactMode.value = false

        val viewModel = createViewModel()

        val editingTheme = EditingTheme.fromTableTheme(CustomTheme.Default)
        assertEquals(
            ThemeDetailUiState.Success(
                editingTheme = editingTheme,
                lectures = emptyList(),
                theme = BuiltInTheme.SNUTT,
                previewTheme = editingTheme.toTableTheme(),
                fittedTrimParam = TableTrimParam.Default,
                tableLectureCustomOptions = TableLectureCustom.Default,
                compactMode = false,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `currentTable이 null이면 Loading 상태를 유지한다`() = runTest {
        fakeTableRepository.currentTable.value = null

        val viewModel = createViewModel()

        assertEquals(ThemeDetailUiState.Loading, viewModel.uiState.value)
    }

    // endregion

    // region Source 반응 — compactMode

    @Test
    fun `compactMode가 변화하면 UiState에 반영된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        fakeTableDisplayRepository.compactMode.value = true

        assertEquals(
            before.copy(compactMode = true),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — tableLectureCustomOption

    @Test
    fun `tableLectureCustomOption이 변화하면 UiState에 반영된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success
        val newCustomOption = TableLectureCustom(title = true, place = false, lectureNumber = true, instructor = true)

        fakeTableDisplayRepository.tableLectureCustomOption.value = newCustomOption

        assertEquals(
            before.copy(tableLectureCustomOptions = newCustomOption),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — tableTrimParam

    @Test
    fun `tableTrimParam이 변화하면 fittedTrimParam이 갱신된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam.Default
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success
        val newTrimParam = TableTrimParam(
            dayOfWeekFrom = 0, dayOfWeekTo = 6, hourFrom = 8, hourTo = 22, forceFitLectures = false,
        )

        fakeTableDisplayRepository.tableTrimParam.value = newTrimParam

        assertEquals(
            before.copy(
                fittedTrimParam = newTrimParam, // forceFitLectures=false → trimParam 그대로 사용
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `forceFitLectures가 true이면 fittedTrimParam이 lectures 기반으로 계산된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam.Default
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        val forceFitTrimParam = TableTrimParam(
            dayOfWeekFrom = 0, dayOfWeekTo = 6, hourFrom = 8, hourTo = 22, forceFitLectures = true,
        )
        fakeTableDisplayRepository.tableTrimParam.value = forceFitTrimParam

        // lectures가 비어있으므로 getFittingTrimParam은 Default 범위 + forceFitLectures=true
        val expectedFittedTrimParam = TableTrimParam(
            dayOfWeekFrom = TableTrimParam.Default.dayOfWeekFrom,
            dayOfWeekTo = TableTrimParam.Default.dayOfWeekTo,
            hourFrom = TableTrimParam.Default.hourFrom,
            hourTo = TableTrimParam.Default.hourTo,
            forceFitLectures = true,
        )
        assertEquals(
            before.copy(fittedTrimParam = expectedFittedTrimParam),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — currentTable

    @Test
    fun `currentTable이 변화하면 lectures가 갱신된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-2"),
            themeRef = ThemeReference.BuiltIn(0),
        )

        assertEquals(
            before.copy(lectures = emptyList()),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — getCurrentTableThemeUseCase (themeRef 변경)

    @Test
    fun `currentTable의 themeRef가 변경되면 theme이 갱신된다`() = runTest {
        val myTheme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = myTheme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0), // → BuiltInTheme.SNUTT
        )
        val viewModel = createViewModel(themeId = "my-1")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(BuiltInTheme.MODERN.code), // → BuiltInTheme.MODERN
        )

        assertEquals(
            before.copy(theme = BuiltInTheme.MODERN),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — dialogState/editingTheme 보존

    @Test
    fun `source가 변화해도 dialogState가 보존된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam.Default
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        viewModel.updateName("변경됨")
        viewModel.onClickBack() // ConfirmCancelEdit 다이얼로그 열림
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        fakeTableDisplayRepository.compactMode.value = true

        assertEquals(
            before.copy(compactMode = true),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `source가 변화해도 editingTheme이 보존된다`() = runTest {
        val theme = customTheme(id = "my-1", name = "원래 이름")
        fakeThemeRepository.getThemeResult = theme
        fakeTableDisplayRepository.compactMode.value = false
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam.Default
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        viewModel.updateName("수정된 이름")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        fakeTableDisplayRepository.compactMode.value = true

        assertEquals(
            before.copy(compactMode = true),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region updateName

    @Test
    fun `updateName 호출 시 editingTheme의 name이 변경된다`() = runTest {
        val theme = customTheme(id = "my-1", name = "원래 이름")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        viewModel.updateName("새 이름")

        val expectedEditingTheme = before.editingTheme.copy(name = "새 이름")
        assertEquals(
            before.copy(
                editingTheme = expectedEditingTheme,
                previewTheme = expectedEditingTheme.toTableTheme(),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `빌트인 테마에서 updateName 호출 시 상태가 변하지 않는다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeCode = BuiltInTheme.SNUTT.code)
        val before = viewModel.uiState.value

        viewModel.updateName("새 이름")

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion

    // region addColor / removeColor / updateColor / duplicateColor / toggleColorExpanded

    @Test
    fun `addColor 호출 시 colors에 항목이 추가된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        viewModel.addColor()

        val expectedEditingTheme = before.editingTheme.copy(
            colors = before.editingTheme.colors + CustomTheme.Default.getColors().first().toDataWithState(true),
        )
        assertEquals(
            before.copy(
                editingTheme = expectedEditingTheme,
                previewTheme = expectedEditingTheme.toTableTheme(),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `removeColor 호출 시 해당 인덱스의 색이 제거된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        viewModel.addColor() // 2개로 만든 후
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        viewModel.removeColor(0)

        val expectedEditingTheme = before.editingTheme.copy(
            colors = before.editingTheme.colors.toMutableList().apply { removeAt(0) },
        )
        assertEquals(
            before.copy(
                editingTheme = expectedEditingTheme,
                previewTheme = expectedEditingTheme.toTableTheme(),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `updateColor 호출 시 해당 인덱스의 색이 교체된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success
        val newFg = 0xFF112233.toInt()
        val newBg = 0xFF445566.toInt()

        viewModel.updateColor(0, newFg, newBg)

        val expectedEditingTheme = before.editingTheme.copy(
            colors = before.editingTheme.colors.toMutableList().apply {
                set(0, ThemeColor(newFg, newBg).toDataWithState(get(0).state))
            },
        )
        assertEquals(
            before.copy(
                editingTheme = expectedEditingTheme,
                previewTheme = expectedEditingTheme.toTableTheme(),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `duplicateColor 호출 시 해당 인덱스 다음에 복제된 색이 추가된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        viewModel.duplicateColor(0)

        val expectedEditingTheme = before.editingTheme.copy(
            colors = before.editingTheme.colors.toMutableList().apply {
                add(1, get(0).copy(state = false))
            },
        )
        assertEquals(
            before.copy(
                editingTheme = expectedEditingTheme,
                previewTheme = expectedEditingTheme.toTableTheme(),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `빌트인 테마에서 addColor 호출 시 상태가 변하지 않는다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeTableDisplayRepository.tableTrimParam.value = TableTrimParam.Default
        fakeTableDisplayRepository.tableLectureCustomOption.value = TableLectureCustom.Default
        fakeTableDisplayRepository.compactMode.value = false
        val viewModel = createViewModel(themeCode = BuiltInTheme.SNUTT.code)
        val before = viewModel.uiState.value

        viewModel.addColor()

        assertEquals(before, viewModel.uiState.value)
    }

    @Test
    fun `toggleColorExpanded 호출 시 해당 인덱스의 expanded가 토글된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        viewModel.toggleColorExpanded(0)

        val expectedEditingTheme = before.editingTheme.copy(
            colors = before.editingTheme.colors.mapIndexed { i, c ->
                if (i == 0) c.copy(state = !c.state) else c
            },
        )
        assertEquals(
            before.copy(
                editingTheme = expectedEditingTheme,
                previewTheme = expectedEditingTheme.toTableTheme(),
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onClickBack — 변경 감지 기반 분기

    @Test
    fun `변경이 없을 때 onClickBack 호출 시 NavigateBack 이벤트가 발생한다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")

        viewModel.uiEvent.test {
            viewModel.onClickBack()
            assertEquals(ThemeDetailUiEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `변경이 있을 때 onClickBack 호출 시 ConfirmCancelEdit 다이얼로그가 열린다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        viewModel.updateName("변경됨")
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        viewModel.onClickBack()

        assertEquals(
            before.copy(dialogState = ThemeDetailUiState.DialogState.ConfirmCancelEdit),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onConfirmCancelEdit

    @Test
    fun `onConfirmCancelEdit 호출 시 NavigateBack 이벤트가 발생한다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        viewModel.updateName("변경됨")
        viewModel.onClickBack()

        viewModel.uiEvent.test {
            viewModel.onConfirmCancelEdit()
            assertEquals(ThemeDetailUiEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onConfirmCancelEdit 호출 시 다이얼로그가 닫힌다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        viewModel.updateName("변경됨")
        viewModel.onClickBack()
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        viewModel.onConfirmCancelEdit()

        assertEquals(
            before.copy(dialogState = ThemeDetailUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onDismissCancelEdit

    @Test
    fun `onDismissCancelEdit 호출 시 다이얼로그만 닫힌다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        viewModel.updateName("변경됨")
        viewModel.onClickBack()
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        viewModel.onDismissCancelEdit()

        assertEquals(
            before.copy(dialogState = ThemeDetailUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onSaveTheme — 기존 테마 수정

    @Test
    fun `기존 커스텀 테마 저장 시 updateTheme을 호출한다`() = runTest {
        val theme = customTheme(id = "my-1", name = "내 테마")
        fakeThemeRepository.getThemeResult = theme
        val updatedTheme = customTheme(id = "my-1", name = "수정된 이름")
        fakeThemeRepository.updateThemeResult = Result.Success(updatedTheme)
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")
        viewModel.updateName("수정된 이름")

        viewModel.onSaveTheme()

        assertEquals(
            Triple("my-1", "수정된 이름", theme.getColors()),
            fakeThemeRepository.updateThemeCalledWith,
        )
    }

    @Test
    fun `기존 커스텀 테마 저장 성공 시 NavigateBack 이벤트가 발생한다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeThemeRepository.updateThemeResult = Result.Success(theme)
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")

        viewModel.uiEvent.test {
            viewModel.onSaveTheme()
            assertEquals(ThemeDetailUiEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `기존 테마가 현재 시간표에 적용 중이면 저장 후 fetchAndSelectTable이 호출된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeThemeRepository.updateThemeResult = Result.Success(theme)
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.Custom("my-1"),
        )
        val viewModel = createViewModel(themeId = "my-1")

        viewModel.onSaveTheme()

        assertEquals("table-1", fakeTableRepository.fetchAndSelectTableCalledWith)
    }

    @Test
    fun `onSaveTheme 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeThemeRepository.updateThemeResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")

        viewModel.uiEvent.test {
            viewModel.onSaveTheme()
            assertEquals(ThemeDetailUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    @Test
    fun `onSaveTheme AuthError 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeThemeRepository.updateThemeResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")

        viewModel.uiEvent.test {
            viewModel.onSaveTheme()
            assertEquals(ThemeDetailUiEvent.ShowToast("인증 만료"), awaitItem())
        }
    }

    @Test
    fun `onSaveTheme AuthError 실패 시 postForceLogout이 호출된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.getThemeResult = theme
        fakeThemeRepository.updateThemeResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel(themeId = "my-1")

        viewModel.onSaveTheme()

        assertEquals(true, fakeUserRepository.postForceLogoutCalled)
    }

    // endregion

    // region onSaveTheme — 새 테마 생성

    @Test
    fun `새 테마 저장 시 createTheme을 호출한다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeThemeRepository.createThemeResult = Result.Success(customTheme(id = "new-1", name = "새 테마"))
        val viewModel = createViewModel()

        viewModel.onSaveTheme()

        assertEquals(
            CustomTheme.Default.name to CustomTheme.Default.getColors(),
            fakeThemeRepository.createThemeCalledWith,
        )
    }

    @Test
    fun `새 테마 저장 성공 시 ConfirmApplyToTable 다이얼로그가 열린다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val newTheme = customTheme(id = "new-1")
        fakeThemeRepository.createThemeResult = Result.Success(newTheme)
        val viewModel = createViewModel()
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        viewModel.onSaveTheme()

        val expectedEditingTheme = EditingTheme.fromTableTheme(newTheme)
        assertEquals(
            before.copy(
                editingTheme = expectedEditingTheme,
                previewTheme = expectedEditingTheme.toTableTheme(),
                dialogState = ThemeDetailUiState.DialogState.ConfirmApplyToTable,
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onConfirmApplyToTable

    @Test
    fun `onConfirmApplyToTable 호출 시 NavigateBack 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val newTheme = customTheme(id = "new-1")
        fakeThemeRepository.createThemeResult = Result.Success(newTheme)
        val viewModel = createViewModel()
        viewModel.onSaveTheme()

        viewModel.uiEvent.test {
            viewModel.onConfirmApplyToTable()
            assertEquals(ThemeDetailUiEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onConfirmApplyToTable 호출 시 updateTableTheme을 호출한다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val newTheme = customTheme(id = "new-1")
        fakeThemeRepository.createThemeResult = Result.Success(newTheme)
        val viewModel = createViewModel()
        viewModel.onSaveTheme()

        viewModel.onConfirmApplyToTable()

        assertEquals("table-1" to "new-1", fakeTableRepository.updateTableThemeCustomCalledWith)
    }

    @Test
    fun `onConfirmApplyToTable 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val newTheme = customTheme(id = "new-1")
        fakeThemeRepository.createThemeResult = Result.Success(newTheme)
        fakeTableRepository.updateTableThemeCustomResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()
        viewModel.onSaveTheme()

        viewModel.uiEvent.test {
            viewModel.onConfirmApplyToTable()
            assertEquals(ThemeDetailUiEvent.ShowToast("에러"), awaitItem())
            assertEquals(ThemeDetailUiEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onConfirmApplyToTable AuthError 실패 시 postForceLogout이 호출된다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val newTheme = customTheme(id = "new-1")
        fakeThemeRepository.createThemeResult = Result.Success(newTheme)
        fakeTableRepository.updateTableThemeCustomResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        val viewModel = createViewModel()
        viewModel.onSaveTheme()

        viewModel.onConfirmApplyToTable()

        assertEquals(true, fakeUserRepository.postForceLogoutCalled)
    }

    // endregion

    // region onDismissApplyToTable

    @Test
    fun `onDismissApplyToTable 호출 시 NavigateBack 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeThemeRepository.createThemeResult = Result.Success(customTheme(id = "new-1"))
        val viewModel = createViewModel()
        viewModel.onSaveTheme()

        viewModel.uiEvent.test {
            viewModel.onDismissApplyToTable()
            assertEquals(ThemeDetailUiEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onDismissApplyToTable 호출 시 다이얼로그가 닫힌다`() = runTest {
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeThemeRepository.createThemeResult = Result.Success(customTheme(id = "new-1"))
        val viewModel = createViewModel()
        viewModel.onSaveTheme()
        val before = viewModel.uiState.value as ThemeDetailUiState.Success

        viewModel.onDismissApplyToTable()

        assertEquals(
            before.copy(dialogState = ThemeDetailUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    // endregion
}
