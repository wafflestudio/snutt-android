package com.wafflestudio.snutt2.views.logged_in.home.search.refactor

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBarDuration
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBarHostState
import com.wafflestudio.snutt2.components.compose.snackbar.SnackBarScaffold
import com.wafflestudio.snutt2.components.compose.snackbar.dismiss
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
fun SearchRouteNew(
    searchViewModelNew: SearchViewModelNew = hiltViewModel(),
    onNavigateVacancy: () -> Unit,
    onNavigateOnboardAsOrigin: () -> Unit,
    onBottomNavigate: (HomeItem) -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val uiState by searchViewModelNew.uiState.collectAsStateWithLifecycle()
    val queryResults = searchViewModelNew.queryResults.collectAsLazyPagingItems()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    // FIXME: 이 상태의 관리 주체는 SearchRoute 인가, 아니면 강의상세 바텀시트인가...
    val detailReviewSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    var lazyListState by remember { mutableStateOf(LazyListState(0, 0)) }
    val snackBarHostState = remember { CustomSnackBarHostState() }
    val hazeState = rememberHazeState()
    val isDarkMode = isDarkMode()

    val reviewWebViewContainer = remember {
        ReviewWebViewContainer(context, userViewModel.accessToken, isDarkMode).apply {
            this.webView.addJavascriptInterface(
                CloseBridge(onClose = { scope.launch { searchViewModelNew.closeBottomSheet() } }),
                "Snutt",
            )
        }
    }

    // FIXME: 위와 동일 고민) 이것의 관리 주체는 SearchRoute 인가, 아니면 강의상세 바텀시트인가...
    val detailReviewWebViewContainer = remember {
        ReviewWebViewContainer(context, userViewModel.accessToken, isDarkMode).apply {
            this.webView.addJavascriptInterface(
                CloseBridge(onClose = { scope.launch { searchViewModelNew.closeDetailReview() } }),
                "Snutt",
            )
        }
    }

    BackHandler {
        searchViewModelNew.onClickBack()
    }

    // UiEvent 처리
    LaunchedEffect(Unit) {
        searchViewModelNew.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is SearchUiEvent.ShowToast -> {
                    context.toast(uiEvent.displayMessage)
                }

                is SearchUiEvent.LoggedOut -> {
                    onNavigateOnboardAsOrigin()
                }

                is SearchUiEvent.OpenBottomSheet -> {
                    val bottomSheetType = uiState.bottomSheetType
                    if (bottomSheetType is SearchUiState.BottomSheetType.Review) {
                        val reviewId = bottomSheetType.lecture.reviewInfo.id
                        val url = context.getString(R.string.review_base_url) + "/detail?id=$reviewId&on_back=close"
                        reviewWebViewContainer.openPage(url)
                    }
                    scope.launch { sheetState.show() }
                }

                is SearchUiEvent.CloseBottomSheet -> {
                    scope.launch { sheetState.hide() }
                }

                is SearchUiEvent.OpenDetailReviewSheet -> {
                    val bt = uiState.bottomSheetType as? SearchUiState.BottomSheetType.LectureDetail ?: return@collect
                    val reviewId = bt.lecture.reviewInfo.id
                    val url = context.getString(R.string.review_base_url) + "/detail?id=$reviewId&on_back=close"
                    detailReviewWebViewContainer.openPage(url)
                    scope.launch { detailReviewSheetState.show() }
                }

                is SearchUiEvent.CloseDetailReviewSheet -> {
                    scope.launch { detailReviewSheetState.hide() }
                }

                is SearchUiEvent.OpenUrl -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, uiEvent.url.toUri()))
                }

                is SearchUiEvent.ShowSnackBar -> {
                    val message = when (uiEvent.event) {
                        SearchSnackBarEvent.FIRST_VACANCY_ADD -> context.getString(R.string.vacancy_first_add_snackbar_message)
                        SearchSnackBarEvent.FIRST_BOOKMARK_ADD -> context.getString(R.string.bookmark_first_alert_message)
                    }
                    val onActionPerformed = when (uiEvent.event) {
                        SearchSnackBarEvent.FIRST_VACANCY_ADD -> onNavigateVacancy
                        SearchSnackBarEvent.FIRST_BOOKMARK_ADD -> searchViewModelNew::onTogglePageMode
                    }
                    scope.launch {
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

                is SearchUiEvent.ResetScroll -> {
                    lazyListState = LazyListState(0, 0)
                }
            }
        }
    }

    SearchDialogs(
        uiState = uiState,
        onDismiss = searchViewModelNew::dismissDialog,
        onConfirmDeleteBookmark = searchViewModelNew::confirmDeleteBookmark,
        onConfirmDeleteVacancy = searchViewModelNew::confirmDeleteVacancy,
        onConfirmAddWithOverlap = searchViewModelNew::confirmAddWithOverlap,
    )

    SnackBarScaffold(
        snackBarHostState = snackBarHostState,
        hazeState = hazeState,
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .hazeSource(hazeState),
        ) {
            SearchBottomSheetLayout(
                uiState = uiState,
                sheetState = sheetState,
                onSelectTagType = searchViewModelNew::setTagType,
                onToggleTag = searchViewModelNew::onToggleTag,
                onRemoveRecent = searchViewModelNew::removeRecentSearchedDepartment,
                onTimeSelectCancel = searchViewModelNew::onTimeSelectCancel,
                onTimeSelectConfirm = searchViewModelNew::onTimeSelectConfirm,
                applyFilter = searchViewModelNew::applyFilterAndClose,
                onDismiss = searchViewModelNew::closeBottomSheet,
                onBookmarkToggle = searchViewModelNew::onClickBookmark,
                onVacancyToggle = searchViewModelNew::onClickVacancy,
                onSyllabus = searchViewModelNew::openSyllabus,
                onReviewFromDetail = searchViewModelNew::openDetailReview,
                onCloseDetailReview = searchViewModelNew::closeDetailReview,
                detailReviewSheetState = detailReviewSheetState,
                detailReviewWebViewContainer = detailReviewWebViewContainer,
                reviewWebViewContainer = reviewWebViewContainer,
            ) {
                SearchScreenNew(
                    uiState = uiState,
                    searchResultPagingItems = queryResults,
                    lazyListState = lazyListState,
                    onSearch = searchViewModelNew::onSearch,
                    onSearchTitleChange = searchViewModelNew::setTitle,
                    onClearEditText = searchViewModelNew::onClearEditText,
                    onFilter = searchViewModelNew::openFilterSheet,
                    onToggleMode = searchViewModelNew::onTogglePageMode,
                    onToggleTagAndQuery = searchViewModelNew::onToggleTagAndQuery,
                    onToggleLectureSelection = searchViewModelNew::onToggleLectureSelection,
                    onClickLectureDetail = searchViewModelNew::openLectureDetailSheet,
                    onClickReview = searchViewModelNew::openReviewSheet,
                    onClickBookmark = searchViewModelNew::onClickBookmark,
                    onClickVacancy = searchViewModelNew::onClickVacancy,
                    onToggleLectureContained = searchViewModelNew::onToggleLectureContained,
                    onBottomNavigate = onBottomNavigate,
                )
            }
        }
    }
}
