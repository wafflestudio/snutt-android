package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.wafflestudio.snutt2.domain_model.Notification

sealed interface NotificationUiState {
    data class Success(val notificationList: LazyPagingItems<Notification>) : NotificationUiState
    data object Error : NotificationUiState
    data object Loading : NotificationUiState
    data object Empty : NotificationUiState
}

fun LazyPagingItems<Notification>.notificationUiState(): NotificationUiState {
    val refreshState = loadState.refresh
    val appendState = loadState.append

    return when {
        refreshState is LoadState.Loading -> NotificationUiState.Loading
        refreshState is LoadState.Error -> NotificationUiState.Error
        appendState.endOfPaginationReached && itemCount < 1 -> NotificationUiState.Empty
        else -> NotificationUiState.Success(this)
    }
}
