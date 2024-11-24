package com.wafflestudio.snutt2.data.notifications

import androidx.paging.PagingData
import com.wafflestudio.snutt2.domain_model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getNotificationListStream(): Flow<PagingData<Notification>>

    suspend fun getNotificationCount(): Long
}
