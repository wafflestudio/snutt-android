package com.wafflestudio.snutt2.views.logged_in.home.settings

import com.wafflestudio.snutt2.fake.FakeUserRepository
import com.wafflestudio.snutt2.ui.ThemeMode
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
class ColorModeSelectViewModelTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var viewModel: ColorModeSelectViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeUserRepository = FakeUserRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ColorModeSelectViewModel(
        userRepository = fakeUserRepository,
    )

    // region source: themeMode

    @Test
    fun `init 시 repository의 themeMode가 UiState에 반영된다`() = runTest {
        fakeUserRepository.themeMode.value = ThemeMode.DARK
        viewModel = createViewModel()

        assertEquals(
            ColorModeSelectUiState(themeMode = ThemeMode.DARK),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `repository의 themeMode가 변경되면 UiState에 반영된다`() = runTest {
        viewModel = createViewModel()
        val before = viewModel.uiState.value

        fakeUserRepository.themeMode.value = ThemeMode.LIGHT

        assertEquals(before.copy(themeMode = ThemeMode.LIGHT), viewModel.uiState.value)
    }

    // endregion

    // region setThemeMode

    @Test
    fun `setThemeMode 호출 시 repository의 setThemeMode를 호출한다`() = runTest {
        viewModel = createViewModel()
        viewModel.setThemeMode(ThemeMode.DARK)
        assertEquals(ThemeMode.DARK, fakeUserRepository.setThemeModeCalledWith)
    }

    // endregion
}
