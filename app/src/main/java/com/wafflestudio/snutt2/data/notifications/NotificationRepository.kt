package com.wafflestudio.snutt2.data.notifications

import androidx.paging.PagingData
import com.wafflestudio.snutt2.domainmodel.Notification
import com.wafflestudio.snutt2.data.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NotificationRepository {
    fun getNotificationListStream(): Flow<PagingData<Notification>>

    val notificationCount: StateFlow<Long>

    suspend fun fetchNotificationCount(): Result<Unit>
}
