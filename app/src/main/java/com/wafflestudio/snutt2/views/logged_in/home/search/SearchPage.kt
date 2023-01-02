@file:OptIn(ExperimentalFoundationApi::class)

package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
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
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getLectureTagText
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getSimplifiedClassTime
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getSimplifiedLocation
import com.wafflestudio.snutt2.lib.getColor
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.lib.network.ErrorCode.LECTURE_TIME_OVERLAP
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModelNew
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModelNew
import kotlinx.coroutines.*

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun SearchPage(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureStateNew>>,
) {
    val navController = LocalNavController.current
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModelNew>()
    val pageController = LocalHomePageController.current
    val reviewWebViewContainer = LocalReviewWebView.current

    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModelNew>()

    val searchKeyword = searchViewModel.searchTitle.collectAsState()
    var searchEditTextFocused by remember { mutableStateOf(false) }
    val keyBoardController = LocalSoftwareKeyboardController.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val bottomSheetContentSetter = LocalBottomSheetContentSetter.current
    val sheetState = LocalBottomSheetState.current

    val scope = rememberCoroutineScope()
    val lazyListState = searchViewModel.lazyListState
    val loadState = searchResultPagingItems.loadState
    val selectedLecture by searchViewModel.selectedLecture.collectAsState()
    val tagsByTagType by searchViewModel.tagsByTagType.collectAsState()
    val selectedTagType by searchViewModel.selectedTagType.collectAsState()
    val selectedTags by searchViewModel.selectedTags.collectAsState()
    val placeHolderState by searchViewModel.placeHolderState.collectAsState()

    var lectureOverlapDialogState by remember { mutableStateOf(false) }
    var lectureOverlapDialogMessage by remember { mutableStateOf("") }

    reviewWebViewContainer.apply {
        this.webView.addJavascriptInterface(
            CloseBridge(
                onClose = { scope.launch { sheetState.hide() } }
            ),
            "Snutt"
        )
    }

    // 강의평 바텀시트가 한번 올라왔다가 내려갈 때 원래 보던 창으로 돌아가기 위해 goBack() 수행
    // * 처음 SearchPage에 진입할 때도 sheetState는 invisible일 텐데, 이 때는 goBack() 하지 않아야 한다. (sheetWasShown 변수 존재 이유)
    var sheetWasShown by remember { mutableStateOf(false) }
    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible && sheetWasShown) {
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
                colorFilter = ColorFilter.tint(SNUTTColors.Black900)
            )
            Spacer(modifier = Modifier.width(12.dp))
            // FIXME: EditText 글자가 살짝 중간에서 아래로 치우쳐 있다.
            EditText(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onFocusChanged { searchEditTextFocused = it.isFocused }
                    .clearFocusOnKeyboardDismiss(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            searchViewModel.query()
                            keyBoardController?.hide()
                        }
                    }
                }),
                value = searchKeyword.value,
                onValueChange = {
                    scope.launch {
                        searchViewModel.setTitle(it)
                    }
                },
                singleLine = true,
                hint = stringResource(R.string.search_hint),
                underlineEnabled = false,
                clearFocusFlag = searchEditTextFocused.not(),
            )
            Spacer(modifier = Modifier.width(12.dp))
            if (searchEditTextFocused) {
                ExitIcon(
                    modifier = Modifier.clicks {
                        scope.launch {
                            searchViewModel.clearEditText()
                            searchEditTextFocused = false
                            keyBoardController?.hide()
                        }
                    },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            } else FilterIcon(
                modifier = Modifier.clicks {
                    // 강의 검색 필터 sheet 띄우기
                    bottomSheetContentSetter.invoke {
                        SearchOptionSheet(tagsByTagType, selectedTagType) {
                            scope.launch {
                                launchSuspendApi(apiOnProgress, apiOnError) {
                                    searchViewModel.query()
                                }
                            }
                            scope.launch { sheetState.hide() }
                        }
                    }
                    scope.launch { sheetState.show() }
                },
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
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
                AnimatedVisibility(visible = selectedTags.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(items = selectedTags, key = { it.name }) {
                            TagCell(tagDto = it) {
                                scope.launch {
                                    launchSuspendApi(apiOnProgress, apiOnError) {
                                        searchViewModel.toggleTag(it)
                                        searchViewModel.query()
                                    }
                                }
                            }
                        }
                    }
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
                            items(searchResultPagingItems) {
                                it?.let {
                                    SearchListItem(lectureDataWithState = it, onSelect = {
                                        scope.launch {
                                            searchViewModel.toggleLectureSelection(it.item)
                                        }
                                    }, onClickAdd = {
                                        scope.launch(Dispatchers.IO) {
                                            lectureApiWithOverlapDialog(
                                                apiOnProgress,
                                                apiOnError,
                                                onLectureOverlap = { message ->
                                                    lectureOverlapDialogMessage = message
                                                    lectureOverlapDialogState = true
                                                }
                                            ) {
                                                timetableViewModel.addLecture(
                                                    lecture = it.item,
                                                    is_force = false
                                                )
                                                searchViewModel.toggleLectureSelection(it.item)
                                                tableListViewModel.fetchTableMap()
                                            }
                                        }
                                    }, onClickRemove = {
                                        scope.launch(Dispatchers.IO) {
                                            launchSuspendApi(apiOnProgress, apiOnError) {
                                                timetableViewModel.removeLecture(it.item)
                                                searchViewModel.toggleLectureSelection(it.item)
                                                tableListViewModel.fetchTableMap()
                                            }
                                        }
                                    }, onClickDetail = {
                                        lectureDetailViewModel.initializeEditingLectureDetail(it.item)
                                        lectureDetailViewModel.setViewMode(true)
                                        bottomSheetContentSetter.invoke {
                                            LectureDetailPage(onCloseViewMode = {
                                                scope.launch { sheetState.hide() }
                                            })
                                        }
                                        scope.launch { sheetState.show() }
                                    }, onClickReview = {
                                        scope.launch {
                                            val job: CompletableJob = Job()
                                            scope.launch {
                                                launchSuspendApi(apiOnProgress, apiOnError) {
                                                    reviewWebViewContainer.openPage(searchViewModel.getLectureReviewUrl(it.item) + "&on_back=close")
                                                    job.complete()
                                                }
                                            }
                                            joinAll(job)
                                            scope.launch {
                                                bottomSheetContentSetter.invoke {
                                                    ReviewWebView(0.95f)
                                                }
                                                sheetState.show()
                                            }
                                        }
                                    })
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

    if (lectureOverlapDialogState) {
        CustomDialog(
            onDismiss = { lectureOverlapDialogState = false },
            onConfirm = {
                scope.launch {
                    selectedLecture?.let { lecture ->
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            timetableViewModel.addLecture(
                                lecture = lecture,
                                is_force = true
                            )
                            searchViewModel.toggleLectureSelection(lecture)
                        }
                    }
                    lectureOverlapDialogState = false
                }
            },
            title = stringResource(id = R.string.lecture_overlap_error_message)
        ) {
            Text(text = lectureOverlapDialogMessage, style = SNUTTTypography.body1)
        }
    }
}

@Composable
fun LazyItemScope.SearchListItem(
    lectureDataWithState: DataWithState<LectureDto, LectureStateNew>,
    onSelect: () -> Unit,
    onClickAdd: () -> Unit,
    onClickRemove: () -> Unit,
    onClickDetail: () -> Unit,
    onClickReview: () -> Unit,
) {
    val selected = lectureDataWithState.state.selected
    val contained = lectureDataWithState.state.contained

    val lectureTitle = lectureDataWithState.item.course_title
    val instructorCreditText = stringResource(
        R.string.search_result_item_instructor_credit_text,
        lectureDataWithState.item.instructor,
        lectureDataWithState.item.credit
    )
    val remarkText = lectureDataWithState.item.remark
    val tagText = getLectureTagText(lectureDataWithState.item)
    val classTimeText = getSimplifiedClassTime(lectureDataWithState.item)

    val backgroundColor = if (selected) SNUTTColors.Dim2 else SNUTTColors.Transparent

    Column(
        modifier = Modifier
            .animateItemPlacement(
                animationSpec = spring(
                    stiffness = Spring.StiffnessHigh,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )
            )
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Divider(color = SNUTTColors.White400)
        Column(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .clicks {
                    onSelect()
                }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = lectureTitle,
                    style = SNUTTTypography.h4.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = instructorCreditText,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TagIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (selected && remarkText.isNotBlank()) remarkText else tagText, // TODO: MARQUEE effect
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ClockIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = classTimeText,
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LocationIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = getSimplifiedLocation(lectureDataWithState.item),
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                )
            }
        }
        AnimatedVisibility(visible = lectureDataWithState.state.selected) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.search_result_item_detail_button),
                    textAlign = TextAlign.Start,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier
                        .weight(1f)
                        .clicks { onClickDetail() }
                )
                Text(
                    text = stringResource(R.string.search_result_item_review_button),
                    textAlign = TextAlign.Center,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier
                        .weight(1f)
                        .clicks { onClickReview() }
                )
                Text(
                    text = if (contained) stringResource(R.string.search_result_item_remove_button) else stringResource(
                        R.string.search_result_item_add_button
                    ),
                    textAlign = TextAlign.End,
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            if (contained) onClickRemove() else onClickAdd()
                        }
                )
            }
        }
    }
}

