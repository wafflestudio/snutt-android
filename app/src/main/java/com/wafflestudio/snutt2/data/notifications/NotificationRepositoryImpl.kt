package com.wafflestudio.snutt2.data.notifications

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.wafflestudio.snutt2.domainmodel.Notification
import com.wafflestudio.snutt2.domainmodel.domainModel
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(private val api: SNUTTRestApi) :
    NotificationRepository {
    override fun getNotificationListStream(): Flow<PagingData<Notification>> {
        TODO("구현하기 - LectureSearchRepository 참고")
    }

    override suspend fun getNotificationCount(): Long {
        TODO("구현하기 - LectureSearchRepository 참고")
    }

    companion object {
        private const val NOTIFICATIONS_LOAD_PAGE_SIZE = 30
    }
}
