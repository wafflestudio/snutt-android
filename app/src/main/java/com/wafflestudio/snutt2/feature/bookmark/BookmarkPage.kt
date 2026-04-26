package com.wafflestudio.snutt2.feature.bookmark

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
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.ThemeReference
import com.wafflestudio.snutt2.ui.preview.PreviewData
import com.wafflestudio.snutt2.feature.home.timetable.TimeTable
import com.wafflestudio.snutt2.feature.search.BookmarkList
import com.wafflestudio.snutt2.feature.search.LectureState
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.components.compose.BottomSheetDismissEffect
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import com.wafflestudio.snutt2.ui.util.toast
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

                            val bookmarkImpressionModifier =
                                Modifier.logImpression(AnalyticsScreen.Bookmark)
                            if (uiState.bookmarkList.isEmpty()) {
                                Box(modifier = bookmarkImpressionModifier) {
                                    BookmarkPlaceHolder()
                                }
                            } else {
                                BookmarkList(
                                    modifier = bookmarkImpressionModifier,
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

@SnuttPreview
@Composable
private fun BookmarkScreen_List() {
    SnuttPreviewSurface {
        BookmarkScreen(
            uiState = BookmarkUiState.Success(
                currentTable = Table(
                    summary = TableSummary(
                        id = "table1",
                        courseBook = CourseBook(semester = 1, year = 2025),
                        title = "2025-1학기",
                        totalCredit = 0L,
                        isPrimary = true,
                    ),
                    lectures = emptyList(),
                    themeRef = ThemeReference.BuiltIn(0),
                ),
                tableTheme = BuiltInTheme.SNUTT,
                bookmarkList = PreviewData.sampleLectures.take(3).mapIndexed { index, lecture ->
                    lecture.toDataWithState(
                        LectureState(
                            selected = index == 0,
                            contained = false,
                            isBookmarked = true,
                            isVacancyRegistered = false,
                        ),
                    )
                },
                selectedLecture = null,
                tableTrimParam = TableTrimParam.Default,
                tableLectureCustomOptions = TableLectureCustom.Default,
                isCompactMode = false,
                uncheckedNotificationCount = 0,
                disableMapFeature = false,
                vacancyList = emptyList(),
            ),
            onClickLectureDetail = {},
            onClickReview = {},
            onClickBookmark = {},
            onClickVacancy = {},
            onToggleLectureContained = {},
            onToggleLectureSelection = {},
            onDismissDialog = {},
            onConfirmDeleteBookmark = {},
            onConfirmDeleteVacancyNotification = {},
            onConfirmForceAddLecture = {},
            onNavigateBack = {},
        )
    }
}
