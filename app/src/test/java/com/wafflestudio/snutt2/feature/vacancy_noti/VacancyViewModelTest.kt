package com.wafflestudio.snutt2.feature.vacancy_noti

import app.cash.turbine.test
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeRemoteConfig
import com.wafflestudio.snutt2.fake.FakeUserRepository
import com.wafflestudio.snutt2.fake.FakeVacancyRepository
import com.wafflestudio.snutt2.fixture.TestFixtures.lecture1
import com.wafflestudio.snutt2.fixture.TestFixtures.lecture2
import com.wafflestudio.snutt2.fixture.TestFixtures.searchedLecture
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.Unknown
import com.wafflestudio.snutt2.domain.WrongUserToken
import com.wafflestudio.snutt2.lib.toDataWithState
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
class VacancyViewModelTest {

    private lateinit var fakeVacancyRepository: FakeVacancyRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver
    private lateinit var fakeRemoteConfig: FakeRemoteConfig

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeVacancyRepository = FakeVacancyRepository()
        fakeUserRepository = FakeUserRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
        fakeRemoteConfig = FakeRemoteConfig()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = VacancyViewModel(
        vacancyRepository = fakeVacancyRepository,
        userRepository = fakeUserRepository,
        displayMessageResolver = fakeDisplayMessageResolver,
        remoteConfig = fakeRemoteConfig,
    )

    // region init — contentState 전이

    @Test
    fun `init 시 fetch 성공하고 강의가 있으면 Loaded 상태가 된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)

        val viewModel = createViewModel()

        assertEquals(
            VacancyUiState(
                contentState = VacancyUiState.ContentState.Loaded(
                    vacancyLecturesWithSelection = listOf(lecture1.toDataWithState(false)),
                ),
            ),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `init 시 fetch 성공하고 강의가 비어 있으면 Empty 상태가 된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = emptyList()

        val viewModel = createViewModel()

