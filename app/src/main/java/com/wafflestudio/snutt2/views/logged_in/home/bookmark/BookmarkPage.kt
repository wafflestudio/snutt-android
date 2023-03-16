package com.wafflestudio.snutt2.views.logged_in.home.bookmark

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.TableContext
import com.wafflestudio.snutt2.views.logged_in.home.TableContextBundle
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModelNew
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureState
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchListItem
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModelNew
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookmarkPage(searchViewModel: SearchViewModel) {
    val navController = LocalNavController.current
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val context = LocalContext.current
    val bottomSheet = LocalBottomSheetState.current
    val scope = rememberCoroutineScope()
    val userViewModel = hiltViewModel<UserViewModel>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModelNew>()
    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModelNew>()

    val isDarkMode = isDarkMode()
    val reviewWebViewContainer =
        remember {
            WebViewContainer(context, userViewModel.accessToken, isDarkMode)
        }
    reviewWebViewContainer.apply {
        this.webView.addJavascriptInterface(
            CloseBridge(
                onClose = { scope.launch { bottomSheet.hide() } }
            ),
            "Snutt"
        )
    }

    BackHandler(bottomSheet.isVisible) {
        scope.launch { bottomSheet.hide() }
    }

    val selectedLecture by searchViewModel.selectedLecture.collectAsState()
    val bookmarks by searchViewModel.bookmarkList.collectAsState()
    val trimParam by userViewModel.trimParam.collectAsState()
    val table by timetableViewModel.currentTable.collectAsState()
    val previewTheme by timetableViewModel.previewTheme.collectAsState()
    val tableContext =
        TableContextBundle(table ?: TableDto.Default, trimParam, previewTheme)

    LaunchedEffect(Unit) {
        launchSuspendApi(apiOnProgress, apiOnError) {
            searchViewModel.getBookmarkList()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = bottomSheet.content,
        sheetState = bottomSheet.state,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5)
    ) {
        Column(
            modifier = Modifier
                .background(SNUTTColors.White900)
                .fillMaxWidth()
        ) {
            SimpleTopBar(title = stringResource(R.string.bookmark_page_title), onClickNavigateBack = { navController.popBackStack() })
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                CompositionLocalProvider(TableContext provides tableContext) {
                    TimeTable(selectedLecture = selectedLecture, touchEnabled = false)
                }
                if (bookmarks.isEmpty()) {
                    BookmarkPlaceHolder()
                } else {
                    BookmarkList(
                        bookmarks,
                        searchViewModel,
                        timetableViewModel,
                        tableListViewModel,
                        lectureDetailViewModel,
                        reviewWebViewContainer
                    )
                }
            }
        }
    }
}

@Composable
fun BookmarkList(
    bookmarks: List<DataWithState<LectureDto, LectureState>>,
    searchViewModel: SearchViewModel,
    timetableViewModel: TimetableViewModel,
    tableListViewModel: TableListViewModelNew,
    lectureDetailViewModel: LectureDetailViewModelNew,
    reviewWebViewContainer: WebViewContainer,
) {
    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier
            .background(SNUTTColors.Dim2)
            .fillMaxSize()
    ) {
        items(bookmarks) {
            SearchListItem(
                lectureDataWithState = it,
                searchViewModel,
                timetableViewModel,
                tableListViewModel,
                lectureDetailViewModel,
                reviewWebViewContainer,
                isBookmarkPage = true,
            )
        }
        item { Divider(color = SNUTTColors.White400) }
    }
}

@Composable
fun BookmarkPlaceHolder() {
    Column(
        modifier = Modifier
            .background(SNUTTColors.Dim2)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.bookmark_page_placeholder_1),
            style = SNUTTTypography.subtitle1.copy(
                fontSize = 18.sp,
                color = SNUTTColors.White700,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = stringResource(R.string.bookmark_page_placeholder_2),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700)
        )
        Text(
            text = stringResource(R.string.bookmark_page_placeholder_3),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}
