package com.wafflestudio.snutt2.views.logged_in.notification

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val apiOnError: ApiOnError
) : ViewModel() {

    val notifications: Flow<PagingData<NotificationDto>> =
        notificationRepository.getNotification(10, 0, 10).flow
}
