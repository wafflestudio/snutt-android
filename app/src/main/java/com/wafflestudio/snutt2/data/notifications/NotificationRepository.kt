package com.wafflestudio.snutt2.data.notifications

import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    suspend fun getNotificationResultStream(): Flow<PagingData<NotificationDto>>

    suspend fun getNotificationCount(): Long

}
