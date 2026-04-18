package com.wafflestudio.snutt2.feature.settings

import app.cash.turbine.test
import com.wafflestudio.snutt2.domain.model.PushPreferenceType
import com.wafflestudio.snutt2.domain.model.PushPreferences
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeUserRepository
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.Unknown
import com.wafflestudio.snutt2.domain.WrongUserToken
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
class PushPreferencesViewModelTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver
    private lateinit var viewModel: PushPreferencesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeUserRepository = FakeUserRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
        viewModel = PushPreferencesViewModel(
            userRepository = fakeUserRepository,
            displayMessageResolver = fakeDisplayMessageResolver,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region init

    @Test
    fun `초기 상태는 Loading이다`() {
        assertEquals(PushPreferencesUiState.Loading, viewModel.pushPreferencesUiState.value)
    }

    // endregion

    // region loadPushPreferences

    @Test
    fun `loadPushPreferences 성공 시 Success 상태가 된다`() = runTest {
        val prefs = PushPreferences(lectureUpdate = true, vacancyNotification = false, lectureDiary = true)
        fakeUserRepository.getPushPreferencesResult = Result.Success(prefs)

        viewModel.loadPushPreferences()

        assertEquals(
            PushPreferencesUiState.Success(prefs),
            viewModel.pushPreferencesUiState.value,
        )
    }

    @Test
    fun `loadPushPreferences 실패 시 Error 상태가 된다`() = runTest {
        fakeUserRepository.getPushPreferencesResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))

        viewModel.loadPushPreferences()

        assertEquals(PushPreferencesUiState.Error, viewModel.pushPreferencesUiState.value)
    }

    @Test
    fun `loadPushPreferences 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.getPushPreferencesResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))

        viewModel.pushPreferencesUiEvent.test {
            viewModel.loadPushPreferences()
            assertEquals(PushPreferencesUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    @Test
    fun `loadPushPreferences AuthError 실패 시 Error 상태가 된다`() = runTest {
        fakeUserRepository.getPushPreferencesResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        fakeUserRepository.postForceLogoutResult = Result.Success(Unit)

        viewModel.loadPushPreferences()

        assertEquals(PushPreferencesUiState.Error, viewModel.pushPreferencesUiState.value)
    }

    @Test
    fun `loadPushPreferences AuthError 실패 시 ShowToast와 NavigateToOnboard 이벤트가 발생한다`() = runTest {
        fakeUserRepository.getPushPreferencesResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        fakeUserRepository.postForceLogoutResult = Result.Success(Unit)

        viewModel.pushPreferencesUiEvent.test {
            viewModel.loadPushPreferences()
            assertEquals(PushPreferencesUiEvent.ShowToast("인증 만료"), awaitItem())
            assertEquals(PushPreferencesUiEvent.NavigateToOnboard, awaitItem())
        }
    }

    @Test
    fun `loadPushPreferences AuthError 실패 시 postForceLogout을 호출한다`() = runTest {
        fakeUserRepository.getPushPreferencesResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        fakeUserRepository.postForceLogoutResult = Result.Success(Unit)

        viewModel.loadPushPreferences()

        assertEquals(true, fakeUserRepository.postForceLogoutCalled)
    }

    // endregion

    // region togglePushPreferences — LECTURE_UPDATE

    @Test
    fun `togglePushPreferences(LECTURE_UPDATE) 호출 시 lectureUpdate가 토글된 값으로 postPushPreferences를 호출한다`() = runTest {
        val prefs = PushPreferences(lectureUpdate = false, vacancyNotification = true, lectureDiary = false)
        fakeUserRepository.getPushPreferencesResult = Result.Success(prefs)
        viewModel.loadPushPreferences()
        fakeUserRepository.postPushPreferencesResult = Result.Success(Unit)

        viewModel.togglePushPreferences(PushPreferenceType.LECTURE_UPDATE)

        assertEquals(
            prefs.copy(lectureUpdate = true),
            fakeUserRepository.postPushPreferencesCalledWith,
        )
    }

    @Test
    fun `togglePushPreferences(LECTURE_UPDATE) 성공 시 토글된 상태로 UiState가 갱신된다`() = runTest {
        val prefs = PushPreferences(lectureUpdate = false, vacancyNotification = true, lectureDiary = false)
        fakeUserRepository.getPushPreferencesResult = Result.Success(prefs)
        viewModel.loadPushPreferences()
        fakeUserRepository.postPushPreferencesResult = Result.Success(Unit)

        viewModel.togglePushPreferences(PushPreferenceType.LECTURE_UPDATE)

        assertEquals(
            PushPreferencesUiState.Success(prefs.copy(lectureUpdate = true)),
            viewModel.pushPreferencesUiState.value,
        )
    }

    // endregion

    // region togglePushPreferences — VACANCY_NOTIFICATION

    @Test
    fun `togglePushPreferences(VACANCY_NOTIFICATION) 성공 시 vacancyNotification이 토글된다`() = runTest {
        val prefs = PushPreferences(lectureUpdate = false, vacancyNotification = true, lectureDiary = false)
        fakeUserRepository.getPushPreferencesResult = Result.Success(prefs)
        viewModel.loadPushPreferences()
        fakeUserRepository.postPushPreferencesResult = Result.Success(Unit)

        viewModel.togglePushPreferences(PushPreferenceType.VACANCY_NOTIFICATION)

        assertEquals(
            PushPreferencesUiState.Success(prefs.copy(vacancyNotification = false)),
            viewModel.pushPreferencesUiState.value,
        )
    }

    // endregion

    // region togglePushPreferences — DIARY

    @Test
    fun `togglePushPreferences(DIARY) 성공 시 lectureDiary가 토글된다`() = runTest {
        val prefs = PushPreferences(lectureUpdate = false, vacancyNotification = false, lectureDiary = false)
        fakeUserRepository.getPushPreferencesResult = Result.Success(prefs)
        viewModel.loadPushPreferences()
        fakeUserRepository.postPushPreferencesResult = Result.Success(Unit)

        viewModel.togglePushPreferences(PushPreferenceType.DIARY)

        assertEquals(
            PushPreferencesUiState.Success(prefs.copy(lectureDiary = true)),
            viewModel.pushPreferencesUiState.value,
        )
    }

    // endregion

    // region togglePushPreferences — 실패

    @Test
    fun `togglePushPreferences 실패 시 Error 상태가 된다`() = runTest {
        val prefs = PushPreferences(lectureUpdate = false, vacancyNotification = false, lectureDiary = false)
        fakeUserRepository.getPushPreferencesResult = Result.Success(prefs)
        viewModel.loadPushPreferences()
        fakeUserRepository.postPushPreferencesResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))

        viewModel.togglePushPreferences(PushPreferenceType.LECTURE_UPDATE)

        assertEquals(PushPreferencesUiState.Error, viewModel.pushPreferencesUiState.value)
    }

    @Test
    fun `togglePushPreferences 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val prefs = PushPreferences(lectureUpdate = false, vacancyNotification = false, lectureDiary = false)
        fakeUserRepository.getPushPreferencesResult = Result.Success(prefs)
        viewModel.loadPushPreferences()
        fakeUserRepository.postPushPreferencesResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))

        viewModel.pushPreferencesUiEvent.test {
            viewModel.togglePushPreferences(PushPreferenceType.LECTURE_UPDATE)
            assertEquals(PushPreferencesUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    @Test
    fun `Loading 상태에서 togglePushPreferences 호출 시 아무 일도 일어나지 않는다`() = runTest {
        viewModel.togglePushPreferences(PushPreferenceType.LECTURE_UPDATE)

        assertEquals(PushPreferencesUiState.Loading, viewModel.pushPreferencesUiState.value)
    }

    // endregion

    // region togglePushPreferences — AuthError

    @Test
    fun `togglePushPreferences AuthError 실패 시 Error 상태가 된다`() = runTest {
        val prefs = PushPreferences(lectureUpdate = true, vacancyNotification = false, lectureDiary = true)
        fakeUserRepository.getPushPreferencesResult = Result.Success(prefs)
        viewModel.loadPushPreferences()
        fakeUserRepository.postPushPreferencesResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        fakeUserRepository.postForceLogoutResult = Result.Success(Unit)

        viewModel.togglePushPreferences(PushPreferenceType.LECTURE_UPDATE)

        assertEquals(PushPreferencesUiState.Error, viewModel.pushPreferencesUiState.value)
    }

    @Test
    fun `togglePushPreferences AuthError 실패 시 ShowToast와 NavigateToOnboard 이벤트가 발생한다`() = runTest {
        val prefs = PushPreferences(lectureUpdate = true, vacancyNotification = false, lectureDiary = true)
        fakeUserRepository.getPushPreferencesResult = Result.Success(prefs)
        viewModel.loadPushPreferences()
        fakeUserRepository.postPushPreferencesResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        fakeUserRepository.postForceLogoutResult = Result.Success(Unit)

        viewModel.pushPreferencesUiEvent.test {
            viewModel.togglePushPreferences(PushPreferenceType.LECTURE_UPDATE)
            assertEquals(PushPreferencesUiEvent.ShowToast("인증 만료"), awaitItem())
            assertEquals(PushPreferencesUiEvent.NavigateToOnboard, awaitItem())
        }
    }

    @Test
    fun `togglePushPreferences AuthError 실패 시 postForceLogout을 호출한다`() = runTest {
        val prefs = PushPreferences(lectureUpdate = true, vacancyNotification = false, lectureDiary = true)
        fakeUserRepository.getPushPreferencesResult = Result.Success(prefs)
        viewModel.loadPushPreferences()
        fakeUserRepository.postPushPreferencesResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        fakeUserRepository.postForceLogoutResult = Result.Success(Unit)

        viewModel.togglePushPreferences(PushPreferenceType.LECTURE_UPDATE)

        assertEquals(true, fakeUserRepository.postForceLogoutCalled)
    }

    // endregion
}
