package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.wafflestudio.snutt2.domainmodel.DiaryWrite

sealed interface DiaryWriteUiState {
    data class Success(val diaryList: LazyPagingItems<DiaryWrite>) : DiaryWriteUiState
    data object Error : DiaryWriteUiState
    data object Loading : DiaryWriteUiState
    data object Empty : DiaryWriteUiState
}

fun LazyPagingItems<DiaryWrite>.diaryWriteUiState(): DiaryWriteUiState {
    val refreshState = loadState.refresh
    val appendState = loadState.append

    return when {
        refreshState is LoadState.Loading -> DiaryWriteUiState.Loading
        refreshState is LoadState.Error -> DiaryWriteUiState.Error
        appendState.endOfPaginationReached && itemCount < 1 -> DiaryWriteUiState.Empty
        else -> DiaryWriteUiState.Success(this)
    }
}
