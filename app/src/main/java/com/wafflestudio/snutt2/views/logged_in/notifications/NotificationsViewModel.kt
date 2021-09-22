package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.data.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    val refreshDataEvent = notificationsRepository.refreshDataEvent

    fun getNotifications(): Flowable<PagingData<NotificationDto>> {
        return notificationsRepository
            .getPagedNotifications()
    }
}
