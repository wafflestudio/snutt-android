package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BookmarkIcon
import com.wafflestudio.snutt2.components.compose.ComposableStatesWithScope
import com.wafflestudio.snutt2.components.compose.ExitIcon
import com.wafflestudio.snutt2.components.compose.FilterIcon
import com.wafflestudio.snutt2.components.compose.IconWithAlertDot
import com.wafflestudio.snutt2.components.compose.SearchIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.logging.AddToBookmarkParameter
import com.wafflestudio.snutt2.lib.logging.AddToTimetableParameter
import com.wafflestudio.snutt2.lib.logging.AddToVacancyParameter
import com.wafflestudio.snutt2.lib.logging.AnalyticsEvent
import com.wafflestudio.snutt2.lib.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.lib.logging.LectureActionReferrer
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.bookmark.showDeleteBookmarkDialog
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.search.bookmark.BookmarkList
import com.wafflestudio.snutt2.views.logged_in.home.search.bookmark.SearchPageMode
import com.wafflestudio.snutt2.views.logged_in.home.search.search_option.SearchOptionSheet
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ModeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SearchRoute(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
    searchResultListState: SearchResultListState,
    timetableViewModel: TimetableViewModel = hiltViewModel(),
    tableListViewModel: TableListViewModel = hiltViewModel(),
    lectureDetailViewModel: LectureDetailViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val composableStates = ComposableStatesWithScope(scope)

    val context = LocalContext.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val bottomSheet = LocalBottomSheetState.current
    val analyticsLogger = LocalAnalyticsLogger.current

    val bookmarks by searchViewModel.bookmarkList.collectAsState()
    val selectedLecture by searchViewModel.selectedLecture.collectAsState()
    val pageMode by searchViewModel.pageMode.collectAsState()
    val firstBookmarkAlert by searchViewModel.firstBookmarkAlert.collectAsState()
    val selectedTags by searchViewModel.selectedTags.collectAsState()
    val lazyListState = searchViewModel.lazyListState
    val isDarkMode = isDarkMode()
    val reviewBottomSheetReviewWebViewContainer = remember {
        ReviewWebViewContainer(context, userViewModel.accessToken, isDarkMode).apply {
            this.webView.addJavascriptInterface(
                CloseBridge(onClose = { scope.launch { bottomSheet.hide() } }),
                "Snutt",
            )
        }
    }
    SearchScreen(
        searchResultPagingItems = searchResultPagingItems,
        searchResultListState = searchResultListState,
        selectedTags = selectedTags,
        lazyListState = lazyListState,
        bookmarks = bookmarks,
        selectedLecture = selectedLecture,
        pageMode = pageMode,
        firstBookmarkAlert = firstBookmarkAlert,
        onClickBack = {
            if (pageMode == SearchPageMode.Bookmark) {
                searchViewModel.togglePageMode()
            }
        },
        onSearch = {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    searchViewModel.query()
                }
            }
        },
        onClearEditText = {
            scope.launch { searchViewModel.clearEditText() }
        },
        onFilter = {
            bottomSheet.setSheetContent {
                SearchOptionSheet(
                    applyOption = {
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                searchViewModel.query()
                            }
                            searchViewModel.storeRecentSearchedDepartments()
                        }
                        scope.launch { bottomSheet.hide() }
                    },
                    hideBottomSheet = {
                        scope.launch { bottomSheet.hide() }
                    },
                )
            }
            scope.launch { bottomSheet.show() }
        },
        onToggleMode = {
            scope.launch { searchViewModel.togglePageMode() }
        },
        onToggleTagAndQuery = { tag ->
            scope.launch {
                searchViewModel.toggleTag(tag)
                launchSuspendApi(apiOnProgress, apiOnError) {
                    searchViewModel.query()
                }
            }
        },
        onToggleLectureSelection = { lecture ->
            scope.launch {
                searchViewModel.toggleLectureSelection(lecture)
            }
        },
        onClickLectureDetail = { lecture ->
            lectureDetailViewModel.initializeEditingLectureDetail(
                lecture, ModeType.Viewing,
            )
            val referrer = if (pageMode == SearchPageMode.Bookmark) DetailScreenReferrer.Bookmark else DetailScreenReferrer.Search(searchViewModel.searchTitle.value)
            bottomSheet.setSheetContent {
                LectureDetailPage(
                    referrer = referrer,
                    searchViewModel = searchViewModel,
                    onCloseViewMode = { scope ->
                        scope.launch { bottomSheet.hide() }
                    },
                )
            }
            scope.launch { bottomSheet.show() }
        },
        onClickReview = { lecture ->
            scope.launch {
                val url = lecture.review?.getReviewUrl(context)
                openReviewBottomSheet(
                    url = url,
                    reviewWebViewContainer = reviewBottomSheetReviewWebViewContainer,
                    bottomSheet = bottomSheet,
                    lectureId = lecture.lecture_id ?: lecture.id,
                    referrer = DetailScreenReferrer.Search(searchViewModel.searchTitle.value),
                )
            }
        },
        onClickBookmark = { lecture, isBookmarked ->
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    if (pageMode == SearchPageMode.Bookmark) {
                        showDeleteBookmarkDialog(
                            composableStates,
                            onConfirm = {
                                searchViewModel.deleteBookmark(lecture)
                                searchViewModel.toggleLectureSelection(
                                    lecture,
                                )
                            },
                        )
                    } else {
                        if (isBookmarked) {
                            searchViewModel.deleteBookmark(lecture)
                        } else {
                            analyticsLogger.logEvent(
                                AnalyticsEvent.AddToBookmark(
                                    AddToBookmarkParameter(
                                        lectureId = lecture.lecture_id ?: lecture.id,
                                        referrer = LectureActionReferrer.Search(searchViewModel.searchTitle.value),
                                    ),
                                ),
                            )
                            searchViewModel.addBookmark(lecture)
                            if (firstBookmarkAlert) {
                                searchViewModel.setFirstBookmarkAlertShown()
                                context.toast(context.getString(R.string.bookmark_first_alert_message))
                            }
                        }
                    }
                }
            }
        },
        onClickVacancy = { lecture, isVacancyRegistered ->
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    if (isVacancyRegistered) {
                        searchViewModel.removeVacancyLecture(lecture.id)
                    } else {
                        analyticsLogger.logEvent(
                            AnalyticsEvent.AddToVacancy(
                                AddToVacancyParameter(
                                    lectureId = lecture.lecture_id ?: lecture.id,
                                    referrer = LectureActionReferrer.Search(searchViewModel.searchTitle.value),
                                ),
                            ),
                        )
                        searchViewModel.addVacancyLecture(lecture.id)
                    }
                }
            }
        },
        onToggleLectureContained = { lecture, contained ->
            if (contained) {
                scope.launch(Dispatchers.IO) {
                    launchSuspendApi(apiOnProgress, apiOnError) {
                        timetableViewModel.removeLecture(lecture)
                        searchViewModel.toggleLectureSelection(lecture)
                        tableListViewModel.fetchTableMap()
                    }
                }
            } else {
                checkLectureOverlap(
                    composableStates,
                    api = {
                        analyticsLogger.logEvent(
                            AnalyticsEvent.AddToTimetable(
                                AddToTimetableParameter(
                                    lectureId = lecture.lecture_id
                                        ?: lecture.id,
                                    timetableId = timetableViewModel.currentTable.value?.id,
                                    referrer = when (pageMode == SearchPageMode.Bookmark) {
                                        true -> LectureActionReferrer.Bookmark
                                        false -> LectureActionReferrer.Search(searchViewModel.searchTitle.value)
                                    },
                                ),
                            ),
                        )
                        timetableViewModel.addLecture(
                            lecture = lecture,
                            is_force = false,
                        )
                        searchViewModel.toggleLectureSelection(lecture)
                        tableListViewModel.fetchTableMap()
                    },
                    onLectureOverlap = { message ->
                        showLectureOverlapDialog(
                            composableStates,
                            message,
                            forceAddApi = {
                                timetableViewModel.addLecture(
                                    lecture = lecture,
                                    is_force = true,
                                )
                                searchViewModel.toggleLectureSelection(
                                    lecture,
                                )
                            },
                        )
                    },
                )
            }
        },
    )
}

