package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyViewModel
import kotlinx.coroutines.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchPage(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val bottomSheet = LocalBottomSheetState.current

    val timetableViewModel: TimetableViewModel = hiltViewModel()
    val tableListViewModel: TableListViewModel = hiltViewModel()
    val lectureDetailViewModel: LectureDetailViewModel = hiltViewModel()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>()
    val vacancyViewModel = hiltViewModel<VacancyViewModel>()
    val selectedLecture by searchViewModel.selectedLecture.collectAsState()
    val pageMode by searchViewModel.pageMode.collectAsState()
    val pagerState = rememberPagerState(initialPage = pageMode.page) { 2 }
    val firstBookmarkAlert by userViewModel.firstBookmarkAlert.collectAsState()

    var searchEditTextFocused by remember { mutableStateOf(false) }
    val isDarkMode = isDarkMode()
    val reviewBottomSheetWebViewContainer = remember {
        WebViewContainer(context, userViewModel.accessToken, isDarkMode).apply {
            this.webView.addJavascriptInterface(
                CloseBridge(onClose = { scope.launch { bottomSheet.hide() } }),
                "Snutt",
            )
        }
    }

    LaunchedEffect(Unit) {
        searchViewModel.pageMode.collect {
            pagerState.animateScrollToPage(
                page = it.page,
            )
        }
    }

    Column {
        TopBar(
            title = {
                AnimatedContent(
                    targetState = pageMode,
                    transitionSpec = {
                        when (targetState) {
                            is SearchPageMode.Search -> {
                                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> width } + fadeOut() using SizeTransform(clip = false)
                            }
                            is SearchPageMode.Bookmark -> {
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> -width } + fadeOut() using SizeTransform(clip = false)
                            }
                        }
                    },
                    label = "top bar animation",
                ) {
                    when (it) {
                        is SearchPageMode.Search -> {
                            Row(
                                modifier = Modifier
                                    .background(SNUTTColors.Gray100, shape = RoundedCornerShape(6.dp))
                                    .weight(1f)
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                SearchIcon(
                                    modifier = Modifier.clicks {
                                        scope.launch {
                                            launchSuspendApi(apiOnProgress, apiOnError) {
                                                searchViewModel.query()
                                            }
                                        }
                                    },
                                )
                                SearchEditText(
                                    searchEditTextFocused = searchEditTextFocused,
                                    onFocus = { isFocused ->
                                        searchEditTextFocused = isFocused
                                    },
                                )
                                if (searchEditTextFocused) {
                                    ExitIcon(
                                        modifier = Modifier.clicks {
                                            scope.launch {
                                                searchViewModel.clearEditText()
                                                searchEditTextFocused = false
                                            }
                                        },
                                    )
                                } else {
                                    FilterIcon(
                                        modifier = Modifier.clicks {
                                            // 강의 검색 필터 sheet 띄우기
                                            bottomSheet.setSheetContent {
                                                SearchOptionSheet(
                                                    applyOption = {
                                                        scope.launch {
                                                            launchSuspendApi(apiOnProgress, apiOnError) {
                                                                searchViewModel.query()
                                                            }
                                                        }
                                                        scope.launch { bottomSheet.hide() }
                                                    },
                                                )
                                            }
                                            scope.launch { bottomSheet.show() }
                                        },
                                    )
                                }
                            }
                        }
                        is SearchPageMode.Bookmark -> {
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp),
                                text = stringResource(R.string.bookmark_page_title),
                                style = SNUTTTypography.h2,
                            )
                        }
                    }
                }
            },
            actions = {
                IconWithAlertDot(firstBookmarkAlert) { centerAlignedModifier ->
                    BookmarkIcon(
                        modifier = centerAlignedModifier
                            .size(30.dp)
                            .clicks {
                                searchViewModel.togglePageMode()
                            },
                        marked = pageMode is SearchPageMode.Bookmark,
                    )
                }
            },
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .background(SNUTTColors.White900)
                .fillMaxWidth(),
        ) {
            TimeTable(touchEnabled = false, selectedLecture = selectedLecture)
            AnimatedContent(
                targetState = pageMode,
                modifier = Modifier.background(SNUTTColors.Dim2),
                transitionSpec = {
                    when (targetState) {
                        is SearchPageMode.Search -> {
                            slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut() using SizeTransform(clip = false)
                        }
                        is SearchPageMode.Bookmark -> {
                            slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut() using SizeTransform(clip = false)
                        }
                    }
                },
                label = "body animation",
            ) { pageMode ->
                when (pageMode) {
                    SearchPageMode.Search -> SearchResultList(
                        scope,
                        searchResultPagingItems,
                        searchViewModel,
                        timetableViewModel,
                        tableListViewModel,
                        lectureDetailViewModel,
                        userViewModel,
                        vacancyViewModel,
                        reviewBottomSheetWebViewContainer,
                    )
                    SearchPageMode.Bookmark -> BookmarkList(
                        searchViewModel,
                        timetableViewModel,
                        tableListViewModel,
                        lectureDetailViewModel,
                        userViewModel,
                        vacancyViewModel,
                        reviewBottomSheetWebViewContainer,
                    )
                }
            }
        }
    }
}

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
                    SearchEmptyPage()
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

