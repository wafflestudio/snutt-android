package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyViewModel

@Composable
fun BookmarkList(
    searchViewModel: SearchViewModel,
    timetableViewModel: TimetableViewModel,
    tableListViewModel: TableListViewModel,
    lectureDetailViewModel: LectureDetailViewModel,
    userViewModel: UserViewModel,
    vacancyViewModel: VacancyViewModel,
    reviewWebViewContainer: WebViewContainer,
) {
    val bookmarks by searchViewModel.bookmarkList.collectAsState()
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
                    searchViewModel = searchViewModel,
                    reviewWebViewContainer = reviewWebViewContainer,
                    isBookmarkPage = true,
                    timetableViewModel = timetableViewModel,
                    tableListViewModel = tableListViewModel,
                    lectureDetailViewModel = lectureDetailViewModel,
                    userViewModel = userViewModel,
                    vacancyViewModel = vacancyViewModel,
                )
            }
            item { Divider(color = SNUTTColors.White400) }
        }
    }
}
