package com.wafflestudio.snutt2.views.logged_in.home.bookmark

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.wafflestudio.snutt2.components.compose.BottomSheetDismissEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.logged_in.home.search.BookmarkList
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import kotlinx.coroutines.launch

@Composable
fun BookmarkRoute(
    viewModel: BookmarkViewModel = hiltViewModel(),
    onNavigateToOnboard: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkMode = isDarkMode()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    // FIXME: (SearchRoute와 동일) 이 상태의 관리 주체는 SearchRoute 인가, 아니면 강의상세 바텀시트인가...
    val detailReviewSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    val reviewWebViewContainer = remember {
        ReviewWebViewContainer(context, viewModel.accessToken, isDarkMode).apply {
            this.webView.addJavascriptInterface(
                CloseBridge(onClose = { scope.launch { viewModel.closeBottomSheet() } }),
                "Snutt",
            )
        }
    }

    val detailReviewWebViewContainer = remember {
        ReviewWebViewContainer(context, viewModel.accessToken, isDarkMode).apply {
            this.webView.addJavascriptInterface(
                CloseBridge(onClose = { scope.launch { viewModel.closeDetailReview() } }),
                "Snutt",
            )
        }
    }

    val bottomSheetType = (uiState as? BookmarkUiState.Success)?.bottomSheetType
    BackHandler(enabled = bottomSheetType != null && bottomSheetType != BookmarkUiState.BottomSheetType.None) {
        if (bottomSheetType is BookmarkUiState.BottomSheetType.LectureDetail && bottomSheetType.reviewVisible) {
            viewModel.closeDetailReview()
        } else {
            viewModel.closeBottomSheet()
        }
    }

    BottomSheetDismissEffect(sheetState, viewModel::onSheetDismissed)
    BottomSheetDismissEffect(detailReviewSheetState, viewModel::onDetailReviewSheetDismissed)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is BookmarkUiEvent.ShowToast -> context.toast(event.message)

                BookmarkUiEvent.NavigateToOnboard -> onNavigateToOnboard()

                BookmarkUiEvent.OpenBottomSheet -> {
                    val successState = uiState as? BookmarkUiState.Success ?: return@collect
                    val bottomSheetType = successState.bottomSheetType
                    if (bottomSheetType is BookmarkUiState.BottomSheetType.Review) {
                        val reviewId = bottomSheetType.lecture.reviewInfo.id
                        val url = context.getString(R.string.review_base_url) + "/detail?id=$reviewId&on_back=close"
                        reviewWebViewContainer.openPage(url)
                    }
                    scope.launch { sheetState.show() }
                }

                BookmarkUiEvent.CloseBottomSheet -> {
                    scope.launch { sheetState.hide() }
                }

                BookmarkUiEvent.OpenDetailReviewSheet -> {
                    val successState = uiState as? BookmarkUiState.Success ?: return@collect
                    val bt = successState.bottomSheetType as? BookmarkUiState.BottomSheetType.LectureDetail ?: return@collect
                    val reviewId = bt.lecture.reviewInfo.id
                    val url = context.getString(R.string.review_base_url) + "/detail?id=$reviewId&on_back=close"
                    detailReviewWebViewContainer.openPage(url)
                    scope.launch { detailReviewSheetState.show() }
                }

                BookmarkUiEvent.CloseDetailReviewSheet -> {
                    scope.launch { detailReviewSheetState.hide() }
                }

                is BookmarkUiEvent.OpenUrl -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, event.url.toUri()))
                }
            }
        }
    }

    BookmarkBottomSheetLayout(
        uiState = uiState,
        sheetState = sheetState,
        onDismiss = viewModel::closeBottomSheet,
        onBookmarkToggle = viewModel::onClickBookmark,
        onVacancyToggle = viewModel::onClickVacancy,
        onSyllabus = viewModel::openSyllabus,
        onReviewFromDetail = viewModel::openDetailReview,
        onCloseDetailReview = viewModel::closeDetailReview,
        detailReviewSheetState = detailReviewSheetState,
        detailReviewWebViewContainer = detailReviewWebViewContainer,
        reviewWebViewContainer = reviewWebViewContainer,
    ) {
        BookmarkScreen(
            uiState = uiState,
            onNavigateBack = onNavigateBack,
            onClickLectureDetail = viewModel::openLectureDetailSheet,
            onClickReview = viewModel::openReviewSheet,
            onClickBookmark = viewModel::onClickBookmark,
            onClickVacancy = viewModel::onClickVacancy,
            onToggleLectureContained = viewModel::onToggleLectureContained,
            onToggleLectureSelection = viewModel::onToggleLectureSelection,
            onDismissDialog = viewModel::onDismissDialog,
            onConfirmDeleteBookmark = viewModel::onConfirmDeleteBookmark,
            onConfirmDeleteVacancyNotification = viewModel::onConfirmDeleteVacancyNotification,
            onConfirmForceAddLecture = viewModel::onConfirmForceAddLecture,
        )
    }
}

@Composable
fun BookmarkScreen(
    uiState: BookmarkUiState,
    onClickLectureDetail: (SearchedLecture) -> Unit,
    onClickReview: (SearchedLecture) -> Unit,
    onClickBookmark: (SearchedLecture) -> Unit,
    onClickVacancy: (SearchedLecture) -> Unit,
    onToggleLectureContained: (SearchedLecture) -> Unit,
    onToggleLectureSelection: (SearchedLecture) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDeleteBookmark: (SearchedLecture) -> Unit,
    onConfirmDeleteVacancyNotification: (SearchedLecture) -> Unit,
    onConfirmForceAddLecture: (SearchedLecture) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(SNUTTColors.White900),
        ) {
            when (uiState) {
                BookmarkUiState.Loading -> {}
                is BookmarkUiState.Success -> {
                    Column {
                        SimpleTopBar(
                            title = stringResource(R.string.bookmark_page_title),
                            onClickNavigateBack = onNavigateBack,
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SNUTTColors.White900),
                        ) {
                            TimeTable(
                                lectures = uiState.currentTable.lectures,
                                selectedLecture = uiState.selectedLecture,
                                fittedTrimParam = uiState.tableTrimParam,
                                theme = uiState.tableTheme,
                                isDarkMode = isDarkMode(),
                                compactMode = uiState.isCompactMode,
                                tableLectureCustomOptions = uiState.tableLectureCustomOptions,
                                touchEnabled = false,
                            )

                            BookmarkList(
                                modifier = Modifier.background(SNUTTColors.Dim2),
                                bookmarks = uiState.bookmarkList,
                                onToggleLectureSelection = onToggleLectureSelection,
                                onClickLectureDetail = onClickLectureDetail,
                                onClickReview = onClickReview,
                                onClickBookmark = onClickBookmark,
                                onClickVacancy = onClickVacancy,
                                onClickAddOrRemove = onToggleLectureContained,
                            )
                        }
                    }

                    BookmarkDialogContent(
                        dialogState = uiState.dialogState,
                        onDismiss = onDismissDialog,
                        onConfirmDeleteBookmark = onConfirmDeleteBookmark,
                        onConfirmDeleteVacancyNotification = onConfirmDeleteVacancyNotification,
                        onConfirmForceAddLecture = onConfirmForceAddLecture,
                    )
                }
            }
        }
    }
}
