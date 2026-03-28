package com.wafflestudio.snutt2.views.logged_in.home

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.logged_in.home.drawer.TimeTableRoute
import com.wafflestudio.snutt2.views.logged_in.home.friend.FriendsRoute
import com.wafflestudio.snutt2.views.logged_in.home.popups.Popup
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsRoute

@Composable
fun HomePageRoute(
    viewModel: HomePageViewModel = hiltViewModel(),
    onNavigateLectureDetailNew: (lectureId: String, tableId: String?, isFromTimetable: Boolean) -> Unit,
    onNavigateThemeDetail: () -> Unit,
    onNavigateLecturesOfTable: () -> Unit,
    onNavigateVacancyNotification: () -> Unit,
    onNavigateBookmark: () -> Unit,
    onNavigateAddLecture: () -> Unit,
    onNavigateOnboardAsOrigin: () -> Unit,
    onNavigateUserConfig: () -> Unit,
    onNavigateNotification: () -> Unit,
    onNavigateThemeModeSelect: () -> Unit,
    onNavigateTimeTableConfig: () -> Unit,
    onNavigateThemeConfig: () -> Unit,
    onNavigateThemeMarket: () -> Unit,
    onNavigatePushPreference: () -> Unit,
    onNavigateLectureReminder: () -> Unit,
    onNavigateDiaryWrite: () -> Unit,
    onNavigateDiaryHistory: () -> Unit,
    onNavigateTeamInfo: () -> Unit,
    onNavigateAppReport: () -> Unit,
    onNavigateOpenLicenses: () -> Unit,
    onNavigateServiceInfo: () -> Unit,
    onNavigatePersonalInformationPolicy: () -> Unit,
    onNavigateNetworkLog: () -> Unit,
    onNavigateTest: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isDarkMode = isDarkMode()

    // FIXME: 강의평 탭의 웹뷰를 미리 로딩하기 위해 상위에서 관리하고 있는데, 더 좋은 방법은 없을까?
    val reviewWebViewContainer =
        remember { ReviewWebViewContainer(context, viewModel.accessToken, isDarkMode) }
    LaunchedEffect((uiState.currentTab as? HomeItem.Review)?.landingPage) {
        reviewWebViewContainer.openPage((uiState.currentTab as? HomeItem.Review)?.landingPage)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomePageUiEvent.NavigateToLectureDetail -> {
                    val isFromTimetable = viewModel.uiState.value.currentTab == HomeItem.Timetable
                    onNavigateLectureDetailNew(event.lectureId, event.tableId, isFromTimetable)
                }

                is HomePageUiEvent.OpenUrl -> {
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_VIEW, event.url.toUri()))
                    }
                }
            }
        }
    }

    BackHandler(enabled = uiState.currentTab != HomeItem.Timetable) {
        viewModel.updateTab(HomeItem.Timetable)
    }

    HomePageNewScreen(
        uiState = uiState,
        reviewWebViewContainer = reviewWebViewContainer,
        onTabSelected = viewModel::updateTab,
        onNavigateLectureDetail = viewModel::onNavigateLectureDetail,
        onNavigateAddLecture = onNavigateAddLecture,
        onNavigateThemeDetail = onNavigateThemeDetail,
        onNavigateLecturesOfTable = onNavigateLecturesOfTable,
        onNavigateVacancyNotification = onNavigateVacancyNotification,
        onNavigateBookmark = onNavigateBookmark,
        onNavigateOnboardAsOrigin = onNavigateOnboardAsOrigin,
        onNavigateUserConfig = onNavigateUserConfig,
        onNavigateNotification = onNavigateNotification,
        onNavigateThemeModeSelect = onNavigateThemeModeSelect,
        onNavigateTimeTableConfig = onNavigateTimeTableConfig,
        onNavigateThemeConfig = onNavigateThemeConfig,
        onNavigateThemeMarket = onNavigateThemeMarket,
        onNavigatePushPreference = onNavigatePushPreference,
        onNavigateLectureReminder = onNavigateLectureReminder,
        onNavigateDiaryWrite = onNavigateDiaryWrite,
        onNavigateDiaryHistory = onNavigateDiaryHistory,
        onNavigateTeamInfo = onNavigateTeamInfo,
        onNavigateAppReport = onNavigateAppReport,
        onNavigateOpenLicenses = onNavigateOpenLicenses,
        onNavigateServiceInfo = onNavigateServiceInfo,
        onNavigatePersonalInformationPolicy = onNavigatePersonalInformationPolicy,
        onNavigateNetworkLog = onNavigateNetworkLog,
        onNavigateTest = onNavigateTest,
        onPopupClickFewDays = viewModel::closePopupWithHiddenDays,
        onPopupClickClose = viewModel::closePopup,
        onPopupClickImage = viewModel::onPopupImageClick,
    )
}

