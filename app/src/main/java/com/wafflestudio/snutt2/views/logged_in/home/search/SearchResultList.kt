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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.components.compose.AnimatedLazyRow
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
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
    reviewBottomSheetWebViewContainer: WebViewContainer,
) {
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val selectedTags by searchViewModel.selectedTags.collectAsState()
    val placeHolderState by searchViewModel.placeHolderState.collectAsState()
    val lazyListState = searchViewModel.lazyListState
    val loadState = searchResultPagingItems.loadState
    val keyBoardController = LocalSoftwareKeyboardController.current

    Column {
        AnimatedLazyRow(itemList = selectedTags, itemKey = { it.name }) {
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
        // loadState만으로는 PlaceHolder과 EmptyPage를 띄울 상황을 구별할 수 없다.
        if (placeHolderState) {
            SearchPlaceHolder(
                onClickSearchIcon = {
                    scope.launch {
                        keyBoardController?.hide()
                        searchViewModel.query()
                    }
                },
            )
        } else {
            when {
                loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && searchResultPagingItems.itemCount < 1 || loadState.refresh is LoadState.Error -> {
                    SearchEmptyPlaceholder()
                }

                else -> {
                    LazyColumn(
                        state = lazyListState, modifier = Modifier.fillMaxSize(),
                    ) {
                        items(searchResultPagingItems) { lectureDataWithState ->
                            lectureDataWithState?.let {
                                LectureListItem(
                                    lectureDataWithState,
                                    reviewBottomSheetWebViewContainer,
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
}
