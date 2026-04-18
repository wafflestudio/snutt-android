package com.wafflestudio.snutt2.feature.notifications

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.wafflestudio.snutt2.domain.model.Notification

// NOTE: ViewModel이 소유하는 UiState가 아니라, LazyPagingItems의 로드 상태를 UI 레이어에서 변환하는 편의 타입.
// Compose 의존 없는 paging 라이브러리로 마이그레이션하면 ViewModel이 직접 소유하는 UiState로 전환할 것.
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