@Composable
private fun HomePageNewScreen(
    uiState: HomePageUiState,
    reviewWebViewContainer: ReviewWebViewContainer,
    onTabSelected: (HomeItem) -> Unit,
    onNavigateLectureDetail: (LocalLecture) -> Unit,
    onNavigateAddLecture: () -> Unit,
    onNavigateThemeDetail: () -> Unit,
    onNavigateLecturesOfTable: () -> Unit,
    onNavigateVacancyNotification: () -> Unit,
    onNavigateBookmark: () -> Unit,
    onNavigateOnboardAsOrigin: () -> Unit,
    onNavigateUserConfig: () -> Unit,
    onNavigateNotification: () -> Unit,
    onNavigateThemeModeSelect: () -> Unit,
    onNavigateTimeTableConfig: () -> Unit,
    onNavigateThemeConfig: () -> Unit,
    onNavigateThemeMarket: () -> Unit,
    onNavigatePushPreference: () -> Unit,
    onNavigateLectureReminder: () -> Unit,
    onNavigateDiaryWrite: () -> Unit,
    onNavigateDiaryHistory: () -> Unit,
    onNavigateTeamInfo: () -> Unit,
    onNavigateAppReport: () -> Unit,
    onNavigateOpenLicenses: () -> Unit,
    onNavigateServiceInfo: () -> Unit,
    onNavigatePersonalInformationPolicy: () -> Unit,
    onNavigateNetworkLog: () -> Unit,
    onNavigateTest: () -> Unit,
    onPopupClickFewDays: () -> Unit,
    onPopupClickClose: () -> Unit,
    onPopupClickImage: () -> Unit,
) {
    val bottomBar: @Composable () -> Unit = {
        Column {
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
            BottomNavigation(
                pageState = uiState.currentTab,
                uncheckedNotificationExist = uiState.uncheckedNotificationCount > 0,
                onUpdatePageState = onTabSelected,
            )
        }
    }

    when (uiState.currentTab) {
        HomeItem.Timetable -> {
            TimeTableRoute(
                bottomBar = bottomBar,
                onNavigateBottomSheetThemeDetail = onNavigateThemeDetail,
                onNavigateLecturesOfTable = onNavigateLecturesOfTable,
                onNavigateVacancyNotification = onNavigateVacancyNotification,
                onNavigateLectureDetail = onNavigateLectureDetail,
                onNavigateBookmark = onNavigateBookmark,
                onNavigateSearch = { onTabSelected(HomeItem.Search) },
                onNavigateAddLecture = onNavigateAddLecture,
            )
        }

        HomeItem.Search -> SearchRoute(
            bottomBar = bottomBar,
            onNavigateVacancy = onNavigateVacancyNotification,
            onNavigateOnboardAsOrigin = onNavigateOnboardAsOrigin,
        )

        is HomeItem.Review -> {
            ReviewPage(
                bottomBar = bottomBar,
                reviewWebViewContainer = reviewWebViewContainer,
                onBack = { onTabSelected(HomeItem.Timetable) },
            )
        }

        HomeItem.Friends -> FriendsRoute(bottomBar = bottomBar)

        HomeItem.Settings -> SettingsRoute(
            bottomBar = bottomBar,
            uncheckedNotifications = uiState.uncheckedNotificationCount,
            onNavigateUserConfig = onNavigateUserConfig,
            onNavigateNotification = onNavigateNotification,
            onNavigateThemeModeSelect = onNavigateThemeModeSelect,
            onNavigateTimeTableConfig = onNavigateTimeTableConfig,
            onNavigateThemeConfig = onNavigateThemeConfig,
            onNavigateVacancyNotification = onNavigateVacancyNotification,
            onNavigateThemeMarket = onNavigateThemeMarket,
            onNavigatePushPreference = onNavigatePushPreference,
            onNavigateLectureReminder = onNavigateLectureReminder,
            onNavigateDiaryWrite = onNavigateDiaryWrite,
            onNavigateDiaryHistory = onNavigateDiaryHistory,
            onNavigateTeamInfo = onNavigateTeamInfo,
            onNavigateAppReport = onNavigateAppReport,
            onNavigateOpenLicenses = onNavigateOpenLicenses,
            onNavigateServiceInfo = onNavigateServiceInfo,
            onNavigatePersonalInformationPolicy = onNavigatePersonalInformationPolicy,
            onNavigateNetworkLog = onNavigateNetworkLog,
            onNavigateTest = onNavigateTest,
            onNavigateOnboardAsOrigin = onNavigateOnboardAsOrigin,
        )
    }

    if (uiState.shouldShowPopup) {
        Popup(
            imageUri = uiState.popupImageUri,
            onClickFewDays = onPopupClickFewDays,
            onClickClose = onPopupClickClose,
            onClickImage = onPopupClickImage,
        )
    }
}
