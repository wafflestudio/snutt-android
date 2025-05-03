package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto

enum class SearchResultListState {
    PLACEHOLDER,
    EMPTY,
    HAS_RESULTS,
}

@Composable
fun rememberSearchResultListState(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
): SearchResultListState {
    var hasLoaded by remember { mutableStateOf(false) }
    var listState by remember { mutableStateOf(SearchResultListState.PLACEHOLDER) }
    val loadState = searchResultPagingItems.loadState.refresh

    LaunchedEffect(loadState) {
        if (loadState is LoadState.Loading) {
            hasLoaded = true
        }

        if (loadState is LoadState.NotLoading && hasLoaded) {
            listState = if (searchResultPagingItems.itemCount == 0) {
                SearchResultListState.EMPTY
            } else {
                SearchResultListState.HAS_RESULTS
            }
        }
    }

    return listState
}
