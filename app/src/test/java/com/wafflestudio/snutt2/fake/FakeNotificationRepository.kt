package com.wafflestudio.snutt2.fake

import androidx.paging.PagingData
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.domainmodel.Notification
import com.wafflestudio.snutt2.data.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNotificationRepository : NotificationRepository {

    // --- StateFlow ---
    override val notificationCount = MutableStateFlow(0L)

    // --- 테스트 제어용 필드 ---
    var fetchNotificationCountResult: Result<Unit> = Result.Success(Unit)
    var fetchNotificationCountCalled = false
        private set

    // --- 인터페이스 구현 ---
    override suspend fun fetchNotificationCount(): Result<Unit> {
        fetchNotificationCountCalled = true
        return fetchNotificationCountResult
    }

    // --- 미사용 메서드 ---
    override fun getNotificationListStream(): Flow<PagingData<Notification>> = TODO("Not used in this test")
}
