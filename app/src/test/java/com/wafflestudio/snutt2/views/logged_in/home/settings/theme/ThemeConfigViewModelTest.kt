package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import app.cash.turbine.test
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeTableRepository
import com.wafflestudio.snutt2.fake.FakeThemeRepository
import com.wafflestudio.snutt2.fake.FakeUserRepository
import com.wafflestudio.snutt2.fixture.TestFixtures.customTheme
import com.wafflestudio.snutt2.fixture.TestFixtures.table
import com.wafflestudio.snutt2.fixture.TestFixtures.tableSummary
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.Unknown
import com.wafflestudio.snutt2.domain.WrongUserToken
import com.wafflestudio.snutt2.domain.model.ThemeReference
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
class ThemeConfigViewModelTest {

    private lateinit var fakeThemeRepository: FakeThemeRepository
    private lateinit var fakeTableRepository: FakeTableRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeThemeRepository = FakeThemeRepository()
        fakeTableRepository = FakeTableRepository()
        fakeUserRepository = FakeUserRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ThemeConfigViewModel(
        themeRepository = fakeThemeRepository,
        tableRepository = fakeTableRepository,
        userRepository = fakeUserRepository,
        displayMessageResolver = fakeDisplayMessageResolver,
    )

    // region init — fetchThemes + combine(customThemes, builtInThemes)

    @Test
    fun `init 시 fetchThemes가 호출된다`() = runTest {
        createViewModel()

        assertEquals(true, fakeThemeRepository.fetchThemesCalled)
    }

