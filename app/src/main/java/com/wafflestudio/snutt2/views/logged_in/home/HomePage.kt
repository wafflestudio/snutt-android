package com.wafflestudio.snutt2.views.logged_in.home

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetablePage
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val pageController = LocalHomePageController.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModelNew>()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val reviewWebViewContainer = remember { WebViewContainer(context, userViewModel.accessToken) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            launchSuspendApi(apiOnProgress, apiOnError) {
                tableListViewModel.fetchTableMap()
                tableListViewModel.fetchCourseBooks()
            }
        }
    }

    LaunchedEffect(pageController.homePageState.value) {
        if (pageController.homePageState.value == HomeItem.Timetable) {
            launchSuspendApi(apiOnProgress, apiOnError) {
                homeViewModel.getUncheckedNotificationsExist().let { uncheckedNotification = it }
            }
        }
    }

    val table by timetableViewModel.currentTable.collectAsState(Defaults.defaultTableDto) // FIXME: 초기값 문제
    val previewTheme by timetableViewModel.previewTheme.collectAsState()
    val trimParam = TableTrimParam.Default // by userViewModel.trimParam.collectAsState()
    val tableContext = TableContextBundle(table, trimParam, previewTheme)

    // HomePage에서 collect 까지 해 줘야 탭 전환했을 때 검색 현황이 유지됨
    val searchResultPagingItems = searchViewModel.queryResults.collectAsLazyPagingItems()

    // BottomSheet 관련
    var bottomSheetState by remember { mutableStateOf(BottomSheetState.HIDE) }
    var bottomSheetHeight by remember { mutableStateOf(200.dp) }
    val bottomSheetHeightPx = with(LocalDensity.current) { bottomSheetHeight.toPx() }
    val bottomSheetOffset = remember { androidx.compose.animation.core.Animatable(0f) }
    val bottomSheetDim = remember { Animatable(SNUTTColors.Transparent) }
    var bottomSheetContent by remember { mutableStateOf<@Composable () -> Unit>({}) }

    val showBottomSheet: suspend (Dp, @Composable () -> Unit) -> Unit = { contentHeight, content ->
        coroutineScope {
            bottomSheetHeight = contentHeight
            bottomSheetOffset.snapTo(bottomSheetHeightPx)
            bottomSheetContent = content
            bottomSheetState = BottomSheetState.SHOW
            launch { bottomSheetDim.animateTo(SNUTTColors.Black600) }
            launch { bottomSheetOffset.animateTo(0f) }
        }
    }
    val hideBottomSheet: suspend (Boolean) -> Unit = { fast ->
        coroutineScope {
            launch { bottomSheetDim.animateTo(SNUTTColors.Transparent) }
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

    LaunchedEffect((pageController.homePageState.value as? HomeItem.Review)?.landingPage) {
        reviewWebViewContainer.openPage((pageController.homePageState.value as? HomeItem.Review)?.landingPage)
    }

    CompositionLocalProvider(
        ShowBottomSheet provides showBottomSheet,
        HideBottomSheet provides hideBottomSheet,
        TableContext provides tableContext,
        LocalDrawerState provides drawerState,
        LocalReviewWebView provides reviewWebViewContainer,
    ) {
        ModalDrawer(
            drawerContent = {
                HomeDrawer()
            },
            drawerState = drawerState,
            gesturesEnabled = (pageController.homePageState.value == HomeItem.Timetable) && (bottomSheetState == BottomSheetState.HIDE)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    when (pageController.homePageState.value) {
                        HomeItem.Timetable -> TimetablePage(uncheckedNotification = uncheckedNotification)
                        HomeItem.Search -> SearchPage(
                            searchResultPagingItems,
                        )
                        is HomeItem.Review -> {
                            ReviewPage()
                        }
                        HomeItem.Settings -> SettingsPage()
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        SNUTTColors.Gray100
                                    )
                                ),
                            )
                    )
                }
                BottomNavigation(
                    pageState = pageController.homePageState.value,
                    onUpdatePageState = { pageController.update(it) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavigation(pageState: HomeItem, onUpdatePageState: (HomeItem) -> Unit) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(SNUTTColors.White900)
    ) {
        BorderButton(
            color = SNUTTColors.White900,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = {
                onUpdatePageState(HomeItem.Timetable)
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
                onUpdatePageState(HomeItem.Search)
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
                onUpdatePageState(HomeItem.Review())
            },
        ) {
            ReviewIcon(
                modifier = Modifier.size(30.dp),
                isSelected = pageState is HomeItem.Review
            )
        }

        BorderButton(
            color = SNUTTColors.White900,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = {
                onUpdatePageState(HomeItem.Settings)
            },
        ) {
            SettingIcon(
                modifier = Modifier.size(30.dp),
                isSelected = pageState == HomeItem.Settings
            )
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    HomePage()
}
