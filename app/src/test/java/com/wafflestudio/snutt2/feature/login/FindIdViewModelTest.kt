package com.wafflestudio.snutt2.feature.login

import app.cash.turbine.test
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.Unknown
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
class FindIdViewModelTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver
    private lateinit var viewModel: FindIdViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeUserRepository = FakeUserRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
        viewModel = FindIdViewModel(
            userRepository = fakeUserRepository,
            displayMessageResolver = fakeDisplayMessageResolver,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region findIdByEmail

    @Test
    fun `findIdByEmail 호출 시 입력된 이메일로 repository를 호출한다`() = runTest {
        fakeUserRepository.findIdByEmailResult = Result.Success(Unit)

        viewModel.findIdByEmail("test@snu.ac.kr")

        assertEquals("test@snu.ac.kr", fakeUserRepository.findIdByEmailCalledWith)
    }

    @Test
    fun `findIdByEmail 성공 시 Success 이벤트가 발생한다`() = runTest {
        fakeUserRepository.findIdByEmailResult = Result.Success(Unit)

        viewModel.uiEvent.test {
            viewModel.findIdByEmail("test@snu.ac.kr")
            assertEquals(FindIdUiEvent.Success("test@snu.ac.kr"), awaitItem())
        }
    }

    @Test
    fun `findIdByEmail 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val error = Unknown(displayTitle = "", displayMessage = "존재하지 않는 이메일입니다")
        fakeUserRepository.findIdByEmailResult = Result.Fail(error)

        viewModel.uiEvent.test {
            viewModel.findIdByEmail("wrong@snu.ac.kr")
            assertEquals(FindIdUiEvent.ShowToast("존재하지 않는 이메일입니다"), awaitItem())
        }
    }

    // endregion
}
