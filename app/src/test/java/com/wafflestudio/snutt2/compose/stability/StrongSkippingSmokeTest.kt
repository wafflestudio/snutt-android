package com.wafflestudio.snutt2.compose.stability

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

// StrongSkipping 활성 여부를 검증하는 스모크.
// UnstableSnapshot은 var 필드 때문에 unstable로 추론된다.
// StrongSkipping이 살아있으면 "새 인스턴스지만 equals()가 같은 경우" consumer가 skip된다.
// 이 테스트가 실패하면 컴파일러 플래그·버전·빌드 구성 회귀를 먼저 의심한다.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StrongSkippingSmokeTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `new but equal unstable instance is skipped via equals under StrongSkipping`() {
        val counter = RecomposeCounter()
        val state = mutableStateOf(UnstableSnapshot(id = "a", value = 1))

        composeRule.setContent {
            Consumer(snapshot = state.value, counter = counter)
        }
        composeRule.waitForIdle()
        val baseline = counter.count

        state.value = UnstableSnapshot(id = "a", value = 1)
        composeRule.waitForIdle()

        assertEquals(
            expected = baseline,
            actual = counter.count,
            message = "StrongSkipping이 꺼졌거나 손상되었습니다. new-but-equal unstable 인스턴스는 skip되어야 합니다.",
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
