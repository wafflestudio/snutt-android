package com.wafflestudio.snutt2.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wafflestudio.snutt2.core.data.source.NotificationPagingSource
import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.core.network.model.NotificationDto
import com.wafflestudio.snutt2.core.qualifiers.CoreNetwork
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    @CoreNetwork private val api: SNUTTNetworkDataSource
) : NotificationRepository {
    override suspend fun getNotificationResultStream(): Flow<PagingData<NotificationDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = NOTIFICATIONS_LOAD_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { NotificationPagingSource(api) },
        ).flow
    }

    override suspend fun getNotificationCount(): Long {
        return api._getNotificationCount().count
    }

    companion object {
        private const val NOTIFICATIONS_LOAD_PAGE_SIZE = 30
    }
}
