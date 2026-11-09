package com.wafflestudio.snutt2.feature.diary.diarywrite

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.lecturediary.DiaryQuestionnaireData
import com.wafflestudio.snutt2.domain.ServerFault
import com.wafflestudio.snutt2.fake.FakeAnalyticsLogger
import com.wafflestudio.snutt2.fake.FakeDiaryRepository
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeUserRepository
import com.wafflestudio.snutt2.fixture.TestFixtures.diaryClassTypeExam
import com.wafflestudio.snutt2.fixture.TestFixtures.diaryClassTypeLecture
import com.wafflestudio.snutt2.fixture.TestFixtures.diaryClassTypePresentation
import com.wafflestudio.snutt2.fixture.TestFixtures.diaryQuestion
import com.wafflestudio.snutt2.logging.AnalyticsEvent
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.DiarySubmittedParameter
import com.wafflestudio.snutt2.logging.DiaryWriteExitParameter
import com.wafflestudio.snutt2.logging.DiaryWriteParameter
import com.wafflestudio.snutt2.logging.DiaryWriteReferrer
import kotlinx.coroutines.CompletableDeferred
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
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TestTimeSource

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryWriteViewModelTest {

    private lateinit var fakeDiaryRepository: FakeDiaryRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver
    private lateinit var fakeAnalyticsLogger: FakeAnalyticsLogger
    private lateinit var testTimeSource: TestTimeSource

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeDiaryRepository = FakeDiaryRepository()
        fakeUserRepository = FakeUserRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
        fakeAnalyticsLogger = FakeAnalyticsLogger()
        testTimeSource = TestTimeSource()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        referrer: String?,
    ) = DiaryWriteViewModel(
        diaryRepository = fakeDiaryRepository,
        userRepository = fakeUserRepository,
        savedStateHandle = SavedStateHandle(
            mapOf(
                "lectureId" to "lecture-1",
                "courseTitle" to "컴퓨터프로그래밍",
                "referrer" to referrer,
            ),
        ),
        displayMessageResolver = fakeDisplayMessageResolver,
        analyticsLogger = fakeAnalyticsLogger,
        timeSource = testTimeSource,
    )

    private fun questionnaire(nextLectureId: String?) = DiaryQuestionnaireData(
        courseTitle = "컴퓨터프로그래밍",
        questions = listOf(diaryQuestion(id = "question-1")),
        nextLectureId = nextLectureId,
        nextLectureTitle = nextLectureId?.let { "자료구조" },
    )

    // region analyticsScreen

    @Test
    fun `nav argument 의 referrer 로 화면 파라미터를 만든다`() {
        val viewModel = createViewModel(referrer = "next")

        assertEquals(
            AnalyticsScreen.DiaryWrite(DiaryWriteParameter(DiaryWriteReferrer.NEXT)),
            viewModel.analyticsScreen,
        )
    }

    @Test
    fun `referrer 가 없으면 push 진입으로 간주한다`() {
        val viewModel = createViewModel(referrer = null)

        assertEquals(
            AnalyticsScreen.DiaryWrite(DiaryWriteParameter(DiaryWriteReferrer.PUSH)),
            viewModel.analyticsScreen,
        )
    }

    // endregion

    // region saveDiaryWrite

    @Test
    fun `제출 성공 시 선택한 활동 id, 코멘트 여부, 경과 시간, 진입 경로를 담아 diary_submitted 를 기록한다`() = runTest {
        // Given: 3개 활동 중 수업·발표를 선택하고 질문에 답한 뒤 코멘트를 작성한 상태
        fakeDiaryRepository.getDailyClassTypesResult =
            Result.Success(listOf(diaryClassTypeLecture, diaryClassTypeExam, diaryClassTypePresentation))
        fakeDiaryRepository.getQuestionnaireResult = Result.Success(questionnaire(nextLectureId = null))
        fakeDiaryRepository.submitDiaryResult = Result.Success(Unit)
        val viewModel = createViewModel(referrer = "push")
        viewModel.toggleActivitySelection(0)
        viewModel.toggleActivitySelection(2)
        viewModel.completeActivitySelection()
        viewModel.toggleAnswer(questionIndex = 0, answerIndex = 1)
        viewModel.onCommentTextChange("과제가 어려웠다")
        testTimeSource += 95.seconds
        fakeAnalyticsLogger.loggedEvents.clear()

        // When
        viewModel.saveDiaryWrite()

        // Then
        assertEquals(
            listOf(
                AnalyticsEvent.DiarySubmitted(
                    DiarySubmittedParameter(
                        activityTypeIds = listOf("class-type-lecture", "class-type-presentation"),
                        hasComment = true,
                        elapsedSec = 95,
                        referrer = DiaryWriteReferrer.PUSH,
                    ),
                ),
            ),
            fakeAnalyticsLogger.loggedEvents,
        )
    }

    @Test
    fun `코멘트가 공백뿐이면 has_comment 를 false 로 기록한다`() = runTest {
        fakeDiaryRepository.getDailyClassTypesResult = Result.Success(listOf(diaryClassTypeLecture, diaryClassTypeExam))
        fakeDiaryRepository.getQuestionnaireResult = Result.Success(questionnaire(nextLectureId = null))
        fakeDiaryRepository.submitDiaryResult = Result.Success(Unit)
        val viewModel = createViewModel(referrer = "history")
        viewModel.toggleActivitySelection(1)
        viewModel.completeActivitySelection()
        viewModel.toggleAnswer(questionIndex = 0, answerIndex = 0)
        viewModel.onCommentTextChange("   ")
        testTimeSource += 30.seconds
        fakeAnalyticsLogger.loggedEvents.clear()

        viewModel.saveDiaryWrite()

        assertEquals(
            listOf(
                AnalyticsEvent.DiarySubmitted(
                    DiarySubmittedParameter(
                        activityTypeIds = listOf("class-type-exam"),
                        hasComment = false,
                        elapsedSec = 30,
                        referrer = DiaryWriteReferrer.HISTORY,
                    ),
                ),
            ),
            fakeAnalyticsLogger.loggedEvents,
        )
    }

    @Test
    fun `제출 실패 시 diary_submitted 를 기록하지 않는다`() = runTest {
        fakeDiaryRepository.getDailyClassTypesResult = Result.Success(listOf(diaryClassTypeLecture))
        fakeDiaryRepository.getQuestionnaireResult = Result.Success(questionnaire(nextLectureId = null))
        fakeDiaryRepository.submitDiaryResult = Result.Fail(ServerFault("서버 오류", "잠시 후 다시 시도해주세요"))
        val viewModel = createViewModel(referrer = "push")
        viewModel.toggleActivitySelection(0)
        viewModel.completeActivitySelection()
        viewModel.toggleAnswer(questionIndex = 0, answerIndex = 0)
        fakeAnalyticsLogger.loggedEvents.clear()

        viewModel.saveDiaryWrite()

        assertEquals(emptyList<AnalyticsEvent>(), fakeAnalyticsLogger.loggedEvents)
    }

    @Test
    fun `제출 응답 전에 다시 제출하면 무시한다`() = runTest {
        fakeDiaryRepository.getDailyClassTypesResult = Result.Success(listOf(diaryClassTypeLecture))
        fakeDiaryRepository.getQuestionnaireResult = Result.Success(questionnaire(nextLectureId = null))
        val gate = CompletableDeferred<Unit>()
        fakeDiaryRepository.submitDiaryGate = gate
        val viewModel = createViewModel(referrer = "push")
        viewModel.toggleActivitySelection(0)
        viewModel.completeActivitySelection()
        viewModel.toggleAnswer(questionIndex = 0, answerIndex = 0)
        fakeAnalyticsLogger.loggedEvents.clear()

        viewModel.saveDiaryWrite()
        viewModel.saveDiaryWrite()
        gate.complete(Unit)

        assertEquals(1, fakeDiaryRepository.submitDiaryCallCount)
        assertEquals(1, fakeAnalyticsLogger.loggedEvents.count { it is AnalyticsEvent.DiarySubmitted })
    }

    // endregion

    // region onExitWithoutSubmit

    @Test
    fun `활동 선택을 완료하기 전에 나가면 step=activity 로 diary_write_exit 를 기록한다`() = runTest {
        fakeDiaryRepository.getDailyClassTypesResult = Result.Success(listOf(diaryClassTypeLecture, diaryClassTypeExam))
        val viewModel = createViewModel(referrer = "push")
        viewModel.toggleActivitySelection(0)
        testTimeSource += 12.seconds

        viewModel.onExitWithoutSubmit()

        assertEquals(
            listOf(
                AnalyticsEvent.DiaryWriteExit(
                    DiaryWriteExitParameter(
                        step = DiaryWriteExitParameter.Step.ACTIVITY,
                        elapsedSec = 12,
                        referrer = DiaryWriteReferrer.PUSH,
                    ),
                ),
            ),
            fakeAnalyticsLogger.loggedEvents,
        )
    }

    @Test
    fun `질문 단계에서 나가면 step=questions 로 diary_write_exit 를 기록한다`() = runTest {
        fakeDiaryRepository.getDailyClassTypesResult = Result.Success(listOf(diaryClassTypeLecture, diaryClassTypeExam))
        fakeDiaryRepository.getQuestionnaireResult = Result.Success(questionnaire(nextLectureId = null))
        val viewModel = createViewModel(referrer = "next")
        viewModel.toggleActivitySelection(1)
        viewModel.completeActivitySelection()
        testTimeSource += 48.seconds
        fakeAnalyticsLogger.loggedEvents.clear()

        viewModel.onExitWithoutSubmit()

        assertEquals(
            listOf(
                AnalyticsEvent.DiaryWriteExit(
                    DiaryWriteExitParameter(
                        step = DiaryWriteExitParameter.Step.QUESTIONS,
                        elapsedSec = 48,
                        referrer = DiaryWriteReferrer.NEXT,
                    ),
                ),
            ),
            fakeAnalyticsLogger.loggedEvents,
        )
    }

    @Test
    fun `질문 단계 진입 후 활동을 다시 고르는 중에 나가도 step=questions 로 기록한다`() = runTest {
        fakeDiaryRepository.getDailyClassTypesResult = Result.Success(listOf(diaryClassTypeLecture, diaryClassTypeExam))
        fakeDiaryRepository.getQuestionnaireResult = Result.Success(questionnaire(nextLectureId = null))
        val viewModel = createViewModel(referrer = "history")
        viewModel.toggleActivitySelection(0)
        viewModel.completeActivitySelection()
        viewModel.toggleActivitySelection(1) // Complete -> ReSelecting
        testTimeSource += 60.seconds
        fakeAnalyticsLogger.loggedEvents.clear()

        viewModel.onExitWithoutSubmit()

        assertEquals(
            listOf(
                AnalyticsEvent.DiaryWriteExit(
                    DiaryWriteExitParameter(
                        step = DiaryWriteExitParameter.Step.QUESTIONS,
                        elapsedSec = 60,
                        referrer = DiaryWriteReferrer.HISTORY,
                    ),
                ),
            ),
            fakeAnalyticsLogger.loggedEvents,
        )
    }

    @Test
    fun `여러 번 나가기를 호출해도 diary_write_exit 는 한 번만 기록한다`() = runTest {
        fakeDiaryRepository.getDailyClassTypesResult = Result.Success(listOf(diaryClassTypeLecture))
        val viewModel = createViewModel(referrer = "push")
        testTimeSource += 5.seconds

        viewModel.onExitWithoutSubmit()
        viewModel.onExitWithoutSubmit()

        assertEquals(
            listOf(
                AnalyticsEvent.DiaryWriteExit(
                    DiaryWriteExitParameter(
                        step = DiaryWriteExitParameter.Step.ACTIVITY,
                        elapsedSec = 5,
                        referrer = DiaryWriteReferrer.PUSH,
                    ),
                ),
            ),
            fakeAnalyticsLogger.loggedEvents,
        )
    }

    @Test
    fun `제출 성공 후에는 나가도 diary_write_exit 를 기록하지 않는다`() = runTest {
        fakeDiaryRepository.getDailyClassTypesResult = Result.Success(listOf(diaryClassTypeLecture))
        fakeDiaryRepository.getQuestionnaireResult = Result.Success(questionnaire(nextLectureId = "lecture-2"))
        fakeDiaryRepository.submitDiaryResult = Result.Success(Unit)
        val viewModel = createViewModel(referrer = "push")
        viewModel.toggleActivitySelection(0)
        viewModel.completeActivitySelection()
        viewModel.toggleAnswer(questionIndex = 0, answerIndex = 0)
        viewModel.saveDiaryWrite()
        fakeAnalyticsLogger.loggedEvents.clear()

        viewModel.onExitWithoutSubmit()

        assertEquals(emptyList<AnalyticsEvent>(), fakeAnalyticsLogger.loggedEvents)
    }

    @Test
    fun `나가기 호출 시 Return 이벤트가 발생한다`() = runTest {
        fakeDiaryRepository.getDailyClassTypesResult = Result.Success(listOf(diaryClassTypeLecture))
        val viewModel = createViewModel(referrer = "push")

        viewModel.uiEvent.test {
            viewModel.onExitWithoutSubmit()
            assertEquals(DiaryWriteUiEvent.Return, awaitItem())
        }
    }

    // endregion
}
