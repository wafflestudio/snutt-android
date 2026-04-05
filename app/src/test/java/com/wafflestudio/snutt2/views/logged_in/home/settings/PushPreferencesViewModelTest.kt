package com.wafflestudio.snutt2.views.logged_in.home.settings

import app.cash.turbine.test
import com.wafflestudio.snutt2.domainmodel.PushPreferenceType
import com.wafflestudio.snutt2.domainmodel.PushPreferences
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
        assertIs<PushPreferencesUiState.Loading>(viewModel.pushPreferenceUiState.value)
    }

    // endregion

    // region loadPushPreferences

    @Test
    fun `loadPushPreferences 성공 시 Success 상태가 된다`() = runTest {
        val prefs = PushPreferences(lectureUpdate = true, vacancyNotification = false, lectureDiary = true)
        fakeUserRepository.getPushPreferencesResult = Result.Success(prefs)

        viewModel.loadPushPreferences()
        2
        assertEquals(
            PushPreferencesUiState.Success(prefs),
            viewModel.pushPreferenceUiState.value,
        )
    }

    @Test
    fun `loadPushPreferences 실패 시 Error 상태가 된다`() = runTest {
        fakeUserRepository.getPushPreferencesResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))

        viewModel.loadPushPreferences()

        assertIs<PushPreferencesUiState.Error>(viewModel.pushPreferenceUiState.value)
    }

    @Test
    fun `loadPushPreferences 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeUserRepository.getPushPreferencesResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))

        viewModel.pushPreferencesUiEvent.test {
            viewModel.loadPushPreferences()
            val event = assertIs<PushPreferencesUiEvent.ShowToast>(awaitItem())
            assertEquals("에러", event.message)
        }
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
            viewModel.pushPreferenceUiState.value,
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
            viewModel.pushPreferenceUiState.value,
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
            viewModel.pushPreferenceUiState.value,
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

        assertIs<PushPreferencesUiState.Error>(viewModel.pushPreferenceUiState.value)
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
            val event = assertIs<PushPreferencesUiEvent.ShowToast>(awaitItem())
            assertEquals("에러", event.message)
        }
    }

    @Test
    fun `Loading 상태에서 togglePushPreferences 호출 시 아무 일도 일어나지 않는다`() = runTest {
        viewModel.togglePushPreferences(PushPreferenceType.LECTURE_UPDATE)

        assertIs<PushPreferencesUiState.Loading>(viewModel.pushPreferenceUiState.value)
    }

    // endregion
}
