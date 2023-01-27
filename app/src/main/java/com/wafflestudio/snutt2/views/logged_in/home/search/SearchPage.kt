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
import androidx.compose.ui.platform.LocalContext
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
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getLectureTagText
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getSimplifiedClassTime
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getSimplifiedLocation
import com.wafflestudio.snutt2.lib.getColor
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.lib.network.ErrorCode.EMAIL_NOT_VERIFIED
import com.wafflestudio.snutt2.lib.network.ErrorCode.LECTURE_TIME_OVERLAP
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModelNew
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModelNew
import kotlinx.coroutines.*

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun SearchPage(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
) {
    val context = LocalContext.current
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModelNew>()
    val userViewModel = hiltViewModel<UserViewModel>()
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
    val isFirstBookmarkAlert by userViewModel.firstBookmarkAlert.collectAsState()

    val isDarkMode = isDarkMode()
    val reviewWebViewContainer =
        remember {
            WebViewContainer(context, userViewModel.accessToken, isDarkMode)
        }
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
            lectureDetailViewModel.setViewMode(false)
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
                                    SearchListItem(
                                        lectureDataWithState = it,
                                        searchViewModel,
                                        timetableViewModel,
                                        tableListViewModel,
                                        lectureDetailViewModel,
                                        reviewWebViewContainer,
                                        isFirstBookmark = isFirstBookmarkAlert,
                                        showFirstBookmarkAlert = { scope.launch { userViewModel.setFirstBookmarkAlertShown() } },
                                    )
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyItemScope.SearchListItem(
    lectureDataWithState: DataWithState<LectureDto, LectureState>,
    searchViewModel: SearchViewModel,
    timetableViewModel: TimetableViewModel,
    tableListViewModel: TableListViewModelNew,
    lectureDetailViewModel: LectureDetailViewModelNew,
    reviewWebViewContainer: WebViewContainer,
    isBookmarkPage: Boolean = false,
    isFirstBookmark: Boolean = false,
    showFirstBookmarkAlert: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val bottomSheetContentSetter = LocalBottomSheetContentSetter.current
    val sheetState = LocalBottomSheetState.current
    val modalState = LocalModalState.current
    val pageController = LocalHomePageController.current
    val context = LocalContext.current

    val selected = lectureDataWithState.state.selected
    val contained = lectureDataWithState.state.contained
    val bookmarked = lectureDataWithState.state.bookmarked

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
                    scope.launch {
                        searchViewModel.toggleLectureSelection(lectureDataWithState.item)
                    }
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
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.search_result_item_detail_button),
                    textAlign = TextAlign.Center,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            lectureDetailViewModel.initializeEditingLectureDetail(
                                lectureDataWithState.item
                            )
                            lectureDetailViewModel.setViewMode(true)
                            bottomSheetContentSetter.invoke {
                                LectureDetailPage(onCloseViewMode = {
                                    scope.launch { sheetState.hide() }
                                }, vm = lectureDetailViewModel)
                            }
                            scope.launch { sheetState.show() }
                        }
                )
                Text(
                    text = stringResource(R.string.search_result_item_review_button),
                    textAlign = TextAlign.Center,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            scope.launch {
                                handleReviewPageWithEmailVerifyCheck(
                                    apiOnProgress, apiOnError,
                                    api = {
                                        val url =
                                            searchViewModel.getLectureReviewUrl(lectureDataWithState.item)
                                        val job: CompletableJob = Job()
                                        scope.launch {
                                            reviewWebViewContainer.openPage("$url&on_back=close")
                                            job.complete()
                                        }
                                        joinAll(job)
                                        scope.launch {
                                            bottomSheetContentSetter.invoke {
                                                CompositionLocalProvider(LocalReviewWebView provides reviewWebViewContainer) {
                                                    ReviewWebView(0.95f)
                                                }
                                            }
                                            sheetState.show()
                                        }
                                    },
                                    onUnVerified = {
                                        modalState
                                            .set(
                                                onDismiss = { modalState.hide() },
                                                title = context.getString(R.string.email_unverified_cta_title),
                                                positiveButton = context.getString(R.string.common_ok),
                                                negativeButton = context.getString(R.string.common_cancel),
                                                onConfirm = {
                                                    modalState.hide()
                                                    scope.launch {
                                                        pageController.update(HomeItem.Review())
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.email_unverified_cta_message),
                                                    style = SNUTTTypography.button,
                                                )
                                            }
                                            .show()
                                    }
                                )
                            }
                        }
                )
                Spacer(modifier = Modifier.weight(0.3f))
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            scope.launch {
                                launchSuspendApi(apiOnProgress, apiOnError) {
                                    if (isBookmarkPage) {
                                        modalState
                                            .set(
                                                onDismiss = { modalState.hide() },
                                                onConfirm = {
                                                    scope.launch {
                                                        launchSuspendApi(
                                                            apiOnProgress,
                                                            apiOnError
                                                        ) {
                                                            searchViewModel.deleteBookmark(
                                                                lectureDataWithState.item
                                                            )
                                                            modalState.hide()
                                                            context.toast(context.getString(R.string.bookmark_remove_toast))
                                                        }
                                                    }
                                                },
                                                title = context.getString(R.string.notifications_app_bar_title),
                                                content = {
                                                    Text(
                                                        text = stringResource(R.string.bookmark_remove_check_message),
                                                        style = SNUTTTypography.body1
                                                    )
                                                },
                                                positiveButton = context.getString(R.string.common_ok),
                                                negativeButton = context.getString(R.string.common_cancel),
                                            )
                                            .show()
                                    } else {
                                        if (lectureDataWithState.state.bookmarked) {
                                            searchViewModel.deleteBookmark(lectureDataWithState.item)
                                        } else {
                                            searchViewModel.addBookmark(lectureDataWithState.item)
                                            if (isFirstBookmark) {
                                                showFirstBookmarkAlert()
                                                context.toast(context.getString(R.string.bookmark_first_alert_message))
                                            }
                                        }
                                    }
                                }
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BookmarkIcon(
                        modifier = Modifier
                            .size(15.dp),
                        marked = bookmarked,
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = stringResource(R.string.search_result_item_bookmark_button),
                        textAlign = TextAlign.Center,
                        style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    )
                }
                Text(
                    text = if (contained) stringResource(R.string.search_result_item_remove_button) else stringResource(
                        R.string.search_result_item_add_button
                    ),
                    textAlign = TextAlign.Center,
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            if (contained) {
                                scope.launch(Dispatchers.IO) {
                                    launchSuspendApi(apiOnProgress, apiOnError) {
                                        timetableViewModel.removeLecture(lectureDataWithState.item)
                                        searchViewModel.toggleLectureSelection(lectureDataWithState.item)
                                        tableListViewModel.fetchTableMap()
                                    }
                                }
                            } else {
                                scope.launch(Dispatchers.IO) {
                                    lectureApiWithOverlapDialog(
                                        apiOnProgress,
                                        apiOnError,
                                        onLectureOverlap = { message ->
                                            modalState
                                                .set(
                                                    onDismiss = { modalState.hide() },
                                                    onConfirm = {
                                                        scope.launch {
                                                            searchViewModel.selectedLecture.value?.let { lecture ->
                                                                launchSuspendApi(
                                                                    apiOnProgress,
                                                                    apiOnError
                                                                ) {
                                                                    timetableViewModel.addLecture(
                                                                        lecture = lecture,
                                                                        is_force = true
                                                                    )
                                                                    searchViewModel.toggleLectureSelection(
                                                                        lecture
                                                                    )
                                                                }
                                                            }
                                                            modalState.hide()
                                                        }
                                                    },
                                                    title = context.getString(R.string.lecture_overlap_error_message),
                                                    positiveButton = context.getString(R.string.common_ok),
                                                    negativeButton = context.getString(R.string.common_cancel),
                                                    content = {
                                                        Text(
                                                            text = message,
                                                            style = SNUTTTypography.body1
                                                        )
                                                    }
                                                )
                                                .show()
                                        }
                                    ) {
                                        timetableViewModel.addLecture(
                                            lecture = lectureDataWithState.item,
                                            is_force = false
                                        )
                                        searchViewModel.toggleLectureSelection(lectureDataWithState.item)
                                        tableListViewModel.fetchTableMap()
                                    }
                                }
                            }
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
                    onLectureOverlap(e.errorDTO.ext?.get("confirm_message") ?: "")
                } else apiOnError(e)
            }
            else -> apiOnError(e)
        }
    } finally {
        apiOnProgress.hideProgress()
    }
}

suspend fun handleReviewPageWithEmailVerifyCheck(
    apiOnProgress: ApiOnProgress,
    apiOnError: ApiOnError,
    onUnVerified: () -> Unit,
    api: suspend () -> Unit
) {
    try {
        apiOnProgress.showProgress()
        api.invoke()
    } catch (e: Exception) {
        when (e) {
            is ErrorParsedHttpException -> {
                if (e.errorDTO?.code == EMAIL_NOT_VERIFIED) {
                    onUnVerified()
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
