package com.wafflestudio.snutt2.compose.stability

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

// Strong Skipping은 unstable 파라미터를 참조 동일성으로 비교한다.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StrongSkippingSmokeTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `same unstable instance skips and equal new instance recomposes`() {
        val counter = RecomposeCounter()
        val parentCounter = RecomposeCounter()
        val trigger = mutableStateOf(0)
        val state = mutableStateOf(UnstableSnapshot(id = "a", value = 1), referentialEqualityPolicy())

        composeRule.setContent {
            trigger.value
            SideEffect { parentCounter.increment() }
            Consumer(snapshot = state.value, counter = counter)
        }
        composeRule.waitForIdle()
        val baseline = counter.count

        val parentBaseline = parentCounter.count

        trigger.value++
        composeRule.waitForIdle()
        assertEquals(parentBaseline + 1, parentCounter.count)
        assertEquals(baseline, counter.count)

        state.value = UnstableSnapshot(id = "a", value = 1)
        composeRule.waitForIdle()

        assertEquals(parentBaseline + 2, parentCounter.count)
        assertEquals(
            expected = baseline + 1,
            actual = counter.count,
            message = "값이 같더라도 새 unstable 인스턴스는 재조합되어야 합니다.",
        )
    }
}

private data class UnstableSnapshot(
    val id: String,
    var value: Int,
)

@Composable
private fun Consumer(snapshot: UnstableSnapshot, counter: RecomposeCounter) {
    SideEffect { counter.increment() }
    BasicText(text = "${snapshot.id}=${snapshot.value}")
}
