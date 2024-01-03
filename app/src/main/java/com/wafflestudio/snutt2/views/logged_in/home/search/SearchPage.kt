package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyViewModel
import kotlinx.coroutines.*

//왜안됨
@Composable
fun SearchPage(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureState>>,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val bottomSheet = LocalBottomSheetState.current

    val timetableViewModel: TimetableViewModel = hiltViewModel()
    val tableListViewModel: TableListViewModel = hiltViewModel()
    val lectureDetailViewModel: LectureDetailViewModel = hiltViewModel()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>()
    val vacancyViewModel = hiltViewModel<VacancyViewModel>()
    val selectedLecture by searchViewModel.selectedLecture.collectAsState()
    val pageMode by searchViewModel.pageMode.collectAsState()
    val firstBookmarkAlert by userViewModel.firstBookmarkAlert.collectAsState()

    var searchEditTextFocused by remember { mutableStateOf(false) }
    val isDarkMode = isDarkMode()
    val reviewBottomSheetWebViewContainer = remember {
        WebViewContainer(context, userViewModel.accessToken, isDarkMode).apply {
            this.webView.addJavascriptInterface(
                CloseBridge(onClose = { scope.launch { bottomSheet.hide() } }),
                "Snutt",
            )
        }
    }

    Column {
        TopBar(
            title = {
                AnimatedContent(
                    targetState = pageMode,
                    transitionSpec = {
                        when (targetState) {
                            SearchPageMode.Search -> {
                                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> width } + fadeOut() using SizeTransform(clip = false)
                            }
                            SearchPageMode.Bookmark -> {
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> -width } + fadeOut() using SizeTransform(clip = false)
                            }
                        }
                    },
                    label = "top bar animation",
                ) {
                    when (it) {
                        SearchPageMode.Search -> {
                            Row(
                                modifier = Modifier
                                    .background(SNUTTColors.Gray100, shape = RoundedCornerShape(6.dp))
                                    .weight(1f)
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                SearchIcon(
                                    modifier = Modifier.clicks {
                                        scope.launch {
                                            launchSuspendApi(apiOnProgress, apiOnError) {
                                                searchViewModel.query()
                                            }
                                        }
                                    },
                                )
                                SearchEditText(
                                    searchEditTextFocused = searchEditTextFocused,
                                    onFocus = { isFocused ->
                                        searchEditTextFocused = isFocused
                                    },
                                )
                                if (searchEditTextFocused) {
                                    ExitIcon(
                                        modifier = Modifier.clicks {
                                            scope.launch {
                                                searchViewModel.clearEditText()
                                                searchEditTextFocused = false
                                            }
                                        },
                                    )
                                } else {
                                    FilterIcon(
                                        modifier = Modifier.clicks {
                                            // 강의 검색 필터 sheet 띄우기
                                            bottomSheet.setSheetContent {
                                                SearchOptionSheet(
                                                    applyOption = {
                                                        scope.launch {
                                                            launchSuspendApi(apiOnProgress, apiOnError) {
                                                                searchViewModel.query()
                                                            }
                                                        }
                                                        scope.launch { bottomSheet.hide() }
                                                    },
                                                )
                                            }
                                            scope.launch { bottomSheet.show() }
                                        },
                                    )
                                }
                            }
                        }
                        SearchPageMode.Bookmark -> {
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp),
                                text = stringResource(R.string.bookmark_page_title),
                                style = SNUTTTypography.h2,
                            )
                        }
                    }
                }
            },
            actions = {
                IconWithAlertDot(firstBookmarkAlert) { centerAlignedModifier ->
                    BookmarkIcon(
                        modifier = centerAlignedModifier
                            .size(30.dp)
                            .clicks {
                                searchViewModel.togglePageMode()
                            },
                        marked = pageMode == SearchPageMode.Bookmark,
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
            TimeTable(touchEnabled = false, selectedLecture = selectedLecture)
            AnimatedContent(
                targetState = pageMode,
                modifier = Modifier.background(SNUTTColors.Dim2),
                transitionSpec = {
                    when (targetState) {
                        SearchPageMode.Search -> {
                            slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut() using SizeTransform(clip = false)
                        }
                        SearchPageMode.Bookmark -> {
                            slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut() using SizeTransform(clip = false)
                        }
                    }
                },
                label = "body animation",
            ) { pageMode ->
                when (pageMode) {
                    SearchPageMode.Search -> SearchResultList(
                        scope,
                        searchResultPagingItems,
                        searchViewModel,
                        timetableViewModel,
                        tableListViewModel,
                        lectureDetailViewModel,
                        userViewModel,
                        vacancyViewModel,
                        reviewBottomSheetWebViewContainer,
                    )
                    SearchPageMode.Bookmark -> BookmarkList(
                        searchViewModel,
                        timetableViewModel,
                        tableListViewModel,
                        lectureDetailViewModel,
                        userViewModel,
                        vacancyViewModel,
                        reviewBottomSheetWebViewContainer,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchPagePreview() {
//    SearchPage()
}
