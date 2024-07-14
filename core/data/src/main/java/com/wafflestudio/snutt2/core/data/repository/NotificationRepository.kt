package com.wafflestudio.snutt2.core.data.repository

import androidx.paging.PagingData
import com.wafflestudio.snutt2.core.model.data.Notification
import com.wafflestudio.snutt2.core.network.model.NotificationDto
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    suspend fun getNotificationResultStream(): Flow<PagingData<Notification>>

    suspend fun getNotificationCount(): Long
}
