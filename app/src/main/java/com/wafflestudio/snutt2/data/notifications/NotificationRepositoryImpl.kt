package com.wafflestudio.snutt2.data.notifications

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.wafflestudio.snutt2.data.mapper.toDomain
import com.wafflestudio.snutt2.domainmodel.Notification
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.error.toDomainError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(private val api: SNUTTRestApi) :
    NotificationRepository {
    override fun getNotificationListStream(): Flow<PagingData<Notification>> {
        return Pager(
            config = PagingConfig(
                pageSize = NOTIFICATIONS_LOAD_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { NotificationPagingSource(api) },
        ).flow.map { pagingData ->
            pagingData.map { notification ->
                notification.toDomain()
            }
        }
    }

    override val notificationCount: MutableStateFlow<Long> = MutableStateFlow(0)

    override suspend fun fetchNotificationCount(): Result<Unit> {
        return try {
            notificationCount.value = api._getNotificationCount().count
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    companion object {
        private const val NOTIFICATIONS_LOAD_PAGE_SIZE = 30
    }
}