@Composable
fun SearchPlaceHolder(onClickSearchIcon: () -> Unit) {
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
fun SearchEmptyPage() {
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

@Composable
private fun LazyItemScope.TagCell(
    tagDto: TagDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .animateItemPlacement()
            .padding(horizontal = 5.dp)
            .height(30.dp)
            .background(color = tagDto.type.getColor(), shape = RoundedCornerShape(15.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = tagDto.name,
            style = SNUTTTypography.body1.copy(fontSize = 15.sp, color = SNUTTColors.AllWhite),
            textAlign = TextAlign.Center,
        )
        WhiteCloseIcon(
            modifier = Modifier
                .size(20.dp)
                .padding(2.5.dp)
                .clicks { onClick() }
        )
        Spacer(modifier = Modifier.width(10.dp))
    }
}

suspend fun lectureApiWithOverlapDialog(
    apiOnProgress: ApiOnProgress,
    apiOnError: ApiOnError,
    onLectureOverlap: (String) -> Unit,
    api: suspend () -> Unit
) {
    try {
        apiOnProgress.showProgress()
        api.invoke()
    } catch (e: Exception) {
        when (e) {
            is ErrorParsedHttpException -> {
                if (e.errorDTO?.code == LECTURE_TIME_OVERLAP) {
                    onLectureOverlap(e.errorDTO.ext!!["confirm_message"] ?: "")
                } else apiOnError(e)
            }
            else -> apiOnError(e)
        }
    } finally {
        apiOnProgress.hideProgress()
    }
}

@Preview
@Composable
fun SearchPagePreview() {
//    SearchPage()
}
