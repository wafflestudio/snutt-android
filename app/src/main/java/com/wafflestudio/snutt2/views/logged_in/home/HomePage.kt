package com.wafflestudio.snutt2.views.logged_in.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.layouts.ModalDrawerWithBottomSheetLayout
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.toOptional
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
import com.wafflestudio.snutt2.views.logged_in.home.share.ShareTablePage
import com.wafflestudio.snutt2.views.logged_in.home.share.ShareTablePageState
import com.wafflestudio.snutt2.views.logged_in.home.share.ShareTableViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TableState
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetablePage
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pageController = LocalHomePageController.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val popupState = LocalPopupState.current
    val bottomSheet = LocalBottomSheetState.current

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val shareTableViewModel = hiltViewModel<ShareTableViewModel>()

    val uncheckedNotification by homeViewModel.unCheckedNotificationExist.collectAsState()
    val table by timetableViewModel.currentTable.collectAsState()
    val previewTheme by timetableViewModel.previewTheme.collectAsState()
    val trimParam by userViewModel.trimParam.collectAsState()
    val tableState = TableState(table ?: TableDto.Default, trimParam, previewTheme)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var popupReady by remember { mutableStateOf(popupState.popup != null) }
    val isDarkMode = isDarkMode()
    val reviewPageWebViewContainer = remember { WebViewContainer(context, userViewModel.accessToken, isDarkMode) }
    // HomePage에서 collect 까지 해 줘야 탭 전환했을 때 검색 현황이 유지됨
    val searchResultPagingItems = searchViewModel.queryResults.collectAsLazyPagingItems()
    val sharedTableList by shareTableViewModel.sharedTableList.collectAsState()

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            launchSuspendApi(apiOnProgress, apiOnError) {
                tableListViewModel.fetchTableMap()
                tableListViewModel.fetchCourseBooks()
                shareTableViewModel.fetchSharedTableList()
            }
        }
    }

    LaunchedEffect(Unit) {
        combine(timetableViewModel.currentTable, userViewModel.trimParam) { _, _ ->
            TimetableWidgetProvider.refreshWidget(context)
        }.launchIn(this)
    }

    LaunchedEffect(pageController.homePageState.value) {
        if (pageController.homePageState.value == HomeItem.Timetable) {
            launchSuspendApi(apiOnProgress, apiOnError) {
                homeViewModel.checkUncheckedNotificationsExist()
            }
        }
    }

    LaunchedEffect((pageController.homePageState.value as? HomeItem.Review)?.landingPage) {
        reviewPageWebViewContainer.openPage((pageController.homePageState.value as? HomeItem.Review)?.landingPage)
    }

    BackHandler(enabled = bottomSheet.isVisible || drawerState.isOpen || pageController.homePageState.value != HomeItem.Timetable) {
        if (bottomSheet.isVisible) {
            scope.launch { bottomSheet.hide() }
        } else if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            pageController.update(HomeItem.Timetable)
        }
    }

    CompositionLocalProvider(
        LocalTableState provides tableState,
        LocalDrawerState provides drawerState,
    ) {
        var pageState by rememberSaveable(saver = object : Saver<MutableState<ShareTablePageState>, TableDto> {
            override fun restore(value: TableDto): MutableState<ShareTablePageState> {
                return if(value.id.isEmpty()) mutableStateOf(ShareTablePageState.List) else mutableStateOf(ShareTablePageState.Table(value))
            }
            override fun SaverScope.save(value: MutableState<ShareTablePageState>): TableDto {
                return if(value.value is ShareTablePageState.Table) (value.value as ShareTablePageState.Table).table
                else TableDto.Default
            }
        }) {
            val state: MutableState<ShareTablePageState> = mutableStateOf(ShareTablePageState.List)
            return@rememberSaveable object : MutableState<ShareTablePageState> by state {
                override var value: ShareTablePageState
                    get() = state.value
                    set(value) {
                        state.value = value
                    }
            }
        }

        ModalDrawerWithBottomSheetLayout(drawerState = drawerState) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                when (pageController.homePageState.value) {
                    HomeItem.Timetable -> TimetablePage()
                    HomeItem.Search -> SearchPage(searchResultPagingItems)
                    is HomeItem.Review -> {
                        CompositionLocalProvider(LocalReviewWebView provides reviewPageWebViewContainer) {
                            ReviewPage()
                        }
                    }
                    is HomeItem.Share -> ShareTablePage(sharedTableList, pageState, { pageState = it })
                    HomeItem.Settings -> SettingsPage(uncheckedNotification)
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

@Preview
@Composable
fun HomePagePreview() {
    HomePage()
}
