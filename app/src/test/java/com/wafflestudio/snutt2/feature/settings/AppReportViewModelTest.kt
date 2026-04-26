package com.wafflestudio.snutt2.feature.settings

import app.cash.turbine.test
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.Unknown
import com.wafflestudio.snutt2.domain.model.User
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeUserRepository
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
class AppReportViewModelTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeUserRepository = FakeUserRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = AppReportViewModel(
        userRepository = fakeUserRepository,
        displayMessageResolver = fakeDisplayMessageResolver,
    )

    // region initial state

    @Test
    fun `user가 있으면 초기 email이 user의 email이다`() {
        fakeUserRepository.user.value = User(email = "user@snu.ac.kr", localId = null, nickname = null)
        val viewModel = createViewModel()
        assertEquals("user@snu.ac.kr", viewModel.uiState.value.email)
    }

    @Test
    fun `user가 없으면 초기 email이 빈 문자열이다`() {
        fakeUserRepository.user.value = null
        val viewModel = createViewModel()
        assertEquals("", viewModel.uiState.value.email)
    }

    // endregion

    // region sendFeedback

    @Test
    fun `sendFeedback 호출 시 repository의 postFeedback을 호출한다`() = runTest {
        fakeUserRepository.postFeedbackResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.onEmailChange("test@snu.ac.kr")
        viewModel.onDetailChange("버그 발견")
        viewModel.sendFeedback()

        assertEquals("test@snu.ac.kr" to "버그 발견", fakeUserRepository.postFeedbackCalledWith)
    }

    @Test
    fun `sendFeedback 성공 시 Success 이벤트가 발생한다`() = runTest {
        fakeUserRepository.postFeedbackResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.onEmailChange("test@snu.ac.kr")
        viewModel.onDetailChange("버그 발견")

        viewModel.uiEvent.test {
            viewModel.sendFeedback()
            assertEquals(AppReportUiEvent.Success, awaitItem())
        }
    }

    @Test
    fun `sendFeedback 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val error = Unknown(displayTitle = "", displayMessage = "전송 실패")
        fakeUserRepository.postFeedbackResult = Result.Fail(error)
        val viewModel = createViewModel()

        viewModel.onEmailChange("test@snu.ac.kr")
        viewModel.onDetailChange("버그 발견")

        viewModel.uiEvent.test {
            viewModel.sendFeedback()
            assertEquals(AppReportUiEvent.ShowToast("전송 실패"), awaitItem())
        }
    }

    @Test
    fun `sendFeedback 호출 시 sentEnabled가 false가 된다`() = runTest {
        fakeUserRepository.postFeedbackResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.onEmailChange("test@snu.ac.kr")
        viewModel.onDetailChange("버그 발견")
        viewModel.sendFeedback()

        assertEquals(false, viewModel.uiState.value.sentEnabled)
    }

    // endregion
}
