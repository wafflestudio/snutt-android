package com.wafflestudio.snutt2.views.logged_in.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewPage
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureState
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsPage
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetablePage
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.rxSingle
import kotlin.RuntimeException

enum class HomeItem(@DrawableRes val icon: Int) {
    Timetable(R.drawable.ic_timetable),
    Search(R.drawable.ic_search),
    Review(R.drawable.ic_review),
    Settings(R.drawable.ic_setting)
}

data class TableContextBundle(
    val table: TableDto,
    val trimParam: TableTrimParam,
    val theme: TimetableColorTheme
)

val HomeDrawerStateContext = compositionLocalOf<DrawerState> {
    throw RuntimeException("")
}
val TableContext = compositionLocalOf<TableContextBundle> {
    throw RuntimeException("")
}
val UncheckedNotificationContext = compositionLocalOf<Boolean> {
    throw RuntimeException("")
}
val SelectedLectureContext = compositionLocalOf<Optional<LectureDto>> {
    throw RuntimeException("")
}
val SearchResultContext =
    compositionLocalOf<LazyPagingItems<DataWithState<LectureDto, LectureState>>> {
        throw RuntimeException("")
    }
val SearchLazyListContext = compositionLocalOf<LazyListState> {
    throw RuntimeException("")
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(HomeItem.Timetable) }
    var unCheckedNotification by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val selectedTimetableViewModel = hiltViewModel<SelectedTimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val searchViewModel = hiltViewModel<SearchViewModel>()

    val keyBoardController = LocalSoftwareKeyboardController.current

    rxSingle { homeViewModel.getUncheckedNotificationsExist() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { unCheckedNotification = it },
            onError = { }
        )
    val (table, theme) =
        Observables.combineLatest(
            selectedTimetableViewModel.lastViewedTable.asObservable().filterEmpty(),
            selectedTimetableViewModel.selectedPreviewTheme.asObservable()
        ).distinctUntilChanged()
            .subscribeAsState(initial = Pair(Defaults.defaultTableDto, Optional.empty())) // TODO: 초기값 문제
            .value
    val trimParam = selectedTimetableViewModel.trimParam
        .asObservable().distinctUntilChanged()
        .subscribeAsState(initial = TableTrimParam.Default).value
    val selectedCourseBook = tableListViewModel.selectedCourseBooks.asObservable().filterEmpty()
        .subscribeAsState(initial = CourseBookDto(1L, 2022)).value
    val selectedCourseBookTableList = tableListViewModel.selectedCourseBookTableList
        .subscribeAsState(initial = emptyList()).value
    val selectedLecture = searchViewModel.selectedLecture.distinctUntilChanged()
        .subscribeAsState(initial = Optional.empty()).value

    // TimeTable을 그리기 위해 필요한 모든 정보
    val tableContext = TableContextBundle(table, trimParam, (theme.get() ?: table.theme))
    // HomePage에서 collect 까지 해 줘야 탭 전환했을 때 검색 현황이 유지됨
    val searchResultPagingItems = searchViewModel.queryResults.collectAsLazyPagingItems()
    // HomePage가 가지고 있어야 탭 전환해도 스크롤 위치가 유지됨
    val listState: LazyListState = rememberLazyListState()

    homeViewModel.refreshData()

    ModalDrawer(
        drawerContent = {
            HomeDrawer(
                selectedCourseBook,
                selectedCourseBookTableList,
                onClickItem = {
                    scope.launch { drawerState.close() }
                    tableListViewModel.changeSelectedTable(it)
                }
            )
        },
        drawerState = drawerState,
        gesturesEnabled = currentScreen == HomeItem.Timetable
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (currentScreen) {
                    HomeItem.Timetable ->
                        CompositionLocalProvider(
                            HomeDrawerStateContext provides drawerState,
                            TableContext provides tableContext,
                            UncheckedNotificationContext provides unCheckedNotification,
                            SelectedLectureContext provides Optional.empty(),
                        ) { TimetablePage() }

                    HomeItem.Search ->
                        CompositionLocalProvider(
                            TableContext provides tableContext,
                            SearchResultContext provides searchResultPagingItems,
                            SearchLazyListContext provides listState,
                            SelectedLectureContext provides selectedLecture
                        ) {
                            SearchPage(
                                selectLecture = { lecture ->
                                    searchViewModel.toggleLectureSelection(lecture)
                                    keyBoardController?.hide()
                                },
                                addLecture = { lecture, is_forced ->
                                    selectedTimetableViewModel.addLecture(lecture, is_forced)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeBy(
                                            onError = {},
                                            onComplete = {
                                                searchViewModel.toggleLectureSelection(lecture)
                                                keyBoardController?.hide()
                                            }
                                        )
                                },
                                removeLecture = { lecture ->
                                    selectedTimetableViewModel.removeLecture(lecture)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeBy(
                                            onError = {},
                                            onComplete = {
                                                searchViewModel.toggleLectureSelection(lecture)
                                                keyBoardController?.hide()
                                            }
                                        )
                                },
                                searchLecture = { searchKeyword ->
                                    searchViewModel.setTitle(searchKeyword)
                                    searchViewModel.refreshQuery()
                                    keyBoardController?.hide()
                                }
                            )
                        }

                    HomeItem.Review -> ReviewPage()
                    HomeItem.Settings -> SettingsPage()
                }
            }

            Row(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = { currentScreen = HomeItem.Timetable },
                ) {
                    Text(text = "timetable")
                }

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = { currentScreen = HomeItem.Search },
                ) {
                    Text(text = "search")
                }

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = { currentScreen = HomeItem.Review },
                ) {
                    Text(text = "review")
                }

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = { currentScreen = HomeItem.Settings },
                ) {
                    Text(text = "settings")
                }
            }
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    HomePage()
}
