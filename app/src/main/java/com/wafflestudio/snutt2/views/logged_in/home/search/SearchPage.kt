package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ComposableStatesWithScope
import com.wafflestudio.snutt2.components.compose.ExitIcon
import com.wafflestudio.snutt2.components.compose.FilterIcon
import com.wafflestudio.snutt2.components.compose.SearchIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBar
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBarDuration
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBarHost
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBarHostState
import com.wafflestudio.snutt2.components.compose.snackbar.dismiss
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.logging.AddToTimetableParameter
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
import com.wafflestudio.snutt2.views.LocalCompactState
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.bookmark.showDialog
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.search.search_option.SearchOptionSheet
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ModeType
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SearchRoute(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
    searchResultListState: SearchResultListState,
    onNavigateVacancy: () -> Unit,
    onNavigateBookmark: () -> Unit,
    onNavigateOnboardAsOrigin: () -> Unit,
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

    val selectedLecture by searchViewModel.selectedLecture.collectAsState()
    val selectedTags by searchViewModel.selectedTags.collectAsState()
    val lazyListState = searchViewModel.lazyListState
    val tagsByTagType = searchViewModel.tagsByTagType.collectAsState()
    val selectedTagType = searchViewModel.selectedTagType.collectAsState()
    val recentSearchedDepartments = searchViewModel.selectableRecentSearchedDepartments.collectAsState()
    val tagTypesNotEmpty = searchViewModel.tagTypesNotEmpty.collectAsState()
    val draggedTimeBlock = searchViewModel.draggedTimeBlock.collectAsState()

    val snackBarHostState = remember { CustomSnackBarHostState() }
    val hazeState = rememberHazeState()
    val isDarkMode = isDarkMode()
    val reviewBottomSheetReviewWebViewContainer = remember {
        ReviewWebViewContainer(context, userViewModel.accessToken, isDarkMode).apply {
            this.webView.addJavascriptInterface(
                CloseBridge(onClose = { scope.launch { bottomSheet.hide() } }),
                "Snutt",
            )
        }
    }

    LaunchedEffect(searchResultPagingItems) {
        snapshotFlow { searchResultPagingItems.loadState }
            .collect { loadState ->
                searchViewModel.onLoadStateChanged(loadState)
            }
    }

    LaunchedEffect(Unit) {
        searchViewModel.searchUiEvent.collect { uiEvent ->
            when (uiEvent) {
                is SearchUiEvent.ShowToastError -> {
                    val displayMessage = uiEvent.displayMessage
                    context.toast(displayMessage)
                }
                is SearchUiEvent.ShowToast -> {
                    val resId = uiEvent.resId
                    context.toast(context.getString(resId))
                }
                is SearchUiEvent.LoggedOut -> {
                    onNavigateOnboardAsOrigin()
                }
                is SearchUiEvent.ShowAlertByEvent -> {
                    val onConfirm = uiEvent.onConfirm
                    val event = uiEvent.event
                    val dialogMessage = when (event) {
                        SearchAlertEvent.DELETE_VACANCY -> context.getString(R.string.vacancy_delete_dialog_message)
                        SearchAlertEvent.DELETE_BOOKMARK -> context.getString(R.string.bookmark_remove_check_message)
                    }
                    showDialog(
                        composableStates,
                        dialogMessage,
                        onConfirm,
                    )
                }

                is SearchUiEvent.ShowSnackBarByEvent -> {
                    val event = uiEvent.event
                    val message = when (event) {
                        SearchSnackBarEvent.FIRST_VACANCY_ADD -> context.getString(R.string.vacancy_first_add_snackbar_message)
                        SearchSnackBarEvent.FIRST_BOOKMARK_ADD -> context.getString(R.string.bookmark_first_alert_message)
                    }
                    val onActionPerformed = when (event) {
                        SearchSnackBarEvent.FIRST_VACANCY_ADD -> onNavigateVacancy
                        SearchSnackBarEvent.FIRST_BOOKMARK_ADD -> onNavigateBookmark
                    }
                    launch {
                        snackBarHostState.currentSnackBarData.dismiss()
                        val result = snackBarHostState.showSnackBar(
                            message = message,
                            actionLabel = context.getString(R.string.vacancy_first_add_snackbar_action_label),
                            duration = CustomSnackBarDuration(
                                fadeIn = 500L,
                                inBetween = 3000L,
                                fadeOut = 500L,
                            ),
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            onActionPerformed()
                        }
                    }
                }
            }
        }
    }

    BackHandler {
        searchViewModel.onClickBack()
    }

    Scaffold(
        snackbarHost = {
            CustomSnackBarHost(
                hostState = snackBarHostState,
                snackBar = { data ->
                    val currentSnackBarData = snackBarHostState.currentSnackBarData
                    CustomSnackBar(
                        modifier = Modifier.zIndex(10F),
                        snackBarData = currentSnackBarData,
                        passedData = data,
                        shape = RoundedCornerShape(10.dp),
                        backgroundColor = SNUTTColors.SnackbarBackground,
                        contentStyle = SNUTTTypography.body1.copy(
                            color = SNUTTColors.White,
                            fontWeight = FontWeight.Medium,
                        ),
                        actionLabelStyle = SNUTTTypography.body1.copy(
                            color = SNUTTColors.MilkMint,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        hazeState = hazeState,
                    )
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .hazeSource(hazeState),
        ) {
            SearchScreen(
                searchResultPagingItems = searchResultPagingItems,
                searchResultListState = searchResultListState,
                selectedTags = selectedTags,
                lazyListState = lazyListState,
                selectedLecture = selectedLecture,
                onSearch = searchViewModel::onSearch,
                onClearEditText = searchViewModel::onClearEditText,
                onFilter = {
                    bottomSheet.setSheetContent {
                        SearchOptionSheet(
                            applyOption = {
                                scope.launch {
                                    searchViewModel.onSearch()
                                    searchViewModel.storeRecentSearchedDepartments()
                                }
                                scope.launch { bottomSheet.hide() }
                            },
                            hideBottomSheet = {
                                scope.launch { bottomSheet.hide() }
                            },
                            tagsByTagType = tagsByTagType,
                            selectedTagType = selectedTagType,
                            recentSearchedDepartments = recentSearchedDepartments,
                            tagTypesNotEmpty = tagTypesNotEmpty,
                            draggedTimeBlock = draggedTimeBlock,
                            onSelectTagType = searchViewModel::setTagType,
                            onToggleTag = searchViewModel::onToggleTag,
                            onRemoveRecent = searchViewModel::removeRecentSearchedDepartment,
                            onTimeSelectCancel = searchViewModel::onTimeSelectCancel,
                            onTimeSelectConfirm = searchViewModel::onTimeSelectConfirm,
                        )
                    }
                    scope.launch { bottomSheet.show() }
                },
                onToggleTagAndQuery = { tag ->
                    scope.launch {
                        searchViewModel.onToggleTag(tag)
                        searchViewModel.onSearch()
                    }
                },
                onToggleLectureSelection = searchViewModel::onToggleLectureSelection,
                onClickLectureDetail = { lecture ->
                    lectureDetailViewModel.initializeEditingLectureDetail(
                        lecture, ModeType.Viewing,
                    )
                    val referrer = DetailScreenReferrer.Search(
                        searchViewModel.searchTitle.value,
                    )
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
                onClickBookmark = searchViewModel::onClickBookmark,
                onClickVacancy = searchViewModel::onClickVacancy,
                onToggleLectureContained = { lecture, contained ->
                    if (contained) {
                        scope.launch(Dispatchers.IO) {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                timetableViewModel.removeLecture(lecture)
                            }
                            searchViewModel.toggleLectureSelection(lecture)
                            tableListViewModel.fetchTableMap()
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
                                            referrer = LectureActionReferrer.Search(searchViewModel.searchTitle.value),
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
    }
}

@Composable
fun SearchScreen(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
    searchResultListState: SearchResultListState,
    selectedTags: List<TagDto>,
    lazyListState: LazyListState,
    selectedLecture: LectureDto?,
    onSearch: () -> Unit,
    onClearEditText: () -> Unit,
    onFilter: () -> Unit,
    onToggleTagAndQuery: (tag: TagDto) -> Unit,

    onToggleLectureSelection: (LectureDto) -> Unit,
    onClickLectureDetail: (LectureDto) -> Unit,
    onClickReview: (LectureDto) -> Unit,
    onClickBookmark: (LectureDto, Boolean) -> Unit,
    onClickVacancy: (LectureDto, Boolean) -> Unit,
    onToggleLectureContained: (LectureDto, Boolean) -> Unit,
) {
    var searchEditTextFocused by remember { mutableStateOf(false) }

    Column {
        TopBar(
            title = {
                Row(
                    modifier = Modifier
                        .padding(start = 12.dp, top = 5.dp, bottom = 5.dp, end = 12.dp)
                        .background(
                            SNUTTColors.Gray100,
                            shape = RoundedCornerShape(6.dp),
                        )
                        .fillMaxSize()
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
            },
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .background(SNUTTColors.White900)
                .fillMaxWidth(),
        ) {
            TimeTable(
                table = LocalTableState.current.table,
                trimParam = LocalTableState.current.trimParam,
                tableLectureCustomOptions = LocalTableState.current.tableLectureCustomOptions,
                previewTheme = LocalTableState.current.previewTheme,
                compactMode = LocalCompactState.current,
                navigator = LocalNavController.current,
                touchEnabled = false,
                selectedLecture = selectedLecture,
            )
            Box(
                modifier = Modifier
                    .background(SNUTTColors.Dim2)
                    .fillMaxSize(),
            ) {
                SearchResultList(
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
            }
        }
    }
}

@Preview
@Composable
fun SearchPagePreview() {
//    SearchPage()
}
