package com.wafflestudio.snutt2.views.logged_in.home.bookmark

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.lib.getReviewUrl
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.lib.logging.ReviewDetailParameter
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
import com.wafflestudio.snutt2.views.LocalHomePageController
import com.wafflestudio.snutt2.views.LocalReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.BottomNavigation
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor.TimeTableNew
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ModeType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookmarkRoute(
    viewModel: BookmarkViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel, // RootActivity에서 주입 받아서 사용 중
    onNavigateToOnboard: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val userViewModel = hiltViewModel<UserViewModel>()
    val pageController = LocalHomePageController.current // FIXME: viewModel으로 옮길 것

    val trimParam by userViewModel.trimParam.collectAsState()
    val tableLectureCustomOptions by userViewModel.tableLectureCustomOption.collectAsState()
    val uncheckedNotification by homeViewModel.uncheckedNotification.collectAsState()

    val uiState by viewModel.uiState.collectAsState(initial = BookmarkUiState.Loading)
    val scope = rememberCoroutineScope()
    val isDarkMode = isDarkMode()
    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModel>()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    val reviewWebViewContainer = remember {
        ReviewWebViewContainer(context, userViewModel.accessToken, isDarkMode).apply {
            this.webView.addJavascriptInterface(
                CloseBridge(onClose = { scope.launch { viewModel.onDismissBottomSheet() } }),
                "Snutt",
            )
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.isVisible }
            .collect { isVisible ->
                if (!isVisible) {
                    viewModel.onDismissBottomSheet()
                }
            }
    }

    val bottomSheetState = (uiState as? BookmarkUiState.Success)?.bottomSheetState ?: BookmarkUiState.BottomSheetState.None
    LaunchedEffect(bottomSheetState) {
        when (bottomSheetState) {
            is BookmarkUiState.BottomSheetState.LectureDetail -> {
                lectureDetailViewModel.initializeEditingLectureDetail(
                    LectureDto.fromSearchedLecture(bottomSheetState.lecture),
                    ModeType.Viewing,
                )
                sheetState.show()
            }
            is BookmarkUiState.BottomSheetState.Review -> {
                val url = bottomSheetState.lecture.reviewInfo.getReviewUrl(context)
                reviewWebViewContainer.openPage("$url&on_back=close")
                sheetState.show()
            }
            is BookmarkUiState.BottomSheetState.None -> {
                sheetState.hide()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is BookmarkUiEvent.ShowToast -> context.toast(event.message)
                BookmarkUiEvent.NavigateToOnboard -> {
                    onNavigateToOnboard()
                }
            }
        }
    }

    BackHandler(enabled = sheetState.isVisible) {
        viewModel.onDismissBottomSheet()
    }

    BookmarkBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (bottomSheetState) {
                is BookmarkUiState.BottomSheetState.LectureDetail -> {
                    LectureDetailPage(
                        referrer = DetailScreenReferrer.Bookmark,
                        onCloseViewMode = { scope ->
                            scope.launch { viewModel.onDismissBottomSheet() }
                        },
                    )
                }
                is BookmarkUiState.BottomSheetState.Review -> {
                    val analyticsLogger = LocalAnalyticsLogger.current
                    LaunchedEffect(sheetState.isVisible) {
                        if (sheetState.isVisible) {
                            analyticsLogger.logScreen(
                                AnalyticsScreen.ReviewDetail(
                                    ReviewDetailParameter(
                                        lectureId = bottomSheetState.lecture.id,
                                        referrer = DetailScreenReferrer.Bookmark,
                                    ),
                                ),
                            )
                        }
                    }
                    CompositionLocalProvider(LocalReviewWebView provides reviewWebViewContainer) {
                        ReviewWebView(
                            height = 0.95f,
                        )
                    }
                }
                else -> {
                    Box(modifier = Modifier)
                }
            }
        },
    ) {
        val compactMode by userViewModel.compactMode.collectAsState()
        BookmarkScreen(
            uiState = uiState,
            isDarkMode = isDarkMode,
            compactMode = compactMode,
            tableTrimParam = trimParam,
            tableLectureCustomOptions = tableLectureCustomOptions,
            onNavigateBack = onNavigateBack,
            onClickLectureDetail = viewModel::onClickLectureDetail,
            onClickReview = viewModel::onClickReview,
            onClickBookmark = viewModel::onClickBookmark,
            onClickVacancy = viewModel::onClickVacancy,
            onToggleLectureContained = viewModel::onToggleLectureContained,
            onToggleLectureSelection = viewModel::onToggleLectureSelection,
            onDismissDialog = viewModel::onDismissDialog,
            onDismissBottomSheet = viewModel::onDismissBottomSheet,
            onConfirmDeleteBookmark = viewModel::onConfirmDeleteBookmark,
            onConfirmDeleteVacancyNotification = viewModel::onConfirmDeleteVacancyNotification,
            onConfirmForceAddLecture = viewModel::onConfirmForceAddLecture,
            uncheckedNotification = uncheckedNotification,
            pageState = pageController.homePageState.value,
            onUpdateHomePageState = { item ->
                onNavigateBack()
                pageController.update(item)
            },
        )
    }
}

