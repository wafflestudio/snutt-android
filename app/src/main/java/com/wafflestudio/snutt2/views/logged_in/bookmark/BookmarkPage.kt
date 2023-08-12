package com.wafflestudio.snutt2.views.logged_in.bookmark

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Text
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
import com.wafflestudio.snutt2.components.compose.bottomSheet
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureListItem
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TableState
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookmarkPage(
    searchViewModel: SearchViewModel,
    vacancyViewModel: VacancyViewModel,
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val bottomSheet = bottomSheet()
    val scope = rememberCoroutineScope()
    val userViewModel = hiltViewModel<UserViewModel>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val tableListViewModel: TableListViewModel = hiltViewModel()
    val lectureDetailViewModel: LectureDetailViewModel = hiltViewModel()

    val isDarkMode = isDarkMode()
    val reviewWebViewContainer =
        remember {
            WebViewContainer(context, userViewModel.accessToken, isDarkMode)
        }
    reviewWebViewContainer.apply {
        this.webView.addJavascriptInterface(
            CloseBridge(
                onClose = { scope.launch { bottomSheet.hide() } },
            ),
            "Snutt",
        )
    }

    /* 뒤로가기 핸들링 */
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomSheet.isVisible) {
                    scope.launch { bottomSheet.hide() }
                } else if (navController.currentDestination?.route == NavigationDestination.Bookmark) {
                    navController.popBackStack()
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onBackPressedDispatcherOwner?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
        onDispose { onBackPressedCallback.remove() }
    }

    val selectedLecture by searchViewModel.selectedLecture.collectAsState()
    val bookmarks by searchViewModel.bookmarkList.collectAsState()
    val trimParam by userViewModel.trimParam.collectAsState()
    val table by timetableViewModel.currentTable.collectAsState()
    val previewTheme by timetableViewModel.previewTheme.collectAsState()
    val tableState =
        TableState(table ?: TableDto.Default, trimParam, previewTheme)

    CompositionLocalProvider(LocalBottomSheetState provides bottomSheet) {
        ModalBottomSheetLayout(
            sheetContent = bottomSheet.content,
            sheetState = bottomSheet.state,
            sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
//            onDismissScrim = {
//                scope.launch { bottomSheet.hide() }
//            }
        ) {
            Column(
                modifier = Modifier
                    .background(SNUTTColors.White900)
                    .fillMaxWidth(),
            ) {
                SimpleTopBar(title = stringResource(R.string.bookmark_page_title), onClickNavigateBack = { onBackPressedCallback.handleOnBackPressed() })
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    CompositionLocalProvider(LocalTableState provides tableState) {
                        TimeTable(selectedLecture = selectedLecture, touchEnabled = false)
                    }
                    if (bookmarks.isEmpty()) {
                        BookmarkPlaceHolder()
                    } else {
                        LazyColumn(
                            state = rememberLazyListState(),
                            modifier = Modifier
                                .background(SNUTTColors.Dim2)
                                .fillMaxSize(),
                        ) {
                            items(bookmarks) {
                                LectureListItem(
                                    lectureDataWithState = it,
                                    searchViewModel = searchViewModel, // 다른 viewModel은 데이터를 갖지 않고 api만 사용하므로 route가 Bookmark인 hiltViewModel 그냥 사용
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
            }
        }
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
                fontWeight = FontWeight.Bold,
            ),
        )
        Text(
            text = stringResource(R.string.bookmark_page_placeholder_2),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700),
        )
        Text(
            text = stringResource(R.string.bookmark_page_placeholder_3),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700),
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}
