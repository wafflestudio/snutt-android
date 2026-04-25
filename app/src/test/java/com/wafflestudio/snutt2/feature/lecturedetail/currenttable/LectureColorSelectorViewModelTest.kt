package com.wafflestudio.snutt2.feature.lecturedetail.currenttable

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.ThemeReference
import com.wafflestudio.snutt2.fake.FakeTableRepository
import com.wafflestudio.snutt2.fake.FakeThemeRepository
import com.wafflestudio.snutt2.fixture.TestFixtures
import com.wafflestudio.snutt2.navigation.NavigationDestination
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
class LectureColorSelectorViewModelTest {

    private lateinit var fakeTableRepository: FakeTableRepository
    private lateinit var fakeThemeRepository: FakeThemeRepository
    private lateinit var getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeTableRepository = FakeTableRepository()
        fakeThemeRepository = FakeThemeRepository()
        getCurrentTableThemeUseCase = GetCurrentTableThemeUseCase(
            themeRepository = fakeThemeRepository,
            tableRepository = fakeTableRepository,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(initialColor: LectureColor) = LectureColorSelectorViewModel(
        savedStateHandle = SavedStateHandle(
            mapOf(NavigationDestination.LectureColorSelector.ARG_COLOR to initialColor),
        ),
        getCurrentTableThemeUseCase = getCurrentTableThemeUseCase,
    )

    private fun setBuiltInTableTheme(code: Int = 0) {
        fakeTableRepository.currentTable.value = TestFixtures.table(themeRef = ThemeReference.BuiltIn(code))
    }

    private fun setCustomTableTheme(theme: CustomTheme) {
        fakeTableRepository.currentTable.value = TestFixtures.table(themeRef = ThemeReference.Custom(theme.id))
        fakeThemeRepository.getThemeResult = theme
    }

    // region init 결과: tableTheme x initialColor 분기

    @Test
    fun `BuiltIn 테마 시간표에 BuiltIn 색이 들어오면 ColorSelection은 Palette, picker 초기값은 Default 가 된다`() = runTest {
        setBuiltInTableTheme(code = 0)

        val viewModel = createViewModel(initialColor = LectureColor.BuiltIn(3))

        assertEquals(
            LectureColorSelectorUiState.BuiltInThemeMode(
                tableTheme = BuiltInTheme.SNUTT,
                selection = LectureColorSelectorUiState.ColorSelection.Palette(3),
                pickerFgColor = LectureColor.Custom.Default.foreground,
                pickerBgColor = LectureColor.Custom.Default.background,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `BuiltIn 테마 시간표에 Custom 색이 들어오면 ColorSelection은 Picker, picker 초기값은 들어온 색이 된다`() = runTest {
        setBuiltInTableTheme(code = 0)

        val viewModel = createViewModel(initialColor = LectureColor.Custom(0x12345678, 0x789ABCDE))

        assertEquals(
            LectureColorSelectorUiState.BuiltInThemeMode(
                tableTheme = BuiltInTheme.SNUTT,
                selection = LectureColorSelectorUiState.ColorSelection.Picker,
                pickerFgColor = 0x12345678,
                pickerBgColor = 0x789ABCDE,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `Custom 테마 시간표에 팔레트와 매칭되는 Custom 색이 들어오면 매칭된 인덱스가 선택된다`() = runTest {
        val customTheme = TestFixtures.customTheme(
            id = "theme-x",
            colors = listOf(
                TestFixtures.themeColor(fg = 0x111, bg = 0x222),
                TestFixtures.themeColor(fg = 0x333, bg = 0x444),
                TestFixtures.themeColor(fg = 0x555, bg = 0x666),
            ),
        )
        setCustomTableTheme(customTheme)

        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x333, background = 0x444))

        assertEquals(
            LectureColorSelectorUiState.CustomThemeMode(
                tableTheme = customTheme,
                selection = LectureColorSelectorUiState.ColorSelection.Palette(1),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `Custom 테마 시간표에 팔레트와 매칭되지 않는 Custom 색이 들어오면 0번 인덱스로 fallback 한다`() = runTest {
        val customTheme = TestFixtures.customTheme(
            colors = listOf(
                TestFixtures.themeColor(fg = 0x111, bg = 0x222),
                TestFixtures.themeColor(fg = 0x333, bg = 0x444),
            ),
        )
        setCustomTableTheme(customTheme)

        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x999, background = 0xAAA))

        assertEquals(
            LectureColorSelectorUiState.CustomThemeMode(
                tableTheme = customTheme,
                selection = LectureColorSelectorUiState.ColorSelection.Palette(0),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `Custom 테마 시간표에 BuiltIn 색이 들어오면 invariant 위반이지만 0번 인덱스로 fallback 한다`() = runTest {
        val customTheme = TestFixtures.customTheme(
            colors = listOf(
                TestFixtures.themeColor(fg = 0x111, bg = 0x222),
                TestFixtures.themeColor(fg = 0x333, bg = 0x444),
            ),
        )
        setCustomTableTheme(customTheme)

        val viewModel = createViewModel(initialColor = LectureColor.BuiltIn(3))

        assertEquals(
            LectureColorSelectorUiState.CustomThemeMode(
                tableTheme = customTheme,
                selection = LectureColorSelectorUiState.ColorSelection.Palette(0),
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onBackPressed

    @Test
    fun `BuiltInThemeMode + Palette 선택 상태에서 onBackPressed 시 BuiltIn 색으로 NavigateBackWithResult 이벤트가 발생한다`() = runTest {
        setBuiltInTableTheme(code = 0)
        val viewModel = createViewModel(initialColor = LectureColor.BuiltIn(2))

        viewModel.uiEvent.test {
            viewModel.onBackPressed()
            assertEquals(
                LectureColorSelectorUiEvent.NavigateBackWithResult(LectureColor.BuiltIn(2)),
                awaitItem(),
            )
        }
    }

    @Test
    fun `BuiltInThemeMode + Picker 선택 상태에서 onBackPressed 시 picker 색으로 NavigateBackWithResult 이벤트가 발생한다`() = runTest {
        setBuiltInTableTheme(code = 0)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x123, background = 0x456))

        viewModel.uiEvent.test {
            viewModel.onBackPressed()
            assertEquals(
                LectureColorSelectorUiEvent.NavigateBackWithResult(LectureColor.Custom(foreground = 0x123, background = 0x456)),
                awaitItem(),
            )
        }
    }

    @Test
    fun `CustomThemeMode 에서 onBackPressed 시 선택된 팔레트 색으로 NavigateBackWithResult 이벤트가 발생한다`() = runTest {
        val customTheme = TestFixtures.customTheme(
            colors = listOf(
                TestFixtures.themeColor(fg = 0x111, bg = 0x222),
                TestFixtures.themeColor(fg = 0x333, bg = 0x444),
            ),
        )
        setCustomTableTheme(customTheme)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x333, background = 0x444))

        viewModel.uiEvent.test {
            viewModel.onBackPressed()
            assertEquals(
                LectureColorSelectorUiEvent.NavigateBackWithResult(LectureColor.Custom(foreground = 0x333, background = 0x444)),
                awaitItem(),
            )
        }
    }

    // endregion

    // region selectPaletteColor

    @Test
    fun `BuiltInThemeMode 에서 selectPaletteColor 호출 시 selection 이 새 Palette 로 변경된다`() = runTest {
        setBuiltInTableTheme(code = 0)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x123, background = 0x456))
        val before = viewModel.uiState.value

        viewModel.selectPaletteColor(5)

        assertEquals(
            (before as LectureColorSelectorUiState.BuiltInThemeMode).copy(
                selection = LectureColorSelectorUiState.ColorSelection.Palette(5),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `CustomThemeMode 에서 selectPaletteColor 호출 시 selection 이 새 Palette 로 변경된다`() = runTest {
        val customTheme = TestFixtures.customTheme(
            colors = listOf(
                TestFixtures.themeColor(fg = 0x111, bg = 0x222),
                TestFixtures.themeColor(fg = 0x333, bg = 0x444),
                TestFixtures.themeColor(fg = 0x555, bg = 0x666),
            ),
        )
        setCustomTableTheme(customTheme)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x111, background = 0x222))
        val before = viewModel.uiState.value

        viewModel.selectPaletteColor(2)

        assertEquals(
            (before as LectureColorSelectorUiState.CustomThemeMode).copy(
                selection = LectureColorSelectorUiState.ColorSelection.Palette(2),
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region selectPickerColor

    @Test
    fun `BuiltInThemeMode 에서 selectPickerColor 호출 시 selection 이 Picker 로 변경된다`() = runTest {
        setBuiltInTableTheme(code = 0)
        val viewModel = createViewModel(initialColor = LectureColor.BuiltIn(0))
        val before = viewModel.uiState.value

        viewModel.selectPickerColor()

        assertEquals(
            (before as LectureColorSelectorUiState.BuiltInThemeMode).copy(
                selection = LectureColorSelectorUiState.ColorSelection.Picker,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `CustomThemeMode 에서 selectPickerColor 호출 시 상태가 변경되지 않는다`() = runTest {
        val customTheme = TestFixtures.customTheme(
            colors = listOf(TestFixtures.themeColor(fg = 0x111, bg = 0x222)),
        )
        setCustomTableTheme(customTheme)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x111, background = 0x222))
        val before = viewModel.uiState.value

        viewModel.selectPickerColor()

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion

    // region openFgPicker

    @Test
    fun `BuiltInThemeMode 에서 openFgPicker 호출 시 dialogState 가 ForegroundPicker 로 변경된다`() = runTest {
        setBuiltInTableTheme(code = 0)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0xABC, background = 0xDEF))
        val before = viewModel.uiState.value

        viewModel.openFgPicker()

        assertEquals(
            (before as LectureColorSelectorUiState.BuiltInThemeMode).copy(
                dialogState = LectureColorSelectorUiState.DialogState.ForegroundPicker(initialColor = 0xABC),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `CustomThemeMode 에서 openFgPicker 호출 시 상태가 변경되지 않는다`() = runTest {
        val customTheme = TestFixtures.customTheme(
            colors = listOf(TestFixtures.themeColor(fg = 0x111, bg = 0x222)),
        )
        setCustomTableTheme(customTheme)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x111, background = 0x222))
        val before = viewModel.uiState.value

        viewModel.openFgPicker()

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion

    // region openBgPicker

    @Test
    fun `BuiltInThemeMode 에서 openBgPicker 호출 시 dialogState 가 BackgroundPicker 로 변경된다`() = runTest {
        setBuiltInTableTheme(code = 0)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0xABC, background = 0xDEF))
        val before = viewModel.uiState.value

        viewModel.openBgPicker()

        assertEquals(
            (before as LectureColorSelectorUiState.BuiltInThemeMode).copy(
                dialogState = LectureColorSelectorUiState.DialogState.BackgroundPicker(initialColor = 0xDEF),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `CustomThemeMode 에서 openBgPicker 호출 시 상태가 변경되지 않는다`() = runTest {
        val customTheme = TestFixtures.customTheme(
            colors = listOf(TestFixtures.themeColor(fg = 0x111, bg = 0x222)),
        )
        setCustomTableTheme(customTheme)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x111, background = 0x222))
        val before = viewModel.uiState.value

        viewModel.openBgPicker()

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion

    // region dismissDialog

    @Test
    fun `BuiltInThemeMode 에서 dismissDialog 호출 시 dialogState 가 None 으로 변경된다`() = runTest {
        setBuiltInTableTheme(code = 0)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0xABC, background = 0xDEF))
        viewModel.openFgPicker()
        val before = viewModel.uiState.value

        viewModel.dismissDialog()

        assertEquals(
            (before as LectureColorSelectorUiState.BuiltInThemeMode).copy(
                dialogState = LectureColorSelectorUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `CustomThemeMode 에서 dismissDialog 호출 시 상태가 변경되지 않는다`() = runTest {
        val customTheme = TestFixtures.customTheme(
            colors = listOf(TestFixtures.themeColor(fg = 0x111, bg = 0x222)),
        )
        setCustomTableTheme(customTheme)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x111, background = 0x222))
        val before = viewModel.uiState.value

        viewModel.dismissDialog()

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion

    // region pickFgColor

    @Test
    fun `BuiltInThemeMode 에서 pickFgColor 호출 시 pickerFgColor 가 갱신되고 selection 은 Picker, dialogState 는 None 이 된다`() = runTest {
        setBuiltInTableTheme(code = 0)
        val viewModel = createViewModel(initialColor = LectureColor.BuiltIn(2))
        viewModel.openFgPicker()
        val before = viewModel.uiState.value

        viewModel.pickFgColor(0xCAFE)

        assertEquals(
            (before as LectureColorSelectorUiState.BuiltInThemeMode).copy(
                pickerFgColor = 0xCAFE,
                selection = LectureColorSelectorUiState.ColorSelection.Picker,
                dialogState = LectureColorSelectorUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `CustomThemeMode 에서 pickFgColor 호출 시 상태가 변경되지 않는다`() = runTest {
        val customTheme = TestFixtures.customTheme(
            colors = listOf(TestFixtures.themeColor(fg = 0x111, bg = 0x222)),
        )
        setCustomTableTheme(customTheme)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x111, background = 0x222))
        val before = viewModel.uiState.value

        viewModel.pickFgColor(0xCAFE)

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion

    // region pickBgColor

    @Test
    fun `BuiltInThemeMode 에서 pickBgColor 호출 시 pickerBgColor 가 갱신되고 selection 은 Picker, dialogState 는 None 이 된다`() = runTest {
        setBuiltInTableTheme(code = 0)
        val viewModel = createViewModel(initialColor = LectureColor.BuiltIn(2))
        viewModel.openBgPicker()
        val before = viewModel.uiState.value

        viewModel.pickBgColor(0xBABE)

        assertEquals(
            (before as LectureColorSelectorUiState.BuiltInThemeMode).copy(
                pickerBgColor = 0xBABE,
                selection = LectureColorSelectorUiState.ColorSelection.Picker,
                dialogState = LectureColorSelectorUiState.DialogState.None,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `CustomThemeMode 에서 pickBgColor 호출 시 상태가 변경되지 않는다`() = runTest {
        val customTheme = TestFixtures.customTheme(
            colors = listOf(TestFixtures.themeColor(fg = 0x111, bg = 0x222)),
        )
        setCustomTableTheme(customTheme)
        val viewModel = createViewModel(initialColor = LectureColor.Custom(foreground = 0x111, background = 0x222))
        val before = viewModel.uiState.value

        viewModel.pickBgColor(0xBABE)

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion
}
