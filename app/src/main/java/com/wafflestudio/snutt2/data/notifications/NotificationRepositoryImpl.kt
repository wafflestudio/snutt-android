package com.wafflestudio.snutt2.data.notifications

import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(private val api: SNUTTRestApi) : NotificationRepository {

    // TODO: https://developer.android.com/codelabs/android-paging-basics#5
    override suspend fun getNotificationResultStream(): Flow<PagingData<NotificationDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun getNotificationCount(): Long {
        // TODO 2: 알림 개수 반환하기
        return 0L
    }

    companion object {
        private const val NOTIFICATIONS_LOAD_PAGE_SIZE = 30
    }
}
