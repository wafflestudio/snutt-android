package com.wafflestudio.snutt2.views.logged_in.home

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.data.popup.PopupRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.table_display.TableDisplayRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalAnimationApi::class)
@HiltViewModel
class HomePageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @param:ApplicationContext private val context: Context,
    private val notificationRepository: NotificationRepository,
    private val tableRepository: TableRepository,
    private val tableDisplayRepository: TableDisplayRepository,
    private val userRepository: UserRepository,
    private val popupRepository: PopupRepository,
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
            tableRepository.currentTable,
            tableDisplayRepository.tableTrimParam,
        ) { _, _ ->
            TimetableWidgetProvider.refreshWidget(context)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            runCatching { popupRepository.ensurePopupsFetched() }
        }

        popupRepository.popups
            .filterNotNull()
            .onEach { popups ->
                val popup = popups.firstOrNull()
                _uiState.update {
                    it.copy(
                        shouldShowPopup = popup != null,
                        popupImageUri = popup?.imageUri ?: "",
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onNavigateLectureDetail(lecture: LocalLecture) {
        viewModelScope.launch {
            val tableId = tableRepository.currentTable.value?.summary?.id
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
            runCatching { popupRepository.closePopupWithHiddenDays() }
        }
    }

    fun closePopup() {
        viewModelScope.launch {
            runCatching { popupRepository.closePopup() }
        }
    }

    fun onPopupImageClick() {
        val linkUrl = popupRepository.popups.value?.firstOrNull()?.linkUrl ?: return
        viewModelScope.launch {
            _uiEvent.emit(HomePageUiEvent.OpenUrl(linkUrl))
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
