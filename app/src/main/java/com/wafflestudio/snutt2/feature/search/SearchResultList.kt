package com.wafflestudio.snutt2.feature.search

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.wafflestudio.snutt2.ui.components.compose.AnimatedLazyRow
import com.wafflestudio.snutt2.domain.model.SearchTag
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.logImpression

@Composable
fun SearchResultList(
    searchResultPagingItems: LazyPagingItems<DataWithState<SearchedLecture, LectureState>>,
    searchResultListState: SearchResultListState,
    selectedTags: List<SearchTag>,
    lazyListState: LazyListState,
    onToggleTag: (SearchTag) -> Unit,
    onToggleLectureSelection: (SearchedLecture) -> Unit,
    onClickLectureDetail: (SearchedLecture) -> Unit,
    onClickReview: (SearchedLecture) -> Unit,
    onClickBookmark: (SearchedLecture) -> Unit,
    onClickVacancy: (SearchedLecture) -> Unit,
    onClickAddOrRemove: (SearchedLecture) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AnimatedLazyRow(itemList = selectedTags, itemKey = { it.toItemKey() }) {
            SearchTagCell(
                modifier = Modifier
                    .animateItem()
                    .padding(horizontal = 5.dp),
                searchTag = it,
                onClick = { onToggleTag(it) },
            )
        }

        when (searchResultListState) {
            SearchResultListState.PLACEHOLDER -> {
                SearchPlaceHolder(
                    modifier = Modifier.logImpression(AnalyticsScreen.SearchHome),
                )
            }

            SearchResultListState.SEARCHED -> {
                // Paging 라이브러리의 한계로 인한 workaround:
                //
                // LazyPagingItems는 실제 PagingData를 수신하기 전에도
                // loadState.refresh = NotLoading, itemCount = 0 을 반환한다.
                // 이는 "검색 결과 0건"과 동일한 상태여서 구별이 불가능하다.
                //
                // SEARCHED 상태 진입과 LazyPagingItems가 새 PagingData를 수신하는 사이에
                // 시간차가 있기 때문에, 이 초기 상태가 한 프레임 이상 노출될 수 있다.
                //
                // 실제 검색은 반드시 Loading → NotLoading 전이를 거치므로,
                // Loading 상태를 한 번이라도 관찰한 이후에만 결과를 확정한다.
                var hasStartedLoading by remember { mutableStateOf(false) }
                if (searchResultPagingItems.loadState.refresh is LoadState.Loading) {
                    hasStartedLoading = true
                }

                val isLoaded = hasStartedLoading &&
                    searchResultPagingItems.loadState.refresh is LoadState.NotLoading
                val isEmpty = isLoaded && searchResultPagingItems.itemCount == 0

                if (isEmpty) {
                    SearchEmptyPlaceholder(
                        modifier = Modifier.logImpression(AnalyticsScreen.SearchEmpty),
                    )
                } else {
                    val listModifier = if (isLoaded) {
                        Modifier
                            .fillMaxSize()
                            .logImpression(AnalyticsScreen.SearchList)
                    } else {
                        Modifier.fillMaxSize()
                    }
                    LazyColumn(
                        state = lazyListState,
                        modifier = listModifier,
                    ) {
                        items(
                            count = searchResultPagingItems.itemCount,
                        ) { index ->
                            searchResultPagingItems[index]?.let { (lecture, lectureState) ->
                                SearchLectureListItem(
                                    modifier = Modifier.animateItem(
                                        placementSpec = spring(
                                            stiffness = Spring.StiffnessHigh,
                                            visibilityThreshold = IntOffset.VisibilityThreshold,
                                        ),
                                    ),
                                    lecture = lecture,
                                    lectureState = lectureState,
                                    onClick = { onToggleLectureSelection(lecture) },
                                    onClickDetail = { onClickLectureDetail(lecture) },
                                    onClickReview = { onClickReview(lecture) },
                                    onClickBookmark = { onClickBookmark(lecture) },
                                    onClickVacancy = { onClickVacancy(lecture) },
                                    onClickAddOrRemove = { onClickAddOrRemove(lecture) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
