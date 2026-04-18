package com.wafflestudio.snutt2.compose.stability

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import com.wafflestudio.snutt2.feature.home.drawer.HomeDrawerUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

// UiState data class의 auto-equals 정합성 canary.
// HomeDrawerUiState가 data class 성격을 잃거나 equals 계약이 깨지면 이 테스트가 실패한다.
// 같은 패턴(내용 동일한 UiState 새 인스턴스 → skip)의 회귀를 대표적으로 잡는다.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeDrawerUiStateSkipTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `new but equal HomeDrawerUiState is skipped`() {
        val counter = RecomposeCounter()
        val state = mutableStateOf(HomeDrawerUiState())

        composeRule.setContent {
            Consumer(uiState = state.value, counter = counter)
        }
        composeRule.waitForIdle()
        val baseline = counter.count

        state.value = HomeDrawerUiState()
        composeRule.waitForIdle()

        assertEquals(
            expected = baseline,
            actual = counter.count,
            message = "HomeDrawerUiState auto-equals가 깨졌거나 data class가 아닙니다.",
        )
    }
}

@Composable
private fun Consumer(uiState: HomeDrawerUiState, counter: RecomposeCounter) {
    SideEffect { counter.increment() }
    BasicText(text = "sheet=${uiState.homeDrawerBottomSheetType}")
}
