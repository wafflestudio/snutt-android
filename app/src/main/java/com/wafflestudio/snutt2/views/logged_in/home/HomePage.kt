package com.wafflestudio.snutt2.views.logged_in.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.data.lecture_search.SearchViewModelNew
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalDrawerState
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsPage
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetablePage
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import kotlinx.coroutines.Dispatchers
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

data class TableContextBundle(
    val table: TableDto,
    val trimParam: TableTrimParam,
    val previewTheme: TimetableColorTheme?,
)

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

@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    var uncheckedNotification by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var pageState by rememberSaveable { mutableStateOf(HomeItem.Timetable) }

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val selectedTimetableViewModel =
        hiltViewModel<SelectedTimetableViewModel>() // SettingRepository 에서 가져올 trimParam 을 위해 옛 viewModel을 남긴다.
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModelNew>()
    val searchViewModel = hiltViewModel<SearchViewModelNew>()

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            tableListViewModel.fetchTableMap()
        }
    }
    LaunchedEffect(pageState == HomeItem.Timetable) {
        if (pageState == HomeItem.Timetable) {
            try {
                homeViewModel.getUncheckedNotificationsExist().let { uncheckedNotification = it }
            } catch (e: Exception) {
            }
        }
    }
    LaunchedEffect(Unit) {
    }

    val table by timetableViewModel.currentTable.collectAsState(Defaults.defaultTableDto) // FIXME: 초기값 문제
    val previewTheme by timetableViewModel.previewTheme.collectAsState()
    val trimParam = selectedTimetableViewModel.trimParam.asObservable().distinctUntilChanged()
        .subscribeAsState(initial = TableTrimParam.Default).value
    val tableContext = TableContextBundle(table, trimParam, previewTheme)

    // HomePage에서 collect 까지 해 줘야 탭 전환했을 때 검색 현황이 유지됨
    val searchResultPagingItems = searchViewModel.queryResults.collectAsLazyPagingItems()

    // BottomSheet 관련
    var bottomSheetState by remember { mutableStateOf(BottomSheetState.HIDE) }
    var bottomSheetHeight by remember { mutableStateOf(200.dp) }
    val bottomSheetHeightPx = with(LocalDensity.current) { bottomSheetHeight.toPx() }
    val bottomSheetOffset = remember { androidx.compose.animation.core.Animatable(0f) }
    val bottomSheetDim = remember { Animatable(Color(0x00000000)) }
    var bottomSheetContent by remember { mutableStateOf<@Composable () -> Unit>({}) }

    val showBottomSheet: suspend (Dp, @Composable () -> Unit) -> Unit = { contentHeight, content ->
        coroutineScope {
            bottomSheetHeight = contentHeight
            bottomSheetOffset.snapTo(bottomSheetHeightPx)
            bottomSheetContent = content
            bottomSheetState = BottomSheetState.SHOW
            launch { bottomSheetDim.animateTo(Color(0x99000000)) } // TODO: Color 정리
            launch { bottomSheetOffset.animateTo(0f) }
        }
    }
    val hideBottomSheet: suspend (Boolean) -> Unit = { fast ->
        coroutineScope {
            launch { bottomSheetDim.animateTo(Color(0x00000000)) } // TODO: Color 정리
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
        TableContext provides tableContext,
        LocalDrawerState provides drawerState,
    ) {
        ModalDrawer(
            drawerContent = {
                HomeDrawer(
//                    tableListViewModel = tableListViewModel,
//                    timetableViewModel = timetableViewModel,
                )
            },
            drawerState = drawerState,
            gesturesEnabled = (pageState == HomeItem.Timetable) && (bottomSheetState == BottomSheetState.HIDE)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    when (pageState) {
                        HomeItem.Timetable -> TimetablePage(uncheckedNotification = uncheckedNotification)
                        HomeItem.Search -> SearchPage(
                            searchResultPagingItems,
                        )
                        HomeItem.Review -> ReviewPage()
                        HomeItem.Settings -> SettingsPage()
                    }
                }
                Row(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                ) {
                    BorderButton(
                        color = SNUTTColors.White900,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            pageState = HomeItem.Timetable
                        },
                    ) {
                        TimetableIcon(
                            modifier = Modifier.size(30.dp),
                            isSelected = pageState == HomeItem.Timetable
                        )
                    }

                    BorderButton(
                        color = SNUTTColors.White900,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            pageState = HomeItem.Search
                        },
                    ) {
                        SearchIcon(
                            modifier = Modifier.size(30.dp),
                            isSelected = pageState == HomeItem.Search
                        )
                    }

                    BorderButton(
                        color = SNUTTColors.White900,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            pageState = HomeItem.Review
                        },
                    ) {
                        ReviewIcon(
                            modifier = Modifier.size(30.dp),
                            isSelected = pageState == HomeItem.Review
                        )
                    }

                    BorderButton(
                        color = SNUTTColors.White900,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            pageState = HomeItem.Settings
                        },
                    ) {
                        SettingIcon(
                            modifier = Modifier.size(30.dp),
                            isSelected = pageState == HomeItem.Settings
                        )
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
