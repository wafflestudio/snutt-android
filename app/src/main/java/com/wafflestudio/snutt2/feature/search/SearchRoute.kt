package com.wafflestudio.snutt2.feature.search

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.wafflestudio.snutt2.ui.components.compose.BottomSheetDismissEffect
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBarDuration
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBarHostState
import com.wafflestudio.snutt2.ui.components.compose.snackbar.SnackBarScaffold
import com.wafflestudio.snutt2.ui.components.compose.snackbar.dismiss
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.ui.util.toast
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel(),
    bottomBar: @Composable () -> Unit,
    onNavigateVacancy: () -> Unit,
    onNavigateOnboardAsOrigin: () -> Unit,
    onNavigateToReview: (SearchedLecture) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val queryResults = viewModel.queryResults.collectAsLazyPagingItems()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    var lazyListState by remember { mutableStateOf(LazyListState(0, 0)) }
    val snackBarHostState = remember { CustomSnackBarHostState() }
    val hazeState = rememberHazeState()

    val activeSheet = uiState.activeBottomSheet
    BackHandler(enabled = activeSheet != null) {
        viewModel.closeBottomSheet()
    }

    BottomSheetDismissEffect(sheetState, viewModel::onSheetDismissed)

    // UiEvent 처리
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is SearchUiEvent.ShowToast -> {
                    context.toast(uiEvent.displayMessage)
                }

                is SearchUiEvent.LoggedOut -> {
                    onNavigateOnboardAsOrigin()
                }

                is SearchUiEvent.OpenBottomSheet -> {
                    scope.launch { sheetState.show() }
                }

                is SearchUiEvent.CloseBottomSheet -> {
                    scope.launch { sheetState.hide() }
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
                        SearchSnackBarEvent.FIRST_BOOKMARK_ADD -> { {} }
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
                onSelectTagType = viewModel::setTagType,
                onToggleTag = viewModel::onToggleTag,
                onRemoveRecent = viewModel::removeRecentSearchedDepartment,
                onTimeSelectCancel = viewModel::onTimeSelectCancel,
                onTimeSelectConfirm = viewModel::onTimeSelectConfirm,
                applyFilter = viewModel::applyFilterAndClose,
                onDismiss = viewModel::closeBottomSheet,
                onBookmarkToggle = viewModel::onClickBookmark,
                onVacancyToggle = viewModel::onClickVacancy,
                onSyllabus = viewModel::openSyllabus,
                onReview = onNavigateToReview,
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        SearchScreen(
                            uiState = uiState,
                            searchResultPagingItems = queryResults,
                            lazyListState = lazyListState,
                            onSearch = viewModel::onSearch,
                            onSearchTitleChange = viewModel::setTitle,
                            onClearEditText = viewModel::onClearEditText,
                            onFilter = viewModel::openFilterSheet,
                            onToggleTagAndQuery = viewModel::onToggleTagAndQuery,
                            onToggleLectureSelection = viewModel::onToggleLectureSelection,
                            onClickLectureDetail = viewModel::openLectureDetailSheet,
                            onClickReview = onNavigateToReview,
                            onClickBookmark = viewModel::onClickBookmark,
                            onClickVacancy = viewModel::onClickVacancy,
                            onToggleLectureContained = viewModel::onToggleLectureContained,
                            onDismissDialog = viewModel::dismissDialog,
                            onConfirmDeleteBookmark = viewModel::confirmDeleteBookmark,
                            onConfirmDeleteVacancy = viewModel::confirmDeleteVacancy,
                            onConfirmAddWithOverlap = viewModel::confirmAddWithOverlap,
                        )
                    }
                    bottomBar()
                }
            }
        }
    }
}
