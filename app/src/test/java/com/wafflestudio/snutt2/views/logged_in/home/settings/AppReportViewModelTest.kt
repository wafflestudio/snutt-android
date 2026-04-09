package com.wafflestudio.snutt2.views.logged_in.home.settings

import app.cash.turbine.test
import com.wafflestudio.snutt2.domain.model.User
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeUserRepository
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.Unknown
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

    // region initialEmail

    @Test
    fun `user가 있으면 initialEmail이 user의 email이다`() {
        fakeUserRepository.user.value = User(email = "user@snu.ac.kr", localId = null, nickname = null)
        val viewModel = createViewModel()
        assertEquals("user@snu.ac.kr", viewModel.initialEmail)
    }

    @Test
    fun `user가 없으면 initialEmail이 빈 문자열이다`() {
        fakeUserRepository.user.value = null
        val viewModel = createViewModel()
        assertEquals("", viewModel.initialEmail)
    }

    // endregion

    // region sendFeedback

    @Test
    fun `sendFeedback 호출 시 repository의 postFeedback을 호출한다`() = runTest {
        fakeUserRepository.postFeedbackResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.sendFeedback("test@snu.ac.kr", "버그 발견")

        assertEquals("test@snu.ac.kr" to "버그 발견", fakeUserRepository.postFeedbackCalledWith)
    }

    @Test
    fun `sendFeedback 성공 시 Success 이벤트가 발생한다`() = runTest {
        fakeUserRepository.postFeedbackResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.sendFeedback("test@snu.ac.kr", "버그 발견")
            assertEquals(AppReportUiEvent.Success, awaitItem())
        }
    }

    @Test
    fun `sendFeedback 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val error = Unknown(displayTitle = "", displayMessage = "전송 실패")
        fakeUserRepository.postFeedbackResult = Result.Fail(error)
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.sendFeedback("test@snu.ac.kr", "버그 발견")
            assertEquals(AppReportUiEvent.ShowToast("전송 실패"), awaitItem())
        }
    }

    // endregion
}
