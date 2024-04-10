package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepository
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notificationList = MutableStateFlow<PagingData<NotificationDto>>(PagingData.empty())
    val notificationList = _notificationList.asStateFlow()

    init {
        viewModelScope.launch {
            notificationRepository.getNotificationResultStream().collect{
                _notificationList.emit(it)
            }
        }
    }
}
