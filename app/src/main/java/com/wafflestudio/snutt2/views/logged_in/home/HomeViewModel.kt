package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.TableRepository
import com.wafflestudio.snutt2.data.UserRepository
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.lib.network.ApiOnError
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val apiOnError: ApiOnError
) : ViewModel() {

    fun refreshData() {
        tableRepository.fetchDefaultTable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)

        tableRepository.fetchTableList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)

        userRepository.fetchUserInfo()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
    }

    suspend fun getUncheckedNotificationsExist(): Boolean {
        val count = notificationRepository.getNotificationCount()
        return count > 0
    }
}
