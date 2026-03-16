package com.wafflestudio.snutt2.views.logged_in.home.search.refactor

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.components.compose.AnimatedLazyRow
import com.wafflestudio.snutt2.domainmodel.SearchTag
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureState
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchEmptyPlaceholder
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchPlaceHolder
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchResultListState

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

            SearchResultListState.EMPTY -> {
                SearchEmptyPlaceholder(
                    modifier = Modifier.logImpression(AnalyticsScreen.SearchEmpty),
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
                        lectureDataWithState?.let { (lecture, lectureState) ->
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
