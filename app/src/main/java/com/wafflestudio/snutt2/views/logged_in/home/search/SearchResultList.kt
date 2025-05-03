package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.components.compose.AnimatedLazyRow
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.analyticsScreen
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchResultList(
    scope: CoroutineScope,
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
    searchViewModel: SearchViewModel,
    timetableViewModel: TimetableViewModel,
    tableListViewModel: TableListViewModel,
    lectureDetailViewModel: LectureDetailViewModel,
    userViewModel: UserViewModel,
    vacancyViewModel: VacancyViewModel,
    reviewBottomSheetReviewWebViewContainer: ReviewWebViewContainer,
) {
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val selectedTags by searchViewModel.selectedTags.collectAsState()
    val lazyListState = searchViewModel.lazyListState
    val keyBoardController = LocalSoftwareKeyboardController.current

    val searchResultListState = rememberSearchResultListState(searchResultPagingItems)

    Column {
        AnimatedLazyRow(itemList = selectedTags, itemKey = { it.toItemKey() }) {
            TagCell(
                tagDto = it,
                onClick = {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            searchViewModel.toggleTag(it)
                            searchViewModel.query()
                        }
                    }
                },
            )
        }

        when (searchResultListState) {
            SearchResultListState.PLACEHOLDER -> {
                SearchPlaceHolder(
                    onClickSearchIcon = {
                        scope.launch {
                            keyBoardController?.hide()
                            searchViewModel.query()
                        }
                    },
                    modifier = Modifier.analyticsScreen(AnalyticsScreen.SearchHome),
                )
            }

            SearchResultListState.EMPTY -> {
                SearchEmptyPlaceholder(
                    modifier = Modifier
                        .analyticsScreen(AnalyticsScreen.SearchEmpty),
                )
            }

            SearchResultListState.HAS_RESULTS -> {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .analyticsScreen(AnalyticsScreen.SearchList),
                ) {
                    items(searchResultPagingItems) { lectureDataWithState ->
                        lectureDataWithState?.let {
                            LectureListItem(
                                lectureDataWithState,
                                reviewBottomSheetReviewWebViewContainer,
                                false,
                                searchViewModel,
                                timetableViewModel,
                                tableListViewModel,
                                lectureDetailViewModel,
                                userViewModel,
                                vacancyViewModel,
                            )
                        }
                    }
                }
            }
        }
    }
}
