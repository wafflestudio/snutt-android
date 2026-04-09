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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BottomSheetDismissEffect
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.lib.android.toast
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
    onNavigateToReview: (SearchedLecture) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    val activeSheet = uiState.activeBottomSheet
    BackHandler(enabled = activeSheet != null) {
        viewModel.closeBottomSheet()
    }

    BottomSheetDismissEffect(sheetState, viewModel::onSheetDismissed)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is BookmarkUiEvent.ShowToast -> context.toast(event.message)

                BookmarkUiEvent.NavigateToOnboard -> onNavigateToOnboard()

                BookmarkUiEvent.OpenBottomSheet -> {
                    scope.launch { sheetState.show() }
                }

                BookmarkUiEvent.CloseBottomSheet -> {
                    scope.launch { sheetState.hide() }
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
        onReview = onNavigateToReview,
    ) {
        BookmarkScreen(
            uiState = uiState,
            onNavigateBack = onNavigateBack,
            onClickLectureDetail = viewModel::openLectureDetailSheet,
            onClickReview = onNavigateToReview,
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
