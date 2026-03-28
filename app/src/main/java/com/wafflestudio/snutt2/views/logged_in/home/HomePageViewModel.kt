package com.wafflestudio.snutt2.views.logged_in.home

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalAnimationApi::class)
@HiltViewModel
class HomePageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val notificationRepository: NotificationRepository,
    private val currentTableRepository: CurrentTableRepository,
    private val userRepository: UserRepository,
    private val popupState: PopupState,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomePageUiState(
            currentTab = HomeItem.fromTabString(savedStateHandle["initialTab"]) ?: HomeItem.Timetable,
        ),
    )
    val uiState: StateFlow<HomePageUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomePageUiEvent>()
    val uiEvent: SharedFlow<HomePageUiEvent> = _uiEvent.asSharedFlow()

    val accessToken: StateFlow<String> = userRepository.accessToken

    init {
        viewModelScope.launch {
            notificationRepository.notificationCount.collect { notifCount ->
                _uiState.update { it.copy(uncheckedNotificationCount = notifCount) }
            }
        }

        combine(
            currentTableRepository.currentTable,
            userRepository.tableTrimParam,
        ) { _, _ ->
            TimetableWidgetProvider.refreshWidget(context)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            if (popupState.fetched.not()) {
                runCatching { userRepository.fetchAndSetPopup() }
                popupState.fetched = true
                updatePopupUiState()
            }
        }
    }

    fun onNavigateLectureDetail(lecture: LocalLecture) {
        viewModelScope.launch {
            val tableId = currentTableRepository.currentTable.value?.summary?.id
            _uiEvent.emit(HomePageUiEvent.NavigateToLectureDetail(lecture.id, tableId))
        }
    }

    fun updateTab(tab: HomeItem) {
        _uiState.update { it.copy(currentTab = tab) }
        if (tab == HomeItem.Timetable || tab == HomeItem.Settings) {
            viewModelScope.launch {
                runCatching { notificationRepository.fetchNotificationCount() }
            }
        }
    }

    fun closePopupWithHiddenDays() {
        viewModelScope.launch {
            runCatching { userRepository.closePopupWithHiddenDays() }
            updatePopupUiState()
        }
    }

    fun closePopup() {
        viewModelScope.launch {
            runCatching { userRepository.closePopup() }
            updatePopupUiState()
        }
    }

    fun onPopupImageClick() {
        val linkUrl = popupState.popup.firstOrNull()?.linkUrl ?: return
        viewModelScope.launch {
            _uiEvent.emit(HomePageUiEvent.OpenUrl(linkUrl))
        }
    }

    private fun updatePopupUiState() {
        val popup = popupState.popup.firstOrNull()
        _uiState.update {
            it.copy(
                shouldShowPopup = popupState.popup.isNotEmpty(),
                popupImageUri = popup?.imageUri ?: "",
            )
        }
    }
}

data class HomePageUiState(
    val currentTab: HomeItem = HomeItem.Timetable,
    val uncheckedNotificationCount: Long = 0,
    val shouldShowPopup: Boolean = false,
    val popupImageUri: String = "",
)

sealed interface HomePageUiEvent {
    data class NavigateToLectureDetail(val lectureId: String, val tableId: String?) : HomePageUiEvent
    data class OpenUrl(val url: String) : HomePageUiEvent
}
