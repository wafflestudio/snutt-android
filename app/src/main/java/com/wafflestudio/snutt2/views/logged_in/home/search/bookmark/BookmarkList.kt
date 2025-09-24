package com.wafflestudio.snutt2.views.logged_in.home.search.bookmark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureListItem
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureState

@Composable
fun BookmarkList(
    bookmarks: List<DataWithState<LectureDto, LectureState>>,
//    searchViewModel: SearchViewModel,
//    timetableViewModel: TimetableViewModel,
//    tableListViewModel: TableListViewModel,
//    lectureDetailViewModel: LectureDetailViewModel,
//    userViewModel: UserViewModel,
//    vacancyViewModel: VacancyViewModel,
    reviewWebViewContainer: ReviewWebViewContainer,
) {
    Box(
        modifier = Modifier.logImpression(AnalyticsScreen.Bookmark),
    ) {
//        val bookmarks by searchViewModel.bookmarkList.collectAsState()
        if (bookmarks.isEmpty()) {
            BookmarkPlaceHolder()
        } else {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                items(bookmarks) {
                    LectureListItem(
                        lectureDataWithState = it,
//                        searchViewModel = searchViewModel,
                        reviewWebViewContainer = reviewWebViewContainer,
                        isBookmarkPage = true,
//                        timetableViewModel = timetableViewModel,
//                        tableListViewModel = tableListViewModel,
//                        lectureDetailViewModel = lectureDetailViewModel,
//                        userViewModel = userViewModel,
//                        vacancyViewModel = vacancyViewModel,
                    )
                }
                item { Divider(color = SNUTTColors.White400) }
            }
        }
    }
}
