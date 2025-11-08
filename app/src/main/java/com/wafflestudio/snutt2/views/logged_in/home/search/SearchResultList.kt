package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.components.compose.AnimatedLazyRow
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchResultList(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
    searchResultListState: SearchResultListState,
    selectedTags: List<TagDto>,
    lazyListState: LazyListState,
    onToggleTagAndQuery: (tag: TagDto) -> Unit,
    onToggleLectureSelection: (LectureDto) -> Unit,
    onClickLectureDetail: (LectureDto) -> Unit,
    onClickReview: (LectureDto) -> Unit,
    onClickBookmark: (LectureDto, Boolean) -> Unit,
    onClickVacancy: (LectureDto, Boolean) -> Unit,
    onToggleLectureContained: (LectureDto, Boolean) -> Unit,
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
                            ExpandableLectureListItem(
                                lectureDataWithState,
                                onClickLectureDetail = onClickLectureDetail,
                                onClickReview = onClickReview,
                                onClickBookmark = onClickBookmark,
                                onClickVacancy = onClickVacancy,
                                onToggleLectureContained = onToggleLectureContained,
                                onToggleLectureSelection = onToggleLectureSelection,
                            )
                        }
                    }
                }
            }
        }
    }
}
