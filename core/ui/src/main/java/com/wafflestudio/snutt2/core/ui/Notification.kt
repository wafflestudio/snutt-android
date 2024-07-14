package com.wafflestudio.snutt2.core.ui

import androidx.paging.PagingData
import com.wafflestudio.snutt2.core.model.data.Notification

sealed interface NotificationsUiState {
    data object Loading : NotificationsUiState

    data object Error : NotificationsUiState

    data class Success(
        val notifications: PagingData<Notification>,
    ) : NotificationsUiState
}
