package com.wafflestudio.snutt2.compose.stability

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.wafflestudio.snutt2.feature.home.drawer.HomeDrawerUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

// List 필드를 가진 HomeDrawerUiState는 unstable이므로 참조 동일성으로 비교된다.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeDrawerUiStateSkipTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `new but equal HomeDrawerUiState recomposes`() {
        val counter = RecomposeCounter()
        val parentCounter = RecomposeCounter()
        val state = mutableStateOf(HomeDrawerUiState(), referentialEqualityPolicy())

        composeRule.setContent {
            SideEffect { parentCounter.increment() }
            Consumer(uiState = state.value, counter = counter)
        }
        composeRule.waitForIdle()
        val baseline = counter.count
        val parentBaseline = parentCounter.count

        state.value = HomeDrawerUiState()
        composeRule.waitForIdle()

        assertEquals(parentBaseline + 1, parentCounter.count)
        assertEquals(
            expected = baseline + 1,
            actual = counter.count,
            message = "새 unstable HomeDrawerUiState 인스턴스는 재조합되어야 합니다.",
        )
    }
}

@Composable
private fun Consumer(uiState: HomeDrawerUiState, counter: RecomposeCounter) {
    SideEffect { counter.increment() }
    BasicText(text = "sheet=${uiState.homeDrawerBottomSheetType}")
}
