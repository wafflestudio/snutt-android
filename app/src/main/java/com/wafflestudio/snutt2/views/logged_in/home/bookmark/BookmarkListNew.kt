package com.wafflestudio.snutt2.views.logged_in.home.bookmark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.home.search.ExpandableLectureListItemNew
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureState

@Composable
fun BookmarkListNew(
    modifier: Modifier = Modifier,
    bookmarks: List<DataWithState<SearchedLecture, LectureState>>,
    onToggleLectureSelection: (SearchedLecture) -> Unit,
    onClickLectureDetail: (SearchedLecture) -> Unit,
    onClickReview: (SearchedLecture) -> Unit,
    onClickBookmark: (SearchedLecture, Boolean) -> Unit,
    onClickVacancy: (SearchedLecture, Boolean) -> Unit,
    onToggleLectureContained: (SearchedLecture, Boolean) -> Unit,
) {
    Box(
        modifier = modifier.logImpression(AnalyticsScreen.Bookmark),
    ) {
        if (bookmarks.isEmpty()) {
            BookmarkPlaceHolder()
        } else {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                items(
                    items = bookmarks,
                    key = { it.item.id },
                ) { item ->
                    ExpandableLectureListItemNew(
                        lectureDataWithState = item,
                        onToggleLectureSelection = onToggleLectureSelection,
                        onClickLectureDetail = onClickLectureDetail,
                        onClickReview = onClickReview,
                        onClickBookmark = onClickBookmark,
                        onClickVacancy = onClickVacancy,
                        onToggleLectureContained = onToggleLectureContained,
                    )
                }
                item { Divider(color = SNUTTColors.White400) }
            }
        }
    }
}
