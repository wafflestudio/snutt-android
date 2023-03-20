package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
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
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import kotlinx.coroutines.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchPage(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val bottomSheet = LocalBottomSheetState.current
    val keyBoardController = LocalSoftwareKeyboardController.current

    val lectureDetailViewModel: LectureDetailViewModel = hiltViewModel()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>()
    val selectedLecture by searchViewModel.selectedLecture.collectAsState()
    val selectedTags by searchViewModel.selectedTags.collectAsState()
    val placeHolderState by searchViewModel.placeHolderState.collectAsState()
    val lazyListState = searchViewModel.lazyListState
    val loadState = searchResultPagingItems.loadState

    var searchEditTextFocused by remember { mutableStateOf(false) }
    val isDarkMode = isDarkMode()
    val reviewWebViewContainer =
        remember {
            WebViewContainer(context, userViewModel.accessToken, isDarkMode)
        }
    reviewWebViewContainer.apply {
        this.webView.addJavascriptInterface(
            CloseBridge(
                onClose = { scope.launch { bottomSheet.hide() } }
            ),
            "Snutt"
        )
    }

    // 강의평 바텀시트가 한번 올라왔다가 내려갈 때 원래 보던 창으로 돌아가기 위해 goBack() 수행
    // * 처음 SearchPage에 진입할 때도 sheetState는 invisible일 텐데, 이 때는 goBack() 하지 않아야 한다. (sheetWasShown 변수 존재 이유)
    var sheetWasShown by remember { mutableStateOf(false) }
    LaunchedEffect(bottomSheet.isVisible) {
        if (!bottomSheet.isVisible && sheetWasShown) {
            reviewWebViewContainer.webView.goBack()
        } else {
            sheetWasShown = true
        }
    }

    Column {
        SearchTopBar {
            SearchIcon(
                modifier = Modifier.clicks {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            searchViewModel.query()
                            keyBoardController?.hide()
                        }
                    }
                },
            )
            SearchEditText(
                searchEditTextFocused = searchEditTextFocused,
                onFocus = { isFocused ->
                    searchEditTextFocused = isFocused
                }
            )
            if (searchEditTextFocused) {
                ExitIcon(
                    modifier = Modifier.clicks {
                        scope.launch {
                            searchViewModel.clearEditText()
                            searchEditTextFocused = false
                            keyBoardController?.hide()
                        }
                    },
                )
            } else FilterIcon(
                modifier = Modifier.clicks {
                    // 강의 검색 필터 sheet 띄우기
                    bottomSheet.setSheetContent {
                        SearchOptionSheet(applyOption = {
                            scope.launch {
                                launchSuspendApi(apiOnProgress, apiOnError) {
                                    searchViewModel.query()
                                }
                            }
                            scope.launch { bottomSheet.hide() }
                        })
                    }
                    scope.launch { bottomSheet.show() }
                },
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .background(SNUTTColors.White900)
                .fillMaxWidth()
        ) {
            TimeTable(touchEnabled = false, selectedLecture = selectedLecture)
            Column(
                modifier = Modifier
                    .background(SNUTTColors.Dim2)
                    .fillMaxSize()
            ) {
                AnimatedLazyRow(itemList = selectedTags, itemKey = { it.name }) {
                    TagCell(tagDto = it, onClick = {
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                searchViewModel.toggleTag(it)
                                searchViewModel.query()
                            }
                        }
                    })
                }
                // loadState만으로는 PlaceHolder과 EmptyPage를 띄울 상황을 구별할 수 없다.
                if (placeHolderState) {
                    SearchPlaceHolder(
                        onClickSearchIcon = {
                            scope.launch {
                                keyBoardController?.hide()
                                searchViewModel.query()
                            }
                        }
                    )
                } else when {
                    loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && searchResultPagingItems.itemCount < 1 || loadState.refresh is LoadState.Error -> {
                        SearchEmptyPage()
                    }
                    else -> {
                        LazyColumn(
                            state = lazyListState, modifier = Modifier.fillMaxSize()
                        ) {
                            items(searchResultPagingItems) { lectureDataWithState ->
                                lectureDataWithState?.let {
                                    LectureListItem(lectureDataWithState, reviewWebViewContainer, searchViewModel = searchViewModel)
                                }
                            }
                            item {
                                Divider(color = SNUTTColors.White400)
                            }
                        }
                    }
                }
            }
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
        verticalArrangement = Arrangement.Center
    ) {
        BigSearchIcon(
            modifier = Modifier
                .width(78.dp)
                .height(76.dp)
                .padding(10.dp)
                .clicks {
                    onClickSearchIcon.invoke()
                }
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_1),
            style = SNUTTTypography.h1.copy(fontSize = 25.sp, color = SNUTTColors.White700)
        )
        Spacer(modifier = Modifier.height(35.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_2),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_3),
            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.White500)
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_4),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_5),
            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.White500)
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
        verticalArrangement = Arrangement.Center
    ) {
        BigSearchIcon(
            modifier = Modifier
                .width(58.dp)
                .height(56.dp)
        )
        Spacer(modifier = Modifier.height(35.dp))
        Text(
            text = stringResource(R.string.search_result_empty),
            style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.White700, fontSize = 18.sp)
        )
    }
}

@Preview
@Composable
fun SearchPagePreview() {
//    SearchPage()
}
