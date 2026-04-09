package com.wafflestudio.snutt2.feature.search

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.feature.bookmark.BookmarkPlaceHolder
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
fun BookmarkList(
    bookmarks: List<DataWithState<SearchedLecture, LectureState>>,
    onToggleLectureSelection: (SearchedLecture) -> Unit,
    onClickLectureDetail: (SearchedLecture) -> Unit,
    onClickReview: (SearchedLecture) -> Unit,
    onClickBookmark: (SearchedLecture) -> Unit,
    onClickVacancy: (SearchedLecture) -> Unit,
    onClickAddOrRemove: (SearchedLecture) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.logImpression(AnalyticsScreen.Bookmark),
    ) {
        if (bookmarks.isEmpty()) {
            BookmarkPlaceHolder()
        } else {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(bookmarks) { (lecture, lectureState) ->
                    SearchLectureListItem(
                        modifier = Modifier.animateItem(
                            placementSpec = spring(
                                stiffness = Spring.StiffnessHigh,
                                visibilityThreshold = IntOffset.VisibilityThreshold,
                            ),
                        ),
                        lecture = lecture,
                        lectureState = lectureState,
                        onClick = { onToggleLectureSelection(lecture) },
                        onClickDetail = { onClickLectureDetail(lecture) },
                        onClickReview = { onClickReview(lecture) },
                        onClickBookmark = { onClickBookmark(lecture) },
                        onClickVacancy = { onClickVacancy(lecture) },
                        onClickAddOrRemove = { onClickAddOrRemove(lecture) },
                    )
                }
                item { Divider(color = SNUTTColors.White400) }
            }
        }
    }
}