        assertEquals(
            VacancyUiState(contentState = VacancyUiState.ContentState.Empty),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `init 시 fetch 실패하면 Error 상태가 된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "서버 에러"))

        val viewModel = createViewModel()

        assertEquals(
            VacancyUiState(contentState = VacancyUiState.ContentState.Error),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `init 시 fetch 실패하면 ShowToast 이벤트가 발생한다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "서버 에러"))

        val viewModel = createViewModel()

        viewModel.vacancyUiEvent.test {
            assertEquals(VacancyUiEvent.ShowToast("서버 에러"), awaitItem())
        }
    }

    @Test
    fun `init 시 fetch 실패 시 AuthError이면 LoggedOut 이벤트가 발생한다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))

        val viewModel = createViewModel()

        // replay=1이므로 init에서 마지막으로 emit된 LoggedOut만 수신 가능
        viewModel.vacancyUiEvent.test {
            assertEquals(VacancyUiEvent.LoggedOut, awaitItem())
        }
    }

    @Test
    fun `init 시 fetch 실패 시 AuthError이면 postForceLogout이 호출된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))
        fakeUserRepository.postForceLogoutResult = Result.Success(Unit)

        createViewModel()

        assertEquals(true, fakeUserRepository.postForceLogoutCalled)
    }

    @Test
    fun `init 시 fetch 성공하면 빈자리 알림 가능한 강의가 상위에 정렬된다`() = runTest {
        val vacantLecture = searchedLecture(id = "lec-vacant", courseTitle = "빈자리 강의", wasFull = true, registrationCount = 10)
        val fullLecture = searchedLecture(id = "lec-full", courseTitle = "꽉 찬 강의", wasFull = false, registrationCount = 30)
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(fullLecture, vacantLecture)

        val viewModel = createViewModel()

        assertEquals(
            VacancyUiState(
                contentState = VacancyUiState.ContentState.Loaded(
                    vacancyLecturesWithSelection = listOf(
                        vacantLecture.toDataWithState(false),
                        fullLecture.toDataWithState(false),
                    ),
                ),
            ),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `init 시 firstVacancyVisit이 true이면 Intro 다이얼로그가 열린다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = true
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)

        val viewModel = createViewModel()

        assertEquals(
            VacancyUiState(
                contentState = VacancyUiState.ContentState.Loaded(
                    vacancyLecturesWithSelection = listOf(lecture1.toDataWithState(false)),
                ),
                dialogState = VacancyUiState.DialogState.Intro,
            ),
            viewModel.vacancyUiState.value,
        )
    }

    // endregion

    // region Source 반응 — vacancyLectures 변화

    @Test
    fun `vacancyLectures가 변화하면 contentState가 갱신된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        val viewModel = createViewModel()
        val before = viewModel.vacancyUiState.value

        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1, lecture2)

        assertEquals(
            before.copy(
                contentState = VacancyUiState.ContentState.Loaded(
                    vacancyLecturesWithSelection = listOf(
                        lecture1.toDataWithState(false),
                        lecture2.toDataWithState(false),
                    ),
                ),
            ),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `vacancyLectures가 빈 리스트로 변화하면 Empty 상태가 된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        val viewModel = createViewModel()
        val before = viewModel.vacancyUiState.value

        fakeVacancyRepository.vacancyLectures.value = emptyList()

        assertEquals(
            before.copy(contentState = VacancyUiState.ContentState.Empty),
            viewModel.vacancyUiState.value,
        )
    }

    // endregion

    // region showIntroDialog / dismissDialog

    @Test
    fun `showIntroDialog 호출 시 Intro 다이얼로그가 열린다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        val viewModel = createViewModel()
        val before = viewModel.vacancyUiState.value

        viewModel.showIntroDialog()

        assertEquals(
            before.copy(dialogState = VacancyUiState.DialogState.Intro),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `Empty 상태에서 showIntroDialog 호출 시 Intro 다이얼로그가 열린다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        val viewModel = createViewModel()
        val before = viewModel.vacancyUiState.value

        viewModel.showIntroDialog()

        assertEquals(
            before.copy(dialogState = VacancyUiState.DialogState.Intro),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `Intro 다이얼로그가 열린 상태에서 dismissDialog 호출 시 다이얼로그가 닫힌다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        val viewModel = createViewModel()
        viewModel.showIntroDialog()
        val before = viewModel.vacancyUiState.value

        viewModel.dismissDialog()

        assertEquals(
            before.copy(dialogState = VacancyUiState.DialogState.None),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `init 시 firstVacancyVisit이 true이면 setVacancyVisited가 호출된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = true
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)

        createViewModel()

        assertEquals(true, fakeVacancyRepository.setVacancyVisitedCalled)
    }

    // endregion

    // region showDeleteDialog

    @Test
    fun `showDeleteDialog 호출 시 ConfirmDeleteSelected 다이얼로그가 열린다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        val viewModel = createViewModel()
        val before = viewModel.vacancyUiState.value

        viewModel.showDeleteDialog()

        assertEquals(
            before.copy(dialogState = VacancyUiState.DialogState.ConfirmDeleteSelected),
            viewModel.vacancyUiState.value,
        )
    }

    // endregion

    // region toggleEditMode

    @Test
    fun `toggleEditMode 호출 시 isEditMode가 토글된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        val viewModel = createViewModel()
        val before = viewModel.vacancyUiState.value

        viewModel.toggleEditMode()

        assertEquals(
            before.copy(isEditMode = true),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `toggleEditMode 시 선택 상태가 초기화된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1, lecture2)
        val viewModel = createViewModel()
        viewModel.toggleEditMode()
        viewModel.toggleLectureSelected(lecture1.id)
        val before = viewModel.vacancyUiState.value

        viewModel.toggleEditMode()

        val beforeContent = before.contentState as VacancyUiState.ContentState.Loaded
        assertEquals(
            before.copy(
                isEditMode = false,
                contentState = beforeContent.copy(
                    vacancyLecturesWithSelection = beforeContent.vacancyLecturesWithSelection.map {
                        it.copy(state = false)
                    },
                    deleteButtonEnabled = false,
                ),
            ),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `toggleEditMode 시 Loaded가 아니면 상태가 변하지 않는다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        val viewModel = createViewModel()
        val before = viewModel.vacancyUiState.value

        viewModel.toggleEditMode()

        assertEquals(before, viewModel.vacancyUiState.value)
    }

    // endregion

    // region toggleLectureSelected

    @Test
    fun `toggleLectureSelected 호출 시 해당 강의가 선택된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1, lecture2)
        val viewModel = createViewModel()
        val before = viewModel.vacancyUiState.value

        viewModel.toggleLectureSelected(lecture1.id)

        val beforeContent = before.contentState as VacancyUiState.ContentState.Loaded
        assertEquals(
            before.copy(
                contentState = beforeContent.copy(
                    vacancyLecturesWithSelection = beforeContent.vacancyLecturesWithSelection.map {
                        if (it.item.id == lecture1.id) it.copy(state = true) else it
                    },
                    deleteButtonEnabled = true,
                ),
            ),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `이미 선택된 강의를 toggleLectureSelected 하면 선택이 해제된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        val viewModel = createViewModel()
        viewModel.toggleLectureSelected(lecture1.id)
        val before = viewModel.vacancyUiState.value

        viewModel.toggleLectureSelected(lecture1.id)

        val beforeContent = before.contentState as VacancyUiState.ContentState.Loaded
        assertEquals(
            before.copy(
                contentState = beforeContent.copy(
                    vacancyLecturesWithSelection = beforeContent.vacancyLecturesWithSelection.map {
                        it.copy(state = false)
                    },
                    deleteButtonEnabled = false,
                ),
            ),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `toggleLectureSelected 시 Loaded가 아니면 상태가 변하지 않는다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        val viewModel = createViewModel()
        val before = viewModel.vacancyUiState.value

        viewModel.toggleLectureSelected("lec-1")

        assertEquals(before, viewModel.vacancyUiState.value)
    }

    // endregion

    // region deleteSelectedLectures

    @Test
    fun `deleteSelectedLectures 호출 시 다이얼로그가 닫힌다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1, lecture2)
        fakeVacancyRepository.removeVacancyLectureResult = Result.Success(Unit)
        val viewModel = createViewModel()
        viewModel.toggleLectureSelected(lecture1.id)
        viewModel.showDeleteDialog()
        val before = viewModel.vacancyUiState.value

        viewModel.deleteSelectedLectures()

        assertEquals(
            before.copy(dialogState = VacancyUiState.DialogState.None),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `deleteSelectedLectures 호출 시 선택된 강의들에 대해 removeVacancyLecture가 호출된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1, lecture2)
        fakeVacancyRepository.removeVacancyLectureResult = Result.Success(Unit)
        val viewModel = createViewModel()
        viewModel.toggleLectureSelected(lecture1.id)

        viewModel.deleteSelectedLectures()

        assertEquals(listOf(lecture1), fakeVacancyRepository.removeVacancyLectureCalledWith)
    }

    @Test
    fun `deleteSelectedLectures 호출 시 Loaded가 아니면 아무 동작도 하지 않는다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = emptyList()
        val viewModel = createViewModel()
        val before = viewModel.vacancyUiState.value

        viewModel.deleteSelectedLectures()

        assertEquals(before, viewModel.vacancyUiState.value)
        assertEquals(emptyList<Any>(), fakeVacancyRepository.removeVacancyLectureCalledWith)
    }

    // endregion

    // region openSugangSnu

    @Test
    fun `openSugangSnu 호출 시 OpenWebPage 이벤트가 발생한다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        fakeRemoteConfig.sugangSNUUrl.value = "https://sugang.snu.ac.kr"
        val viewModel = createViewModel()

        viewModel.vacancyUiEvent.test {
            viewModel.openSugangSnu()
            assertEquals(VacancyUiEvent.OpenWebPage("https://sugang.snu.ac.kr"), awaitItem())
        }
    }

    // endregion

    // region reloadVacancyLectures

    @Test
    fun `reloadVacancyLectures 성공 시 isRefreshing이 false로 복귀한다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        val viewModel = createViewModel()

        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)

        viewModel.reloadVacancyLectures()

        assertEquals(
            VacancyUiState(
                contentState = VacancyUiState.ContentState.Loaded(
                    vacancyLecturesWithSelection = listOf(lecture1.toDataWithState(false)),
                ),
                isRefreshing = false,
            ),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `reloadVacancyLectures 실패 시 Error 상태가 된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        val viewModel = createViewModel()

        fakeVacancyRepository.fetchVacancyLecturesResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "재로드 실패"))

        viewModel.reloadVacancyLectures()

        assertEquals(
            VacancyUiState(contentState = VacancyUiState.ContentState.Error),
            viewModel.vacancyUiState.value,
        )
    }

    @Test
    fun `reloadVacancyLectures 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        val viewModel = createViewModel()

        fakeVacancyRepository.fetchVacancyLecturesResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "재로드 실패"))

        viewModel.vacancyUiEvent.test {
            viewModel.reloadVacancyLectures()
            assertEquals(VacancyUiEvent.ShowToast("재로드 실패"), awaitItem())
        }
    }

    @Test
    fun `reloadVacancyLectures 실패 시 AuthError이면 ShowToast와 LoggedOut 이벤트가 발생한다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        val viewModel = createViewModel()

        fakeVacancyRepository.fetchVacancyLecturesResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))

        viewModel.vacancyUiEvent.test {
            viewModel.reloadVacancyLectures()
            assertEquals(VacancyUiEvent.ShowToast("인증 만료"), awaitItem())
            assertEquals(VacancyUiEvent.LoggedOut, awaitItem())
        }
    }

    @Test
    fun `reloadVacancyLectures 실패 시 AuthError이면 postForceLogout이 호출된다`() = runTest {
        fakeVacancyRepository.firstVacancyVisit.value = false
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        fakeUserRepository.postForceLogoutResult = Result.Success(Unit)
        val viewModel = createViewModel()

        fakeVacancyRepository.fetchVacancyLecturesResult =
            Result.Fail(WrongUserToken(displayTitle = "", displayMessage = "인증 만료"))

        viewModel.reloadVacancyLectures()

        assertEquals(true, fakeUserRepository.postForceLogoutCalled)
    }

    // endregion

}
