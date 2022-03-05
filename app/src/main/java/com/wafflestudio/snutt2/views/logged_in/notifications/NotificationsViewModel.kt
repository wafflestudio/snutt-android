package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    suspend fun getNotificationsPagingData(): Flow<PagingData<NotificationDto>> {
        return notificationRepository
            .getNotificationResultStream()
    }
}
