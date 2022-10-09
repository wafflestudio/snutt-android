package com.wafflestudio.snutt2.views.logged_in.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.dto.GetPopupResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _popupState = MutableStateFlow(false)
    val popupState = _popupState.asStateFlow()

    private val _popupInfo = MutableStateFlow<GetPopupResults.Popup?>(null)
    val popupInfo = _popupInfo.asStateFlow()

    private val popupTimeStamp = userRepository.popupTimeStamp.stateIn(
        viewModelScope, SharingStarted.Eagerly, mapOf()
    )

    suspend fun refreshData() {
        try {
            coroutineScope {
                awaitAll(
                    async { tableRepository.fetchDefaultTable() },
                    async { tableRepository.getTableList() },
                    async { userRepository.fetchUserInfo() },
                )
            }
        } catch (e: Exception) {
            // do nothing (just sync)
        }
    }

    suspend fun getUncheckedNotificationsExist(): Boolean {
        val count = notificationRepository.getNotificationCount()
        return count > 0
    }


    suspend fun fetchPopup() {
        try {
            userRepository.getPopup().popups.firstOrNull { popupFromServer ->
                val expireMillis: Long? = popupTimeStamp.value[popupFromServer.key]
                val currentMillis = System.currentTimeMillis()
                (expireMillis == null || currentMillis >= expireMillis)
            }?.let { popupToShow ->
                _popupInfo.emit(popupToShow)
                _popupState.emit(true)
            }
        } catch (_: Exception) {}
    }

    suspend fun updatePopupTimeStampByHiddenDays() {
        popupInfo.value?.let {
            val expireDayInMillis = it.popupHideDays?.let {
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(it.toLong())
            } ?: INFINITE_LONG_MILLIS
            userRepository.updatePopupTimeStamp(it.key, expireDayInMillis)
            _popupState.emit(false)
        }
    }

    suspend fun closePopup() {
        _popupState.emit(false)
    }
    companion object {
        const val INFINITE_LONG_MILLIS = Long.MAX_VALUE
    }
}
