package com.wafflestudio.snutt2.views.logged_out.reset_password

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeUserRepository
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.Unknown
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
class FindPasswordViewModelTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver
    private lateinit var viewModel: FindPasswordViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeUserRepository = FakeUserRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
        viewModel = FindPasswordViewModel(
            userRepository = fakeUserRepository,
            savedStateHandle = SavedStateHandle(),
            displayMessageResolver = fakeDisplayMessageResolver,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region init

    @Test
    fun `초기 상태는 CheckId이다`() {
        assertEquals(
            FindPasswordViewModel.UIState.CheckId(userId = ""),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region checkEmailById

    @Test
    fun `checkEmailById 호출 시 repository의 checkEmailById를 호출한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")

        viewModel.checkEmailById("testuser")

        assertEquals("testuser", fakeUserRepository.checkEmailByIdCalledWith)
    }

    @Test
    fun `checkEmailById 성공 시 EnterFullEmail 상태로 전이한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")

        viewModel.checkEmailById("testuser")

        assertEquals(
            FindPasswordViewModel.UIState.EnterFullEmail(
                userId = "testuser",
                maskedEmail = "t***@snu.ac.kr",
                fullEmail = "",
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `checkEmailById 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "존재하지 않는 ID"))

        viewModel.uiEvent.test {
            viewModel.checkEmailById("wronguser")
            assertEquals(FindPasswordUiEvent.ShowToast("존재하지 않는 ID"), awaitItem())
        }
    }

    @Test
    fun `checkEmailById 실패 시 상태는 변하지 않는다`() = runTest {
        fakeUserRepository.checkEmailByIdResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val before = viewModel.uiState.value

        viewModel.checkEmailById("wronguser")

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion

    // region sendFullEmailAndRequestCode

    @Test
    fun `sendFullEmailAndRequestCode 호출 시 repository의 sendPwResetCodeToEmail을 호출한다`() = runTest {
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)

        viewModel.sendFullEmailAndRequestCode("test@snu.ac.kr")

        assertEquals("test@snu.ac.kr", fakeUserRepository.sendPwResetCodeToEmailCalledWith)
    }

    @Test
    fun `sendFullEmailAndRequestCode 성공 시 VerifyCode 상태로 전이한다`() = runTest {
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)

        viewModel.sendFullEmailAndRequestCode("test@snu.ac.kr")

        assertEquals(
            FindPasswordViewModel.UIState.VerifyCode(fullEmail = "test@snu.ac.kr"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `sendFullEmailAndRequestCode 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.sendPwResetCodeToEmailResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "이메일 불일치"))

        viewModel.uiEvent.test {
            viewModel.sendFullEmailAndRequestCode("wrong@snu.ac.kr")
            assertEquals(FindPasswordUiEvent.ShowToast("이메일 불일치"), awaitItem())
        }
    }

    // endregion

    // region verifyCode

    @Test
    fun `verifyCode 호출 시 savedStateHandle의 userId로 repository를 호출한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.checkEmailById("testuser") // savedStateHandle에 userId 저장
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.sendFullEmailAndRequestCode("test@snu.ac.kr")
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)

        viewModel.verifyCode("123456")

        assertEquals("testuser" to "123456", fakeUserRepository.verifyPwResetCodeCalledWith)
    }

    @Test
    fun `verifyCode 성공 시 EnterNewPassword 상태로 전이한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.checkEmailById("testuser")
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.sendFullEmailAndRequestCode("test@snu.ac.kr")
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)

        viewModel.verifyCode("123456")

        assertEquals(
            FindPasswordViewModel.UIState.EnterNewPassword(showCompleteDialog = false),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `verifyCode 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.verifyPwResetCodeResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "잘못된 코드"))

        viewModel.uiEvent.test {
            viewModel.verifyCode("000000")
            assertEquals(FindPasswordUiEvent.ShowToast("잘못된 코드"), awaitItem())
        }
    }

    // endregion

    // region resetPassword

    @Test
    fun `resetPassword 호출 시 savedStateHandle의 userId와 code로 repository를 호출한다`() = runTest {
        // 전체 플로우를 거쳐 savedStateHandle에 userId, code 저장
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.checkEmailById("testuser")
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.sendFullEmailAndRequestCode("test@snu.ac.kr")
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.verifyCode("123456")
        fakeUserRepository.resetPasswordResult = Result.Success(Unit)

        viewModel.resetPassword("newPassword!")

        assertEquals(
            Triple("testuser", "newPassword!", "123456"),
            fakeUserRepository.resetPasswordCalledWith,
        )
    }

    @Test
    fun `resetPassword 성공 시 showCompleteDialog가 true가 된다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.checkEmailById("testuser")
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.sendFullEmailAndRequestCode("test@snu.ac.kr")
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.verifyCode("123456")
        fakeUserRepository.resetPasswordResult = Result.Success(Unit)

        viewModel.resetPassword("newPassword!")

        assertEquals(
            FindPasswordViewModel.UIState.EnterNewPassword(showCompleteDialog = true),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `resetPassword 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.resetPasswordResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "비밀번호 조건 불충족"))

        viewModel.uiEvent.test {
            viewModel.resetPassword("weak")
            assertEquals(FindPasswordUiEvent.ShowToast("비밀번호 조건 불충족"), awaitItem())
        }
    }

    // endregion

    // region goToPreviousStep

    @Test
    fun `EnterFullEmail에서 goToPreviousStep 호출 시 CheckId로 돌아가고 userId가 복원된다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.checkEmailById("testuser")

        viewModel.goToPreviousStep()

        assertEquals(
            FindPasswordViewModel.UIState.CheckId(userId = "testuser"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `VerifyCode에서 goToPreviousStep 호출 시 EnterFullEmail로 돌아가고 저장값이 복원된다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.checkEmailById("testuser")
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.sendFullEmailAndRequestCode("test@snu.ac.kr")

        viewModel.goToPreviousStep()

        assertEquals(
            FindPasswordViewModel.UIState.EnterFullEmail(
                userId = "testuser",
                maskedEmail = "t***@snu.ac.kr",
                fullEmail = "test@snu.ac.kr",
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `EnterNewPassword에서 goToPreviousStep 호출 시 CheckId로 돌아간다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.checkEmailById("testuser")
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.sendFullEmailAndRequestCode("test@snu.ac.kr")
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.verifyCode("123456")

        viewModel.goToPreviousStep()

        assertEquals(
            FindPasswordViewModel.UIState.CheckId(userId = "testuser"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `CheckId에서 goToPreviousStep 호출 시 상태가 변하지 않는다`() = runTest {
        val before = viewModel.uiState.value

        viewModel.goToPreviousStep()

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion

    // region onCompleteDialogConfirm

    @Test
    fun `onCompleteDialogConfirm 호출 시 NavigateBack 이벤트가 발생한다`() = runTest {
        viewModel.uiEvent.test {
            viewModel.onCompleteDialogConfirm()
            assertEquals(FindPasswordUiEvent.NavigateBack, awaitItem())
        }
    }

    // endregion
}
