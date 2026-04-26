package com.wafflestudio.snutt2.feature.login.resetpassword

import androidx.lifecycle.SavedStateHandle
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
    fun `checkEmailById 호출 시 onIdFieldChange 로 입력한 값으로 repository를 호출한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")

        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()

        assertEquals("testuser", fakeUserRepository.checkEmailByIdCalledWith)
    }

    @Test
    fun `checkEmailById 성공 시 EnterFullEmail 상태로 전이한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")

        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()

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

        viewModel.onIdFieldChange("wronguser")

        viewModel.uiEvent.test {
            viewModel.checkEmailById()
            assertEquals(FindPasswordUiEvent.ShowToast("존재하지 않는 ID"), awaitItem())
        }
    }

    @Test
    fun `checkEmailById 실패 시 step 은 변하지 않고 입력값은 보존된다`() = runTest {
        fakeUserRepository.checkEmailByIdResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))

        viewModel.onIdFieldChange("wronguser")
        viewModel.checkEmailById()

        assertEquals(
            FindPasswordViewModel.UIState.CheckId(userId = "wronguser"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `CheckId step 이 아니면 checkEmailById 는 무시된다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById() // EnterFullEmail 로 전이
        val before = viewModel.uiState.value

        viewModel.checkEmailById()

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion

    // region sendFullEmailAndRequestCode

    @Test
    fun `sendFullEmailAndRequestCode 호출 시 onEmailFieldChange 입력값으로 repository를 호출한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)

        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()

        assertEquals("test@snu.ac.kr", fakeUserRepository.sendPwResetCodeToEmailCalledWith)
    }

    @Test
    fun `sendFullEmailAndRequestCode 성공 시 VerifyCode 상태로 전이한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)

        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()

        assertEquals(
            FindPasswordViewModel.UIState.VerifyCode(fullEmail = "test@snu.ac.kr"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `sendFullEmailAndRequestCode 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "이메일 불일치"))

        viewModel.onEmailFieldChange("wrong@snu.ac.kr")

        viewModel.uiEvent.test {
            viewModel.sendFullEmailAndRequestCode()
            assertEquals(FindPasswordUiEvent.ShowToast("이메일 불일치"), awaitItem())
        }
    }

    @Test
    fun `EnterFullEmail step 이 아니면 sendFullEmailAndRequestCode 는 무시된다`() = runTest {
        viewModel.sendFullEmailAndRequestCode()

        assertEquals(null, fakeUserRepository.sendPwResetCodeToEmailCalledWith)
    }

    // endregion

    // region verifyCode

    @Test
    fun `verifyCode 호출 시 savedStateHandle의 userId 와 onCodeFieldChange 입력값으로 repository를 호출한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)

        viewModel.onCodeFieldChange("123456")
        viewModel.verifyCode()

        assertEquals("testuser" to "123456", fakeUserRepository.verifyPwResetCodeCalledWith)
    }

    @Test
    fun `verifyCode 성공 시 EnterNewPassword 상태로 전이한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)

        viewModel.onCodeFieldChange("123456")
        viewModel.verifyCode()

        assertEquals(
            FindPasswordViewModel.UIState.EnterNewPassword(),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `verifyCode 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "잘못된 코드"))

        viewModel.onCodeFieldChange("000000")

        viewModel.uiEvent.test {
            viewModel.verifyCode()
            assertEquals(FindPasswordUiEvent.ShowToast("잘못된 코드"), awaitItem())
        }
    }

    @Test
    fun `VerifyCode step 이 아니면 verifyCode 는 무시된다`() = runTest {
        viewModel.verifyCode()

        assertEquals(null, fakeUserRepository.verifyPwResetCodeCalledWith)
    }

    // endregion

    // region resendVerifyCode

    @Test
    fun `resendVerifyCode 호출 시 같은 fullEmail 로 sendPwResetCodeToEmail 을 다시 호출한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()

        viewModel.resendVerifyCode()

        assertEquals("test@snu.ac.kr", fakeUserRepository.sendPwResetCodeToEmailCalledWith)
    }

    // endregion

    // region validateAndResetPassword

    @Test
    fun `validateAndResetPassword 성공 시 savedStateHandle의 userId, code 와 입력 비밀번호로 repository를 호출한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.onCodeFieldChange("123456")
        viewModel.verifyCode()
        fakeUserRepository.resetPasswordResult = Result.Success(Unit)

        viewModel.onNewPasswordFieldChange("newPassword1!")
        viewModel.onNewPasswordConfirmFieldChange("newPassword1!")
        viewModel.validateAndResetPassword(timerRunning = true)

        assertEquals(
            Triple("testuser", "newPassword1!", "123456"),
            fakeUserRepository.resetPasswordCalledWith,
        )
    }

    @Test
    fun `validateAndResetPassword 성공 시 dialogState 가 Complete 가 된다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.onCodeFieldChange("123456")
        viewModel.verifyCode()
        fakeUserRepository.resetPasswordResult = Result.Success(Unit)

        viewModel.onNewPasswordFieldChange("newPassword1!")
        viewModel.onNewPasswordConfirmFieldChange("newPassword1!")
        viewModel.validateAndResetPassword(timerRunning = true)

        assertEquals(
            FindPasswordViewModel.UIState.EnterNewPassword(
                newPasswordField = "newPassword1!",
                newPasswordConfirmField = "newPassword1!",
                dialogState = FindPasswordViewModel.UIState.EnterNewPassword.NewPasswordDialogState.Complete,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `validateAndResetPassword 시 비밀번호 미일치면 ConfirmFail 다이얼로그가 뜨고 repository는 호출되지 않는다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.onCodeFieldChange("123456")
        viewModel.verifyCode()

        viewModel.onNewPasswordFieldChange("newPassword1!")
        viewModel.onNewPasswordConfirmFieldChange("different1!")
        viewModel.validateAndResetPassword(timerRunning = true)

        val state = viewModel.uiState.value as FindPasswordViewModel.UIState.EnterNewPassword
        assertEquals(
            FindPasswordViewModel.UIState.EnterNewPassword.NewPasswordDialogState.Error(
                FindPasswordViewModel.UIState.EnterNewPassword.ErrorType.ConfirmFail,
            ),
            state.dialogState,
        )
        assertEquals(null, fakeUserRepository.resetPasswordCalledWith)
    }

    @Test
    fun `validateAndResetPassword 시 비밀번호 형식이 부적합하면 InvalidPassword 다이얼로그가 뜨고 repository는 호출되지 않는다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.onCodeFieldChange("123456")
        viewModel.verifyCode()

        viewModel.onNewPasswordFieldChange("short")
        viewModel.onNewPasswordConfirmFieldChange("short")
        viewModel.validateAndResetPassword(timerRunning = true)

        val state = viewModel.uiState.value as FindPasswordViewModel.UIState.EnterNewPassword
        assertEquals(
            FindPasswordViewModel.UIState.EnterNewPassword.NewPasswordDialogState.Error(
                FindPasswordViewModel.UIState.EnterNewPassword.ErrorType.InvalidPassword,
            ),
            state.dialogState,
        )
        assertEquals(null, fakeUserRepository.resetPasswordCalledWith)
    }

    @Test
    fun `validateAndResetPassword 시 timer 가 종료되었으면 아무 동작도 하지 않는다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.onCodeFieldChange("123456")
        viewModel.verifyCode()

        viewModel.onNewPasswordFieldChange("newPassword1!")
        viewModel.onNewPasswordConfirmFieldChange("newPassword1!")
        viewModel.validateAndResetPassword(timerRunning = false)

        assertEquals(null, fakeUserRepository.resetPasswordCalledWith)
    }

    @Test
    fun `validateAndResetPassword repository 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.onCodeFieldChange("123456")
        viewModel.verifyCode()
        fakeUserRepository.resetPasswordResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "비밀번호 변경 실패"))

        viewModel.onNewPasswordFieldChange("newPassword1!")
        viewModel.onNewPasswordConfirmFieldChange("newPassword1!")

        viewModel.uiEvent.test {
            viewModel.validateAndResetPassword(timerRunning = true)
            assertEquals(FindPasswordUiEvent.ShowToast("비밀번호 변경 실패"), awaitItem())
        }
    }

    // endregion

    // region onTimerExpired / dismissNewPasswordDialog

    @Test
    fun `onTimerExpired 호출 시 Expired 다이얼로그가 뜬다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.onCodeFieldChange("123456")
        viewModel.verifyCode()

        viewModel.onTimerExpired()

        val state = viewModel.uiState.value as FindPasswordViewModel.UIState.EnterNewPassword
        assertEquals(
            FindPasswordViewModel.UIState.EnterNewPassword.NewPasswordDialogState.Error(
                FindPasswordViewModel.UIState.EnterNewPassword.ErrorType.Expired,
            ),
            state.dialogState,
        )
    }

    @Test
    fun `dismissNewPasswordDialog 호출 시 dialogState 가 None 으로 돌아간다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.onCodeFieldChange("123456")
        viewModel.verifyCode()
        viewModel.onTimerExpired()

        viewModel.dismissNewPasswordDialog()

        val state = viewModel.uiState.value as FindPasswordViewModel.UIState.EnterNewPassword
        assertEquals(
            FindPasswordViewModel.UIState.EnterNewPassword.NewPasswordDialogState.None,
            state.dialogState,
        )
    }

    // endregion

    // region showWhyNotCodeComingDialog / dismissVerifyCodeDialog

    @Test
    fun `showWhyNotCodeComingDialog 호출 시 VerifyCode dialogState 가 WhyNotCodeComing 이 된다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()

        viewModel.showWhyNotCodeComingDialog()

        val state = viewModel.uiState.value as FindPasswordViewModel.UIState.VerifyCode
        assertEquals(
            FindPasswordViewModel.UIState.VerifyCode.VerifyCodeDialogState.WhyNotCodeComing,
            state.dialogState,
        )
    }

    @Test
    fun `dismissVerifyCodeDialog 호출 시 VerifyCode dialogState 가 None 이 된다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        viewModel.showWhyNotCodeComingDialog()

        viewModel.dismissVerifyCodeDialog()

        val state = viewModel.uiState.value as FindPasswordViewModel.UIState.VerifyCode
        assertEquals(
            FindPasswordViewModel.UIState.VerifyCode.VerifyCodeDialogState.None,
            state.dialogState,
        )
    }

    // endregion

    // region goToPreviousStep

    @Test
    fun `EnterFullEmail에서 goToPreviousStep 호출 시 CheckId로 돌아가고 userId가 복원된다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()

        viewModel.goToPreviousStep()

        assertEquals(
            FindPasswordViewModel.UIState.CheckId(userId = "testuser"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `VerifyCode에서 goToPreviousStep 호출 시 EnterFullEmail로 돌아가고 저장값이 복원된다`() = runTest {
        fakeUserRepository.checkEmailByIdResult = Result.Success("t***@snu.ac.kr")
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()

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
        viewModel.onIdFieldChange("testuser")
        viewModel.checkEmailById()
        fakeUserRepository.sendPwResetCodeToEmailResult = Result.Success(Unit)
        viewModel.onEmailFieldChange("test@snu.ac.kr")
        viewModel.sendFullEmailAndRequestCode()
        fakeUserRepository.verifyPwResetCodeResult = Result.Success(Unit)
        viewModel.onCodeFieldChange("123456")
        viewModel.verifyCode()

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
