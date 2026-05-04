package com.wafflestudio.snutt2.feature.notifications

import com.wafflestudio.snutt2.fake.FakeNotificationRepository
import com.wafflestudio.snutt2.navigation.DeeplinkParser
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
    fun `init 만으로는 repository의 resetNotificationCount가 호출되지 않는다`() = runTest {
        viewModel = createViewModel()

        assertEquals(false, fakeNotificationRepository.resetNotificationCountCalled)
    }

    @Test
    fun `onListLoadSuccess 호출 시 repository의 resetNotificationCount가 호출된다`() = runTest {
        viewModel = createViewModel()

        viewModel.onListLoadSuccess()

        assertEquals(true, fakeNotificationRepository.resetNotificationCountCalled)
    }

    @Test
    fun `onListLoadSuccess 호출 시 repository의 notificationCount가 0으로 초기화된다`() = runTest {
        fakeNotificationRepository.notificationCount.value = 7L
        viewModel = createViewModel()

        viewModel.onListLoadSuccess()

        assertEquals(0L, fakeNotificationRepository.notificationCount.value)
    }

    // endregion
}
