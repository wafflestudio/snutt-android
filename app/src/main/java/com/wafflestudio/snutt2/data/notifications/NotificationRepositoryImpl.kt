package com.wafflestudio.snutt2.data.notifications

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchPagingSource
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepositoryImpl.Companion.LECTURES_LOAD_PAGE_SIZE
import com.wafflestudio.snutt2.domainmodel.Notification
import com.wafflestudio.snutt2.domainmodel.domainModel
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(private val api: SNUTTRestApi) :
    NotificationRepository {
    override fun getNotificationListStream(): Flow<PagingData<NotificationDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = NOTIFICATIONS_LOAD_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                NotificationPagingSource(api)
            }
        ).flow
    }

    override suspend fun getNotificationCount(): Long {
        val response = api._getNotificationCount()
        return response.count
    }

    companion object {
        private const val NOTIFICATIONS_LOAD_PAGE_SIZE = 30
    }
}
