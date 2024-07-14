package com.wafflestudio.snutt2.feature.notifications

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.core.data.repository.NotificationRepository
import com.wafflestudio.snutt2.core.model.data.Notification
import com.wafflestudio.snutt2.core.ui.NotificationsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _notificationList =
        MutableStateFlow<PagingData<Notification>>(PagingData.empty())
    val notificationList: StateFlow<PagingData<Notification>> = _notificationList

    val notificationsUiState: StateFlow<NotificationsUiState> =
        notificationList
            .map<PagingData<Notification>, NotificationsUiState>(NotificationsUiState::Success)
            .onStart { emit(NotificationsUiState.Loading) }
            .catch { emit(NotificationsUiState.Error) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NotificationsUiState.Loading,
            )

    fun getNotifications() {
        viewModelScope.launch {
            notificationRepository.getNotificationResultStream()
                .cachedIn(viewModelScope)
                .collect {
                    _notificationList.emit(it)
                }
        }
    }
}
