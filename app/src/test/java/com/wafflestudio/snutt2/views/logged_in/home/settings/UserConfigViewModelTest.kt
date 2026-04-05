package com.wafflestudio.snutt2.views.logged_in.home.settings

import app.cash.turbine.test
import com.wafflestudio.snutt2.domainmodel.Nickname
import com.wafflestudio.snutt2.domainmodel.User
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
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class UserConfigViewModelTest {

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

    private fun createViewModel() = UserConfigViewModel(
        userRepository = fakeUserRepository,
        displayMessageResolver = fakeDisplayMessageResolver,
    )

    // region Source 반응 — user

    @Test
    fun `user가 null이면 빈 문자열로 UiState가 구성된다`() = runTest {
        fakeUserRepository.user.value = null

        val viewModel = createViewModel()

        assertEquals(
            UserConfigUiState(userName = "", localId = null, email = null),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `user가 있으면 nickname, localId, email이 UiState에 반영된다`() = runTest {
        fakeUserRepository.user.value = User(
            email = "test@snu.ac.kr",
            localId = "testuser",
            nickname = Nickname(nickname = "닉네임", tag = "1234"),
        )

        val viewModel = createViewModel()

        assertEquals(
            UserConfigUiState(
                userName = "닉네임#1234",
                localId = "testuser",
                email = "test@snu.ac.kr",
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `user가 변경되면 UiState가 갱신되고 dialogState는 보존된다`() = runTest {
        fakeUserRepository.user.value = User(
            email = "old@snu.ac.kr", localId = "old", nickname = null,
        )
        val viewModel = createViewModel()
        viewModel.showLeaveDialog()
        val before = viewModel.uiState.value

        fakeUserRepository.user.value = User(
            email = "new@snu.ac.kr", localId = "new", nickname = null,
        )

        assertEquals(
            before.copy(localId = "new", email = "new@snu.ac.kr"),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region showChangePasswordDialog / hideChangePasswordDialog

    @Test
    fun `showChangePasswordDialog 호출 시 ChangePassword 다이얼로그가 열린다`() = runTest {
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        viewModel.showChangePasswordDialog()

        assertEquals(
            before.copy(dialogState = UserConfigUiState.DialogState.ChangePassword),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `hideChangePasswordDialog 호출 시 다이얼로그가 닫힌다`() = runTest {
        val viewModel = createViewModel()
        viewModel.showChangePasswordDialog()
        val before = viewModel.uiState.value

        viewModel.hideChangePasswordDialog()

        assertEquals(
            before.copy(dialogState = UserConfigUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region showAddIdPasswordDialog / hideAddIdPasswordDialog

    @Test
    fun `showAddIdPasswordDialog 호출 시 AddIdPassword 다이얼로그가 열린다`() = runTest {
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        viewModel.showAddIdPasswordDialog()

        assertEquals(
            before.copy(dialogState = UserConfigUiState.DialogState.AddIdPassword),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `hideAddIdPasswordDialog 호출 시 다이얼로그가 닫힌다`() = runTest {
        val viewModel = createViewModel()
        viewModel.showAddIdPasswordDialog()
        val before = viewModel.uiState.value

        viewModel.hideAddIdPasswordDialog()

        assertEquals(
            before.copy(dialogState = UserConfigUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region showLeaveDialog / hideLeaveDialog

    @Test
    fun `showLeaveDialog 호출 시 Leave 다이얼로그가 열린다`() = runTest {
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        viewModel.showLeaveDialog()

        assertEquals(
            before.copy(dialogState = UserConfigUiState.DialogState.Leave),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `hideLeaveDialog 호출 시 다이얼로그가 닫힌다`() = runTest {
        val viewModel = createViewModel()
        viewModel.showLeaveDialog()
        val before = viewModel.uiState.value

        viewModel.hideLeaveDialog()

        assertEquals(
            before.copy(dialogState = UserConfigUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region changePassword

    @Test
    fun `changePassword 성공 시 repository의 putUserPassword를 호출한다`() = runTest {
        fakeUserRepository.putUserPasswordResult = Result.Success(Unit)

        val viewModel = createViewModel()
        viewModel.changePassword("oldPw1", "newPw1a", "newPw1a")

        assertEquals("oldPw1" to "newPw1a", fakeUserRepository.putUserPasswordCalledWith)
    }

    @Test
    fun `changePassword 성공 시 ChangePasswordSuccess 이벤트가 발생하고 다이얼로그가 닫힌다`() = runTest {
        fakeUserRepository.putUserPasswordResult = Result.Success(Unit)
        val viewModel = createViewModel()
        viewModel.showChangePasswordDialog()

        viewModel.uiEvent.test {
            viewModel.changePassword("oldPw1", "newPw1a", "newPw1a")
            val event = assertIs<UserConfigUiEvent.ShowToastByEvent>(awaitItem())
            assertEquals(UserConfigEvent.ChangePasswordSuccess, event.event)
        }
        assertEquals(
            UserConfigUiState.DialogState.None,
            viewModel.uiState.value.dialogState,
        )
    }

    @Test
    fun `changePassword 시 비밀번호가 유효하지 않으면 InvalidPasswordError 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.changePassword("oldPw1", "short", "short") // 숫자+영문 6~20자 불만족
            val event = assertIs<UserConfigUiEvent.ShowToastByEvent>(awaitItem())
            assertEquals(UserConfigEvent.InvalidPasswordError, event.event)
        }
    }

    @Test
    fun `changePassword 시 비밀번호 확인이 불일치하면 PasswordMismatchError 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.changePassword("oldPw1", "newPw1a", "different1")
            val event = assertIs<UserConfigUiEvent.ShowToastByEvent>(awaitItem())
            assertEquals(UserConfigEvent.PasswordMismatchError, event.event)
        }
    }

    @Test
    fun `changePassword 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.putUserPasswordResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "비밀번호 오류"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.changePassword("oldPw1", "newPw1a", "newPw1a")
            val event = assertIs<UserConfigUiEvent.ShowToast>(awaitItem())
            assertEquals("비밀번호 오류", event.message)
        }
    }

    // endregion

    // region addNewLocalId

    @Test
    fun `addNewLocalId 성공 시 repository의 postUserPassword를 호출한다`() = runTest {
        fakeUserRepository.postUserPasswordResult = Result.Success(Unit)

        val viewModel = createViewModel()
        viewModel.addNewLocalId("testid", "testPw1", "testPw1")

        assertEquals("testid" to "testPw1", fakeUserRepository.postUserPasswordCalledWith)
    }

    @Test
    fun `addNewLocalId 성공 시 AddIdPasswordSuccess 이벤트가 발생하고 다이얼로그가 닫히고 fetchUserInfo가 호출된다`() = runTest {
        fakeUserRepository.postUserPasswordResult = Result.Success(Unit)
        val viewModel = createViewModel()
        viewModel.showAddIdPasswordDialog()

        viewModel.uiEvent.test {
            viewModel.addNewLocalId("testid", "testPw1", "testPw1")
            val event = assertIs<UserConfigUiEvent.ShowToastByEvent>(awaitItem())
            assertEquals(UserConfigEvent.AddIdPasswordSuccess, event.event)
        }
        assertEquals(
            UserConfigUiState.DialogState.None,
            viewModel.uiState.value.dialogState,
        )
        assertEquals(true, fakeUserRepository.fetchUserInfoCalled)
    }

    @Test
    fun `addNewLocalId 시 ID가 유효하지 않으면 InvalidIdError 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.addNewLocalId("ab", "testPw1", "testPw1") // 4~32자 영숫자 불만족
            val event = assertIs<UserConfigUiEvent.ShowToastByEvent>(awaitItem())
            assertEquals(UserConfigEvent.InvalidIdError, event.event)
        }
    }

    @Test
    fun `addNewLocalId 시 비밀번호가 유효하지 않으면 InvalidPasswordError 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.addNewLocalId("testid", "short", "short")
            val event = assertIs<UserConfigUiEvent.ShowToastByEvent>(awaitItem())
            assertEquals(UserConfigEvent.InvalidPasswordError, event.event)
        }
    }

    @Test
    fun `addNewLocalId 시 비밀번호 확인이 불일치하면 PasswordMismatchError 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.addNewLocalId("testid", "testPw1", "different1")
            val event = assertIs<UserConfigUiEvent.ShowToastByEvent>(awaitItem())
            assertEquals(UserConfigEvent.PasswordMismatchError, event.event)
        }
    }

    @Test
    fun `addNewLocalId 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.postUserPasswordResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "중복 ID"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.addNewLocalId("testid", "testPw1", "testPw1")
            val event = assertIs<UserConfigUiEvent.ShowToast>(awaitItem())
            assertEquals("중복 ID", event.message)
        }
    }

    // endregion

    // region leave

    @Test
    fun `leave 호출 시 repository의 deleteUserAccount를 호출한다`() = runTest {
        fakeUserRepository.deleteUserAccountResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.leave()

        assertEquals(true, fakeUserRepository.deleteUserAccountCalled)
    }

    @Test
    fun `leave 성공 시 다이얼로그가 닫히고 NavigateToOnboard 이벤트가 발생한다`() = runTest {
        fakeUserRepository.deleteUserAccountResult = Result.Success(Unit)
        val viewModel = createViewModel()
        viewModel.showLeaveDialog()

        viewModel.uiEvent.test {
            viewModel.leave()
            assertEquals(UserConfigUiEvent.NavigateToOnboard, awaitItem())
        }
        assertEquals(
            UserConfigUiState.DialogState.None,
            viewModel.uiState.value.dialogState,
        )
    }

    @Test
    fun `leave 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.deleteUserAccountResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "탈퇴 실패"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.leave()
            val event = assertIs<UserConfigUiEvent.ShowToast>(awaitItem())
            assertEquals("탈퇴 실패", event.message)
        }
    }

    // endregion

    // region resetToastMessage

    @Test
    fun `resetToastMessage 호출 시 빈 ShowToast 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.resetToastMessage()
            assertEquals(UserConfigUiEvent.ShowToast(""), awaitItem())
        }
    }

    // endregion
}