@Composable
fun BookmarkList(
    searchViewModel: SearchViewModel,
    timetableViewModel: TimetableViewModel,
    tableListViewModel: TableListViewModel,
    lectureDetailViewModel: LectureDetailViewModel,
    userViewModel: UserViewModel,
    vacancyViewModel: VacancyViewModel,
    reviewWebViewContainer: WebViewContainer,
) {
    val bookmarks by searchViewModel.bookmarkList.collectAsState()
    if (bookmarks.isEmpty()) {
        BookmarkPlaceHolder()
    } else {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .fillMaxSize(),
        ) {
            items(bookmarks) {
                LectureListItem(
                    lectureDataWithState = it,
                    searchViewModel = searchViewModel,
                    reviewWebViewContainer = reviewWebViewContainer,
                    isBookmarkPage = true,
                    timetableViewModel = timetableViewModel,
                    tableListViewModel = tableListViewModel,
                    lectureDetailViewModel = lectureDetailViewModel,
                    userViewModel = userViewModel,
                    vacancyViewModel = vacancyViewModel,
                )
            }
            item { Divider(color = SNUTTColors.White400) }
        }
    }
}

@Composable
private fun SearchPlaceHolder(onClickSearchIcon: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BigSearchIcon(
            modifier = Modifier
                .width(78.dp)
                .height(76.dp)
                .padding(10.dp)
                .clicks {
                    onClickSearchIcon.invoke()
                },
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_1),
            style = SNUTTTypography.h1.copy(fontSize = 25.sp, color = SNUTTColors.White700),
        )
        Spacer(modifier = Modifier.height(35.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_2),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700),
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_3),
            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.White500),
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_4),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700),
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_5),
            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.White500),
        )
    }
}

@Composable
private fun SearchEmptyPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BigSearchIcon(
            modifier = Modifier
                .width(58.dp)
                .height(56.dp),
        )
        Spacer(modifier = Modifier.height(35.dp))
        Text(
            text = stringResource(R.string.search_result_empty),
            style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.White700, fontSize = 18.sp),
        )
    }
}

@Composable
fun BookmarkPlaceHolder() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.bookmark_page_placeholder_1),
            style = SNUTTTypography.subtitle1.copy(
                fontSize = 18.sp,
                color = SNUTTColors.White700,
                fontWeight = FontWeight.Bold,
            ),
        )
        Text(
            text = stringResource(R.string.bookmark_page_placeholder_2),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700),
        )
        Text(
            text = stringResource(R.string.bookmark_page_placeholder_3),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700),
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
fun SearchPagePreview() {
//    SearchPage()
}
