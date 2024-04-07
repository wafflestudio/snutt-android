package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    // TODO 1: 의존성 주입
) : ViewModel() {

    private val _notificationList = MutableStateFlow<PagingData<NotificationDto>>(PagingData.empty())
    // TODO 2: UI에 상태 제공

    init {
        viewModelScope.launch {
            // TODO 3: API로 데이터 가져오기
        }
    }
}
