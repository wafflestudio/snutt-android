package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.domain_model.Notification
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _notificationResult =
        MutableStateFlow<PagingData<NotificationDto>>(PagingData.empty())
    val notificationResult: StateFlow<PagingData<NotificationDto>> = _notificationResult

    private val _notificationList =
        MutableStateFlow<PagingData<Notification>>(PagingData.empty())
    val notificationList: StateFlow<PagingData<Notification>> = _notificationList

    init {
        viewModelScope.launch {
            when (snuttAppState) {
                SNUTTAppState.NORMAL -> {
                    notificationRepository.getNotificationResultStream()
                        .cachedIn(viewModelScope)
                        .collect {
                            _notificationResult.emit(it)
                        }
                }
                SNUTTAppState.REFACTOR -> {
                    notificationRepository.getNotificationListStream()
                        .cachedIn(viewModelScope)
                        .collect {
                            _notificationList.emit(it)
                        }
                }
            }
        }
    }
}
