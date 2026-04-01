package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.components.compose.ModalBottomSheetPlaceholder
import com.wafflestudio.snutt2.domainmodel.SearchTag
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.ReviewDetailParameter
import com.wafflestudio.snutt2.model.TagType
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.search.search_option.SearchOptionSheet

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
    val analyticsLogger = LocalAnalyticsLogger.current

    ModalBottomSheetLayout(
        sheetContent = {
            when (val bottomSheetType = uiState.bottomSheetType) {
                SearchUiState.BottomSheetType.None -> {
                    ModalBottomSheetPlaceholder()
                }

                SearchUiState.BottomSheetType.Filter -> {
                    if (uiState.pageMode == PageMode.Search) {
                        LaunchedEffect(Unit) {
                            analyticsLogger.logScreen(AnalyticsScreen.SearchFilter)
                        }
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
                        disableMapFeature = uiState.disableMapFeature,
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
                    LaunchedEffect(Unit) {
                        analyticsLogger.logScreen(
                            AnalyticsScreen.ReviewDetail(
                                ReviewDetailParameter(
                                    lectureId = bottomSheetType.lecture.id,
                                    referrer = bottomSheetType.referrer,
                                ),
                            ),
                        )
                    }
                    ReviewWebView(modifier = Modifier.fillMaxHeight(0.95f), reviewWebViewContainer = reviewWebViewContainer)
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
