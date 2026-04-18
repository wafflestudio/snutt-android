package com.wafflestudio.snutt2.compose.stability

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import com.wafflestudio.snutt2.domain.model.LectureSession
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LectureSessionsSkipTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `parent state change skips consumer that takes same List reference`() {
        val counter = RecomposeCounter()
        val triggerState = mutableStateOf(0)
        val fixedSessions = newSessions()

        composeRule.setContent {
            @Suppress("UNUSED_VARIABLE")
            val t = triggerState.value
            LectureSessionsConsumer(sessions = fixedSessions, counter = counter)
        }
        composeRule.waitForIdle()
        val baseline = counter.count

        triggerState.value = 1
        composeRule.waitForIdle()

        assertEquals(
            expected = baseline,
            actual = counter.count,
            message = "parent state change should skip consumer when same List reference is re-passed",
        )
    }

    @Test
    fun `new but equal List LectureSession triggers skip via equals under StrongSkipping`() {
        val counter = RecomposeCounter()
        val sessionsState = mutableStateOf(newSessions())

        composeRule.setContent {
            LectureSessionsConsumer(sessions = sessionsState.value, counter = counter)
        }
        composeRule.waitForIdle()
        val baseline = counter.count

        sessionsState.value = newSessions()
        composeRule.waitForIdle()

        assertEquals(
            expected = baseline,
            actual = counter.count,
            message = "new-but-equal List should skip via equals() under StrongSkipping",
        )
    }

    private fun newSessions(): List<LectureSession> = listOf(
        LectureSession.Default,
        LectureSession(
            id = "2",
            day = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(12, 0),
            place = "",
        ),
    )
}

@Stable
private class RecomposeCounter {
    var count = 0
        private set

    fun increment() {
        count++
    }
}

@Composable
private fun LectureSessionsConsumer(
    sessions: List<LectureSession>,
    counter: RecomposeCounter,
) {
    SideEffect { counter.increment() }
    BasicText(text = "${sessions.size} sessions")
}
