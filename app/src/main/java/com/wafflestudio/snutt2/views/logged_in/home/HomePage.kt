package com.wafflestudio.snutt2.views.logged_in.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.popups.Popup
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetablePage
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@Stable
data class TableContextBundle(
    val table: TableDto,
    val trimParam: TableTrimParam,
    val previewTheme: TimetableColorTheme?,
)

val TableContext = compositionLocalOf<TableContextBundle> {
    throw RuntimeException("")
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    var uncheckedNotification by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current
    val pageController = LocalHomePageController.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val popupState = LocalPopupState.current
    var popupReady by remember { mutableStateOf(popupState.popup != null) }

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModelNew>()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val isDarkMode = isDarkMode()
    val reviewWebViewContainer =
        remember { WebViewContainer(context, userViewModel.accessToken, isDarkMode) }

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

    LaunchedEffect(Unit) {
        combine(timetableViewModel.currentTable, userViewModel.trimParam) { _, _ ->
            TimetableWidgetProvider.refreshWidget(context)
        }.launchIn(this)
    }

    val table by timetableViewModel.currentTable.collectAsState()
    val previewTheme by timetableViewModel.previewTheme.collectAsState()
    val trimParam by userViewModel.trimParam.collectAsState()
    val tableContext =
        TableContextBundle(table ?: TableDto.Default, trimParam, previewTheme)

    // HomePage에서 collect 까지 해 줘야 탭 전환했을 때 검색 현황이 유지됨
    val searchResultPagingItems = searchViewModel.queryResults.collectAsLazyPagingItems()

    LaunchedEffect((pageController.homePageState.value as? HomeItem.Review)?.landingPage) {
        reviewWebViewContainer.openPage((pageController.homePageState.value as? HomeItem.Review)?.landingPage)
    }

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    var bottomSheetContent by remember {
        mutableStateOf<@Composable ColumnScope.() -> Unit>({
            Box(modifier = Modifier.size(1.dp))
        })
    }
    val bottomSheetContentSetter: (@Composable ColumnScope.() -> Unit) -> Unit = {
        bottomSheetContent = it
    }
    LaunchedEffect(sheetState.isVisible) {
        // 숨겨질 때, 내부 content를 초기화 해 주지 않으면 다른 sheet를 띄울 때 어색한 모습이 된다. (높이 널뛰기)
        if (!sheetState.isVisible) {
            bottomSheetContent = { Box(modifier = Modifier.size(1.dp)) }
        }
    }
    BackHandler(enabled = sheetState.isVisible || drawerState.isOpen || pageController.homePageState.value != HomeItem.Timetable) {
        if (sheetState.isVisible) {
            scope.launch { sheetState.hide() }
        } else if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            pageController.update(HomeItem.Timetable)
        }
    }

    CompositionLocalProvider(
        TableContext provides tableContext,
        LocalDrawerState provides drawerState,
        LocalBottomSheetState provides sheetState,
        LocalReviewWebView provides reviewWebViewContainer,
        LocalBottomSheetContentSetter provides bottomSheetContentSetter,
    ) {
        ModalBottomSheetLayout(
            sheetContent = bottomSheetContent,
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5)
            // gesturesEnabled 가 없다! 그래서 드래그해서도 닫아진다..
        ) {
            ModalDrawer(
                drawerContent = {
                    HomeDrawer()
                },
                drawerState = drawerState,
                gesturesEnabled = (pageController.homePageState.value == HomeItem.Timetable) && !sheetState.isVisible,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        when (pageController.homePageState.value) {
                            HomeItem.Timetable -> TimetablePage()
                            HomeItem.Search -> SearchPage(
                                searchResultPagingItems,
                            )
                            is HomeItem.Review -> {
                                ReviewPage()
                            }
                            HomeItem.Settings -> SettingsPage(uncheckedNotification = uncheckedNotification)
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
                        onUpdatePageState = { pageController.update(it) },
                        uncheckedNotification = uncheckedNotification,
                    )
                }
            }
        }
    }

    if (popupReady) {
        Popup(
            url = popupState.popup?.url ?: "",
            onClickFewDays = {
                scope.launch {
                    userViewModel.closePopupWithHiddenDays()
                    popupReady = false
                }
            },
            onClickClose = {
                scope.launch {
                    userViewModel.closePopup()
                    popupReady = false
                }
            }
        )
    }
}

@Composable
private fun BottomNavigation(pageState: HomeItem, onUpdatePageState: (HomeItem) -> Unit, uncheckedNotification: Boolean,) {
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
                isSelected = pageState == HomeItem.Timetable,
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
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
                isSelected = pageState == HomeItem.Search,
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
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
                isSelected = pageState is HomeItem.Review,
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
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
            IconWithAlertDot(uncheckedNotification && pageState != HomeItem.Settings) { centerAlignedModifier ->
                SettingIcon(
                    modifier = centerAlignedModifier.size(30.dp),
                    isSelected = pageState == HomeItem.Settings,
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            }
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    HomePage()
}
