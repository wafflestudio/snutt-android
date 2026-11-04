package com.wafflestudio.snutt2.feature.settings

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.fixture.TestFixtures
import com.wafflestudio.snutt2.ui.theme.SNUTTTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "ko-rKR-w411dp-h891dp")
class LectureReminderScreenUiTest {
    @get:Rule val composeRule = createComposeRule()

    private fun text(id: Int): String =
        RuntimeEnvironment.getApplication().getString(id)

    // 빈 목록이면 안내 화면을 표시하고 선택 버튼은 표시하지 않는다
    @Test
    fun showsEmptyState() {
        composeRule.setContent {
            SNUTTTheme { LectureReminderScreen(onClickBack = {}, lectures = emptyList()) }
        }
        composeRule.onNodeWithText(text(R.string.settings_lecture_reminder_empty_title)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.settings_lecture_reminder_ten_minutes_before)).assertDoesNotExist()
    }

    // 로딩 중에는 빈 화면 대신 진행 표시를 보여준다
    @Test
    fun showsLoadingInsteadOfEmpty() {
        composeRule.setContent {
            SNUTTTheme { LectureReminderScreen(onClickBack = {}, isLoading = true) }
        }
        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.settings_lecture_reminder_empty_title)).assertDoesNotExist()
    }

    // 조회 실패 시 오류와 재시도 버튼을 표시한다
    @Test
    fun showsErrorAndRetry() {
        composeRule.setContent {
            SNUTTTheme { LectureReminderScreen(onClickBack = {}, errorMessage = "연결이 끊어졌어요") }
        }
        composeRule.onNodeWithText("연결이 끊어졌어요").assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.reviews_error_retry)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.settings_lecture_reminder_empty_title)).assertDoesNotExist()
    }

    // 재시도 버튼을 누르면 재조회 콜백을 호출한다
    @Test
    fun retryInvokesCallback() {
        var retryCount = 0
        composeRule.setContent {
            SNUTTTheme {
                LectureReminderScreen(onClickBack = {}, errorMessage = "조회 실패", onRetry = { retryCount++ })
            }
        }
        composeRule.onNodeWithText(text(R.string.reviews_error_retry)).performClick()
        composeRule.runOnIdle { assertEquals(1, retryCount) }
    }

    // 상단 뒤로가기 버튼은 전달받은 콜백을 호출한다
    @Test
    fun backInvokesCallback() {
        var backCount = 0
        composeRule.setContent {
            SNUTTTheme { LectureReminderScreen(onClickBack = { backCount++ }, lectures = emptyList()) }
        }
        composeRule.onNode(hasClickAction()).performClick()
        composeRule.runOnIdle { assertEquals(1, backCount) }
    }

    // 목록의 각 과목에 네 가지 선택지를 표시한다
    @Test
    fun showsFourOptionsPerLecture() {
        val first = TestFixtures.lectureReminder("lecture-1", "과목 1", LectureReminderOffset.NONE)
        val second = TestFixtures.lectureReminder("lecture-2", "과목 2", LectureReminderOffset.TEN_MINUTES_BEFORE)
        composeRule.setContent {
            SNUTTTheme { LectureReminderScreen(onClickBack = {}, lectures = listOf(first, second)) }
        }
        composeRule.onNodeWithText("과목 1").assertIsDisplayed()
        composeRule.onNodeWithText("과목 2").assertIsDisplayed()
        listOf(text(R.string.settings_lecture_reminder_none), text(R.string.settings_lecture_reminder_ten_minutes_before), text(R.string.settings_lecture_reminder_at_start_time), text(R.string.settings_lecture_reminder_ten_minutes_after)).forEach { label ->
            composeRule.onAllNodesWithText(label).assertCountEquals(2)
        }
    }

    // 없음 클릭 시 해당 과목 ID와 옵션을 전달한다
    @Test
    fun nonePassesLectureIdAndOption() {
        val first = TestFixtures.lectureReminder("lecture-1", "과목 1", LectureReminderOffset.NONE)
        val second = TestFixtures.lectureReminder("lecture-2", "과목 2", LectureReminderOffset.TEN_MINUTES_BEFORE)
        var selection: Pair<String, LectureReminderOffset>? = null
        composeRule.setContent {
            SNUTTTheme {
                LectureReminderScreen(
                    onClickBack = {}, lectures = listOf(first, second),
                    onSelectOffset = { id, option -> selection = id to option },
                )
            }
        }
        composeRule.onAllNodesWithText(text(R.string.settings_lecture_reminder_none))[1].performClick()
        composeRule.runOnIdle { assertEquals(second.lectureId to LectureReminderOffset.NONE, selection) }
    }

    // 10분 전 클릭 시 해당 과목 ID와 옵션을 전달한다
    @Test
    fun beforePassesLectureIdAndOption() {
        val first = TestFixtures.lectureReminder("lecture-1", "과목 1", LectureReminderOffset.NONE)
        val second = TestFixtures.lectureReminder("lecture-2", "과목 2", LectureReminderOffset.TEN_MINUTES_BEFORE)
        var selection: Pair<String, LectureReminderOffset>? = null
        composeRule.setContent {
            SNUTTTheme {
                LectureReminderScreen(
                    onClickBack = {}, lectures = listOf(first, second),
                    onSelectOffset = { id, option -> selection = id to option },
                )
            }
        }
        composeRule.onAllNodesWithText(text(R.string.settings_lecture_reminder_ten_minutes_before))[1].performClick()
        composeRule.runOnIdle { assertEquals(second.lectureId to LectureReminderOffset.TEN_MINUTES_BEFORE, selection) }
    }

    // 수업 시작 시 클릭 시 해당 과목 ID와 옵션을 전달한다
    @Test
    fun atStartPassesLectureIdAndOption() {
        val first = TestFixtures.lectureReminder("lecture-1", "과목 1", LectureReminderOffset.NONE)
        val second = TestFixtures.lectureReminder("lecture-2", "과목 2", LectureReminderOffset.TEN_MINUTES_BEFORE)
        var selection: Pair<String, LectureReminderOffset>? = null
        composeRule.setContent {
            SNUTTTheme {
                LectureReminderScreen(
                    onClickBack = {}, lectures = listOf(first, second),
                    onSelectOffset = { id, option -> selection = id to option },
                )
            }
        }
        composeRule.onAllNodesWithText(text(R.string.settings_lecture_reminder_at_start_time))[1].performClick()
        composeRule.runOnIdle { assertEquals(second.lectureId to LectureReminderOffset.AT_START_TIME, selection) }
    }

    // 10분 후 클릭 시 해당 과목 ID와 옵션을 전달한다
    @Test
    fun afterPassesLectureIdAndOption() {
        val first = TestFixtures.lectureReminder("lecture-1", "과목 1", LectureReminderOffset.NONE)
        val second = TestFixtures.lectureReminder("lecture-2", "과목 2", LectureReminderOffset.TEN_MINUTES_BEFORE)
        var selection: Pair<String, LectureReminderOffset>? = null
        composeRule.setContent {
            SNUTTTheme {
                LectureReminderScreen(
                    onClickBack = {}, lectures = listOf(first, second),
                    onSelectOffset = { id, option -> selection = id to option },
                )
            }
        }
        composeRule.onAllNodesWithText(text(R.string.settings_lecture_reminder_ten_minutes_after))[1].performClick()
        composeRule.runOnIdle { assertEquals(second.lectureId to LectureReminderOffset.TEN_MINUTES_AFTER, selection) }
    }

    // 로딩 후 목록이 들어오면 빈 안내 없이 과목을 표시한다
    @Test
    fun showsListAfterLoading() {
        val first = TestFixtures.lectureReminder("lecture-1", "과목 1", LectureReminderOffset.NONE)
        val second = TestFixtures.lectureReminder("lecture-2", "과목 2", LectureReminderOffset.TEN_MINUTES_BEFORE)
        val loading = mutableStateOf(true)
        composeRule.setContent {
            SNUTTTheme {
                LectureReminderScreen(onClickBack = {}, lectures = listOf(first, second), isLoading = loading.value)
            }
        }
        composeRule.runOnIdle { loading.value = false }
        composeRule.onNodeWithText("과목 1").assertIsDisplayed()
        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertDoesNotExist()
        composeRule.onNodeWithText(text(R.string.settings_lecture_reminder_empty_title)).assertDoesNotExist()
    }
}
