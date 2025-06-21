package com.wafflestudio.snutt2.views.logged_in.home

import NavigationDestination
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.layouts.ModalDrawerWithBottomSheetLayout
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.LocalDrawerState
import com.wafflestudio.snutt2.views.LocalHomePageController
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.LocalPopupState
import com.wafflestudio.snutt2.views.LocalReviewWebView
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.friend.FriendsPage
import com.wafflestudio.snutt2.views.logged_in.home.popups.Popup
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TableState
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetablePage
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.navigateAsOrigin
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
    val navController = LocalNavController.current

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val searchViewModel = hiltViewModel<SearchViewModel>()

    val uncheckedNotification by homeViewModel.unCheckedNotificationExist.collectAsState()
    val table by timetableViewModel.currentTable.collectAsState()
    val previewTheme by timetableViewModel.previewTheme.collectAsState()
    val trimParam by userViewModel.trimParam.collectAsState()
    val tableLectureCustomOptions by userViewModel.tableLectureCustomOption.collectAsState()
    val tableState = TableState(table ?: TableDto.Default, trimParam, tableLectureCustomOptions, previewTheme)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var shouldShowPopup by remember { mutableStateOf(false) }
    var popupImageUri by remember { mutableStateOf("") }
    val isDarkMode = isDarkMode()
    val reviewPageReviewWebViewContainer = remember { ReviewWebViewContainer(context, userViewModel.accessToken, isDarkMode) }
    // HomePage에서 collect 까지 해 줘야 탭 전환했을 때 검색 현황이 유지됨
    val searchResultPagingItems = searchViewModel.queryResults.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            launchSuspendApi(apiOnProgress, apiOnError) {
                tableListViewModel.fetchTableMap()
                tableListViewModel.fetchCourseBooks()
            }
        }
    }

    LaunchedEffect(Unit) {
        combine(timetableViewModel.currentTable, userViewModel.trimParam) { _, _ ->
            TimetableWidgetProvider.refreshWidget(context)
        }.launchIn(this)
    }

    LaunchedEffect(pageController.homePageState.value) {
        if (pageController.homePageState.value == HomeItem.Timetable || pageController.homePageState.value == HomeItem.Settings) {
            launchSuspendApi(apiOnProgress, apiOnError) {
                homeViewModel.checkUncheckedNotificationsExist()
            }
        }
    }

    LaunchedEffect((pageController.homePageState.value as? HomeItem.Review)?.landingPage) {
        reviewPageReviewWebViewContainer.openPage((pageController.homePageState.value as? HomeItem.Review)?.landingPage)
    }

    LaunchedEffect(Unit) {
        if (popupState.fetched.not()) {
            launchSuspendApi(apiOnProgress, apiOnError) {
                userViewModel.fetchPopup()
                popupState.fetched = true
                shouldShowPopup = (popupState.popup.isNotEmpty())
                popupImageUri = popupState.popup.firstOrNull()?.imageUri ?: ""
            }
        }
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
        ModalDrawerWithBottomSheetLayout(drawerState = drawerState) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomCenter,
            ) {
                when (pageController.homePageState.value) {
                    HomeItem.Timetable -> TimetablePage(uncheckedNotification)
                    HomeItem.Search -> SearchPage(searchResultPagingItems)
                    is HomeItem.Review -> {
                        CompositionLocalProvider(LocalReviewWebView provides reviewPageReviewWebViewContainer) {
                            ReviewPage()
                        }
                    }

                    HomeItem.Friends -> FriendsPage()
                    HomeItem.Settings -> SettingsRoute(
                        onNavigateUserConfig = {
                            navController.navigate(NavigationDestination.UserConfig)
                        },
                        onNavigateThemeModeSelect = {
                            navController.navigate(NavigationDestination.ThemeModeSelect)
                        },
                        onNavigateTimeTableConfig = {
                            navController.navigate(NavigationDestination.TimeTableConfig)
                        },
                        onNavigateThemeConfig = {
                            navController.navigate(NavigationDestination.ThemeConfig)
                        },
                        onNavigateVacancyNotification = {
                            navController.navigate(NavigationDestination.VacancyNotification)
                        },
                        onNavigateThemeMarket = {
                            navController.navigate(NavigationDestination.ThemeMarket)
                        },
                        onNavigatePushPreference = {
                            navController.navigate(NavigationDestination.PushPreferences)
                        },
                        onNavigateLectureDiary = {
                            navController.navigate(NavigationDestination.LectureDiary)
                        },
                        onNavigateTeamInfo = {
                            navController.navigate(NavigationDestination.TeamInfo)
                        },
                        onNavigateAppReport = {
                            navController.navigate(NavigationDestination.AppReport)
                        },
                        onNavigateOpenLicenses = {
                            navController.navigate(NavigationDestination.OpenLicenses)
                        },
                        onNavigateServiceInfo = {
                            navController.navigate(NavigationDestination.ServiceInfo)
                        },
                        onNavigatePersonalInformationPolicy = {
                            navController.navigate(NavigationDestination.PersonalInformationPolicy)
                        },
                        onNavigateNetworkLog = {
                            navController.navigate(NavigationDestination.NetworkLog)
                        },
                        onNavigateOnboardAsOrigin = {
                            navController.navigateAsOrigin(NavigationDestination.Onboard)
                        },
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    SNUTTColors.Gray100,
                                ),
                            ),
                        ),
                )
            }
            BottomNavigation(
                pageState = pageController.homePageState.value,
                onUpdatePageState = { pageController.update(it) },
            )
        }
    }

    if (shouldShowPopup) {
        Popup(
            imageUri = popupImageUri,
            onClickFewDays = {
                scope.launch {
                    userViewModel.closePopupWithHiddenDays()
                    shouldShowPopup = popupState.popup.isNotEmpty()
                    popupImageUri = popupState.popup.firstOrNull()?.imageUri ?: ""
                }
            },
            onClickClose = {
                scope.launch {
                    userViewModel.closePopup()
                    shouldShowPopup = popupState.popup.isNotEmpty()
                    popupImageUri = popupState.popup.firstOrNull()?.imageUri ?: ""
                }
            },
            onClickImage = {
                popupState.popup.firstOrNull()?.linkUrl?.let { linkUrl ->
                    runCatching {
                        val uri = Uri.parse(linkUrl)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    }
                }
            },
        )
    }
}
