package com.wafflestudio.snutt2.views.logged_in.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.views.logged_in.home.PagerConstants.ReviewPage
import com.wafflestudio.snutt2.views.logged_in.home.PagerConstants.SearchPage
import com.wafflestudio.snutt2.views.logged_in.home.PagerConstants.SettingsPage
import com.wafflestudio.snutt2.views.logged_in.home.PagerConstants.TimeTablePage
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsPage
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetablePage
import io.reactivex.rxjava3.kotlin.Observables
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

enum class HomeItem(@DrawableRes val icon: Int) {
    Timetable(R.drawable.ic_timetable),
    Search(R.drawable.ic_search),
    Review(R.drawable.ic_review),
    Settings(R.drawable.ic_setting)
}

enum class BottomSheetState() {
    SHOW, HIDE
}

object PagerConstants {
    const val TimeTablePage = 0
    const val SearchPage = 1
    const val ReviewPage = 2
    const val SettingsPage = 3
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

// 하위 컴포저블 어디서나 bottomSheet를 올리고 내릴 수 있도록 compositionLocal을 사용.
val ShowBottomSheet = compositionLocalOf<suspend (Dp, @Composable () -> Unit) -> Unit> {
    throw RuntimeException("")
}
val HideBottomSheet = compositionLocalOf<suspend (Boolean) -> Unit> {
    throw RuntimeException("")
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    var uncheckedNotification by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val selectedTimetableViewModel = hiltViewModel<SelectedTimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val searchViewModel = hiltViewModel<SearchViewModel>()

    val pageState = rememberPagerState()

    LaunchedEffect(pageState.currentPage == TimeTablePage) {
        if (pageState.currentPage == TimeTablePage) {
            try {
                homeViewModel.getUncheckedNotificationsExist()
                    .let { uncheckedNotification = it }
            } catch (e: Exception) {
            }
        }
    }

    val (table, theme) =
        Observables.combineLatest(
            selectedTimetableViewModel.lastViewedTable.asObservable().filterEmpty(),
            selectedTimetableViewModel.selectedPreviewTheme.asObservable()
        ).distinctUntilChanged()
            .subscribeAsState(
                initial = Pair(
                    selectedTimetableViewModel.lastViewedTable.get().get()
                        ?: Defaults.defaultTableDto,
                    Optional.empty()
                )
            ).value
    val trimParam = selectedTimetableViewModel.trimParam
        .asObservable().distinctUntilChanged()
        .subscribeAsState(initial = TableTrimParam.Default).value
    val selectedLecture = searchViewModel.selectedLecture.distinctUntilChanged()
        .subscribeAsState(initial = Optional.empty()).value

    // TimeTable을 그리기 위해 필요한 모든 정보
    val tableContext = TableContextBundle(table, trimParam, (theme.get() ?: table.theme))
    // HomePage에서 collect 까지 해 줘야 탭 전환했을 때 검색 현황이 유지됨
    val searchResultPagingItems = searchViewModel.queryResults.collectAsLazyPagingItems()
    // HomePage가 가지고 있어야 탭 전환해도 스크롤 위치가 유지됨
    val listState: LazyListState = rememberLazyListState()


    // BottomSheet 관련
    var bottomSheetState by remember { mutableStateOf(BottomSheetState.HIDE) }
    var bottomSheetHeight by remember { mutableStateOf(200.dp) }
    val bottomSheetHeightPx = with(LocalDensity.current) { bottomSheetHeight.toPx() }
    val bottomSheetOffset = remember { androidx.compose.animation.core.Animatable(0f) }
    val bottomSheetDim = remember { Animatable(Color(0x00000000)) }
    var bottomSheetContent by remember { mutableStateOf<@Composable () -> Unit>({}) }

    val showBottomSheet: suspend (Dp, @Composable () -> Unit) -> Unit =
        { contentHeight, content ->
            coroutineScope {
                bottomSheetHeight = contentHeight
                bottomSheetOffset.snapTo(bottomSheetHeightPx)
                bottomSheetContent = content
                bottomSheetState = BottomSheetState.SHOW
                launch { bottomSheetDim.animateTo(Color(0x99000000)) }  // TODO: Color 정리
                launch { bottomSheetOffset.animateTo(0f) }
            }
        }
    val hideBottomSheet: suspend (Boolean) -> Unit = { fast ->
        coroutineScope {
            launch { bottomSheetDim.animateTo(Color(0x00000000)) }      // TODO: Color 정리
            launch {
                bottomSheetOffset.animateTo(
                    targetValue = bottomSheetHeightPx,
                    animationSpec = spring(stiffness = if (fast) Spring.StiffnessHigh else Spring.StiffnessMedium)
                )
                bottomSheetState = BottomSheetState.HIDE
                bottomSheetContent = {}
            }
        }
    }
    if (bottomSheetState == BottomSheetState.SHOW) {
        BottomSheet(
            content = bottomSheetContent,
            animatedOffset = bottomSheetOffset,
            animateDim = bottomSheetDim,
            onDismiss = { hideBottomSheet(false) },
        )
    }

    CompositionLocalProvider(
        ShowBottomSheet provides showBottomSheet,
        HideBottomSheet provides hideBottomSheet,
    ) {
        ModalDrawer(
            drawerContent = {
                HomeDrawer(
                    onClickTableItem = {
                        scope.launch { drawerState.close() }
                        tableListViewModel.changeSelectedTable(it) // TODO: CoroutineScope에 changetable 묶기
                    },
                    selectedTable = table,
                    closeDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            },
            drawerState = drawerState,
            gesturesEnabled = (pageState.currentPage == TimeTablePage) && (bottomSheetState == BottomSheetState.HIDE)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    HorizontalPager(
                        count = 4,
                        state = pageState,
                        userScrollEnabled = false
                    ) { page ->
                        when (page) {
                            TimeTablePage ->
                                CompositionLocalProvider(
                                    HomeDrawerStateContext provides drawerState,
                                    TableContext provides tableContext
                                ) {
                                    TimetablePage(
                                        uncheckedNotification = uncheckedNotification
                                    )
                                }
                            SearchPage ->
                                CompositionLocalProvider(
                                    TableContext provides tableContext
                                ) {
                                    SearchPage(
                                        searchResultPagingItems,
                                        listState,
                                        selectedLecture
                                    )
                                }
                            ReviewPage -> ReviewPage()
                            SettingsPage -> SettingsPage()
                        }
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
                        onClick = {
                            scope.launch {
                                pageState.animateScrollToPage(TimeTablePage)
                            }
                        },
                    ) {
                        Text(text = "timetable")
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            scope.launch {
                                pageState.animateScrollToPage(SearchPage)
                            }
                        },
                    ) {
                        Text(text = "search")
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            scope.launch {
                                pageState.animateScrollToPage(ReviewPage)
                            }
                        },
                    ) {
                        Text(text = "review")
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            scope.launch {
                                pageState.animateScrollToPage(SettingsPage)
                            }
                        },
                    ) {
                        Text(text = "settings")
                    }
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
