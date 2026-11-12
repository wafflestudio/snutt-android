package com.wafflestudio.snutt2.feature.notifications

import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import app.cash.turbine.test
import com.wafflestudio.snutt2.domain.model.Notification
import com.wafflestudio.snutt2.fake.FakeNotificationRepository
import com.wafflestudio.snutt2.fixture.TestFixtures
import com.wafflestudio.snutt2.navigation.DeeplinkParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NotificationsViewModelTest {

    private lateinit var fakeNotificationRepository: FakeNotificationRepository
    private lateinit var deeplinkParser: DeeplinkParser
    private lateinit var viewModel: NotificationsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeNotificationRepository = FakeNotificationRepository()
        deeplinkParser = DeeplinkParser(appScheme = "snutt://")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = NotificationsViewModel(
        notificationRepository = fakeNotificationRepository,
        deeplinkParser = deeplinkParser,
    )

    // region onListLoadSuccess

    @Test
    fun `init does not reset notification count`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()

        assertEquals(false, fakeNotificationRepository.resetNotificationCountCalled)
    }

    @Test
    fun `onListLoadSuccess resets notification count`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()

        viewModel.onListLoadSuccess()

        assertEquals(true, fakeNotificationRepository.resetNotificationCountCalled)
    }

    // endregion

    // region onNotificationClick

    @Test
    fun `external deeplink emits open external link event`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(deeplink = "https://snutt.kr/notice")

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            assertEquals(
                NotificationUiEvent.OpenExternalLink("https://snutt.kr/notice"),
                awaitItem(),
            )
        }
    }

    @Test
    fun `timetable lecture deeplink emits navigation with lecture and timetable IDs`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(
            deeplink = "snutt://timetable-lecture?timetableId=table-42&lectureId=lecture-17",
        )

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            assertEquals(NotificationUiEvent.NavigateToTimetableLectureDetail("lecture-17", "table-42"), awaitItem())
        }
    }

    @Test
    fun `bookmark deeplink emits navigation with lecture and semester`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(
            deeplink = "snutt://bookmarks?year=2025&semester=AUTUMN&lectureId=lecture-23",
        )

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            assertEquals(NotificationUiEvent.NavigateToBookmarkLectureDetail("lecture-23", 2025L, 3L), awaitItem())
        }
    }

    @Test
    fun `friends deeplink emits friends navigation`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(deeplink = "snutt://friends")

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            assertEquals(NotificationUiEvent.NavigateToFriends, awaitItem())
        }
    }

    @Test
    fun `null deeplink emits no event`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(deeplink = null)

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            expectNoEvents()
        }
    }

    @Test
    fun `unknown internal deeplink emits no event`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(deeplink = "snutt://unknown")

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            expectNoEvents()
        }
    }

    @Test
    fun `timetable lecture deeplink without timetable ID emits no event`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(deeplink = "snutt://timetable-lecture?lectureId=lecture-17")

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            expectNoEvents()
        }
    }

    @Test
    fun `timetable lecture deeplink without lecture ID emits no event`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(deeplink = "snutt://timetable-lecture?timetableId=table-42")

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            expectNoEvents()
        }
    }

    @Test
    fun `bookmark deeplink without year emits no event`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(deeplink = "snutt://bookmarks?semester=SPRING&lectureId=lecture-23")

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            expectNoEvents()
        }
    }

    @Test
    fun `bookmark deeplink with invalid year emits no event`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(deeplink = "snutt://bookmarks?year=invalid&semester=SPRING&lectureId=lecture-23")

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            expectNoEvents()
        }
    }

    @Test
    fun `bookmark deeplink without semester emits no event`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(deeplink = "snutt://bookmarks?year=2025&lectureId=lecture-23")

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            expectNoEvents()
        }
    }

    @Test
    fun `bookmark deeplink with unknown semester navigates with semester zero`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(
            deeplink = "snutt://bookmarks?year=2025&semester=UNKNOWN&lectureId=lecture-23",
        )

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            assertEquals(NotificationUiEvent.NavigateToBookmarkLectureDetail("lecture-23", 2025L, 0L), awaitItem())
        }
    }

    @Test
    fun `bookmark deeplink without lecture ID emits no event`() = runTest {
        fakeNotificationRepository.notificationPagingDataFlow = flowOf(PagingData.empty())
        viewModel = createViewModel()
        val notification = TestFixtures.notification(deeplink = "snutt://bookmarks?year=2025&semester=SPRING")

        viewModel.uiEvent.test {
            viewModel.onNotificationClick(notification)
            expectNoEvents()
        }
    }

    // endregion

    // region source: notification list

    @Test
    fun `notification list updates with each source emission`() = runTest {
        val source = MutableSharedFlow<PagingData<Notification>>()
        fakeNotificationRepository.notificationPagingDataFlow = source
        viewModel = createViewModel()
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val presenter = object : PagingDataPresenter<Notification>(mainContext = dispatcher) {
            override suspend fun presentPagingDataEvent(event: PagingDataEvent<Notification>) = Unit
        }
        backgroundScope.launch(dispatcher) {
            viewModel.notificationList.collectLatest { presenter.collectFrom(it) }
        }
        val first = TestFixtures.notification(title = "첫 번째 알림")
        val second = TestFixtures.notification(title = "새 알림", deeplink = "snutt://friends")

        source.emit(PagingData.from(listOf(first)))
        advanceUntilIdle()
        assertEquals(listOf(first), presenter.snapshot().items)

        source.emit(PagingData.from(listOf(second, first)))
        advanceUntilIdle()
        assertEquals(listOf(second, first), presenter.snapshot().items)
    }

    // endregion
}