@Composable
fun BookmarkBottomSheetLayout(
    sheetState: ModalBottomSheetState,
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
        scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
        sheetGesturesEnabled = false,
        sheetContent = sheetContent,
        content = content,
    )
}

@Composable
fun BookmarkScreen(
    uiState: BookmarkUiState,
    isDarkMode: Boolean,
    compactMode: Boolean,
    tableTrimParam: TableTrimParam,
    tableLectureCustomOptions: TableLectureCustom,
    onClickLectureDetail: (SearchedLecture) -> Unit,
    onClickReview: (SearchedLecture) -> Unit,
    onClickBookmark: (SearchedLecture, Boolean) -> Unit,
    onClickVacancy: (SearchedLecture, Boolean) -> Unit,
    onToggleLectureContained: (SearchedLecture, Boolean) -> Unit,
    onToggleLectureSelection: (SearchedLecture) -> Unit,
    onDismissDialog: () -> Unit,
    onDismissBottomSheet: () -> Unit,
    onConfirmDeleteBookmark: (SearchedLecture) -> Unit,
    onConfirmDeleteVacancyNotification: (SearchedLecture) -> Unit,
    onConfirmForceAddLecture: (SearchedLecture) -> Unit,
    onNavigateBack: () -> Unit,
    uncheckedNotification: Long,
    pageState: HomeItem,
    onUpdateHomePageState: (HomeItem) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(SNUTTColors.White900),
        ) {
            when (uiState) {
                BookmarkUiState.Empty -> {
                    Column {
                        SimpleTopBar(
                            title = stringResource(R.string.bookmark_page_title),
                            onClickNavigateBack = onNavigateBack,
                        )
                        BookmarkPlaceHolder()
                    }
                }

                BookmarkUiState.Error -> {}
                BookmarkUiState.Loading -> {}
                is BookmarkUiState.Success -> {
                    val bookmarks = uiState.bookmarkList
                    val selectedLecture = uiState.bookmarkList.firstOrNull { it -> it.state.selected }?.item
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
                            uiState.currentTable?.let { table ->
                                val fittedTrimParam = if (tableTrimParam.forceFitLectures) {
                                    (
                                        selectedLecture?.let {
                                            LectureDto.fromSearchedLecture(it).toLocalLecture()
                                        }?.let { table.lectures + it } ?: table.lectures
                                        ).getFittingTrimParam(TableTrimParam.Default)
                                } else {
                                    tableTrimParam
                                }
                                TimeTableNew(
                                    lectures = table.lectures,
                                    selectedLecture = selectedLecture,
                                    fittedTrimParam = fittedTrimParam,
                                    theme = uiState.tableTheme,
                                    isDarkMode = isDarkMode,
                                    compactMode = compactMode,
                                    tableLectureCustomOptions = tableLectureCustomOptions,
                                    touchEnabled = false,
                                )
                            }
                            BookmarkListNew(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SNUTTColors.Dim2),
                                bookmarks = bookmarks,
                                onToggleLectureSelection = onToggleLectureSelection,
                                onClickLectureDetail = onClickLectureDetail,
                                onClickReview = onClickReview,
                                onClickBookmark = onClickBookmark,
                                onClickVacancy = onClickVacancy,
                                onToggleLectureContained = onToggleLectureContained,
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
        BottomNavigation(
            pageState = pageState,
            uncheckedNotificationExist = uncheckedNotification > 0,
            onUpdatePageState = onUpdateHomePageState,
        )
    }
}
