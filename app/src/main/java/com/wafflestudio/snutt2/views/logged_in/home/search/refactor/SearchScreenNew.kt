package com.wafflestudio.snutt2.views.logged_in.home.search.refactor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
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
import com.wafflestudio.snutt2.components.compose.BookmarkIcon
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.ExitIcon
import com.wafflestudio.snutt2.components.compose.FilterIcon
import com.wafflestudio.snutt2.components.compose.IconWithAlertDot
import com.wafflestudio.snutt2.components.compose.SearchIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clearFocusOnKeyboardDismiss
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.SearchTag
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureState
import com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor.TimeTableNew

@Composable
fun SearchScreenNew(
    uiState: SearchUiState,
    searchResultPagingItems: LazyPagingItems<DataWithState<SearchedLecture, LectureState>>,
    lazyListState: LazyListState,
    onSearch: () -> Unit,
    onSearchTitleChange: (String) -> Unit,
    onClearEditText: () -> Unit,
    onFilter: () -> Unit,
    onToggleMode: () -> Unit,
    onToggleTagAndQuery: (SearchTag) -> Unit,
    onToggleLectureSelection: (SearchedLecture) -> Unit,
    onClickLectureDetail: (SearchedLecture) -> Unit,
    onClickReview: (SearchedLecture) -> Unit,
    onClickBookmark: (SearchedLecture, Boolean) -> Unit,
    onClickVacancy: (SearchedLecture, Boolean) -> Unit,
    onToggleLectureContained: (SearchedLecture, Boolean) -> Unit,
) {
    val isSearchMode = uiState.pageMode == PageMode.Search

    Column {
        TopBar(
            title = {
                AnimatedContent(
                    targetState = isSearchMode,
                    transitionSpec = {
                        if (targetState) {
                            slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut() using SizeTransform(clip = false)
                        } else {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut() using SizeTransform(clip = false)
                        }
                    },
                    label = "top bar animation",
                ) { isSearch ->
                    if (isSearch) {
                        SearchTopBarContent(
                            searchTitle = uiState.searchTitle,
                            onSearchTitleChange = onSearchTitleChange,
                            onSearch = onSearch,
                            onClearEditText = onClearEditText,
                            onFilter = onFilter,
                        )
                    } else {
                        BookmarkTopBarContent()
                    }
                }
            },
            actions = {
                IconWithAlertDot(uiState.firstBookmarkAlert) { centerAlignedModifier ->
                    BookmarkIcon(
                        modifier = centerAlignedModifier
                            .size(30.dp)
                            .clicks { onToggleMode() },
                        marked = !isSearchMode,
                    )
                }
            },
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .background(SNUTTColors.White900)
                .fillMaxWidth(),
        ) {
            TimeTableNew(
                lectures = uiState.currentTableLectures,
                selectedLecture = uiState.selectedLecture,
                fittedTrimParam = uiState.tableTrimParam,
                theme = uiState.tableTheme,
                isDarkMode = isDarkMode(),
                compactMode = uiState.isCompactMode,
                tableLectureCustomOptions = uiState.tableLectureCustomOptions,
                touchEnabled = false,
            )

            AnimatedContent(
                targetState = isSearchMode,
                modifier = Modifier.background(SNUTTColors.Dim2),
                transitionSpec = {
                    if (targetState) {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut() using SizeTransform(clip = false)
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut() using SizeTransform(clip = false)
                    }
                },
                label = "body animation",
            ) { isSearch ->
                if (isSearch) {
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
                } else if (uiState.pageMode == PageMode.Bookmark) {
                    val bookmarksWithState = uiState.bookmarks.map { lecture ->
                        lecture.toDataWithState(
                            LectureState(
                                selected = uiState.selectedLecture?.id == lecture.id,
                                contained = uiState.currentTableLectures.any { it.id == lecture.id },
                                isBookmarked = true,
                                isVacancyRegistered = uiState.vacancyList.any { it.id == lecture.id },
                            ),
                        )
                    }
                    BookmarkList(
                        bookmarks = bookmarksWithState,
                        onToggleLectureSelection = onToggleLectureSelection,
                        onClickLectureDetail = onClickLectureDetail,
                        onClickReview = onClickReview,
                        onClickBookmark = { lecture -> onClickBookmark(lecture, true) },
                        onClickVacancy = { lecture ->
                            val isVacancyRegistered = uiState.vacancyList.any { it.id == lecture.id }
                            onClickVacancy(lecture, isVacancyRegistered)
                        },
                        onClickAddOrRemove = { lecture ->
                            val contained = uiState.currentTableLectures.any { it.id == lecture.id }
                            onToggleLectureContained(lecture, contained)
                        },
                    )
                }
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

@Composable
private fun BookmarkTopBarContent() {
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.bookmark_page_title),
            style = SNUTTTypography.h2,
        )
    }
}