@Composable
fun SearchScreen(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
    searchResultListState: SearchResultListState,
    selectedTags: List<TagDto>,
    lazyListState: LazyListState,
    bookmarks: List<DataWithState<LectureDto, LectureState>>,
    selectedLecture: LectureDto?,
    pageMode: SearchPageMode,
    firstBookmarkAlert: Boolean,
    onClickBack: () -> Unit,
    onSearch: () -> Unit,
    onClearEditText: () -> Unit,
    onFilter: () -> Unit,
    onToggleMode: () -> Unit,
    onToggleTagAndQuery: (tag: TagDto) -> Unit,

    onToggleLectureSelection: (LectureDto) -> Unit,
    onClickLectureDetail: (LectureDto) -> Unit,
    onClickReview: (LectureDto) -> Unit,
    onClickBookmark: (LectureDto, Boolean) -> Unit,
    onClickVacancy: (LectureDto, Boolean) -> Unit,
    onToggleLectureContained: (LectureDto, Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var searchEditTextFocused by remember { mutableStateOf(false) }

    BackHandler {
        onClickBack()
    }

    Column {
        TopBar(
            title = {
                AnimatedContent(
                    targetState = pageMode,
                    transitionSpec = {
                        when (targetState) {
                            SearchPageMode.Search -> {
                                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> width } + fadeOut() using SizeTransform(clip = false)
                            }
                            SearchPageMode.Bookmark -> {
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> -width } + fadeOut() using SizeTransform(clip = false)
                            }
                        }
                    },
                    label = "top bar animation",
                ) {
                    when (it) {
                        SearchPageMode.Search -> {
                            Row(
                                modifier = Modifier
                                    .padding(start = 8.dp, top = 5.dp, bottom = 5.dp)
                                    .background(
                                        SNUTTColors.Gray100,
                                        shape = RoundedCornerShape(6.dp),
                                    )
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                SearchIcon(
                                    modifier = Modifier.clicks {
                                        onSearch()
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
                                            onClearEditText()
                                        },
                                    )
                                } else {
                                    FilterIcon(
                                        modifier = Modifier.clicks {
                                            onFilter()
                                        },
                                    )
                                }
                            }
                        }
                        SearchPageMode.Bookmark -> {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(start = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(R.string.bookmark_page_title),
                                    style = SNUTTTypography.h2,
                                )
                            }
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
                                onToggleMode()
                            },
                        marked = pageMode == SearchPageMode.Bookmark,
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
                        SearchPageMode.Search -> {
                            slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut() using SizeTransform(clip = false)
                        }
                        SearchPageMode.Bookmark -> {
                            slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut() using SizeTransform(clip = false)
                        }
                    }
                },
                label = "body animation",
            ) { pageMode ->
                when (pageMode) {
                    SearchPageMode.Search -> SearchResultList(
                        searchResultPagingItems = searchResultPagingItems,
                        searchResultListState = searchResultListState,
                        selectedTags = selectedTags,
                        lazyListState = lazyListState,
                        onToggleTagAndQuery = onToggleTagAndQuery,
                        onClickLectureDetail = onClickLectureDetail,
                        onClickReview = onClickReview,
                        onClickBookmark = onClickBookmark,
                        onClickVacancy = onClickVacancy,
                        onToggleLectureContained = onToggleLectureContained,
                        onToggleLectureSelection = onToggleLectureSelection,
                    )
                    SearchPageMode.Bookmark -> BookmarkList(
                        bookmarks = bookmarks,
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

@Preview
@Composable
fun SearchPagePreview() {
//    SearchPage()
}
