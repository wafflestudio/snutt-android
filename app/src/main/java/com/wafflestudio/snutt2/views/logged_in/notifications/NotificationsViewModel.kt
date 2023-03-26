package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    // TODO
}
