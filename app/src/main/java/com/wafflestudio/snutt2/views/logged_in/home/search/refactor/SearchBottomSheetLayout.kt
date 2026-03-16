package com.wafflestudio.snutt2.views.logged_in.home.search.refactor

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import com.wafflestudio.snutt2.domainmodel.SearchTag
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.components.compose.ModalBottomSheetPlaceholder
import com.wafflestudio.snutt2.model.TagType
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.search.refactor.search_option.SearchOptionSheet

@Composable
fun SearchBottomSheetLayout(
    uiState: SearchUiState,
    sheetState: ModalBottomSheetState,

    onSelectTagType: (TagType) -> Unit,
    onToggleTag: (SearchTag) -> Unit,
    onRemoveRecent: (SearchTag) -> Unit,
    onTimeSelectCancel: () -> Unit,
    onTimeSelectConfirm: (List<List<Boolean>>) -> Unit,
    applyFilter: () -> Unit,
    onDismiss: () -> Unit,

    onBookmarkToggle: (lecture: SearchedLecture, isBookmarked: Boolean) -> Unit,
    onVacancyToggle: (lecture: SearchedLecture, isVacancyRegistered: Boolean) -> Unit,
    onSyllabus: (SearchedLecture) -> Unit,
    onReviewFromDetail: () -> Unit,
    onCloseDetailReview: () -> Unit,
    detailReviewSheetState: ModalBottomSheetState,
    detailReviewWebViewContainer: ReviewWebViewContainer,

    reviewWebViewContainer: ReviewWebViewContainer,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            onDismiss()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            when (val bottomSheetType = uiState.bottomSheetType) {
                SearchUiState.BottomSheetType.None -> {
                    ModalBottomSheetPlaceholder()
                }

                SearchUiState.BottomSheetType.Filter -> {
                    if (uiState.pageMode == PageMode.Search) {
                        // TODO: caller 에서 BottomSheetLoggingEffect(analyticsScreen = AnalyticsScreen.SearchFilter) 호출 필요
                        SearchOptionSheet(
                            searchTags = uiState.searchTags,
                            tagTypes = uiState.tagTypes,
                            selectedTagType = uiState.selectedTagType,
                            recentSearchedDepartments = uiState.recentSearchedDepartments,
                            draggedTimeBlock = uiState.draggedTimeBlock,
                            currentTableLectures = uiState.currentTableLectures,
                            tableLectureCustomOptions = uiState.tableLectureCustomOptions,
                            onSelectTagType = onSelectTagType,
                            onToggleTag = onToggleTag,
                            onRemoveRecentSearchedDepartments = onRemoveRecent,
                            onTimeSelectCancel = onTimeSelectCancel,
                            onTimeSelectConfirm = onTimeSelectConfirm,
                            applyOption = applyFilter,
                            hideBottomSheet = onDismiss,
                        )
                    }
                }

                is SearchUiState.BottomSheetType.LectureDetail -> {
                    SearchLectureDetailSheetContent(
                        bottomSheetType = bottomSheetType,
                        bookmarks = uiState.bookmarks,
                        vacancyList = uiState.vacancyList,
                        tableTheme = uiState.tableTheme,
                        courseBook = uiState.courseBook,
                        detailReviewSheetState = detailReviewSheetState,
                        detailReviewWebViewContainer = detailReviewWebViewContainer,
                        onDismiss = onDismiss,
                        onBookmarkToggle = onBookmarkToggle,
                        onVacancyToggle = onVacancyToggle,
                        onSyllabus = onSyllabus,
                        onReviewFromDetail = onReviewFromDetail,
                        onCloseDetailReview = onCloseDetailReview,
                    )
                }

                is SearchUiState.BottomSheetType.Review -> {
                    CompositionLocalProvider(LocalReviewWebView provides reviewWebViewContainer) {
                        ReviewWebView(height = 0.95f)
                    }
                }
            }
        },
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
        scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
        sheetGesturesEnabled = false,
    ) {
        content()
    }
}
