package com.wafflestudio.snutt2.data.notifications

import androidx.paging.Pager
import androidx.paging.PagingConfig
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
        return Pager(
            config = PagingConfig(
                pageSize = NOTIFICATIONS_LOAD_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {NotificationPagingSource(
                api = api
            )}
        ).flow
    }

    override suspend fun getNotificationCount(): Long {
        return api._getNotificationCount().count
    }

    companion object {
        private const val NOTIFICATIONS_LOAD_PAGE_SIZE = 30
    }
}
