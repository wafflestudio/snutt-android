package com.wafflestudio.snutt2.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.ExitIcon
import com.wafflestudio.snutt2.ui.components.compose.FilterIcon
import com.wafflestudio.snutt2.ui.components.compose.SearchIcon
import com.wafflestudio.snutt2.ui.components.compose.TopBar
import com.wafflestudio.snutt2.ui.components.compose.clearFocusOnKeyboardDismiss
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.domain.model.SearchTag
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import com.wafflestudio.snutt2.feature.home.timetable.TimeTable

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    searchResultPagingItems: LazyPagingItems<DataWithState<SearchedLecture, LectureState>>,
    lazyListState: LazyListState,
    onSearch: () -> Unit,
    onSearchTitleChange: (String) -> Unit,
    onClearEditText: () -> Unit,
    onFilter: () -> Unit,
    onToggleTagAndQuery: (SearchTag) -> Unit,
    onToggleLectureSelection: (SearchedLecture) -> Unit,
    onClickLectureDetail: (SearchedLecture) -> Unit,
    onClickReview: (SearchedLecture) -> Unit,
    onClickBookmark: (SearchedLecture, Boolean) -> Unit,
    onClickVacancy: (SearchedLecture, Boolean) -> Unit,
    onToggleLectureContained: (SearchedLecture, Boolean) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDeleteBookmark: (SearchedLecture) -> Unit,
    onConfirmDeleteVacancy: (SearchedLecture) -> Unit,
    onConfirmAddWithOverlap: (SearchedLecture) -> Unit,
) {
    SearchDialogs(
        uiState = uiState,
        onDismiss = onDismissDialog,
        onConfirmDeleteBookmark = onConfirmDeleteBookmark,
        onConfirmDeleteVacancy = onConfirmDeleteVacancy,
        onConfirmAddWithOverlap = onConfirmAddWithOverlap,
    )

    Column {
        TopBar(
            title = {
                SearchTopBarContent(
                    searchTitle = uiState.searchTitle,
                    onSearchTitleChange = onSearchTitleChange,
                    onSearch = onSearch,
                    onClearEditText = onClearEditText,
                    onFilter = onFilter,
                )
            },
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .background(SNUTTColors.White900)
                .fillMaxWidth(),
        ) {
            TimeTable(
                lectures = uiState.currentTableLectures,
                selectedLecture = uiState.selectedLecture,
                fittedTrimParam = uiState.tableTrimParam,
                theme = uiState.tableTheme,
                isDarkMode = isDarkMode(),
                compactMode = uiState.isCompactMode,
                tableLectureCustomOptions = uiState.tableLectureCustomOptions,
                touchEnabled = false,
            )

            Box(modifier = Modifier.background(SNUTTColors.Dim2)) {
                SearchResultList(
                    searchResultPagingItems = searchResultPagingItems,
                    searchResultListState = uiState.searchResultListState,
                    selectedTags = uiState.selectedTags,
                    lazyListState = lazyListState,
                    onToggleTag = onToggleTagAndQuery,
                    onToggleLectureSelection = onToggleLectureSelection,
                    onClickLectureDetail = onClickLectureDetail,
                    onClickReview = onClickReview,
                    onClickBookmark = { lecture ->
                        val state = searchResultPagingItems.itemSnapshotList.items
                            .find { it.item.id == lecture.id }?.state
                        onClickBookmark(lecture, state?.isBookmarked ?: false)
                    },
                    onClickVacancy = { lecture ->
                        val state = searchResultPagingItems.itemSnapshotList.items
                            .find { it.item.id == lecture.id }?.state
                        onClickVacancy(lecture, state?.isVacancyRegistered ?: false)
                    },
                    onClickAddOrRemove = { lecture ->
                        val state = searchResultPagingItems.itemSnapshotList.items
                            .find { it.item.id == lecture.id }?.state
                        onToggleLectureContained(lecture, state?.contained ?: false)
                    },
                )
            }
        }
    }
}

@Composable
private fun RowScope.SearchTopBarContent(
    searchTitle: String,
    onSearchTitleChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearEditText: () -> Unit,
    onFilter: () -> Unit,
) {
    var searchEditTextFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(start = 8.dp, top = 5.dp, bottom = 5.dp)
            .background(SNUTTColors.Gray100, shape = RoundedCornerShape(6.dp))
            .fillMaxHeight()
            .weight(1f)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SearchIcon(
            modifier = Modifier.clicks { onSearch() },
        )
        EditText(
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { searchEditTextFocused = it.isFocused }
                .clearFocusOnKeyboardDismiss(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    searchEditTextFocused = false
                    onSearch()
                },
            ),
            value = searchTitle,
            onValueChange = onSearchTitleChange,
            singleLine = true,
            hint = stringResource(R.string.search_hint),
            underlineEnabled = false,
            clearFocusFlag = !searchEditTextFocused,
        )
        if (searchEditTextFocused) {
            ExitIcon(modifier = Modifier.clicks { onClearEditText() })
        } else {
            FilterIcon(modifier = Modifier.clicks { onFilter() })
        }
    }
}
