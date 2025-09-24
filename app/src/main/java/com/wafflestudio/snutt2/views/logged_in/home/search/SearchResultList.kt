package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.components.compose.AnimatedLazyRow
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchResultList(
    scope: CoroutineScope,
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
    searchResultListState: SearchResultListState,
    selectedTags: List<TagDto>,
    lazyListState: LazyListState,
    onToggleTagAndQuery: (tag: TagDto) -> Unit,
    reviewBottomSheetReviewWebViewContainer: ReviewWebViewContainer,
) {
    Column {
        AnimatedLazyRow(itemList = selectedTags, itemKey = { it.toItemKey() }) {
            TagCell(
                tagDto = it,
                onClick = {
                    onToggleTagAndQuery(it)
                },
            )
        }

        when (searchResultListState) {
            SearchResultListState.PLACEHOLDER -> {
                SearchPlaceHolder(
                    modifier = Modifier.logImpression(AnalyticsScreen.SearchHome),
                )
            }

            SearchResultListState.EMPTY -> {
                SearchEmptyPlaceholder(
                    modifier = Modifier
                        .logImpression(AnalyticsScreen.SearchEmpty),
                )
            }

            SearchResultListState.HAS_RESULTS -> {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .logImpression(AnalyticsScreen.SearchList),
                ) {
                    items(searchResultPagingItems) { lectureDataWithState ->
                        lectureDataWithState?.let {
                            LectureListItem(
                                lectureDataWithState,
                                reviewBottomSheetReviewWebViewContainer,
                                false,
                            )
                        }
                    }
                }
            }
        }
    }
}
