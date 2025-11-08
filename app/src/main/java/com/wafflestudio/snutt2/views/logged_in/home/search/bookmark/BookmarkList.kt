package com.wafflestudio.snutt2.views.logged_in.home.search.bookmark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.home.search.ExpandableLectureListItem
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureState

@Composable
fun BookmarkList(
    bookmarks: List<DataWithState<LectureDto, LectureState>>,
    onToggleLectureSelection: (LectureDto) -> Unit,
    onClickLectureDetail: (LectureDto) -> Unit,
    onClickReview: (LectureDto) -> Unit,
    onClickBookmark: (LectureDto, Boolean) -> Unit,
    onClickVacancy: (LectureDto, Boolean) -> Unit,
    onToggleLectureContained: (LectureDto, Boolean) -> Unit,
) {
    Box(
        modifier = Modifier.logImpression(AnalyticsScreen.Bookmark),
    ) {
        if (bookmarks.isEmpty()) {
            BookmarkPlaceHolder()
        } else {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                items(bookmarks) {
                    ExpandableLectureListItem(
                        lectureDataWithState = it,
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