    @Test
    fun `init 시 customThemes가 isFromMarket 기준으로 분류되어 UiState에 반영된다`() = runTest {
        val myTheme = customTheme(id = "my-1", name = "내 테마", isFromMarket = false)
        val marketTheme = customTheme(id = "mkt-1", name = "마켓 테마", isFromMarket = true)
        fakeThemeRepository.customThemes.value = listOf(myTheme, marketTheme)
        fakeThemeRepository.builtInThemes.value = listOf(BuiltInTheme.SNUTT)

        val viewModel = createViewModel()

        assertEquals(
            ThemeConfigUiState(
                myCustomThemes = listOf(myTheme),
                marketCustomThemes = listOf(marketTheme),
                builtInThemes = listOf(BuiltInTheme.SNUTT),
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — customThemes/builtInThemes 변경 시 isFromMarket 분류 재적용

    @Test
    fun `customThemes가 변화하면 myCustomThemes와 marketCustomThemes가 재분류된다`() = runTest {
        val myTheme = customTheme(id = "my-1", isFromMarket = false)
        fakeThemeRepository.customThemes.value = listOf(myTheme)
        fakeThemeRepository.builtInThemes.value = listOf(BuiltInTheme.SNUTT)
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        val newMarketTheme = customTheme(id = "mkt-1", isFromMarket = true)
        fakeThemeRepository.customThemes.value = listOf(myTheme, newMarketTheme)

        assertEquals(
            before.copy(
                myCustomThemes = listOf(myTheme),
                marketCustomThemes = listOf(newMarketTheme),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `customThemes 변화 시 dialogState는 보존된다`() = runTest {
        val myTheme = customTheme(id = "my-1", isFromMarket = false)
        fakeThemeRepository.customThemes.value = listOf(myTheme)
        fakeThemeRepository.builtInThemes.value = emptyList()
        val viewModel = createViewModel()
        viewModel.onClickDelete(myTheme)
        val before = viewModel.uiState.value

        val myTheme2 = customTheme(id = "my-2")
        fakeThemeRepository.customThemes.value = listOf(myTheme, myTheme2)

        assertEquals(
            before.copy(myCustomThemes = listOf(myTheme, myTheme2)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `builtInThemes가 변화하면 UiState에 반영된다`() = runTest {
        fakeThemeRepository.customThemes.value = emptyList()
        fakeThemeRepository.builtInThemes.value = listOf(BuiltInTheme.SNUTT)
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        fakeThemeRepository.builtInThemes.value = listOf(BuiltInTheme.SNUTT, BuiltInTheme.MODERN)

        assertEquals(
            before.copy(builtInThemes = listOf(BuiltInTheme.SNUTT, BuiltInTheme.MODERN)),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onOpenBottomSheet

    @Test
    fun `onOpenBottomSheet에 내 커스텀 테마를 전달하면 MyCustomThemeActions 상태가 된다`() = runTest {
        val viewModel = createViewModel()
        val myTheme = customTheme(id = "my-1", isFromMarket = false)
        val before = viewModel.uiState.value

        viewModel.onOpenBottomSheet(myTheme)

        assertEquals(
            before.copy(
                bottomSheetType = ThemeConfigUiState.BottomSheetType.MyCustomThemeActions(myTheme),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `onOpenBottomSheet에 마켓 테마를 전달하면 MarketCustomThemeActions 상태가 된다`() = runTest {
        val viewModel = createViewModel()
        val marketTheme = customTheme(id = "mkt-1", isFromMarket = true)
        val before = viewModel.uiState.value

        viewModel.onOpenBottomSheet(marketTheme)

        assertEquals(
            before.copy(
                bottomSheetType = ThemeConfigUiState.BottomSheetType.MarketCustomThemeActions(marketTheme),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `onOpenBottomSheet에 빌트인 테마를 전달하면 BuiltInThemeActions 상태가 된다`() = runTest {
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        viewModel.onOpenBottomSheet(BuiltInTheme.SNUTT)

        assertEquals(
            before.copy(
                bottomSheetType = ThemeConfigUiState.BottomSheetType.BuiltInThemeActions(BuiltInTheme.SNUTT),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `onOpenBottomSheet 호출 시 OpenBottomSheet 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onOpenBottomSheet(BuiltInTheme.SNUTT)
            assertEquals(ThemeConfigUiEvent.OpenBottomSheet, awaitItem())
        }
    }

    // endregion

    // region onCloseBottomSheet

    @Test
    fun `onCloseBottomSheet 호출 시 CloseBottomSheet 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onCloseBottomSheet()
            assertEquals(ThemeConfigUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    // endregion

    // region onSheetDismissed

    @Test
    fun `onSheetDismissed 호출 시 bottomSheetType이 None이 된다`() = runTest {
        val viewModel = createViewModel()
        viewModel.onOpenBottomSheet(BuiltInTheme.SNUTT)
        val before = viewModel.uiState.value

        viewModel.onSheetDismissed()

        assertEquals(
            before.copy(bottomSheetType = ThemeConfigUiState.BottomSheetType.None),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onClickDetail

    @Test
    fun `onClickDetail 호출 시 CloseBottomSheet와 NavigateToDetail 이벤트가 순서대로 발생한다`() = runTest {
        val viewModel = createViewModel()
        val theme = customTheme(id = "my-1")

        viewModel.uiEvent.test {
            viewModel.onClickDetail(theme)
            assertEquals(ThemeConfigUiEvent.CloseBottomSheet, awaitItem())
            assertEquals(ThemeConfigUiEvent.NavigateToDetail(theme), awaitItem())
        }
    }

    // endregion

    // region onClickApply

    @Test
    fun `onClickApply에 커스텀 테마를 전달하면 updateTableTheme(themeId)을 호출한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(id = "table-1"))
        val viewModel = createViewModel()

        viewModel.onClickApply(customTheme(id = "my-1"))

        assertEquals("table-1" to "my-1", fakeTableRepository.updateTableThemeCustomCalledWith)
    }

    @Test
    fun `onClickApply에 빌트인 테마를 전달하면 updateTableTheme(code)을 호출한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(id = "table-1"))
        val viewModel = createViewModel()

        viewModel.onClickApply(BuiltInTheme.SNUTT)

        assertEquals("table-1" to BuiltInTheme.SNUTT.code, fakeTableRepository.updateTableThemeBuiltInCalledWith)
    }

    @Test
    fun `onClickApply 호출 시 CloseBottomSheet 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(id = "table-1"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickApply(customTheme(id = "my-1"))
            assertEquals(ThemeConfigUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    @Test
    fun `onClickApply 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(id = "table-1"))
        fakeTableRepository.updateTableThemeCustomResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickApply(customTheme(id = "my-1"))
            assertEquals(ThemeConfigUiEvent.CloseBottomSheet, awaitItem())
            assertEquals(ThemeConfigUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    @Test
    fun `onClickApply AuthError 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(id = "table-1"))
        fakeTableRepository.updateTableThemeCustomResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickApply(customTheme(id = "my-1"))
            assertEquals(ThemeConfigUiEvent.CloseBottomSheet, awaitItem())
            assertEquals(ThemeConfigUiEvent.ShowToast("인증 만료"), awaitItem())
        }
    }

    @Test
    fun `onClickApply AuthError 실패 시 postForceLogout이 호출된다`() = runTest {
        fakeTableRepository.currentTable.value = table(summary = tableSummary(id = "table-1"))
        fakeTableRepository.updateTableThemeCustomResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        val viewModel = createViewModel()

        viewModel.onClickApply(customTheme(id = "my-1"))

        assertEquals(true, fakeUserRepository.postForceLogoutCalled)
    }

    // endregion

    // region onClickDuplicate

    @Test
    fun `onClickDuplicate 호출 시 repository의 copyTheme을 호출한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.onClickDuplicate(customTheme(id = "my-1"))

        assertEquals("my-1", fakeThemeRepository.copyThemeCalledWith)
    }

    @Test
    fun `onClickDuplicate 호출 시 CloseBottomSheet 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickDuplicate(customTheme(id = "my-1"))
            assertEquals(ThemeConfigUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    @Test
    fun `onClickDuplicate 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeThemeRepository.copyThemeResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickDuplicate(customTheme(id = "my-1"))
            assertEquals(ThemeConfigUiEvent.CloseBottomSheet, awaitItem())
            assertEquals(ThemeConfigUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    @Test
    fun `onClickDuplicate AuthError 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeThemeRepository.copyThemeResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickDuplicate(customTheme(id = "my-1"))
            assertEquals(ThemeConfigUiEvent.CloseBottomSheet, awaitItem())
            assertEquals(ThemeConfigUiEvent.ShowToast("인증 만료"), awaitItem())
        }
    }

    @Test
    fun `onClickDuplicate AuthError 실패 시 postForceLogout이 호출된다`() = runTest {
        fakeThemeRepository.copyThemeResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        val viewModel = createViewModel()

        viewModel.onClickDuplicate(customTheme(id = "my-1"))

        assertEquals(true, fakeUserRepository.postForceLogoutCalled)
    }

    // endregion

    // region onClickDelete / onConfirmDeleteTheme

    @Test
    fun `onClickDelete 호출 시 DeleteTheme 다이얼로그가 열린다`() = runTest {
        val viewModel = createViewModel()
        val theme = customTheme(id = "my-1")
        val before = viewModel.uiState.value

        viewModel.onClickDelete(theme)

        assertEquals(
            before.copy(dialogState = ThemeConfigUiState.DialogState.DeleteTheme(theme)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `onConfirmDeleteTheme 호출 시 repository의 deleteTheme을 호출한다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.deleteThemeResult = Result.Success(Unit)
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel()
        viewModel.onClickDelete(theme)

        viewModel.onConfirmDeleteTheme()

        assertEquals("my-1", fakeThemeRepository.deleteThemeCalledWith)
    }

    @Test
    fun `onConfirmDeleteTheme 호출 시 CloseBottomSheet 이벤트가 발생한다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.deleteThemeResult = Result.Success(Unit)
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel()
        viewModel.onClickDelete(theme)

        viewModel.uiEvent.test {
            viewModel.onConfirmDeleteTheme()
            assertEquals(ThemeConfigUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    @Test
    fun `onConfirmDeleteTheme 호출 시 다이얼로그가 닫힌다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.deleteThemeResult = Result.Success(Unit)
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel()
        viewModel.onClickDelete(theme)
        val before = viewModel.uiState.value

        viewModel.onConfirmDeleteTheme()

        assertEquals(
            before.copy(dialogState = ThemeConfigUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `삭제한 테마가 현재 적용 중이면 fetchAndSelectTable이 호출된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.Custom("my-1"),
        )
        val viewModel = createViewModel()
        viewModel.onClickDelete(theme)

        viewModel.onConfirmDeleteTheme()

        assertEquals("table-1", fakeTableRepository.fetchAndSelectTableCalledWith)
    }

    @Test
    fun `삭제한 테마가 현재 적용 중이 아니면 fetchAndSelectTable이 호출되지 않는다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "table-1"),
            themeRef = ThemeReference.BuiltIn(0),
        )
        val viewModel = createViewModel()
        viewModel.onClickDelete(theme)

        viewModel.onConfirmDeleteTheme()

        assertEquals(null, fakeTableRepository.fetchAndSelectTableCalledWith)
    }

    @Test
    fun `onConfirmDeleteTheme 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.deleteThemeResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()
        viewModel.onClickDelete(theme)

        viewModel.uiEvent.test {
            viewModel.onConfirmDeleteTheme()
            assertEquals(ThemeConfigUiEvent.CloseBottomSheet, awaitItem())
            assertEquals(ThemeConfigUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    @Test
    fun `onConfirmDeleteTheme AuthError 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.deleteThemeResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        val viewModel = createViewModel()
        viewModel.onClickDelete(theme)

        viewModel.uiEvent.test {
            viewModel.onConfirmDeleteTheme()
            assertEquals(ThemeConfigUiEvent.CloseBottomSheet, awaitItem())
            assertEquals(ThemeConfigUiEvent.ShowToast("인증 만료"), awaitItem())
        }
    }

    @Test
    fun `onConfirmDeleteTheme AuthError 실패 시 postForceLogout이 호출된다`() = runTest {
        val theme = customTheme(id = "my-1")
        fakeThemeRepository.deleteThemeResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        val viewModel = createViewModel()
        viewModel.onClickDelete(theme)

        viewModel.onConfirmDeleteTheme()

        assertEquals(true, fakeUserRepository.postForceLogoutCalled)
    }

    // endregion

    // region onDismissDialog

    @Test
    fun `onDismissDialog 호출 시 다이얼로그가 닫힌다`() = runTest {
        val viewModel = createViewModel()
        viewModel.onClickDelete(customTheme(id = "my-1"))
        val before = viewModel.uiState.value

        viewModel.onDismissDialog()

        assertEquals(
            before.copy(dialogState = ThemeConfigUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    // endregion
}
