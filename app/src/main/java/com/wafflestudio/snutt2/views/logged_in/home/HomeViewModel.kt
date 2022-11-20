package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    suspend fun refreshDataAndCheckToken(token: String): String {
        try {
            coroutineScope {
                awaitAll(
                    async { tableRepository.fetchDefaultTable() },
                    async { tableRepository.getTableList() },
                    async { userRepository.fetchUserInfo() },
                    async { userRepository.fetchAndSetPopup() },
                )
            }
        } catch (e: Exception) {
            // when
            return ""
        }
        return token
    }

    suspend fun getUncheckedNotificationsExist(): Boolean {
        val count = notificationRepository.getNotificationCount()
        return count > 0
    }
}
