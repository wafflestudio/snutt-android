package com.wafflestudio.snutt2.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.domain.model.Notification
import com.wafflestudio.snutt2.navigation.DeeplinkAction
import com.wafflestudio.snutt2.navigation.DeeplinkParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val deeplinkParser: DeeplinkParser,
) : ViewModel() {

    private val _notificationList =
        MutableStateFlow<PagingData<Notification>>(PagingData.empty())
    val notificationList: StateFlow<PagingData<Notification>> = _notificationList

    private val _uiEvent = MutableSharedFlow<NotificationUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            notificationRepository.getNotificationListStream()
                .cachedIn(viewModelScope)
                .collect {
                    _notificationList.emit(it)
                }
        }
    }

    fun onNotificationClick(notification: Notification) {
        val action = deeplinkParser.parse(notification.deeplink ?: return) ?: return

        viewModelScope.launch {
            when (action) {
                is DeeplinkAction.TimetableLecture -> {
                    _uiEvent.emit(
                        NotificationUiEvent.NavigateToTimetableLectureDetail(
                            lectureId = action.lectureId,
                            timetableId = action.timetableId,
                        ),
                    )
                }

                is DeeplinkAction.Bookmark -> {
                    _uiEvent.emit(
                        NotificationUiEvent.NavigateToBookmarkLectureDetail(
                            lectureId = action.lectureId,
                            year = action.year,
                            semester = action.semester,
                        ),
                    )
                }

                is DeeplinkAction.Friends -> {
                    _uiEvent.emit(NotificationUiEvent.NavigateToFriends)
                }
            }
        }
    }
}

sealed interface NotificationUiEvent {
    data class NavigateToTimetableLectureDetail(
        val lectureId: String,
        val timetableId: String,
    ) : NotificationUiEvent

    data class NavigateToBookmarkLectureDetail(
        val lectureId: String,
        val year: Long,
        val semester: Long,
    ) : NotificationUiEvent

    data object NavigateToFriends : NotificationUiEvent
}
