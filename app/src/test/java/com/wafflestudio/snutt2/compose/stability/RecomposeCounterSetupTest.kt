package com.wafflestudio.snutt2.compose.stability

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RecomposeCounterSetupTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `SideEffect fires on initial composition and on state change`() {
        var sideEffectCount = 0
        val state = mutableStateOf(0)

        composeRule.setContent {
            @Suppress("UNUSED_VARIABLE")
            val value = state.value
            SideEffect { sideEffectCount++ }
            BasicText(text = value.toString())
        }
        composeRule.waitForIdle()
        assertEquals(1, sideEffectCount)

        state.value = 1
        composeRule.waitForIdle()
        assertEquals(2, sideEffectCount)
    }
}
