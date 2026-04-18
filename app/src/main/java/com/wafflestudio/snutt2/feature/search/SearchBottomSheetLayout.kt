package com.wafflestudio.snutt2.feature.search

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.wafflestudio.snutt2.domain.model.SearchTag
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.TagType
import com.wafflestudio.snutt2.feature.search.searchoption.SearchOptionSheet
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.LocalAnalyticsLogger
import com.wafflestudio.snutt2.ui.components.compose.ModalBottomSheetPlaceholder
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

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
    onReview: (SearchedLecture) -> Unit,
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

                is SearchUiState.BottomSheetType.LectureDetail -> {
                    SearchLectureDetailSheetContent(
                        bottomSheetType = bottomSheetType,
                        bookmarks = uiState.bookmarks,
                        vacancyList = uiState.vacancyList,
                        tableTheme = uiState.tableTheme,
                        courseBook = uiState.courseBook,
                        disableMapFeature = uiState.disableMapFeature,
                        onDismiss = onDismiss,
                        onBookmarkToggle = onBookmarkToggle,
                        onVacancyToggle = onVacancyToggle,
                        onSyllabus = onSyllabus,
                        onReview = onReview,
                    )
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
