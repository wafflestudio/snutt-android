package com.wafflestudio.snutt2.fake

import androidx.paging.PagingData
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.domainmodel.Notification
import com.wafflestudio.snutt2.lib.network.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNotificationRepository : NotificationRepository {

    // --- StateFlow ---
    override val notificationCount = MutableStateFlow(0L)

    // --- 미사용 메서드 ---
    override fun getNotificationListStream(): Flow<PagingData<Notification>> = TODO("Not used in this test")
    override suspend fun fetchNotificationCount(): Result<Unit> = TODO("Not used in this test")
}
